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
#ifndef ANDROID_HARDWARE_LIGHT_V2_0_LIGHT_H
#define ANDROID_HARDWARE_LIGHT_V2_0_LIGHT_H

#include <android/hardware/light/2.0/ILight.h>
#include <hidl/Status.h>

#include <array>
#include <mutex>

namespace android {
namespace hardware {
namespace light {
namespace V2_0 {
namespace implementation {
using ::android::hardware::Return;
using ::android::hardware::light::V2_0::ILight;
using ::android::hardware::light::V2_0::LightState;
using ::android::hardware::light::V2_0::Status;
using ::android::hardware::light::V2_0::Type;

class Light : public ILight {
public:
    Light();

    // Methods from ::android::hardware::light::V2_0::ILight follow.
    Return<Status> setLight(Type type, const LightState &state) override;
    Return<void> getSupportedTypes(getSupportedTypes_cb _hidl_cb) override;

private:
    struct Color {
        Color() = default;
        Color(unsigned int);
        int red, green, blue;
    };

    std::mutex mLock, mLcdLock;
    std::string mLcdFile;
    int mBacklightMax = 0;
    // Max values for the RGB LED(s)
    Color mMaxSingle, mMaxMix;

    bool mHasButtonFile, mHasPersistenceFile;
    bool mLowPersistenceEnabled = false;
    bool mIsBlinking = false;
    LightState batteryState;
    LightState notificationState;

    hidl_vec<Type> mSupportedTypes;

    bool setLightBacklight(const LightState &state);
    bool setLightBattery(const LightState &state);
    bool setLightNotifications(const LightState &state);
    Status setLightButtons(const LightState &state);
    bool handleSpeakerBatteryLocked();
    bool setSpeakerLightLocked(const LightState &state);
};
} // namespace implementation
} // namespace V2_0
} // namespace light
} // namespace hardware
} // namespace android

#endif // ANDROID_HARDWARE_LIGHT_V2_0_LIGHT_H
