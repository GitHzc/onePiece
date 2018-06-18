package com.example.onepiece.model;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class Query {

    /**
     * query : 陈奕迅
     */

    private String query;

    public static Query objectFromData(String str) {

        return new Gson().fromJson(str, Query.class);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
