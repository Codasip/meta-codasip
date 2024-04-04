FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
# This corresponds to tag: v8.1.3
SRCREV = "179cc58e00eab7497ce0ac3a1897ec4878588a15"

PV = "8.1.3+git"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac"

ERROR_QA:remove = "patch-status"

SRC_URI = " \
    gitsm://github.com/qemu/qemu;protocol=https;branch=stable-8.1 \
    file://0001-target-cpu-Add-Codasip-A730-CPU-type.patch \
    file://0002-hw-riscv-Add-Codasip-Hobgoblin-board.patch \
    file://0003-hw-gpio-Import-Xilinx-AXI-GPIO.patch \
    file://0004-hw-riscv-hobgoblin-Change-PLIC-priority-base.patch \
    file://0005-hw-riscv-hobgoblin-Add-SPI-and-GPIO-devices.patch \
    file://0006-hw-riscv-hobgoblin-Support-two-PLIC-contexts.patch \
    file://0007-hw-riscv-hobgoblin-Wire-up-an-SD-card-to-the-SPI-con.patch \
    file://0008-hw-riscv-hobgoblin-Load-FSBL-or-normal-QEMU-OpenSBI-.patch \
    file://0009-target-cpu-Update-A730-extensions.patch \
    file://0010-hw-sd-ssi-sd-Fix-response-to-CMD58-READ_OCR.patch \
    file://0011-hw-sd-sdcard-Fix-response-to-CMD0-GO_IDLE_STATE.patch \
    file://0012-hw-riscv-hobgoblin-Add-SRAM-block.patch \
    file://0013-hw-riscv-hobgoblin-Add-virtio-blocks.patch \
    file://0014-hw-riscv-hobgoblin-add-gpio-restart.patch \
    file://0015-hw-riscv-hobgoblin-add-Xilinx-Ethernet-Lite-MAC.patch \
    file://0016-net-xilinx_ethlite-Add-PHY-emulation.patch \
    file://0017-hw-intc-sifive_plic-Add-edge-detection-for-level-int.patch \
    file://fixedmeson.patch \
    file://powerpc_rom.bin \
    file://qemu-guest-agent.init \
    file://qemu-guest-agent.udev \
"

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
