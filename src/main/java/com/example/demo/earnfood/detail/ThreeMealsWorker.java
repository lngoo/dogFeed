package com.example.demo.earnfood.detail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.Worker;
import com.example.demo.util.RestTemplateUtils;
import com.example.demo.util.ThreadUtil;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * 三餐
 */
public class ThreeMealsWorker extends Worker {

    private static Worker worker = new ThreeMealsWorker();

    public static Worker getInstance() {
        return worker;
    }

    @Override
    public void doJob(String cookie, JSONObject obj) {
        int taskChance = obj.getInteger("taskChance");
        if (taskChance > 0) {
            boolean result = doSingleTask(cookie);
            System.out.println("### [" +new Date().toLocaleString()+ "] three meals result = " + result);
        }
    }

    private boolean doSingleTask(String cookie) {
        try {
            String url = "https://draw.jdfcloud.com//pet/getFood?taskType=ThreeMeals";
            Map<String, String> headers = geneHeaders(cookie);
            String result = RestTemplateUtils.getHttps(url, headers, 60, 60, 5);
            return result.contains("\"errorCode\":\"received\"");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
