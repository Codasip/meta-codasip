require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit uboot-extlinux-config

SUMMARY = "Hobgoblin U-Boot recipe"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
PV = "2023.10+git"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
# This corresponds to tag: v2023.10
SRCREV = "4459ed60cb1e0562bc5b40405e2b4b9bbf766d57"

# git://source.denx.de/u-boot/u-boot.git;protocol=https;branch=master

SRC_URI += " \
    file://0001-board-codasip-add-support-for-Codasip-A70X-cores-on-.patch \
    file://0002-spi-xilinx_spi-Fix-warning.patch \
    file://0003-spi-xilinx_spi-Reinstate-xfer-function.patch \
    file://0004-configs-codasip-a70x-hobgoblin-Add-new-config.patch \
    file://0005-spi-xilinx_spi-Fix-startup-workaround.patch \
    file://0006-board-codasip-a70x-Ensure-some-dependant-options-are.patch \
    file://0007-configs-codasip-a70x-hobgoblin-Move-environment-file.patch \
    file://0008-board-codasip-Tidy-up-default-environment-variables.patch \
    file://0009-board-codasip-Remove-CONFIG_SYS_SDRAM_BASE.patch \
    file://0010-board-codasip-Switch-to-distro_bootcmd.patch \
    file://0011-board-codasip-Remove-support-for-EFI.patch \
    file://0012-configs-codasip-a70x-hobgoblin-add-reset-support.patch \
    file://0013-configs-codasip-a70x-hobgoblin-secure-boot.patch \
    file://boot-mmc.scr.pp \
"

COMPATIBLE_MACHINE = "hobgoblin"

TOOLCHAIN = "gcc"

do_deploy:append() {
    install -m 644 ${WORKDIR}/boot-mmc.scr.pp ${DEPLOYDIR}/
}

# U-boot sets O=... which needs it to build outside of S
B = "${WORKDIR}/build"
