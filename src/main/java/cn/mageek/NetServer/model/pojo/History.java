package cn.mageek.NetServer.model.pojo;

/**
 * @author Mageek Chiu
 * @date 2018/4/2 0002:12:10
 */
public class History {
    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        History.id = id;
    }

    public static float getSignal() {
        return signal;
    }

    public static void setSignal(float signal) {
        History.signal = signal;
    }

    public static float getPower() {
        return power;
    }

    public static void setPower(float power) {
        History.power = power;
    }
    private static String id;
    private static float signal;
    private static float power;
}
