#!/bin/bash

DIR="build"
MACHINE="hobgoblin"
CONFFILE="conf/auto.conf"
BITBAKEIMAGE="core-image-minimal"

# clean up the output dir
#echo "Cleaning build dir"
#rm -rf $DIR

# make sure sstate is there
#echo "Creating sstate directory"
#mkdir -p ~/sstate/$MACHINE

echo $(pwd)

# Reconfigure dash on debian-like systems
which aptitude > /dev/null 2>&1
ret=$?
if [ "$(readlink /bin/sh)" = "dash" -a "$ret" = "0" ]; then
  sudo aptitude install expect -y
  expect -c 'spawn sudo dpkg-reconfigure -freadline dash; send "n\n"; interact;'
elif [ "${0##*/}" = "dash" ]; then
  echo "dash as default shell is not supported"
  return
fi
# bootstrap OE
echo "Init OE"
export BASH_SOURCE="poky/oe-init-build-env"
. ./poky/oe-init-build-env $DIR

# Symlink the cache
#echo "Setup symlink for sstate"
#ln -s ~/sstate/${MACHINE} sstate-cache

# add the missing layers
echo "Adding layers"
bitbake-layers add-layer ../meta-codasip
bitbake-layers add-layer ../meta-clang

# fix the configuration
echo "Creating auto.conf"

if [ -e $CONFFILE ]; then
    rm -rf $CONFFILE
fi
cat <<EOF > $CONFFILE
MACHINE ?= "${MACHINE}"
USER_CLASSES ?= "buildstats buildhistory buildstats-summary"

# Only needed if we use OE directly, not when using poky
#require conf/distro/include/no-static-libs.inc
#require conf/distro/include/yocto-uninative.inc
#require conf/distro/include/security_flags.inc

INHERIT += "uninative"
DISTRO_FEATURES = "ipv4 sysvinit"
IMAGE_FSTYPES="ext4"
TOOLCHAIN = "clang"
EOF

#echo "Creating initramfs.conf"
#
#if [ -e $INITRAMFS_CONF ]; then
#    rm -rf $INITRAMFS_CONF
#fi
#cat <<EOF > $INITRAMFS_CONF
#INITRAMFS_IMAGE = "mpfs-core-image-base"
#EOF


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
