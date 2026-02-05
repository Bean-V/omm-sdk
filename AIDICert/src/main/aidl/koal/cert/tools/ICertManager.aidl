package koal.cert.tools;

import koal.cert.tools.ResultBean;
import koal.cert.tools.ByteBuf;
/*
  采用Base64均为org.apache.commons.codec.binary.Base64,详见文档CertManager_aidl文档.docx
*/
interface ICertManager {

  /*
  获取证书容器路径
  返回值：
      certPath: devName/appName/conName
  */
   List<String> SOF_GetUserList();

	/*
	校验设备的用户认证口令，进行用户认证
	    [IN]certPath:证书容器路径通过SOF_GetUserList获取
		      strPin：用户口令字符串
	返回值：
		ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                    调用失败：errorCode = 1
                                             message 为错误消息
                                             detail 为错误详细日志
	*/
	ResultBean SOF_Login(in String certPath,in String strPin);

	/*
	导出签名证书
	    [IN]certPath 证书容器路径通过SOF_GetUserList获取
	返回值：
	    String: base64编码的证书字符串
	*/
	String SOF_ExportUserCert(in String certPath);

	/*
	导出加密证书
	    [IN]certPath:证书容器路径通过SOF_GetUserList获取
  返回值：
    	String: base64编码的证书字符串
	*/
    String SOF_ExportExChangeUserCert(in String certPath);

	/*
	获取证书内指定类型的信息
	    [IN]b64cert:base64编码的签名证书
	    [IN]type：获取信息的类型，见“0006-2012_密码应用标识规范_20090323_送审稿_评审后修改.pdf”
		            标识符	       标签						               描述
		        0x00000001 SGD_CERT_VERSION 						     证书版本
        		0x00000002 SGD_CERT_SERIAL  							证书序列号
        		0x00000005 SGD_CERT_ISSUER  							证书颁发者信息
        		0x00000006 SGD_CERT_VALID_TIME  						证书有效期
        		0x00000007 SGD_CERT_SUBJECT								证书拥有者信息
        		0x00000008 SGD_CERT_DER_PUBLIC_KEY  				    证书公钥信息
        		0x00000009 SGD_CERT_DER_EXTENSIONS  					证书扩展项信息
        		0x00000011 SGD_EXT_AUTHORITYKEYIDENTIFIER_INFO 			颁发者密钥标示符
        		0x00000012 SGD_EXT_SUBJECTKEYIDENTIFIER_INFO			证书持有者密钥标示符
        		0x00000013 SGD_EXT_KEYUSAGE_INFO 						密钥用途
        		0x00000014 SGD_EXT_PRIVATEKEYUSAGEPERIOD_INFO 			私钥有效期
        		0x00000015 SGD_EXT_CERTIFICATEPOLICIES_INFO			 	证书策略
        		0x00000016 SGD_EXT_POLICYMAPPINGS_INFO 					策略影射
        		0x00000017 SGD_EXT_BASICCONSTRAINTS_INFO 				基本限制
        		0x00000018 SGD_EXT_POLICYCONSTRAINTS_INFO				策略限制
        		0x00000019 SGD_EXT_EXTKEYUSAGE_INFO 					扩展密钥用途
        		0x0000001A SGD_EXT_CRLDISTRIBUTIONPOINTS_INFO CRL		发布点
        		0x0000001B SGD_EXT_NETSCAPE_CERT_TYPE_INFO				Netscape属性
        		0x0000001C SGD_EXT_SELFDEFINED_EXTENSION_INFO			私有的自定义扩展项
        		0x00000021 SGD_CERT_ISSUER_CN							证书颁发者CN
        		0x00000022 SGD_CERT_ISSUER_O							证书颁发者
        		0x00000023 SGD_CERT_ISSUER_OU							证书颁发者OU
        		0x00000031 SGD_CERT_SUBJECT_CN							证书拥有者信息CN
        		0x00000032 SGD_CERT_SUBJECT_O							证书拥有者信息O
        		0x00000033 SGD_CERT_SUBJECT_OU							证书拥有者信息OU
        		0x00000034 SGD_CERT_SUBJECT_EMAIL						证书拥有者信息EMAIL
        		0x00000035 SGD_CERT_NOTBEFORE_TIME						证书起始日期
        		0x00000036 SGD_CERT_NOTAFTER_TIME						证书截止日期
	返回值：
	      证书内指定类型的字符串信息
	      失败：""空字符串
	*/
	String SOF_GetCertInfo(in String b64cert, in int type);

    /*
    获取证书扩展信息
   	    [IN]b64cert:Base64编码的签名证书
        [IN]oid:私有扩展对象ID
    返回值：
        成功：证书内指定OID对应的信息
        失败：""空字符串
    */
	String SOF_GetCertInfoByOid(in String b64cert,in String oid);

   /*
    设置签名算法
        [IN]SignMethod:签名算法标识，详见GM/T 0006
    返回值：
        ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                        调用失败：errorCode = 1
                                                 message 为错误消息
                                                 detail 为错误详细日志
         目前支持的算法如下
         SGD_SM3_RSA 0x00010001 基于SM3算法和RSA算法的签名 SM3WithRSA
         SGD_SHA1_RSA 0x00010002 基于SHA_1算法和RSA算法的签名 SHA1WithRSA
         SGD_SHA256_RSA 0x00010004 基于SHA_256算法和RSA算法的签名 SHA256WithRSA
         SGD_SM3_SM2 0x00020201 基于SM3算法和SM2算法的签名 SM3WithSM2
    */
    ResultBean SOF_SetSignMethod(in long SignMethod);

    /*
    获取签名算法
    返回值：
        非0：当前接口使用的签名算法的预定义值
        0：没有设置签名算法
    */
    long SOF_GetSignMethod();

	/*
	对数据进行签名
      [IN]plainText:原文的byte[]
	    [OUT]signature:签名原文经过Base64.encode处理后的byte[]
	返回值：
      ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                      调用失败：errorCode = 1
                                               message 为错误消息
                                               detail 为错误详细日志
	*/
	ResultBean SOF_SignData(in byte[] plainText,out ByteBuf signature);

	/*
	验证数字签名
	    [IN]b64cert:Base64编码的签名证书
	    [IN]plainText:原文的byte[]
	    [IN]signature:签名原文经过Base64.encode处理后的byte[]
	返回值：
		  ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                      调用失败：errorCode = 1
                                               message 为错误消息
                                               detail 为错误详细日志
	*/
	ResultBean SOF_VerifySignedData(in String b64cert,in byte[] plainText, in byte[] signature);

    /*
    消息签名
        目前仅支持带原文消息签名，isOrigin参数为预留
        [IN]isOrigin:是否带原文的标识：1.不带原文；0.带原文。
        [IN]plainText:原文的byte[]
        [OUT]signature:签名原文经过Base64.encode处理后的byte[]
    返回值：
        ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                        调用失败：errorCode = 1
                                                 message 为错误消息
                                                 detail 为错误详细日志
    */
    ResultBean SOF_SignMessage(in int isWithOrigin,in byte[] plainText,out ByteBuf signature);

    /*
    验证消息签名
        [IN]plainText:原文的byte[],目前仅支持待原文消息签名，plainText参数为预留，目前直接传"".getBytes()
        [IN]signature:签名原文经过Base64.encode处理后的byte[]
    返回值：
        ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                        调用失败：errorCode = 1
                                                 message 为错误消息
                                                 detail 为错误详细日志
    */
    ResultBean SOF_VerifySignedMessage(in byte[] plainText, in byte[] signature);

	 /*
     设置加密算法
        [IN]EncryptMethod:对称加解密算法标识，详见GM/T 0006
     返回值：
        ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                        调用失败：errorCode = 1
                                                 message 为错误消息
                                                 detail 为错误详细日志
      目前支持的算法:
       SM4_ECB 0x40011002 CIPHER_SM4_ECB
       SM4_CBC 0x40011003 CIPHER_SM4_CBC
       AES_128_ECB 0x40011004 CIPHER_AES_128_ECB
       AES_128_CBC 0x40011005 CIPHER_AES_128_CBC
       AES_256_ECB 0x00004004 CIPHER_AES_256_ECB
       AES_256_CBC 0x00004008 CIPHER_AES_256_CBC
      */
    ResultBean SOF_SetEncryptMethod(in long encryptMethod);

    /*
     获取加密算法
     返回值：
        非0：当前接口使用的加密算法的预定义值
        0：没有设置加密算法
    */
    long SOF_GetEncryptMethod();

	/*
	对称密钥加密
	    [IN]key:对称密钥的二进制值
	    [IN]plainText:待加密的数据的byte[]
	    [IN]symmAlgName:加密算法，传入时必须使用“算法/加密方式/填充方式”，如"SM4/ECB/PKCS5Padding"
	    [IN]iv:向量
	    [OUT]cipherText:加密出的密文的byte[]
	返回值：
      ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                      调用失败：errorCode = 1
                                               message 为错误消息
                                               detail 为错误详细日志
  备注：目前支持的算法
      SM4/ECB/NoPadding
      SM4/ECB/PKCS5Padding
      SM4/CBC/NoPadding
      SM4/CBC/PKCS5Padding
      AES/ECB/NoPadding
      AES/ECB/PKCS5Padding
      AES/CBC/NoPadding
      AES/CBC/PKCS5Padding

	*/
	ResultBean _SOF_symmetricEnc(in byte[] key,in byte[] plainText,in String symmAlgName,in byte[] iv,out ByteBuf cipherText);

    /*
    对称密钥解密
        [IN]key:对称密钥的二进制值
        [IN]cipherText:待解密的数据的byte[]
        [IN]symmAlgName:加密算法，传入时必须使用“算法/加密方式/填充方式”，如"SM4/ECB/PKCS5Padding"
        [IN]iv:向量
        [OUT]plainText:解密出的密文的byte[]
    返回值：
         ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                         调用失败：errorCode = 1
                                                  message 为错误消息
                                                  detail 为错误详细日志
    */
    ResultBean _SOF_symmetricDec(in byte[] key,in byte[] cipherText,in String symmAlgName,in byte[] iv,out ByteBuf plainText);

    /*
    加密数据 数字信封
        [IN]b64encCert:base64编码的加密用的数字证书
        [IN]plainText:待加密的明文的byte[]
        [OUT]envelope:数字信封经过Base64.encode处理后的byte[]
    返回值：
    ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                    调用失败：errorCode = 1
                                             message 为错误消息
                                             detail 为错误详细日志
    */
    ResultBean SOF_EncryptData(in String b64encCert,in byte[] plainText,out ByteBuf envelope);

    /*
    解密数据
        [IN]envelope:数字信封经过Base64.encode处理后的byte[]
        [OUT]plainText:解密后明文的byte[]
    返回值：
        ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                        调用失败：errorCode = 1
                                                 message 为错误消息
                                                 detail 为错误详细日志
    */
    ResultBean SOF_DecryptData(in byte[] envelope,out ByteBuf plainText);

    /*
    三段式加密cipher初始化
        [IN]key:对称密钥的byte[]
        [IN]symmAlgName:加密算法，传入时必须使用“算法/加密方式/填充方式”，如"SM4/ECB/PKCS5Padding",详细参考“_SOF_symmetricEnc”中的定义
        [IN]iv:向量的byte[]
        [IN]mode:加密模式 javax.crypto.Cipher.ENCRYPT_MODE
                 者解密模式 javax.crypto.Cipher.DECRYPT_MODE
    返回值：
        加密对象句柄
    */
    long SOF_SymInit(in byte[] key,in String symmAlgName,in byte[] iv,in int mode);

    /*
    三段式加密update
        [IN]handle:加密对象句柄,即SOF_SymInit返回值
        [IN]plainText:待处理的数据的byte[]
        [OUT]cipherText:加密出的结果的byte[]
    返回值：
        ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                        调用失败：errorCode = 1
                                                 message 为错误消息
                                                 detail 为错误详细日志
    */
    ResultBean SOF_SymUpdate(in long handle,in byte[] plainText, out ByteBuf cipherText);

    /*
    三段式加密DoFinal
        [IN]handle:加密对象句柄,即SOF_SymInit返回值
        [OUT]cipherTextEnd:结束加密出的byte[]
    返回值：
        ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                        调用失败：errorCode = 1
                                                 message 为错误消息
                                                 detail 为错误详细日志
    */
    ResultBean SOF_SymDoFinal(in long handle,out ByteBuf cipherTextEnd);


    /*
    裸签。SKF_RSASignData、SKF_ECCSignData默认使用PKCS1填充，不对输入的数据做摘要，区别于“SOF_SignData”。
        [IN]plainText:原文的byte[]
 	      [OUT]signature:签名原文(密文)byte[]
 	返回值：
       ResultBean(详见ResultBean.java)：调用成功：errorCode = 0
                                       调用失败：errorCode = 1
                                                message 为错误消息
                                                detail 为错误详细日志

    */
    ResultBean SKF_NakedSignData(in byte[] plainText,out ByteBuf signature);

}
