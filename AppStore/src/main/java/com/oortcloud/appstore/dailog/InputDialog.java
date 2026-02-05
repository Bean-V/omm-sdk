package com.oortcloud.appstore.dailog;

import static com.oortcloud.appstore.AppStoreInit.MODNAME_LEN;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oortcloud.appstore.R;
import com.oortcloud.appstore.utils.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @filename:
 * @function：   自定义应用模块对话框
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/13 20:32
 */
public class InputDialog extends Dialog{

    private Context mContext;
    /**
     * UI
     */
    private ViewGroup contentView;
    private TextView titleView;
    private EditText contentEdit;
    private TextView btnComfirm;
    private TextView btnCancle;

    public interface DialogClickListener {
         void onDialogClick();
         void onDialogClick(String moduleName);
    }

    public InputDialog( Context context,
                         DialogClickListener confirmClick, DialogClickListener cancelClick) {
        this(context , null , null ,confirmClick , cancelClick );

    }
    public InputDialog(Context context, String title, String content,
                        DialogClickListener confirmClick, DialogClickListener cancelClick) {
        super(context, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog);
        mContext = context;
        initDialogStyle(title, content, confirmClick, cancelClick);
    }


    private void initDialogStyle(String title, final String content,
                                 final DialogClickListener confirmClick, final DialogClickListener cancelClick) {
        setContentView(createDialogView(R.layout.dialog_input_layout));
//        setParams(dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        titleView = (TextView) findChildViewById(R.id.title);
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }

        contentEdit = (EditText) findChildViewById(R.id.message);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        btnComfirm = (TextView) findChildViewById(R.id.confirm_btn);
        btnCancle = (TextView) findChildViewById(R.id.cancle_btn);

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (cancelClick != null) {
                    cancelClick.onDialogClick();
                }
            }
        });
        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmClick != null) {
                   String contentStr =   contentEdit.getText().toString();

                   if (!TextUtils.isEmpty(contentStr)){


                       Pattern pattern = Pattern.compile("^(\\d+)(.*)");
                       Matcher matchers = pattern.matcher(contentStr);

                       String regEx="^[0-9A-Za-z\\u4e00-\\u9fa5]+$";//字符串中只有数字英文和汉字
                       Pattern p = Pattern.compile(regEx);
                       Matcher matcher =p.matcher(contentStr);

                       if (matchers.matches()){

                           ToastUtils.showBottom(mContext.getString(R.string.module_name_not_pre_num));

                       }else if(!contentStr.matches(regEx)){

                           ToastUtils.showBottom(mContext.getString(R.string.module_name_not_contain_spical_char));
                       }
                       else if (contentStr.length() > MODNAME_LEN){
                           ToastUtils.showBottom(mContext.getString(R.string.module_name_lenght_not_use,String.valueOf(MODNAME_LEN)) );
                       }else {
                           dismiss();
                           confirmClick.onDialogClick(contentStr);
                       }


                   }else {
                       Toast.makeText(mContext , mContext.getString(R.string.please_input_module_name) ,Toast.LENGTH_SHORT).show();
                   }

                }

            }
        });
    }
    private ViewGroup createDialogView(int layoutId) {
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    public void setParams(int width, int height) {
        WindowManager.LayoutParams dialogParams = this.getWindow().getAttributes();
        dialogParams.width = width;
        dialogParams.height = height;
        this.getWindow().setAttributes(dialogParams);
    }

    public View findChildViewById(int id) {
        return contentView.findViewById(id);
    }

}
