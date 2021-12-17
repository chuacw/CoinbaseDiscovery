package cx.ath.chuacw.MessageHandlers;

import cx.ath.chuacw.Coinbase.Handlers.MessageHandler;
import cx.ath.chuacw.Coinbase.messages.responses.SubscriptionsMessage;
import cx.ath.chuacw.StopReason;

import java.util.concurrent.BlockingQueue;

public abstract class BaseMessageHandler extends Thread implements MessageHandler {
    protected final BlockingQueue<String> mSharedQueue;

    // Helps keep track if app stops normally, or is terminated by an error, or Ctrl+C
    protected StopReason mStopReason;
    protected boolean run;

    BaseMessageHandler(BlockingQueue<String> aSharedQueue, StopReason aStopReason) {
        super();
        this.mSharedQueue = aSharedQueue;
        this.mStopReason = aStopReason;
        this.run = true;
    }

    @Override
    public abstract void terminateApp(String reason) throws InterruptedException;

    @Override
    public abstract void updateOrderBook(String order) throws InterruptedException;

    @Override
    public abstract void unhandledType(String type, String JSON) throws InterruptedException;

    @Override
    public abstract void Subscribed(SubscriptionsMessage msg) throws InterruptedException;

    @Override
    public abstract void handleBuffering() throws InterruptedException;
}
