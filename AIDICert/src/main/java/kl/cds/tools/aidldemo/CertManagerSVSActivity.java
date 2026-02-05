package kl.cds.tools.aidldemo;

import static kl.cds.utils.SvsHelper.getErrorCode;
import static kl.cds.utils.SvsHelper.getReturnData;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.koalii.svs.client.Svs2ClientHelper;

import java.util.Locale;

import kl.cds.constant.SymnCipher;
import kl.cds.utils.Settings;
import kl.cds.utils.SvsHelper;
import koal.cert.tools.ByteBuf;
import koal.cert.tools.ICertManager;
import koal.cert.tools.ResultBean;

public class CertManagerSVSActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvInfo;

    // 按钮声明
    private View btnSvsSignDataSmfVerifySvs;
    private View btnSmfSignDataSvsVerifySvs;
    private View btnSvsSignmsgSmfVerifymsg;
    private View btnSmfSignMsgSvsVerifymsgSvs;
    private View btnSmfEncryptSvsDecrypt;
    private View btnSvsEncryptSmfDecrypt;
    private View btnClearSvs;
    private View btnMultiTestSvs;

    private ICertManager mICertManager;
    private SvsHelper svsHelper;
    private String certPath = null;
    private boolean isLogin = false;
    private Svs2ClientHelper instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svs);
        initViews(); // 初始化视图
        initData();  // 初始化数据
    }

    // 初始化视图（替换ButterKnife.bind）
    private void initViews() {
        // 基础视图
        tvInfo = findViewById(R.id.tv_info);

        // 按钮绑定
        btnSvsSignDataSmfVerifySvs = findViewById(R.id.btn_svs_signData_smf_verify_svs);
        btnSmfSignDataSvsVerifySvs = findViewById(R.id.btn_smf_signData_svs_verify_svs);
        btnSvsSignmsgSmfVerifymsg = findViewById(R.id.btn_svs_signmsg_smf_verifymsg);
        btnSmfSignMsgSvsVerifymsgSvs = findViewById(R.id.btn_smf_signMsg_svs_verifymsg_svs);
        btnSmfEncryptSvsDecrypt = findViewById(R.id.btn_smf_encrypt_svs_decrypt);
        btnSvsEncryptSmfDecrypt = findViewById(R.id.btn_svs_encrypt_smf_decrypt);
        btnClearSvs = findViewById(R.id.btn_clear_svs);
        btnMultiTestSvs = findViewById(R.id.btn_multi_test_svs);

        // 设置点击监听
        btnSvsSignDataSmfVerifySvs.setOnClickListener(this);
        btnSmfSignDataSvsVerifySvs.setOnClickListener(this);
        btnSvsSignmsgSmfVerifymsg.setOnClickListener(this);
        btnSmfSignMsgSvsVerifymsgSvs.setOnClickListener(this);
        btnSmfEncryptSvsDecrypt.setOnClickListener(this);
        btnSvsEncryptSmfDecrypt.setOnClickListener(this);
        btnClearSvs.setOnClickListener(this);
        btnMultiTestSvs.setOnClickListener(this);
    }

    // 初始化数据
    private void initData() {
        mICertManager = MainActivity.certManager;
        initSVSServerSdk();

        try {
            certPath = mICertManager.SOF_GetUserList().get(Settings.CERT_INDEX);
            appendInfo("当前证书路径：\n" + certPath);
            ResultBean resultBean = mICertManager.SOF_Login(certPath, Settings.PIN);
            isLogin = (resultBean.getErrorCode() == resultBean.OPER_SUC ? true : false);
            appendInfo("登录状态：\n" + (isLogin ? "成功" : "失败:" + resultBean.getDetail() + " " + resultBean.getMessage()));

        } catch (RemoteException e) {
            e.printStackTrace();
            appendInfo("初始化异常：" + e.getMessage());
        }

        String svsUrl = String.format("%s:%s/", Settings.SVS_SERVER_HOST, Settings.SVS_SERVER_PORT);
        svsHelper = new SvsHelper(svsUrl);
    }

    private void initSVSServerSdk() {
        instance = Svs2ClientHelper.getInstance();
        String hereUrl = "";
        if (Settings.SVS_SERVER_HOST.startsWith("http")) {
            hereUrl = Settings.SVS_SERVER_HOST.split("//")[1];
        }
        boolean init = instance.init(hereUrl, Integer.valueOf(Settings.SVS_SERVER_PORT), 20);
    }

    // 点击事件处理（替换@OnClick）
    @Override
    public void onClick(final View view) {
        new Thread(() -> {
            try {
                int id = view.getId();
                if (id == R.id.btn_smf_signData_svs_verify_svs) {
                    SOF_SignData_SVS_VerifyData();
                } else if (id == R.id.btn_svs_signData_smf_verify_svs) {
                    SVS_SignData_SOF_VerifyData();
                } else if (id == R.id.btn_smf_signMsg_svs_verifymsg_svs) {
                    SOF_SignMessage_SVS_VerifyMessageOutBound();
                } else if (id == R.id.btn_svs_signmsg_smf_verifymsg) {
                    SVS_SignMessage_SOF_VerifyMessage();
                } else if (id == R.id.btn_smf_encrypt_svs_decrypt) {
                    SOF_EncryptMessage_SVS_DecryptMessage();
                } else if (id == R.id.btn_svs_encrypt_smf_decrypt) {
                    SVS_EncryptMessage_SOF_DecryptMessage();
                } else if (id == R.id.btn_multi_test_svs) {
                    SOF_SignData_SVS_VerifyData();
                    SVS_SignData_SOF_VerifyData();
                    SOF_SignMessage_SVS_VerifyMessage();
                    SVS_SignMessage_SOF_VerifyMessage();
                    SOF_EncryptMessage_SVS_DecryptMessage();
                    SVS_EncryptMessage_SOF_DecryptMessage();
                } else if (id == R.id.btn_clear_svs) {
                    runOnUiThread(() -> tvInfo.setText(""));
                }
            } catch (Exception e) {
                appendInfo("失败：\n" + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 本地数字（P1）签名，远端数字（p1）验签
     *
     * @throws Exception
     */
    private void SOF_SignData_SVS_VerifyData() throws Exception {
        checkLogin();

        ByteBuf localSignDataByte = new ByteBuf();
        ResultBean resultBeanSign = mICertManager.SOF_SignData(svsHelper.plainData.getBytes(), localSignDataByte);
        checkResultBeanSuc(resultBeanSign);
        String b64Cert = mICertManager.SOF_ExportUserCert(certPath);
        String result = svsHelper.verifySigndata(b64Cert, localSignDataByte.getByte());
        boolean verified = getErrorCode(result).equals("0");
        appendInfo("本地数字签名、SVS验证" + (verified ? "成功" : "失败"));
    }

    /**
     * 远端数字（P1）签名，本地数字（P1）验签
     *
     * @throws Exception
     */
    private void SVS_SignData_SOF_VerifyData() throws Exception {
        String svsSign = getReturnData(svsHelper.getSigndata());
        String svsCert = svsHelper.getSvsCert();
        ResultBean resultBean = mICertManager.SOF_VerifySignedData(svsCert,
                svsHelper.plainData.getBytes("utf-8"), svsSign.getBytes());
        checkResultBeanSuc(resultBean);
        appendInfo("SVS数字签名、本地验证成功!");
    }

    /**
     * 本地消息（P7）签名，远端消息（P7）验签
     *
     * @throws Exception
     */
    private void SOF_SignMessage_SVS_VerifyMessage() throws Exception {
        checkLogin();

        ByteBuf localSignDataByte = new ByteBuf();
        ResultBean resultBeanSign = mICertManager.SOF_SignMessage(0,
                svsHelper.plainData.getBytes(), localSignDataByte);
        checkResultBeanSuc(resultBeanSign);
        final byte[] signByte = localSignDataByte.getByte();
        String result = svsHelper.verifySignMessage(signByte);
        boolean verified = getErrorCode(result).equals("0");
        appendInfo("本地消息签名、SVS验证" + (verified ? "成功" : "失败"));
    }

    /**
     * 扩展：本地消息（P7）签名，远端消息（P7）验签
     *
     * @throws Exception
     */
    private void SOF_SignMessage_SVS_VerifyMessageOutBound() throws Exception {
        String origniText = "20090101000605MEDIA";
        ByteBuf localSignDataByte = new ByteBuf();
        ResultBean resultBeanSign = mICertManager.SOF_SignMessage(0,
                origniText.getBytes(), localSignDataByte);
        checkResultBeanSuc(resultBeanSign);
        byte[] signByte = localSignDataByte.getByte();
        //base64字符串
        String fi = new String(signByte);
        //服务端验签
        Svs2ClientHelper.SvsResultData svsResultData = instance.pkcs7Verify(fi, origniText.getBytes());
        int m_errno = svsResultData.m_errno;
        boolean verified = String.valueOf(m_errno).equals("0");
        appendInfo("本地消息签名、SVS验证" + (verified ? "成功" : "失败"));
    }

    /**
     * 扩展：远端消息（P7）签名，本地消息（P7）验签
     *
     * @throws Exception
     */
    private void SVS_SignMessage_SOF_VerifyMessage() throws Exception {
        String svsSign = getReturnData(svsHelper.getSignMessage());
        ResultBean resultBean = mICertManager.SOF_VerifySignedMessage("".getBytes()
                , svsSign.getBytes());
        checkResultBeanSuc(resultBean);
        appendInfo("SVS消息签名、本地验证成功!");
    }

    /**
     * 本地数字信封加密，远端数字信封解密
     *
     * @throws Exception
     */
    private void SOF_EncryptMessage_SVS_DecryptMessage() throws Exception {
        ByteBuf envelopBuf = new ByteBuf();
        ResultBean resultBean = mICertManager.SOF_EncryptData(svsHelper.getSvsCert(), svsHelper.plainData.getBytes(), envelopBuf);
        checkResultBeanSuc(resultBean);
        String result = svsHelper.DecryptData(new String(envelopBuf.getByte()));
        boolean verified = getErrorCode(result).equals("0");
        appendInfo("本地数字信封加密、SVS解密" + (verified ? "成功" : "失败"));
    }

    /**
     * 远端数字信封加密，本地数字信封解密
     *
     * @throws Exception
     */
    private void SVS_EncryptMessage_SOF_DecryptMessage() throws Exception {
        checkLogin();

        mICertManager.SOF_SetEncryptMethod(SymnCipher.SM4_CBC.getId());
        String b64EncCert = mICertManager.SOF_ExportExChangeUserCert(certPath);
        String inData = SvsHelper.plainData;
        ByteBuf localEnvelopBuf = new ByteBuf();
        ResultBean resultBean = mICertManager
                .SOF_EncryptData(b64EncCert, inData.getBytes("utf-8"), localEnvelopBuf);
        checkResultBeanSuc(resultBean);

        String remoteEnvelop = getReturnData(svsHelper.getEncryptedData(b64EncCert));
        ByteBuf plainBuf = new ByteBuf();
        resultBean = mICertManager.SOF_DecryptData(remoteEnvelop.getBytes(), plainBuf);
        checkResultBeanSuc(resultBean);
        appendInfo("SVS数字信封加密、本地解密成功!");
    }

    // 工具方法（未修改）
    private void checkLogin() throws Exception {
        if (!isLogin)
            throw new Exception("未登录，请先登录后操作！");
    }

    void appendInfo(final String message) {
        runOnUiThread(() -> tvInfo.append(message + "\n"));
    }

    private void checkResultBeanSuc(ResultBean resultBean) throws Exception {
        if (resultBean.OPER_SUC != resultBean.getErrorCode()) {
            throw new Exception(String.format(Locale.CHINA, "failed!errorCode:%d message:%s detail:%s"
                    , resultBean.getErrorCode(), resultBean.getMessage(), resultBean.getDetail()));
        }
    }
}