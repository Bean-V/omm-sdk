package com.oortcloud.oort_zhifayi;

import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.login.http.HttpRequestParam;
import com.oortcloud.basemodule.login.http.RxBus;
import com.oortcloud.basemodule.login.okhttp.HttpUtils;
import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.ToastUtil;

import java.io.File;
import java.util.HashMap;

public class ActivityPreview extends ActivityBase {

    private String filePath;
    private TextView et;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        try {
            Intent in = getIntent();
            String s = in.getStringExtra("picPath");
            Log.e("ceshi",s);

            if(s == null){
                finish();
                return;
            }

            filePath = s;

            Bitmap bitmap = BitmapFactory.decodeFile(s);
            Log.e("ceshi",bitmap.toString());
            ImageView iv = findViewById(R.id.iv_image);
            //iv.setImageBitmap(bitmap);

            Glide.with(this).load(new File(s)).into(iv);
        }catch (Exception e){
            Log.e("ceshi",e.toString());
        }

        Log.e("ceshi","tv_retake");
        findViewById(R.id.tv_retake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et = findViewById(R.id.et_remark);


        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPreview.this);
                builder.setTitle("");
                // 添加选项
                final String[] options = {"市容环境","安全生产","综治维稳","其他"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 处理选项点击事件
                        String selectedOption = options[which];
                        et.setText(selectedOption);
                    }
                });
                // 显示对话框
                builder.show();
            }
        });
        findViewById(R.id.tv_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//
//                if(Constant.IsDebbug) {
//
//                    startActivity(new Intent(ActivityPreview.this,ActivitySubmit.class));
//                    finish();
//                    return;
//                }

//
//
//                HashMap paras = {"accessToken": "accessToken",
//                        "describe": "每周街道巡更任务",
//                        "id": "1",
//                        "pics": [
//                    "http://com/test.jpg"
//  ],
//                    "point": {
//                    "address": "深圳市福田区松岭路57号",
//                            "lat": 22.71991,
//                            "lng": 114.24779
//                }
//                }


                progressDialog = new ProgressDialog(ActivityPreview.this);
                progressDialog.setMessage("加载中..."); // 设置对话框显示的文字
                progressDialog.setCancelable(false); // 设置是否可以通过点击对话框外部取消对话框
// 显示对话框
                progressDialog.show();
                ImageUtils.compressAndReplace(filePath);

                                // 压缩成功后调用，compressedFile 为压缩后的图片文件
                                // 可以将 compressedFile 用于上传等操作

                String des = et.getText().toString();
//                String [] pics = new String []{f.getPath()};
                HashMap point = new HashMap();
                point.put("address",ReportInfo.elements);//ReportInfo.elements
                point.put("lat",ReportInfo.latitude);
                point.put("lng",ReportInfo.longitude);

                HashMap paras = new HashMap();
                paras.put("accessToken", IMUserInfoUtil.getInstance().getToken());
                paras.put("describe",des);;
                //paras.put("id",CUser.taskId);
//                paras.put("pics", pics);//JSON.toJSONString(pics)

                paras.put("point",JSON.toJSONString(point));//JSON.toJSONString(point)
                HttpUtils.post()
                        .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/mytask_updata")
                        .params(paras)
                        .build(false,true)
                        .execute(new BaseCallback<XGEvent>(XGEvent.class) {

                            @Override
                            public void onResponse(ObjectResult<XGEvent> result) {
                                if(result.getCode() == 200){
                                    XGEvent event = result.getData();
                                    uploadFile(new File(filePath),event.getId(),des,ReportInfo.elements, new HttpRequestParam.Callback() {
                                        @Override
                                        public void sucCallback(Object o) {
                                            progressDialog.dismiss();
                                            startActivity(new Intent(ActivityPreview.this,ActivitySubmit.class));
                                            finish();
                                        }
                                        @Override
                                        public void failCallback(Object o) {
                                            progressDialog.dismiss();
                                            ToastUtil.showLongToast(ActivityPreview.this,o.toString());
                                        }
                                    });


                                }else{
                                    progressDialog.dismiss();
                                    ToastUtil.showLongToast(ActivityPreview.this,result.getMsg());
                                }


                            }

                            @Override
                            public void onError(okhttp3.Call call, Exception e) {
                                progressDialog.dismiss();
                                ToastUtil.showLongToast(ActivityPreview.this,e.getLocalizedMessage());
                            }
                        } );









            }
        });
        Log.e("ceshi","tv_post");



    }
    void uploadFile(File file,String event_id,String mark,String address, HttpRequestParam.Callback callback){


        String uuid= UserInfoUtils.getInstance(this).getUserId();
        String mToken = ZFYConstant.testToken;

        HttpRequestParam.uploadfile(mToken,file,event_id,mark,address).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                    ResObj<OORTFile> res = JSON.parseObject(s,new TypeToken<ResObj<OORTFile>>() {}.getType());//
                    if(res.getCode() == 200 && res.getData() != null){
                        if(callback != null){
                            callback.sucCallback(res.getData());
                        }
                    }else{
                        if(callback != null){
                            callback.failCallback(res.getMsg());
                        }

                        //XToast.success(DynamicSendActivity.this,res.getMsg());
                    }

            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());

                if(callback != null){
                    callback.failCallback(e.toString());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}

































//    uploadFile(new File(filePath), new HttpRequestParam.Callback() {
//@Override
//public void sucCallback(Object o) {
//
//        OORTFile f = (OORTFile) o;
//
//
//        String url = Constant.BASE_URL + "multi/apaas-location-service/file/" + Constant.testToken + "/" + f.getPath() + "?download=1";
//
//        Log.d("ceshi",url);
//        //String [] pics = new String []{f.getUrl()};
//
//        String des = et.getText().toString();
//        String [] pics = new String []{f.getPath()};
//        HashMap point = new HashMap();
//        point.put("address",ReportInfo.elements);//ReportInfo.elements
//        point.put("lat",ReportInfo.latitude);
//        point.put("lng",ReportInfo.longitude);
//
//        HashMap paras = new HashMap();
//        paras.put("accessToken",Constant.testToken);
//        paras.put("describe",des);;
//        paras.put("id",CUser.taskId);
//        paras.put("pics", pics);//JSON.toJSONString(pics)
//
//        paras.put("point",point);//JSON.toJSONString(point)
//        HttpUtils.post()
//        .url(Constant.BASE_URL + "multi/apaas-location-service/task/v1/mytask_updata")
//        .params(paras)
//        .build(false,true)
//        .execute(new BaseCallback<HashMap>(HashMap.class) {
//
//@Override
//public void onResponse(ObjectResult<HashMap> result) {
//        progressDialog.dismiss();
//        if(result.getCode() == 200){
//        HashMap dictionary = result.getData();
//        startActivity(new Intent(ActivityPreview.this,ActivitySubmit.class));
//        finish();
//        }else{
//        progressDialog.dismiss();
//        ToastUtil.showLongToast(ActivityPreview.this,result.getMsg());
//        }
//
//
//        }
//
//@Override
//public void onError(okhttp3.Call call, Exception e) {
//        progressDialog.dismiss();
//        ToastUtil.showLongToast(ActivityPreview.this,e.getLocalizedMessage());
//        }
//        } );
//
//
//
//
//        }
//
//@Override
//public void failCallback(Object o) {
//        progressDialog.dismiss();
//        }
//        });