package cx.ath.chuacw.Coinbase;

import cx.ath.chuacw.Coinbase.Handlers.MessageHandler;
import cx.ath.chuacw.Coinbase.messages.SignedSubscribeMessage;
import cx.ath.chuacw.Coinbase.messages.SubscribeMessage;
import cx.ath.chuacw.Coinbase.messages.UnsubscribeMessage;

import java.net.http.WebSocket;

import static cx.ath.chuacw.Utility.WebSocket.getWebSocket;
import static cx.ath.chuacw.Utility.WebSocket.toJSON;

public class WebSocketFeed {

    private final WebSocket mWebSocket;
    private final MessageHandler mMsgHandler;
    private String mPassphrase;
    private String mApiKey;
    private String mSecret;

    public WebSocketFeed(String url, MessageHandler handler) {
        super();
        this.mMsgHandler = handler;
        this.mWebSocket = getWebSocket(url, handler);
    }

    public void setAPIKey(String newAPIkey) {
        this.mApiKey = newAPIkey;
    }

    public void setPassphrase(String newPassphrase) {
        this.mPassphrase = newPassphrase;
    }

    public void setAPISecret(String newSecret) {
        this.mSecret = newSecret;
    }

    public void Subscribe(SubscribeMessage msg) {
        SignedSubscribeMessage signedMessage = new SignedSubscribeMessage(msg,
                mSecret, mApiKey, mPassphrase);
        String signedMsgValue = signedMessage.toString();
        if (this.mMsgHandler != null) {
            try {
                mMsgHandler.Subscribing(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mWebSocket.sendText(signedMsgValue, true);
    }

    public void UnsubscribeAll() {
        UnsubscribeMessage msg = new UnsubscribeMessage();
        final String text = toJSON(msg);
        mWebSocket.sendText(text, true);
    }
}
