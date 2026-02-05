package com.oortcloud.contacts.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.oortcloud.contacts.R;
import com.oortcloud.contacts.utils.TKeybord;


/**
 * @ProjectName: omm-master
 * @FileName: SearchView.java
 * @Function:
 * @Author: zhangzhijun / @CreateDate: 20/03/12 03:05
 * @Version: 1.0
 */
public class SearchView extends RelativeLayout implements View.OnFocusChangeListener,TextWatcher
{
   private ImageView mSearchImg;
   private EditText mEditText;
   private ImageView mDeleteImg;

   private Context mContext;
   private View mView;
    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    public SearchView(Context context) {
        this(context , null , 0);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs ,0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.ord_search_layout,this);
        initView();


    }
    private void  initView(){
       int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;

       mSearchImg = mView.findViewById(R.id.search_img) ;
       mEditText = mView.findViewById(R.id.et_content);
       mDeleteImg = mView.findViewById(R.id.delete_img);

        mDeleteImg.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View view) {
                   clear(false);
               }
           });

        mEditText.setOnFocusChangeListener(this);
        mEditText.addTextChangedListener(this);


    }
    public void clear(boolean closeKeyBord){
        mEditText.setText("");
        if (closeKeyBord){
            mEditText.setFocusable(false);
            mEditText.setFocusableInTouchMode(true);
            TKeybord.closeKeybord( mEditText , mContext);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setClearIconVisible(charSequence.length() > 0);
        if (mListener != null){
            mListener.onSearch(charSequence);
        }
    }

    @Override
    public void afterTextChanged(Editable editable){
    }

    protected void setClearIconVisible(boolean visible) {
        if (visible){
            mDeleteImg.setVisibility(View.VISIBLE);
        }else {
            mDeleteImg.setVisibility(View.GONE);
           //Bug 隐藏键盘异常错误
        }
    }

    /**
     * search view回调方法
     */
    public interface SearchViewListener {

        /**
         * 更新自动补全内容
         *
         * @param text 传入补全后的文本
         */
//        void onRefreshAutoComplete(String text);

        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(CharSequence text);

        /**
         * 提示列表项点击时回调方法 (提示/自动补全)
         */
//        void onTipsItemClick(String text);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

//        if (hasFocus){
//            TKeybord.openKeybord((EditText) v , mContext);
//
//        }else {
//            TKeybord.closeKeybord((EditText) v , mContext);
//        }

    }
}

