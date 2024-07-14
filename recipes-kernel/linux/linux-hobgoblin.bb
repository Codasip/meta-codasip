DESCRIPTION = "Hobgoblin Linux Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

KCONF_AUDIT_LEVEL ?= "2"
CONF_BSP_AUDIT_LEVEL ?= "3"
KMETA_AUDIT ?= "yes"


FILESEXTRAPATHS =. "${FILE_DIRNAME}/files:"

KERNEL_VERSION_SANITY_SKIP = "1"

SRCREV = "${AUTOREV}"
PV = "6.5+git${SRCPV}"

SRC_URI = " \
    git://${CODASIP_GIT_CHERILINUX_REPO}/linux.git;protocol=${CODASIP_GIT_PROTOCOL};branch=${CODASIP_GIT_BRANCH} \
"

LINUX_VERSION ?= "6.5.0"
LINUX_VERSION_EXTENSION:append = "-hobgoblin"

KCONFIG_MODE="--alldefconfig"

KBUILD_DEFCONFIG = "codasip-a70x-hobgoblin_defconfig"

COMPATIBLE_MACHINE = "(hobgoblin)"

TOOLCHAIN:forcevariable = "clang"
DEPENDS:append:toolchain-clang = " clang-cross-${TARGET_ARCH}"
KERNEL_CC:toolchain-clang = "${CCACHE}clang ${HOST_CC_KERNEL_ARCH} ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH}"
KERNEL_LD:toolchain-clang = "${CCACHE}ld.lld"
KERNEL_AR:toolchain-clang = "${CCACHE}llvm-ar"

# Keep kernel_configcheck task happy when it calls symbol_why.py
CLANG_FLAGS:toolchain-clang = "-fintegrated-as"
export CLANG_FLAGS
