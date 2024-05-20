SUMMARY = "A console-only image with more full-featured Linux system \
functionality installed plus some development and testing tools."

INIT_MANAGER = "systemd"

IMAGE_FEATURES += "\
    debug-tweaks \
    nfs-client \
    package-management \
    ssh-server-openssh \
    tools-debug \
    tools-sdk \
"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    packagegroup-core-sdk \
    packagegroup-core-tools-debug \
    e2fsprogs-resize2fs \
    e2fsprogs-tune2fs \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

# From meta-openembedded
IMAGE_INSTALL += "\
    libgpiod \
    iperf3 \
    mdio-tools \
    mg \
    stressapptest \
    tcpdump \
"
RDEPENDS:mdio-tools:remove = "kernel-module-mdio-netlink"

inherit core-image
