package cx.ath.chuacw.Coinbase.messages;

import cx.ath.chuacw.CryptoUtils.HMAC;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Base64;

import static cx.ath.chuacw.WebSocket.Utils.toJSON;

public class SignedSubscribeMessage extends SubscribeMessage {
    private String signature;
    private String key;
    private String passphrase;
    private String timestamp;
    private SubscribeMessage msg;

    public SignedSubscribeMessage(SubscribeMessage subscribeMsg) {
        this.msg = subscribeMsg;
        this.setType(msg.getType());
        this.setProductIds(msg.getProductIds());
        this.addChannels(msg.getChannels());
        this.timestamp = String.valueOf(Instant.now().getEpochSecond());
    }

    public String getKey() {
        return this.key;
    }
    public void setKey(String newKey) {
        this.key = newKey;
    }

    public String getPassphrase() {
        return this.passphrase;
    }
    public void setPassphrase(String newPassphrase) {
        this.passphrase = newPassphrase;
    }

    public String getSignature() {
        return this.signature;
    }
    public void setSignature(String newSignature) {
        this.signature = newSignature;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(String newTimestamp) {
        this.timestamp = newTimestamp;
    }

    @Override
    public String toString() {
        final String method = "GET";
        final String requestPath = "/users/self/verify";
        final String timestamp = this.timestamp; // String.valueOf(Instant.now().getEpochSecond());
        final String body = toJSON(msg);
        try {
            String prehash = timestamp + method.toUpperCase() + requestPath + body;
            byte[] secretDecoded = Base64.getDecoder().decode(this.key);
            Mac sha256 = (Mac) HMAC.getMac();
            SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, sha256.getAlgorithm());
            sha256.init(keyspec);
            this.signature = Base64.getEncoder().encodeToString(sha256.doFinal(prehash.getBytes()));
            return toJSON(this);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeErrorException(new Error("Cannot set up authentication headers."));
        }

    }

}
