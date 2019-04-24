package com.keerqin.springboot02;


import com.keerqin.springboot02.service.infoService;
import com.keerqin.springboot02.util.Tools;
import com.keerqin.springboot02.util.urlUtils;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;

@ComponentScan
@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private infoService infoService;

    //管理员首页
    @RequestMapping("/")
    public String indexPage(HttpServletRequest request) {

        System.out.println(request.getSession().getServletContext().getRealPath("/"));

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            String username = "";
            String password = "";
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];

                if ("name".equals(cookie.getName())) {
                    username = cookie.getValue();
                }
                if ("password".equals(cookie.getName())) {
                    password = cookie.getValue();
                }
            }
            //登陆判断
            Map map = infoService.adminLogin(username, password);
            String isLogin = Tools.filterNull(map.get("status"));
            HttpSession s = request.getSession();

            //System.out.println("session id = " + s.getId());
            //登陆session赋值
            if (isLogin.equals("true")) {
                s.setAttribute("isLogin", "true");
                return "admin_index";
            }
        }
        return "admin_login";
    }

    //管理员登陆
    @PostMapping("/api/adminLogin")
    @ResponseBody
    public Map adminLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("checkbox") String checkbox,
                          HttpServletRequest request,
                          HttpServletResponse response) throws NoSuchAlgorithmException {
        Map map = infoService.adminLogin(username, password);
        String isLogin = Tools.filterNull(map.get("status"));
        HttpSession s = request.getSession();

        String md5password = DigestUtils.md5DigestAsHex(password.getBytes());

        //登陆session赋值
        if (isLogin.equals("true")) {
            s.setAttribute("isLogin", "true");
            //添加Cookie
            if ("remember".equals(checkbox)) {
                infoService.addCookie(username, md5password, request, response);
            }
        }
        return map;
    }


    //进入后台
    @RequestMapping("/adminIndex")
    public String toAdminIndex(HttpSession s) {
        String isLogin = Tools.filterNull(s.getAttribute("isLogin"));
        //session校验
        if ("".equals(isLogin) || isLogin == null) {
            return "admin_login";
        } else if ("true".equals(isLogin)) {
            return "admin_index";
        } else {
            return "admin_login";
        }
    }

    //数据面板
    @RequestMapping("/dashboard")
    public String dashboard(HttpSession s) {
        String isLogin = Tools.filterNull(s.getAttribute("isLogin"));
        //session校验
        if ("".equals(isLogin) || isLogin == null) {
            return "admin_login";
        } else if ("true".equals(isLogin)) {
            return "admin_index";
        } else {
            return "admin_login";
        }
    }

    //进入 用户管理 页面
    @RequestMapping("/userManage")
    public String userManage(HttpSession s, HttpServletRequest request) {
        String isLogin = Tools.filterNull(s.getAttribute("isLogin"));
        //session校验
        if ("".equals(isLogin) || isLogin == null) {
            return "admin_login";
        } else if ("true".equals(isLogin)) {
            //获取所有用户信息
            List list = infoService.getAllUser();
            request.setAttribute("userList", list);
            return "basic_table";
        } else {
            return "admin_login";
        }
    }


    //管理员 新增用户
    @PostMapping("/addUser")
    @ResponseBody
    public Map addUser(@RequestParam("name") String name,
                       @RequestParam("phone") String phone) {
        Map map = infoService.addUser(name, phone);
        return map;
    }

    //管理员 删除用户
    @PostMapping("/deleteUser")
    @ResponseBody
    public Map deleteUser(@RequestParam("name") String name,
                          @RequestParam("phone") String phone) {
        Map map = infoService.deleteUser(name, phone);
        return map;
    }


    //进入 器材管理 页面
    @RequestMapping("/itemManage")
    public String itemManage(HttpSession s, HttpServletRequest request) {
        String isLogin = Tools.filterNull(s.getAttribute("isLogin"));
        //session校验
        if ("".equals(isLogin) || isLogin == null) {
            return "admin_login";
        } else if ("true".equals(isLogin)) {
            //获取所有器材信息
            List list = infoService.getAllItems();
            request.setAttribute("itemList", list);
            return "dynamic_table";
        } else {
            return "admin_login";
        }
    }

    //管理员 新增器材
    @PostMapping("/addItem")
    @ResponseBody
    public Map addItem(@RequestParam("itemName") String itemName,
                       @RequestParam("area") String area) {
        Map map = infoService.addItem(itemName, area);
        return map;
    }

    //管理员 删除器材
    @PostMapping("/deleteItem")
    @ResponseBody
    public Map deleteItem(@RequestParam("itemName") String itemName,
                          @RequestParam("area") String area) {
        Map map = infoService.deleteItem(itemName, area);
        return map;
    }

    //进入 打卡统计 页面
    @RequestMapping("/countManage")
    public String recordManage(HttpSession s, HttpServletRequest request) {
        String isLogin = Tools.filterNull(s.getAttribute("isLogin"));
        //session校验
        if ("".equals(isLogin) || isLogin == null) {
            return "admin_login";
        } else if ("true".equals(isLogin)) {
            //获取所有打卡统计信息
            List list = infoService.getAllCount();
            request.setAttribute("countList", list);
            return "editable_table";
        } else {
            return "admin_login";
        }
    }
    //进入统计总和页面
    @RequestMapping("/countAll")
    public String countAll(HttpSession s, HttpServletRequest request){
        String isLogin = Tools.filterNull(s.getAttribute("isLogin"));
        //session校验
        if ("".equals(isLogin) || isLogin == null) {
            return "admin_login";
        } else if ("true".equals(isLogin)) {
            //获取人员打卡统计总和信息
            List list = infoService.getCountAll();
            request.setAttribute("countList", list);
            return "count_table";
        } else {
            return "admin_login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession s) {
        //清除session
        s.removeAttribute("isLogin");
        return "admin_login";
    }


    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        List list = infoService.getAllArea();
        List list1 = infoService.getAllItem();
        request.setAttribute("list", list);
        request.setAttribute("list1", list1);
        return "index";
    }

    @PostMapping("/admin")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String, Object> map, HttpServletResponse response) {
        if (username.equals("admin") && password.equals("123")) {
            return "redirect:qrcode";
        } else {
            map.put("msg", "用户名或密码错误！");
            return "/login";
        }
    }

    @RequestMapping("/qrcode")
    public String qrcode() {
        return "qrcode";
    }

    @PostMapping(value = "/api/add")
    @ResponseBody
    public Map addItem(HttpServletRequest request) {
        Map map = new HashMap();
        String itemArea = request.getParameter("itemArea");
        String itemName = request.getParameter("itemName");
        infoService.addItem(itemArea, itemName);
        map.put("result", "success");
        List list = infoService.getAllArea();
        List list1 = infoService.getAllItem();
        request.setAttribute("list", list);
        request.setAttribute("list1", list1);
        return map;
    }

    @RequestMapping(value = "/api/changeArea", method = RequestMethod.POST)
    @ResponseBody
    public Map changeArea(HttpServletRequest request) {
        Map map = new HashMap();
        String area = request.getParameter("area");
        List list = infoService.changeArea(area);
        map.put("items", list);
        return map;
    }


    @RequestMapping(value = "/api/update", method = RequestMethod.POST)
    @ResponseBody
    public Map makeQRCode(HttpServletRequest request) {
        String area = request.getParameter("area");
        String item = request.getParameter("item");
        String uuid = request.getParameter("uuid");
        String flushInterval = request.getParameter("flush");

        infoService.updateQRCode(area, item, uuid, flushInterval);
        Map returnMap = new HashMap();
        returnMap.put("result", "success");
        return returnMap;
    }

    //微信端扫码
    @RequestMapping("/api/checkFromWX")
    @ResponseBody
    public String checkFromWX(HttpServletRequest request) throws ParseException {
        String returnString = "";
        String itemArea = request.getParameter("itemArea");
        String itemName = request.getParameter("itemName");
        String uuid = request.getParameter("uuid");
        String openid = request.getParameter("openid");
        //判断是不是最新的二维码
        List list = infoService.checkQRCode(uuid.trim());
        if (list.size() == 0) {
            //itemtable没有记录，返回错误
            returnString = "fail";
        } else {
            //有记录
            returnString = infoService.addScanRecord(openid, uuid, itemArea, itemName);
        }
        return returnString;
    }

    @RequestMapping("/api/AccountRecord")
    @ResponseBody
    public List AccountRecord(HttpServletRequest request) {
        String username = request.getParameter("username");
        Map map = new HashMap();
        List list = infoService.AccountRecord(username);
        map.put("items", list);
        return list;
    }

    @RequestMapping("/api/Count")
    @ResponseBody
    public List Count(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        Map map = new HashMap();
        List list = infoService.Count(openid);
        map.put("items", list);
        return list;
    }

    @RequestMapping("/api/CountAll")
    @ResponseBody
    public List CountAll(HttpServletRequest request) {
        String param = request.getParameter("param");
        String desc = request.getParameter("desc");
        String openid = request.getParameter("openid");
        if (desc.equals("0")) {
            desc = "";
        } else if (desc.equals("1")) {
            desc = "desc";
        }
        Map map = new HashMap();
        List list = infoService.CountAll(param, desc, openid);
        map.put("items", list);
        return list;
    }

    @RequestMapping("/api/login")
    @ResponseBody
    public String login(HttpServletRequest request)  {
        //微信端登录code值
        String code = request.getParameter("code");
        //请求地址 https://api.weixin.qq.com/sns/jscode2session
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        //开发者设置中的appId
        requestUrlParam.put("appid", "wx226b0e2c78a5a089");
        //开发者设置中的appSecret
        requestUrlParam.put("secret", "475341058eec70714776e97bcd4b93e7");
        //小程序调用wx.login返回的code
        requestUrlParam.put("js_code", code);
        //默认参数
        requestUrlParam.put("grant_type", "authorization_code");

        //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识
        //JSONObject jsonObject = JSONObject.parseObject(urlUtils.sendPost(requestUrl, requestUrlParam));
        //JSONObject jsonObject = new JSONObject(urlUtils.sendPost(requestUrl, requestUrlParam));
        String data = "";
        data = urlUtils.sendPost(requestUrl, requestUrlParam);
        JSONObject json = JSONObject.fromObject(data);
        String openId = Tools.filterNull(json.get("openid"));
        List list = infoService.checkUser(openId);
        if (list.size() == 0) {
            json.put("isUser", "false");
            data = json.toString();
            return data;
        } else {
            json.put("isUser", "true");
            data = json.toString();
            return data;
        }
    }

    @RequestMapping("/api/register")
    @ResponseBody
    public String register(HttpServletRequest request) {
        String phone = Tools.filterNull(request.getParameter("phone"));
        String openid = Tools.filterNull(request.getParameter("openid"));
        String result = infoService.register(phone, openid);
        return result;
    }
}
