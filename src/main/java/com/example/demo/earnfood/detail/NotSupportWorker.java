package com.example.demo.earnfood.detail;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.Worker;

/**
 * 不支持
 */
public class NotSupportWorker extends Worker {

    private static Worker worker = new NotSupportWorker();

    public static Worker getInstance() {
        return worker;
    }

    @Override
    public void doJob(String cookieKey, String cookie, JSONObject obj) {

    }
}
