package com.oort.weichat.ui.lccontact;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.Label;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.contacts.activity.PersonDetailActivity;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.DeptInfo;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
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

public class LCLabUserActivity extends BaseActivity {

    public String createtag_url;
    public String gettaglist_url;
    public String deltag_url;
    public String add_url;
    public String tagusers_url;
    public String deluser_url;


    public ArrayList mLabelList = new ArrayList();
    public LabAdapter mlabAd;
    public PSAdapter mUsersAd;
    public int mSelectIndex = 0;
    public List mSortList = new ArrayList();
    public String mtitle;
    public TextView countlab;
    public TextView rtvTitle;

//    public static void startAddress{
//
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_user);

        initTextData();

        initActionBar();
        ListView labList = findViewById(R.id.list_Lab);
        ListView plist = findViewById(R.id.list_p);
        countlab = findViewById(R.id.lab_name);

        List type = new ArrayList();

        List p = new ArrayList();

        mlabAd = new LabAdapter(this,type);
        mUsersAd = new PSAdapter(this,p);

        labList.setAdapter(mlabAd);

        labList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == mLabelList.size()) {
// TODO: 2022/11/1

                    @SuppressLint("ResourceType") AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LCLabUserActivity.this, R.color.transparent));

                    //加载自定义的那个View,同时设置下
                    final LayoutInflater inflater = LCLabUserActivity.this.getLayoutInflater();
                    View inputView = inflater.inflate(R.layout.layout_inputlab_dialog, null,false);
                    builder.setView(inputView);
                    builder.setCancelable(false);
                    @SuppressLint("ResourceType") final Dialog dialog = new Dialog(LCLabUserActivity.this, R.color.transparent);
                    dialog.setContentView(inputView);

                    EditText et = inputView.findViewById(R.id.edit_inputlab);



                    dialog.show();
                    //alert.getWindow().setBackgroundDrawable(new ColorDrawable());

                    inputView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                        }
                    });
                    inputView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            if(et.getText().length() > 0){

                                if(et.getText().length() > 20){

                                    ToastUtil.showToast(LCLabUserActivity.this,getString(R.string.input_name_over_top));
                                    return;
                                }
                                createLabel(et.getText().toString());
                            }

                        }
                    });

                } else{
                    mSelectIndex = i;
                    mlabAd.selectIndex(i);
                    Label lab = (Label) mLabelList.get(i);
                    refreshOpenStateData(lab);
                    mUsersAd.setHideDel(lab.getIs_open() == 1 ? true : false);
                    getUser(lab.getTid());
                }
            }
        });

        plist.setAdapter(mUsersAd);

        findViewById(R.id.btn_addP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonPickActivity.pickFinish = null;
                PersonPickActivity.pickFinish_v2 = null;
                PersonPickActivity p = new PersonPickActivity();
                Intent in = new Intent(LCLabUserActivity.this,PersonPickActivity.class);
                startActivityForResult(in,100);

                PersonPickActivity.pickFinish = null;


            }
        });


        plist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserInfo info = (UserInfo) mSortList.get(i);
                PersonDetailActivity.actionStart(mContext ,info);
            }
        });

        mUsersAd.setOnItemClickListener(new PSAdapter.ItemClickListener() {
            @Override
            public void onItemDelClick(int position) {
                UserInfo info = (UserInfo) mSortList.get(position);


                com.oortcloud.appstore.dailog.DialogHelper.getConfirmDialog(LCLabUserActivity.this, getString(R.string.del_user_from_tag,info.getOort_name()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Label lab = (Label) mLabelList.get(mSelectIndex);
                        deleteUser(lab.getTid(),info);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        getLabs();



    }

    public void initTextData() {
        mtitle = getString(R.string.tag);
    }

    public void initInterFace(){
        createtag_url = Constant.BASE_URL + Constant.TAG_ADD;
        gettaglist_url = Constant.BASE_URL + Constant.TAG_LIST;
        deltag_url = Constant.BASE_URL + Constant.TAG_DELETE;
        add_url = Constant.BASE_URL + Constant.TAG_ADD;
        deluser_url = Constant.BASE_URL + Constant.TAG_USERDEL;
        tagusers_url = Constant.BASE_URL + Constant.TAG_LIST;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            switch (requestCode) {
                case 100:
                    Bundle bundle = data.getExtras();
                    List ids = bundle.getStringArrayList("data");

                    if(mLabelList.size() == 0){
                        return;
                    }
                    Label lab = (Label) mLabelList.get(mSelectIndex);
                    updateLabelUserIdList(lab.getTid(),ids);
                    break;
                default:
                    break;
            }
        }
    }




    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(mtitle);

        rtvTitle = (TextView) findViewById(R.id.tv_title_right);
        rtvTitle.setVisibility(View.GONE);
        rtvTitle.setText(getString(com.oortcloud.appstore.R.string.del_str));
        rtvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Label label = (Label) mLabelList.get(mSelectIndex);


                com.oortcloud.appstore.dailog.DialogHelper.getConfirmDialog(LCLabUserActivity.this, getString(R.string.del_tag_tip), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteLabel(label);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

            }
        });
    }


    void refreshOpenStateData(Label lab){

        if(lab.getIs_open() == 1){

            rtvTitle.setVisibility(View.GONE);
            findViewById(R.id.btn_addP).setVisibility(View.GONE);
        }else {
            rtvTitle.setVisibility(View.VISIBLE);
            findViewById(R.id.btn_addP).setVisibility(View.VISIBLE);
        }
    }

    public void createLabel(String groupName) {
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("is_open","0");
        params.put("sort", String.valueOf(mLabelList.size() + 1));
        params.put("tid", "");
        params.put("name", groupName);
        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.post().url(Constant.BASE_URL + Constant.TAG_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<Label>(Label.class) {
                    @Override
                    public void onResponse(ObjectResult<Label> result) {
                        if (result.getCode() == 200) {
                            mSelectIndex = 0;
                            getLabs();
                        } else {
                            DialogHelper.dismissProgressDialog();
                            ToastUtil.showErrorData(getBaseContext());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(getBaseContext());
                    }
                });
    }

    public void deleteLabel(final Label label) {

        HttpRequestCenter.tagDel(label.getTid()).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {
                }.getType());
                if (result.isOk()) {
                    mSelectIndex = 0;
                    getLabs();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg", e.toString());
            }
        });
    }
    public void getLabs() {

        mLabelList.clear();
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("is_open", "0");

        HttpUtils.post().url(Constant.BASE_URL + Constant.TAG_LIST)
                .params(params)
                .build()
                .execute(new BaseCallback<HashMap>(HashMap.class) {
                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        if (result.getCode() == 200 && result.getData() != null) {
                            DialogHelper.dismissProgressDialog();
                            JSONArray labelList = (JSONArray) result.getData().get("list");
                            ArrayList labs = new ArrayList();
                            Gson son = new Gson();
                            for(int i = 0;i<labelList.size();i++){
                                labs.add(son.fromJson(labelList.get(i).toString(), Label.class));
                            }
                            mLabelList.addAll(labs);
                            mlabAd.refreshData(mLabelList);

                            {
                                mlabAd.selectIndex(mSelectIndex);
                                if(mLabelList.size() > 0) {
                                    rtvTitle.setVisibility(View.VISIBLE);
                                    Label lab = (Label) mLabelList.get(mSelectIndex);
                                    refreshOpenStateData(lab);
                                    mUsersAd.setHideDel(lab.getIs_open() == 1 ? true : false);
                                    getUser(lab.getTid());


                                }else{
                                    mSortList.clear();
                                    mUsersAd.refresh(mSortList);

                                    countlab.setText(getString(R.string.p_in_tag_count) + " " +  mSortList.size());

                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });

        Map<String, String> params1 = new HashMap<>();
        params1.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params1.put("is_open", "1");

        HttpUtils.post().url(Constant.BASE_URL + Constant.TAG_LIST)
                .params(params1)
                .build()
                .execute(new BaseCallback<HashMap>(HashMap.class) {
                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        if (result.getCode() == 200 && result.getData() != null) {
                            DialogHelper.dismissProgressDialog();
                            JSONArray labelList = (JSONArray) result.getData().get("list");
                            ArrayList labs = new ArrayList();
                            Gson son = new Gson();
                            for(int i = 0;i<labelList.size();i++){

                                Label lab = son.fromJson(labelList.get(i).toString(), Label.class);
                                if(mLabelList.size() > i){
                                    mLabelList.add(i,lab);
                                }else{
                                    mLabelList.add(lab);
                                }
                            }
                            mlabAd.refreshData(mLabelList);

                            {
                                mlabAd.selectIndex(mSelectIndex);
                                if(mLabelList.size() > 0) {
                                    rtvTitle.setVisibility(View.VISIBLE);
                                    Label lab = (Label) mLabelList.get(mSelectIndex);
                                    refreshOpenStateData(lab);
                                    mUsersAd.setHideDel(lab.getIs_open() == 1 ? true : false);
                                    getUser(lab.getTid());
                                }else{
                                    mSortList.clear();
                                    mUsersAd.refresh(mSortList);

                                    countlab.setText(getString(R.string.p_in_tag_count) + " " + mSortList.size());

                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    public void updateLabelUserIdList(String slabId, List<String> inviteIdList) {
        HttpRequestCenter.addTagUsers(slabId, inviteIdList).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                Result<Data<DeptInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<DeptInfo>>>() {
                }.getType());
                if (result.isOk()) {
                    getUser(slabId);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg", e.toString());
            }
        });
    }
    private void getUser(String labId) {


        HttpRequestCenter.getTagUsers(labId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                List<Sort> dataList = new ArrayList<>();
                Result<Data<UserInfo>> UserResult = new Gson().fromJson(s, new TypeToken<Result<Data<UserInfo>>>() {
                }.getType());
                if (UserResult.isOk()) {
                    dataList.addAll(UserResult.getData().getList());
                    ///获取部门数据
                    mSortList = dataList;

                    mUsersAd.refresh(dataList);

                    countlab.setText(getString(R.string.p_in_tag_count) +   " " + mSortList.size());

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg", e.toString());
            }
        });
    }

    public void deleteUser(String labId, final UserInfo user) {

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
                    getUser(labId);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg", e.toString());
            }
        });
    }


}