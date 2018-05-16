#!/sbin/sh

if [ ! -d /tmp/system ]; then
    mkdir /tmp/system
fi
mount -t ext4 -o ro "/dev/block/bootdevice/by-name/system" /tmp/system
ret=$?
if [ $ret != 0 ]; then
    echo "Could not read build properties from /system"
    setprop crypto.ready 1
    exit 0
fi

build_prop="/tmp/system/build.prop"
if [ -r $build_prop ]; then
    osver=$(grep 'ro.build.version.release' $build_prop  | cut -d'=' -f2)
    patchlevel=$(grep 'ro.build.version.security_patch' $build_prop  | cut -d'=' -f2)

    if [ "x$osver" != "x" ]; then
        setprop ro.build.version.release "$osver"
    fi
    if [ "x$patchlevel" != "x" ]; then
        setprop ro.build.version.security_patch "$patchlevel"
    fi
fi

umount /tmp/system

setprop crypto.ready 1
exit 0
