package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.Worker;
import com.example.demo.earnfood.WorkerFactory;
import com.example.demo.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class MyTask implements ApplicationRunner {

    // 能喂养的几个数字
    private final Integer[] foodArrays = new Integer[]{80, 40, 20, 10};
    private final String cookie = "pt_key=AAJeH3ONADBp11cof76ZapZjzQeqB7g57JX9kzJ0xmvgsbNiMZ17FIvaimSe-dVB2CBa_ZwHrW8";
    // 喂养间隔 3小时+200毫秒
    private final long periodMillSeconds = 3 * 60 * 60 * 1000 + 200;

    @Autowired
    DogDataRequester requester;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String roomInfo = requester.getEnterRoomInfo(cookie);
        if (StringUtils.isEmpty(roomInfo)) {
            System.out.println("### error when get room info...feed directly..");
            requester.feed(cookie, 10);
        } else {
            try {
                JSONObject data = JSONObject.parseObject(roomInfo).getJSONObject("data");
                long lastFeedTime = data.getLong("lastFeedTime");
                long remainMillSeconds = periodMillSeconds - (System.currentTimeMillis() - lastFeedTime);
                System.out.println("###### try start timer. delayMillSeconds=" + remainMillSeconds + ". time = " + new Date().toLocaleString());

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
                System.out.println("### error when parse room info...feed directly..");
                requester.feed(cookie, 10);
            }
        }
    }

    private void earnFoodTask() {
        int hour = new Date().getHours();
        // 8-24点做任务
        if (hour > 7) {
            String resp = requester.getAllTask(cookie);
            if (null != resp) {
                JSONObject obj = JSON.parseObject(resp);
                String errorCode = obj.getString("errorCode");
                boolean result = (null == errorCode || StringUtils.pathEquals("null", errorCode));
                if (!result) {
                    System.out.println("### failed getAllTask = " + resp);
                    return;
                }

                JSONArray array = obj.getJSONArray("datas");
                Iterator<Object> it = array.iterator();
                while (it.hasNext()) {
                    JSONObject taskCategory = (JSONObject) it.next();
                    String taskType = taskCategory.getString("taskType");
                    Worker worker = WorkerFactory.getWorkerByType(taskType);
                    worker.doJob(cookie, taskCategory);
                    ThreadUtil.sleepRandomSeconds(3, 5);
                }
            }
        }
    }

    private void feedDog() {
        Integer feedNum = requester.getRightFeedNum(cookie, foodArrays);
        boolean feedResult = false;
        for (int i = 0; i < 5; i++) {
            feedResult = requester.feed(cookie, feedNum);
            System.out.println("### finished feed dog. result = " + feedResult + ", food num = " + feedNum +
                    ". time = " + new Date().toLocaleString());
            if (feedResult) {
                break;
            } else {
                ThreadUtil.sleepSeconds(5);
            }
        }
    }
}
