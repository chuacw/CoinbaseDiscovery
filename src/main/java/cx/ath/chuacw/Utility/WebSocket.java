package cx.ath.chuacw.Utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cx.ath.chuacw.Coinbase.Handlers.MessageHandler;
import cx.ath.chuacw.Coinbase.WebSocketClientHandler;

import java.net.URI;
import java.net.http.HttpClient;

public class WebSocket {
    public static java.net.http.WebSocket getWebSocket(String url, MessageHandler handler) {
        final java.net.http.WebSocket result = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create(url),
                new WebSocketClientHandler(handler)).join();
        return result;
    }

    public static String toJSON(Object obj) {
        ObjectMapper om = new ObjectMapper();
        String result = null;
        try {
            result = om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
        }
        return result;
    }
}
