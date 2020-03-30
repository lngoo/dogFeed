package com.example.demo.earnfood.detail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.Worker;
import com.example.demo.util.ThreadUtil;
import com.example.demo.vo.StealInfo;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.Iterator;

/**
 * 偷粮食和金币
 */
public class StealFoodAndCoinWorker extends Worker {

    private static Worker worker = new StealFoodAndCoinWorker();

    public static Worker getInstance() {
        return worker;
    }

    public void doRealJob(String cookieKey, String cookie, StealInfo stealInfo) {
        // 只做1开头的，其他的标识不做此任务
        if (cookieKey.startsWith("1")) {
            // 初始化积分
            initCoinCount(cookie, stealInfo);

            int pageNum = 5;
            while (!isStealTaskFinished(stealInfo)) {
                // 做一页商品的任务
                boolean isLastPage = doPageFriendsSteal(cookieKey, cookie, stealInfo, pageNum);
                if (isLastPage) {
                    break;
                }
                pageNum++;
            }
            System.out.println("### [cookie=" + cookieKey + "] StealFood finished.");
        }
    }

    private void initCoinCount(String cookie, StealInfo stealInfo) {
        try {
            String url = "https://draw.jdfcloud.com//pet/getCoinChanges?changeDate="+System.currentTimeMillis();
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get(url)
                    .header("charset", "utf-8")
                    .header("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/525/page-frame.html")
                    .header("cookie", cookie)
                    .header("content-type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; OPPO R17 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessenger/7.0.7.1500(0x27000730) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .asString();
            String result = response.getBody();
            boolean flag = result.contains("\"success\":true");
            if (flag) {
                JSONObject obj = JSONObject.parseObject(result);
                JSONArray datas = obj.getJSONArray("datas");
                Iterator it = datas.iterator();
                while (it.hasNext()) {
                    JSONObject tempTypeInfo = (JSONObject) it.next();
                    if (StringUtils.pathEquals("visit_friend", tempTypeInfo.getString("changeEvent"))) {
                        int changeCoin = tempTypeInfo.getInteger("changeCoin");
                        stealInfo.setCoinCount(changeCoin / 5);
                        break;
                    }
                }
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

    /**
     * 任务是否完成
     *
     * @param stealInfo
     * @return
     */
    private boolean isStealTaskFinished(StealInfo stealInfo) {
        return stealInfo.getCoinCount() >= 20 && stealInfo.getFoodCount() >= 20;
    }

    /**
     * @param cookieKey
     * @param cookie
     * @param stealInfo
     * @param pageNum
     * @return 是否是最后一页，如果出错，也认为不继续弄下一页了
     */
    private boolean doPageFriendsSteal(String cookieKey, String cookie, StealInfo stealInfo, int pageNum) {
        JSONObject pageInfo = loadPageInfo(cookieKey, cookie, pageNum);
        if (null == pageInfo) {
            System.out.println("##### [cookie=" + cookieKey + "] StealFood get error when load page data.");
            return true;
        } else {
            System.out.println("### [cookie=" + cookieKey + "] StealFood load one page data.pageNum="+pageNum);
            JSONArray datas = pageInfo.getJSONArray("datas");
            Iterator<Object> it = datas.iterator();
            boolean isTaskFull = false;
            while (it.hasNext()) {
                // 一个朋友
                JSONObject friend = (JSONObject) it.next();
                String stealStatus = friend.getString("stealStatus");
                // 粮食已满
                if (StringUtils.pathEquals("chance_full", stealStatus)) {
                    stealInfo.setFoodCount(20);
                }

                // 偷一个朋友的
                isTaskFull = doOneFriendSteal(friend, cookieKey, cookie, stealInfo);
                if (isTaskFull) {
                    break;
                }
                ThreadUtil.sleepRandomSeconds(1, 5);
            }
            return isTaskFull || datas.size() < 20;
        }
    }

    /**
     * 偷一个朋友的粮食
     *
     * @param friend
     * @param cookieKey
     * @param cookie
     * @param stealInfo
     * @return 是否粮食偷满了
     */
    private boolean doOneFriendSteal(JSONObject friend, String cookieKey, String cookie, StealInfo stealInfo) {
        try {
            String friendPin = URLEncoder.encode(friend.getString("friendPin"));
            JSONObject friendHomeInfo = enterFriendHome(friendPin, cookie);
            if (null != friendHomeInfo) {
                JSONObject data = friendHomeInfo.getJSONObject("data");
                Object stealFoodObj = data.get("stealFood");
                int stealFood = null == stealFoodObj ? 0 : (int)stealFoodObj;
                Object friendHomeCoinObj = data.get("friendHomeCoin");
                int friendHomeCoin =  null == friendHomeCoinObj ? 0 : (int)friendHomeCoinObj;
                System.out.println("### [cookie=" + cookieKey + "] StealFood food/coin num =" + stealFood + "/" + friendHomeCoin);
                if (stealFood == 3) {
                    boolean result = doStealFood(cookie, friendPin);
                    if (result) {
                        System.out.println("### [cookie=" + cookieKey + "] StealFood : steal one food.");
                        stealInfo.addFoodCount();
                    }
                }
                if (friendHomeCoin == 5) {
                    boolean result = doStealCoin(cookie, friendPin);
                    if (result) {
                        System.out.println("### [cookie=" + cookieKey + "] StealFood : steal one coin.");
                        stealInfo.addCoinCount();
                    }
                }
                return isStealTaskFinished(stealInfo);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean doStealCoin(String cookie, String friendPin) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get("https://draw.jdfcloud.com//pet/getFriendCoin?friendPin="+friendPin)
                    .header("charset", "utf-8")
                    .header("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/525/page-frame.html")
                    .header("cookie", cookie)
                    .header("app-id", "wxccb5c536b0ecd1bf")
                    .header("lottery-access-signature", "wxccb5c536b0ecd1bf1537237540544h79HlfU")
                    .header("openid", "oPcgJ4wtzmHoy6vY1DgkkBDy6xik")
                    .header("content-type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; OPPO R17 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessenger/7.0.7.1500(0x27000730) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .asString();
            String result = response.getBody();
            System.out.println(result);
            return !result.contains("\"errorCode\": \"coin_took_fail\"");
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean doStealFood(String cookie, String friendPin) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get("https://draw.jdfcloud.com//pet/doubleRandomFood?friendPin=" + friendPin)
                    .header("charset", "utf-8")
                    .header("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/525/page-frame.html")
                    .header("cookie", cookie)
                    .header("app-id", "wxccb5c536b0ecd1bf")
                    .header("lottery-access-signature", "wxccb5c536b0ecd1bf1537237540544h79HlfU")
                    .header("openid", "oPcgJ4wtzmHoy6vY1DgkkBDy6xik")
                    .header("content-type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; OPPO R17 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessenger/7.0.7.1500(0x27000730) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .asString();

            ThreadUtil.sleepRandomSeconds(2, 4);

            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response1 = Unirest.get("https://draw.jdfcloud.com//pet/getRandomFood?friendPin=" + friendPin)
                    .header("charset", "utf-8")
                    .header("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/525/page-frame.html")
                    .header("cookie", cookie)
                    .header("app-id", "wxccb5c536b0ecd1bf")
                    .header("lottery-access-signature", "wxccb5c536b0ecd1bf1537237540544h79HlfU")
                    .header("openid", "oPcgJ4wtzmHoy6vY1DgkkBDy6xik")
                    .header("content-type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; OPPO R17 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessenger/7.0.7.1500(0x27000730) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .asString();
            String result = response1.getBody();
            System.out.println(result);
            return result.contains("\"errorCode\": \"steal_ok\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private JSONObject enterFriendHome(String friendPin, String cookie) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get("https://draw.jdfcloud.com//pet/enterFriendRoom?friendPin=" + friendPin)
                    .header("charset", "utf-8")
                    .header("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/525/page-frame.html")
                    .header("cookie", cookie)
                    .header("app-id", "wxccb5c536b0ecd1bf")
                    .header("lottery-access-signature", "wxccb5c536b0ecd1bf1537237540544h79HlfU")
                    .header("openid", "oPcgJ4wtzmHoy6vY1DgkkBDy6xik")
                    .header("content-type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; OPPO R17 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessenger/7.0.7.1500(0x27000730) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .asString();
            String result = response.getBody();
            boolean flag = result.contains("\"success\":true");
            if (flag) {
                return JSONObject.parseObject(result);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载单页信息
     *
     * @param cookieKey
     * @param cookie
     * @param pageNum
     * @return
     */
    private JSONObject loadPageInfo(String cookieKey, String cookie, int pageNum) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get("https://draw.jdfcloud.com//pet/getFriends?itemsPerPage=20&currentPage=" + pageNum)
                    .header("charset", "utf-8")
                    .header("referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/525/page-frame.html")
                    .header("cookie", cookie)
                    .header("app-id", "wxccb5c536b0ecd1bf")
                    .header("lottery-access-signature", "wxccb5c536b0ecd1bf1537237540544h79HlfU")
                    .header("openid", "oPcgJ4wtzmHoy6vY1DgkkBDy6xik")
                    .header("content-type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; OPPO R17 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.136 Mobile Safari/537.36 MicroMessenger/7.0.7.1500(0x27000730) Process/appbrand0 NetType/WIFI Language/zh_CN")
                    .header("Host", "draw.jdfcloud.com")
                    .asString();

            String result = response.getBody();
            boolean flag = result.contains("\"success\":true");
            if (flag) {
                return JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法不用，没有被调用
     */
    @Override
    public void doJob(String cookieKey, String cookie, JSONObject obj) {
    }
}
