package com.app.bollyhood.util;

import java.util.HashMap;

public class MyStorage {
    private static MyStorage ourInstance = new MyStorage();
    public HashMap<String, Object> storage = new HashMap<>();

    public static MyStorage getInstance() {
        return ourInstance;
    }

    private MyStorage() {
    }
}
