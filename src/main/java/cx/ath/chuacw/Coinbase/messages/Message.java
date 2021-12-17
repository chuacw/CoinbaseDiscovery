package cx.ath.chuacw.Coinbase.messages;

public class Message extends BaseMessage {
    private String type;

    public Message() {
        super();
    }

    public String getType() {
        return this.type;
    }

    public void setType(String newType) {
        this.type = newType;
    }

}
