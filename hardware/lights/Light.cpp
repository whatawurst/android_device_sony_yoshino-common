/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright (C) 2018 Shane Francis
 * Copyright (C) 2022 Alexander Grund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "Light.h"
#include <android-base/logging.h>
#include <cutils/properties.h>
#include <fstream>
#include <stdexcept>
#include <thread>

#define LEDS_CLASS_BASE "/sys/class/leds/"
#define LED_FILE(led, file) LEDS_CLASS_BASE #led "/" #file

constexpr const char* BUTTON_FILE = LED_FILE(button-backlight, brightness);

constexpr const char* LCD_CLASS_BASE = "/sys/class/leds/lcd-backlight";
constexpr const char* LCD_CLASS_BASE2 = "/sys/class/backlight/panel0-backlight";
static const char* PERSISTENCE_FILE = "/sys/class/graphics/fb0/msm_fb_persist_mode";
constexpr int32_t DEFAULT_LOW_PERSISTENCE_MODE_BRIGHTNESS = 128;
constexpr const char* LP_MODE_BRIGHTNESS_PROPERTY = "sys.display.low_persistence_mode_brightness";

using ::android::hardware::light::V2_0::LightState;

static bool exists(const char* path) {
    return access(path, F_OK) == 0;
}

static bool exists(const std::string& path) {
    return exists(path.c_str());
}

template<typename T>
static bool write(const char* path, const T& value) {
    std::ofstream stream(path);

    if (!stream) {
        PLOG(ERROR) << "Failed to open " << path;
        return false;
    }

    if (stream << value << std::endl)
        return true;
    PLOG(ERROR) << "Failed to write to " << path;
    return false;
}

template<typename T>
static bool read(const char* path, T& value) {
    std::ifstream stream(path);

    if (!stream) {
        PLOG(ERROR) << "Failed to open " << path;
        return false;
    }

    if (stream >> value)
        return true;
    PLOG(ERROR) << "Failed to read from " << path;
    return false;
}

template<typename T>
static T read(const char* path) {
    T result;
    return read(path, result) ? T{} : result;
}

static bool isLit(const LightState &state) {
    return state.color & 0x00ffffff;
}

static int rgbToBrightness(const LightState &state) {
    const int color = state.color;
    return ((77 * ((color >> 16) & 0x00ff))
               + (150 * ((color >> 8) & 0x00ff)) + (29 * (color & 0x00ff)))
        >> 8;
}

/// Scale a color value (0-255) to the range 0-maxBrightness
static int scaleBrightness(const int brightness, const int maxBrightness) {
    // Adding half of the max (255/2=127) provides proper rounding while staying in integer mode
    return (brightness * maxBrightness + 127) / 255;
}

static void readMaxBrightness(const char* file, int& maxBrightness, const char* name, int defaultValue = -1) {
    if(read(file, maxBrightness) < 0) {
        LOG(WARNING) << "Can't read max brightness for " << name;
        maxBrightness = defaultValue;
    } else if (maxBrightness < 0) {
        LOG(WARNING) << "Max brightness value " << maxBrightness << " for " << name << " is invalid";
        maxBrightness = defaultValue;
    }
}

namespace android {
namespace hardware {
namespace light {
namespace V2_0 {
namespace implementation {

Light::Color::Color(const unsigned colorRGB):
    red((colorRGB >> 16) & 0xFF),
    green((colorRGB >> 8) & 0xFF),
    blue(colorRGB & 0xFF)
{}

Light::Light() {
    android::base::SetMinimumLogSeverity(android::base::LogSeverity::VERBOSE);

    LOG(INFO) << __func__ << ": Setup HAL";

    // Assume those always exist
    std::vector<Type> supportedTypes{
        Type::BACKLIGHT,
        Type::BATTERY,
        Type::NOTIFICATIONS,
        Type::ATTENTION,
    };

    const std::string lcd_class_base = exists(LCD_CLASS_BASE) ? LCD_CLASS_BASE : LCD_CLASS_BASE2;
    mLcdFile = lcd_class_base + "/brightness";
    if(!exists(mLcdFile)) {
        LOG(FATAL) << "Unknown LCD";
    }
    
    readMaxBrightness((lcd_class_base + "/max_brightness").c_str(), mBacklightMax, "LCD");
    readMaxBrightness(LED_FILE(red, max_single_brightness), mMaxSingle.red, "red LED", 0xFF);
    readMaxBrightness(LED_FILE(green, max_single_brightness), mMaxSingle.green, "green LED", 0xFF);
    readMaxBrightness(LED_FILE(blue, max_single_brightness), mMaxSingle.blue, "blue LED", 0xFF);
    readMaxBrightness(LED_FILE(red, max_mix_brightness), mMaxMix.red, "red LED(mix)", 0xFF);
    readMaxBrightness(LED_FILE(green, max_mix_brightness), mMaxMix.green, "green LED(mix)", 0xFF);
    readMaxBrightness(LED_FILE(blue, max_mix_brightness), mMaxMix.blue, "blue LED(mix)", 0xFF);

    mHasPersistenceFile = exists(PERSISTENCE_FILE);
    mHasButtonFile = exists(BUTTON_FILE);
    if(mHasButtonFile)
        supportedTypes.push_back(Type::BUTTONS);
    mSupportedTypes = supportedTypes;
}

Return<Status> Light::setLight(Type type, const LightState &state) {
    bool status;
    switch (type) {
    case Type::BACKLIGHT:
        LOG(DEBUG) << __func__ << " : Type::BACKLIGHT";
        status = setLightBacklight(state);
        break;
    case Type::BUTTONS:
        LOG(DEBUG) << __func__ << " : Type::BUTTONS";
        return setLightButtons(state);
    case Type::BATTERY:
        LOG(DEBUG) << __func__ << " : Type::BATTERY";
        status = setLightBattery(state);
        break;
    case Type::NOTIFICATIONS:
        LOG(DEBUG) << __func__ << " : Type::NOTIFICATIONS";
        status = setLightNotifications(state);
        break;
    case Type::ATTENTION:
        LOG(DEBUG) << __func__ << " : Type::ATTENTION";
        status = setLightNotifications(state);
        break;
    case Type::KEYBOARD:
    case Type::BLUETOOTH:
    case Type::WIFI:
        return Status::LIGHT_NOT_SUPPORTED;
    default:
        LOG(DEBUG) << __func__ << " : Unknown light type " << static_cast<int32_t>(type);
        return Status::LIGHT_NOT_SUPPORTED;
    }
    return status ? Status::SUCCESS : Status::UNKNOWN;
}

bool Light::setLightBacklight(const LightState &state) {
    std::lock_guard<std::mutex> lock(mLcdLock);

    bool status = true;
    int brightness = rgbToBrightness(state);
    const bool lpEnabled = state.brightnessMode == Brightness::LOW_PERSISTENCE;

    const bool cannotHandlePersistence = !mHasPersistenceFile && lpEnabled;
    // If persistence mode has been changed
    if (mHasPersistenceFile && mLowPersistenceEnabled != lpEnabled) {
        if ((status = write(PERSISTENCE_FILE, lpEnabled ? 1 : 0)))
            LOG(ERROR) << __func__ << " : Failed to write to " << PERSISTENCE_FILE << ": " << strerror(errno);
        if (lpEnabled)
            brightness = property_get_int32(LP_MODE_BRIGHTNESS_PROPERTY, DEFAULT_LOW_PERSISTENCE_MODE_BRIGHTNESS);
        mLowPersistenceEnabled = lpEnabled;
    }

    if (status) {
        if (brightness && mBacklightMax > 0) {
            // Scale by max brightness but make sure the min and max values are 1 and max_lcd_brightness
            brightness = (brightness == 255) ? mBacklightMax : scaleBrightness(brightness - 1, mBacklightMax) + 1;
        }
        status = write(mLcdFile.c_str(), brightness);
    }

    return !cannotHandlePersistence && status;
}

bool write_lut(const char* file, int brightness) {
    char buffer[22];
    int n = snprintf(buffer, sizeof(buffer), "%d,0\n", brightness);
    if (n < 0 || n >= sizeof(buffer))
        return false;
    return write(file, buffer);
}

#define HANDLE_LED_BLINK_VALUES(led)                            \
    if (color.led) {                                            \
        status &= write_lut(LED_FILE(led, lut_pwm), color.led); \
        status &= write(LED_FILE(led, pause_lo_multi), onMS);   \
        status &= write(LED_FILE(led, pause_hi_multi), offMS);  \
        status &= write(LED_FILE(led, step_duration), 0);       \
    }

bool Light::setSpeakerLightLocked(const LightState &state) {
    Color color(state.color);
    const Color maxColor = ((color.red != 0) + (color.green != 0) + (color.blue != 0) > 1) ? mMaxMix : mMaxSingle;
    color.red = scaleBrightness(color.red, maxColor.red);
    color.green = scaleBrightness(color.green, maxColor.green);
    color.blue = scaleBrightness(color.blue, maxColor.blue);

    const int onMS = state.flashOnMs;
    const int offMS = state.flashOffMs;
    bool status;
    if (state.flashMode != Flash::NONE) {
        // Setup synchronized blinking
        status = write(LED_FILE(rgb, sync_state), 1);
        HANDLE_LED_BLINK_VALUES(red)
        HANDLE_LED_BLINK_VALUES(green)
        HANDLE_LED_BLINK_VALUES(blue)
        // And start
        status &= write(LED_FILE(rgb, start_blink), 1);
        mIsBlinking = true;
    } else {
        if (mIsBlinking) {
            // Disable blinking
            write(LED_FILE(rgb, sync_state), 0);
            mIsBlinking = false;
        }
        status = write(LED_FILE(red, brightness), color.red) && 
            write(LED_FILE(green, brightness), color.green) && 
            write(LED_FILE(blue, brightness), color.blue);
    }

#if 1
    LOG(DEBUG) << "set_speaker_light_locked mode " << static_cast<int>(state.flashMode) <<
            " colorRGB=" << state.color << " onMS=" << onMS << " offMS=" << offMS << " result: " << status;
#endif

    return status;
}

Status Light::setLightButtons(const LightState &state) {
    if(!mHasButtonFile)
        return Status::LIGHT_NOT_SUPPORTED;
    std::lock_guard<std::mutex> lock(mLock);
    return write(BUTTON_FILE, static_cast<int>(state.color & 0xFF)) ? Status::SUCCESS : Status::UNKNOWN;
}

bool Light::handleSpeakerBatteryLocked() {
    if (isLit(batteryState)) {
        return setSpeakerLightLocked(batteryState);
    } else {
         return setSpeakerLightLocked(notificationState);
    }
}

bool Light::setLightBattery(const LightState &state) {
    std::lock_guard<std::mutex> lock(mLock);
    batteryState = state;
    return handleSpeakerBatteryLocked();
}

bool Light::setLightNotifications(const LightState &state) {
    std::lock_guard<std::mutex> lock(mLock);
    notificationState = state;
    return handleSpeakerBatteryLocked();
}

Return<void> Light::getSupportedTypes(getSupportedTypes_cb _hidl_cb) {
    _hidl_cb(mSupportedTypes);
    return Void();
}
} // namespace implementation
} // namespace V2_0
} // namespace light
} // namespace hardware
} // namespace android
