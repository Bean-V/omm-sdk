package com.jun.baselibrary.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/8-16:30.
 * Version 1.0
 * Description: 磁盘缓存类，支持 Bitmap、可序列化对象、JSON 文本
 */
public class UniversalDiskCache {
    private final File cacheDir;
    private static final long MAX_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int MAX_COUNT = 200;

    private final ReentrantLock ioLock = new ReentrantLock();

    public UniversalDiskCache(Context context, String uniqueName) {
        cacheDir = new File(context.getCacheDir(), uniqueName);
        if (!cacheDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdirs();
        }
    }

    // ========================= 写入 =========================

    public void putBitmap(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) return;
        String filename = hashKeyForDisk(key);
        File file = new File(cacheDir, filename + ".img");

        ioLock.lock();
        try {
            File tmp = new File(file.getAbsolutePath() + ".tmp");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmp);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
                closeQuietly(fos);
            }
            replaceAtomically(tmp, file);
            trimCacheLocked();
        } finally {
            ioLock.unlock();
        }
    }

    public void putBitmap(String key, Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        if (key == null || bitmap == null || format == null) return;
        String filename = hashKeyForDisk(key);
        File file = new File(cacheDir, filename + ".img");

        ioLock.lock();
        try {
            File tmp = new File(file.getAbsolutePath() + ".tmp");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmp);
                bitmap.compress(format, quality, fos);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
                closeQuietly(fos);
            }
            replaceAtomically(tmp, file);
            trimCacheLocked();
        } finally {
            ioLock.unlock();
        }
    }

    public void putObject(String key, Serializable object) {
        if (key == null || object == null) return;
        String filename = hashKeyForDisk(key);
        File file = new File(cacheDir, filename + ".obj");

        ioLock.lock();
        try {
            File tmp = new File(file.getAbsolutePath() + ".tmp");
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(new FileOutputStream(tmp));
                oos.writeObject(object);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
                closeQuietly(oos);
            }
            replaceAtomically(tmp, file);
            trimCacheLocked();
        } finally {
            ioLock.unlock();
        }
    }

    public void putJson(String key, String json) {
        if (key == null || json == null) return;
        String filename = hashKeyForDisk(key);
        File file = new File(cacheDir, filename + ".json");

        ioLock.lock();
        try {
            File tmp = new File(file.getAbsolutePath() + ".tmp");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmp);
                fos.write(json.getBytes(StandardCharsets.UTF_8));
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
                closeQuietly(fos);
            }
            replaceAtomically(tmp, file);
            trimCacheLocked();
        } finally {
            ioLock.unlock();
        }
    }

    // ========================= 读取 =========================

    public Bitmap getBitmap(String key) {
        if (key == null) return null;
        String filename = hashKeyForDisk(key);
        File file = new File(cacheDir, filename + ".img");

        ioLock.lock();
        try {
            if (!file.exists()) return null;
            file.setLastModified(System.currentTimeMillis());
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } finally {
            ioLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getObject(String key) {
        if (key == null) return null;
        String filename = hashKeyForDisk(key);
        File file = new File(cacheDir, filename + ".obj");

        ioLock.lock();
        try {
            if (!file.exists()) return null;
            file.setLastModified(System.currentTimeMillis());

            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                return (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            } finally {
                closeQuietly(ois);
            }
        } finally {
            ioLock.unlock();
        }
    }

    public String getJson(String key) {
        if (key == null) return null;
        String filename = hashKeyForDisk(key);
        File file = new File(cacheDir, filename + ".json");

        ioLock.lock();
        try {
            if (!file.exists()) return null;
            file.setLastModified(System.currentTimeMillis());

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                int read = fis.read(data);
                if (read <= 0) return null;
                return new String(data, 0, read, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                closeQuietly(fis);
            }
        } finally {
            ioLock.unlock();
        }
    }

    // ========================= 工具 =========================

    public boolean contains(String key) {
        if (key == null) return false;
        String filename = hashKeyForDisk(key);
        File fileImg = new File(cacheDir, filename + ".img");
        File fileObj = new File(cacheDir, filename + ".obj");
        File fileJson = new File(cacheDir, filename + ".json");
        return fileImg.exists() || fileObj.exists() || fileJson.exists();
    }

    public boolean remove(String key) {
        if (key == null) return false;
        String filename = hashKeyForDisk(key);
        boolean deleted = false;

        ioLock.lock();
        try {
            deleted |= new File(cacheDir, filename + ".img").delete();
            deleted |= new File(cacheDir, filename + ".obj").delete();
            deleted |= new File(cacheDir, filename + ".json").delete();
            return deleted;
        } finally {
            ioLock.unlock();
        }
    }

    public void clear() {
        ioLock.lock();
        try {
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                }
            }
        } finally {
            ioLock.unlock();
        }
    }

    public long getTotalSize() {
        long total = 0;
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File f : files) total += f.length();
        }
        return total;
    }

    public int getTotalCount() {
        File[] files = cacheDir.listFiles();
        return files == null ? 0 : files.length;
    }

    private void trimCacheLocked() {
        File[] files = cacheDir.listFiles();
        if (files == null || files.length == 0) return;

        // 最旧在前
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                long l1 = f1.lastModified();
                long l2 = f2.lastModified();
                if (l1 < l2) return -1;
                if (l1 > l2) return 1;
                return 0;
            }
        });

        long totalSize = 0;
        for (File file : files) totalSize += file.length();
        int remainingCount = files.length;
        int index = 0;

        while ((totalSize > MAX_SIZE || remainingCount > MAX_COUNT) && index < files.length) {
            File victim = files[index++];
            long len = victim.length();
            if (victim.delete()) {
                totalSize -= len;
                remainingCount--;
            }
        }
    }

    private void replaceAtomically(File tmp, File target) {
        if (!tmp.renameTo(target)) {
            //noinspection ResultOfMethodCallIgnored
            target.delete();
            //noinspection ResultOfMethodCallIgnored
            tmp.renameTo(target);
        }
        //noinspection ResultOfMethodCallIgnored
        target.setLastModified(System.currentTimeMillis());
    }

    private void closeQuietly(Closeable c) {
        if (c != null) {
            try { c.close(); } catch (IOException ignore) {}
        }
    }

    private String hashKeyForDisk(String key) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes(StandardCharsets.UTF_8));
            return bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(key.hashCode());
        }
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}