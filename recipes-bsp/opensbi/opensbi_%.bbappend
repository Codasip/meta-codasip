FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

ERROR_QA:remove = "patch-status"

SRCREV = "${AUTOREV}"
PV = "1.4+git${SRCPV}"

SRC_URI = " \
    git://${CODASIP_GIT_CHERILINUX_REPO}/opensbi;protocol=${CODASIP_GIT_PROTOCOL};branch=${CODASIP_GIT_BRANCH} \
"
