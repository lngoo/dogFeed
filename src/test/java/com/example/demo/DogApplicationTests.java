package com.example.demo;

import com.example.demo.util.RestTemplateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DogApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void enterRoom() {
        String url = "https://draw.jdfcloud.com//pet/enterRoom?reqSource=weapp&invitePin=";
        String cookie = "pt_key=AAJeH3ONADBp11cof76ZapZjzQeqB7g57JX9kzJ0xmvgsbNiMZ17FIvaimSe-dVB2CBa_ZwHrW8";
        Map<String, String> headers = geneHeaders(cookie);
        String json = RestTemplateUtils.getHttps(url, headers, 60, 60, 5);
        System.out.println(json);
    }

    @Test
    public void feed() {
        String url = "https://draw.jdfcloud.com//pet/feed?feedCount=80";
        String cookie = "pt_key=AAJeH3ONADBp11cof76ZapZjzQeqB7g57JX9kzJ0xmvgsbNiMZ17FIvaimSe-dVB2CBa_ZwHrW8";
        Map<String, String> headers = geneHeaders(cookie);
        String json = RestTemplateUtils.getHttps(url, headers, 60, 60, 5);
        System.out.println(json);
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
}
