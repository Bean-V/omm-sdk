package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.AppDetailedActivity;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.appstore.widget.DownloadLoadingView;
import com.oortcloud.appstore.widget.listener.DownloadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function：模块内App
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/19 13:27
 */
public class ModuleAppInfoAdapter  extends BaseAdapter {
    private Context mContext;
    private List<AppInfo> data = new ArrayList<>();
    private GridView gv;
    private int[] mostRowHeight;
    public ModuleAppInfoAdapter( Context context, List<AppInfo> objects ) {
        mContext = context;
        data = objects;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_module_app_info_layout ,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
//            if (gv == null){
//                //获取gridview实例
//                gv = (GridView) convertView;
//                //计算行数
//                int row = getCount() / gv.getNumColumns();
//                if (getCount() % gv.getNumColumns() != 0){
//                    row += 1;
//                }
//                mostRowHeight = new int[row];
//            }
//            //缓存视图位置
//            // viewHolder.tvText.setTag(position);
//            //设置视图高度
//            viewHolder.updateHeight();

        }
        else {
            viewHolder= (ViewHolder) convertView.getTag();
        }


        final AppInfo appInfo = getItem(position);
        if (appInfo != null){

            Glide.with(mContext).load(appInfo.getIcon_url()).into((viewHolder.appIcon));
            viewHolder.appName.setText(appInfo.getApplabel());

            viewHolder.converView.setOnClickListener(view ->  {
//                AppDetailedActivity.actionStart(mContext , appInfo);

                AppInfo applyApp = DataInit.getAppinfo(appInfo.getApppackage());
                if(applyApp != null) {
                    if (applyApp.getApply_status() == 0) {
                        AppDetailedActivity.actionStart(mContext, applyApp);

                        return;
                    }
                }

                AppEventUtil.onClick(appInfo , new DownloadListener(appInfo , viewHolder.loadingView , ""));

            });
        }

        AppInfo applyApp = DataInit.getAppinfo(appInfo.getApppackage());
        convertView.setLayerType(View.LAYER_TYPE_NONE,null);
        if(applyApp != null) {
            if (applyApp.getApply_status() == 0) {
//            ColorMatrix matrix = new ColorMatrix();
//            matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);//灰度效果
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                convertView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);

                viewHolder.appApplyStatu.setVisibility(View.GONE);
            } else if (applyApp.getApply_status() == 2) {
                viewHolder.appApplyStatu.setVisibility(View.VISIBLE);
                viewHolder.appApplyStatu.setText(R.string.apply_str);
                viewHolder.appApplyStatu.setTextColor(Color.parseColor("#00BA4A"));
                viewHolder.appApplyStatu.setBackground(mContext.getResources().getDrawable(R.drawable.background_apply));
            } else if (applyApp.getApply_status() == 3) {
                viewHolder.appApplyStatu.setVisibility(View.VISIBLE);
                viewHolder.appApplyStatu.setText(R.string.review_str);
                viewHolder.appApplyStatu.setTextColor(Color.parseColor("#E36000"));
                viewHolder.appApplyStatu.setBackground(mContext.getResources().getDrawable(R.drawable.background_apply_review));
            } else if (applyApp.getApply_status() == 4) {
                viewHolder.appApplyStatu.setVisibility(View.VISIBLE);
                viewHolder.appApplyStatu.setText(R.string.reject_str);
                viewHolder.appApplyStatu.setTextColor(Color.parseColor("#E30000"));
                viewHolder.appApplyStatu.setBackground(mContext.getResources().getDrawable(R.drawable.background_apply_reject));
            } else {
                viewHolder.appApplyStatu.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    class ViewHolder{
        private final View itemView;
        View converView;
        ImageView appIcon;
        TextView appName;
        TextView appType;

        TextView appApplyStatu;
        DownloadLoadingView loadingView;

        public ViewHolder(View convertView) {
            this.converView = convertView;
            appIcon = convertView.findViewById(R.id.app_icon);
            appName = convertView.findViewById(R.id.tv_app_name);
            appApplyStatu = convertView.findViewById(R.id.tv_apply);
            appType = convertView.findViewById(R.id.tv_app_type);
            loadingView = convertView.findViewById(R.id.down_loading_view);



                LinearLayout llView;//视图项
                TextView tvText;//文字
            itemView = convertView;
            /**
             * 设置gridView的高度
             * @throws Exception
             */
        }
//        private void setGridViewHeight() throws Exception{
//            ViewGroup.LayoutParams params = gv.getLayoutParams();
//            int totalHeight = 0;
//            //计算行总高度
//            for (int i = 0; i < mostRowHeight.length; i++){
//                totalHeight += mostRowHeight[i];
//            }
//
//            Class<?> clazz=gv.getClass();
//            //利用反射，取得纵向分割线高度
//            Field horizontalSpacing = clazz.getDeclaredField("mRequestedHorizontalSpacing");
//            horizontalSpacing.setAccessible(true);
//            int horizontalBorderHeight = (Integer)horizontalSpacing.get(gv);
//            //高度等于行总高 + 行间距
//            params.height = totalHeight + horizontalBorderHeight  * (mostRowHeight.length - 1);
//            gv.setLayoutParams(params);
//        }
//
//        /**
//         * 更新视图高度
//         */
//        private void updateHeight() {
//            //当一个视图create还没有开始绘制，获取不到宽高，但是视图绘制完成，视图树的布局发生改变时，可以被ViewTreeObserver监听到
//            itemView.getViewTreeObserver().addOnGlobalLayoutListener(
//                    new ViewTreeObserver.OnGlobalLayoutListener() {
//                        public void onGlobalLayout() {
//                            //当前位置
//                            int position = (int) itemView.getTag();
//                            //当前行
//                            int curRow = position / gv.getNumColumns();
//                            //当前view高度
//                            int height = itemView.getHeight();
//                            //初始最大行高度
//                            if (mostRowHeight[curRow] == 0){
//                                mostRowHeight[curRow] = height;
//                            }else {
//                                //当前View高度小于最大行高
//                                if (height < mostRowHeight[curRow]){
//                                    //设置当前View高度=最大行高
//                                    itemView.setLayoutParams(new GridView.LayoutParams(
//                                            GridView.LayoutParams.MATCH_PARENT,
//                                            mostRowHeight[curRow]));
//                                    //设置当前View高度>最大行高
//                                }else if(height > mostRowHeight[curRow]) {
//                                    //更新最大行高
//                                    mostRowHeight[curRow] = height;
//                                    //更新所在行所有在此之前的view的行高
//                                    for (int i = curRow * gv.getNumColumns(); i < getCount() && i < position; i++){
//                                        View view = gv.getChildAt(i);
//                                        if (view.getHeight() != mostRowHeight[curRow]){
//                                            view.setLayoutParams(new GridView.LayoutParams(
//                                                    GridView.LayoutParams.MATCH_PARENT,
//                                                    mostRowHeight[curRow]));
//                                        }
//                                    }
//                                }
//                            }
//                            if (position == getCount() - 1){
//                                try {
//                                    setGridViewHeight();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    });
//        }
    }

}
