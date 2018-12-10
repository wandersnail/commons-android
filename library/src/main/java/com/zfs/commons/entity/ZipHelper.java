package com.zfs.commons.entity;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.zfs.commons.interfaces.Callback;
import com.zfs.commons.utils.FileUtils;
import com.zfs.commons.utils.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 描述: 解压缩
 * 时间: 2018/12/10 21:55
 * 作者: zengfansheng
 */
public class ZipHelper {
    private ZipHelper() {
    }

    public static ZipBuilder zip() {
        return new ZipBuilder();
    }

    public static UnzipBuilder unzip() {
        return new UnzipBuilder();
    }

    public static class ZipBuilder {
        private String comment;
        private int method = -1;
        private int level = -1;
        private List<File> files = new ArrayList<>();
        private String targetPath;
        private boolean replace;

        private ZipBuilder() {
        }

        public ZipBuilder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * 压缩类型
         *
         * @param method {@link ZipEntry#STORED}, {@link ZipEntry#DEFLATED}
         */
        public ZipBuilder setMethod(int method) {
            if (method == ZipEntry.STORED || method == ZipEntry.DEFLATED) {
                this.method = method;
            }
            return this;
        }

        /**
         * 压缩级别
         *
         * @param level 0~9
         */
        public ZipBuilder setLevel(int level) {
            if (level > 9) {
                this.level = 9;
            } else {
                this.level = level;
            }
            return this;
        }

        /**
         * 添加待压缩文件
         */
        public ZipBuilder addSourceFile(@NonNull File file) {
            if (file.exists() && !files.contains(file)) {
                files.add(file);
            }
            return this;
        }

        /**
         * 压缩包保存路径
         */
        public ZipBuilder setTargetPath(String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        /**
         * 目标路径下已存在同名压缩包，是否替换
         */
        public ZipBuilder setReplace(boolean replace) {
            this.replace = replace;
            return this;
        }

        /**
         * 执行压缩，同步的
         */
        public File execute() {
            if (files.isEmpty()) {
                return null;
            } else {
                File zipFile;
                if (targetPath == null) {
                    File f = files.get(0);
                    zipFile = new File(f.getParent(), f.getParentFile().getName() + ".zip");
                } else {
                    zipFile = new File(targetPath);
                }
                zipFile.mkdirs();
                ZipOutputStream zos = null;
                try {
                    boolean first = true;
                    for (File file : files) {
                        //如果已存在同名压缩包
                        if (first && zipFile.exists()) {
                            if (replace) {
                                if (!zipFile.delete()) {
                                    return null;
                                }
                            } else {
                                String path = zipFile.getAbsolutePath();
                                String newName = FileUtils.getFileNameWithoutSuffix(path) + System.currentTimeMillis() + FileUtils.getSuffix(path);
                                zipFile = new File(zipFile.getParent(), newName);
                            }
                        }
                        if (zos == null) {
                            zos = new ZipOutputStream(new FileOutputStream(zipFile));
                            if (level > 0) {
                                zos.setLevel(level);
                            }
                            if (!TextUtils.isEmpty(comment)) {
                                zos.setComment(comment);
                            }
                            if (method > 0) {
                                zos.setMethod(method);
                            }
                        }
                        addEntry(File.separator, file, zos);
                        first = false;
                    }
                    return zipFile;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    IOUtils.closeQuietly(zos);
                }
            }
        }

        /**
         * 执行压缩，异步的
         */
        public void execute(final Callback<File> callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = execute();
                    if (callback != null) {
                        callback.onCallback(file);
                    }
                }
            }).start();
        }

        /**
         * 扫描添加文件Entry
         *
         * @param base   基路径
         * @param source 源文件
         * @param zos    Zip文件输出流
         */
        private void addEntry(String base, File source, ZipOutputStream zos) throws IOException {
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
    }

    public static class UnzipBuilder {
        private List<File> zipFiles = new ArrayList<>();
        private String targetDir;

        
        public UnzipBuilder addZipPath(@NonNull File zipFile) {
            if (zipFile.exists() && !zipFiles.contains(zipFile)) {
                zipFiles.add(zipFile);
            }
            return this;
        }

        public UnzipBuilder setTargetDir(String targetDir) {
            this.targetDir = targetDir;
            return this;
        }

        /**
         * 执行解压，同步的
         */
        public boolean execute() {
            if (zipFiles.isEmpty()) {
                return false;
            } else {
                for (File source : zipFiles) {
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
                                if (!target.getParentFile().mkdirs()) {
                                    return false;
                                }
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
                        return false;
                    } finally {
                        IOUtils.closeQuietly(zis, bos);
                    }
                }
                return true;
            }
        }

        /**
         * 执行解压，异步的
         */
        public void execute(final Callback<Boolean> callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean result = execute();
                    if (callback != null) {
                        callback.onCallback(result);
                    }
                }
            }).start();
        }
    }
}
