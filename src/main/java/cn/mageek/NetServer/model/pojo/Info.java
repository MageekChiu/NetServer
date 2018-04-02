package cn.mageek.NetServer.model.pojo;

import java.time.LocalDateTime;

/**
 * @author Mageek Chiu
 * @date 2018/4/2 0002:12:10
 */
public class Info {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSignal() {
        return signal;
    }

    public void setSignal(float signal) {
        this.signal = signal;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    private int id;
    private float signal;
    private String mac;
    private LocalDateTime createTime;
}
