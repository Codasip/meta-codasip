PERF_SRC:append = "arch/arm64/tools arch/${ARCH}/include/uapi/asm"
EXTRA_OEMAKE += " BUILD_BPF_SKEL=0"

do_configure:append () {
}

