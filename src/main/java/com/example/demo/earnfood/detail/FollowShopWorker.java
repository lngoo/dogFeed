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
 * 关注店铺 FollowShop
 */
public class FollowShopWorker extends Worker {

    private static Worker worker = new FollowShopWorker();

    public static Worker getInstance() {
        return worker;
    }

    @Override
    public void doJob(String cookie, JSONObject obj) {
        int taskChance = obj.getInteger("taskChance");
        if (taskChance > 0) {
            JSONArray array = obj.getJSONArray("followShops");
            Iterator<Object> it = array.iterator();
            while (it.hasNext()) {
                JSONObject object = (JSONObject) it.next();
                boolean status = object.getBoolean("status");
                if (status) {
                    continue;
                } else {
                    int shopId = object.getInteger("shopId");
                    ThreadUtil.sleepRandomSeconds(6, 8);
                    boolean singleResult = doSingleTask(cookie, shopId);
                    System.out.println("### [" +new Date().toLocaleString()+ "] FollowShop result = " + singleResult + ", shopID="+shopId);
                }
            }
        }
    }
//
//    private boolean doSingleTask(String cookie, int shopId) {
//        try {
//            String url = "https://draw.jdfcloud.com//pet/followShop";
//            Map<String, String> headers = geneHeaders(cookie);
//
//            JSONObject params = new JSONObject();
//            params.put("shopId", shopId);
//
//            String result = RestTemplateUtils.postHttps(url, params, headers, 60, 60, 5);
//            return result.contains("\"errorCode\":\"success\"");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    private boolean doSingleTask(String cookie, int shopId) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("https://draw.jdfcloud.com//pet/followShop")
                    .header("cookie", cookie)
                    .header("charset", "utf-8")
                    .header("Accept-Encoding", "gzip")
//                    .header("content-type", "application/json")
                    .header("Referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/501/page-frame.html")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; CHM-TL00H Build/HonorCHM-TL00H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 MicroMessenger/7.0.6.1480(0x2700063D) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .header("Connection", "Keep-Alive")
                    .field("shopId", shopId)
                    .asString();
            String result = response.getBody();
            System.out.println(result);
            return result.contains("\"errorCode\":\"success\"");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
