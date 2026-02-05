package kl.cds.utils;


import androidx.annotation.NonNull;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Certificate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

//特别注意使用正确的Base64工具

/**
 * Created by NC040 on 2018/3
 * 与svs服务器进行通信的相关逻辑
 */

public class SvsHelper {
    private String SERVER;
    //测试公用字符串
    public final static String plainData = "just a test";

    public static String SVS_SIGN_DATA, SVS_VERIFY_DATA, SVS_SIGN_MESSAGE,
            SVS_VERIFY_SIGN_MESSAGE, SVS_ENCRYPT_MESSAGE,
            SVS_DECRYPT_MESSAGE, SVS_EXPORT_SERVER_CERT;

    public SvsHelper(String Server) {
        this.SERVER = Server;
        //初始化url
        // P1签名
        SVS_SIGN_DATA = SERVER + "SignData";
        // P1验签
        SVS_VERIFY_DATA = SERVER + "VerifySignedData";
        // P7签名
        SVS_SIGN_MESSAGE = SERVER + "SignMessage";
        // p7验签
        SVS_VERIFY_SIGN_MESSAGE = SERVER + "VerifySignedMessage";
        // 数字信封加密
        SVS_ENCRYPT_MESSAGE = SERVER + "EncryptMessage";
        // 数字信封解密
        SVS_DECRYPT_MESSAGE = SERVER + "DecryptMessage";
        // 导出站点证书
        SVS_EXPORT_SERVER_CERT = SERVER + "ExportServerCert";
    }

    public String getSigndata() throws Exception {
        URL url = new URL(SVS_SIGN_DATA);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        conn.setRequestProperty("HashType", "Sha256");
//        conn.setRequestProperty("HashType", "SM3");
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print("Data=" + new String(Base64.encodeBase64(plainData.getBytes()), "utf-8"));
        out.flush();
        out.close();
        return GetHttpRequest(conn);
    }

    public String verifySigndata(String localCert, byte[] localSignDataByte) throws Exception {
        URL url = new URL(
                SVS_VERIFY_DATA + "?Data=" + new String(Base64.encodeBase64(plainData.getBytes())) + "&SignedData="
                        + new String(localSignDataByte) + "&Cert=" + localCert);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        /*
        *   要求服务端用什么hash算法
            Raw：服务端不做hash运算
            Sha1：服务端要对Data做SHA1(签名证书为RSA时的默认值)
            Sha256:服务端要对Data做SHA256运算
            Md5：服务端要对Data做MD5
            SM3：服务端要对Data做SM3（签名证书为SM2时的默认值）
            注：为空时根据证书类型选择默认值，证书为RSA，则取Sha1；证书为SM2，则取SM3
        * */
        /* 不再设置。
        String hashType = getHashType(localCert);
        conn.setRequestProperty("HashType", hashType);
        */
        conn.setRequestProperty("CertVerifyOption", "None");
        return GetHttpRequest(conn);
    }

    @NonNull
    public String getHashType(String b64Cert) throws IOException {
        ASN1InputStream asn1InputStream = new ASN1InputStream(new ByteArrayInputStream(Base64.decodeBase64(b64Cert.getBytes())));
        Certificate certificate = Certificate.getInstance(asn1InputStream.readObject());
        String oid = certificate.getSignatureAlgorithm().getAlgorithm().getId();
        String sigAlog = Settings.oidToNameMap.get(oid);
        String hashType = "";
        if (sigAlog.contains("SM2"))
            hashType = "SM3";
        else if (sigAlog.contains("RSA"))
            hashType = "SHA1";
        else
            throw new IllegalArgumentException("unknow hashType!");
        return hashType;
    }

    public String getSignMessage() throws Exception {
        URL url = new URL(SVS_SIGN_MESSAGE);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("AttachOrigin", "Yes");
        conn.setRequestProperty("AttachCert", "Cert");
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print("Data=" + new String(Base64.encodeBase64(plainData.getBytes())));
        out.flush();
        out.close();
        return GetHttpRequest(conn);
    }

    public String verifySignMessage(byte[] localSignMessage) throws Exception {
        URL url = new URL(SVS_VERIFY_SIGN_MESSAGE);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("CertVerifyOption", "None");
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        // P7签名返回的已经B64
        String localSigned = new String(localSignMessage);
        out.print("SignedMessage=" + localSigned);
        out.flush();
        out.close();
        return GetHttpRequest(conn);
    }

    public String getEncryptedData(String localCert) throws Exception {
        URL url = new URL(SVS_ENCRYPT_MESSAGE);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("EncryptAlg", "SM4");
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print("Data=" + new String(Base64.encodeBase64(plainData.getBytes())) + "&Cert=" + localCert);
        out.flush();
        out.close();
        return GetHttpRequest(conn);
    }

    public String DecryptData(String localEncryptData) throws Exception {
        URL url = new URL(SVS_DECRYPT_MESSAGE);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print("EncryptedMessage=" + localEncryptData);
        out.flush();
        out.close();
        return GetHttpRequest(conn);
    }


    private static String GetHttpRequest(HttpURLConnection conn) throws Exception {
        StringBuilder resultInfo = new StringBuilder();
        int code = conn.getResponseCode();
        if (code == 200) {
            String result = new String(readStream(conn.getInputStream()));
            resultInfo.append(result + "\n");

        } else if (code == 404) {
            resultInfo.append("接口返回状态码404 Fail not Find \n");
        } else if (code == 500) {
            resultInfo.append("接口返回状态码500 Server Internal Error \n");
        } else {
            resultInfo.append("接口返回状态码" + code + "\n");
        }
        return resultInfo.toString();
    }

    /**
     * 获取SVS站点证书
     *
     * @return 站点证书B64编码
     */
    public String getSvsCert() {
        try {
            URL url = new URL(SVS_EXPORT_SERVER_CERT);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            if (code == 200) {
                String result = new String(readStream(conn.getInputStream()));
                return getReturnData(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析HttpUrlConnection中返回输入流
     *
     * @param inStream 输入流
     * @return 返回byte数组
     */
    private static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    /**
     * 获取返回的ErrorCode,服务器返回结果都带\n
     */
    public static String getErrorCode(String result) {
        int index = result.indexOf("=");
        return result.substring(index + 1, index + 2);
    }

    /**
     * 返回格式基本都为
     * HttpRequest=200ok
     * ErrorCode = xxx
     * Data = xxx
     * 需要的数据为Data
     */
    public static String getReturnData(String result) {
        int index = result.indexOf("=", 10);
        return result.substring(index + 1);
    }

}
