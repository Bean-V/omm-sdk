package com.oortcloud.oort_zhifayi;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.login.okhttp.HttpUtils;
import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.utils.DeviceIdFactory;
import com.oortcloud.basemodule.utils.DeviceGPSUtils;
import com.oortcloud.basemodule.utils.PermissionUtil;
import com.oortcloud.basemodule.utils.ToastUtil;
import com.oortcloud.oort_zhifayi.event.MessageEvent;
//import com.runhua.application.OORTSDKTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ActivityTasks extends ActivityBase {

    private RecyclerView recyclerView;
    private String[] neededPermissions;
    private Thread postionThread;
    private GXReport mReport;
    private long startTimeval;

    private long endTimeval;

    private long ckeyCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);


        findViewById(R.id.iv_user_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                checkpoint();
//                getData();
//                getUserInfo();
//                Intent intent1 = new Intent(ActivityTasks.this, ActivityScrren.class);
//                startActivity(intent1);
                startActivity(new Intent(ActivityTasks.this, ZXMainActivity.class));

            }
        });
        findViewById(R.id.btn_logOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
 //               checkpoint();
//                getData();
//                getUserInfo();


                ReportInfo.lastlatitude = 0;
                ReportInfo.lastlongitude = 0;
                if(ZFYConstant.testToken.isEmpty()){
                    finish();

                    return;
                }
                getUserLogout();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        // 设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 创建适配器并设置给RecyclerView

        //getData();

        getUserInfo();

        keyReciever();

        //getPostPostion();


        MediaPlayer player = MediaPlayer.create(this, R.raw.audio_start_task);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 播放完成后执行逻辑，例如停止播放或者释放资源
                 mediaPlayer.stop(); // 如果您希望停止播放
                 mediaPlayer.release(); // 如果您希望释放 MediaPlayer 对象
            }
        });
        // player.start();
        if(ZFYConstant.IsDebbug) {
            player.start();
        }

        EventBus.getDefault().register(this);


//        OORTSDKTool.startPoc(this);
    }


    private XGTask mTask;


    void getData(){
        HashMap<String ,Object > paras = new HashMap();
        paras.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        paras.put("keyword", "");
        paras.put("pagesize", String.valueOf(1));
        paras.put("page", String.valueOf(1));
        paras.put("status", String.valueOf(1));//1:已完成 2:待处理 0:全部
        HttpUtils.post()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/mytask_list")
                .params(paras)
                .build(false,true)
                .execute(new BaseCallback<String>(String.class) {
                    @Override
                    public void onResponse(ObjectResult<String> result) {

                        Log.e("ceshi","mytask_list--" + result.toString());
                        if(result.getCode() == 200) {
//                            mTask = result.getData();
//
//                            try {
//                                recyclerView.setAdapter(new ItemAdapter(mTask, new OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(Object item, int index) {
//                                        //XGTask.ListDTO it = (XGTask.ListDTO) item;
//                                    }
//                                }));
//
//                                try {
//
//                                    CUser.taskId = mTask.getList().get(0).getId();
//
//
//                                    ((TextView) findViewById(R.id.tv_xc_count)).setText(String.valueOf(mTask.getAllPoint().size()));
//                                    getData_report();
//                                }catch (Exception e){
//
//                                }
//
//
//                            }catch (Exception e){
//                                Log.e("ceshi",e.toString());
//                            }
                        }else{
                            ToastUtil.showLongToast(ActivityTasks.this,result.getMsg());
                        }

                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Exception e1 = e;
                        Log.e("ceshi",e.toString());
                        ToastUtil.showLongToast(ActivityTasks.this,e.getLocalizedMessage());
                    }
                });
    }

    void getData_report(){
        HashMap paras = new HashMap();
        paras.put("accessToken", ZFYConstant.testToken);
        paras.put("id",mTask.getList().get(0).getId());


        ///task/v1/mytask_report_info
        HttpUtils.post()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/mytask_report_info")
                .params(paras)
                .build(false,true)
                .execute(new BaseCallback<GXReport>(GXReport.class) {
                    @Override
                    public void onResponse(ObjectResult<GXReport> result) {
                        if(result.getCode() == 200) {
                            mReport = result.getData();


                        changeCheckPoint();

                        upDatePos();

                            recyclerView.setAdapter(new ItemAdapter(mTask, new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object item, int index) {
                                    //XGTask.ListDTO it = (XGTask.ListDTO) item;
                                }
                            }));

                        }else{
                            ToastUtil.showLongToast(ActivityTasks.this,result.getMsg());
                        }

                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Exception e1 = e;
                        ToastUtil.showLongToast(ActivityTasks.this,e.getLocalizedMessage());
                    }
                });
    }
    private boolean isVisible = false;
    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;

        boolean needRefresh  = true;
        try{
            boolean res =  mTask.getList().size() > 0;
            needRefresh  = false;
        }catch (Exception e){
        }

        if(needRefresh && !ZFYConstant.testToken.isEmpty()){
            getData();
            getUserInfo();
        }




    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    public boolean isActivityVisible() {
        return isVisible;
    }


    void getUserLogout(){
        HashMap paras = new HashMap();
        paras.put("accessToken", IMUserInfoUtil.getInstance().getUserId());//1:已完成 2:待处理 0:全部

        ///task/v1/myinfo/task/v1/qrcode_logout

        HttpUtils.post()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/qrcode_logout")
                .params(paras)
                .build(false,true)
                .execute(new BaseCallback<CUser>(CUser.class) {

                    @Override
                    public void onResponse(ObjectResult<CUser> result) {
                        if(result.getCode() == 200) {
                            CUser.cUser = null;
                            ZFYConstant.testToken = "";
                            CUser.taskCount = 0;
                            CUser.taskId  = "";

                            finish();
                        }else{
                            ToastUtil.showLongToast(ActivityTasks.this,result.getMsg());
                        }

                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Exception e1 = e;
                        ToastUtil.showLongToast(ActivityTasks.this,e.getLocalizedMessage());
                    }
                });
    }

    void getUserInfo(){
        HashMap paras = new HashMap();
        paras.put("accessToken", IMUserInfoUtil.getInstance().getToken());//1:已完成 2:待处理 0:全部

        ///task/v1/myinfo

        HttpUtils.post()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/myinfo")
                .params(paras)
                .build(false,true)
                .execute(new BaseCallback<CUser>(CUser.class) {
                    @Override
                    public void onResponse(ObjectResult<CUser> result) {
                        Log.e("ceshi",result.toString());
                        if(result.getCode() == 200 && !ActivityTasks.this.isDestroyed()) {
                            CUser.cUser = result.getData();
                            if(CUser.cUser !=  null ){
                                Glide.with(ActivityTasks.this).load(CUser.cUser.getUserinfo().getPhoto()).
                            placeholder(R.mipmap.defaultheader).into((ImageView) findViewById(R.id.iv_user_header));

                                ((TextView)findViewById(R.id.tv_user_name)).setText(CUser.cUser.getUserinfo().getUser_name().isEmpty() ? "未设置" : CUser.cUser.getUserinfo().getUser_name());

                                String dptName = "";
                                if(CUser.cUser.getUserinfo().getDept_list().size() > 0){
                                    CUser.UserinfoBean.DeptListBean listBean = CUser.cUser.getUserinfo().getDept_list().get(0);
                                    dptName = listBean.getDeptinfo().getDept_name();
                                }
                                ((TextView)findViewById(R.id.tv_user_depart)).setText(dptName);

                                ((TextView)findViewById(R.id.tv_eventcount)).setText(String.valueOf(CUser.cUser.getEvent_count()));
                            }

                        }else{
                            ToastUtil.showLongToast(ActivityTasks.this,result.getMsg());
                        }

                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Log.e("ceshi","e----" + e.toString());
                        Exception e1 = e;
                        ToastUtil.showLongToast(ActivityTasks.this,e.getLocalizedMessage());
                    }
                });
    }
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private List<XGTask.ListDTO.PointsDTO> itemList;
        private OnItemClickListener listener;

        public ItemAdapter(XGTask mTask, OnItemClickListener listener) {
            if(mTask == null){
                this.itemList = new ArrayList<>();
            }else {

                List<XGTask.ListDTO> tasks = mTask.getList();
                this.itemList = new ArrayList<>();
                if(tasks != null) {
                    List l = new ArrayList();
                    for (XGTask.ListDTO task : tasks) {
                        l.addAll(task.getPoints());
                    }

                    this.itemList.addAll(l);
                }

            }
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = itemList.get(position).getAddress() + "  " + itemList.get(position).getDistance_();
            holder.textView.setText(item);
            holder.textView.setTextColor(itemList.get(position).isCheck() ? Color.GREEN : Color.BLACK);

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(itemList.get(position),position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }
        }

        public void refresh(){


            notifyDataSetChanged();

        }

    }
    public interface OnItemClickListener {
        void onItemClick(Object item,int index);
    }


    void keyReciever(){
        // 创建一个新的IntentFilter来注册你感兴趣的广播类型

        neededPermissions = new String[]{

                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_PHONE_STATE,



        };
        if(PermissionUtil.checkSelfPermissions(ActivityTasks.this,neededPermissions)) {


        }else {
            PermissionUtil.requestPermissions(ActivityTasks.this,10009,neededPermissions);
        }

        IntentFilter filter = new IntentFilter("android.intent.action.keyevent");
       // 创建一个BroadcastReceiver的实例
        startTimeval = 0;
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {



                int keycode = intent.getIntExtra("KEY_CODE",-1);
                int keyAction = intent.getIntExtra("KEY_ACTION",-1);
                long keyTime = intent.getLongExtra("KEY_TIME",-1);
                int keyFlags = intent.getIntExtra("KEY_FLAGS", -1);
                Log.e("zfy", "keycode:" + KeyEvent.keyCodeToString(keycode)
                        +" ,keyAction:"+keyAction
                        + " ,keyTime:" + keyTime
                        + " ,keyFlags:"+keyFlags
                        + " ,long press:"+
                        ((keyFlags&KeyEvent.FLAG_LONG_PRESS) !=0));
                if(ZFYConstant.testToken.isEmpty()){


                    if(isVisible) {
                        Intent intent1 = new Intent(ActivityTasks.this, ActivityHome.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent1);
                    }
                    return;
                }

                if(!isVisible){
                    return;
                }
                if(mTask == null){



                    return;
                }
                if(mTask.getList() == null){

                    if(keyAction == 0){
                        startTimeval = keyTime;
                        if(keycode == ckeyCode && keyTime - endTimeval < 3000){
                            return;
                        }
                    }

                    if(keyAction == 1) {

                        endTimeval = keyTime;
                        long val = keyTime - startTimeval;
                        ckeyCode = keycode;

                        if (keycode == 134) {
//                            if(val > 1000) {
//                                checkpoint();
//
//                                return;
//                            }

                            startActivity(new Intent(ActivityTasks.this, ZXMainActivity.class));
                            return;


                        }
                    }
                    return;
                }
                if(mTask.getList().size() == 0){

                    if(keyAction == 1) {

                        endTimeval = keyTime;
                        long val = keyTime - startTimeval;
                        ckeyCode = keycode;

                        if (keycode == 134) {
//                            if(val > 1000) {
//                                checkpoint();
//
//                                return;
//                            }

                            startActivity(new Intent(ActivityTasks.this, ZXMainActivity.class));
                            return;


                        }
                    }
                    return;
                }
                String action = intent.getAction();
                if ("android.intent.action.keyevent".equals(action)) {




                    if(keyAction == 0){
                        startTimeval = keyTime;
                        if(keycode == ckeyCode && keyTime - endTimeval < 3000){
                            return;
                        }
                    }

                    if(keyAction == 1){

                        endTimeval= keyTime;
                        long val  = keyTime - startTimeval;
                       ckeyCode = keycode;

                        if(keycode == 134 ){
//                            if(val > 1000) {
//                                checkpoint();
//
//                                return;
//                            }

                                startActivity(new Intent(ActivityTasks.this, ZXMainActivity.class));
                                return;


                        }
                        if(keycode == 133 ){
                            checkpoint(true);
                            return;
                        }
                        if(keycode == 131){

                            if(isVisible){

                            }else {
                                Intent intent1 = new Intent(ActivityTasks.this, ActivityTasks.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent1);
                                return;
                            }
                        }

                        if(isVisible) {
                            Intent intent1 = new Intent(ActivityTasks.this, ActivityScrren.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                        }
                    }

                }
                // 处理接收到的广播
            }

        };

// 使用Context注册广播接收器
        Context context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);

        }
    }

    void checkpoint(boolean sound){

        XGTask.ListDTO.PointsDTO p = null;
        try {
            p = mTask.getList().get(0).getPoints().get(0);
        }catch (Exception e){
            return;
        }


        if(mTask.isEablePost() == 0){

            if(sound){


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            MediaPlayer player = MediaPlayer.create(ActivityTasks.this, R.raw.not_in_post);
                                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        // 播放完成后执行逻辑，例如停止播放或者释放资源
                                        mediaPlayer.stop(); // 如果您希望停止播放
                                        mediaPlayer.release(); // 如果您希望释放 MediaPlayer 对象
                                    }
                                });
                            player.start();
                            } catch (Exception e){
                                Exception e3 = e;
                            }
                        }
                    });

            }
            return;
        }

        if(mTask.isEablePost() == 2){
            if(sound){
                MediaPlayer player = MediaPlayer.create(this, R.raw.posted);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        // 播放完成后执行逻辑，例如停止播放或者释放资源
                        mediaPlayer.stop(); // 如果您希望停止播放
                        mediaPlayer.release(); // 如果您希望释放 MediaPlayer 对象
                    }
                });
                player.start();
            }
            return;
        }
       // p = mTask.getList().get(0).getPoints().get();
        //GPSUtils.calculateDistance(ReportInfo.latitude,ReportInfo.longitude,)
        HashMap point = new HashMap();
        point.put("address",ReportInfo.elements);//ReportInfo.elements
        point.put("lat",ReportInfo.latitude);
        point.put("lng",ReportInfo.longitude);

        HashMap paras = new HashMap();
        paras.put("accessToken",ZFYConstant.testToken);
        paras.put("describe",mTask.getList().get(0).getDescribe());;
        paras.put("id",mTask.getList().get(0).getId());//JSON.toJSONString(pics)
        paras.put("terminal_no",DeviceIdFactory.getSerialNumber());
        paras.put("point",point);//JSON.toJSONString(point)
        HttpUtils.post()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/mytask_checkpoint")
                .params(paras)
                .build(false,true)
                .execute(new BaseCallback<HashMap>(HashMap.class) {

                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {

                        if(result.getCode() == 200){
                            startActivity(new Intent(ActivityTasks.this, ActivityPost.class));

                            getData();
                            getUserInfo();
                        }else{

                            ToastUtil.showLongToast(ActivityTasks.this,result.getMsg());
                        }
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Exception e1 = e;
                        ToastUtil.showLongToast(ActivityTasks.this,e.getLocalizedMessage());
                    }
                } );

    }


    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        String message = event.message;
        // 处理收到的消息，例如更新UI
        upDatePos();
    }





    void upDatePos(){
        try {
//            pcount++;
//            if (pcount > 400) {
//            }


//            if(mTask == null){
//
//                return;
//            }
//            try{
//                String id = mTask.getList().get(0).getId();
//            }catch (Exception e){
//                return;
//            }

            Thread.sleep(3000);//每隔1s执行一次

//
//            if(ReportInfo.latitude < 1 || ReportInfo.longitude < 1){
//
//                return;
//            }
//
//
//            if(ReportInfo.lastlatitude < 1 || ReportInfo.lastlongitude < 1){
//
//            }else {
//                long d = DeviceGPSUtils.calculateDistance(ReportInfo.latitude, ReportInfo.longitude, ReportInfo.lastlatitude, ReportInfo.lastlongitude);
//
//                if (d < 20) {
//                    return;
//                }
//            }

            checkpoint(false);
            HashMap point = new HashMap();
            point.put("address",ReportInfo.elements);//ReportInfo.elements
            point.put("lat",ReportInfo.latitude);
            point.put("lng",ReportInfo.longitude);

            HashMap paras = new HashMap();
            paras.put("accessToken",ZFYConstant.testToken);
            paras.put("id",mTask.getList().get(0).getId());//JSON.toJSONString(pics)
            paras.put("terminal_no",DeviceIdFactory.getSerialNumber());
            paras.put("point",point);//JSON.toJSONString(point)
            ///task/v1/mytask_report
            HttpUtils.post()
                    .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/mytask_report")
                    .params(paras)
                    .build(false,true)
                    .execute(new BaseCallback<HashMap>(HashMap.class) {

                        @Override
                        public void onResponse(ObjectResult<HashMap> result) {
                            HashMap dictionary = result.getData();
                            ReportInfo.lastlatitude = ReportInfo.latitude;
                            ReportInfo.lastlongitude = ReportInfo.longitude;
                            ReportInfo.postCount ++;
                        }

                        @Override
                        public void onError(okhttp3.Call call, Exception e) {
                            Exception e1 = e;
                        }
                    } );


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressLint("SetTextI18n")
    void changeCheckPoint(){

        int postCount = 0;

        for(GXReport.CheckpointBean a : mReport.getCheckpoint()){
            for(XGTask.ListDTO.PointsDTO b : mTask.getAllPoint()){

               if(b.isCheck()){
                   continue;
               }
                long d = DeviceGPSUtils.calculateDistance(a.getLat(),a.getLng(),b.getLat(),b.getLng());
                b.setIsvaildCheck(d < 50);

                if(d < 50){
                    postCount ++;
                }
            }
        }

        ((TextView) findViewById(R.id.tv_xc_count)).setText(String.valueOf(postCount) + "/" + String.valueOf(mTask.getAllPoint().size()));

    }



}