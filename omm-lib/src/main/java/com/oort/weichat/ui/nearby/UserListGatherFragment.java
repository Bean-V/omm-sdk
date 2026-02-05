package com.oort.weichat.ui.nearby;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.BaseGridFragment;
import com.oort.weichat.ui.other.BasicInfoActivity;
import com.oort.weichat.util.ScreenUtil;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.data.PageListResult;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.contacts.activity.PersonDetailActivity;
import com.oortcloud.contacts.bean.UserInfo;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 搜索...
 */
public class UserListGatherFragment extends BaseGridFragment<UserListGatherFragment.UserListGatherHolder> {
    double latitude;
    double longitude;
    private List<UserInfo> mUsers = new ArrayList<>();

    private boolean isPullDwonToRefersh;
    private int mPageIndex = 1;

    private String mKeyWord;// 关键字(keyword)
    private int mSex;                 // 城市Id(cityId)
    private int mMinAge;         // 行业Id(industryId)
    private int mMaxAge;         // 职能Id(fnId)
    private int mShowTime;     // 日期(days)

    @Override
    public void initDatas(int pager) {
        if (pager == 0) {
            isPullDwonToRefersh = true;
        } else {
            isPullDwonToRefersh = false;
        }

        latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
        longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();

        mKeyWord = getActivity().getIntent().getStringExtra("key_word");
        mSex = getActivity().getIntent().getIntExtra("sex", 0);
        mMinAge = getActivity().getIntent().getIntExtra("min_age", 0);
        mMaxAge = getActivity().getIntent().getIntExtra("max_age", 200);
        mShowTime = getActivity().getIntent().getIntExtra("show_time", 0);
        requestData(isPullDwonToRefersh);
    }

    public RecyclerView.LayoutManager getLayoutManager(){
        return new GridLayoutManager(getActivity(), 1);
    }
    private void requestData(final boolean isPullDwonToRefersh) {
        if (isPullDwonToRefersh) {
            mPageIndex = 1;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("pageIndex", String.valueOf(mPageIndex));
        // params.put("pageSize", String.valueOf(AppConfig.PAGE_SIZE));
        params.put("pageSize", "20");
        if (!TextUtils.isEmpty(mKeyWord)) {
            params.put("nickname", mKeyWord);
        }
        if (mSex != 0) {
            params.put("sex", String.valueOf(mSex));
        }

        if (mMinAge != 0) {
            params.put("minAge", String.valueOf(mMinAge));
        }

        if (mMaxAge != 0) {
            params.put("maxAge", String.valueOf(mMaxAge));
        }

        params.put("active", String.valueOf(mShowTime));

        DialogHelper.showDefaulteMessageProgressDialog(getActivity());




        refreshUserListFromService(mKeyWord);


        /*
        HttpUtils.get().url(coreManager.getConfig().USER_NEAR)
                .params(params)
                .build()
                .execute(new ListCallback<User>(User.class) {
                    @Override
                    public void onResponse(ArrayResult<User> result) {
                        DialogHelper.dismissProgressDialog();
                        mPageIndex++;
                        if (isPullDwonToRefersh) {
                            mUsers.clear();

                        }
                        List<User> datas = result.getData();
                        if (datas != null && datas.size() > 0) {
                            mUsers.addAll(datas);

                        }
                        update(mUsers);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
                    }
                });

         */
    }


    private void refreshUserListFromService(String key) {

//        "accessToken": "accessToken",
//                "keyword": "陈三",
//                "oort_depcode": "99",
//                "page": 1,
//                "pagesize": 10
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("keyword", key);
        //params.put("oort_depcode", "");
        params.put("page", String.valueOf(mPageIndex));
        params.put("pagesize", "100");

        HttpUtils.post().url(Constant.BASE_URL + Constant.USER_LIST)
                .params(params)
                .build()
                //.execute(new BaseCallback<PageListResult>(PageListResult.class) {
                    .execute(new BaseCallback<String>(String.class) {




                    @Override
                    public void onResponse(ObjectResult<String> o) {



                        if (o.getCode() == 200 && o.getData() != null) {

                            PageListResult res = (new Gson()).fromJson(o.getData(),new TypeToken<PageListResult<UserInfo>>(){}.getType());
                            DialogHelper.dismissProgressDialog();
                            ArrayList labs = (ArrayList) res.getList();
                            Gson son = new Gson();

                            if(res.getPages() < mPageIndex){

                                return;
                            }
                            DialogHelper.dismissProgressDialog();
                            mPageIndex++;
                            if (isPullDwonToRefersh) {
                                mUsers.clear();

                            }

                            mUsers.addAll(labs);

                            update(mUsers);

                            //LabelDao.getInstance().refreshLabel(coreManager.getSelf().getUserId(), labs);
                            //update(labs);
//                            mLabelList = labs;
//                            loadData();
                        }

                    }


                        @Override
                    public void onError(Call call, Exception e) {

                        DialogHelper.dismissProgressDialog();
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }






    @Override
    public UserListGatherHolder initHolder(ViewGroup parent) {
        View v = mInflater.inflate(R.layout.item_search_user_grid, parent, false);
        return new UserListGatherHolder(v);
    }

    @Override
    public void fillData(UserListGatherHolder holder, int position) {
        if (mUsers != null && mUsers.size() > 0) {
            UserInfo user = mUsers.get(position);
            String name;
//            if (user.getFriends() != null && !TextUtils.isEmpty(user.getFriends().getRemarkName())) {
//                // 此接口服务端不返回friends，判断还是先放这在这里吧
//                name = user.getFriends().getRemarkName();
//            } else {
                name = user.getOort_name();
                //           }
//            AvatarHelper.getInstance().displayRoundAvatar(name, user.getImuserid(), holder.ivBgImg, false);
//            AvatarHelper.getInstance().displayAvatar(name, user.getImuserid(), holder.ivHead, true);

            Glide.with(getContext()).load(user.getOort_photo()).into(holder.ivBgImg);
            Glide.with(getContext()).load(user.getOort_photo()).error(com.oortcloud.contacts.R.mipmap.default_head_portrait).into(holder.ivHead);
            holder.tvName.setText(name);
            /*String distance = DisplayUtil.getDistance(latitude, longitude, user);
            holder.tvDistance.setText(distance);*/
            holder.tvDistance.setVisibility(View.GONE);
            holder.tvTime.setText(user.getOort_depname());//TimeUtils.skNearbyTimeString(
        }
    }

    public void onItemClick(int position) {
        UserInfo user = mUsers.get(position);
        String userId = user.getImuserid();
        int fromAddType;
        if (user.getOort_name().contains(mKeyWord)) {
            fromAddType = BasicInfoActivity.FROM_ADD_TYPE_NAME;
        } else {
            // 昵称不包含关键字的话就是通过手机号搜索出来的，
            fromAddType = BasicInfoActivity.FROM_ADD_TYPE_PHONE;
        }
        //BasicInfoActivity.start(requireActivity(), userId, fromAddType);
        PersonDetailActivity.actionStart(requireActivity(),user);
    }

    class UserListGatherHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        RoundedImageView ivBgImg;
        TextView tvName;
        ImageView ivHead;
        TextView tvDistance;
        TextView tvTime;

        UserListGatherHolder(View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.ll_nearby_grid_root);
            ivBgImg = (RoundedImageView) itemView.findViewById(R.id.iv_nearby_img);
            ivBgImg.setCornerRadius(ScreenUtil.dip2px(requireContext(), 7), ScreenUtil.dip2px(requireContext(), 7), 0, 0);
            tvName = (TextView) itemView.findViewById(R.id.tv_nearby_name);
            ivHead = (ImageView) itemView.findViewById(R.id.iv_nearby_head);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_nearby_distance);
            tvTime = (TextView) itemView.findViewById(R.id.tv_nearby_time);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(getLayoutPosition());
                }
            });
        }
    }
}
