ifeq ($(BOARD_SUPPORTS_SOUND_TRIGGER_HAL),true)

include $(CLEAR_VARS)
WCD9320_ANC_SYMLINK := $(TARGET_OUT_VENDOR)/etc/firmware/wcd9320/wcd9320_anc.bin
$(WCD9320_ANC_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "WCD9320 anc link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /data/vendor/misc/audio/$(notdir $@) $@

WCD9320_MAD_AUDIO_SYMLINK := $(TARGET_OUT_VENDOR)/etc/firmware/wcd9320/wcd9320_mad_audio.bin
$(WCD9320_MAD_AUDIO_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "WCD9320 mad aduio link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /data/vendor/misc/audio/$(notdir $@) $@

WCD9320_MBHC_SYMLINK := $(TARGET_OUT_VENDOR)/etc/firmware/wcd9320/wcd9320_mbhc.bin
$(WCD9320_MBHC_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "WCNSS MAC bin link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /data/vendor/misc/audio/mbhc.bin $@

ALL_DEFAULT_INSTALLED_MODULES += \
    $(WCD9320_ANC_SYMLINK) \
    $(WCD9320_MAD_AUDIO_SYMLINK) \
    $(WCD9320_MBHC_SYMLINK)

endif
