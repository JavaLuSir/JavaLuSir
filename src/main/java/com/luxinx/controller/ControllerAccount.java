package com.luxinx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luxinx.bean.BeanAccount;
import com.luxinx.bean.BeanWater;
import com.luxinx.cron.Tzcrond;
import com.luxinx.service.ServiceDataAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 后台统一请求接口方法
 */
@RestController
@RequestMapping("/rest")
public class ControllerAccount {
    @Autowired
    private ServiceDataAccount serviceDataAccount;

    @Autowired
    private Tzcrond tzcrond;

    @RequestMapping("/doLogin")
    public String dologin() {

        return "redirect:/index.html";
    }

    @RequestMapping("/list")
    public String list(@RequestParam String id) {
        List<BeanWater> accountlist = serviceDataAccount.queryAccountInfoById(id);
        return JSONObject.toJSONString(accountlist);
    }

    @RequestMapping("/account")
    public String account() {
        List<BeanAccount> accountlist = serviceDataAccount.queryAccount();
        return JSONObject.toJSONString(accountlist);
    }

    @RequestMapping("/addaccount")
    public String addaccount(@RequestBody Map<String, String> reqparam) {
        Map<String, String> resultmap = serviceDataAccount.addAccount(reqparam);

        return JSONObject.toJSONString(resultmap);
    }

    @RequestMapping("/delaccount")
    public String delaccount(@RequestParam String id) {
        serviceDataAccount.delAccount(id);
        return "";
    }

    @RequestMapping("/deldetail")
    public String deldetail(@RequestParam String id) {
        String resultBalance = serviceDataAccount.delDetail(id);
        Map<String, String> result = new HashMap<>();
        result.put("code", "0");
        result.put("funds", resultBalance);
        result.put("msg", "操作成功");
        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/adddetail")
    public String adddetail(@RequestBody Map<String, String> reqparam) {
        Map<String, String> result = serviceDataAccount.addDetail(reqparam);
        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/querykind")
    public String querykind() {
        List<Map<String, Object>> result = serviceDataAccount.queryKind();
        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/querymonth")
    public String querymonth() {
        List<Map<String, Object>> monthList = serviceDataAccount.queryMonth();
        return JSONObject.toJSONString(monthList);
    }

    @RequestMapping("/querymonthitem")
    public String querymonthitem(@RequestParam String datestr) {
        List<Map<String, Object>> monthList = serviceDataAccount.queryMonthItem(datestr);
        return JSONObject.toJSONString(monthList);
    }

    @RequestMapping("/queryyearreport")
    public String queryyearreport(@RequestParam String datestr) {
        List<Map<String, Object>> yearReport = serviceDataAccount.queryYearReport(datestr);
        return JSONObject.toJSONString(yearReport);
    }

    @RequestMapping("/heatmap")
    public String heatmap(@RequestParam String year) {
        List<Map<String, Object>> rst = serviceDataAccount.queryHeatMapMoney(year);
        List<List> daymoney = new ArrayList<>();
        for (Map<String, Object> h : rst) {
            List<String> t = new ArrayList<>();
            t.add(h.get("DAYS") + "");
            t.add(h.get("TOTAL") + "");
            daymoney.add(t);
        }

        return JSONObject.toJSONString(daymoney);
    }

    @RequestMapping("/dayfunds")
    public String dayfunds() {
        //  String s= "[{'name':'xxx','value': '-0.07','itemStyle':{'color':'#129eff'}},{'name':'bbb','value': '0.07','itemStyle':{'color':'red'}}]";
        List<Map<String, Object>> fundays = serviceDataAccount.queryDayFunds();
        for (Map<String, Object> funds : fundays) {
            Map<String, String> color = new HashMap<>();
            Map<String, String> label = new HashMap<>();
            if ((funds.get("value") + "").startsWith("-")) {
                color.put("color", "#129eff");
                //label.put("position", "right");
            } else {
                color.put("color", "red");
                //label.put("position", "left");
            }
            funds.put("itemStyle", color);
            funds.put("label", label);
        }
        return JSON.toJSONString(fundays);
    }

    @RequestMapping("/monthfunds")
    public String monthfunds(@RequestParam String datestr) {
        List<Map<String, Object>> fundays = serviceDataAccount.monthFunds(datestr);
        List<List> result = new ArrayList<>();
        Calendar calendar = GregorianCalendar.getInstance();
        String[] dr = datestr.split("-");
        //设置当前月份
        calendar.set(Integer.parseInt(dr[0]), Integer.parseInt(dr[1]) - 1, 1);

        int enddate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取当月最后一天

        //循环判断是否需要填充日期
        for (int i = 1; i <= enddate; i++) {
            String datestrfor = "";
            boolean isfilldatestr = true;//是否填充日期
            List<String> a = new ArrayList<>();
            if (i < 10) {
                datestrfor = datestr + "-" + "0" + i;
            } else {
                datestrfor = datestr + "-" + i;
            }

            for (Map<String, Object> m : fundays) {
                String dbdaystr = (m.get("DAYS") + "");
                if (datestrfor.equals(dbdaystr)) {
                    //有相同的就用数据库日期设置状态不填充
                    isfilldatestr = false;
                    a.add(m.get("DAYS") + "");
                    a.add("");
                    double num = Double.parseDouble(m.get("TOTAL") + "");
                    if (num > 0) {
                        a.add("+" + m.get("TOTAL") + "");
                    } else {
                        a.add(m.get("TOTAL") + "");
                    }
                    result.add(a);
                    break;
                }
            }
            if (isfilldatestr) {
                a.add(datestrfor);
                a.add("");
                a.add("0");
                result.add(a);
            }
        }

        return JSON.toJSONString(result);
    }

    @RequestMapping("/earntotal")
    public String earntotal(@RequestParam String datestr) {
        List<Map<String, Object>> erantotal = serviceDataAccount.earnTotal(datestr);
        return JSON.toJSONString(erantotal);
    }

    @RequestMapping("/refreshfunds")
    public String refreshfunds() {
        tzcrond.configureTasks();
        return "操作成功";
    }

    @RequestMapping("/fenbu")
    public String fenbu() {
        return JSON.toJSONString(serviceDataAccount.fenbu());
    }

    @RequestMapping("/resetdate")
    public String resetdate() {
        serviceDataAccount.resetDate();
        return "操作成功";
    }
}
