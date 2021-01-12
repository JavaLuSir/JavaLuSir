package com.luxinx.service;

import com.luxinx.bean.BeanAccount;
import com.luxinx.bean.BeanWater;
import com.luxinx.bean.BeanWaterVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 后台统一接口方法
 */
public interface ServiceDataAccount {
    /**
     * 查询所有账户以及关联的流水信息
     * @return 账户+流水信息
     */
    List<BeanWaterVO> queryAllAccounts();

    /**
     * 查询所有账户以及关联的流水信息
     * @return 账户详情信息
     */
    List<BeanAccount> queryAccount();

    /**
     * 添加账户信息
     * @return {code:0,"msg":"成功！"}  0成功；-1失败；
     */
    Map<String,String> addAccount(Map<String,String> param);

    /**
     * 删除账户信息
     * @return {code:0,"msg":"成功！"}  0成功；-1失败；
     */
    String delAccount(String id);
    /**
     * 删除账户信息
     * @return {code:0,"funds":"12（余额）","msg":"成功！"}  0成功；-1失败；
     */
    String delDetail(String id);
    /**
     * 增加消费明细
     * @param param 消费明细
     * @return
     */
    Map<String,String> addDetail(Map<String,String> param);

    /**
     * 查询交易类别
     * @return
     */
    List<Map<String,Object>> queryKind();
    /**
     * 查询月份以及消费金额
     * @return
     */
    List<Map<String,Object>> queryMonth();
    /**
     * 查询月份明细
     * @return
     */
    List<Map<String,Object>> queryMonthItem(String datestr);
    /**
     * 查询年度报表
     * @return
     */
    List<Map<String,Object>> queryYearReport(String datestr);

    /**
     * 查询投资账户ttype=1股票ttype=2基金
     * @return
     */
    List<Map<String,Object>> queryTouziInfo(String ttype);

    /**
     * 更新投资表字段
     * @return
     */
    void updateTouziInfo(String tcode ,Map<String,String> touziMap);
    /**
     * 更新投资表字段
     * @return
     */
    void updateTouziTime(String datestr);
    /**
     * 查询今天投资账户变化多少
     * @return
     */
    List<Map<String,Object>> queryTodayTouziMoney(String datestr);

}
