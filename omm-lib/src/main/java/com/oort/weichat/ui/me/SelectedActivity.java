package com.oort.weichat.ui.me;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.StateInputUtils;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.bean.UserInfo;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;

import java.util.HashMap;
import java.util.Map;

public class SelectedActivity extends BaseActivity implements View.OnClickListener{
    private Map<String , String> map = new HashMap<>();
    private ImageView selectedIv;
    private TextView selectedTv;
    private EditText selectedEt;
    private StateInputUtils mStateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        getUserInfo();
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_title_left).setOnClickListener(v -> finish());
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_left);
        TextView tvTitle1 = (TextView) findViewById(R.id.tv_title_right);
        selectedIv = (ImageView) findViewById(R.id.selectedIv);
        selectedTv = (TextView) findViewById(R.id.selectedTv);
        selectedEt = (EditText) findViewById(R.id.selectedEt);
        tvTitle.setText("选中状态");
        tvTitle.setTextSize(18);
        tvTitle1.setText("确定");
        tvTitle.setTextSize(16);
        tvTitle1.setOnClickListener(this);
    }
    private void getUserInfo() {
        mStateInput = StateInputUtils.getInstance();
        HttpRequestCenter.GetUserInfo().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<Data<UserInfo>> result = new Gson().fromJson(s,new TypeToken<Result<Data<UserInfo>>>(){}.getType());
                if (result.isok()){
                    String nameState = result.getData().getUserInfo().getImstatus();
                    String imageState = mStateInput.getProCode(nameState);
                    selectedIv.setBackgroundDrawable(mContext.getDrawable(Integer.parseInt(imageState)));
                    selectedTv.setText(nameState);
                    Log.v("msgxkss",nameState);
                    Log.v("msgxksss",imageState);
                }
            }
        });
    }
    private void putSelected(){
        String putselected = selectedEt.getText().toString().trim();
        FastSharedPreferences.get("USERINFO_SAVE").edit().putString("selected",putselected).apply();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_title_right) {
            putSelected();
            finish();
        }
    }
}
