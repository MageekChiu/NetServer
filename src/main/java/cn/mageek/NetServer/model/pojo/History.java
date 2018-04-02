package cn.mageek.NetServer.model.pojo;

/**
 * @author Mageek Chiu
 * @date 2018/4/2 0002:12:10
 */
public class History {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSignal() {
        return signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }
    private int id;
    private double signal;
    private double power;
}
