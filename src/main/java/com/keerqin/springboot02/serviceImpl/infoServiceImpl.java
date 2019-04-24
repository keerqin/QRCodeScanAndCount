package com.keerqin.springboot02.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keerqin.springboot02.service.infoService;
import com.keerqin.springboot02.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class infoServiceImpl implements infoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List getAllArea() {
        String sql = "select distinct itemArea from itemtable ";
        List list = jdbcTemplate.queryForList(sql);
        return list;
    }

    @Override
    public List getAllItem() {
        List firstRecord = getAllArea();
        Map map = (Map) firstRecord.get(0);
        String itemArea = map.get("itemArea").toString();
        String sql = "select distinct itemName from itemtable where itemArea = '" + itemArea + "'";
        List list = jdbcTemplate.queryForList(sql);
        return list;
    }

    @Override
    public List changeArea(String itemArea) {
        String sql = "select distinct itemName from itemtable where itemArea = '" + itemArea + "'";
        List list = jdbcTemplate.queryForList(sql);
        List returnList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            String itemName = map.get("itemName").toString();
            returnList.add(itemName);
        }
        return returnList;
    }

    @Transactional
    @Override
    public String updateQRCode(String itemArea, String itemName, String uuid, String flushInterval) {
        try {
            String updateSql = "update itemtable set uuid='" + uuid + "' , lastUpdateTime=sysdate(),flushInterval='" + flushInterval + "' where itemArea='" + itemArea + "' and itemName='" + itemName + "'";
            jdbcTemplate.execute(updateSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }


    //判断是不是最新的二维码
    @Override
    public List checkQRCode(String uuid) {
        String checkSQL = "select * from itemtable where uuid='" + uuid + "'";
        List list = jdbcTemplate.queryForList(checkSQL);
        return list;
    }

    @Transactional
    @Override
    public String addScanRecord(String openid, String uuid, String itemArea, String itemName) throws ParseException {
        String getNameSql = "select name from user where openid = ? ";
        List openIdList = jdbcTemplate.queryForList(getNameSql, openid);
        Map openIdMap = (Map) openIdList.get(0);
        String username = Tools.filterNull(openIdMap.get("NAME"));

        String checkSQL = "select * from scanrecord where username='" + username + "' and itemArea = '" + itemArea + "' and itemName='" + itemName + "' order by updateTime desc";
        List list = jdbcTemplate.queryForList(checkSQL);
        //没有记录，直接添加
        if (list.size() == 0) {
            String insertSQL = "insert into scanrecord(qrCodeUUID,username,updateTime,itemArea,itemName) " +
                    "values('" + uuid + "','" + username + "',sysdate(),'" + itemArea + "','" + itemName + "')";
            jdbcTemplate.execute(insertSQL);
        } else {
            //偶数  都是成对的记录。直接插入即可
            if (list.size() % 2 != 1) {
                String insertSQL = "insert into scanrecord(qrCodeUUID,username,updateTime,itemArea,itemName) " +
                        "values('" + uuid + "','" + username + "',sysdate(),'" + itemArea + "','" + itemName + "')";
                jdbcTemplate.execute(insertSQL);
            } else {
                //奇数  有单条记录(需要进行统计操作)
                //有记录，计算扫码间隔
                Map map = (Map) list.get(0);
                String updateTime = map.get("updateTime").toString();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

                Date last = df.parse(updateTime);
                Date now = new Date();
                long diff = now.getTime() - last.getTime();
                long minutes = diff / (60 * 1000);
                //插入redis
                redisTemplate.opsForZSet().incrementScore("count",username,minutes);

                String insertSQL = "insert into scanrecord(qrCodeUUID,username,updateTime,itemArea,itemName) " +
                        "values('" + uuid + "','" + username + "',sysdate(),'" + itemArea + "','" + itemName + "')";
                jdbcTemplate.execute(insertSQL);

                String checkExist = "select * from recordcount where username='" + username + "' and itemArea='" + itemArea + "' and itemName='" + itemName + "'";
                List countList = jdbcTemplate.queryForList(checkExist);
                //判断这个人在这个区域、器材有没有记录
                //没有统计记录，新增
                if (countList.size() == 0) {
                    //插入即可
                    String sql = "insert into recordcount(username,minutes,itemArea,itemName) values ('" + username + "','" + minutes + "','" + itemArea + "','" + itemName + "')";
                    jdbcTemplate.execute(sql);
                } else {
                    //有记录，则更新记录
                    String minutesSql = "select r.minutes from recordcount as r where username='" + username + "' and itemArea='" + itemArea + "' and itemName='" + itemName + "' ";
                    Map minutesMap = jdbcTemplate.queryForMap(minutesSql);
                    String oldMinutes = minutesMap.get("minutes").toString();

                    String sql = "update recordcount set minutes= '" + oldMinutes + "' +'" + minutes + "' " +
                            " where username='" + username + "' and itemArea='" + itemArea + "' and itemName='" + itemName + "' ";
                    jdbcTemplate.execute(sql);
                    return "" + minutes;
                }
            }
        }
        return "success";
    }


    @Override
    public List AccountRecord(String username) {
        List list = new ArrayList();
        String sql1 = "select itemName,itemArea,DATE_FORMAT(updateTime,'%Y-%m-%d %H:%m:%s') as updateTime from scanrecord where username='" + username + "' order by itemArea,itemName ";
        list = jdbcTemplate.queryForList(sql1);
        return list;
    }

    @Override
    public List Count(String openid) {

        String sql = "select name from user where openid = ?";
        List openidList = jdbcTemplate.queryForList(sql, openid);
        Map map = (Map) openidList.get(0);

        List list = new ArrayList();
        String sql1 = "select itemName,itemArea,minutes from recordcount where username='" + Tools.filterNull(map.get("NAME")) + "' order by itemArea,itemName ";
        list = jdbcTemplate.queryForList(sql1);
        return list;
    }

    @Override
    public List CountAll(String param, String desc, String openid) {
        String sql = "select name from user where openid = ?";
        List openidList = jdbcTemplate.queryForList(sql, openid);
        Map map = (Map) openidList.get(0);

        List list = new ArrayList();
        String sql1 = "";
        if (StringUtils.isEmpty(param)) {
            sql1 = "select itemName,itemArea,minutes,username from recordcount where username='" + Tools.filterNull(map.get("NAME")) + "' order by username ";
        } else {
            if (param.equals("minutes")) {
                sql1 = "select itemName,itemArea,minutes,username from recordcount where username='" + Tools.filterNull(map.get("NAME")) + "' order by " + param + "+0  " + desc + "  ";
            } else {
                sql1 = "select itemName,itemArea,minutes,username from recordcount where username='" + Tools.filterNull(map.get("NAME")) + "' order by " + param + " " + desc + "  ";
            }
        }
        list = jdbcTemplate.queryForList(sql1);
        return list;
    }

    //用户登陆校验
    @Override
    public Map adminLogin(String username, String password) {

        Map returnMap = new HashMap();
        try {
            String sql = "select * from admin where username =? and password=? ";
            //密码MD5加密后的值
            String md5password = DigestUtils.md5DigestAsHex(password.getBytes());
            List userList = jdbcTemplate.queryForList(sql, username, md5password);
            if (userList.size() == 0 || userList == null) {
                //没有这个用户
                returnMap.put("status", "false");
            } else if (userList.size() == 1) {
                //有一个用户，正确
                returnMap.put("status", "true");
            } else {
                //有多个用户，异常
                returnMap.put("status", "exception");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    @Override
    public List getAllUser() {
        List list = new ArrayList();
        try {
            String sql = "select * from User ";
            list = jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List getAllItems() {
        List list = new ArrayList();
        try {
            String sql = "select id,itemname,itemarea,lastupdatetime from itemtable";
            list = jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List getAllCount() {
        List list = new ArrayList();
        try {
            String sql = "select * from recordcount";
            list = jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    @Transactional
    public Map addUser(String name, String phone) {
        Map map = new HashMap();
        int i = 0;
        try {
            String checkSql = "select * from user where name = '" + name + "' or phone = '" + phone + "' ";
            List list = jdbcTemplate.queryForList(checkSql);
            if (list != null && list.size() > 0) {
                map.put("status", "false");
                map.put("reason", "新增失败，姓名或手机号重复！");
            } else {
                String sql = "insert into user(name,phone) values('" + name + "','" + phone + "')";
                i = jdbcTemplate.update(sql);
                if (i == 1) {
                    map.put("status", "true");
                    map.put("reason", "新增成功！");
                } else if (i == 0) {
                    map.put("status", "false");
                    map.put("reason", "新增失败，程序出错！");
                }
            }
        } catch (Exception e) {
            map.put("status", "false");
            map.put("reason", "程序出错！");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional
    public Map deleteUser(String name, String phone) {
        Map map = new HashMap();
        int i = 0;
        try {
            String sql = "delete from user where name=? and phone=?";
            i = jdbcTemplate.update(sql, name, phone);
            if (i == 1) {
                map.put("status", "true");
                map.put("reason", "删除成功！");
            } else if (i == 0) {
                map.put("status", "false");
                map.put("reason", "删除失败，程序出错！");
            }
        } catch (Exception e) {
            map.put("status", "false");
            map.put("reason", "删除失败，程序出错！");
            e.printStackTrace();
        }
        return map;
    }
    @Transactional
    @Override
    public Map addItem(String itemName, String area) {
        Map map = new HashMap();
        int i = 0;
        try {
            String checkSql = "select * from itemtable where itemname = '" + itemName + "' and itemarea = '" + area + "' ";
            List list = jdbcTemplate.queryForList(checkSql);
            if (list != null && list.size() > 0) {
                map.put("status", "false");
                map.put("reason", "新增失败，厂区和器材名称重复 ！");
            } else {
                String sql = "insert into itemtable(itemname,itemarea) values('" + itemName + "','" + area + "')";
                i = jdbcTemplate.update(sql);
                if (i == 1) {
                    map.put("status", "true");
                    map.put("reason", "新增成功！");
                } else if (i == 0) {
                    map.put("status", "false");
                    map.put("reason", "新增失败，程序出错！");
                }
            }
        } catch (Exception e) {
            map.put("status", "false");
            map.put("reason", "程序出错！");
            e.printStackTrace();
        }
        return map;
    }
    @Transactional
    @Override
    public Map deleteItem(String itemName, String area) {
        Map map = new HashMap();
        int i = 0;
        try {
            String sql = "delete from itemtable where itemname=? and itemarea=?";
            i = jdbcTemplate.update(sql, itemName, area);
            if (i == 1) {
                map.put("status", "true");
                map.put("reason", "删除成功！");
            } else if (i == 0) {
                map.put("status", "false");
                map.put("reason", "删除失败，程序出错！");
            }
        } catch (Exception e) {
            map.put("status", "false");
            map.put("reason", "删除失败，程序出错！");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List checkUser(String openId) {
        String sql = "select * from user where openid =? ";
        List list = jdbcTemplate.queryForList(sql, openId);
        return list == null ? new ArrayList() : list;
    }

    @Override
    @Transactional
    public String register(String phone, String openId) {
        String result = "";
        //先检查该手机号是否存在数据
        String sql = "select * from user where phone = ?";
        List list = jdbcTemplate.queryForList(sql, phone);
        if (list.size() == 1) {
            //更新 user 绑定openid
            String updateSql = "update user set openid = ? where phone = ?";
            jdbcTemplate.update(updateSql, openId, phone);
            result = "1";
        } else {
            //不存在该手机号，提醒用户
            result = "0";
        }
        return result;
    }

    @Override
    public void addCookie(String name, String password, HttpServletRequest request, HttpServletResponse response) {
        Cookie nameCookie = new Cookie("name", name);
        Cookie passwordCookie = new Cookie("password", password);

        nameCookie.setPath(request.getContextPath() + "/");
        passwordCookie.setPath(request.getContextPath() + "/");

        nameCookie.setMaxAge(60 * 60 * 24 * 7);
        passwordCookie.setMaxAge(60 * 60 * 24 * 7);

        nameCookie.setSecure(true);

        response.addCookie(nameCookie);
        response.addCookie(passwordCookie);
    }

    //打卡总和返回
    @Override
    public List getCountAll() {
        return saveToRedis();
    }


    public List saveToRedis() {
        //Set<String> range = redisTemplate.opsForZSet().reverseRange("count", 0, 10);
        //System.out.println("获取到的排行列表:" + JSON.toJSONString(range));
        Set<ZSetOperations.TypedTuple<String>> rangeWithScores = redisTemplate.opsForZSet().reverseRangeWithScores("count", 0, -1);
        Iterator<ZSetOperations.TypedTuple<String>> it = rangeWithScores.iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            Map map = new HashMap();
            ZSetOperations.TypedTuple<String> str = it.next();
            map.put("name",str.getValue());
            map.put("score",str.getScore());
            list.add(map);
        }
        return list;
    }
}
