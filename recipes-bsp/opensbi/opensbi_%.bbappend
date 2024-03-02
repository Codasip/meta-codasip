FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${AUTOREV}"
PV = "1.2+git${SRCPV}"

SRC_URI = " \
    git://${CODASIP_GIT_REPO}/opensbi;protocol=${CODASIP_GIT_PROTOCOL};branch=${CODASIP_GIT_BRANCH} \
"
