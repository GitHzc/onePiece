package com.example.onepiece.model;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class User {
    static private User sUser;
    private String username;

    public static User get() {
        if (sUser == null) {
            sUser = new User("traveler");
        }
        return sUser;
    }

    private User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLogin() {
        return !username.equals("traveler");
    }
}
