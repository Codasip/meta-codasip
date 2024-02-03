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
BRANCH = "hobgoblin"

SRC_URI = " \
    git://git@gitlab.codasip.com/cheri/software/cherilinux/linux.git;protocol=ssh;branch=${BRANCH} \
"

LINUX_VERSION ?= "6.5.0"
LINUX_VERSION_EXTENSION:append = "-hobgoblin"

KBUILD_DEFCONFIG = "codasip-a70x-hobgoblin_defconfig"

COMPATIBLE_MACHINE = "(hobgoblin)"
