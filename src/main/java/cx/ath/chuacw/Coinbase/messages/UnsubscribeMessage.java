package cx.ath.chuacw.Coinbase.messages;

public class UnsubscribeMessage extends SubscribeMessage {
    public UnsubscribeMessage() {
        super();
        this.setType("unsubscribe");
    }
}
