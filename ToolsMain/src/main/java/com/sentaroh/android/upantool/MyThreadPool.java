package com.sentaroh.android.upantool;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//    public void customThreadPool(View view) {
//        final MyThreadPool myThreadPool = new MyThreadPool(3, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());
//        for (int i = 0; i < 10; i++) {
//            final int finalI = i;
//            Runnable runnable = new Runnable(){
//                @Override
//                public void run() {
//                    SystemClock.sleep(100);
//                    Log.d("google_lenve_fb", "run: " + finalI);
//                }
//            };
//            myThreadPool.execute(runnable);
//        }
//    }
public class MyThreadPool extends ThreadPoolExecutor {

    public MyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        Log.d("google_lenve_fb", "beforeExecute: 开始执行任务！");
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        Log.d("google_lenve_fb", "beforeExecute: 任务执行结束！");
    }

    @Override
    protected void terminated() {
        super.terminated();
        //当调用shutDown()或者shutDownNow()时会触发该方法
        Log.d("google_lenve_fb", "terminated: 线程池关闭！");
    }
}
