require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit uboot-extlinux-config

SUMMARY = "Hobgoblin U-Boot recipe"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRCREV = "${AUTOREV}"
PV = "1.0+git${SRCPV}"

BRANCH = "hobgoblin"

SRC_URI = " \
    git://git@gitlab.codasip.com/cheri/software/cherilinux/u-boot.git;protocol=ssh;branch=${BRANCH} \
"
DEPENDS:append = " u-boot-tools-native"

COMPATIBLE_MACHINE = "hobgoblin"

TOOLCHAIN = "gcc"


# U-boot sets O=... which needs it to build outside of S
B = "${WORKDIR}/build"
