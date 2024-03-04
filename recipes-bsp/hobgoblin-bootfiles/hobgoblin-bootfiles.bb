DESCRIPTION = "Files needed to boot on the Hobgoblin platform."
LICENSE = "Codasip"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2faad86a22b25ebf61a8d9b1ed916ada"
LICENSE_FLAGS = "commercial"

inherit nopackages deploy

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "hobgoblin"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

S = "${WORKDIR}"

SRC_URI = " \
	file://config.txt \
	file://LICENSE \
	${@bb.utils.contains('BBMULTICONFIG', 'baremetal', '', 'file://fsbl_rom.xexe', d)} \
"

do_deploy() {
	cp ${WORKDIR}/config.txt ${DEPLOYDIR}/
	fsbldir="${@bb.utils.contains('BBMULTICONFIG', 'baremetal', '${BAREMETAL_DEPLOY_DIR}', '${WORKDIR}', d)}"
	cp $fsbldir/fsbl_rom.xexe ${DEPLOYDIR}/
}

addtask deploy after do_compile
do_deploy[dirs] += "${DEPLOYDIR}/"

FSBL_MCDEPENDS = "${@bb.utils.contains('BBMULTICONFIG', 'baremetal', 'mc::baremetal:baremetal-sdk:do_deploy', '', d)}"
do_fetch[mcdepends] += "${FSBL_MCDEPENDS}"
