package cx.ath.chuacw.Utility;

import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    public static Mac getMac() {
        Mac result = null;
        try {
            result = (Mac) Mac.getInstance("HmacSHA256").clone();
        } catch (NoSuchAlgorithmException | CloneNotSupportedException nsaEx) {
            nsaEx.printStackTrace();
        }
        return result;
    }

}
