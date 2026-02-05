package com.oort.weichat.ui;

import android.util.Log;

import com.facebook.react.modules.network.CustomClientBuilder;
import com.facebook.react.modules.websocket.WebSocketModule;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * 用于配置 React Native WebSocketModule 信任所有 SSL 证书
 * （利用 WebSocketModule 提供的 CustomClientBuilder 扩展点）
 */
public class ReactWebSocketSSLHelper {
    private static final String TAG = "ReactWebSocketSSLHelper";

    /**
     * 初始化 WebSocket 的 SSL 配置，信任所有证书
     * 需在 Jitsi 初始化前调用
     */
    public static void initInsecureSSL() {
        // 设置自定义的 OkHttpClient 构建器
        WebSocketModule.setCustomClientBuilder(new CustomClientBuilder() {
            @Override
            public void apply(OkHttpClient.Builder builder) {
                try {
                    // 1. 创建信任所有证书的 TrustManager
                    TrustManager[] trustAll = new TrustManager[]{new TrustAllManager()};

                    // 2. 初始化 SSLContext
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustAll, null);

                    // 3. 配置 OkHttpClient 信任所有证书
                    builder.sslSocketFactory(
                        new InsecureSSLSocketFactory(sslContext.getSocketFactory()),
                        (X509TrustManager) trustAll[0]
                    );

                    // 4. 跳过主机名校验
                    builder.hostnameVerifier((hostname, session) -> {
                        Log.d(TAG, "跳过主机名校验: " + hostname);
                        return true;
                    });

                    Log.d(TAG, "WebSocket 不安全 SSL 配置已应用");
                } catch (Exception e) {
                    Log.e(TAG, "配置 SSL 失败", e);
                    throw new RuntimeException("初始化 WebSocket SSL 配置失败", e);
                }
            }
        });
    }

    /**
     * 信任所有证书的 TrustManager
     */
    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // 不验证客户端证书
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // 不验证服务器证书
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 委托给安全的 SSLSocketFactory，仅用于适配 OkHttp 的类型要求
     */
    private static class InsecureSSLSocketFactory extends SSLSocketFactory {
        private final SSLSocketFactory delegate;

        public InsecureSSLSocketFactory(SSLSocketFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket() throws IOException {
            return delegate.createSocket();
        }

        @Override
        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return delegate.createSocket(s, host, port, autoClose);
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return delegate.createSocket(host, port);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return delegate.createSocket(host, port, localHost, localPort);
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return delegate.createSocket(host, port);
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return delegate.createSocket(address, port, localAddress, localPort);
        }
    }
}
