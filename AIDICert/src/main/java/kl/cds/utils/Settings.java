package kl.cds.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwb on 2018/5/29.
 */

public class Settings {

    public static Map<String, String> oidToNameMap = new HashMap<>();

    static {
        oidToNameMap.put("1.2.840.113549.1.1.5", "SHA1WITHRSA");
        oidToNameMap.put("1.2.840.113549.1.1.11", "SHA256WITHRSA");
        oidToNameMap.put("1.2.156.10197.1.501", "SM3WITHSM2");
        oidToNameMap.put( "1.2.840.113549.1.1.1","RSAENCRYPTION");
        oidToNameMap.put("1.2.840.10045.2.1","SM2ECC");
    }

    //SVS测试环境 http://60.247.61.98:35005
    public static String SVS_SERVER_HOST = "http://60.247.61.98";
    public static String SVS_SERVER_PORT = "35005";

    public static String PIN = "111111";

    public static int CERT_INDEX = 0;

    public static final String REMOTE_APP_PKG_NAME = "kl.cds";

}
