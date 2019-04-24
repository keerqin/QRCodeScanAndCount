package com.keerqin.springboot02.testKeerqin;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.*;

public class testJson {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test(){
        List list = new ArrayList();
        list.add("list1");
        list.add("list2");
        list.add("list3");

        Map map = new LinkedHashMap();
        map.put("name","keerqin");
        map.put("age","15");
        map.put("list",list);
        String t = JSON.toJSONString(map,true);
        System.out.println(t);
    }

    public static void main(String[] args) throws IOException {
        //Jedis jedis = new Jedis("192.168.188.128", 6379);
        //jedis.zrangeWithScores("count", 100, 500);

        File file = new File("E:\\test.txt");
        File file1 = new File("E:\\test1.txt");
        if(!file1.exists()){

        }
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] b = new byte[1024];
        int x = 0;
        while ( (x = fileInputStream.read(b)) != -1){
            String s = new String(b,"GBK");
            System.out.println(s);
        }
        String s1 = "123";
        String s2 = new String(s1.getBytes("GB2312"), "ISO-8859-1");

    }

}

