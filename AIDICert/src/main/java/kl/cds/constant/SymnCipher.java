package kl.cds.constant;


/**
 * P7Engine使用的对称加密分组算法标识
 *
 * Created by hanj on 2017/8/10.
 */
public enum SymnCipher {
    SM4_ECB	(0x40011002,"CIPHER_SM4_ECB"),
    SM4_CBC(0x40011003,"CIPHER_SM4_CBC"),
    AES_128_ECB(0x40011004,"CIPHER_AES_128_ECB"),
    AES_128_CBC (0x40011005,"CIPHER_AES_128_CBC"),
    AES_256_ECB(0x00004004,"CIPHER_AES_128_ECB"),
    AES_256_CBC (0x00004008,"CIPHER_AES_128_CBC");

    private final int id;
    private final String name;


    SymnCipher(int id, String name) {
        this.id=id;
        this.name=name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static SymnCipher valueOf(long type) {
        SymnCipher[] encryptMethods = SymnCipher.values();
        for (SymnCipher encryptMethod : encryptMethods) {
            if (encryptMethod.getId() == type) {
                return encryptMethod;
            }
        }
        return null;
    }
}
