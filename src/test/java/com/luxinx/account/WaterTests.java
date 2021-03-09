package com.luxinx.account;

import com.luxinx.bean.BeanAccount;
import com.luxinx.util.HttpUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WaterTests {
    @Test
    public void testServiceDataAccountQueryAllAccounts() throws Exception {
        File file = new File("D://11.txt");
        FileInputStream a = new FileInputStream(file);
        InputStreamReader ins = new InputStreamReader(a);
        BufferedReader bufferedReader = new BufferedReader(ins);
        String i;
        List<Node> nlist = new ArrayList<>();
        while ((i = bufferedReader.readLine()) != null) {
            String[] z = i.split(",");
            Node n = new Node();
            n.name=z[0];
            n.num=Float.parseFloat(z[1]);
            n.price = Float.parseFloat(z[2]);
            nlist.add(n);
        }
        bufferedReader.close();
        ins.close();
        a.close();
        Collections.sort(nlist);
        calcFunds(nlist);
    }

     class Node<T> implements Comparable<T>{
        private String name;
        private float num;
        private float price;


         @Override
         public int compareTo(T o) {
             Node n = (Node)o;
             char b1 = n.name.charAt(1);
             char b2 = name.charAt(1);

             if(b1>b2){
                 return 1;
             }else if(b1<b2){
                 return -1;
             }else
             return 0;
         }
         @Override
         public String toString(){
             return  name+":"+num+":"+price+"\r\n";
         }
     }
     private void calcFunds(List<Node> list){
        Map<String,Float> name_num = new HashMap<>();
        for(int i=0;i<list.size();i++){
            //求总数量 如果有基金名字取出做计算并放回name_num
            if(name_num.containsKey(list.get(i).name)){
                BigDecimal bdm = new BigDecimal(name_num.get(list.get(i).name));
                BigDecimal bdm2 = new BigDecimal(list.get(i).num);
                BigDecimal a = bdm.add(bdm2);
                name_num.put(list.get(i).name,a.floatValue());
            }else{
                //如果没有直接放入数量
                name_num.put(list.get(i).name,list.get(i).num);
            }
        }
        //存储基金价格
        Map<String,Float> pr = new HashMap<>();
        for(Node n:list){
            //判断如果不存在放入pr变量
            pr.putIfAbsent(n.name, 0.0F);
            //从pr获取净值
            Float cvalue = pr.get(n.name);
            //获取数量
            Float num = name_num.get(n.name);
            //总数量
            BigDecimal total = new BigDecimal(num);
            //当前基金数量
            BigDecimal nnum = new BigDecimal(n.num);
            //当前基金占总数比
            BigDecimal percent = nnum.divide(total,8,BigDecimal.ROUND_HALF_DOWN);
            //当前基金净值
            BigDecimal pric = new BigDecimal(n.price);
            BigDecimal c = new BigDecimal(cvalue);
            //基金净值乘以占比后相加
            pr.put(n.name,c.add(percent.multiply(pric)).floatValue());

        }
         System.out.println(pr);
         System.out.println(name_num);
     }
}
