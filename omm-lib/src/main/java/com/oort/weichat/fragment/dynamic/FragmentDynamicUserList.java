package com.oort.weichat.fragment.dynamic;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.BuildConfig;
import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.DynamicUser;
import com.oort.weichat.fragment.entity.ResArr;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.util.ViewPiexlUtil;
import com.oort.weichat.view.PullRefreshFooter;
import com.oortcloud.appstore.adapter.BaseRecyclerViewAdapter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeRecyclerView;
import com.oortcloud.login.net.utils.RxBus;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDynamicUserList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDynamicUserList extends EasyFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int pageIndex;
    private SwipeRecyclerView mListView1;


    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_dynamic_user_list;
    }


    private SmartRefreshLayout mRefreshLayout;
    private SwipeRecyclerView mListView;
    private UserAdpter mAdapter;

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    private int contentType = 1;


    public String getOort_userid() {
        return oort_userid;
    }

    public void setOort_userid(String oort_userid) {
        this.oort_userid = oort_userid;
    }

    private String oort_userid;


    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mListView = findViewById(R.id.recyclerView);
        mListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRefreshLayout = findViewById(R.id.refreshLayout);
        // mListView.addHeaderView(mHeadView);
        mAdapter = new UserAdpter(mContext);
        mListView.setAdapter(mAdapter);

        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(true); // 数据不足一屏时显示 Footer
        mRefreshLayout.setEnableLoadMore(true); // 启用加载更多
        mRefreshLayout.setEnableFooterFollowWhenLoadFinished(true); // Footer 始终可见
        mRefreshLayout.finishLoadMoreWithNoMoreData(); // 数据加载完成时显示“加载完成”

        ClassicsFooter footer = new ClassicsFooter(mContext);
        footer.setFinishDuration(0); // 加载完成后不自动隐藏 Footer
        mRefreshLayout.setRefreshFooter(footer);

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            pageIndex = 1;
            requestData(true);
        });
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            requestData(false);
        });

        //EventBus.getDefault().register(this);

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int totalScroll;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        requestData(true);


//        mListView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            int itemCount = mAdapter.getItemCount();
//            int itemHeight = ViewPiexlUtil.dp2px(getContext(),72);
//            int totalHeight = itemCount * itemHeight;
//
//            if (totalHeight < mListView.getHeight()) {
//                ViewGroup.LayoutParams params = mListView.getLayoutParams();
//                params.height = totalHeight;
//                mListView.setLayoutParams(params);
//            }else {
//                // 数据多于一屏，使用默认高度
//                ViewGroup.LayoutParams params = mListView.getLayoutParams();
//                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                mListView.setLayoutParams(params);
//            }
//        });




    }

    private void requestData(boolean isPullDownToRefresh) {


        String uuid = UserInfoUtils.getInstance(getContext()).getUserId();
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token", "");



        if(contentType == 1) {

            HttpRequestParam.dynamic_followlist(mToken, oort_userid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    ResArr<DynamicUser> res = JSON.parseObject(s, new TypeToken<ResArr<DynamicUser>>() {
                    }.getType());//
                    if (res.getCode() == 200 && res.getData() != null && res.getData().getList() != null) {


                        mRefreshLayout.finishRefresh();
                        if (res.getData().getPage() == res.getData().getPages()) {
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        } else {
                            mRefreshLayout.finishLoadMore();
                            pageIndex++;
                        }


                        mAdapter.setData(res.getData().getList());


                        if (res.getData().getList().size() == 0) {
                            showEmpty();
                        } else {
                            hideEmpty();
                            mRefreshLayout.post(() -> {
//                                View footer01 = mRefreshLayout.getRefreshFooter().getView();
//                                RecyclerView recyclerView = findViewById(R.id.recyclerView);
//
//                                // 获取 RefreshLayout 总高度和 Footer 高度
//                                int totalHeight = mRefreshLayout.getHeight();
//                                int footerHeight = footer01.getMeasuredHeight();
//
//                                // 设置 RecyclerView 的高度，减去 Footer 的高度
//                                ViewGroup.LayoutParams params = mRefreshLayout.getLayoutParams();
//                                params.height = totalHeight + footerHeight;
//                                mRefreshLayout.setLayoutParams(params);
                                int itemCount = mAdapter.getItemCount();
                                int itemHeight = ViewPiexlUtil.dp2px(getContext(),82);
                                int totalHeight = itemCount * itemHeight;
                                PullRefreshFooter f = findViewById(R.id.footer);


                                int h = mListView.getHeight();
                                int h1 = mRefreshLayout.getHeight();


                                if (totalHeight <= mListView.getHeight()) {
                                    {
                                        ViewGroup.LayoutParams params = mListView.getLayoutParams();
                                        params.height = totalHeight;
                                        mListView.setLayoutParams(params);
                                    }
                                    {
                                        ViewGroup.LayoutParams params = mRefreshLayout.getLayoutParams();
                                        params.height = totalHeight + ViewPiexlUtil.dp2px(getContext(),64);
                                        mRefreshLayout.setLayoutParams(params);
                                    }
                                }else {
                                    // 数据多于一屏，使用默认高度
                                    ViewGroup.LayoutParams params = mListView.getLayoutParams();
                                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                    mListView.setLayoutParams(params);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg", e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });
        }

        if(contentType == 2) {

            HttpRequestParam.dynamic_fanslist(mToken, oort_userid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    ResArr<DynamicUser> res = JSON.parseObject(s, new TypeToken<ResArr<DynamicUser>>() {
                    }.getType());//
                    if (res.getCode() == 200 && res.getData() != null && res.getData().getList() != null) {


                        mRefreshLayout.finishRefresh();
                        if (res.getData().getPage() == res.getData().getPages()) {
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        } else {
                            mRefreshLayout.finishLoadMore();
                            pageIndex++;
                        }

                        if(BuildConfig.DEBUG){
                            ArrayList ls = new ArrayList();
                            ls.addAll(res.getData().getList());
                            ls.addAll(res.getData().getList());
                            ls.addAll(res.getData().getList());
                            mAdapter.setData(ls);
                        }else {

                            mAdapter.setData(res.getData().getList());
                        }


                        if (res.getData().getList().size() == 0) {
                            showEmpty();
                        } else {
                            hideEmpty();
                            mRefreshLayout.post(() -> {
//                                View footer01 = mRefreshLayout.getRefreshFooter().getView();
//                                RecyclerView recyclerView = findViewById(R.id.recyclerView);
//
//                                // 获取 RefreshLayout 总高度和 Footer 高度
//                                int totalHeight = mRefreshLayout.getHeight();
//                                int footerHeight = footer01.getMeasuredHeight();
//
//                                // 设置 RecyclerView 的高度，减去 Footer 的高度
//                                ViewGroup.LayoutParams params = mRefreshLayout.getLayoutParams();
//                                params.height = totalHeight + footerHeight;
//                                mRefreshLayout.setLayoutParams(params);
                                int itemCount = mAdapter.getItemCount();
                                int itemHeight = ViewPiexlUtil.dp2px(getContext(),82);
                                int totalHeight = itemCount * itemHeight;

                                PullRefreshFooter f = findViewById(R.id.footer);


                                if (totalHeight <= mListView.getHeight()) {
                                    {
                                        ViewGroup.LayoutParams params = mListView.getLayoutParams();
                                        params.height = totalHeight;
                                        mListView.setLayoutParams(params);
                                    }{
                                        ViewGroup.LayoutParams params = mRefreshLayout.getLayoutParams();
                                        params.height = totalHeight + ViewPiexlUtil.dp2px(getContext(),64);
                                        mRefreshLayout.setLayoutParams(params);
                                    }
                                }else {
                                    // 数据多于一屏，使用默认高度
                                    ViewGroup.LayoutParams params = mListView.getLayoutParams();
                                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                    mListView.setLayoutParams(params);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg", e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });
        }
    }


    public class UserAdpter extends BaseRecyclerViewAdapter<DynamicUser> {

        public UserAdpter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.item_dymaic_user_item_layout, parent , false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

            final ViewHolder holder = (ViewHolder) viewHolder;

            final DynamicUser info = lists.get(position);


            ImageLoader.loadImage(holder.fileIcon,info.getOort_photo(),com.oortcloud.contacts.R.mipmap.default_head_portrait);
            holder.name.setText(info.getOort_name());


            holder.des.setText(info.getOort_depname());


            holder.itemView.setOnClickListener(view ->  {



              DynamicActivityUserHome.start(mContext,info.getOort_userid());

            });

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView fileIcon;
            TextView name;
            TextView des;
            public ViewHolder( View itemView) {
                super(itemView);
                fileIcon = itemView.findViewById(R.id.iv_file_icon);
                name = itemView.findViewById(R.id.tv_name);
                des = itemView.findViewById(R.id.tv_des);

            }
        }

    }








}