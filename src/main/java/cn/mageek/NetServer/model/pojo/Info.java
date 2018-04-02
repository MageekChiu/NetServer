package cn.mageek.NetServer.model.pojo;

/**
 * @author Mageek Chiu
 * @date 2018/4/2 0002:12:10
 */
public class Info {
    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Info.id = id;
    }

    public static float getSignal() {
        return signal;
    }

    public static void setSignal(float signal) {
        Info.signal = signal;
    }

    private static String id;
    private static float signal;
}
