package com.oort.weichat.ui.lccontact;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.Label;
import com.oort.weichat.helper.DialogHelper;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CYFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CYFragment extends LabFragment {


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
                            adl.refreshData(mLabelList);

                            {
                                adl.selectIndex(mSelectIndex);

                                if(mSelectIndex < mLabelList.size()) {
                                    Label lab = (Label) mLabelList.get(mSelectIndex);
                                    getUser(lab.getTid());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    public void getUser(String labId) {


        HttpRequestCenter.getAddressTagUsers(labId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg", s);
                List<Sort> dataList = new ArrayList<>();
                Result<Data<UserInfo>> UserResult = new Gson().fromJson(s, new TypeToken<Result<Data<UserInfo>>>() {
                }.getType());
                if (UserResult.isOk()) {
                    dataList.addAll(UserResult.getData().getList());
                    mSortList = dataList;
                    cl.setMlist(dataList);

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