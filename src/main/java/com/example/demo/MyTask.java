package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
                System.out.println("###### try start timer. delayMillSeconds=" + remainMillSeconds);

                Timer timer = new Timer(true);    //treu就是守护线程
                //开始执行任务,第一个参数是任务,第二个是延迟时间,第三个是每隔多长时间执行一次
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Integer feedNum = requester.getRightFeedNum(cookie, foodArrays);
                        boolean bl = requester.feed(cookie, feedNum);
                        System.out.println("### finished feed dog. result = " + bl + ", food num = " + feedNum);
                    }
                }, remainMillSeconds, periodMillSeconds);
            } catch (Exception e) {
                System.out.println("### error when parse room info...feed directly..");
                requester.feed(cookie, 10);
            }
        }
    }
}
