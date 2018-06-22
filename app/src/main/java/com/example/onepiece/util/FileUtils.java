package com.example.onepiece.util;

import android.content.Context;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;

import com.example.onepiece.model.DiscoveryBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import okhttp3.ResponseBody;

import static android.os.Environment.DIRECTORY_MUSIC;

/**
 * Created by Administrator on 2018/5/14 0014.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static final int FLAG_SUCCESS = 1;   // 创建成功
    public static final int FLAG_EXISTS = 2;    // 已存在
    public static final int FLAG_FAILED = 3;    // 创建失败

    public static int CreateFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Log.e(TAG, "The file [" + filePath + "] has already exists");
            return FLAG_EXISTS;
        }

        if (filePath.endsWith(File.separator)) {
            Log.e(TAG, "The file [" + filePath + "] can not be a directory");
            return FLAG_FAILED;
        }

        if (!file.getParentFile().exists()) {
            Log.d(TAG, "creating parent directory...");
            if (!file.getParentFile().mkdirs()) {
                Log.e(TAG, "created parent directory failed.");
                return FLAG_FAILED;
            }
        }

        try {
            if (file.createNewFile()) {
                Log.i(TAG, "create file [" + filePath + "] success");
                return FLAG_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "create file [" + filePath + "] failed");
            return FLAG_FAILED;
        }

        return FLAG_FAILED;
    }

    public static int createDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            Log.w(TAG, "The directory [" + dirPath + "] has already exists");
            return FLAG_SUCCESS;
        }

        if (dir.mkdirs()) {
            Log.d(TAG, "create directory [" + dirPath + "] success");
            return FLAG_SUCCESS;
        }

        Log.e(TAG, "create directory [" + dirPath + "] failed");
        return FLAG_FAILED;
    }

    public static boolean writeResponseBodyToFile(String fileName, ResponseBody body) {
        try {
            File file = new File(fileName);
            createDir(file.getParent());
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSizeDownloaded = 0;
                long fileSize = body.contentLength();
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 保存对象
     *
     * @param ser 要保存的序列化对象
     * @param file 保存在本地的文件名
     * @throws IOException
     */
    public static boolean saveSerializableObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取对象
     *
     * @param file 保存在本地的文件名
     * @return
     * @throws IOException
     */
    public static Serializable readSerializableObject(String file) {
        File f = new File(file);
        if (!f.exists()) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof InvalidClassException) {
                f.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {}

            try {
                fis.close();
            } catch (Exception e) {}
        }
        return null;
    }

    public static boolean saveParcelableObject(List<DiscoveryBean> discoveryBeans, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(discoveryBeans);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<DiscoveryBean> readParcelableObject(String file) {
        File f = new File(file);
        if (!f.exists()) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            return (List<DiscoveryBean>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(DebugMessage.TAG, "readParcelableObject: " + e.toString());
            e.printStackTrace();
            if (e instanceof InvalidClassException) {
                f.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {}

            try {
                fis.close();
            } catch (Exception e) {}
        }
        return null;
    }

    public static void clearCache(Context context) {
        deleteDir(context.getExternalCacheDir());
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir == null || dir.isDirectory()) {
            return true;
        }
        return dir.delete();
    }

    public static String getMusicDirectory() {
        return Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath() + File.separator;
    }

    public static String getCacheDirectory(Context context) {
        return context.getExternalCacheDir().getAbsolutePath() + File.separator;
    }

    public static String getLyricDirectory() {
        return getMusicDirectory() + "lyric" + File.separator;
    }

    public static String getOneCacheDirectory(Context context) {
        return getCacheDirectory(context) + "one" + File.separator;
    }

    public static String getOnePictureCacheDirectory(Context context) {
        return getOneCacheDirectory(context)  + "picture" + File.separator;
    }

    public static String getDiscoveryCacheDirectory(Context context) {
        return getCacheDirectory(context) + "discovery" + File.separator;
    }
}

