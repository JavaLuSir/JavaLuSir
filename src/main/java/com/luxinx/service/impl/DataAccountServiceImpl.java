package com.luxinx.service.impl;

import com.luxinx.bean.BeanAccount;
import com.luxinx.bean.BeanWater;
import com.luxinx.bean.BeanWaterVO;
import com.luxinx.service.ServiceDataAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流水查询实现类
 */
@Service
public class DataAccountServiceImpl implements ServiceDataAccount {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<BeanWaterVO> queryAllAccounts() {

        ResultSetExtractor rset = new RowMapperResultSetExtractor(new BeanWater());
        String sql = "SELECT T.AID,T.PROP,T.OWNER,T.ACCNAME,T.ACCOUNT,T.BALANCE,T.MTYPE,W.REMARK,T.OPERATER,W.AID,W.TRDATE,W.WID,W.TRADEKIND,W.TRTYPE,W.TRNUM,W.CREATETIME CREATETIME,W.UPDATETIME UPDATETIME FROM T_ACCOUNT T LEFT JOIN T_WATER W on T.AID=W.AID AND W.DEL='0' WHERE T.IFUSE='0' order by ORDERNUM asc,UPDATETIME desc";
        List<BeanWater> result = (List<BeanWater>) jdbcTemplate.query(sql, rset);

        return BeanWater.toVO(result);
    }

    @Override
    public List<BeanAccount> queryAccount() {
        String queryaccount = "SELECT T.* FROM T_ACCOUNT T WHERE T.IFUSE='0'";
        List<BeanAccount> beanaccounts = jdbcTemplate.query(queryaccount, new BeanAccount());
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
        /*PreparedStatementSetter setter = new ArgumentPreparedStatementSetter(new Object[]{prop,owner,accname,account,balance,remark});
        try {
            PreparedStatement stmt;
            // stmt = jdbcTemplate.getDataSource().getConnection().prepareStatement();
            PreparedStatementCreatorFactory creator = new PreparedStatementCreatorFactory(addaccount);
            creator.newPreparedStatementCreator(listargs);
            //jdbcTemplate.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
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
        Long aid = (Long) mapdetail.get("AID");
        String prop = (String) mapdetail.get("PROP");
        BigDecimal balance = (BigDecimal) mapdetail.get("BALANCE");
        String trtype = (String) mapdetail.get("TRTYPE");
        BigDecimal trnum = (BigDecimal) mapdetail.get("TRNUM");
        BigDecimal resultBalance = null;
        if("1".equals(prop)&&"0".equals(trtype)){ //如果是资产账户出金删除明细 增加金额

            resultBalance = balance.add(trnum);
        }else if("1".equals(prop)&&"1".equals(trtype)){  //如果是资产账户入金删除明细 减少金额
            resultBalance = balance.add(trnum.negate());
        }else if("2".equals(prop)&&"0".equals(trtype)){ //如果是负债户出金 删除明细 减少金额
            resultBalance = balance.add(trnum.negate());
        }else if("2".equals(prop)&&"1".equals(trtype)){//如果是负债户入金 删除明细 增加金额
            resultBalance = balance.add(trnum);
        }

        String delaccount = "DELETE FROM T_WATER WHERE WID=?";
        jdbcTemplate.update(delaccount, id);
        jdbcTemplate.update("UPDATE T_ACCOUNT SET BALANCE =? WHERE AID=?",resultBalance,aid);
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
        if (StringUtils.isEmpty(oppid)) {
            oppid = "0";
        }

        //加入交易明细
        String insertSQL = "INSERT INTO T_WATER (AID,TRDATE,TRADEKIND,TRTYPE,WACCOUNT,WACCNAME,TRNUM,REMARK,OPPID,CREATETIME,UPDATETIME) VALUES(?,?,?,?,?,?,?,?,?,NOW(),NOW())";
        jdbcTemplate.update(insertSQL, aid, trdate, tradekind, trtype, waccount, waccname, trnum, remark, oppid);
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
        String sqlMonth = "select DISTINCT DATE_FORMAT(TRDATE,'%Y-%m') months from T_WATER order by months desc";
        return jdbcTemplate.queryForList(sqlMonth);
    }

    @Override
    public List<Map<String, Object>> queryMonthItem(String datestr) {
        String sqlMonthItem = "select wid,trdate,remark,trnum,trtype from T_WATER WHERE  DATE_FORMAT(TRDATE,'%Y-%m') =? order by UPDATETIME desc";
        return jdbcTemplate.queryForList(sqlMonthItem,datestr);
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
