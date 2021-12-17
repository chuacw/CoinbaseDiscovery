package cx.ath.chuacw.MessageHandlers;

import cx.ath.chuacw.Coinbase.messages.SubscribeMessage;
import cx.ath.chuacw.Coinbase.messages.responses.SubscriptionsMessage;
import cx.ath.chuacw.StopEnum;
import cx.ath.chuacw.StopReason;

import java.util.concurrent.BlockingQueue;

public class MainHandler extends BaseMessageHandler {

    public MainHandler(BlockingQueue<String> aSharedQueue, StopReason aStopReason) {
        super(aSharedQueue, aStopReason);
    }

    @Override
    public void run() {
        while (run) {
            try {
                String s = mSharedQueue.take();
                System.out.println(s);
            } catch (InterruptedException e) {
                run = false;
                mStopReason.setStop(StopEnum.ERROR);
            }
        }
        Thread.currentThread().interrupt();
    }

    // https://docs.cloud.coinbase.com/exchange/docs/overview
    // https://docs.cloud.coinbase.com/exchange/docs/authorization-and-authentication#creating-a-request

    // Terminates the app for whatever reason, by setting run to false,
    // as well as putting the reason into the shared queue, which will then
    // output onto the console.
    @Override
    public void terminateApp(String reason) throws InterruptedException {
        run = false;
        mStopReason.setStop(StopEnum.ERROR);
        mSharedQueue.put(reason);
    }

    // Updates the console by placing order book values into the queue
    @Override
    public void updateOrderBook(String orderBook) throws InterruptedException {
        mSharedQueue.put(orderBook);
    }

    @Override
    public void unhandledType(String type, String JSON) throws InterruptedException {
        final var msg = String.format("Unhandled type: %s", type);
        mSharedQueue.put(msg);
    }

    @Override
    public void Subscribed(SubscriptionsMessage subMsg) throws InterruptedException {
        final var msg = "Subscription acknowledged: \n" + subMsg.toString();
        mSharedQueue.put(msg);
    }

    @Override
    public void Subscribing(SubscribeMessage subMsg) throws InterruptedException {
        final var productIds = subMsg.getProductIds();
        final String msg = String.format("Subscribing to %s...", productIds[0]);
        mSharedQueue.put(msg);
    }

    @Override
    public void handleBuffering() throws InterruptedException {
        final var msg = "Buffering data...";
        mSharedQueue.put(msg);
    }

    @Override
    public void opened() throws InterruptedException {
        final var msg = "Connected to Coinbase.";
        mSharedQueue.put(msg);
    }

    @Override
    public void closed() throws InterruptedException {
        final String msg = "Disconnected from Coinbase.";
        mSharedQueue.put(msg);
    }
}
