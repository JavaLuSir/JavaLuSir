package com.luxinx.controller;

import com.alibaba.fastjson.JSONObject;
import com.luxinx.bean.BeanAccount;
import com.luxinx.bean.BeanWater;
import com.luxinx.bean.BeanWaterVO;
import com.luxinx.service.ServiceDataAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台统一请求接口方法
 */
@RestController
@RequestMapping("/rest")
public class ControllerAccount {
    @Autowired
    private ServiceDataAccount serviceDataAccount;

    @RequestMapping("/doLogin")
    public String dologin() {

        return "redirect:/index.html";
    }

    @RequestMapping("/list")
    public String list() {
        List<BeanWaterVO> accountlist = serviceDataAccount.queryAllAccounts();
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
        System.out.println(id);
        serviceDataAccount.delDetail(id);
        Map<String, String> result = new HashMap<>();
        result.put("code", "0");
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
}
