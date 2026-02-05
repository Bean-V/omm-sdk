package com.oort.weichat.util.secure;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * 简单加密工具类
 * 使用AES对称加密，密钥基于用户ID生成
 */
public class SimpleEncryptUtil {
    
    private static final String TAG = "SimpleEncryptUtil";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String ENCRYPT_PREFIX = "SIMPLE_ENCRYPT:"; // 密聊消息标识前缀
    
    /**
     * 生成基于用户ID的密钥
     */
    private static SecretKey generateKey(String userId) {
        try {
            Log.e(TAG, "=== 生成密钥 ===");
            Log.e(TAG, "用户ID: " + userId);
            
            // 使用用户ID的MD5作为种子生成固定密钥
            String keyString = MD5.encryptHex(userId + "simple_encrypt_key");
            Log.e(TAG, "MD5结果: " + keyString);
            
            byte[] keyBytes = keyString.substring(0, 16).getBytes("UTF-8"); // 取前16位作为AES密钥
            Log.e(TAG, "密钥字节长度: " + keyBytes.length);
            
            SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
            Log.e(TAG, "密钥生成成功");
            Log.e(TAG, "=== 密钥生成完成 ===");
            
            return key;
        } catch (Exception e) {
            Log.e(TAG, "生成密钥失败", e);
            Log.e(TAG, "异常详情: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 加密消息
     */
    public static String encrypt(String message, String userId) {
        try {
            if (message == null || message.isEmpty()) {
                return message;
            }
            
            SecretKey key = generateKey(userId);
            if (key == null) {
                return message;
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(message.getBytes("UTF-8"));
            
            // 添加密聊标识前缀
            return ENCRYPT_PREFIX + Base64.encodeToString(encrypted, Base64.DEFAULT);
            
        } catch (Exception e) {
            Log.e(TAG, "加密失败", e);
            return message; // 加密失败返回原文
        }
    }
    
    /**
     * 解密消息
     */
    public static String decrypt(String encryptedMessage, String userId) {
        try {
            Log.e(TAG, "=== 开始解密 ===");
            Log.e(TAG, "加密消息: " + encryptedMessage);
            Log.e(TAG, "用户ID: " + userId);
            
            if (encryptedMessage == null || encryptedMessage.isEmpty()) {
                Log.e(TAG, "消息为空，返回原文");
                return encryptedMessage;
            }
            
            // 检查是否有密聊标识前缀
            if (!encryptedMessage.startsWith(ENCRYPT_PREFIX)) {
                Log.e(TAG, "没有密聊前缀，返回原文");
                return encryptedMessage; // 不是密聊消息，返回原文
            }
            
            // 移除前缀，获取实际的加密内容
            String actualEncrypted = encryptedMessage.substring(ENCRYPT_PREFIX.length());
            Log.e(TAG, "实际加密内容: " + actualEncrypted);
            
            SecretKey key = generateKey(userId);
            if (key == null) {
                Log.e(TAG, "密钥生成失败，返回原文");
                return encryptedMessage;
            }
            
            Log.e(TAG, "密钥生成成功");
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            byte[] encryptedBytes = Base64.decode(actualEncrypted, Base64.DEFAULT);
            Log.e(TAG, "Base64解码后的字节长度: " + encryptedBytes.length);
            
            byte[] decrypted = cipher.doFinal(encryptedBytes);
            String result = new String(decrypted, "UTF-8");
            
            Log.e(TAG, "解密成功: " + result);
            Log.e(TAG, "=== 解密完成 ===");
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "解密失败", e);
            Log.e(TAG, "异常详情: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return encryptedMessage; // 解密失败返回原文
        }
    }
    
    /**
     * 检查消息是否已加密
     */
    public static boolean isEncrypted(String message) {
        try {
            if (message == null || message.isEmpty()) {
                return false;
            }
            
            // 检查是否有密聊标识前缀
            boolean isEncrypted = message.startsWith(ENCRYPT_PREFIX);
            Log.e(TAG, "检查加密状态: " + message + " -> " + isEncrypted);
            return isEncrypted;
            
        } catch (Exception e) {
            Log.e(TAG, "检查加密状态失败", e);
            return false;
        }
    }
    
    /**
     * 测试密聊功能
     */
    public static void testEncrypt() {
        String testMessage = "这是一条测试消息";
        String testUserId = "test_user_123";
        
        Log.e(TAG, "=== 密聊功能测试 ===");
        Log.e(TAG, "原始消息: " + testMessage);
        
        String encrypted = encrypt(testMessage, testUserId);
        Log.e(TAG, "加密后: " + encrypted);
        
        boolean isEncrypted = isEncrypted(encrypted);
        Log.e(TAG, "是否识别为加密: " + isEncrypted);
        
        String decrypted = decrypt(encrypted, testUserId);
        Log.e(TAG, "解密后: " + decrypted);
        
        Log.e(TAG, "测试结果: " + (testMessage.equals(decrypted) ? "成功" : "失败"));
        Log.e(TAG, "==================");
    }
    
    /**
     * 测试名片密聊功能
     */
    public static void testCardEncrypt() {
        String testNickName = "测试用户";
        String testUserId = "test_user_456";
        String cardInfo = testNickName + "|" + testUserId;
        
        Log.e(TAG, "=== 名片密聊功能测试 ===");
        Log.e(TAG, "原始名片信息: " + cardInfo);
        
        String encrypted = encrypt(cardInfo, testUserId);
        Log.e(TAG, "加密后: " + encrypted);
        
        boolean isEncrypted = isEncrypted(encrypted);
        Log.e(TAG, "是否识别为加密: " + isEncrypted);
        
        String decrypted = decrypt(encrypted, testUserId);
        Log.e(TAG, "解密后: " + decrypted);
        
        // 解析解密后的名片信息
        String[] cardInfoArray = decrypted.split("\\|");
        if (cardInfoArray.length >= 2) {
            String decryptedNickName = cardInfoArray[0];
            String decryptedUserId = cardInfoArray[1];
            Log.e(TAG, "解析后的昵称: " + decryptedNickName);
            Log.e(TAG, "解析后的用户ID: " + decryptedUserId);
            Log.e(TAG, "测试结果: " + (cardInfo.equals(decrypted) ? "成功" : "失败"));
        } else {
            Log.e(TAG, "测试结果: 解析失败");
        }
        
        Log.e(TAG, "======================");
    }
}
