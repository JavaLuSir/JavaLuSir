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

    /**
     * 秒	 	0-59	 	, - * /
     * 分	 	0-59	 	, - * /
     * 小时	 	0-23	 	, - * /
     * 日期	 	1-31	 	, - * ? / L W C
     * 月份	 	1-12 或者 JAN-DEC	 	, - * /
     * 星期	 	1-7 或者 SUN-SAT	 	, - * ? / L C #
     * 年（可选）	 	留空, 1970-2099	 	, - * /
     */
    //3.添加定时任务  周一到周五晚上23:45执行定时任务
    @Scheduled(cron = "0 45 23 ? * MON-FRI")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate = 10000)
    public void configureTaskFund() {
        //更新投资账户金额
        updateTouziInfo();
        //新账户变动资金流水
        updateAccountInfo();

    }
    //3.添加定时任务  周一到周五晚上23:45执行定时任务
    @Scheduled(cron = "0 0 18 ? * *")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate = 10000)
    public void configureTaskCash() {
        //更新理财投资账户金额
        updateLicai();
        //新账户变动资金流水
        updateAccountInfo();

    }

    private void updateLicai(){

    }

    private void updateAccountInfo() {
        List<Map<String, Object>> moneylist = serviceDataAccount.queryTodayTouziMoney(getLastDate());

        for (Map<String, Object> m : moneylist) {
            String aid = m.get("AID") + "";
            String tztype = m.get("TZTYPE") + "";

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
                String remark = "基金收益";
                if (aid.equals("38") || aid.equals("52")) {
                    remark = "股票收益";
                }
                if (aid.equals("56")) {
                    remark = "理财收益";
                }
                param.put("REMARK", remark);
            } else {
                String remark = "基金亏损";
                if (aid.equals("38") || aid.equals("52")) {
                    remark = "股票亏损";
                }
                param.put("TRKIND", "90");
                param.put("REMARK", remark);
            }
            param.put("TRNUM", String.valueOf(Math.abs(trnum)));
            param.put("IFAUTO", "1");

            serviceDataAccount.addDetail(param);
        }
        serviceDataAccount.updateTouziTime(getTodayDate());


    }

    private void updateTouziInfo() {
        //获取上个交易日期
        String lastDateStr = getLastDate();
        //获取当天交易日期
        String todayDateStr = getTodayDate();
        //更新股票收益信息
        updateStockInfo(lastDateStr, todayDateStr);
        //更新基金收益信息
        updateFundsInfo(lastDateStr, todayDateStr);
    }

    private void updateStockInfo(String lastDateStr, String todayDateStr) {
        List<Map<String, Object>> touziacc = serviceDataAccount.queryTouziInfo("1");
        for (Map<String, Object> m : touziacc) {
            {
                //获取基金编码/股票代码
                String tcode = m.get("TCODE") + "";
                String aid = m.get("AID") + "";

                //获取日期对应收盘价格
                Map<String, String> paramMap = getStockPriceMap(tcode);
                String tprice = paramMap.get(todayDateStr);
                String lprice = paramMap.get(lastDateStr);
                String baseprice = m.get("TBASE") + "";
                String tnum = m.get("TNUM") + "";
                System.out.println(tcode + "--tprice:" + tprice);
                System.out.println(tcode + "--lprice:" + lprice);
                //计算收益入库
                calcPrice(tcode, baseprice, tnum, tprice, lprice, aid);
            }
        }
    }

    /**
     * 更新基金收益
     *
     * @param lastDateStr
     * @param todayDateStr
     */
    private void updateFundsInfo(String lastDateStr, String todayDateStr) {
        List<Map<String, Object>> touziacc = serviceDataAccount.queryTouziInfo("2");
        for (Map<String, Object> m : touziacc) {
            try {
                //获取基金编码/股票代码
                String tcode = m.get("TCODE") + "";
                String aid = m.get("AID") + "";
                //获取上日请求的URL
                String lastUrlStr = paddingFundsURL(tcode, lastDateStr);
                //获取上个交易日的基金净值
                String lprice = getPrice(lastUrlStr);

                String tbase = m.get("TBASE").toString();
                String tnum = m.get("TNUM").toString();
                //获取今天交易日的基金净值
                String todayUrlStr = paddingFundsURL(tcode, todayDateStr);
                //获取今天交易日基金净值
                String tprice = getPrice(todayUrlStr);
                System.out.println(tcode + "--tprice:" + tprice);
                System.out.println(tcode + "--lprice:" + lprice);
                //计算收益入库
                calcPrice(tcode, tbase, tnum, tprice, lprice, aid);
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

    private String paddingFundsURL(String tcode, String datestr) {
        return "https://fundf10.eastmoney.com/F10DataApi.aspx?type=lsjz&code=" + tcode + "&page=1&per=2&sdate=" + datestr + "&edate=" + datestr;
    }

    private String paddingStockURL(String tcode) {
        return "https://data.gtimg.cn/flashdata/hushen/daily/" + getTodayDate().substring(2, 4) + "/" + tcode + ".js";
    }

    /**
     * 根据获取结果计算收益
     *
     * @param tcode
     * @param baseprice
     * @param tprice
     * @param lprice
     * @param aid
     */
    private void calcPrice(String tcode, String baseprice, String tnum, String tprice, String lprice, String aid) {
        if (tprice == null || lprice == null || tprice.isEmpty() || lprice.isEmpty()) {
            return;
        }
        Map<String, String> paramMap = new HashMap<>();
        if (!tprice.isEmpty()) {
            //设置当天净值/价格
            paramMap.put("TNPRICE", tprice);
        }
        if (!lprice.isEmpty()) {
            //设置上个交易日净值/价格
            paramMap.put("TLPRICE", lprice);
        }

        //计算当天收益额度
        BigDecimal numdecimal = new BigDecimal(tnum);
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
        BigDecimal basedecimal = new BigDecimal(baseprice);
        BigDecimal totaldeta = todayprice.subtract(basedecimal);
        BigDecimal totalresult = numdecimal.multiply(totaldeta);
        totalresult.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        paramMap.put("TOTALEARN", String.valueOf(totalresult.floatValue()));

        serviceDataAccount.updateTouziInfo(tcode, aid, paramMap);
    }

    /**
     * 获取股票日期对应收盘价格
     *
     * @param tcode
     * @return
     */
    private Map<String, String> getStockPriceMap(String tcode) {
        String urlStr = paddingStockURL(tcode);
        String result = null;
        try {
            result = HttpUtil.get(urlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String lastday = getLastDate().substring(2).replaceAll("-", "");
        String today = getTodayDate().substring(2).replaceAll("-", "");
        Map<String, String> resultmap = new HashMap<>();
        String pricelast = result.substring(result.indexOf(lastday));
        String[] pricearry = pricelast.split("n\\\\");
        for (String s : pricearry) {
            if (s.startsWith(lastday)) {
                String[] w = s.split(" ");
                resultmap.put(getLastDate(), w[2]);

            }
            if (s.startsWith(today)) {
                String[] d = s.split(" ");
                resultmap.put(getTodayDate(), d[2]);
            }
        }
        return resultmap;
    }

}
