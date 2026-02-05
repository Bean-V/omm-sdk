package com.oort.weichat.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.NewsCommentRes;
import com.oort.weichat.fragment.entity.OORTGANews;
import com.oort.weichat.fragment.entity.ResObj;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.TimeUtils;
import com.oortcloud.appstore.adapter.BaseRecyclerViewAdapter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.login.net.utils.RxBus;

public class HomeActivityReplyDetail extends BaseActivity {

    private int mTid;
    private WebView mWebView;
    private RecyclerView rv_comment;
    private CommentAdpter commentAdp;
    private NewsCommentRes.DataBean.ListsBean mTid1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_reply_detail);


        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText("回复详情");
        ImageView iv_left = (ImageView) findViewById(R.id.iv_title_left);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTid1 = getIntent().getParcelableExtra("comment");
        String name = getIntent().getStringExtra("name");

        if(mTid1 != null) {
            initView();
            initClient();
            initEvent();;
            loadData();
        }



    }


    private void initView() {
        rv_comment = findViewById(R.id.rv_comment);
        commentAdp = new CommentAdpter(this);
        rv_comment.setAdapter(commentAdp);
    }

    private void initClient() {

    }

    private void initEvent() {

    }
    private void loadData() {
        String uuid= UserInfoUtils.getInstance(this).getUserId();
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");

        HttpRequestParam.reply_list_second(mToken,1,uuid,mTid1.getReply_id()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                    ResObj<OORTGANews> res = JSON.parseObject(s,new TypeToken<ResObj<OORTGANews>>() {}.getType());//
                    if(res.getCode() == 200 && res.getData() != null){


                       TextView time = findViewById(R.id.tv_time);
                       time.setText(res.getData().getTime());

                        TextView title = findViewById(R.id.tv_title);
                        title.setText(res.getData().getTitle());

                        String content = res.getData().getContent();

                        content = content.replace("<img", "<img style=\"max-width:100%;height:auto;margin:0 auto;display: flex;\"");

                        mWebView.loadDataWithBaseURL(Constant.BASE_URL,content,"text/html","utf-8",null);


                    }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                //mRefreshLayout.closeHeaderOrFooter();
            }
        });


        HttpRequestParam.reply_list(mToken,1,uuid, String.valueOf(mTid)).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                NewsCommentRes res = JSON.parseObject(s,NewsCommentRes.class);//
                if(res.getCode() == 200 && res.getData() != null && res.getData().getLists() != null){

                    commentAdp.setData(res.getData().getLists());

                     TextView tv = findViewById(R.id.tv_allcomment);
                     tv.setText("全部评论(" + res.getData().getLists().size() +")");

                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                //mRefreshLayout.closeHeaderOrFooter();
            }
        });
    }

    public static void start(Context context,NewsCommentRes.DataBean.ListsBean bean) {
        Intent starter = new Intent(context, HomeActivityReplyDetail.class);
        starter.putExtra("comment",bean);
        context.startActivity(starter);
    }




    public class CommentAdpter extends BaseRecyclerViewAdapter<NewsCommentRes.DataBean.ListsBean> {

        public CommentAdpter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.item_home_news_reply_list_item_layout, parent , false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

            final ViewHolder holder = (ViewHolder) viewHolder;

            final NewsCommentRes.DataBean.ListsBean info = lists.get(position);
            holder.name.setText(info.getName());
            holder.content.setText(info.getContent());
            holder.time.setText(TimeUtils.getFriendlyTimeDesc(mContext, (int) info.getCreated_on()));


            ImageLoader.loadImage(holder.headerIcon,info.getPortrait(), com.oortcloud.contacts.R.mipmap.default_head_portrait);


            holder.reply.setOnClickListener(view ->  {


                if(info != null) {
                    if(1 == 0){


                    }

                }

            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView headerIcon;
            TextView name;
            TextView content;

            TextView time;

            Button reply;
            public ViewHolder( View itemView) {
                super(itemView);
                headerIcon = itemView.findViewById(R.id.iv_head_icon);
                name = itemView.findViewById(R.id.tv_name);
                content = itemView.findViewById(R.id.tv_content);
                time = itemView.findViewById(R.id.tv_time);
                reply = itemView.findViewById(R.id.btn_replay);

            }
        }

    }
}