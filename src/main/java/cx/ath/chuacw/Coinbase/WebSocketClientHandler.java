package cx.ath.chuacw.Coinbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cx.ath.chuacw.Coinbase.Handlers.MessageHandler;
import cx.ath.chuacw.Coinbase.messages.responses.*;

import java.math.BigDecimal;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketClientHandler implements WebSocket.Listener {

    private final MessageHandler mMsgHandler;
    private final Map<String, String> mSellOrderBook;
    private final Map<String, String> mBuyOrderBook;
    private StringBuffer mBufferedData;

    public WebSocketClientHandler(MessageHandler newHandler) {
        initBuffer();
        this.mMsgHandler = newHandler;
        this.mSellOrderBook = new ConcurrentHashMap<>();
        this.mBuyOrderBook = new ConcurrentHashMap<>();
    }

    private static void addOrderLine(Map<String, String> orderBook, String header, StringBuilder sb, int limit) {
        final String headerLine = String.format("%s\n", header);
        sb.append(headerLine);
        int nCount = 0;
        final var sl = new ArrayList<String>();
        for (final String price : orderBook.keySet()) {
            if (nCount++ >= limit) {
                break;
            }
            final String size = orderBook.get(price);
            final String orderLine = String.format("Price %s, Size: %s\n", price, size);
            sl.add(orderLine);
        }
        Collections.sort(sl);
        nCount = 0;
        for (final String orderLine : sl) {
            if (nCount++ >= limit) {
                break;
            }
            sb.append(orderLine);
        }
    }

    private static void addOrderBookItems(List<List<String>> listOfList,
                                          Map<String, String> orderBook, int Limit) {
        int nCount = 0;
        for (final List<String> list : listOfList) {
            if (nCount++ >= Limit) {
                break;
            }
            final String price = list.get(0);
            final String size = list.get(1);
            orderBook.put(price, size);
        }
    }

    private void initBuffer() {
        mBufferedData = new StringBuffer();
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
        if (mMsgHandler != null) {
            try {
                mMsgHandler.opened();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        if (mMsgHandler != null) {
            try {
                mMsgHandler.closed();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }

    private void handle(ErrorMessage msg) throws InterruptedException {
        if (mMsgHandler != null) {
            mMsgHandler.terminateApp(msg.reason);
        }
    }

    private void handle(HeartbeatMessage msg) {
    }

    private void handle(SnapshotMessage msg) {
        addOrderBookItems(msg.asks, mSellOrderBook, Constants.displayLEVELS);
        addOrderBookItems(msg.bids, mBuyOrderBook, Constants.displayLEVELS);
    }

    private void handle(SubscriptionsMessage msg) {
        if (mMsgHandler != null) {
            try {
                mMsgHandler.Subscribed(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBuffering() {
        if (mMsgHandler != null) {
            try {
                mMsgHandler.handleBuffering();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(TickerMessage msg) {
    }

    private void handleUnhandledType(String type, String json) {
        if (mMsgHandler != null) {
            try {
                mMsgHandler.unhandledType(type, json);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(L2UpdateMessage msg) {
        // handle l2 message
        // {"type":"l2update","product_id":"BTC-USD","changes":[["sell","51683.26","0.00000000"]],"time":"2021-12-07T16:17:28.634903Z"}
        // when changes[0][2] is 0.00000000, it's removed
        // changes[0][0] == "buy" or "sell"
        final List<String> changeList = msg.changes.get(0);
        final String orderType = changeList.get(0);
        final String price = changeList.get(1);
        final String size = changeList.get(2);
        final BigDecimal nSize = new BigDecimal(size);
        if (nSize.compareTo(BigDecimal.ZERO) == 0) {
            switch (orderType) {
                case Constants.BUY -> mBuyOrderBook.remove(price);
                case Constants.SELL -> mSellOrderBook.remove(price);
            }
        } else {
            switch (orderType) {
                case Constants.BUY -> mBuyOrderBook.put(price, size);
                case Constants.SELL -> mSellOrderBook.put(price, size);
            }
        }
        if ((mBuyOrderBook.size() >= Constants.displayLEVELS) && (mSellOrderBook.size() >= Constants.displayLEVELS)) {
            if (mMsgHandler != null) {
                final var bOrderBook = new StringBuilder();
                addOrderLine(mBuyOrderBook, Constants.headerBUY, bOrderBook, Constants.displayLEVELS);
                bOrderBook.append("\n");
                addOrderLine(mSellOrderBook, Constants.headerSELL, bOrderBook, Constants.displayLEVELS);
                final String orderBook = bOrderBook.toString();
                try {
                    mMsgHandler.updateOrderBook(orderBook);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        mBufferedData.append(data);
        if (last) {
            final String json = mBufferedData.toString();
            final ObjectMapper om = new ObjectMapper();
            try {
                final JsonNode jsonNode = om.readTree(json);
                final String type = jsonNode.get("type").asText();
                switch (type) {
                    case Constants.ERROR -> {
                        ErrorMessage errorMsg = om.readValue(json, ErrorMessage.class);
                        handle(errorMsg);
                    }
                    case Constants.HEARTBEAT -> {
                        HeartbeatMessage heartbeatMsg = om.readValue(json, HeartbeatMessage.class);
                        handle(heartbeatMsg);
                    }
                    case Constants.L2UPDATE -> {
                        L2UpdateMessage l2updateMsg = om.readValue(json, L2UpdateMessage.class);
                        handle(l2updateMsg);
                    }
                    case Constants.SNAPSHOT -> {
                        SnapshotMessage snapshotMsg = om.readValue(json, SnapshotMessage.class);
                        handle(snapshotMsg);
                    }
                    case Constants.SUBSCRIPTIONS -> {
                        SubscriptionsMessage subscriptionsMsg = om.readValue(json, SubscriptionsMessage.class);
                        handle(subscriptionsMsg);
                    }
                    case Constants.TICKER -> {
                        TickerMessage tickerMsg = om.readValue(json, TickerMessage.class);
                        handle(tickerMsg);
                    }
                    default -> handleUnhandledType(type, json);
                }
            } catch (JsonProcessingException | InterruptedException e) {
                e.printStackTrace();
            }

            initBuffer();
        } else {
            handleBuffering();
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

}