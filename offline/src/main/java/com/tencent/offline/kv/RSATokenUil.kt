package com.tencent.offline.kv

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Created by rui
 *  on 2021/8/2
 *  离线token 加密解密
 *
 */
object RSATokenUil {
    private const val TRANSFORMATION = "RSA"
    private const val ENCRYPT_MAX_SIZE = 117
    private const val DECRYPT_MAX_SIZE = 256

    private const val PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvDPpbSL1njP25cc/kMq6Bg9eu3pyECFO\n" +
                "kSl+AEjPwqGMh5NunUwPPgmyH09J3g7CEWBvYEU7IZpQsbHAjHQZjol9ZJGx/dfsbu0MIW7B5NYi\n" +
                "BKguA9VGJFXdDcwtQIaXjeHUzpeK1ORfJmxPxklAXzqcqwC714MdZ+QZvl39IMXiGjKKv2lLleeY\n" +
                "ylGDNnAIIqQoy0+7Ik4ZXVhZP6GzStVQ/L9xkWVX0vKvWqABTWd8kdRLMX4yQ3HrQ9AMB9obVWBG\n" +
                "5RIZ1MT3KvbEzryNF0irnmtubQhNIfcogz1etimE6+w/peg7+O6QR81eFHNk082ZST5pCSrYhJ28\n" +
                "kvq1QwIDAQAB\n"
    private const val PRIVATE_KEY =
        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8M+ltIvWeM/blxz+QyroGD167\n" +
                "enIQIU6RKX4ASM/CoYyHk26dTA8+CbIfT0neDsIRYG9gRTshmlCxscCMdBmOiX1kkbH91+xu7Qwh\n" +
                "bsHk1iIEqC4D1UYkVd0NzC1AhpeN4dTOl4rU5F8mbE/GSUBfOpyrALvXgx1n5Bm+Xf0gxeIaMoq/\n" +
                "aUuV55jKUYM2cAgipCjLT7siThldWFk/obNK1VD8v3GRZVfS8q9aoAFNZ3yR1EsxfjJDcetD0AwH\n" +
                "2htVYEblEhnUxPcq9sTOvI0XSKuea25tCE0h9yiDPV62KYTr7D+l6Dv47pBHzV4Uc2TTzZlJPmkJ\n" +
                "KtiEnbyS+rVDAgMBAAECggEAXLCgdTyzhVAIeFByUQhXJ+bn/COpC6KrxDiQMumpUS0pPhhxcQzl\n" +
                "sOMrk9oEAlZ4ZAB/ugU+wyTyOyHVOBdyAs5aMG7FH8T5UAR1Zf3bhq2joXGHACJI4lpbfZEidFYV\n" +
                "TC5+FaVzHbi/1/eeLoUjLAN854d7+/86O1GPED/QDBQv0WMFB/0gJbPQU9xOUKJIGMPnFbRnonZA\n" +
                "sQ9jbMGDsnlqNnpm+UPx7+R+1rQBQs9EBq7iMcBpE+9yMeDZJJiSZ9UE6SdFBp/zeCoyFofaMga/\n" +
                "pnol/16FwSHaFj9oZL83qXwTfPqY0fgNHu3Jly/re1H8SLvPtH2U4iDfk0ZJsQKBgQD2CwMVcS4S\n" +
                "TfL1CQdOil3ba3P77sWWo9Nx1VCuQXh7GVWel7MgkamiLBxDAyN3iWOH7GU4/l+ZaVrW0H3plM43\n" +
                "1/KqXIbBkf3fGr9RnIJcWjAfgdBuQubFMkkmnzkXeaXnvF4jppwEZkkVDPJhLFbB7Gf+5ZEcpISY\n" +
                "9IHErjzlewKBgQDD0a3ZS8V5U/rXfGCYM/xXgTszdJ6IBorPMYvCuOWmFxQgYy/yxvghAv0LBcdt\n" +
                "GnBrB6X/Irh7fMID4yL3eU545bzjJQ9eVkwDwBCp6KYRF/MrYRgfLAnOr8OeWf3VOuIi80NbTggQ\n" +
                "i8+ES6usXzySEFGknXbGXe3lp89eqKWQ2QKBgHJUGaG8DtF4oR3VjSykxQoxexqlD/F4vMcXfT8L\n" +
                "syVYV12zVgwbQ3zVDGjjK3bfQ32kjkCWiuupdgl9phSFDfILCXq6Ne/6m8IxFpegpz6stdHeK3Eg\n" +
                "60TUogMtV4UeBMaS1Bey0qqEsQ70DiizRrkj1gsut2hc1jSQ4T/4StnDAoGBAKkjEFWTZBNDHXRJ\n" +
                "AyoUJQ70Gc19Gn2FoRoLwptDl2CZqJG8+qppf+MLsMN3j2TGpHleFtxEAjLvdKlk5Ev7ZHN6hGdy\n" +
                "GE80nN3UZNBNmjtDg49hgzNRl3sGSCIbuusVvEPR+MkBomegS3MK5VKyxB+ppstYE0KN2TE3fB9X\n" +
                "7gIpAoGAVWr+H/2x+ofO0nJ0SUfjAJ/0Q9pobvL7eX82C2zQj+zsd4Stt+hDlbc2j7nIu5ydES+n\n" +
                "abBW9NlBFzraM27RtJkukUbGna0TjO450Lf/fer1zFML+n8hm5RqBP8MgR6j8Je7VDrSag5ycgth\n" +
                "yhuCzaJVskDD5wPpzeMZpdB5fho=\n"

    fun getPrivateKey(): PrivateKey {

        //字符串转成秘钥对对象
        val kf = KeyFactory.getInstance("RSA")
        val privateKey = kf.generatePrivate(
            PKCS8EncodedKeySpec(
                Base64.decode(
                    PRIVATE_KEY,
                    Base64.DEFAULT
                )
            )
        )
        return privateKey
    }

    fun getPublicKey(): PublicKey {

        //字符串转成秘钥对对象
        val kf = KeyFactory.getInstance("RSA")
        val publicKey = kf.generatePublic(
            X509EncodedKeySpec(
                Base64.decode(
                    PUBLIC_KEY,
                    Base64.DEFAULT
                )
            )
        )
        return publicKey
    }


    /**
     * 私钥加密
     */
    fun encryptByPrivateKey(str: String): String {
        val byteArray = str.toByteArray()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val privateKey = getPrivateKey()
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)
        //定义缓冲区
        var temp: ByteArray? = null
        //当前偏移量
        var offset = 0
        val outputStream = ByteArrayOutputStream()

        while (byteArray.size - offset > 0) {
            //剩余的部分大于最大加密字段，则加密117个字节的最大长度
            if (byteArray.size - offset >= ENCRYPT_MAX_SIZE) {
                temp = cipher.doFinal(byteArray, offset, ENCRYPT_MAX_SIZE)
                //偏移量增加117
                offset += ENCRYPT_MAX_SIZE
            } else {
                //如果剩余的字节数小于117，则加密剩余的全部
                temp = cipher.doFinal(byteArray, offset, (byteArray.size - offset))
                offset = byteArray.size
            }
            outputStream.write(temp)
        }
        outputStream.close()
        return Base64.encode(outputStream.toByteArray(), Base64.DEFAULT).toString()
    }

    /**
     * 公钥加密
     */
    fun encryptByPublicKey(str: String): String {
        val byteArray = str.toByteArray()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey())

        var temp: ByteArray? = null
        var offset = 0

        val outputStream = ByteArrayOutputStream()

        while (byteArray.size - offset > 0) {
            if (byteArray.size - offset >= ENCRYPT_MAX_SIZE) {
                temp = cipher.doFinal(byteArray, offset, ENCRYPT_MAX_SIZE)
                offset += ENCRYPT_MAX_SIZE
            } else {
                temp = cipher.doFinal(byteArray, offset, (byteArray.size - offset))
                offset = byteArray.size
            }
            outputStream.write(temp)
        }

        outputStream.close()
        return String(Base64.encode(outputStream.toByteArray(), Base64.DEFAULT))
    }

    /**
     * 私钥解密
     * 注意Exception in thread "main" javax.crypto.IllegalBlockSizeException:
     * Data must not be longer than 256 bytes
     * 关于到底是128个字节还是256个，我也很迷糊了，我写成128的时候就报这个错误，改成256后就没事了
     */
    fun decryptByPrivateKey(str: String): String {
        val outputStream = ByteArrayOutputStream()
        try {
            val byteArray = Base64.decode(str, Base64.DEFAULT)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val privateKey = getPrivateKey()
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            //定义缓冲区
            var temp: ByteArray? = null
            //当前偏移量
            var offset = 0

            while (byteArray.size - offset > 0) {
                //剩余的部分大于最大解密字段，则加密限制的最大长度
                if (byteArray.size - offset >= DECRYPT_MAX_SIZE) {
                    temp = cipher.doFinal(byteArray, offset, DECRYPT_MAX_SIZE)
                    //偏移量增加128
                    offset += DECRYPT_MAX_SIZE
                } else {
                    //如果剩余的字节数小于最大长度，则解密剩余的全部
                    temp = cipher.doFinal(byteArray, offset, (byteArray.size - offset))
                    offset = byteArray.size
                }
                outputStream.write(temp)
            }

            return String(outputStream.toByteArray())
        } catch (e: Exception) {
            e.toString()
        } finally {
            outputStream.close()
        }
        return ""
    }

    /**
     * 公钥解密
     */
    fun decryptByPublicKey(str: String, publicKey: PublicKey): String {
        val byteArray = Base64.decode(str, Base64.DEFAULT)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, publicKey)

        var temp: ByteArray? = null
        var offset = 0

        val outputStream = ByteArrayOutputStream()

        while (byteArray.size - offset > 0) {
            if (byteArray.size - offset >= DECRYPT_MAX_SIZE) {
                temp = cipher.doFinal(byteArray, offset, DECRYPT_MAX_SIZE)
                offset += DECRYPT_MAX_SIZE
            } else {
                temp = cipher.doFinal(byteArray, offset, (byteArray.size - offset))
                offset = byteArray.size
            }
            outputStream.write(temp)
        }
        outputStream.close()
        return String(outputStream.toByteArray())
    }

}