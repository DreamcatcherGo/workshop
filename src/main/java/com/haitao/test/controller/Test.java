package com.haitao.test.controller;

import java.io.*;

/**
 * Description:
 * Created by 王海涛（ht.wang05@zuche.com） on 2017/3/20 18:27
 */
public class Test {

    public static void main(String [] args){
        try {
            File file = new File("D:/uploadFiles/aa.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("2");
            bw.close();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
