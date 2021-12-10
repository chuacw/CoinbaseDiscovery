package cx.ath.chuacw.Coinbase.messages.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class HeartbeatMessage extends BaseTypeProductIdMessage {

    @JsonProperty("last_trade_id")
    public int last_trade_id;

    public long sequence;
    public Date time;
}
