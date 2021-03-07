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
        Map<String,Float> s = new HashMap<>();
        for(int i=0;i<list.size();i++){
            if(s.containsKey(list.get(i).name)){
                BigDecimal bdm = new BigDecimal(s.get(list.get(i).name));
                BigDecimal bdm2 = new BigDecimal(list.get(i).num);
                BigDecimal a = bdm.add(bdm2);
                s.put(list.get(i).name,a.floatValue());
            }else{
                s.put(list.get(i).name,list.get(i).num);
            }
        }
        Map<String,Float> pr = new HashMap<>();
        for(Node n:list){
            if(pr.get(n.name)==null){
                pr.put(n.name,0.0F);
            }
            Float cvalue = pr.get(n.name);
            Float r = s.get(n.name);
            BigDecimal total = new BigDecimal(r);
            BigDecimal nnum = new BigDecimal(n.num);
            BigDecimal b = nnum.divide(total,BigDecimal.ROUND_HALF_UP);
            BigDecimal pric = new BigDecimal(n.price);
            BigDecimal c = new BigDecimal(cvalue);
            pr.put(n.name,c.add(b.multiply(pric)).floatValue());
        }
         System.out.println(pr);
         System.out.println(s);
     }
}
