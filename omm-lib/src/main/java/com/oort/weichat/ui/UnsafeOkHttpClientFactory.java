package com.oort.weichat.ui;

import android.content.Context;
import android.util.Log;

import com.facebook.react.modules.network.OkHttpClientFactory;
import com.facebook.react.modules.network.OkHttpClientProvider;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response; /**
 * 自定义 OkHttp 工厂，创建信任所有证书的实例（基于 OkHttpClientProvider 源码支持）
 */
public class UnsafeOkHttpClientFactory implements OkHttpClientFactory {
    private static final String TAG = "lclogUnsafeOkHttpFactory";
    private Context context; // 用于创建缓存（可选，保留 React 默认缓存逻辑）

    public UnsafeOkHttpClientFactory(Context context) {
        this.context = context;
    }

    @Override
    public OkHttpClient createNewNetworkModuleClient() {
        Log.d(TAG, "创建信任所有证书的 OkHttp 实例（React Native 用）");
        try {
            // 1. 配置信任所有证书的 SSL 上下文
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // 2. 基于 React Native 默认 Builder 扩展（保留缓存、超时等默认配置）
            OkHttpClient.Builder builder = OkHttpClientProvider.createClientBuilder(context)
                // 替换为信任所有证书的 SSLSocketFactory
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                // 跳过主机名校验
                .hostnameVerifier((hostname, session) -> {
                    Log.d(TAG, "跳过主机名校验: " + hostname);
                    return true;
                })
                // 添加请求日志拦截器（关键：定位 config.js 请求是否生效）
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        String url = request.url().toString();
                        Log.d(TAG, "React fetch 请求发起: " + url); // 打印请求 URL（如 config.js）

                        // 记录请求时间，排查超时
                        long start = System.currentTimeMillis();
                        try {
                            Response response = chain.proceed(request);
                            Log.i(TAG, "请求成功: " + url + "，响应码: " + response.code() +
                                "，耗时: " + (System.currentTimeMillis() - start) + "ms");
                            return response;
                        } catch (Exception e) {
                            Log.e(TAG, "请求失败: " + url + "，原因: " + e.getMessage(), e);
                            throw e; // 抛出异常，让 Jitsi 触发失败回调
                        }
                    }
                });

            // 3. 设置超时（避免默认 0 超时导致卡住）
            builder.connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS);

            OkHttpClient client = builder.build();
            Log.d(TAG, "OkHttp 实例创建完成（已信任所有证书）");
            return client;

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            Log.e(TAG, "创建 OkHttp 实例失败", e);
            // 异常时返回默认实例（避免崩溃）
            return OkHttpClientProvider.createClient(context);
        }
    }
}






