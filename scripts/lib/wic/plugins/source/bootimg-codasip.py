#
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2024 Codasip GmbH
#
# DESCRIPTION
# Implement the 'secure-boot' source plugin class for
# 'wic'. The plugin creates an image of boot partition
# using the secure-boot tools.
#
# This is a thin layer ontop of the existing bootimg-partition
# plugin, generating the 'flash.bin' file from the secure-boot
# specific parameters, and then adding it to the partition.
# However this is made more complicated because we can't
# directly inherit from bootimg-partition as the name has a
# hyphen in it. See biosplusefi.py for more infotmation on
# how this is worked around using importlib.

import logging
import os
import re
import configparser
import types

from glob import glob

from wic import WicError
from wic.pluginbase import SourcePlugin
from wic.misc import exec_cmd, exec_native_cmd, get_bitbake_var

from importlib.machinery import SourceFileLoader

logger = logging.getLogger('wic')

class BootimgCodasipPlugin(SourcePlugin):
    """
    Create an image of boot partition, copying over files
    listed in IMAGE_BOOT_FILES bitbake variable.
    """

    name = 'bootimg-codasip'
    __bootimg_partition_obj = None
    __BOOTIMG_MODULE_NAME = 'bootimg-partition'
    __bbvar_prefix = 'CODASIP_'

    @classmethod
    def __init__(cls):
        """
        Constructor (init)
        """

        # XXX
        # For some reasons, __init__ constructor is never called.
        # Something to do with how pluginbase works?
        cls.__instanciateSubClasses()

    @classmethod
    def __instanciateSubClasses(cls):
        """

        """

        # Import bootimg-partition (class name "BootimgPartitionPlugin")
        corebase = get_bitbake_var("COREBASE")
        path = os.path.join(corebase, "scripts/lib/wic/plugins/source",
                                cls.__BOOTIMG_MODULE_NAME + ".py")
        if not os.path.isfile(path):
            raise WicError("Couldn't find %s, exiting" % cls.__BOOTIMG_MODULE_NAME)

        loader = SourceFileLoader(cls.__BOOTIMG_MODULE_NAME, path)
        mod = types.ModuleType(loader.name)
        loader.exec_module(mod)
        cls.__bootimg_partition_obj = mod.BootimgPartitionPlugin()

    @classmethod
    def do_install_disk(cls, disk, disk_name, creator, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image.
        """

        if not cls.__bootimg_partition_obj :
            cls.__instanciateSubClasses()

        cls.__bootimg_partition_obj.do_install_disk(disk, disk_name, creator, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot)

    @classmethod
    def do_configure_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             native_sysroot):
        """
        Called before do_prepare_partition(), create secure-boot image
        """

        def check_get_bitbake_var(var, prefix = cls.__bbvar_prefix, empty_ok = False) :
            if prefix :
                var = prefix + var
            val = get_bitbake_var(var)
            logger.debug('Var "%s" is \"%s\"', var, val)
            if not val and not empty_ok :
                raise WicError("Variable %s undefined" % var)
            return val

        def split_path_addr(string) :
            path, addr = string.split(':')
            return path, addr

        def set_path_addr(section, key_prefix, path_addr) :
            path, addr = split_path_addr(path_addr)
            if path[0] != "/" :
                path = os.path.join(kernel_dir, path)
            section[key_prefix + '_path'] = path
            section[key_prefix + '_addr'] = addr

        def set_path_addr_var(section, key_prefix, var) :
            path_addr = check_get_bitbake_var(var)
            set_path_addr(section, key_prefix, path_addr)

        def run_sb_image_tool(config, tool, filename) :
            config_file = os.path.join(tmpdir, filename)
            with open(config_file, "w") as fp :
                config.write(fp)

            exec_native_cmd("%s --config %s" % (tool, config_file), native_sysroot)

        def preprocess_replace(matchobj) :
            var, word, field = matchobj.group(1, 2, 3)
            logger.debug('Matched "%s", word "%s", field "%s"', var, word, field)
            val = check_get_bitbake_var(var, prefix = None)
            if word :
                s = val.split()
                val = s[int(word)]
            if field :
                s = val.split(':')
                val = s[int(field)]
            return val

        def preprocess(src_filename, dst_filename) :
            with open(src_filename, "r") as fp :
                text = fp.read()

            text = re.sub(r'@\{([^:}]+)(?::([0-9]*)(?::([0-9]*))?)?\}@', preprocess_replace, text)

            with open(dst_filename, "w") as fp :
                fp.write(text)

        if not cls.__bootimg_partition_obj :
            cls.__instanciateSubClasses()

        cls.__bootimg_partition_obj.do_configure_partition(part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             native_sysroot)

        hdddir = "%s/boot.%d" % (cr_workdir, part.lineno)

        tmpdir = "%s/bootimg-codasip-tmp.%d" % (cr_workdir, part.lineno)
        install_cmd = "install -d %s" % tmpdir
        exec_cmd(install_cmd)

        if not kernel_dir:
            kernel_dir = check_get_bitbake_var("DEPLOY_DIR_IMAGE", prefix = None)

        logger.debug('source_params: %s', source_params)
        logger.debug('cr_workdir: %s', cr_workdir)
        logger.debug('oe_builddir: %s', oe_builddir)
        logger.debug('bootimg_dir: %s', bootimg_dir)
        logger.debug('kernel_dir: %s', kernel_dir)
        logger.debug('native_sysroot: %s', native_sysroot)

        datadir = os.path.join(native_sysroot + os.getenv("datadir"),
                               "secure-boot")

        if int(check_get_bitbake_var("SECURE_BOOT")) :

            # Create Secure Boot Image (SBFW)

            config = configparser.ConfigParser()
            config.read_dict({'settings' : {
                'version':              '1',
                'image_type':           'SBFW',
                'reg_width':            '64',
                'runtime_path':         os.path.join(datadir, 'binaries', 'sbfw.bin'),
                'runtime_addr':         '0x20040000',
                'out_path':             os.path.join(tmpdir, 'sbfw.sbi'),
            } } )
            run_sb_image_tool(config, 'sb_image_tool', 'sb_image_tool_sbfw.ini')

            # Create Secure Boot Image (RT)

            config = configparser.ConfigParser()
            config.read_dict({'settings' : {
                'version':              '1',
                'image_type':           'RT',
                'out_path':             os.path.join(tmpdir, 'rt.sbi'),
                'reg_width':            '64',
            } } )

            settings = config['settings']
            set_path_addr_var(settings, 'dtb', 'DTB')
            set_path_addr_var(settings, 'opensbi', 'OPENSBI')
            set_path_addr_var(settings, 'runtime', 'RUNTIME')
            imgs = check_get_bitbake_var('IMGS', empty_ok = True) or ''
            num = 0
            for path_addr in imgs.split() :
                set_path_addr(settings, 'load_img_%d' % num, path_addr)
                num += 1

            run_sb_image_tool(config, 'sb_image_tool', 'sb_image_tool_rt.ini')

            # Sign Secure Boot Images (SBFW)

            config = configparser.ConfigParser()
            config.read_dict({'settings' : {
                'version':              '1',
                'image_type':           'SBFW',
                'image_path':           os.path.join(tmpdir, 'sbfw.sbi'),
                'out_path':             os.path.join(tmpdir, 'sbfw_signed.sbi'),
                'auth_key_path':        os.path.join(datadir, 'keys', 'sbfw_auth_public.key'),
                'certificate_signing_key_path':
                    os.path.join(datadir, 'keys', 'root_private.key'),
                'enc_key_path':         os.path.join(datadir, 'keys', 'sbfw_enc.key'),
                'manifest_signing_key_path':
                    os.path.join(datadir, 'keys', 'sbfw_auth_private.key'),
                'device_key_path':      os.path.join(datadir, 'keys', 'device.key'),
                'device_nonce_path':    os.path.join(datadir, 'keys', 'device_nonce.bin'),
            } } )
            run_sb_image_tool(config, 'sb_signing_tool', 'sb_signing_tool_sbfw.ini')

            # Sign Secure Boot Images (RT)

            changes = {
                'image_type':           'RT',
                'image_path':           os.path.join(tmpdir, 'rt.sbi'),
                'out_path':             os.path.join(tmpdir, 'rt_signed.sbi'),
            }
            settings = config['settings']
            for key in changes :
                settings[key] = changes[key]
            run_sb_image_tool(config, 'sb_signing_tool', 'sb_signing_tool_rt.ini')

            # Create Flash Image

            config = configparser.ConfigParser()
            config.read_dict({'settings' : {
                'sbfw_a_path':          os.path.join(tmpdir, 'sbfw_signed.sbi'),
                'rt_a_path':            os.path.join(tmpdir, 'rt_signed.sbi'),
                'out_path':             os.path.join(tmpdir, 'flash.bin'),
            } } )
            run_sb_image_tool(config, 'sb_flash_image_tool', 'sb_flash_image_tool.ini')

            # Finally add the generated file to those copied into the partition
            cls.__bootimg_partition_obj.install_task.append(
                (os.path.join(tmpdir, 'flash.bin'), 'flash.bin'))

        else :
            config_txt = "# Codasip fsbl configuration file\n"

            for f in [('DTB', 'FDT'), ('OPENSBI', 'BOOT'), ('RUNTIME', 'NXT')] :
                val = check_get_bitbake_var(f[0])
                path, addr = split_path_addr(val)
                config_txt += "/%s\t%s\t%s\n" % (path, addr, f[1])
                cls.__bootimg_partition_obj.install_task.append((path, path))

            imgs = check_get_bitbake_var('IMGS', empty_ok = True) or ''
            for path_addr in imgs.split() :
                path, addr = split_path_addr(path_addr)
                config_txt += "/%s\t%s\n" % (path, addr)
                cls.__bootimg_partition_obj.install_task.append((path, path))

            with open(os.path.join(kernel_dir, 'config.txt'), "w") as cfg :
                cfg.write(config_txt)

            cls.__bootimg_partition_obj.install_task.append(('config.txt', 'config.txt'))

        # Pre-process and build the U-Boot script
        for task in cls.__bootimg_partition_obj.install_task:
            src, dst = task
            logger.debug('task %s -> %s', src, dst)
            if src[-7:] != '.scr.pp' or dst[-9:] != '.scr.uimg' :
                continue

            cls.__bootimg_partition_obj.install_task.remove(task)

            tmp1 = os.path.join(tmpdir, os.path.basename(src))
            preprocess(os.path.join(kernel_dir, src), tmp1)
            tmp2 = os.path.join(tmpdir, os.path.basename(dst))
            cmd = ('mkimage -O linux -T script -C none -n "U-Boot boot script" -d %s %s' %
                (tmp1, tmp2))
            exec_native_cmd(cmd, native_sysroot)

            cls.__bootimg_partition_obj.install_task.append((tmp2, dst))

    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        if not cls.__bootimg_partition_obj :
            cls.__instanciateSubClasses()

        cls.__bootimg_partition_obj.do_prepare_partition(part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot)
