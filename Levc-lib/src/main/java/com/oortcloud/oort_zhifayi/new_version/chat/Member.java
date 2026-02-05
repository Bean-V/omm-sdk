package com.oortcloud.oort_zhifayi.new_version.chat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Member {
    private String name;
    private boolean isOnline;

    public Member(String name, boolean isOnline) {
        this.name = name;
        this.isOnline = isOnline;
    }

    // Getters and Setters
    public String getName() { return name; }
    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }
}
