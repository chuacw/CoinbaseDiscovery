package cx.ath.chuacw;

import cx.ath.chuacw.Coinbase.WebSocketFeed;
import cx.ath.chuacw.Coinbase.messages.SubscribeMessage;
import cx.ath.chuacw.MessageHandlers.BaseMessageHandler;
import cx.ath.chuacw.MessageHandlers.MainHandler;
import sun.misc.Signal;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ShowOrderDepth {

    static final BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<>(1);
    private static ShowOrderDepth mShowOrderDepth;
    private final boolean run;
    // Helps keep track if app stops normally, or is terminated by an error, or Ctrl+C
    private final StopReason mStopReason;
    private final String mCurrencyPair;

    public ShowOrderDepth(String newCurrencyPair) {
        run = true;
        mCurrencyPair = newCurrencyPair;
        mStopReason = new StopReason(StopEnum.UNSET);
    }

    // https://docs.cloud.coinbase.com/exchange/docs/overview
    // https://docs.cloud.coinbase.com/exchange/docs/authorization-and-authentication#creating-a-request

    // build an order book of 10 levels, and print the order book on each "tick"
    public static void main(String[] args) throws InterruptedException {
        // Handle any shutdowns, including Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Ctrl+C encountered...
            if (mShowOrderDepth != null) {
                final StopEnum lStopEnum = mShowOrderDepth.mStopReason.getStop();
                switch (lStopEnum) {
                    case NORMAL -> {
                        System.out.println("Stopping application normally.");
                    }
                    case ERROR -> {
                        System.out.println("Stopping application due to error encountered.");
                    }
                    case UNSET -> {
                        clearConsoleInput();
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
        mShowOrderDepth.run();
    }

    private static void sendCtrlC() {
        Signal signalINT = new Signal("INT");
        Signal.raise(signalINT);
    }

    private static void clearConsoleInput() {
        try {
            while (System.in.available()!=0) {
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws InterruptedException {

        BaseMessageHandler handler = new MainHandler(sharedQueue, mStopReason);
        Thread watchThread = new Thread(handler);
        watchThread.start();

        String socketURL = "wss://ws-feed.exchange.coinbase.com";
        WebSocketFeed feed = new WebSocketFeed(socketURL, handler);

// Get API Key from pro.coinbase.com's API Settings tab
        // this is the key in the Default Portfolio, or whatever you use.
//        feed.setAPIKey("");
        // this is the Passphrase when a new API key is created, in the Add an API key dialog
//        feed.setPassphrase("");
        // this is the API Secret
//        feed.setSecretKey("");

        SubscribeMessage subscribeMsg = new SubscribeMessage();
        subscribeMsg.addProductId(this.mCurrencyPair);
        subscribeMsg.addChannels(new String[]{"level2", "heartbeat"});
        feed.Subscribe(subscribeMsg);
        int nCount = 0;
        while (!watchThread.isInterrupted()) {
            Thread.sleep(100);
        }
        feed.UnsubscribeAll();
    }

}
