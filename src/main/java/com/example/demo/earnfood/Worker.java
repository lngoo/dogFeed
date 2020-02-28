package com.example.demo.earnfood;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Worker {

    public abstract void doJob(String cookieKey, String cookie, JSONObject obj);

    protected Map<String, String> geneHeaders(String cookie) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie);
        map.put("charset", "utf-8");
        map.put("Accept-Encoding", "gzip");
        map.put("content-type", "application/json");
        map.put("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/501/page-frame.html");
        map.put("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; CHM-TL00H Build/HonorCHM-TL00H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 MicroMessenger/7.0.6.1480(0x2700063D) Process/appbrand1 NetType/WIFI Language/zh_CN");
        map.put("Host", "draw.jdfcloud.com");
        map.put("Connection", "Keep-Alive");
        return map;
    }
}
