package cx.ath.chuacw.Coinbase.messages;

import java.util.ArrayList;
import java.util.Arrays;

public class SubscribeMessage extends Message {

    private boolean defaultTicker;

    private ArrayList<Object> channels;

    public SubscribeMessage() {
        this.channels = new ArrayList<Object>();
        defaultTicker = true;
        setType("subscribe");
    }

    public void addChannels(Object[] objects) {
        for(Object object: objects) {
            this.channels.add(object);
        }
    }

    public void addTicker(MessageTicker ticker) {
        defaultTicker = false;
        this.channels.add(ticker);
    }

    public Object[] getChannels() {
        final Object[] tempObj = this.channels.toArray();
        Object[] result = null;

        if (defaultTicker) {
            ArrayList<Object> newObj = new ArrayList<Object>(Arrays.asList(tempObj));
            MessageTicker ticker = new MessageTicker();
            ticker.setProductIds(getProductIds());
            newObj.add(ticker);
            result = newObj.toArray();
        } else {
            result = this.channels.toArray();
        }
        return result;
    }

}
