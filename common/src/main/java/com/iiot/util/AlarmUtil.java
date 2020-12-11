package com.iiot.util;

/**
 * Created by Administrator on 2018/1/3.
 */
public class AlarmUtil {

    //是否需要二押点0解除，1需要设置
    private boolean twoCharge;

    //加入绑定的省市区域地址
    private String address;

    //车辆id
    private long vehId;

    //车组id
    private int groupId;

    //经常停留点
    private String staypoint;

    public boolean getTwoCharge() {
        return twoCharge;
    }

    public void setTwoCharge(boolean twoCharge) {
        this.twoCharge = twoCharge;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getVehId() {
        return vehId;
    }

    public void setVehId(long vehId) {
        this.vehId = vehId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getStaypoint() {
        return staypoint;
    }

    public void setStaypoint(String staypoint) {
        this.staypoint = staypoint;
    }
}
