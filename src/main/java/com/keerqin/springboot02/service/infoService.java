package com.keerqin.springboot02.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface infoService {

    public List getAllArea();

    public List getAllItem();

    //修改二维码uuid
    public String updateQRCode(String area, String item, String uuid, String flushInterval);

    //查询厂区对应的健身器材
    public List changeArea(String area);

    //添加健身器材和对应的厂区
    //public void addItem(String itemArea, String itemName);

    //检测 扫描的二维码是否为有效的二维码
    public List checkQRCode(String uuid);

    //将有效的扫描记录添加到表 scanReord表
    public String addScanRecord(String openid, String uuid, String area, String item) throws ParseException;

    //统计人员打卡记录
    public List AccountRecord(String username);

    //
    public List Count(String openid);

    public List CountAll(String param, String desc,String openid);

    //web端 管理员登陆
    public Map adminLogin(String username, String password);
    //获取所有用户信息
    public List getAllUser();
    //获取所有器材信息
    public List getAllItems();
    //获取所有打卡统计信息
    public List getAllCount();
    //添加用户
    public Map addUser(String name,String phone);
    //删除用户
    public Map deleteUser(String name,String phone);
    //添加器材
    public Map addItem(String itemName,String area);
    //删除器材
    public Map deleteItem(String itemName,String area);
    //查询该用户是否 在数据库
    public List checkUser(String openId);
    //绑定用户 手机号和openId
    public String register(String phone,String openId);
    //添加Cookie
    public void addCookie(String name, String password, HttpServletRequest request, HttpServletResponse response);
    //查询人员打卡总和
    public List getCountAll();

}
