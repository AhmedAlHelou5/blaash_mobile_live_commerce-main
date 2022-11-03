package social.gaming.blaash;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static Cipher aesCipher;
    private static SecretKey secretKey;
    private static IvParameterSpec ivParameterSpec;

    private static String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static String CIPHER_ALGORITHM = "AES";
    // Replace me with a 16-byte key, share between Java and C#
    private static byte[] rawSecretKey = {0x10, 0x2E, 0x3A, 0x2F, 0x4A, 0x3F, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x11, 0x2A, 0x11};

    private static String MESSAGEDIGEST_ALGORITHM = "MD5";

    protected Crypto(String passphrase) {
        byte[] passwordKey = encodeDigest(passphrase);

        try {
            aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {
        }

        secretKey = new SecretKeySpec(passwordKey, CIPHER_ALGORITHM);
        ivParameterSpec = new IvParameterSpec(rawSecretKey);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encryptAsBase64(byte[] clearData) throws UnsupportedEncodingException {
        byte[] encryptedData = encrypt(clearData);
//        return Base64.getEncoder().encodeToString(encryptedData);
        return URLEncoder.encode(Base64.getEncoder().encodeToString(encryptedData), StandardCharsets.UTF_8.toString());
    }

    public byte[] encrypt(byte[] clearData) {
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            return null;
        }

        byte[] encryptedData;
        try {
            encryptedData = aesCipher.doFinal(clearData);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
        return encryptedData;
    }

    private byte[] encodeDigest(String text) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(MESSAGEDIGEST_ALGORITHM);
            return digest.digest(text.getBytes());
        } catch (NoSuchAlgorithmException ignored) {
        }

        return null;
    }
}
