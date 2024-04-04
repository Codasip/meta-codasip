# Codasip Linux Yocto layer

This layer provides a Linux BSP for Codasip processors and platforms.

## Supported platforms
This layer provides support for the following platforms:
- Hobgoblin FPGA platform with Codasip A730 application processor

## Building

Please see the build instructions in the [Codasip Yocto manifest](https://github.com/Codasip/yocto-manifest?tab=readme-ov-file#building-from-source)

## Dependencies

This layer depends on:

	URI: https://git.yoctoproject.org/poky
	layers: meta, meta-poky
	branch: nanbield

	URI: https://git.openembedded.org/meta-openembedded
	layers: meta-oe
	branch: nanbield

	URI: https://github.com/kraj/meta-clang
	layers: meta-clang
