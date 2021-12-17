package cx.ath.chuacw.Coinbase.messages.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Channel {
    public String name;
    @JsonProperty("product_ids")
    public List<String> product_ids;

    @Override
    public String toString() {
        final var result = String.format("Channel: %s, Products: %s\n",
                name, product_ids.toString());
        return result;
    }
}
