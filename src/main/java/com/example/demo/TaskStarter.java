package com.example.demo;

import com.example.demo.vo.StealInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskStarter implements ApplicationRunner {
    private static Map<String, String> cookieMap = new HashMap<>();
    private static Map<String, StealInfo> stealCounter = new ConcurrentHashMap<>();
    static {
        cookieMap.put("181", "pt_key=AAJer4inADBQ_dspgJqn95Xvcu17B3nsNCIAY4fLiGGHOjFf4qr05A_CU5kd2F1h2i9orEtY6lw");
        cookieMap.put("o152", "pt_key=AAJe2J5PADCm0gKrF_7xLmT8DYxCkXQw9HwYqVxYhX2HDPnubO9n_cJgUyhYLgkoqrrpLCY0pLA");
        cookieMap.put("171", "pt_key=AAJe2KtrADAt3K_jVe8TD7nciPrFyw5Yq4ExgU9uzS8ielkbmFmc-FFf9lHDDDAO6wXEDfahrh0");
//        cookieMap.put("xlyz", "pt_key=AAJeWNCsADDIfJF76rnm8t1hWnArZi1sPNgf_spKO-m8Ya3abGJHuH9zBXZorOi1HRJXeyn5RDo");
        cookieMap.put("xjl", "pt_key=AAJefHCCADBM5zQ6CBThaPgrkhMqirvcMo92b5H2PBHrCxMsf_OM-2kQWVhhxJmFrp4oiyNLxz8");
        cookieMap.put("xqq", "pt_key=AAJefHlTADAjWH6GFrXKgpQx46v9Fu42khjkZqOM8T9KUjrtaKS_4ZyN2DBSBlKtrW0SCqB9YAw");
    }

    @Autowired
    DogDataRequester requester;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ExecutorService service = Executors.newScheduledThreadPool(cookieMap.size());
        for (String key : cookieMap.keySet()) {
            stealCounter.put(key, new StealInfo());
            service.submit(new MyTask(key, cookieMap.get(key), requester, stealCounter));
        }
    }
}
