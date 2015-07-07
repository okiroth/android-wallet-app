package com.palebluepagos.androidwallet.utilities;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 *
 * @author ferenc.hechler
 */
public class SimpleCrypto {

    // random default seed used
    // TODO replace this with an algorithm
    public static String SEED_DEFAULT = "d5234h6789ab6de1";

    public static String encrypt(String inString, String seed) {
        SecretKey key = new SecretKeySpec(seed.getBytes(), "AES/ECB/PKCS5Padding");

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] inputByte = inString.getBytes("UTF-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.encode(cipher.doFinal(inputByte), Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String decrypt(String inString, String seed) {
        SecretKey key = new SecretKeySpec(seed.getBytes(), "AES/ECB/PKCS5Padding");

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] inputByte = inString.getBytes("UTF-8");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.decode(inputByte, Base64.NO_WRAP)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}