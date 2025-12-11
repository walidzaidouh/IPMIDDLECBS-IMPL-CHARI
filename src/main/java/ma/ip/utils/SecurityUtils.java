package ma.ip.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecurityUtils {

    private static String HMAC_SHA256 = "HmacSHA256";

    public static String getRequestSignature(String message, String secret) {
        // Convert secret key to bytes
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, HMAC_SHA256);

        // Create Mac instance and initialize
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA256);
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | java.security.InvalidKeyException e) {
            e.printStackTrace();
        }

        // Compute the HMAC
        byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        // Encode to Base64
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
}
