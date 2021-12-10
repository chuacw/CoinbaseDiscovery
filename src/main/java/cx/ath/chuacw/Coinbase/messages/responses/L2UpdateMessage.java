package cx.ath.chuacw.Coinbase.messages.responses;

import java.util.Date;
import java.util.List;

public class L2UpdateMessage extends BaseTypeProductIdMessage {

    public List<List<String>> changes;
    public Date time;
}