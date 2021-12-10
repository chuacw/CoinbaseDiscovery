package cx.ath.chuacw;

import cx.ath.chuacw.Coinbase.Handlers.MessageHandler;
import cx.ath.chuacw.Coinbase.WebSocketFeed;
import cx.ath.chuacw.Coinbase.messages.SubscribeMessage;
import cx.ath.chuacw.Coinbase.messages.responses.SubscriptionsMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ShowOrderDepth extends Thread implements MessageHandler {

    private static final BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>(1);

    static boolean run;
    // Helps keep track if app stops normally, or is terminated by an error, or Ctrl+C
    private StopReason stopReason;
    String currencyPair;

    public ShowOrderDepth(String newCurrencyPair) {
        this.currencyPair = newCurrencyPair;
        stopReason = StopReason.UNSET;
    }
    public void run() {
        String socketURL = "wss://ws-feed.exchange.coinbase.com";
        System.out.println("Connecting to Coinbase...");
        WebSocketFeed feed = new WebSocketFeed(socketURL, this);
        System.out.println("Connected to Coinbase.");

        SubscribeMessage subscribeMsg = new SubscribeMessage();
        subscribeMsg.addProductId(this.currencyPair);
        subscribeMsg.addChannels(new Object[]{"level2", "heartbeat"});
        System.out.println("Sending subscribe message...");
        feed.Subscribe(subscribeMsg);
        while (run) {
            try {
                String s = sharedQueue.take();
                System.out.println(s);
            } catch (InterruptedException e) {
                run = false;
                stopReason = StopReason.ERROR;
            }
        }
    }

    // https://docs.cloud.coinbase.com/exchange/docs/overview
    // https://docs.cloud.coinbase.com/exchange/docs/authorization-and-authentication#creating-a-request

    private static ShowOrderDepth mShowOrderDepth;

    // build an order book of 10 levels, and print the order book on each "tick"
    public static void main(String[] args) throws InterruptedException {
        // Handle any shutdowns, including Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Ctrl+C encountered...
            if (mShowOrderDepth != null) {
                switch (mShowOrderDepth.stopReason) {
                    case NORMAL -> {
                        System.out.println("Stopping application normally.");
                    }
                    case ERROR -> {
                        System.out.println("Stopping application due to error encountered.");
                    }
                    case UNSET -> {
                        System.out.println("Ctrl+C encountered. Stopping.");
                    }
                    default -> System.out.println("stopReason is not set.");
                }
            }
        }));

        System.out.println("ShowOrderDepth (c) 2021 Chee-Wee Chua");
        System.out.println();
        if (args.length == 0) {
            System.out.println("Needs a currency pair to watch.");
            System.exit(0);
        }
        final String currencyPair = args[0];
        mShowOrderDepth = new ShowOrderDepth(currencyPair);
        run = true;
        Thread watchThread = new Thread(mShowOrderDepth);
        watchThread.start();
        while (run) {
            Thread.sleep(100);
        }
        mShowOrderDepth.stopReason = StopReason.NORMAL;
        // Nothing will run here...

    }

    // Terminates the app for whatever reason, by setting run to false,
    // as well as putting the reason into the shared queue, which will then
    // output onto the console.
    @Override
    public void terminateApp(String reason) throws InterruptedException {
        run = false;
        stopReason = StopReason.ERROR;
        sharedQueue.put(reason);
    }

    // Updates the console by placing order book values into the queue
    @Override
    public void updateOrderBook(String orderBook) throws InterruptedException {
        sharedQueue.put(orderBook);
    }

    @Override
    public void unhandledType(String type, String JSON) throws InterruptedException {
        String msg = String.format("Unhandled type: %s", type);
        sharedQueue.put(msg);
    }

    @Override
    public void Subscribed(SubscriptionsMessage subMsg) throws InterruptedException {
        String msg = "Subscription acknowledged.";
        sharedQueue.put(msg);
    }

    @Override
    public void handleBuffering() throws InterruptedException {
        String msg = "Buffering data...";
        sharedQueue.put(msg);
    }
}
