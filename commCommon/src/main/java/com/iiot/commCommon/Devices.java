package com.iiot.commCommon;

import java.io.Serializable;
import java.sql.Timestamp;

public class Devices implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // 数据库中的设备序号
    private Integer dept_id; // 部门id
    private String dev_name; // 设备名称
    private String dev_desc; // 设备描述
    private String dev_type; // 设备类型
    private Integer dev_type_id; // 设备类型ID
    private String dev_group; // 设备分组
    private Integer dev_group_id; // 设备分组ID
    private String dev_code; // 设备编号
    private char status; // 设备状态（0初始值 1正常 2离线 3故障）
    private String oil_code; // 油桶编号
    private Integer bind_dept_id; // 绑定部门id
    private Timestamp bind_time; // 绑定时间
    private char register; // 注册状态（0未注册 1已注册）
    private Timestamp register_time; // 注册时间
    private char work_mod; // 工作模式（1基本模式 2汽修店模式 3油厂模式）
    private String province; // 所在省份
    private String city; // 所在城市
    private String address; // 地址
    private char del_flag; // 删除标志（0代表存在 2代表删除）
    private String create_by; // 创建者
    private Timestamp create_time; // 创建时间
    private String update_by; // 更新者
    private Timestamp update_time; // 更新时间

    public Devices() {}

    public Devices(Integer id, Integer dept_id, String dev_name, String dev_desc, String dev_type, Integer dev_type_id, String dev_group, Integer dev_group_id, String dev_code, char status, String oil_code, Integer bind_dept_id, Timestamp bind_time, char register, Timestamp register_time, char work_mod, String province, String city, String address, char del_flag, String create_by, Timestamp create_time, String update_by, Timestamp update_time) {
        this.id = id;
        this.dept_id = dept_id;
        this.dev_name = dev_name;
        this.dev_desc = dev_desc;
        this.dev_type = dev_type;
        this.dev_type_id = dev_type_id;
        this.dev_group = dev_group;
        this.dev_group_id = dev_group_id;
        this.dev_code = dev_code;
        this.status = status;
        this.oil_code = oil_code;
        this.bind_dept_id = bind_dept_id;
        this.bind_time = bind_time;
        this.register = register;
        this.register_time = register_time;
        this.work_mod = work_mod;
        this.province = province;
        this.city = city;
        this.address = address;
        this.del_flag = del_flag;
        this.create_by = create_by;
        this.create_time = create_time;
        this.update_by = update_by;
        this.update_time = update_time;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDept_id() {
        return dept_id;
    }

    public void setDept_id(Integer dept_id) {
        this.dept_id = dept_id;
    }

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public String getDev_desc() {
        return dev_desc;
    }

    public void setDev_desc(String dev_desc) {
        this.dev_desc = dev_desc;
    }

    public String getDev_type() {
        return dev_type;
    }

    public void setDev_type(String dev_type) {
        this.dev_type = dev_type;
    }

    public Integer getDev_type_id() {
        return dev_type_id;
    }

    public void setDev_type_id(Integer dev_type_id) {
        this.dev_type_id = dev_type_id;
    }

    public String getDev_group() {
        return dev_group;
    }

    public void setDev_group(String dev_group) {
        this.dev_group = dev_group;
    }

    public Integer getDev_group_id() {
        return dev_group_id;
    }

    public void setDev_group_id(Integer dev_group_id) {
        this.dev_group_id = dev_group_id;
    }

    public String getDev_code() {
        return dev_code;
    }

    public void setDev_code(String dev_code) {
        this.dev_code = dev_code;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getOil_code() {
        return oil_code;
    }

    public void setOil_code(String oil_code) {
        this.oil_code = oil_code;
    }

    public Integer getBind_dept_id() {
        return bind_dept_id;
    }

    public void setBind_dept_id(Integer bind_dept_id) {
        this.bind_dept_id = bind_dept_id;
    }

    public Timestamp getBind_time() {
        return bind_time;
    }

    public void setBind_time(Timestamp bind_time) {
        this.bind_time = bind_time;
    }

    public char getRegister() {
        return register;
    }

    public void setRegister(char register) {
        this.register = register;
    }

    public Timestamp getRegister_time() {
        return register_time;
    }

    public void setRegister_time(Timestamp register_time) {
        this.register_time = register_time;
    }

    public char getWork_mod() {
        return work_mod;
    }

    public void setWork_mod(char work_mod) {
        this.work_mod = work_mod;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public char getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(char del_flag) {
        this.del_flag = del_flag;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "devices{" +
                "id=" + id +
                ", dept_id=" + dept_id +
                ", dev_name='" + dev_name + '\'' +
                ", dev_desc='" + dev_desc + '\'' +
                ", dev_type='" + dev_type + '\'' +
                ", dev_type_id=" + dev_type_id +
                ", dev_group='" + dev_group + '\'' +
                ", dev_group_id=" + dev_group_id +
                ", dev_code='" + dev_code + '\'' +
                ", status=" + status +
                ", oil_code='" + oil_code + '\'' +
                ", bind_dept_id=" + bind_dept_id +
                ", bind_time=" + bind_time +
                ", register=" + register +
                ", register_time=" + register_time +
                ", work_mod=" + work_mod +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", del_flag=" + del_flag +
                ", create_by='" + create_by + '\'' +
                ", create_time=" + create_time +
                ", update_by='" + update_by + '\'' +
                ", update_time=" + update_time +
                '}';
    }
}
