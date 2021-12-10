package cx.ath.chuacw.Coinbase;

public class Constants {
    // For comparing to .type field in message classes
    public static final String ERROR = "error";
    public static final String HEARTBEAT = "heartbeat";
    public static final String L2UPDATE = "l2update";
    public static final String SNAPSHOT = "snapshot";
    public static final String SUBSCRIPTIONS = "subscriptions";
    public static final String TICKER =  "ticker";

    // For comparing to order book
    public static final String BUY = "buy";
    public static final String SELL = "sell";

    // For output to console
    public static final String headerBUY = "BUY";
    public static final String headerSELL = "SELL";

    // for limiting number of items in order book.
    public static final int displayLEVELS = 10;
}
