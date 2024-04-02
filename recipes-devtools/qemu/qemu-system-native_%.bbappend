require qemu-hobgoblin.inc

do_install[depends] += "hobgoblin-bootfiles:do_deploy"

do_install:append() {
    install -Dm 0644 ${DEPLOY_DIR_IMAGE}/fsbl_rom.xexe ${D}${datadir}/qemu
}
