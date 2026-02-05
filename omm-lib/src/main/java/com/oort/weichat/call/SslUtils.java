package com.oort.weichat.call;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 证书工具类：提供忽略SSL证书验证的相关配置
 */
public class SslUtils {
    /**
     * 获取信任所有证书的SSLSocketFactory
     */
    public static SSLSocketFactory getTrustAllSslSocketFactory() {
        try {
            // 创建信任所有证书的TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // 初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("创建信任所有证书的SSLSocketFactory失败", e);
        }
    }

    /**
     * 获取信任所有主机名的HostnameVerifier
     */
    public static HostnameVerifier getTrustAllHostnameVerifier() {
        return (hostname, session) -> true; // 始终返回true，忽略主机名验证
    }

    /**
     * WebView处理SSL错误（忽略证书问题）
     */
    public static void handleWebViewSslError(WebView view, SslErrorHandler handler, SslError error) {
        // 注意：仅在内网环境使用，生产环境必须移除
        handler.proceed(); // 继续加载，忽略证书错误
    }

    /**
     * 获取X509TrustManager实例（用于OkHttp配置）
     */
    public static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }
}
