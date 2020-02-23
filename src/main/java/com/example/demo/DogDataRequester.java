package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.RestTemplateUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据请求器
 */
@Service
public class DogDataRequester {

    public String getEnterRoomInfo(String cookie){
        try {
            String url = "https://draw.jdfcloud.com//pet/enterRoom?reqSource=weapp&invitePin=";
            Map<String, String> headers = geneHeaders(cookie);
            return RestTemplateUtils.getHttps(url, headers, 60, 60, 5);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public Integer getRightFeedNum(String cookie, Integer[] foodArrays){
        try {
            String roomInfo = getEnterRoomInfo(cookie);
            JSONObject data = JSONObject.parseObject(roomInfo).getJSONObject("data");
            int petFood = data.getInteger("petFood");
            return calFeedNum(petFood, foodArrays);
        } catch (Exception e) {
            e.printStackTrace();
            return 10;
        }
    }

    /**
     * {"errorCode":"feed_ok","errorMessage":null,"currentTime":1582189921737,"data":null,"success":true}
     * {"errorCode":"level_upgrade",……}
     * @param cookie
     * @param food
     * @return 是否成功
     */
    public boolean feed(String cookie, int food){
        try {
            String url = "https://draw.jdfcloud.com//pet/feed?feedCount="+food;
            Map<String, String> headers = geneHeaders(cookie);
            String json = RestTemplateUtils.getHttps(url, headers, 60, 60, 5);
            JSONObject obj = JSON.parseObject(json);
            String errorCode = obj.getString("errorCode");
            boolean result = (StringUtils.equals("feed_ok", errorCode) || StringUtils.equals("level_upgrade", errorCode));
            if (!result) {
                System.out.println("### failed feed info = " + json);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private Map<String, String> geneHeaders(String cookie) {
        Map<String, String> map = new HashMap<>();
        map.put("cookie", cookie);
        map.put("charset", "utf-8");
        map.put("Accept-Encoding", "gzip");
        map.put("content-type", "application/json");
        map.put("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/494/page-frame.html");
        map.put("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; CHM-TL00H Build/HonorCHM-TL00H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 MicroMessenger/7.0.6.1480(0x2700063D) Process/appbrand1 NetType/WIFI Language/zh_CN");
        map.put("Host", "draw.jdfcloud.com");
        map.put("Connection", "Keep-Alive");
        return map;
    }

    /**
     * 要保存能喂5次
     * @param petFood
     * @param foodArrays
     * @return
     */
    private int calFeedNum(int petFood, Integer[] foodArrays) {
        for (Integer temp : foodArrays){
            if (petFood > temp * 5) {
                return temp;
            }
        }
        return foodArrays[foodArrays.length-1];
    }
}
