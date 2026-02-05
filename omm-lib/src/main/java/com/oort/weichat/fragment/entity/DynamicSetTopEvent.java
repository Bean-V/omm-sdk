package com.oort.weichat.fragment.entity;

public class DynamicSetTopEvent {



    public interface SetCallback{
        public void setCallback(int top);
    }
    public boolean show = false;
    public int topN = 0;
    public int type = 0;
    public SetCallback callback;

}
