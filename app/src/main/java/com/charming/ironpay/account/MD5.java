package com.charming.ironpay.account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by cm on 16/7/5.
 */
public class MD5 {

    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }
    private static String getString(byte[] bytes){
//        StringBuffer sb = new StringBuffer();
//        for(int i = 0; i < b.length; i ++){
//            sb.append(b[i]);
//        }
//        return sb.toString();

        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}