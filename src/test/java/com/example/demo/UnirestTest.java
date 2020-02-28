package com.example.demo;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @创建时间： 2020/2/27 21:36
 * @类说明：请填写
 * @修改记录：
 */
public class UnirestTest {

    public static void main(String[] args) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("https://draw.jdfcloud.com//pet/followShop")
//                    .header("cookie", "pt_key=AAJeOj0hADAjFj4EcGOubyjtvguEtk8yNNCTqq78eaAP4gstGfcIEgWK80ElhZCEXYREEUWqTWc")
                    .header("cookie", "pt_key=AAJeH3ONADBp11cof76ZapZjzQeqB7g57JX9kzJ0xmvgsbNiMZ17FIvaimSe-dVB2CBa_ZwHrW8")
                    .header("charset", "utf-8")
                    .header("Accept-Encoding", "gzip")
//                    .header("content-type", "application/json")
                    .header("Referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/501/page-frame.html")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.2; CHM-TL00H Build/HonorCHM-TL00H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 MicroMessenger/7.0.6.1480(0x2700063D) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .header("Connection", "Keep-Alive")
                    .field("shopId", "761472")
                    .asString();
            System.out.println(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
