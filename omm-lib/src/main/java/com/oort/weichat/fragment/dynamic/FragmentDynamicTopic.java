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
import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.DynamicTopic;
import com.oort.weichat.fragment.entity.ResArr;
import com.oort.weichat.ui.base.EasyFragment;
import com.oortcloud.appstore.adapter.BaseRecyclerViewAdapter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.login.net.utils.RxBus;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDynamicTopic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDynamicTopic extends EasyFragment {

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
        return R.layout.fragment_dynamic_topic;
    }


    private SmartRefreshLayout mRefreshLayout;
    private SwipeRecyclerView mListView;
    private TopicAdpter mAdapter;

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mListView = findViewById(R.id.recyclerView);
        mListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRefreshLayout = findViewById(R.id.refreshLayout);
        // mListView.addHeaderView(mHeadView);
        mAdapter = new TopicAdpter(mContext);
        mListView.setAdapter(mAdapter);
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
    }

    private void requestData(boolean isPullDownToRefresh) {


        String uuid = UserInfoUtils.getInstance(getContext()).getUserId();
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token", "");

        HttpRequestParam.dynamic_topic_list(mToken, 1).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                ResArr<DynamicTopic> res = JSON.parseObject(s, new TypeToken<ResArr<DynamicTopic>>() {
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


                    if(res.getData().getList().size() == 0){
                        showEmpty();
                    }else {
                        hideEmpty();
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


    public class TopicAdpter extends BaseRecyclerViewAdapter<DynamicTopic> {

        public TopicAdpter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.item_dymaic_topic_item_layout, parent , false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

            final ViewHolder holder = (ViewHolder) viewHolder;

            final DynamicTopic info = lists.get(position);
            holder.name.setText(info.getOort_name());


            holder.des.setText(info.getOort_dcount() + mContext.getString(R.string.moments_page_str));


            holder.itemView.setOnClickListener(view ->  {



              DynamicActivityTopicList.start(mContext,info.getOort_tuuid(),info.getOort_name());

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