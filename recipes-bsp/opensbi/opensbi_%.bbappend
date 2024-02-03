FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${AUTOREV}"
PV = "1.2+git${SRCPV}"

BRANCH = "hobgoblin"

SRC_URI = " \
    git://git@gitlab.codasip.com/cheri/software/cherilinux/opensbi.git;protocol=ssh;branch=${BRANCH} \
"
