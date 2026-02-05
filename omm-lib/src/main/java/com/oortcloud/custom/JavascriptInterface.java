package com.oortcloud.custom;

import android.content.Context;
import android.content.Intent;

import com.oort.weichat.AppConstant;
import com.oort.weichat.ui.tool.SingleImagePreviewActivity;

public class JavascriptInterface {
    private Context context;

    public JavascriptInterface(Context context) {
        this.context = context;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent(context, SingleImagePreviewActivity.class);
        intent.putExtra(AppConstant.EXTRA_IMAGE_URI, img);
        context.startActivity(intent);
    }
}


