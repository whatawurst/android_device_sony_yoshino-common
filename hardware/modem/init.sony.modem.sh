#!/vendor/bin/sh

OEM_DIR="/tmp/oem"

if [ ! -d "${OEM_DIR}" ]; then
    mkdir "${OEM_DIR}"
fi

ln -sf /oem-modem/modem-config ${OEM_DIR}/modem-config

mount --bind ${OEM_DIR} /oem
mount -o remount,bind,ro ${OEM_DIR} /oem

exit 0
