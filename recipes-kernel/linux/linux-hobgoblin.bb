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

# We use the revision in order to avoid having to fetch it from the
# repo during parse
# This corresponds to tag: v6.8.9
SRCREV = "f3d61438b613b87afb63118bea6fb18c50ba7a6b"

PV = "6.5+git"

SRC_URI = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git;branch=linux-6.8.y \
    file://0001-riscv-Add-the-Codasip-SoC-family-Kconfig-option.patch \
    file://0002-riscv-dts-codasip-Add-Hobgoblin-device-tree.patch \
    file://0003-riscv-dts-Add-codasip-devicetree.patch \
    file://0004-riscv-configs-hobgoblin-Add-a-Codasip-Hobgoblin-conf.patch \
    file://0005-riscv-dts-codasip-Add-Hobgoblin-qemu-support.patch \
    file://0006-riscv-configs-hobgoblin-Add-LED-and-GPIO-devices.patch \
    file://0007-riscv-hobgoblin-add-configuration-for-gpio-restart.patch \
    file://0008-riscv-hobgoblin-Xilinx-EthernetLite-support.patch \
    file://0009-riscv-hobgoblin-Enable-syn-cookies.patch \
    file://0010-net-emaclite-Use-xemaclite_-readl-writel-to-access-p.patch \
    file://0011-net-emaclite-Fix-xemaclite_aligned_read-of-trailing-.patch \
    file://0012-riscv-configs-hobgoblin-Enable-NFS-support.patch \
    file://0013-riscv-Fix-linear-mapping-checks-for-non-contiguous-m.patch \
    file://0014-riscv-Remove-redundant-restriction-on-memory-size.patch \
    file://0015-riscv-configs-hobgoblin-Convert-isa-detection-to-new.patch \
    file://defconfig \
"

LINUX_VERSION ?= "6.5.0"
LINUX_VERSION_EXTENSION:append = "-hobgoblin"

KCONFIG_MODE="--alldefconfig"

COMPATIBLE_MACHINE = "(hobgoblin)"

TOOLCHAIN:forcevariable = "clang"
DEPENDS:append:toolchain-clang = " clang-cross-${TARGET_ARCH}"
KERNEL_CC:toolchain-clang = "${CCACHE}clang ${HOST_CC_KERNEL_ARCH} ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH}"
KERNEL_LD:toolchain-clang = "${CCACHE}ld.lld"
KERNEL_AR:toolchain-clang = "${CCACHE}llvm-ar"

# Keep kernel_configcheck task happy when it calls symbol_why.py
CLANG_FLAGS:toolchain-clang = "-fintegrated-as"
export CLANG_FLAGS
