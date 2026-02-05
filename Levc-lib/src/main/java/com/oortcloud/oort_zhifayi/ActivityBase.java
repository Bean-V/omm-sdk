package com.oortcloud.oort_zhifayi;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.oortcloud.oort_zhifayi.databinding.ActivityBaseBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.View;


public class ActivityBase extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideNavigationBar();
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
    protected boolean isVisible = false;
    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

}