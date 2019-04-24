package com.keerqin.springboot02.testKeerqin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class Test {
    public static void main(String[] args) {
 /*       String s1 = new StringBuilder("go").append("od").toString();
        System.out.println(s1.intern()==s1);

        String s2 = new StringBuilder("ja").append("va").toString();
        System.out.println(s2.intern()==s2);
*/

        String s1 = "Programming";
        String s2 = new String("Programming");
        String s3 = "Program";
        String s4 = "ming";
        String s5 = "Program" + "ming";
        String s6 = s3 + s4;
        System.out.println(s1 == s2);//false
        System.out.println(s1 == s5);//true
        System.out.println(s1 == s6);//false
        System.out.println(s1 == s6.intern());
        System.out.println(s2 == s2.intern());

      /*try {
          File file = new File("E:\\test.txt");
          File wirteFile = new File("E:\\test1.txt");
          FileInputStream f = new FileInputStream(file);
          FileOutputStream f1 = new FileOutputStream(wirteFile);
          byte[] b = new byte[1024];
          while( f.read(b) != -1){
              f1.write(b);
              //设置字符编码
              String str = new String(b,"GBK");
              System.out.println(str);
          }
      }catch (Exception e){
          e.printStackTrace();
      }*/

        System.out.println((int)(char)(byte)-1);

    }

}
