package com.keerqin.springboot02.testKeerqin;

import com.keerqin.springboot02.util.Tools;

import java.io.File;

public class testBreak {
    public static void main(String[] args) {
        break1:
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            for (int j = 0; j < 5; j++) {
                System.out.println(j);
                if (j == 2) {
                    break break1;
                }
            }
        }

        String str = "123";
        Tools.filterNull(str);
        String result = str == null ? "" : str.toString().trim();
        System.out.println(Integer.parseInt(str));

        File file = new File("D:\\");
        File[] files = file.listFiles();
        for(File file1: files){
            System.out.println(file1);
        }
    }



}
