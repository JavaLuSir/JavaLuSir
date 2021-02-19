package com.luxinx.cron;

import com.alibaba.fastjson.JSONObject;
import com.luxinx.service.ServiceDataAccount;
import com.luxinx.util.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class Tzcrond {

    @Autowired
    private ServiceDataAccount serviceDataAccount;

    //3.添加定时任务  周一到周五晚上23:45执行定时任务
    @Scheduled(cron = "0 45 23 ? * MON-FRI")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate = 10000)
    public void configureTasks() {
        //更新投资账户金额
        updateTouziInfo();
        //更新账户变动资金流水
        updateAccountInfo();

    }

    private void updateAccountInfo() {
        List<Map<String, Object>> moneylist = serviceDataAccount.queryTodayTouziMoney(getLastDate());

        for (Map<String, Object> m : moneylist) {
            String aid = m.get("AID") + "";
            Float trnum = Float.valueOf(m.get("TRNUM") + "");
            if (trnum == 0) {
                continue;
            }
            String todayDateStr = getTodayDate();
            Map<String, String> param = new HashMap<>();
            param.put("AID", aid);
            param.put("TRDATE", todayDateStr);
            if (trnum > 0) {
                param.put("TRKIND", "71");
                param.put("REMARK", "基金收益");
            } else {
                param.put("TRKIND", "90");
                param.put("REMARK", "基金亏损");
            }
            param.put("TRNUM", String.valueOf(Math.abs(trnum)));
            serviceDataAccount.addDetail(param);
        }
        serviceDataAccount.updateTouziTime(getTodayDate());


    }

    private void updateTouziInfo() {
        //获取上个交易日期
        String lastDateStr = getLastDate();
        //获取当天交易日期
        String todayDateStr = getTodayDate();
        List<Map<String, Object>> touziacc = serviceDataAccount.queryTouziInfo("2");
        for (Map<String, Object> m : touziacc) {
            try {
                //获取基金编码/股票代码
                String tcode = m.get("TCODE") + "";
                String aid = m.get("AID") + "";
                //获取上日请求的URL
                String lastUrlStr = paddingURL(tcode, lastDateStr);
                //获取上个交易日的基金净值
                String lprice = getPrice(lastUrlStr);
                //获取今天交易日的基金净值
                String todayUrlStr = paddingURL(tcode, todayDateStr);
                //获取今天交易日基金净值
                String tprice = getPrice(todayUrlStr);
                System.out.println(tcode + "--tprice:" + tprice);
                System.out.println(tcode + "--lprice:" + lprice);

                //构造更新数据参数
                Map<String, String> paramMap = new HashMap<>();
                if (!tprice.isEmpty()) {
                    //设置当天净值/价格
                    paramMap.put("TNPRICE", tprice);
                }
                if (!lprice.isEmpty()) {
                    //设置上个交易日净值/价格
                    paramMap.put("TLPRICE", lprice);
                }
                if (tprice.isEmpty() || lprice.isEmpty()) {
                    continue;
                }
                //计算当天收益额度
                BigDecimal numdecimal = new BigDecimal(m.get("TNUM").toString());
                //今天交易净值
                BigDecimal todayprice = new BigDecimal(tprice);
                //上个交易净值
                BigDecimal lpricedecimal = new BigDecimal(lprice);
                //净值差额
                BigDecimal delta = todayprice.subtract(lpricedecimal);
                //净值价差
                BigDecimal todayresult = numdecimal.multiply(delta);
                todayresult.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                paramMap.put("TEARN", String.valueOf(todayresult.floatValue()));
                //计算总收益额度
                BigDecimal basedecimal = new BigDecimal(m.get("TBASE").toString());
                BigDecimal totaldeta = todayprice.subtract(basedecimal);
                BigDecimal totalresult = numdecimal.multiply(totaldeta);
                totalresult.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                paramMap.put("TOTALEARN", String.valueOf(totalresult.floatValue()));

                serviceDataAccount.updateTouziInfo(tcode, aid, paramMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取上个交易日的基金净值如果当天为周一获取周日时间
     *
     * @return dateStr yyyy-mm-dd
     */
    private String getLastDate() {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = new Date();
        Calendar cd = Calendar.getInstance();
        cd.setTime(dt);
        String wk = weeks[cd.get(Calendar.DAY_OF_WEEK) - 1];
        if (wk.equals("星期一")) {
            cd.add(Calendar.DATE, -3);
        } else {
            cd.add(Calendar.DATE, -1);
        }
        return sdf.format(cd.getTime());
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = new Date();
        return sdf.format(dt);
    }

    private String getPrice(String urlStr) {
        try {
            Thread.sleep(200);//防止过快请求
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(urlStr);
        String r = null;
        try {
            r = HttpUtil.post(urlStr, null, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map tb = (Map) JSONObject.parse(r.substring(12, r.length() - 1).replaceAll("'", "\\\\'"));
        String html = (String) tb.get("content");
        Document doc = Jsoup.parseBodyFragment(html);
        Element body = doc.body();
        if (body.getElementsByClass("tor bold").first() == null) {
            return "";
        } else {
            return body.getElementsByClass("tor bold").first().text();
        }
    }

    private String paddingURL(String tcode, String datestr) {
        return "https://fundf10.eastmoney.com/F10DataApi.aspx?type=lsjz&code=" + tcode + "&page=1&per=2&sdate=" + datestr + "&edate=" + datestr;
    }

}
