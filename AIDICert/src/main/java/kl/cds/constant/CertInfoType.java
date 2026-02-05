package kl.cds.constant;

/**
 * Created by C596 on 2016/12/6.
 */

public class CertInfoType {

    public static final int SGD_CERT_VERSION = 0x00000001;
    public static final int SGD_CERT_SERIAL = 0x00000002;
    public static final int SGD_CERT_ISSUER = 0x00000005;
    public static final int SGD_CERT_VALID_TIME = 0x00000006;
    public static final int SGD_CERT_SUBJECT = 0x00000007;
    public static final int SGD_CERT_DER_PUBLIC_KEY = 0x00000008;
    public static final int SGD_CERT_DER_EXTENSIONS = 0x00000009;
    public static final int SGD_EXT_AUTHORITYKEYIDENTIFIER_INFO = 0x00000011;
    public static final int SGD_EXT_SUBJECTKEYIDENTIFIER_INFO = 0x00000012;
    public static final int SGD_EXT_KEYUSAGE_INFO = 0x00000013;
    public static final int SGD_EXT_PRIVATEKEYUSAGEPERIOD_INFO = 0x00000014;
    public static final int SGD_EXT_CERTIFICATEPOLICIES_INFO = 0x00000015;
    public static final int SGD_EXT_POLICYMAPPINGS_INFO = 0x00000016;
    public static final int SGD_EXT_BASICCONSTRAINTS_INFO = 0x00000017;
    public static final int SGD_EXT_POLICYCONSTRAINTS_INFO = 0x00000018;
    public static final int SGD_EXT_EXTKEYUSAGE_INFO = 0x00000019;
    public static final int SGD_EXT_CRLDISTRIBUTIONPOINTS_INFO = 0x0000001A;
    public static final int SGD_EXT_NETSCAPE_CERT_TYPE_INFO = 0x0000001B;
    public static final int SGD_EXT_SELFDEFINED_EXTENSION_INFO = 0x0000001C;
    public static final int SGD_CERT_ISSUER_CN = 0x00000021;
    public static final int SGD_CERT_ISSUER_O = 0x00000022;
    public static final int SGD_CERT_ISSUER_OU = 0x00000023;
    public static final int SGD_CERT_SUBJECT_CN = 0x00000031;
    public static final int SGD_CERT_SUBJECT_O = 0x00000032;
    public static final int SGD_CERT_SUBJECT_OU = 0x00000033;
    public static final int SGD_CERT_SUBJECT_EMAIL = 0x00000034;
    public static final int SGD_CERT_NOTBEFORE_TIME = 0x00000035;
    public static final int SGD_CERT_NOTAFTER_TIME = 0x00000036;

    private CertInfoType() {
    }

}
