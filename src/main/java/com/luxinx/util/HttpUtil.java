package com.luxinx.util;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class HttpUtil {

    /**
     * HTTP POST请求
     * @param urlStr 要访问的HTTP地址
     * @param property 连接要设置的参数
     * @param content 要发送的内容
     * @return
     * @throws Exception
     */
    public static String post(String urlStr,Map<String,String> property,String content) throws Exception{

        //创建一个URL连接对象
        URL url=new URL(urlStr);
        //打开连接 URL会根据url具体类型返回不同的连接对象
        HttpURLConnection conn=(HttpURLConnection) url.openConnection();
        //允许输出操作
        conn.setDoOutput(true);
        //允许输入操作
        conn.setDoInput(true);
        //请求方式 POST
        conn.setRequestMethod("POST");
        //是否允许客户端缓存 不允许
        conn.setUseCaches(false);
        //是否可以重定向
        conn.setInstanceFollowRedirects(true);
        //设置请求头
        conn.setRequestProperty("Content", "application/x-www-form-urlencoded");
        //设置发送内容的编码格式
        conn.setRequestProperty("Charset", "utf-8");
        //若有自定义设置 则循环设置
        if(property!=null){
            Set<String> keySet=property.keySet();
            for(String key:keySet){
                if(key!=null&&!key.isEmpty()){
                    conn.setRequestProperty(key, property.get(key));
                }
            }
        }
        //设置连接主机超时时间
        conn.setConnectTimeout(3*1000);
        //设置传送数据时间
        conn.setReadTimeout(3*1000);
        //连接
        conn.connect();
        //创建一个输出流
        DataOutputStream out=new DataOutputStream(conn.getOutputStream());
        //向连接主机发送数据
        out.writeBytes(content);
        //out.writeBytes(URLEncoder.encode(contentStr, "utf-8"));
        //关闭数据流
        out.flush();
        out.close();
        //从主机读取数据
        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        //读取数据存放位置
        String line;
        while ((line=br.readLine())!=null) {
            if(line!=null&&!line.isEmpty()){
                break;
            }
        }
        //断开连接
        conn.disconnect();
        return line;
    }

}
