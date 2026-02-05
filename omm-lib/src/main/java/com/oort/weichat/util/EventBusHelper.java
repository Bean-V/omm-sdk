package com.oort.weichat.util;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import org.greenrobot.eventbus.EventBus;

public class EventBusHelper {
    private EventBusHelper() {
    }

    public static void register(LifecycleOwner activity) {
        activity.getLifecycle().addObserver(new LifecycleObserver() {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            void create() {
                EventBus.getDefault().register(activity);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            void destroy() {
                EventBus.getDefault().unregister(activity);
            }
        });
    }
}
