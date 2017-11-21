### KEYLAYOUT
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/system/keylayout/gpio-keys.kl:system/usr/keylayout/gpio-keys.kl

### IDC
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/system/idc/clearpad.idc:system/usr/idc/clearpad.idc

### IRCBALANCE
# MSM IRQ Balancer configuration file
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/irqbalance/msm_irqbalance.conf:system/etc/msm_irqbalance.conf

### AUDIO
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/audio/aanc_tuning_mixer.txt:system/etc/aanc_tuning_mixer.txt
