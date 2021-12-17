package cx.ath.chuacw;

public class StopReason {
    private StopEnum mStop;

    public StopReason(StopEnum value) {
        super();
        setStop(value);
    }

    public StopEnum getStop() {
        return mStop;
    }

    public void setStop(StopEnum value) {
        mStop = value;
    }
}
