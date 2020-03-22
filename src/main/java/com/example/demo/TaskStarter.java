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
        cookieMap.put("181", "pt_key=AAJeH3ONADBp11cof76ZapZjzQeqB7g57JX9kzJ0xmvgsbNiMZ17FIvaimSe-dVB2CBa_ZwHrW8");
        cookieMap.put("152", "pt_key=AAJeWMxXADAhM5suAOxUdds4vH4UH3_3KdhxHJHjIyxewTiEfEAw7IqY3ddiaPC-JnHzGHoC6So");
        cookieMap.put("171", "pt_key=AAJeWMlmADB4AHPOqA5MHzQwoMrl_J8cmOv4pMwvMto5A0GyqZK8No5sYMujMI_O01Yc6JQsDfg");
        cookieMap.put("xqq", "pt_key=AAJeWNCsADDIfJF76rnm8t1hWnArZi1sPNgf_spKO-m8Ya3abGJHuH9zBXZorOi1HRJXeyn5RDo");
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
