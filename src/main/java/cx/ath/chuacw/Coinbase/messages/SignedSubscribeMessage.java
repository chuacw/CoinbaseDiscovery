package cx.ath.chuacw.Coinbase.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import cx.ath.chuacw.Utility.Crypto;
import cx.ath.chuacw.Utility.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Base64;

import static cx.ath.chuacw.Utility.WebSocket.toJSON;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignedSubscribeMessage extends SubscribeMessage {
    private String mSignature;
    private String mKey;
    private String mPassphrase;
    private String mTimestamp;

    public SignedSubscribeMessage(SubscribeMessage subscribeMsg) {
        super();
        this.setType(subscribeMsg.getType());
        this.setProductIds(subscribeMsg.getProductIds());
        this.addChannels(subscribeMsg.getChannels());
    }

    public SignedSubscribeMessage(SubscribeMessage subscribeMsg, String secret, String apikey, String passphrase) {
        super();
        this.setType(subscribeMsg.getType());
        this.setProductIds(subscribeMsg.getProductIds());
        this.addChannels(subscribeMsg.getChannels());
        if (!StringUtils.isEmpty(secret) && !StringUtils.isEmpty(apikey) && !StringUtils.isEmpty(passphrase)) {
            this.mTimestamp = String.valueOf(Instant.now().getEpochSecond());
            final String what = mTimestamp + "GET" + "/users/self/verify";
            this.mSignature = sign(what, secret, apikey, passphrase);
        }
    }

    public String getKey() {
        return this.mKey;
    }

    public void setKey(String newKey) {
        this.mKey = newKey;
    }

    public String getPassphrase() {
        return this.mPassphrase;
    }

    public void setPassphrase(String newPassphrase) {
        this.mPassphrase = newPassphrase;
    }

    public String getSignature() {
        return this.mSignature;
    }

    public void setSignature(String newSignature) {
        this.mSignature = newSignature;
    }

    public String getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(String newTimestamp) {
        this.mTimestamp = newTimestamp;
    }

    private String sign(String what, String secret, String apikey, String passphrase) {
        try {
            this.mKey = apikey;
            this.mPassphrase = passphrase;

            final byte[] secretDecoded = Base64.getDecoder().decode(secret);
            final Mac sha256 = Crypto.getMac();
            final SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, sha256.getAlgorithm());
            sha256.init(keyspec);
            final String result = Base64.getEncoder().encodeToString(sha256.doFinal(what.getBytes()));
            return result;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeErrorException(new Error("Cannot set up authentication headers."));
        }

    }

    @Override
    public String toString() {
        // This must be called within 30 secs after the constructor got called.
        final String result = toJSON(this);
        return result;
    }

}
