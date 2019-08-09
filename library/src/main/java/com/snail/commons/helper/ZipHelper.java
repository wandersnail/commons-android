package com.snail.commons.helper;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.snail.commons.interfaces.Callback;
import com.snail.commons.util.FileUtils;
import com.snail.commons.util.IOUtils;
import com.snail.commons.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * date: 2019/8/8 09:57
 * author: zengfansheng
 */
public class ZipHelper {
    public static class ZipExecutor {
        private String comment;
        private int method = -1;
        private int level = -1;
        private List<File> files = new ArrayList<>();
        private String targetDir;
        private String targetName;
        private boolean replace;
        private Handler handler;

        ZipExecutor() {
            handler = new Handler(Looper.getMainLooper());
        }

        public ZipExecutor setComment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * 压缩类型
         *
         * @param method [ZipEntry.STORED], [ZipEntry.DEFLATED]
         */
        public ZipExecutor setMethod(int method) {
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
        public ZipExecutor setLevel(int level) {
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
        public ZipExecutor addSourceFile(@NonNull File file) {
            Objects.requireNonNull(file, "file is null, cannot be added to zip");
            if (file.exists() && !files.contains(file)) {
                files.add(file);
            }
            return this;
        }

        /**
         * 添加待压缩文件
         */
        public ZipExecutor addSourceFiles(@NonNull List<File> files) {
            Objects.requireNonNull(files, "files is null, cannot be added to zip");
            if (files.size() > 0) {
                for (File file : files) {
                    addSourceFile(file);
                }
            }
            return this;
        }

        /**
         * 压缩包保存路径
         *
         * @param dir      保存目录
         * @param filename 保存的文件名，不含后缀
         */
        public ZipExecutor setTarget(@NonNull String dir, @NonNull String filename) {
            targetDir = dir;
            targetName = filename;
            return this;
        }

        /**
         * 目标路径下已存在同名压缩包，是否替换
         */
        public ZipExecutor setReplace(boolean replace) {
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
                File f = files.get(0);
                if (targetDir == null) {
                    zipFile = new File(f.getParent(), (targetName == null ? f.getParentFile().getName() : targetName) + ".zip");
                } else {
                    zipFile = new File(targetDir, (targetName == null ? f.getParentFile().getName() : targetName) + ".zip");
                }
                File zipParentFile = zipFile.getParentFile();
                if (!zipParentFile.exists()) {
                    zipParentFile.mkdirs();
                }
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
                                String fileName = FileUtils.getFileName(path, true);
                                String newName = fileName + "_" + StringUtils.randomUuid() + FileUtils.getSuffix(path);
                                zipFile = new File(zipFile.getParentFile(), newName);
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
                        addEntry("", file, zos);
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
                    final File file = execute();
                    if (callback != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCallback(file);
                            }
                        });
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
            String basePath = base;
            // 按目录分级，形如：/aaa/bbb.txt
            basePath += source.getName();
            if (source.isDirectory()) {
                File[] files = source.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        // 递归列出目录下的所有文件，添加文件Entry
                        addEntry(basePath + "/", file, zos);
                    }
                } else {
                    zos.putNextEntry(new ZipEntry(basePath + "/"));
                }
            } else {
                BufferedInputStream bis = null;
                try {
                    zos.putNextEntry(new ZipEntry(basePath));
                    byte[] buffer = new byte[40960];
                    bis = new BufferedInputStream(new FileInputStream(source));
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                } finally {
                    IOUtils.closeQuietly(bis);
                }
            }
        }
    }

    public static class UnzipExecutor {
        private List<File> zipFiles = new ArrayList<>();
        private String targetDir;
        private Handler handler;

        UnzipExecutor() {
            handler = new Handler(Looper.getMainLooper());
        }

        public UnzipExecutor addZipFile(@NonNull File zipFile) {
            Objects.requireNonNull(zipFile, "zipFile is null, cannot unzip");
            if (zipFile.exists() && !zipFiles.contains(zipFile)) {
                zipFiles.add(zipFile);
            }
            return this;
        }

        public UnzipExecutor addZipFiles(@NonNull List<File> zipFiles) {
            Objects.requireNonNull(zipFiles, "zipFiles is null, cannot unzip");
            if (zipFiles.size() > 0) {
                for (File file : zipFiles) {
                    addZipFile(file);
                }
            }
            return this;
        }

        public UnzipExecutor setTargetDir(@NonNull String targetDir) {
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
                        ZipEntry entry = zis.getNextEntry();
                        while (entry != null) {
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
                                byte[] buffer = new byte[40960];
                                int len;
                                while ((len = zis.read(buffer)) != -1) {
                                    bos.write(buffer, 0, len);
                                }
                                bos.flush();
                            }
                            entry = zis.getNextEntry();
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
                    final boolean result = execute();
                    if (callback != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCallback(result);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public static ZipExecutor zip() {
        return new ZipExecutor();
    }

    public static UnzipExecutor unzip() {
        return new UnzipExecutor();
    }
}
