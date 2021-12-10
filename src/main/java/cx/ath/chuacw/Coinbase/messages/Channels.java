package cx.ath.chuacw.Coinbase.messages;

import java.util.ArrayList;
import java.util.List;

public class Channels {
    private List<Object> objs;
    public Channels() {
        this.objs = new ArrayList<Object>();
    }
    public void addObject(Object o)
    {
        this.objs.add(o);
    }
    public void add(String s) {
        this.objs.add(s);
    }
}
