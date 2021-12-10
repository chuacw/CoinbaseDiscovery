package cx.ath.chuacw.Coinbase.messages.responses;

/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */

import java.util.List;

public class SubscriptionsMessage extends BaseTypeMessage {
    public List<Channel> channels;
}
