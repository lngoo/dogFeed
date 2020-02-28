package com.example.demo.util;

public class ThreadUtil {

    public static void sleepSeconds(int sec){
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepRandomSeconds(int min, int max){
        try {
            Thread.sleep(getRandomRange(min, max) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int getRandomRange(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
}
