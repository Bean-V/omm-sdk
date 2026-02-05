package cn.com.jit.provider.lib.net;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.OutputStream;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import cn.com.jit.provider.lib.JbProviderClient;

public class HttpDemo {


    public static final String SUBMIT_METHOD_POST = "POST";

    public class My509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public String httpPost(String path, String param, Context ctx) {
        Log.e(JbProviderClient.TAG, "path:" + path + ",param:" + param);

        StringBuffer sb =new StringBuffer();
        HttpsURLConnection connection = null;
        try {
            TrustManager[] tm = {new My509TrustManager()};
            SSLContext ssl = null;
            ssl = SSLContext.getInstance("TLS");
            ssl.init(null, tm, new SecureRandom());
            SSLSocketFactory sslSocketFactory = ssl.getSocketFactory();
            URL url = new URL(path);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(SUBMIT_METHOD_POST);
            connection.setSSLSocketFactory(sslSocketFactory);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("content-type", "application/json; charset=UTF-8");
            connection.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            connection.connect();
            String strJson = param;
            OutputStream out = connection.getOutputStream();
            if (out != null && strJson != null && !TextUtils.isEmpty(strJson)) {
                out.write(strJson.getBytes("UTF-8"));
            }
            int statusCode = connection.getResponseCode();
            sb.append("statusCode:" + statusCode + "\n");
            Log.e(JbProviderClient.TAG,"statusCode:" + statusCode);
            Map<String, List<String>> map = connection.getHeaderFields();
            sb.append("显示响应Header信息...\n");
            Log.e(JbProviderClient.TAG,"显示响应Header信息...\n");
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                Log.e(JbProviderClient.TAG,"Key : " + entry.getKey() +
                        " ,Value : " + entry.getValue());
                sb.append("Key : " + entry.getKey() + " ,Value : " + entry.getValue() + "\n");
            }
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
//            String line = "";
//            StringBuffer buffer = new StringBuffer();
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line);
//            }
//            reader.close();
//            Log.e(JbProviderClient.TAG, "response:" + buffer.toString());
//            return buffer.toString();
        } catch (Throwable e) {
            sb.append(e.getMessage());
            Log.e(JbProviderClient.TAG,"httpPost",e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return sb.toString();

    }
}
