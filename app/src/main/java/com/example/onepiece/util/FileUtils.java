package com.example.onepiece.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
}

