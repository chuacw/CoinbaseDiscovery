package cx.ath.chuacw.Coinbase.messages.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseTypeProductIdMessage extends BaseTypeMessage {
    @JsonProperty("product_id")
    public String product_id;
}
