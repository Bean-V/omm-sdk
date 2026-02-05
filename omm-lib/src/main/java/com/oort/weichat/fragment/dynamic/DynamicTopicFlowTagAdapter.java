package com.oort.weichat.fragment.dynamic;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.DynamicTopic;
import com.xuexiang.xui.widget.flowlayout.BaseTagAdapter;

/**
 * @author xuexiang
 * @date 2017/11/21 上午10:44
 */
public class DynamicTopicFlowTagAdapter extends BaseTagAdapter<DynamicTopic, TextView> {

    public DynamicTopicFlowTagAdapter(Context context) {
        super(context);
    }

    @Override
    protected TextView newViewHolder(View convertView) {
        return (TextView) convertView.findViewById(R.id.tv_name);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_dymaic_topic_layout;
    }

    @Override
    protected void convert(TextView textView, DynamicTopic item, int position) {
        textView.setText(item.getOort_name());
    }
}
