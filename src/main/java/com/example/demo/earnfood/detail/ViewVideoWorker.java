package com.example.demo.earnfood.detail;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.Worker;
import com.example.demo.util.ThreadUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.util.Date;

/**
 * 视频
 */
public class ViewVideoWorker extends Worker {

    private static Worker worker = new ViewVideoWorker();

    public static Worker getInstance() {
        return worker;
    }

    @Override
    public void doJob(String cookieKey, String cookie, JSONObject obj) {
        int taskChance = obj.getInteger("taskChance");
        Object followCount = obj.get("joinedCount");
        if (null == followCount || taskChance > ((Integer) followCount)) {
            int tempCount = (null == followCount ? 0 : ((Integer) followCount));
            for (; tempCount < taskChance; tempCount++) {
                ThreadUtil.sleepRandomSeconds(16, 20);
                boolean singleResult = doSingleTask(cookieKey, cookie);
                System.out.println("### [cookie=" + cookieKey + "] [" + new Date().toLocaleString() + "] ViewVideo result = " + singleResult);
            }
        }
    }

    private boolean doSingleTask(String cookieKey, String cookie) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("https://draw.jdfcloud.com//pet/scan")
                    .header("charset", "utf-8")
                    .header("cookie", cookie)
                    .header("content-type", "application/json")
                    .header("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/501/page-frame.html")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; CHM-TL00H Build/HonorCHM-TL00H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 MicroMessenger/7.0.6.1480(0x2700063D) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .body("{\"taskType\":\"ViewVideo\",\"reqSource\":\"weapp\"}")
                    .asString();

            String result = response.getBody();
            boolean flag = result.contains("\"success\":true");
            if (!flag) {
                System.out.println("### [cookie=" + cookieKey + "] = " + result);
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
