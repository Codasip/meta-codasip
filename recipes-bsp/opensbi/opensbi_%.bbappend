FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

ERROR_QA:remove = "patch-status"

SRC_URI += " \
    file://0001-platform-generic-use-minimal-defconfig.patch \
"
