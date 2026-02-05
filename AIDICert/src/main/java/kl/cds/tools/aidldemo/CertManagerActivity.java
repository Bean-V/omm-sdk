package kl.cds.tools.aidldemo;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;

import javax.crypto.Cipher;

import kl.cds.constant.CertInfoType;
import kl.cds.constant.SignMethod;
import kl.cds.constant.SymnCipher;
import kl.cds.utils.Settings;
import kl.cds.utils.StringUtil;
import koal.cert.tools.ByteBuf;
import koal.cert.tools.ICertManager;
import koal.cert.tools.ResultBean;

public class CertManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvInfo;
    private EditText edtPin;

    // 按钮声明
    private View btnLogin;
    private View btnExportUserCert;
    private View btnExportExchangeCert;
    private View btnGetCertInfo;
    private View btnGetCertInfoByOid;
    private View btnSetSignMethod;
    private View btnGetSignMethod;
    private View btnSignData;
    private View btnVerifyData;
    private View btnNakeSign;
    private View btnNakeVerify;
    private View btnSignMessage;
    private View btnVerifyMessage;
    private View btnSetEncryptMethod;
    private View btnGetEncryptMethod;
    private View btnSymmetricEncrypt;
    private View btnSymmetricDecrypt;
    private View btnEncryptData;
    private View btnDecryptData;
    private View btnCipherFile;
    private View btnClearSvs;
    private View btnMultiTest;

    private ICertManager mICertManager = null;
    private String certPath = null;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_manager);
        initViews(); // 初始化视图
        initData();  // 初始化数据
    }

    // 初始化视图（替换ButterKnife.bind）
    private void initViews() {
        // 基础视图
        tvInfo = findViewById(R.id.tv_info);
        edtPin = findViewById(R.id.edt_pin);

        // 按钮绑定
        btnLogin = findViewById(R.id.btn_login);
        btnExportUserCert = findViewById(R.id.btn_export_user_cert);
        btnExportExchangeCert = findViewById(R.id.btn_export_exchange_cert);
        btnGetCertInfo = findViewById(R.id.btn_get_cert_info);
        btnGetCertInfoByOid = findViewById(R.id.btn_get_cert_info_by_oid);
        btnSetSignMethod = findViewById(R.id.btn_set_sign_method);
        btnGetSignMethod = findViewById(R.id.btn_get_sign_method);
        btnSignData = findViewById(R.id.btn_sign_data);
        btnVerifyData = findViewById(R.id.btn_verify_data);
        btnNakeSign = findViewById(R.id.btn_nakeSign);
        btnNakeVerify = findViewById(R.id.btn_nakeVerify);
        btnSignMessage = findViewById(R.id.btn_sign_message);
        btnVerifyMessage = findViewById(R.id.btn_verify_message);
        btnSetEncryptMethod = findViewById(R.id.btn_set_encrypt_method);
        btnGetEncryptMethod = findViewById(R.id.btn_get_encrypt_method);
        btnSymmetricEncrypt = findViewById(R.id.btn_symmetric_encrypt);
        btnSymmetricDecrypt = findViewById(R.id.btn_symmetric_decrypt);
        btnEncryptData = findViewById(R.id.btn_encrypt_data);
        btnDecryptData = findViewById(R.id.btn_decrypt_data);
        btnCipherFile = findViewById(R.id.btn_cipher_file);
        btnClearSvs = findViewById(R.id.btn_clear_svs);
        btnMultiTest = findViewById(R.id.btn_multi_test);

        // 设置点击监听
        btnLogin.setOnClickListener(this);
        btnExportUserCert.setOnClickListener(this);
        btnExportExchangeCert.setOnClickListener(this);
        btnGetCertInfo.setOnClickListener(this);
        btnGetCertInfoByOid.setOnClickListener(this);
        btnSetSignMethod.setOnClickListener(this);
        btnGetSignMethod.setOnClickListener(this);
        btnSignData.setOnClickListener(this);
        btnVerifyData.setOnClickListener(this);
        btnNakeSign.setOnClickListener(this);
        btnNakeVerify.setOnClickListener(this);
        btnSignMessage.setOnClickListener(this);
        btnVerifyMessage.setOnClickListener(this);
        btnSetEncryptMethod.setOnClickListener(this);
        btnGetEncryptMethod.setOnClickListener(this);
        btnSymmetricEncrypt.setOnClickListener(this);
        btnSymmetricDecrypt.setOnClickListener(this);
        btnEncryptData.setOnClickListener(this);
        btnDecryptData.setOnClickListener(this);
        btnCipherFile.setOnClickListener(this);
        btnClearSvs.setOnClickListener(this);
        btnMultiTest.setOnClickListener(this);
    }

    // 初始化数据
    private void initData() {
        edtPin.setText(Settings.PIN);
        edtPin.setSelection(StringUtil.getViewString(edtPin).length());
        mICertManager = MainActivity.certManager;
    }

    // 点击事件处理（替换@OnClick）
    @Override
    public void onClick(View view) {
        try {
            int id = view.getId();
            if (id == R.id.btn_login) {
                certPath = mICertManager.SOF_GetUserList().get(Settings.CERT_INDEX);
                appendInfo("当前证书路径：\n" + certPath);
                ResultBean resultBean = mICertManager.SOF_Login(certPath, StringUtil.getViewString(edtPin));
                isLogin = (resultBean.getErrorCode() == resultBean.OPER_SUC ? true : false);
                appendInfo("登录状态：" + (isLogin ? "成功" : "失败:" + resultBean.getDetail() + " " + resultBean.getMessage()));
            } else if (id == R.id.btn_export_user_cert) {//导出签名证书
                exportUserCert();
            } else if (id == R.id.btn_export_exchange_cert) {//导出加密证书
                exportExChangeUserCert();
            } else if (id == R.id.btn_get_cert_info) {//获取证书全部信息
                getCertInfo();
            } else if (id == R.id.btn_get_cert_info_by_oid) {//通过OID获取证书信息
                getCertInfoByOid();
            } else if (id == R.id.btn_sign_data) {//PKCS#1消息签名（数字签名）
                signData();
            } else if (id == R.id.btn_verify_data) {//验证 PKCS#1消息签名（数字签名）
                verifyData();
            } else if (id == R.id.btn_sign_message) {//PKCS#7消息签名
                signMessage();
            } else if (id == R.id.btn_verify_message) {//验证 PKCS#7消息签名
                vetifyMessage();
            } else if (id == R.id.btn_encrypt_data) {//数字信封消息加密
                encryptData();
            } else if (id == R.id.btn_decrypt_data) {//数字信封消息解密
                decryptData();
            } else if (id == R.id.btn_set_sign_method) {//设置裸签算法
                setSignMethod();
            } else if (id == R.id.btn_get_sign_method) {//获取裸签算法
                getSignMethod();
            } else if (id == R.id.btn_nakeSign) {//裸签
                nakedSignData();
            } else if (id == R.id.btn_nakeVerify) {//验证 裸签
                verifyNakedSignData();
            } else if (id == R.id.btn_set_encrypt_method) {//设置对称加密算法
                setEncryptMethod();
            } else if (id == R.id.btn_get_encrypt_method) {//获取对加密算法
                getEncryptMethod();
            } else if (id == R.id.btn_symmetric_encrypt) {//对称加密
                symmetricEnc();
            } else if (id == R.id.btn_symmetric_decrypt) {//解 对称加密
                symmetricDec();
            } else if (id == R.id.btn_cipher_file) {
                largeFileEncAndDecrypt();//三段式加密
            } else if (id == R.id.btn_clear_svs) {
                runOnUiThread(() -> tvInfo.setText(""));
            } else if (id == R.id.btn_multi_test) {
                exportUserCert();
                exportExChangeUserCert();
                getCertInfo();
                getCertInfoByOid();
                setSignMethod();
                getSignMethod();
                signData();
                verifyData();
                nakedSignData();
                verifyNakedSignData();
                signMessage();
                vetifyMessage();
                setEncryptMethod();
                getEncryptMethod();
                symmetricEnc();
                symmetricDec();
                encryptData();
                decryptData();
                largeFileEncAndDecrypt();
            }
        } catch (Exception e) {
            e.printStackTrace();
            appendInfo("异常：" + e.getMessage());
        }
    }

    // 以下为原业务方法（未修改）
    private void exportUserCert() throws RemoteException {
        String b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        appendInfo("签名证书:\n" + b64Cert);
    }

    private void exportExChangeUserCert() throws RemoteException {
        String b64Cert = mICertManager.SOF_ExportExChangeUserCert(certPath);
        appendInfo("加密证书:\n" + b64Cert);
    }

    private void getCertInfo() throws RemoteException, IllegalAccessException {
        appendInfo("获取证书信息:\n");
        String b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        Field[] fields = CertInfoType.class.getFields();
        for (Field one : fields) {
            int mod = one.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                String value = mICertManager.SOF_GetCertInfo(b64Cert, (int) one.get(null));


                //if(one.getName().equals("SGD_CERT_SUBJECT_CN")){
                    //李响 413026199906103633
                appendInfo(String.format("【%s】\n%s\n", one.getName(), value));
                    //}
            }
        }
    }

    private String getSFZHFromCertInfo() throws RemoteException, IllegalAccessException {
        appendInfo("获取证书信息:\n");
        String b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        Field[] fields = CertInfoType.class.getFields();
        for (Field one : fields) {
            int mod = one.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                if(one.getName().equals("SGD_CERT_SUBJECT_CN")){
                    String value = mICertManager.SOF_GetCertInfo(b64Cert, (int) one.get(null));
                    //李响 413026199906103633
                    String[] parts = value.split(" ");
                    String lastPart = parts.length > 0 ? parts[parts.length - 1] : "";
                    return lastPart;
                }
            }
        }
        return "";
    }

    private void getCertInfoByOid() throws RemoteException, IOException {
        appendInfo("获取证书扩展信息:\n");
        String b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(Base64.decodeBase64(b64Cert.getBytes("utf-8"))));
        ASN1Sequence seq = (ASN1Sequence) aIn.readObject();
        Certificate obj = Certificate.getInstance(seq);
        TBSCertificate tbsCert = obj.getTBSCertificate();
        Extensions ext = tbsCert.getExtensions();
        if (ext != null) {
            Enumeration en = ext.oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) en.nextElement();
                String value = mICertManager.SOF_GetCertInfoByOid(b64Cert, oid.getId());
                appendInfo(String.format("【%s】\n%s\n", oid.getId(), value));
            }
        }
    }

    private void signData() throws Exception {
        checkLogin();
        String inData = "Test For SOF_SignData";
        ByteBuf signature = new ByteBuf();
        ResultBean resultBean = mICertManager.SOF_SignData(inData.getBytes("utf-8"), signature);
        checkResultBeanSuc(resultBean);
        appendInfo("数字签名成功:\n" + new String(signature.getByte()));
    }

    private void verifyData() throws Exception {
        String inData = "Test For SOF_SignData";
        checkLogin();
        ByteBuf signature = new ByteBuf();
        ResultBean resultBean = mICertManager.SOF_SignData(inData.getBytes("utf-8"), signature);
        checkResultBeanSuc(resultBean);
        String b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        resultBean = mICertManager.SOF_VerifySignedData(b64Cert, inData.getBytes("utf-8"), signature.getByte());
        checkResultBeanSuc(resultBean);
        appendInfo("数字签名验签成功！");
    }

    private void signMessage() throws Exception {
        checkLogin();
        String inData = "Test For SOF_SignMessage";
        ByteBuf localSignMessage = new ByteBuf();
        ResultBean resultBean = mICertManager.SOF_SignMessage(0, inData.getBytes("utf-8"), localSignMessage);
        checkResultBeanSuc(resultBean);
        appendInfo("消息签名成功：\n" + new String(localSignMessage.getByte()));
    }

    private void vetifyMessage() throws Exception {
        checkLogin();
        String inData = "Test For SOF_SignMessage";
        ByteBuf localSignMessage = new ByteBuf();
        ResultBean resultBean = mICertManager.SOF_SignMessage(0, inData.getBytes("utf-8"), localSignMessage);
        checkResultBeanSuc(resultBean);
        resultBean = mICertManager.SOF_VerifySignedMessage(inData.getBytes("utf-8"), localSignMessage.getByte());
        checkResultBeanSuc(resultBean);
        appendInfo("消息验签成功!");
    }

    private void encryptData() throws Exception {
        String b64Cert = mICertManager.SOF_ExportExChangeUserCert(certPath);
        if (TextUtils.isEmpty(b64Cert)){
            b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        }
        String inData = "Test For SOF_EncryptData";
        ByteBuf envelop = new ByteBuf();
        ResultBean resultBean = mICertManager.SOF_EncryptData(b64Cert, inData.getBytes("utf-8"), envelop);
        checkResultBeanSuc(resultBean);
        appendInfo("数字信封加密:\n" + new String(envelop.getByte()));
        Log.d("+++++-----", "数字信封加密结果B64:" + new String(envelop.getByte()));
    }

    private void decryptData() throws Exception {
        String b64Cert = mICertManager.SOF_ExportExChangeUserCert(certPath);
        if (TextUtils.isEmpty(b64Cert)){
            b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        }
        String inData = "Test For SOF_EncryptData";
        ByteBuf envelop = new ByteBuf();
        ResultBean resultBean = mICertManager.SOF_EncryptData(b64Cert, inData.getBytes("utf-8"), envelop);
        checkResultBeanSuc(resultBean);
        Log.d("+++++-----", "数字信封加密结果B64:" + new String(envelop.getByte()));
        ByteBuf decryptData = new ByteBuf();
        resultBean = mICertManager.SOF_DecryptData(envelop.getByte(), decryptData);
        checkResultBeanSuc(resultBean);
        appendInfo("数字信封解密:\n" + new String(decryptData.getByte()));
    }

    private void setSignMethod() throws Exception {
        ResultBean resultBean = mICertManager.SOF_SetSignMethod(SignMethod.SGD_SHA256_RSA.getType());
        checkResultBeanSuc(resultBean);
        appendInfo("设置签名算法成功:\n");
    }

    private void getSignMethod() throws RemoteException {
        long result = mICertManager.SOF_GetSignMethod();
        appendInfo("获取签名算法:\n" + SignMethod.valueOf(result).getDesc());
    }

    private void nakedSignData() throws Exception {
        checkLogin();
        String inData = "Test For SOF_NakeSignData";
        ByteBuf encryptData = new ByteBuf();
        ResultBean resultBean = mICertManager.SKF_NakedSignData(inData.getBytes("utf-8"), encryptData);
        checkResultBeanSuc(resultBean);
        appendInfo("p1裸签成功:\n" + new String(Base64.encodeBase64(encryptData.getByte())));
    }

    private void verifyNakedSignData() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        String inData = "AAH////////////////////////////////////////\n" +
                "//////wCE6FwOW5hNFNszviu2ywTtdc6S4feP\n" +
                "VPbvuk2ZoycytlI6/uA=";

        checkLogin();
        ByteBuf encryptData = new ByteBuf();
        ResultBean resultBean = mICertManager.SKF_NakedSignData(inData.getBytes(), encryptData);
        checkResultBeanSuc(resultBean);
        long result = mICertManager.SOF_GetSignMethod();
        String StringResult = "fail";
        if (SignMethod.valueOf(result).getDesc().contains("RSA")) {

            String b64Cert = mICertManager.SOF_ExportUserCert(certPath);
            String pemCert = "-----BEGIN CERTIFICATE-----\n" + b64Cert + "\n-----END CERTIFICATE-----";
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            java.security.cert.Certificate cert = certificateFactory.generateCertificate(new ByteArrayInputStream(
                    pemCert.getBytes("UTF-8")));
            PublicKey publicKey = cert.getPublicKey();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            StringResult = new String(cipher.doFinal(encryptData.getByte()));
        }
        appendInfo("p1裸签解密结果:\n" + StringResult);
    }

    private void setEncryptMethod() throws Exception {
        ResultBean resultBean = mICertManager.SOF_SetEncryptMethod(SymnCipher.SM4_ECB.getId());
        checkResultBeanSuc(resultBean);
        appendInfo("设置加密算法成功！");
    }

    private void getEncryptMethod() throws RemoteException {
        long result = mICertManager.SOF_GetEncryptMethod();
        appendInfo("获取加密算法:\n" + SymnCipher.valueOf(result).getName());
    }

    private void symmetricDec() throws Exception {
        String inData = "Test For _SOF_symmetricEnc";
        ByteBuf encData = new ByteBuf();
        ResultBean resultBean = mICertManager._SOF_symmetricEnc("1234567890123456".getBytes("utf-8"), inData.getBytes("utf-8"),
                "SM4/ECB/PKCS5Padding", null, encData);
        checkResultBeanSuc(resultBean);
        ByteBuf decData = new ByteBuf();
        resultBean = mICertManager._SOF_symmetricDec("1234567890123456".getBytes("utf-8"),
                encData.getByte(), "SM4/ECB/PKCS5Padding", null, decData);
        checkResultBeanSuc(resultBean);
        appendInfo("对称解密成功:\n" + new String(decData.getByte()));
    }

    private void symmetricEnc() throws Exception {
        String inData = "Test For _SOF_symmetricEnc";
        ByteBuf encData = new ByteBuf();
        ResultBean resultBean = mICertManager._SOF_symmetricEnc("1234567890123456".getBytes("utf-8"), inData.getBytes("utf-8"),
                "SM4/ECB/PKCS5Padding", null, encData);
        checkResultBeanSuc(resultBean);
        appendInfo("对称加密结果：\n" + new String(encData.getByte()));
    }

    private void largeFileEncAndDecrypt() throws Exception {
        byte[] file_1MB = new byte[1024 * 1024];
        Arrays.fill(file_1MB, (byte) 1);
        byte[] encryptData = largeFileEncryptAndDecrypt(file_1MB, Cipher.ENCRYPT_MODE, "AES/ECB/PKCS5Padding", null);
        byte[] decryptData = largeFileEncryptAndDecrypt(encryptData, Cipher.DECRYPT_MODE, "AES/ECB/PKCS5Padding", null);
        appendInfo("1MB文件三段式加密解密:\n" + (isEncEqualsDec(file_1MB, decryptData) ? "成功" : "失败"));
    }

    private byte[] largeFileEncryptAndDecrypt(byte[] file, int mode, String symmAlgName, byte[] iv) throws Exception {
        int i = 1, blockSize = 128 * 1024;
        ResultBean resultBean;
        long handle = mICertManager.SOF_SymInit("1234567890123456".getBytes(), symmAlgName,
                iv, mode);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] plainTest = new byte[blockSize];
        ByteBuf cipherTest = new ByteBuf();
        while (i * blockSize < file.length) {
            System.arraycopy(file, (i - 1) * blockSize, plainTest, 0, plainTest.length);
            resultBean = mICertManager.SOF_SymUpdate(handle, plainTest, cipherTest);
            checkResultBeanSuc(resultBean);
            bos.write(cipherTest.getByte());
            i++;
        }
        int length = file.length - ((i - 1) * blockSize);
        plainTest = new byte[length];
        System.arraycopy(file, (i - 1) * blockSize, plainTest, 0, length);
        resultBean = mICertManager.SOF_SymUpdate(handle, plainTest, cipherTest);
        bos.write(cipherTest.getByte());
        checkResultBeanSuc(resultBean);
        resultBean = mICertManager.SOF_SymDoFinal(handle, cipherTest);
        checkResultBeanSuc(resultBean);
        bos.write(cipherTest.getByte());

        return bos.toByteArray();
    }

    private boolean isEncEqualsDec(byte[] plain, byte[] dec) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(plain);
        String plainDigest = new String(Base64.encodeBase64(md.digest()));
        md.update(dec);
        String decDigest = new String(Base64.encodeBase64(md.digest()));
        return plainDigest.equals(decDigest);
    }

    private void checkLogin() throws Exception {
        if (!isLogin)
            throw new Exception("未登录，请先登录后操作！");
    }

    void appendInfo(final String message) {
        runOnUiThread(() -> {
            tvInfo.append(message + "\n");
            Log.d("CertManagerActivity_Tag", message);
        });
    }

    private void checkResultBeanSuc(ResultBean resultBean) throws Exception {
        if (resultBean.OPER_SUC != resultBean.getErrorCode()) {
            throw new Exception(String.format(Locale.CHINA, "failed!errorCode:%d message:%s detail:%s"
                    , resultBean.getErrorCode(), resultBean.getMessage(), resultBean.getDetail()));
        }
    }
}