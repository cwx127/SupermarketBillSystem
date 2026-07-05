package com.example.supermarketbillsystem.entity;

import java.sql.Timestamp;

/**
 * 用户实体类
 * 对应数据库表 sys_user
 * 用于封装用户信息
 */
public class User {
    /** 用户ID（主键，自增） */
    private int id;
    /** 用户名（登录账号） */
    private String username;
    /** 性别（0=女，1=男） */
    private int sex;
    /** 年龄 */
    private int age;
    /** 电话号码 */
    private String phonenumber;
    /** 地址 */
    private String address;
    /** 密码（登录密码） */
    private String password;
    /** 角色（0=普通用户/员工，1=部门经理） */
    private int role;
    /** 创建时间（注册时间，由数据库自动生成） */
    private Timestamp createTime;

    /**
     * 无参构造方法
     * 用于反射创建对象、JSON反序列化等场景
     */
    public User() {
    }

    /**
     * 带参构造方法
     * 用于创建用户对象时直接初始化字段
     * @param id 用户ID
     * @param username 用户名
     * @param password 密码
     * @param role 角色（0=普通用户，1=部门经理）
     * @param sex 性别（0=女，1=男）
     * @param age 年龄
     * @param phonenumber 电话号码
     * @param address 地址
     */
    public User(int id, String username, String password, int role, int sex, int age, String phonenumber, String address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.sex = sex;
        this.age = age;
        this.phonenumber = phonenumber;
        this.address = address;
    }

    /** 获取用户ID */
    public int getId() {
        return id;
    }

    /** 设置用户ID */
    public void setId(int id) {
        this.id = id;
    }

    /** 获取密码 */
    public String getPassword() {
        return password;
    }

    /** 设置密码 */
    public void setPassword(String password) {
        this.password = password;
    }

    /** 获取角色（0=普通用户，1=部门经理） */
    public int getRole() {
        return role;
    }

    /** 设置角色 */
    public void setRole(int role) {
        this.role = role;
    }

    /** 获取用户名 */
    public String getUsername() {
        return username;
    }

    /** 设置用户名 */
    public void setUsername(String username) {
        this.username = username;
    }

    /** 获取性别（0=女，1=男） */
    public int getSex() {
        return sex;
    }

    /** 设置性别 */
    public void setSex(int sex) {
        this.sex = sex;
    }

    /** 获取年龄 */
    public int getAge() {
        return age;
    }

    /** 设置年龄 */
    public void setAge(int age) {
        this.age = age;
    }

    /** 获取电话号码 */
    public String getPhonenumber() {
        return phonenumber;
    }

    /** 设置电话号码 */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /** 获取地址 */
    public String getAddress() {
        return address;
    }

    /** 设置地址 */
    public void setAddress(String address) {
        this.address = address;
    }

    /** 获取创建时间 */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /** 设置创建时间 */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}