package com.oort.weichat.ui.lccontact;

import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.Label;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
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

public class LCAddressContactActivity extends LCLabUserActivity{

    public void initTextData() {
        mtitle = "常用联系人";
    }

    public void createLabel(String groupName) {
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("is_open","0");
        params.put("sort", String.valueOf(mLabelList.size() + 1));
        params.put("tid", "");
        params.put("name", groupName);
        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.post().url(Constant.BASE_URL + Constant.ADDRESS_TAG_ADD)
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

        HttpRequestCenter.addressTagDel(label.getTid()).subscribe(new RxBusSubscriber<String>() {
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
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("is_open", "0");

        HttpUtils.post().url(Constant.BASE_URL + Constant.ADDRESS_TAG_LIST)
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
                            mLabelList = labs;
                            mlabAd.refreshData(mLabelList);

                            {
                                mlabAd.selectIndex(mSelectIndex);
                                if(mLabelList.size() > 0) {
                                    rtvTitle.setVisibility(View.VISIBLE);
                                    Label lab = (Label) mLabelList.get(mSelectIndex);
                                    getUser(lab.getTid());
                                }else{
                                    mSortList.clear();
                                    mUsersAd.refresh(mSortList);

                                    countlab.setText(getString(R.string.p_in_tag_count) + mSortList.size());

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
        HttpRequestCenter.addAddressTagUsers(slabId, inviteIdList).subscribe(new RxBusSubscriber<String>() {
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


        HttpRequestCenter.getAddressTagUsers(labId).subscribe(new RxBusSubscriber<String>() {
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
                    countlab.setText(getString(R.string.p_in_tag_count) + mSortList.size());
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
        HttpRequestCenter.addressDelTagUsers(labId,list).subscribe(new RxBusSubscriber<String>() {
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
