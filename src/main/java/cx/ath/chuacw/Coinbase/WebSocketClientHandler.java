package cx.ath.chuacw.Coinbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cx.ath.chuacw.Coinbase.Handlers.MessageHandler;
import cx.ath.chuacw.Coinbase.messages.responses.*;

import java.math.BigDecimal;
import java.net.http.WebSocket;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketClientHandler implements WebSocket.Listener {

    private StringBuffer bufferedData;
    private final MessageHandler msgHandler;
    private final Map<String, String> sellOrderBook;
    private final Map<String, String> buyOrderBook;
    private void initBuffer() {
        bufferedData = new StringBuffer();
    }
    public WebSocketClientHandler(MessageHandler newHandler) {
        initBuffer();
        msgHandler = newHandler;
        sellOrderBook = new ConcurrentHashMap<>();
        buyOrderBook = new ConcurrentHashMap<>();
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }

    private void handle(ErrorMessage msg) throws InterruptedException {
        if (msgHandler!=null) {
            msgHandler.terminateApp(msg.reason);
        }
    }
    private void handle(HeartbeatMessage msg) {}

    private static void addOrderLine(Map<String, String> orderBook, String header, StringBuffer sb, int limit) {
        String headerLine = String.format("%s\n", header);
        sb.append(headerLine);
        int nCount = 0;
        for (String price: orderBook.keySet()) {
            if (nCount == limit) {
                continue;
            }
            final String size = orderBook.get(price);
            String orderLine = String.format("Price: %s, Size: %s\n", price, size);
            sb.append(orderLine);
            nCount++;
        }
    }

    private void handle(SnapshotMessage msg) {
        addOrderBookItems(msg.asks.listIterator(), sellOrderBook, Constants.displayLEVELS);
        addOrderBookItems(msg.bids.listIterator(), buyOrderBook, Constants.displayLEVELS);
    }

    private void handle(SubscriptionsMessage msg) {
        if (msgHandler != null) {
            try {
                msgHandler.Subscribed(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleBuffering() {
        if (msgHandler != null) {
            try {
                msgHandler.handleBuffering();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void handle(TickerMessage msg) {
    }
    private void handleUnhandledType(String type, String json) {
        if (msgHandler != null) {
            try {
                msgHandler.unhandledType(type, json);
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
        String size = changeList.get(2);
        final BigDecimal nSize = new BigDecimal(size);
        final BigDecimal Zero = new BigDecimal(0);
        if (nSize.compareTo(Zero) == 0) {
            switch (orderType) {
                case Constants.BUY -> buyOrderBook.remove(price);
                case Constants.SELL -> sellOrderBook.remove(price);
            }
        } else {
            switch (orderType) {
                case Constants.BUY -> buyOrderBook.put(price, size);
                case Constants.SELL -> sellOrderBook.put(price, size);
            }
        }
        if ((buyOrderBook.size() >= Constants.displayLEVELS) && (sellOrderBook.size() >= Constants.displayLEVELS)) {
            if (msgHandler!=null) {
                final StringBuffer bOrderBook = new StringBuffer();
                addOrderLine(buyOrderBook, Constants.headerBUY, bOrderBook, Constants.displayLEVELS);
                bOrderBook.append("\n");
                addOrderLine(sellOrderBook, Constants.headerSELL, bOrderBook, Constants.displayLEVELS);
                final String orderBook = bOrderBook.toString();
                try {
                    msgHandler.updateOrderBook(orderBook);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static void addOrderBookItems(ListIterator<List<String>> listIterator,
                                         Map<String, String> orderBook, int Limit) {
        int nCount = 0;
        for (; listIterator.hasNext(); ) {
            if (nCount == Limit) {
                break;
            }
            List<String> buyItem = listIterator.next();
            final String price = buyItem.get(0);
            final String size = buyItem.get(1);
            orderBook.put(price, size);
            nCount++;
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        bufferedData.append(data);
        if (last) {
            String json = bufferedData.toString();
            ObjectMapper om = new ObjectMapper();
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
                    default -> {
                        handleUnhandledType(type, json);
                    }
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
            }

            initBuffer();
        } else {
            handleBuffering();
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

}