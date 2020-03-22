package com.example.demo.earnfood;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.earnfood.detail.*;

public class WorkerFactory {

    public static Worker getWorkerByType(String taskType){
        switch (taskType) {
            case "FollowShop" :
                return FollowShopWorker.getInstance();
            case "ThreeMeals" :
                return ThreeMealsWorker.getInstance();
            case "FollowGood" :
                return FollowGoodWorker.getInstance();
            case "FollowChannel" :
                return FollowChannelWorker.getInstance();
            case "ScanMarket" :
                return ScanMarketWorker.getInstance();
            case "ViewVideo" :
                return ViewVideoWorker.getInstance();
//            case "StealFoodAndCoin" :
//                return StealFoodAndCoinWorker.getInstance();
            case "deskGoods" :
                return DeskGoodsWorker.getInstance();
            default:
                return NotSupportWorker.getInstance();
        }
    }
}
