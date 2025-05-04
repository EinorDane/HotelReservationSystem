package com.hotelres.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    // In production, do not hardcode keys; securely store them instead.
    private static final byte[] KEY_BYTES = "1234567890123456".getBytes();

    public static String encrypt(String data) throws Exception {
         Cipher cipher = Cipher.getInstance(ALGORITHM);
         SecretKeySpec keySpec = new SecretKeySpec(KEY_BYTES, ALGORITHM);
         cipher.init(Cipher.ENCRYPT_MODE, keySpec);
         byte[] encrypted = cipher.doFinal(data.getBytes());
         return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData) throws Exception {
         Cipher cipher = Cipher.getInstance(ALGORITHM);
         SecretKeySpec keySpec = new SecretKeySpec(KEY_BYTES, ALGORITHM);
         cipher.init(Cipher.DECRYPT_MODE, keySpec);
         byte[] decoded = Base64.getDecoder().decode(encryptedData);
         byte[] decrypted = cipher.doFinal(decoded);
         return new String(decrypted);
    }
}