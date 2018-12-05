package com.zfs.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by zeng on 2016/5/5.
 * 解压缩工具
 */
public class ZipUtils {
    /**
     * 压缩文件
     * @param replace 如果文件存在是否替换
     * @param filePath 待压缩的文件路径
     * @return 压缩后的文件
     */
    public static File zip(boolean replace, String filePath) {
        File target = null;
        File source = new File(filePath);
        if (source.exists()) {
            // 压缩文件名=源文件名.zip
            target = new File(source.getParent(), source.getName() + ".zip");
            if (target.exists()) {
                if (replace) {
                    if (!target.delete()) {
                        return null;
                    }
                } else {
                    return target;
                }
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // 添加对应的文件Entry
                addEntry(File.separator, source, zos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(zos, fos);
            }
        }
        return target;
    }
    
    /**
     * 多文件压缩文件
     * @param replace 如果文件存在是否替换
     * @param targetPath 压缩包保存路径
     * @param zipPaths 待压缩文件路径
     * @return 压缩包
     */
    public static File zip(boolean replace, String targetPath, String... zipPaths) {
        File zipFile = null;
        if (targetPath != null && zipPaths != null && zipPaths.length > 0) {
			zipFile = new File(targetPath);
            ZipOutputStream zos = null;           
            try {
                boolean first = true;
                for (String path : zipPaths) {
                    File file = new File(path);
                    if (file.exists()) {
                        //如果已存在同名压缩包
                        if (first && zipFile.exists()) {
                            if (replace) {
                                if (!zipFile.delete()) {
                                    return null;
                                }
                            } else {
                                return zipFile;
                            }                            
                        }
                        if (zos == null) {
                            zos = new ZipOutputStream(new FileOutputStream(zipFile));
                        }
                        addEntry(File.separator, file, zos);
                        first = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(zos);
            }            
        }
        return zipFile;
    }
    
    /**
     * 扫描添加文件Entry
     * @param base 基路径
     * @param source 源文件
     * @param zos Zip文件输出流
     */
    private static void addEntry(String base, File source, ZipOutputStream zos) throws IOException {
        // 按目录分级，形如：/aaa/bbb.txt
        base += source.getName();
        if (source.isDirectory()) {
			File[] files = source.listFiles();
			if (files.length > 0) {
				for (File file : files) {
					// 递归列出目录下的所有文件，添加文件Entry
					addEntry(base + File.separator, file, zos);
				}
			} else {
				zos.putNextEntry(new ZipEntry(base + File.separator));
			}            
        } else {
			BufferedInputStream bis = null;
			try {
				zos.putNextEntry(new ZipEntry(base));
				byte[] buffer = new byte[1024 * 10];
				bis = new BufferedInputStream(new FileInputStream(source), buffer.length);
				int read;
				while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
					zos.write(buffer, 0, read);
				}
				zos.closeEntry();
			} finally {
				IOUtils.closeQuietly(bis);
			}
        }
    }

	/**
     * 解压文件到当前文件夹
     * @param filePath 压缩文件路径
     */
    public static void unZip(String filePath) {
        unZip(filePath, null);
    }

    /**
     * 解压文件到指定文件夹
     * @param sourcePath 压缩文件路径
     * @param targetDir 解压的文件夹
     */
    public static void unZip(String sourcePath, String targetDir) {
        File source = new File(sourcePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            try {
                zis = new ZipInputStream(new FileInputStream(source));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    File target;
                    if (targetDir == null) {
                        target = new File(source.getParent(), entry.getName());
                    } else {
                        target = new File(targetDir, entry.getName());
                    }
                    if (!target.getParentFile().exists()) {
                        // 创建文件父目录
                        target.getParentFile().mkdirs();
                    }
                    if (!entry.isDirectory()) {
                        // 写入文件
                        bos = new BufferedOutputStream(new FileOutputStream(target));
                        int read;
                        byte[] buffer = new byte[1024 * 10];
                        while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                            bos.write(buffer, 0, read);
                        }
                        bos.flush();
                    }
                }
                zis.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(zis, bos);
            }
        }
    }
}