ifeq ($(BOARD_WLAN_DEVICE),qcwcn)

include $(CLEAR_VARS)
WCNSS_INI_SYMLINK := $(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/WCNSS_qcom_cfg.ini
$(WCNSS_INI_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "WCNSS config ini link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /vendor/etc/wifi/$(notdir $@) $@

WCNSS_BDWLAN_SYMLINK := $(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/bdwlan.bin
$(WCNSS_BDWLAN_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "WCNSS BDWLAN bin link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /vendor/etc/wifi/$(notdir $@) $@

WCNSS_MAC_SYMLINK := $(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/wlan_mac.bin
$(WCNSS_MAC_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "WCNSS MAC bin link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /data/etc/$(notdir $@) $@

ALL_DEFAULT_INSTALLED_MODULES += \
    $(WCNSS_INI_SYMLINK) \
    $(WCNSS_BDWLAN_SYMLINK) \
    $(WCNSS_MAC_SYMLINK)

endif
