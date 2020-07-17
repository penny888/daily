package com.daily.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class EncryptUtil {

    // 可配置到Constant中，并读取配置文件注入,16位,自己定义
    private static final String AES_KEY = "20200517daily888";

    // 参数分别代表 算法名称/加密模式/数据填充方式
    private static final String AES_ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    // 摘要算法
    public static String getMDString(String input, String algo) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algo);
        byte[] result = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuffer sb;
        sb = new StringBuffer();
        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }


    /**
     * 加密
     *
     * @param content    加密的字符串
     * @param encryptKey key值
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_STR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        byte[] b = cipher.doFinal(content.getBytes("utf-8"));
        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.encodeBase64String(b);

    }

    /**
     * 解密
     *
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     * @return String
     * @throws Exception e
     */
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_STR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        // 采用base64算法进行转码,避免出现中文乱码
        byte[] encryptBytes = Base64.decodeBase64(encryptStr);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String encrypt(String content) throws Exception {
        return encrypt(content, AES_KEY);
    }

    public static String decrypt(String encryptStr) throws Exception {
        return decrypt(encryptStr, AES_KEY);
    }


    public static void main(String[] args) throws Exception {
        // Map<String, String> map = new HashMap<>();
        // map.put("key", "value");
        // map.put("中文", "汉字");
        // String content = JSONObject.toJSONString(map);
        // System.out.println("加密前：" + content);
        // String encrypt = encrypt(content, AES_KEY);
        // System.out.println("加密后：" + encrypt);
        // String decrypt = decrypt(encrypt, AES_KEY);
        // System.out.println("解密后：" + decrypt);

        // jasypt加密
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //1.加密所需的salt(盐)，此处的值要与application中的配置的password一样。 application.yml中配置：jasypt.encryptor.password=zrxJuly
        textEncryptor.setPassword("abc123");
        // //2.要加密的数据.运行完main方法后，将打印出的加密内容在application.yml相关参数中替换：
        String user = textEncryptor.encrypt("root");
        String password = textEncryptor.encrypt("root");
        // application.yml中替换：ENC(jdbcUrl)   ENC(password)
        System.out.println("user="+user);
        System.out.println("password="+password);
    }

}
