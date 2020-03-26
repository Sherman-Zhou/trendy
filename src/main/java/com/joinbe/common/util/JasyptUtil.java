package com.joinbe.common.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtil {

    /**
     * Jasypt生成加密结果
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待加密值
     * @return
     */
    public static String encyptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(cryptor(password));
        String result = encryptor.encrypt(value);
        return result;
    }

    /**
     * 解密
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待解密密文
     * @return
     */
    public static String decyptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(cryptor(password));
        String result = encryptor.decrypt(value);
        return result;
    }

    public static SimpleStringPBEConfig cryptor(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize(1);
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        return config;
    }

    public static void main(String[] args) {
         if(args == null || args.length < 3) {
             System.out.println("Usage: ");
             System.out.println("Encrypt: JasyptUtil -e password value");
             System.out.println("Decrypt: JasyptUtil -d password value");
             System.exit(1);
         }
        //加密
        if("-e".equalsIgnoreCase(args[0])){
            System.out.println("encrypted result: ");
            System.out.println(encyptPwd(args[1], args[2]));
        }else  if("-d".equalsIgnoreCase(args[0])){
            System.out.println("decrypted result: ");
            System.out.println(decyptPwd(args[1], args[2]));
        }else {
            System.err.println("Unknown Option:"+ args[0]);
        }
    }
}
