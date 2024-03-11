FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${AUTOREV}"
PV = "8.1.3+git${SRCPV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac"

SRC_URI = " \
    gitsm://${CODASIP_GIT_REPO}/qemu;protocol=${CODASIP_GIT_PROTOCOL};branch=${CODASIP_GIT_BRANCH} \
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

S = "${WORKDIR}/git"
