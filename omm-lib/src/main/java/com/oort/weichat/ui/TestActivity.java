package com.oort.weichat.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.oort.weichat.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_home_parent);
    }
}