FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

ERROR_QA:remove = "patch-status"

PV = "1.4+"
PR = "r0"

# This corresponds to v1.4 + several commits
SRCREV = "d4d2582eef7aac442076f955e4024403f8ff3d96"

SRC_URI += " \
    file://0001-platform-generic-use-minimal-defconfig.patch \
"
