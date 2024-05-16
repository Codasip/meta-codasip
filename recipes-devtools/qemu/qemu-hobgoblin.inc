FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
# This corresponds to tag: v8.2.4
SRCREV = "1332b8dd434674480f0feb2cdf3bbaebb85b4240"

PV = "8.2.4+git"
PR = "r0"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac"

ERROR_QA:remove = "patch-status"

SRC_URI = " \
    gitsm://github.com/qemu/qemu;protocol=https;branch=stable-8.2 \
    file://0001-hw-sd-ssi-sd-Fix-response-to-CMD58-READ_OCR.patch \
    file://0002-hw-sd-sdcard-Fix-response-to-CMD0-GO_IDLE_STATE.patch \
    file://0003-hw-gpio-Import-Xilinx-AXI-GPIO.patch \
    file://0004-riscv-plic-add-const-qualifier-to-signature.patch \
    file://0005-target-cpu-Add-Codasip-A730-CPU.patch \
    file://0006-hw-riscv-Add-Codasip-Hobgoblin-machine.patch \
    file://0007-hw-riscv-hobgoblin-sanitize-and-explain-UART-mapping.patch \
    file://0008-hw-riscv-hobgoblin-add-gpio-restart.patch \
    file://0009-hw-riscv-hobgoblin-Add-FPGA-SRAM-block.patch \
    file://0010-hw-riscv-hobgoblin-add-Xilinx-Ethernet-Lite-MAC.patch \
    file://0011-net-xilinx_ethlite-Add-PHY-emulation.patch \
    file://0012-hw-riscv-hobgoblin-Add-virtio-blocks.patch \
    file://0013-hw-intc-sifive_plic-Add-edge-detection-for-level-int.patch \
    file://0014-hw-riscv-hobgoblin-Add-support-for-proFPGA-platform.patch \
    file://0015-hw-riscv-hobgoblin-Increase-size-of-ROM-to-128K.patch \
    file://fixedmeson.patch \
    file://powerpc_rom.bin \
    file://qemu-guest-agent.init \
    file://qemu-guest-agent.udev \
"

SRC_URI:remove:class-target = " file://cross.patch"
SRC_URI:remove:class-nativesdk = " file://cross.patch"

EXTRA_OECONF:append = " --host-cc=${BUILD_CC}"
EXTRA_OECONF:append:class-target = " --cross-prefix=${HOST_PREFIX}"
EXTRA_OECONF:append:class-nativesdk = " --cross-prefix=${HOST_PREFIX}"

do_configure:prepend () {
    export PKG_CONFIG=pkg-config
}

# qemu uses a mixture of git submodules (handled by specifying a SRC_URI of gitsm:)
# but also meson submodules, so fetch those as well:
do_meson_fetch() {
    cd ${S}
    pwd
    meson subprojects download
}
do_meson_fetch[depends] = "meson-native:do_populate_sysroot"
addtask meson_fetch after do_unpack before do_patch
do_meson_fetch[network] = "1"

S = "${WORKDIR}/git"

do_install[depends] += "hobgoblin-bootfiles:do_deploy"

do_install:append() {
    install -Dm 0644 ${DEPLOY_DIR_IMAGE}/fsbl_rom.xexe ${D}${datadir}/qemu
}
