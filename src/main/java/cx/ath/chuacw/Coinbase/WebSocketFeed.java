package cx.ath.chuacw.Coinbase;


import cx.ath.chuacw.Coinbase.Handlers.MessageHandler;
import cx.ath.chuacw.Coinbase.messages.SignedSubscribeMessage;
import cx.ath.chuacw.Coinbase.messages.SubscribeMessage;

import java.net.http.WebSocket;

import static cx.ath.chuacw.WebSocket.Utils.getWebSocket;
import static cx.ath.chuacw.WebSocket.Utils.toJSON;

public class WebSocketFeed {

    private WebSocket ws;
    private String passphrase;
    private String key;
    private String signature;

    public WebSocketFeed(String url, MessageHandler handler) {
        ws = getWebSocket(url, handler);
    }

    public void setPassphrase(String newPassphrase) {
        this.passphrase = newPassphrase;
    }

    public void setSecretKey(String newKey) {
        this.key = newKey;
    }

    public void Subscribe(SubscribeMessage msg) {

        SignedSubscribeMessage signedMessage = new SignedSubscribeMessage(msg);
        String signedMsgValue = toJSON(msg);

        ws.sendText(signedMsgValue, true);

    }

}
