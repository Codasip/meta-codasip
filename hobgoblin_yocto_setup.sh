#!/bin/bash

DIR="${1:-build}"
MACHINE="hobgoblin"
CONFFILE="conf/auto.conf"
BITBAKEIMAGE="core-image-minimal"

# clean up the output dir
#echo "Cleaning build dir"
#rm -rf $DIR

# make sure sstate is there
#echo "Creating sstate directory"
#mkdir -p ~/sstate/$MACHINE

if [ "${0##*/}" = "dash" ]; then
    echo "Error: dash as default shell is not supported" >&2
    return
    exit 1
elif [ -n "$BASH_SOURCE" ]; then
    THIS_SCRIPT=$BASH_SOURCE
elif [ -n "$ZSH_NAME" ]; then
    THIS_SCRIPT=$0
else
    THIS_SCRIPT="$(pwd)/meta-codasip/hobgoblin_yocto_setup.sh"
    if [ ! -e "$THIS_SCRIPT" ]; then
	echo "Error: $THIS_SCRIPT doesn't exist!" >&2
	echo "Please run this script in the same directory as meta-codasip." >&2
	return
	exit 1
    fi
fi

# bootstrap OE
echo "Init OE"
base=$(dirname -- $(dirname -- $(readlink -f -- "$THIS_SCRIPT" )))
poky_init=$base/poky/oe-init-build-env

if [ ! -f $poky_init ] ; then
    echo "Error: Unable to find $poky_init relative to hobgoblin_yocto_setup.sh" >&2
    echo "Make sure the poky and meta-codasip directories are present" >&2
    echo "and in the current directory?" >&2
    return
    exit 1
fi

export BASH_SOURCE=$poky_init
. $poky_init $DIR

# Symlink the cache
#echo "Setup symlink for sstate"
#ln -s ~/sstate/${MACHINE} sstate-cache

# add the missing layers
echo "Adding layers"
bitbake-layers add-layer ${base}/meta-codasip
bitbake-layers add-layer ${base}/meta-clang
bitbake-layers add-layer ${base}/meta-openembedded/meta-oe
bitbake-layers add-layer ${base}/meta-openembedded/meta-networking

# fix the configuration
echo "Creating auto.conf"

if [ -e $CONFFILE ]; then
    rm -rf $CONFFILE
fi
cat <<EOF > $CONFFILE
MACHINE ?= "${MACHINE}"
DISTRO ?= "codasip-poky"
USER_CLASSES ?= "buildstats buildhistory buildstats-summary"

# Only needed if we use OE directly, not when using poky
#require conf/distro/include/no-static-libs.inc
#require conf/distro/include/yocto-uninative.inc
#require conf/distro/include/security_flags.inc

INHERIT += "uninative"
DISTRO_FEATURES = "ipv4 ipv6"
PACKAGECONFIG:remove:pn-qemu-system-native = "xen"
PACKAGECONFIG:append:pn-qemu-system-native = "vhost virtfs"
IMAGE_FSTYPES="ext4 wic"
IMAGE_NAME_SUFFIX=".sdcard"
EXTRA_IMAGECMD:ext4 = "-i 4096 -O ^orphan_file"
TOOLCHAIN_HOST_TASK:append = " nativesdk-e2fsprogs nativesdk-e2fsprogs-resize2fs nativesdk-e2fsprogs-tune2fs nativesdk-u-boot-tools"
EOF

echo "To build an image run"
echo "---------------------------------------------------"
echo "MACHINE=${MACHINE} bitbake ${BITBAKEIMAGE}"
echo "---------------------------------------------------"
echo ""
echo "Buildable machine info"
echo "---------------------------------------------------"
echo " Default MACHINE=${MACHINE}"
echo "* hobgoblin: Codasip A7xx Hobgoblin FPGA platform"
echo "---------------------------------------------------"
echo "Bitbake Image"
echo "---------------------------------------------------"
echo "* core-image-minimal: OE console-only image"
echo "* core-image-minimal-dev: OE console-only image, with some additional development packages."
echo "---------------------------------------------------"

# start build
#echo "Starting build"
#bitbake $BITBAKEIMAGE
