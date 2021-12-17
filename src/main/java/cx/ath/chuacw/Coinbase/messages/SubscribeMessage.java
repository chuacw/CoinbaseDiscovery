package cx.ath.chuacw.Coinbase.messages;

import java.util.ArrayList;
import java.util.Arrays;

public class SubscribeMessage extends Message {

    private final ArrayList<Object> mChannels;
    private boolean mDefaultTicker;

    public SubscribeMessage() {
        super();
        this.mChannels = new ArrayList<Object>();
        this.mDefaultTicker = true;
        setType("subscribe");
    }

    public void addChannels(Object[] objects) {
        for (Object object : objects) {
            this.mChannels.add(object);
        }
    }

    public void addTicker(MessageTicker ticker) {
        this.mDefaultTicker = false;
        this.mChannels.add(ticker);
    }

    public Object[] getChannels() {
        final Object[] tempObj = this.mChannels.toArray();
        Object[] result = null;

        if (mDefaultTicker) {
            final ArrayList<Object> newObj = new ArrayList<Object>(Arrays.asList(tempObj));
            final MessageTicker ticker = new MessageTicker();
            ticker.setProductIds(getProductIds());
            newObj.add(ticker);
            result = newObj.toArray();
        } else {
            result = this.mChannels.toArray();
        }
        return result;
    }

}
