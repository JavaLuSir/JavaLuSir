package com.luxinx.service;

import com.luxinx.bean.BeanAccount;
import com.luxinx.bean.BeanWater;

import java.util.List;
import java.util.Map;

/**
 * 后台统一接口方法
 */
public interface ServiceDataAccount {

    /**
     * 查询所有账户以及关联的流水信息
     * @param id 账户Account主建
     * @return 流水信息
     */
    List<BeanWater> queryAccountInfoById(String id);

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
    void updateTouziInfo(String tcode,String aid ,Map<String,String> touziMap);
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
    /**
     * 查询当前热力图数据
     * @param datestr 年度字符串
     * @return
     */
    List<Map<String,Object>> queryHeatMapMoney(String datestr);
    /**
     * 查询当日基金贡献额度
     * @return
     */
    List<Map<String,Object>> queryDayFunds();
    /**
     * 查询当月基金显示
     * @param datestr 日期年月
     * @return
     */
    List<Map<String,Object>> monthFunds(String datestr);
    /**
     * 查询基金总共盈利
     * @param datestr 日期到月
     * @return
     */
    List<Map<String,Object>> earnTotal(String datestr);

    /**
     * 查询投资分布
     * @return
     */
    List<Map<String,Object>> fenbu();

    /**
     * 日期设置成上个交易日
     */
    void resetDate();
}
