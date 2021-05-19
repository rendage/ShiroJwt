package com.wang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author Mr.ren
 * @destciption AES decrypt encrypt
 * @date 2021/5/17 15:34
 */
public class AESUtils {

    private static Logger log = LoggerFactory.getLogger(AESUtils.class);
    private static String ENCRYPT_TYPE = "AES";
    private static String UNICODE = "UTF-8";
    private static String AES_KEY = UUID.randomUUID().toString().replaceAll("-","").substring(20);

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  秘钥
     * @return
     */
    public static String encrypt(String data, String key) {
        return doAES(data, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param data 带解密的内容
     * @param key  秘钥
     * @return
     */
    public static String decrypt(String data, String key) {
        return doAES(data, key, Cipher.DECRYPT_MODE);
    }


    /**
     * 加解密
     *
     * @param data 待处理的数据
     * @param key  秘钥
     * @param encryptMode 加解密密码
     * @return
     */
    private static String doAES(String data, String key, int encryptMode) {
        try {
            if (StringUtils.isEmpty(data) || StringUtils.isEmpty(key)) { return null; }
            boolean encrypt = encryptMode == Cipher.ENCRYPT_MODE;
            byte[] content;
            // true加密 false解密
            if (encrypt) {
                content = data.getBytes(UNICODE);
            } else {
                content = parseHexStr2Byte(data);
            }
            // 构造秘钥生成器
            KeyGenerator kgen = KeyGenerator.getInstance(ENCRYPT_TYPE);
            kgen.init(128, new SecureRandom(key.getBytes(UNICODE)));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            // 生成AES密钥
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, ENCRYPT_TYPE);
            // 初始化密码器
            Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE);
            cipher.init(encryptMode, keySpec);
            byte[] result = cipher.doFinal(content);
            if (encrypt) {
                return parseByte2HexStr(result);
            } else {
                return new String(result, UNICODE);
            }
        } catch (Exception e) {
            if (encryptMode == Cipher.ENCRYPT_MODE) {
                log.error("AES 加密处理异常：异常信息{} \r\n密文{}\r\n秘钥{}", e.getMessage(),data,key);
            }
            if (encryptMode == Cipher.DECRYPT_MODE) {
                log.error("AES 解密处理异常：异常信息{} \r\n密文{}\r\n 秘钥{}", e.getMessage(),data,key);
            }
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String content = "{'repairPhone':'18547854787','customPhone':'12365478965','captchav':'58m7'}";
        System.out.println("加密前：" + content);
        System.out.println("密钥：" + AES_KEY);
        String encrypt = encrypt(content, AES_KEY);
        System.out.println("加密后：" + encrypt);
        String decrypt = decrypt(encrypt, "1");
        System.out.println("解密后：" + decrypt);

    }


}
