package com.oort.weichat.ui.contacts.label;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.Label;
import com.oort.weichat.db.dao.LabelDao;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.BaseLabelGridFragment;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.UiUtils;
import com.oort.weichat.view.VerifyDialog;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.utils.SkinUtils;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Result;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class LabelFragment extends BaseLabelGridFragment<LabelFragment.LabelHolder> {
    private List<Label> mLabelList;
    private Map<String, String> map = new HashMap<>();

    public void setEdit(Boolean edit) {
        this.edit = edit;
        update();
    }

    Boolean edit = false;

    @Override
    public void initDatas(int pager) {
        refreshLabelListFromService();
    }

    @Override
    public LabelHolder initHolder(ViewGroup parent) {
        View v = mInflater.inflate(R.layout.item_label_grid, parent, false);
        return new LabelHolder(v);
    }

    @Override
    public LabelHolder initOtherHolder(ViewGroup parent) {
        View v = mInflater.inflate(R.layout.item_other_label_grid, parent, false);
        return new LabelHolder(v);
    }

    @Override
    public void fillData(LabelHolder holder, int position) {
        final Label label = mLabelList.get(position);
        if (label != null) {
           // List<String> userIds = JSON.parseArray(label.getUserIdList(), String.class);
//            if (userIds != null) {
//                holder.tv_label.setText(label.getName() + "(" + userIds.size() + ")");
//            } else {
                holder.tv_label.setText(label.getName());// + "(0)"
 //           }
        }
        ViewCompat.setBackgroundTintList(holder.tv_label, ColorStateList.valueOf(SkinUtils.getSkin(requireActivity()).getAccentColor()));
        if (label == null) {
            ViewCompat.setBackgroundTintList(holder.iv_label, ColorStateList.valueOf(SkinUtils.getSkin(requireActivity()).getAccentColor()));
            holder.iv_label.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), CreateLabelActivity.class);
                intent.putExtra("isEditLabel", false);
                getActivity().startActivityForResult(intent, 0x01);
            });
        } else {
            holder.iv_delete_label.setVisibility(edit ? View.VISIBLE : View.GONE);
            holder.tv_label.setOnClickListener(v -> {
                onItemClick(v, position);
                holder.iv_delete_label.setVisibility(holder.iv_delete_label.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
            });
//            holder.tv_label.setOnLongClickListener(v -> {
//                if (map.containsKey(label.getGroupId())) {
//                    map.remove(label.getGroupId());
//                } else {
//                    map.put(label.getGroupId(), label.getGroupId());
//                }
//                update();
//                return true;
//            });
            holder.iv_delete_label.setOnClickListener(v -> {
                onIvClick(position);
                //holder.iv_delete_label.setVisibility(holder.iv_delete_label.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
            });
        }
    }

    @Override
    public void fillOtherData(LabelHolder holder, int position) {
        ViewCompat.setBackgroundTintList(holder.iv_label, ColorStateList.valueOf(SkinUtils.getSkin(requireActivity()).getAccentColor()));
        holder.iv_label.setOnClickListener(v -> {
            VerifyDialog verifyDialog = new VerifyDialog(getContext());
            verifyDialog.setVerifyClickListener(getString(R.string.tag_name), new VerifyDialog.VerifyClickListener() {
                @Override
                public void cancel() {

                }

                @Override
                public void send(String str) {
                    createLabel(str);
                }
            });
            verifyDialog.setOkButton(R.string.sure);
            verifyDialog.show();

//            Intent intent = new Intent(requireActivity(), CreateLabelActivity.class);
//            intent.putExtra("isEditLabel", false);
//            startActivityForResult(intent, 0x01);
        });
    }

    private void createLabel(String groupName) {
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("is_open","0");
        params.put("sort", "1");
        params.put("tid", "");
        params.put("name", groupName);
        DialogHelper.showDefaulteMessageProgressDialog(getActivity());

        HttpUtils.post().url(Constant.BASE_URL + Constant.TAG_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<Label>(Label.class) {
                    @Override
                    public void onResponse(ObjectResult<Label> result) {
                        if (result.getCode() == 200) {
                            // LabelDao.getInstance().createLabel(result.getData());
                            // updateLabelUserIdList(result.getData(), inviteIdList);

//                            Intent intent = new Intent(this, CreateLabelActivity.class);
//                            intent.putExtra("isEditLabel", false);
//                            startActivity(intent);
                            refreshLabelListFromService();
                        } else {
                            DialogHelper.dismissProgressDialog();
                            ToastUtil.showErrorData(getActivity());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(getActivity());
                    }
                });
    }
    public void onItemClick(View view, int position) {
        if (UiUtils.isNormalClick(view)) {// 防止过快点击
            Label label = mLabelList.get(position);
            if (label != null) {
                Intent intent = new Intent(requireActivity(), LabUsersActivity.class);
                intent.putExtra("title", label.getName());
                intent.putExtra("labId", label.getTid());
                startActivityForResult(intent, 0x01);


               // refreshLabelUserListFromService(label.getTid());
            }else {
                //                Intent intent = new Intent(requireActivity(), CreateLabelActivity.class);
//                intent.putExtra("isEditLabel", true);
//                intent.putExtra("labelId", label.getGroupId());
//                startActivityForResult(intent, 0x01);
            }
        }
    }

    public void onIvClick(int position) {
        final Label label = mLabelList.get(position);
        com.oortcloud.appstore.dailog.DialogHelper.getConfirmDialog(getContext(), getContext().getString(R.string.del_tag_tip), new DialogInterface.OnClickListener() {
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

    private void deleteLabel(final Label label) {

        HttpRequestCenter.tagDel(label.getTid()).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Log.v("msg"  ,s);
                Result<Data> result = new Gson().fromJson(s, new TypeToken<Result<Data>>() {}.getType());
                if (result.isOk()) {
                    // finishActivity(0x01);
                   // finish();
                    refreshLabelListFromService();
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
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("groupId", label.getGroupId());
        DialogHelper.showDefaulteMessageProgressDialog(requireActivity());

        HttpUtils.get().url(coreManager.getConfig().FRIENDGROUP_DELETE)
                .params(params)
                .build()
                .execute(new BaseCallback<Label>(Label.class) {
                    @Override
                    public void onResponse(ObjectResult<Label> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            LabelDao.getInstance().deleteLabel(coreManager.getSelf().getUserId(), label.getGroupId());
                            loadData();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                    }
                });
    }

    // 从服务端下载标签
    private void refreshLabelListFromService() {
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

                            //LabelDao.getInstance().refreshLabel(coreManager.getSelf().getUserId(), labs);
                            //update(labs);
                            mLabelList = labs;
                            loadData();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }



    // 从服务端下载标签
    private void refreshLabelUserListFromService(String tid) {
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("pagesize", "1000");
        params.put("keyword", "");
        params.put("tid", tid);
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

                            //LabelDao.getInstance().refreshLabel(coreManager.getSelf().getUserId(), labs);
                            //update(labs);
                            //mLabelList = labs;
                            //loadData();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }



    // 从数据库加载标签
    private void loadData() {
        if (mLabelList == null) {
            mLabelList = new ArrayList<>();
        }
 //       mLabelList = //LabelDao.getInstance().getAllLabels(coreManager.getSelf().getUserId());
        update(mLabelList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01) {
            loadData();
        }
    }

    class LabelHolder extends RecyclerView.ViewHolder {
        TextView tv_label;
        ImageView iv_label;
        ImageView iv_delete_label;

        LabelHolder(@NonNull View itemView) {
            super(itemView);
            iv_delete_label = itemView.findViewById(R.id.iv_delete_label);
            tv_label = itemView.findViewById(R.id.tv_label);
            iv_label = itemView.findViewById(R.id.iv_label);
        }
    }
}
