package sound.wave.oort;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }


    protected String TAG = getClass().getSimpleName();

    @Override
    protected void attachBaseContext(Context newBase) {
        Log.e(TAG, "attachBaseContext");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  unregisterReceiver(myBroadcastReceive);
        // EventBus.getDefault().unregister(this);
    }


    protected void setStatusBarLight(boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // >=5.0 背景为全透明
            /* >=5.0，this method(getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS));
            in some phone is half-transparent like vivo、nexus6p..
            in some phone is full-transparent
            so ...*/

            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (light) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//| View.SYSTEM_UI_FLAG_VISIBLE
                } else {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
//                window.setStatusBarColor(Color.TRANSPARENT);
                window.setStatusBarColor(getResources().getColor(R.color.mainColor));
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                if (light) {
                    window.setStatusBarColor(getResources().getColor(R.color.mainColor));
                } else {
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4背景为渐变半透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void toast(String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, str,
                        Toast.LENGTH_LONG).show();
            }
        });

    }





    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
