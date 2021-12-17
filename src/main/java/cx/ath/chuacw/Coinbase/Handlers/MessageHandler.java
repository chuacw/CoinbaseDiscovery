package cx.ath.chuacw.Coinbase.Handlers;

import cx.ath.chuacw.Coinbase.messages.SubscribeMessage;
import cx.ath.chuacw.Coinbase.messages.responses.SubscriptionsMessage;

public interface MessageHandler {
    void terminateApp(String reason) throws InterruptedException;

    void updateOrderBook(String order) throws InterruptedException;

    void unhandledType(String type, String JSON) throws InterruptedException;

    void Subscribed(SubscriptionsMessage msg) throws InterruptedException;

    void handleBuffering() throws InterruptedException;

    void opened() throws InterruptedException;

    void closed() throws InterruptedException;

    void Subscribing(SubscribeMessage msg) throws InterruptedException;
}
