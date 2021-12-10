package cx.ath.chuacw.CryptoUtils;

import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;

public class HMAC {

    public static Mac getMac() {
        Mac result = null;
        try {
            result = (Mac) Mac.getInstance("HmacSHA256").clone();
        } catch (NoSuchAlgorithmException | CloneNotSupportedException nsaEx) {
            nsaEx.printStackTrace();
        }
        return result;
    };

}
