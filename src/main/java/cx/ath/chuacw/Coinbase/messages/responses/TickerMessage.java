package cx.ath.chuacw.Coinbase.messages.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

// @JsonProperty to map the field name is used as ObjectMapper automatically removes _ from field names.
public class TickerMessage extends BaseTypeProductIdMessage {
        public long sequence;

        public String price;

        @JsonProperty("open_24h")
        public String open_24h;

        @JsonProperty("volume_24h")
        public String volume_24h;

        @JsonProperty("low_24h")
        public String low_24h;

        @JsonProperty("high_24h")
        public String high_24h;

        @JsonProperty("volume_30d")
        public String volume_30d;

        @JsonProperty("best_bid")
        public String best_bid;

        @JsonProperty("best_ask")
        public String best_ask;

        public String side;
        public Date time;

        @JsonProperty("trade_id")
        public int trade_id;

        @JsonProperty("last_size")
        public String last_size;
}
