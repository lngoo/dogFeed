package com.example.demo.earnfood.detail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.Worker;
import com.example.demo.util.RestTemplateUtils;
import com.example.demo.util.ThreadUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * 关注商品
 */
public class FollowGoodWorker extends Worker {

    private static Worker worker = new FollowGoodWorker();

    public static Worker getInstance() {
        return worker;
    }

    @Override
    public void doJob(String cookie, JSONObject obj) {
        int taskChance = obj.getInteger("taskChance");
        if (taskChance > 0) {
            JSONArray array = obj.getJSONArray("followGoodList");
            Iterator<Object> it = array.iterator();
            while (it.hasNext()) {
                JSONObject object = (JSONObject) it.next();
                boolean status = object.getBoolean("status");
                if (status) {
                    continue;
                } else {
                    String sku = object.getString("sku");
                    ThreadUtil.sleepRandomSeconds(6, 8);
                    boolean singleResult = doSingleTask(cookie, sku);
                    if (singleResult) {
                        System.out.println("### [" +new Date().toLocaleString()+ "] FollowGood result = " + singleResult + ", sku ="+sku);
                    }
                }
            }
        }
    }
//
//    private boolean doSingleTask(String cookie, String sku) {
//        try {
//            String url = "https://draw.jdfcloud.com//pet/followGood";
//            Map<String, String> headers = geneHeaders(cookie);
//
//            JSONObject params = new JSONObject();
//            params.put("sku", sku);
//
//            String result = RestTemplateUtils.postHttps(url, params, headers, 60, 60, 5);
//            return result.contains("\"errorCode\":\"success\"");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    private boolean doSingleTask(String cookie, String sku) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("https://draw.jdfcloud.com//pet/followGood")
                    .header("Cookie", cookie)
                    .header("charset", "utf-8")
                    .header("Accept-Encoding", "gzip")
//                    .header("content-type", "application/json")
                    .header("Referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/501/page-frame.html")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; CHM-TL00H Build/HonorCHM-TL00H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 MicroMessenger/7.0.6.1480(0x2700063D) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .header("Connection", "Keep-Alive")
                    .field("sku", sku)
                    .asString();
            String result = response.getBody();
            return result.contains("\"errorCode\":\"success\"");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
