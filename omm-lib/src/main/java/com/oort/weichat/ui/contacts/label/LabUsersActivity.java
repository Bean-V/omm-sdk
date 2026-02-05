package com.oort.weichat.ui.contacts.label;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.contacts.adapter.DepartAndUserAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.fragment.ContactsFragment;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class LabUsersActivity extends BaseActivity {

    private ContactsFragment contactsFragment;
    RecyclerView mDeptAndUserRv;
    private String title;
    private String labId;

    private DepartAndUserAdapter mDepAndUsrAdapter;
    private ArrayList<UserInfo> mSortList = new ArrayList();
    private LinearLayoutManager linearLayoutManager;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_users);
        initActionBar();
        Intent in = getIntent();
        title = in.getStringExtra("title");
        labId = in.getStringExtra("labId");
        initActionBar();


        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mDeptAndUserRv = findViewById(R.id.rv_hail_fellow);
        mDeptAndUserRv.setLayoutManager(linearLayoutManager);
        mDepAndUsrAdapter = new DepartAndUserAdapter(mContext, mSortList,Constants.TAG_USER);
        mDeptAndUserRv.setAdapter(mDepAndUsrAdapter);
        getData();

        mDepAndUsrAdapter.setOnItemClickListener(new DepartAndUserAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onCheckItemClick(int statu, UserInfo user) {

            }

            @Override
            public void onItemTagDelClick(int position, UserInfo user) {
                com.oortcloud.appstore.dailog.DialogHelper.getConfirmDialog(LabUsersActivity.this, getString(R.string.del_user_from_tag,user.getOort_name()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUser(user);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });



    }

    private void deleteUser(final UserInfo user) {

        ArrayList list = new ArrayList();
        list.add(user.getOort_uuid());
        HttpRequestCenter.delTagUsers(labId,list).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {
                }.getType());
                if (result.isOk()) {
                    // finishActivity(0x01);
                    // finish();
                    getData();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg", e.toString());
            }
        });
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(title);

        TextView tvrTitle = (TextView) findViewById(R.id.tv_title_right);
        tvrTitle.setText("添加人员");
        tvrTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LabUsersActivity.this, SelectLabelFriendActivity.class);
                List<String> ids = new ArrayList<>();
                for (int i = 0; i < mSortList.size(); i++) {
                    ids.add(mSortList.get(i).getOort_uuid());
                }
                intent.putExtra("exist_ids", JSON.toJSONString(ids));
                intent.putExtra("labId", labId);
                startActivityForResult(intent, 0x01);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getData();
    }

    // 从服务端下载标签
    private void getData() {




        HttpRequestCenter.getTagUsers(labId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg"  ,s);
                List<Sort> dataList = new ArrayList<>();
                Result<Data<UserInfo>> UserResult = new Gson().fromJson(s, new TypeToken<Result<Data<UserInfo>>>() {
                }.getType());
                if (UserResult.isOk()) {
                    dataList.addAll(UserResult.getData().getList());
                    ///获取部门数据
                    //mSortList = dataList;

                    mDepAndUsrAdapter.updateList(dataList);

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg"  , e.toString());
            }
        });

        if(true){
            return;
        }



        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("pagesize", "1000");
        params.put("keyword", "");
        params.put("tid", labId);
        params.put("page", "1");

        HttpUtils.post().url(Constant.BASE_URL + Constant.TAG_USERLIST)
                .params(params)
                .build()
                .execute(new BaseCallback<HashMap>(HashMap.class) {



                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        if (result.getCode() == 200 && result.getData() != null) {
                            JSONArray labelList = (JSONArray) result.getData().get("list");
                            ArrayList labs = new ArrayList();
                            Gson son = new Gson();
                            for(int i = 0;i<labelList.size();i++){
                                labs.add(son.fromJson(labelList.get(i).toString(), UserInfo.class));
                            }

                            mSortList = labs;
                            mDepAndUsrAdapter.updateList(labs);


                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

}