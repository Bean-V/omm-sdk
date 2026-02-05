package kl.cds.constant;

/**
 * 根据GM/T 0006，设置本接口支持的签名算法，不支持的算法将不再此处定义。
 *
 * @author huangff
 */
public enum SignMethod {
    // @formatter:off
    SGD_SM3_RSA(0x00010001, "基于SM3算法和RSA算法的签名", "SM3WithRSA"),
    SGD_SHA1_RSA(0x00010002, "基于SHA_1算法和RSA算法的签名", "SHA1WithRSA"),
    SGD_SHA256_RSA(0x00010004, "基于SHA_256算法和RSA算法的签名", "SHA256WithRSA"),
    SGD_SM3_SM2(0x00020201, "基于SM3算法和SM2算法的签名", "SM3WithSM2"),;
    // @formatter:on
    private long type;
    private String desc;
    private String signType;

    public long getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public String getSignType() {
        return signType;
    }

    private SignMethod(long type, String desc, String signType) {
        this.type = type;
        this.desc = desc;
        this.signType = signType;
    }

    public static SignMethod valueOf(long type) {
        SignMethod[] signMethods = SignMethod.values();
        for (SignMethod signMethod : signMethods) {
            if (signMethod.getType() == type) {
                return signMethod;
            }
        }
        return null;
    }

    public static SignMethod valueTypeOf(String type) {
        SignMethod[] signMethods = SignMethod.values();
        for (SignMethod signMethod : signMethods) {
            if (signMethod.getSignType().equalsIgnoreCase(type)) {
                return signMethod;
            }
        }
        return null;
    }


    public long getValidSignType(){
        if(getSignType().contains("RSA")){
            return SGD_SHA256_RSA.getType();
        }else if(getSignType().contains("SM2")){
            return SGD_SM3_SM2.getType();
        }else{
            return -1;
        }
    }

}
