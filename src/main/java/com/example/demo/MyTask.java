package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.Worker;
import com.example.demo.earnfood.WorkerFactory;
import com.example.demo.earnfood.detail.StealFoodAndCoinWorker;
import com.example.demo.util.ThreadUtil;
import com.example.demo.vo.StealInfo;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;

public class MyTask implements Callable<Boolean> {
    // 能喂养的几个数字
    private final Integer[] foodArrays = new Integer[]{80, 40, 20, 10};
    // 喂养间隔 3小时+200毫秒
    private final long periodMillSeconds = 3 * 60 * 60 * 1000 + 200;

    private String cookieKey;
    private String cookie;
    private DogDataRequester requester;
    private Map<String, StealInfo> stealCounter;

    public MyTask(String key, String cookie, DogDataRequester requester, Map<String, StealInfo> stealCounter) {
        this.cookieKey = key;
        this.cookie = cookie;
        this.requester = requester;
        this.stealCounter = stealCounter;
    }

    @Override
    public Boolean call() throws Exception {
        String roomInfo = requester.getEnterRoomInfo(cookie);
        if (StringUtils.isEmpty(roomInfo)) {
            System.out.println("### [cookie=" + cookieKey + "] error when get room info...feed directly..");
            requester.feed(cookie, 10);
        } else {
            try {
                JSONObject data = JSONObject.parseObject(roomInfo).getJSONObject("data");
                long lastFeedTime = data.getLong("lastFeedTime");
                long remainMillSeconds = periodMillSeconds - (System.currentTimeMillis() - lastFeedTime);
                if (remainMillSeconds < 0) {
                    remainMillSeconds = 0;
                }
                System.out.println("### [cookie=" + cookieKey + "] try start timer. delayMillSeconds=" + remainMillSeconds + ". time = " + new Date().toLocaleString());

                Timer timer = new Timer(true);    //treu就是守护线程
                //开始执行任务,第一个参数是任务,第二个是延迟时间,第三个是每隔多长时间执行一次
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // 喂狗
                        feedDog();
                        // 做任务
                        earnFoodTask();
                    }
                }, remainMillSeconds, periodMillSeconds);
//                }, 1, periodMillSeconds);
            } catch (Exception e) {
                System.out.println("### [cookie=" + cookieKey + "] error when parse room info...feed directly..");
                requester.feed(cookie, 10);
            }
        }
        return true;
    }

    private void earnFoodTask() {
        int hour = new Date().getHours();
        // 7-22点做任务
        if (hour > 6 && hour < 23) {
            // 通用任务
            commonEarnTask();
            // 特殊桌面任务
            deskEarnTask();
            // 偷粮食
            stealFoodAndCoin();
        } else {
            // 清空计数器
            Iterator<StealInfo> it = stealCounter.values().iterator();
            while (it.hasNext()) {
                StealInfo info = it.next();
                info.clear();
            }
        }
    }

    private void stealFoodAndCoin() {
        StealFoodAndCoinWorker worker = (StealFoodAndCoinWorker) StealFoodAndCoinWorker.getInstance();
        worker.doRealJob(cookieKey, cookie, stealCounter.get(cookieKey));
        ThreadUtil.sleepRandomSeconds(3, 5);
    }

    private void deskEarnTask() {
        try {
            String resp = requester.getDeskTaskList(cookie);
            if (null != resp) {
                JSONObject obj = JSON.parseObject(resp);
                String errorCode = obj.getString("errorCode");
                boolean result = (null == errorCode || StringUtils.pathEquals("null", errorCode));
                if (!result) {
                    System.out.println("### [cookie=" + cookieKey + "] failed get desk goods list = " + resp);
                    return;
                }

                JSONObject data = obj.getJSONObject("data");
                int taskChance = data.getInteger("taskChance");
                Object followCount = data.get("followCount");
                if (null == followCount || ((Integer) followCount) < taskChance) {
                    JSONArray array = data.getJSONArray("deskGoods");
                    Iterator<Object> it = array.iterator();
                    int tempCount = (null == followCount ? 0 : (Integer) followCount);
                    while (it.hasNext()) {
                        if (tempCount == taskChance) {
                            break;
                        }
                        JSONObject goodInfo = (JSONObject) it.next();
                        boolean status = goodInfo.getBoolean("status");
                        // 已经做过就不做了
                        if (status) {
                            continue;
                        }
                        Worker worker = WorkerFactory.getWorkerByType("deskGoods");
                        worker.doJob(cookieKey, cookie, goodInfo);
                        ThreadUtil.sleepRandomSeconds(3, 5);
                        tempCount++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否做全部任务，false时只做三餐任务
     *
     */
    private void commonEarnTask() {
        String resp = requester.getAllTask(cookie);
        if (null != resp) {
            JSONObject obj = JSON.parseObject(resp);
            String errorCode = obj.getString("errorCode");
            boolean result = (null == errorCode || StringUtils.pathEquals("null", errorCode));
            if (!result) {
                System.out.println("### [cookie=" + cookieKey + "] failed getAllTask = " + resp);
                return;
            }

            JSONArray array = obj.getJSONArray("datas");
            Iterator<Object> it = array.iterator();
            while (it.hasNext()) {
                JSONObject taskCategory = (JSONObject) it.next();
                String taskType = taskCategory.getString("taskType");
                Worker worker = WorkerFactory.getWorkerByType(taskType);
                worker.doJob(cookieKey, cookie, taskCategory);
                // 每一种之间间隔时间
                ThreadUtil.sleepRandomSeconds(3, 5);
            }
        }
    }

    private void feedDog() {
        Integer feedNum = requester.getRightFeedNum(cookie, foodArrays);
        boolean feedResult = false;
        for (int i = 0; i < 3; i++) {
            feedResult = requester.feed(cookie, feedNum);
            System.out.println("### [cookie=" + cookieKey + "] finished feed dog. result = " + feedResult + ", food num = " + feedNum +
                    ". time = " + new Date().toLocaleString());
            if (feedResult) {
                break;
            } else {
                ThreadUtil.sleepSeconds(5);
            }
        }
    }
}
