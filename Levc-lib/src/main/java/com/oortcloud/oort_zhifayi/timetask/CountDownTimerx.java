//package com.oortcloud.oort_zhifayi.service;
//
//
///**
// * Email 465571041@qq.com
// * Created by zhang-zhi-jun on 2025/8/28-17:45.
// * Version 1.0
// * Description:适合倒计时场景
// */
//public class CountDownTimerx {
//    private CountDownTimer mCountDownTimer;
//
//    private void startCountDownTimer() {
//        mCountDownTimer = new CountDownTimer(60000, 1000) { // 60秒，每秒回调
//            @Override
//            public void onTick(long millisUntilFinished) {
//                // 每秒执行
//                updateTimerUI(millisUntilFinished / 1000);
//            }
//
//            @Override
//            public void onFinish() {
//                // 计时结束
//                doTask();
//            }
//        };
//        mCountDownTimer.start();
//    }
//
//    private void stopCountDownTimer() {
//        if (mCountDownTimer != null) {
//            mCountDownTimer.cancel();
//        }
//    }
//}
