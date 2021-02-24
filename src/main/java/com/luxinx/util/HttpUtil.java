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
import java.net.URLEncoder;
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
        StringBuffer sbf = new StringBuffer();
        while ((line=br.readLine())!=null) {
            if(line!=null&&!line.isEmpty()){
                sbf.append(line);
            }
        }
        //断开连接
        conn.disconnect();
        return sbf.toString();
    }


    /**
     * HTTP GET请求
     * @param urlStr 要访问的HTTP地址
     * @return
     * @throws Exception
     */
    public static String get(String urlStr) throws Exception{
        StringBuffer sbf = new StringBuffer();
        URL url = new URL(urlStr);
        HttpURLConnection  httpConn = (HttpURLConnection) url.openConnection();
        try {
            // //设置连接属性
            httpConn.setRequestMethod("GET");// 设置URL请求方法，默认为“GET”
            httpConn.setDoOutput(false);// 禁止 URL 连接进行输出，默认为“false”
            httpConn.setDoInput(true);// 使用 URL 连接进行输入，默认为“true”
            httpConn.setUseCaches(false);// 忽略缓存
            // 设置 《请求头》信息
            httpConn.setRequestProperty("accept", "*/*");
            httpConn.setRequestProperty("Content-Type", "application/octet-stream"); //设置的文本类型,此字段必须和和服务器端处理请求流的编码一致,否则无法解析
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.setRequestProperty("Charset", "UTF-8");
            //httpConn.setRequestProperty("BrokerID", URLEncoder.encode("MFTST0", "utf-8"));

            // 前面的操作只是将“请求头”和“正文”组装成request对象，最后真正以HTTP协议发送数据的是下面的getInputStream();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            //读取数据存放位置
            String line;

            while ((line=responseReader.readLine())!=null) {
                if(line!=null&&!line.isEmpty()){
                    sbf.append(line);
                }
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            httpConn.disconnect();
        }
        return sbf.toString();
    }

}
