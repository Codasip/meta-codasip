echo Loading kernel...
fatload mmc 0:1 @{CODASIP_KERNEL_LOADADDR}@ @{KERNEL_IMAGETYPE}@
if test -z "${bootargs}" ; then setenv bootargs earlycon console=ttyS0,115200 root=/dev/mmcblk0p2 ; fi
booti @{CODASIP_KERNEL_LOADADDR}@ - ${fdtcontroladdr}
