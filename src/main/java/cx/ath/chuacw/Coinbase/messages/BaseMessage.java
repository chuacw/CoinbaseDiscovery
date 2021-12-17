package cx.ath.chuacw.Coinbase.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class BaseMessage {
    private final ArrayList<String> productIds;

    public BaseMessage() {
        this.productIds = new ArrayList<>();
    }

    @JsonProperty("product_ids")
    public String[] getProductIds() {
        final String[] result = this.productIds.toArray(new String[0]);
        return result;
    }

    @JsonProperty("product_ids")
    public void setProductIds(String[] newProductIds) {
        for (final String productId : newProductIds) {
            this.productIds.add(productId);
        }
    }

    public void addProductId(String productId) {
        this.productIds.add(productId);
    }
}
