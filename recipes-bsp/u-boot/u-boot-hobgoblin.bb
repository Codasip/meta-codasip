require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit uboot-extlinux-config

SUMMARY = "Hobgoblin U-Boot recipe"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRCREV = "${AUTOREV}"
PV = "2023.10+git${SRCPV}"

SRC_URI = " \
    git://${CODASIP_GIT_CHERILINUX_REPO}/u-boot.git;protocol=${CODASIP_GIT_PROTOCOL};branch=${CODASIP_GIT_BRANCH} \
    file://boot-mmc.scr.pp \
"

COMPATIBLE_MACHINE = "hobgoblin"

TOOLCHAIN = "gcc"

do_deploy:append() {
    install -m 644 ${WORKDIR}/boot-mmc.scr.pp ${DEPLOYDIR}/
}

# U-boot sets O=... which needs it to build outside of S
B = "${WORKDIR}/build"
