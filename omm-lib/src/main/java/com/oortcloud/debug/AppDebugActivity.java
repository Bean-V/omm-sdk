package com.oortcloud.debug;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.constant.Constant;
import com.oort.weichat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppDebugActivity extends BaseActivity {


    private EditText et;

    private Button btn;

    private ListView lv;

    private HistoryAdapter historyAdapter;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_debug_activity);
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.HISTORY_RECORD);
        String b = sharedPreferences.getString("list","");
        if (!TextUtils.isEmpty(b)) {
            list = JSON.parseObject(b, new TypeReference<ArrayList<String>>() {
            });
        }


        et = (EditText) findViewById(R.id.et);
        btn = (Button) findViewById(R.id.btn);
        lv = (ListView) findViewById(R.id.lv);
        historyAdapter = new HistoryAdapter();
        lv.setAdapter(historyAdapter);
        verifyStoragePermissions(this);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void click(View v) {
        if (!TextUtils.isEmpty(et.getText().toString()) && v.getId() == R.id.btn) {
            list.add(0, et.getText().toString());
            historyAdapter.notifyDataSetChanged();


            Intent intent = new Intent(AppDebugActivity.this,DebugActivity.class);

            intent.putExtra("web_type", "h5");
            intent.putExtra("url", et.getText().toString());
            startActivity(intent);


        }else if(v.getId() == R.id.chooser){
            BaseActivity.iOortPluginCallback = new IOortPluginCallback() {
                @Override
                public void onOortFrameCallback (String json) {
                    try {
                        final JSONObject jsonObject = new JSONObject(json);
                        AppDebugActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String path = "file://" + jsonObject.optString("file_path");
                                et.setText(path);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            Intent intent = new Intent(this, ChoiceFileActivity.class);
            intent.setAction("choice_file_activity");
            this.startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String a = JSON.toJSONString(list);
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.HISTORY_RECORD);
        sharedPreferences.edit().putString("list", a).apply();
    }


    public class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(AppDebugActivity.this).inflate(R.layout.debug_listview_item, null);
            TextView tvItem = view.findViewById(R.id.tv_item);
            tvItem.setText(list.get(i));
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AppDebugActivity.this, DebugActivity.class);
                    intent.putExtra("web_type", "h5");
                    intent.putExtra("url", list.get(i));
                    startActivity(intent);
                }
            });

            return view;
        }
    }

}
