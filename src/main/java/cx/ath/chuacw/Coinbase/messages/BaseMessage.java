package cx.ath.chuacw.Coinbase.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class BaseMessage {
    private ArrayList<String> productIds;

    public BaseMessage() {
        this.productIds = new ArrayList<String>();
    }

    @JsonProperty("product_ids")
    public String[] getProductIds() {
        String[] result = this.productIds.toArray(new String[0]);
        return result;
    }

    @JsonProperty("product_ids")
    public void setProductIds(String[] newProductIds) {
        for(String productId: newProductIds) {
            this.productIds.add(productId);
        }
    }

    public void addProductId(String productId) {
        this.productIds.add(productId);
    }
}
