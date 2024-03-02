require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit uboot-extlinux-config

SUMMARY = "Hobgoblin U-Boot recipe"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRCREV = "${AUTOREV}"
PV = "1.0+git${SRCPV}"

SRC_URI = " \
    git://${CODASIP_GIT_REPO}/u-boot;protocol=${CODASIP_GIT_PROTOCOL};branch=${CODASIP_GIT_BRANCH} \
    file://boot.scr \
"
DEPENDS:append = " u-boot-tools-native"

COMPATIBLE_MACHINE = "hobgoblin"

TOOLCHAIN = "gcc"

do_configure:prepend () {
    mkimage -O linux -T script -C none -n "U-Boot boot script" \
	    -d ${WORKDIR}/boot.scr ${WORKDIR}/boot.scr.uimg
}

do_deploy:append() {
    install -m 644 ${WORKDIR}/boot.scr.uimg ${DEPLOYDIR}/
}

# U-boot sets O=... which needs it to build outside of S
B = "${WORKDIR}/build"
