DESCRIPTION = "Files needed to boot on the Hobgoblin platform."
LICENSE = "Codasip"
LICENSE_FLAGS = "commercial"

inherit nopackages deploy

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "hobgoblin"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = " \
	file://config.txt \
	file://fsbl_rom.xexe \
"

do_deploy() {
	cp ${WORKDIR}/config.txt ${DEPLOYDIR}/
	cp ${WORKDIR}/fsbl_rom.xexe ${DEPLOYDIR}/
}

addtask deploy after do_compile
do_deploy[dirs] += "${DEPLOYDIR}/"
