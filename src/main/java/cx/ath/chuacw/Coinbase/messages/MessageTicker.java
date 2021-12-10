package cx.ath.chuacw.Coinbase.messages;

public class MessageTicker extends BaseMessage {
    private String name;

    public MessageTicker() {
        this.name = "ticker";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }
}
