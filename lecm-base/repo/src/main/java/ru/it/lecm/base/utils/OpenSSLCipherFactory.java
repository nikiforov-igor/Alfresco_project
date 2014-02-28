package ru.it.lecm.base.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author vkuprin
 */
public class OpenSSLCipherFactory {

    
    private static final String ALGO = "AES";
    private static final String ALGO_MODE = "CBC";
    private static final String PADDING = "PKCS5Padding";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String DIGEST = "MD5";
    private static final Integer ITERATIONS = 1;

    
      /**
     * Thanks go to Ola Bini for releasing this source on his blog.
     * The source was obtained from <a href="http://olabini.com/blog/tag/evp_bytestokey/">here</a> .
     */
    private static byte[][] EVP_BytesToKey(int key_len, int iv_len, MessageDigest md,
        byte[] salt, byte[] data, int count) {
        byte[][] both = new byte[2][];
        byte[] key = new byte[key_len];
        int key_ix = 0;
        byte[] iv = new byte[iv_len];
        int iv_ix = 0;
        both[0] = key;
        both[1] = iv;
        byte[] md_buf = null;
        int nkey = key_len;
        int niv = iv_len;
        int i = 0;
        if (data == null) {
            return both;
        }
        int addmd = 0;
        for (;;) {
            md.reset();
            if (addmd++ > 0) {
                md.update(md_buf);
            }
            md.update(data);
            if (null != salt) {
                md.update(salt, 0, 8);
            }
            md_buf = md.digest();
            for (i = 1; i < count; i++) {
                md.reset();
                md.update(md_buf);
                md_buf = md.digest();
            }
            i = 0;
            if (nkey > 0) {
                for (;;) {
                    if (nkey == 0) {
                        break;
                    }
                    if (i == md_buf.length) {
                        break;
                    }
                    key[key_ix++] = md_buf[i];
                    nkey--;
                    i++;
                }
            }
            if (niv > 0 && i != md_buf.length) {
                for (;;) {
                    if (niv == 0) {
                        break;
                    }
                    if (i == md_buf.length) {
                        break;
                    }
                    iv[iv_ix++] = md_buf[i];
                    niv--;
                    i++;
                }
            }
            if (nkey == 0 && niv == 0) {
                break;
            }
        }
        for (i = 0; i < md_buf.length; i++) {
            md_buf[i] = 0;
        }
        return both;
    }

    public static Cipher getInstance(byte[] passwd, byte[] salt, Integer mode, Integer keySizeBits) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        MessageDigest digest = MessageDigest.getInstance(DIGEST);

        // the IV is useless, OpenSSL might as well have use zero's
        final byte[][] keyAndIV = EVP_BytesToKey(
            keySizeBits / Byte.SIZE,
            cipher.getBlockSize(),
            digest,
            salt,
            passwd,
            ITERATIONS);
        SecretKeySpec key = new SecretKeySpec(keyAndIV[0], ALGO);
        IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);
        // --- initialize cipher instance and decrypt ---

        cipher.init(mode, key, iv);
        return cipher;
    }

}
