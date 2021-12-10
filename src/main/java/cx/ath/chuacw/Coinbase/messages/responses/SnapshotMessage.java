package cx.ath.chuacw.Coinbase.messages.responses;

import java.util.List;

//Root root = om.readValue(myJsonString), Root.class); */
public class SnapshotMessage extends BaseTypeProductIdMessage {

    public List<List<String>> asks;
    public List<List<String>> bids;

}

