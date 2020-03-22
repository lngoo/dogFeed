package com.example.demo.vo;

/**
 * 偷粮食详情
 */
public class StealInfo {
    private int foodCount = 0;
    private int coinCount = 0;

    public void addFoodCount(){
        this.foodCount++;
    }

    public void addCoinCount(){
        this.coinCount++;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void setFoodCount(int foodCount) {
        this.foodCount = foodCount;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public void clear() {
        this.foodCount = 0;
        this.coinCount = 0;
    }
}
