package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.Comment;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.bean.UserInfo;
import com.oortcloud.appstore.databinding.ItemCommentLayoutBinding;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.StringTimeUtils;
import com.oortcloud.appstore.widget.RatingBarView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/6/30 11:04
 */
public class CommentAdapter extends BaseRecyclerViewAdapter<Comment> {
    LayoutInflater mInflater;
    public CommentAdapter(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_comment_layout , parent , false);

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Comment comment = lists.get(position);
        if (!TextUtils.isEmpty(comment.getPortrait())){
            Glide.with(mContext).load(comment.getPortrait()).error(R.mipmap.default_head_portrait).into((viewHolder.photo));
        }
        getCommentUserName(viewHolder.name  , comment.getUuid());
        viewHolder.ratingBar.setStar(comment.getScore());
        viewHolder.content.setText(comment.getContent());
        Date date = new Date(comment.getCreated_on() *1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        viewHolder.date.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
        viewHolder.commentDate.setText(StringTimeUtils.formatSomeAgo(simpleDateFormat.format(date),mContext));
        viewHolder.ratingBar.setStar(comment.getScore());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCommentLayoutBinding binding; // 对应布局 item_comment.xml

        ImageView photo;
        TextView name;
        RatingBarView ratingBar;
        TextView date;
        TextView content;
        TextView commentDate;
        RecyclerView commentRV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCommentLayoutBinding.bind(itemView); // 初始化 ViewBinding

            // 通过 ViewBinding 初始化视图变量
            photo = binding.imgHeadPortait;
            name = binding.tvUserName;
            ratingBar = binding.ratingBarView;
            date = binding.tvDate;
            content = binding.tvCommentContent;
            commentDate = binding.tvCommentDate;
            commentRV = binding.rvTwoLevelComment;
        }

        // 可选：添加数据绑定方法

    }

    private void getCommentUserName(TextView name , String uuid){
        HttpRequestCenter.getUserInfo(uuid).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                Result<Data<UserInfo>> result = new Gson().fromJson(s,new TypeToken<Result<Data<UserInfo>>>(){}.getType());

                if (result.isok()){
                    name.setText(result.getData().getUserInfo().getOort_name());
                }
            }
        });
    }
}
