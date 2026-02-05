package com.oortcloud.login.net;

import com.alibaba.fastjson.JSONObject;
import com.oort.weichat.AppConfig;
import com.oort.weichat.MyApplication;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.login.net.utils.RxBus;
import com.oortcloud.login.net.utils.StringConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.oortcloud.basemodule.constant.Constant.BASE_IP;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/11/7 17:14
 * @version： v1.0
 * @function： 获取域名解析动态ip
 */
public class IPRetrofitSreviceManager {
    private static final int DEFAULT_CONNECT_TIME = 10;
    private static final int DEFAULT_WRITE_TIME = 30;
    private static final int DEFAULT_READ_TIME = 30;
    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    private IPRetrofitSreviceManager() {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(Constant.IP_PARAM)//设置服务器路径
                .addConverterFactory(StringConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加回调库，采用RxJava
                .build();

    }

    private static class SingletonHolder {
        private static final IPRetrofitSreviceManager INSTANCE = new IPRetrofitSreviceManager();
    }

    /*
     * 获取RetrofitServiceManager
     **/
    public static IPRetrofitSreviceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }


    private static ApiService apiService = IPRetrofitSreviceManager.getInstance().create(ApiService.class);

    static Observable<String> getObservable() {

        return apiService.Getbservable(Constant.IP_PARAM).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public static final void getIP(){
        getObservable().subscribe(new RxBus.BusObserver<String>(){
            @Override
            public void onNext(String s) {
                super.onNext(s);
                JSONObject jsonObject = JSONObject.parseObject(s);
                if (jsonObject.getIntValue("code") == 200){
                    Constant.BASE_IP = "http://"+ getIps( jsonObject.getString("data")).get(0);
                    Constant.BASE_URL =  BASE_IP + ":31610/";
                    AppConfig.CONFIG_URL = BASE_IP +":31500/config";
                    Constant.IM_UPLOAD_URL = BASE_IP + ":31520";
                }else {
                    ToastUtil.showToast(MyApplication.getContext() , "网络异常");
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtil.showToast(MyApplication.getContext() , "网络异常");
            }
        });
    }

    /**
     * 正则获取IP地址
     * @param ipString
     * @return
     */
    public static List<String> getIps(String ipString){
        String regEx="((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
        List<String> ips = new ArrayList<String>();
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(ipString);
        while (m.find()) {
            String result = m.group();
            ips.add(result);
        }
        return ips;
    }
}
