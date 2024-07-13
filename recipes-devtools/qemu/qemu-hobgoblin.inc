FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV = "${AUTOREV}"
PV = "8.2.4+git${SRCPV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac"

ERROR_QA:remove = "patch-status"

SRC_URI = " \
    gitsm://${CODASIP_GIT_CHERILINUX_REPO}/qemu.git;protocol=${CODASIP_GIT_PROTOCOL};branch=${CODASIP_GIT_BRANCH} \
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
