package com.luxinx.service.impl;

import com.luxinx.bean.BeanAccount;
import com.luxinx.bean.BeanWater;
import com.luxinx.service.ServiceDataAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 流水查询实现类
 */
@Service
public class DataAccountServiceImpl implements ServiceDataAccount {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<BeanWater> queryAccountInfoById(String id) {
        String sql = "SELECT WID,AID,TRDATE,TRADEKIND,TRTYPE,TRNUM,REMARK,UPDATETIME FROM T_WATER WHERE AID=:id ORDER BY UPDATETIME DESC";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        ResultSetExtractor rset = new RowMapperResultSetExtractor(new BeanWater());
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", id);
        return (List<BeanWater>) npjt.query(sql, param, rset);
    }

    @Override
    public List<BeanAccount> queryAccount() {
        String queryaccount = "SELECT T.* FROM T_ACCOUNT T WHERE T.IFUSE='0' ORDER BY ORDERNUM";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        List<BeanAccount> beanaccounts = npjt.query(queryaccount, new BeanAccount());
        return beanaccounts;
    }

    @Override
    public Map<String, String> addAccount(Map<String, String> param) {
        String prop = param.get("PROP");
        String owner = param.get("OWNER");
        String accname = param.get("ACCNAME");
        String account = param.get("ACCOUNT");
        String balance = param.get("BALANCE");
        String remark = param.get("REMARK");

        String addaccount = "INSERT INTO T_ACCOUNT (PROP,OWNER,ACCNAME,ACCOUNT,BALANCE,REMARK,OPERATER,CREATETIME,UPDATETIME) VALUES (?,?,?,?,?,?,?,NOW(),NOW())";

        jdbcTemplate.update(addaccount, prop, owner, accname, account, balance, remark, "admin");
        Map<String, String> result = new HashMap<>();
        result.put("code", "0");
        result.put("msg", "创建成功");

        return result;
    }

    @Override
    public String delAccount(String id) {
        String delaccount = "UPDATE T_ACCOUNT SET IFUSE='-1' WHERE AID=?";
        jdbcTemplate.update(delaccount, id);
        return "";
    }

    @Override
    public String delDetail(String id) {
        String querydetail = "SELECT T.AID,T.PROP,W.WID,T.BALANCE,W.TRTYPE,W.TRNUM FROM T_ACCOUNT T LEFT JOIN T_WATER W ON T.AID = W.AID WHERE W.WID = ?";
        Map<String, Object> mapdetail = jdbcTemplate.queryForMap(querydetail, id);
        if (mapdetail == null || mapdetail.isEmpty()) {
            return "";
        }
        Long aid = (Long) mapdetail.get("AID");
        String prop = (String) mapdetail.get("PROP");
        BigDecimal balance = (BigDecimal) mapdetail.get("BALANCE");
        String trtype = (String) mapdetail.get("TRTYPE");
        BigDecimal trnum = (BigDecimal) mapdetail.get("TRNUM");
        BigDecimal resultBalance = null;
        if ("1".equals(prop) && "0".equals(trtype)) { //如果是资产账户出金删除明细 增加金额

            resultBalance = balance.add(trnum);
        } else if ("1".equals(prop) && "1".equals(trtype)) {  //如果是资产账户入金删除明细 减少金额
            resultBalance = balance.add(trnum.negate());
        } else if ("2".equals(prop) && "0".equals(trtype)) { //如果是负债户出金 删除明细 减少金额
            resultBalance = balance.add(trnum.negate());
        } else if ("2".equals(prop) && "1".equals(trtype)) {//如果是负债户入金 删除明细 增加金额
            resultBalance = balance.add(trnum);
        }

        String delaccount = "DELETE FROM T_WATER WHERE WID=?";
        jdbcTemplate.update(delaccount, id);
        jdbcTemplate.update("UPDATE T_ACCOUNT SET BALANCE =? WHERE AID=?", resultBalance, aid);
        return resultBalance.toString();
    }

    @Override
    public Map<String, String> addDetail(Map<String, String> param) {

        String aid = param.get("AID");
        String trdate = param.get("TRDATE");
        String tradekind = param.get("TRKIND");
        String trtype = tradekind.substring(tradekind.length() - 1); //从TRKIND中最后一位获取 trtype 0出金；1入金
        String waccount = param.get("WACCOUNT");
        String waccname = param.get("WACCNAME");
        String trnum = param.get("TRNUM");
        String remark = param.get("REMARK");
        String oppid = param.get("OPPID");
        String ifauto = param.get("IFAUTO");
        if (ifauto == null) {
            ifauto = "0";
        }
        if (StringUtils.isEmpty(oppid)) {
            oppid = "0";
        }

        //加入交易明细
        String insertSQL = "INSERT INTO T_WATER (AID,TRDATE,TRADEKIND,TRTYPE,WACCOUNT,WACCNAME,TRNUM,REMARK,OPPID,CREATETIME,UPDATETIME,IFAUTO) VALUES(?,?,?,?,?,?,?,?,?,NOW(),NOW(),?)";
        jdbcTemplate.update(insertSQL, aid, trdate, tradekind, trtype, waccount, waccname, trnum, remark, oppid,ifauto);
        BigDecimal changeMoney = new BigDecimal(trnum);
        if ("0".equals(oppid)) {
            //更新金额
            spend(Integer.parseInt(aid), Integer.parseInt(trtype), changeMoney);
        } else {
            jdbcTemplate.update(insertSQL, oppid, trdate, tradekind, 1, waccount, waccname, trnum, remark, aid);
            //更新金额
            trans(Integer.parseInt(aid), Integer.parseInt(oppid), Integer.parseInt(trtype), changeMoney);
        }

        Map<String, String> result = new HashMap<>();
        result.put("code", "0");
        result.put("msg", "创建成功");
        return result;
    }


    @Override
    public List<Map<String, Object>> queryKind() {
        String sqlKind = "SELECT DICKEY VALUE,DICVAL TEXT FROM T_DICT WHERE TEAM='A' ORDER BY ORDERNUM";
        List<Map<String, Object>> querdict = jdbcTemplate.queryForList(sqlKind);
        return querdict;
    }

    @Override
    public List<Map<String, Object>> queryMonth() {
        //按月份统计的时候不记录负债账户的入金。只记录出金，资产户的出金入金都进行统计
        String sqlMonth = "SELECT DATE_FORMAT(TRDATE,'%Y-%m') months,SUM(CASE TRTYPE WHEN 0 THEN -TRNUM ELSE 0 END) outcome,SUM(CASE TRTYPE WHEN 1 THEN TRNUM ELSE 0 END) income,SUM(CASE TRTYPE WHEN 0 THEN -TRNUM ELSE TRNUM END) total FROM T_WATER T LEFT JOIN T_ACCOUNT A ON T.AID=A.AID WHERE (A.PROP='2' AND T.TRTYPE='0') OR A.PROP='1' AND T.TRADEKIND<>00 GROUP BY months ORDER BY months DESC";
        return jdbcTemplate.queryForList(sqlMonth);
    }

    @Override
    public List<Map<String, Object>> queryMonthItem(String datestr) {
        //按月份统计的时候不记录负债账户的入金。只记录出金，资产户的出金入金都进行统计
        String sqlMonthItem = "SELECT A.accname,T.wid,trdate,T.remark,T.trnum,T.trtype FROM T_WATER T LEFT JOIN T_ACCOUNT A ON T.AID=A.AID WHERE ((A.PROP='2' AND T.TRTYPE='0') OR A.PROP='1') AND T.TRADEKIND<>00 AND  DATE_FORMAT(TRDATE,'%Y-%m') =? ORDER BY T.UPDATETIME DESC";
        return jdbcTemplate.queryForList(sqlMonthItem, datestr);
    }

    @Override
    public List<Map<String, Object>> queryYearReport(String datestr) {
        String sql = "SELECT sum(TRNUM) CASH,  CASE  TRADEKIND WHEN 10 THEN '居家'WHEN 20 THEN '食品' WHEN 30 THEN '交通' WHEN 40 THEN '投资' WHEN 50 THEN '还款' WHEN 61 THEN '工资' WHEN 71 THEN '投资收益' WHEN 81 THEN '其他收益'WHEN 90 THEN '投资亏损' END TRADEKIND FROM T_WATER WHERE DEL=0 AND TRADEKIND<>'00'  AND date_format(TRDATE,'%Y') = :datestr GROUP BY TRADEKIND ";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("datestr", datestr);
        List<Map<String, Object>> result = npjt.queryForList(sql, param);
        for (Map<String, Object> m : result) {
            if (m.get("TRADEKIND").equals("还款")) {
                BigDecimal bcm = new BigDecimal(m.get("CASH") + "");
                BigDecimal huankuan = bcm.divide(new BigDecimal(2));
                m.put("CASH", huankuan.floatValue());
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryTouziInfo(String ttype) {
        String query = "SELECT * FROM T_TOUZI T WHERE TTYPE = ?";
        return jdbcTemplate.queryForList(query, ttype);
    }

    @Override
    public void updateTouziInfo(String tcode, String aid, Map<String, String> touziMap) {
        Set<String> kset = touziMap.keySet();
        StringBuilder updateStr = new StringBuilder("UPDATE T_TOUZI SET ");
        StringBuilder sbd = new StringBuilder();
        for (String mpkey : kset) {
            sbd.append(mpkey).append("='").append(touziMap.get(mpkey)).append("',");
        }
        String str = sbd.substring(0, sbd.length() - 1);
        str += " WHERE TCODE='" + tcode + "' AND AID='" + aid + "'";
        updateStr.append(str);
        System.out.println(updateStr.toString());
        jdbcTemplate.update(updateStr.toString());
    }

    @Override
    public void updateTouziTime(String datestr) {

        jdbcTemplate.update("UPDATE T_TOUZI SET DATESTR='" + datestr + "'");
    }

    @Override
    public List<Map<String, Object>> queryTodayTouziMoney(String datestr) {
        String sql = "SELECT AID,sum(TEARN) TRNUM from T_TOUZI WHERE DATESTR = '" + datestr + "' group by AID";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> queryHeatMapMoney(String datestr) {
        String quersql = "SELECT DATE_FORMAT(TRDATE,'%Y-%m-%d') DAYS,SUM(CASE TRTYPE WHEN 0 THEN -TRNUM ELSE TRNUM END) TOTAL FROM T_WATER T WHERE (TRADEKIND='71' OR TRADEKIND='90') AND DATE_FORMAT(TRDATE,'%Y')=:datestr GROUP BY DAYS";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("datestr", datestr);
        return npjt.queryForList(quersql, param);
    }

    @Override
    public List<Map<String, Object>> queryDayFunds() {
        String sql = "SELECT T.TNAME name,T.TEARN value,T.DATESTR datestr FROM T_TOUZI T WHERE T.TOTALEARN IS NOT NULL ORDER BY TEARN ";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        return npjt.queryForList(sql, new HashMap<>());
    }

    @Override
    public List<Map<String, Object>> monthFunds(String datestr) {
        String sql = "SELECT SUM(CASE TRTYPE WHEN 0 THEN -TRNUM ELSE TRNUM END) TOTAL, DATE_FORMAT(TRDATE,'%Y-%m-%d') DAYS FROM T_WATER WHERE (TRADEKIND='71' OR TRADEKIND='90') AND DATE_FORMAT(TRDATE,'%Y-%m')=:datestr GROUP BY DAYS ";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("datestr", datestr);
        return npjt.queryForList(sql, param);
    }

    @Override
    public List<Map<String, Object>> earnTotal(String datestr) {
        String sql = "";
        if (datestr.length() > 4) {
            sql = "SELECT SUM(CASE TRTYPE WHEN 0 THEN -TRNUM ELSE TRNUM END) TOTAL, DATE_FORMAT(TRDATE,'%Y-%m') DAYS FROM T_WATER WHERE (TRADEKIND='71' OR TRADEKIND='90') AND DATE_FORMAT(TRDATE,'%Y-%m')=:datestr GROUP BY DAYS ";
        } else {
            sql = " SELECT SUM(CASE TRTYPE WHEN 0 THEN -TRNUM ELSE TRNUM END) TOTAL, DATE_FORMAT(TRDATE,'%Y') DAYS FROM T_WATER WHERE (TRADEKIND='71' OR TRADEKIND='90') AND DATE_FORMAT(TRDATE,'%Y')=:datestr GROUP BY DAYS";
        }
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("datestr", datestr);
        return npjt.queryForList(sql, param);
    }

    @Override
    public List<Map<String, Object>> fenbu() {
        String sql = "SELECT ROUND(sum(TNUM*TBASE),2) CASH,CASE TZTYPE WHEN 1 THEN '股票' WHEN 2 THEN '黄金'  WHEN 3 THEN '存款'  WHEN 4 THEN '债券' WHEN 5 THEN '房产' WHEN 6 THEN '车' WHEN 7 THEN '比特币' END KIND FROM T_TOUZI GROUP BY TZTYPE";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        MapSqlParameterSource param = new MapSqlParameterSource();
        return npjt.queryForList(sql, param);
    }

    @Override
    public void resetDate() {
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
        String datestr = sdf.format(cd.getTime());
        String sql = "UPDATE T_TOUZI SET DATESTR=:datestr";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("datestr", datestr);
        npjt.update(sql, param);
        String sqlrevokeIds = "SELECT WID FROM T_WATER WHERE DATE_FORMAT(CREATETIME,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d') AND IFAUTO='1'";
        List<Map<String, Object>> listWaterid = npjt.queryForList(sqlrevokeIds, param);
        for (Map<String,Object> waterid:listWaterid){
            delDetail(waterid.get("WID")+"");
        }

    }

    /**
     * 交易转账更新余额
     *
     * @param accountFrom 来源账户
     * @param accountTo   目标账户
     * @param trtype      交易类型;0出金；1入金
     * @param money       金额
     */
    private void trans(int accountFrom, int accountTo, int trtype, BigDecimal money) {
        if (accountFrom == accountTo) {
            return;
        }
        String queryaccount = "SELECT T.PROP,T.BALANCE FROM T_ACCOUNT T WHERE T.AID=? AND T.IFUSE='0'";
        //源头账户金额
        Map<String, Object> dbfrom = jdbcTemplate.queryForMap(queryaccount, accountFrom);
        String fromProp = (String) dbfrom.get("PROP");

        BigDecimal dbfrommoney = (BigDecimal) dbfrom.get("BALANCE");
        //目标账户金额
        Map<String, Object> dbTo = jdbcTemplate.queryForMap(queryaccount, accountTo);
        String toProp = (String) dbTo.get("PROP");
        BigDecimal dbTomoney = (BigDecimal) dbTo.get("BALANCE");

        //目标账户加
        BigDecimal toBalnaceResult = new BigDecimal(0);
        //原始账号减
        BigDecimal fromBanalceResult = new BigDecimal(0);

        if (fromProp.equals("1") && toProp.equals("1")) {//原/目标账户为资产账户
            //目标账户加
            toBalnaceResult = dbTomoney.add(money);
            //原始账号减
            fromBanalceResult = dbfrommoney.add(money.negate());
        } else if (fromProp.equals("1") && toProp.equals("2")) {//原账户资产。目标账户负债
            //目标账户减
            toBalnaceResult = dbTomoney.add(money.negate());
            //原始账号减
            fromBanalceResult = dbfrommoney.add(money.negate());

        } else if (fromProp.equals("2") && toProp.equals("1")) {//原账户负债。目标账户资产
            //目标账户减
            toBalnaceResult = dbTomoney.add(money);
            //原始账号减
            fromBanalceResult = dbfrommoney.add(money);

        } else if (fromProp.equals("2") && toProp.equals("2")) {//原账户负债。目标账户也是负债
            //目标账户减
            toBalnaceResult = dbTomoney.add(money.negate());
            //原始账号减
            fromBanalceResult = dbfrommoney.add(money);
        }

        String updatemoney = "UPDATE T_ACCOUNT SET BALANCE=? WHERE AID=?";
        jdbcTemplate.update(updatemoney, fromBanalceResult, accountFrom);
        jdbcTemplate.update(updatemoney, toBalnaceResult, accountTo);

    }

    /**
     * 消费更新余额
     *
     * @param account 账户id
     * @param trtype  交易类型0入金；1出金
     * @param num     金额
     */
    private void spend(int account, int trtype, BigDecimal num) {
        String queryaccount = "SELECT T.PROP,T.BALANCE FROM T_ACCOUNT T WHERE T.AID=? AND T.IFUSE='0'";

        Map<String, Object> dbaccount = jdbcTemplate.queryForMap(queryaccount, account);
        BigDecimal balancemoney = (BigDecimal) dbaccount.get("BALANCE");
        String prop = (String) dbaccount.get("PROP");
        //判断是资产还是负债账户。对应记账方式相反
        BigDecimal balnaceresult = new BigDecimal(0);
        if (prop.equals("1") && trtype == 0) {//资产户出金
            BigDecimal oppsitenum = num.negate();//取负值
            balnaceresult = balancemoney.add(oppsitenum); //进行金额计算
        } else if (prop.equals("1") && trtype == 1) {//资产户入金
            balnaceresult = balancemoney.add(num); //进行金额计算,负债账户消费加钱
        } else if (prop.equals("2") && trtype == 0) {
            balnaceresult = balancemoney.add(num); //负债户出金;
        } else if (prop.equals("2") && trtype == 1) {
            BigDecimal oppsitenum = num.negate();//取负值
            balnaceresult = balancemoney.add(num); //负债户入金;
        }

        String updatemoney = "UPDATE T_ACCOUNT SET BALANCE=? WHERE AID=?";

        jdbcTemplate.update(updatemoney, balnaceresult, account);
    }


}
