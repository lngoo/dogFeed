package com.example.demo.earnfood;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.detail.FollowGoodWorker;
import com.example.demo.earnfood.detail.FollowShopWorker;
import com.example.demo.earnfood.detail.NotSupportWorker;
import com.example.demo.earnfood.detail.ThreeMealsWorker;

public class WorkerFactory {

    public static Worker getWorkerByType(String taskType){
        switch (taskType) {
            case "FollowShop" :
                return FollowShopWorker.getInstance();
            case "ThreeMeals" :
                return ThreeMealsWorker.getInstance();
            case "FollowGood" :
                return FollowGoodWorker.getInstance();
            default:
                return NotSupportWorker.getInstance();
        }
    }
}
