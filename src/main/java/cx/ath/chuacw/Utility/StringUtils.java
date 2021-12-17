package cx.ath.chuacw.Utility;

public class StringUtils {
    public static boolean isEmpty(String src) {
        final boolean result = (src == null) || (src.length() == 0);
        return result;
    }
}
