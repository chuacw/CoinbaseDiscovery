package cx.ath.chuacw.Coinbase.messages.responses;

import java.util.List;

public class SubscriptionsMessage extends BaseTypeMessage {
    public List<Channel> channels;

    @Override
    public String toString() {
        final String result = java.util.Arrays.toString(channels.toArray());
        return result;
    }
}
