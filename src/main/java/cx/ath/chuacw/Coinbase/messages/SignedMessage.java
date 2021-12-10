package cx.ath.chuacw.Coinbase.messages;

import cx.ath.chuacw.CryptoUtils.HMAC;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;
import java.security.InvalidKeyException;
import java.util.Base64;

public class SignedMessage {
    private String msg;
    private String secretKey;
    private String timestamp;

    public SignedMessage(String newKey, String newTimestamp, String newMsg) {
        this.msg = newMsg;
        this.secretKey = newKey;
        this.timestamp = newTimestamp;
    }

    @Override
    public String toString() {
        final String method = "GET";
        final String requestPath = "/users/self/verify";
        final String timestamp = this.timestamp; // String.valueOf(Instant.now().getEpochSecond());
        final String body = msg;
        try {
            String prehash = timestamp + method.toUpperCase() + requestPath + body;
            byte[] secretDecoded = Base64.getDecoder().decode(this.secretKey);
            SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, HMAC.getMac().getAlgorithm());
            Mac sha256 = (Mac) HMAC.getMac();
            sha256.init(keyspec);
            String result = Base64.getEncoder().encodeToString(sha256.doFinal(prehash.getBytes()));
            return result;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeErrorException(new Error("Cannot set up authentication headers."));
        }

    }
}
