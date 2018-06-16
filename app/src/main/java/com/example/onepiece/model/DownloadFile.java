package com.example.onepiece.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class DownloadFile {

    /**
     * request_type : audio
     * file_id : 109
     */

    @SerializedName("request_type")
    private String requestType;
    @SerializedName("file_id")
    private int fileId;

    public static DownloadFile objectFromData(String str) {

        return new Gson().fromJson(str, DownloadFile.class);
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
}
