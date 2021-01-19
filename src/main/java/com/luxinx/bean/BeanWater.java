package com.luxinx.bean;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 流水类T_WATER
 */
public class BeanWater implements RowMapper {
    /**
     *     private int wid;//流水id
     *     private Date trdate;//交易时间
     *     private String tradekind;//0转账；1居家；2食品；3交通；4投资；5还款
     *     private String trtype;//交易类型0出金；1入金
     *     private BigDecimal trnum;//交易金额
     *     private Date createtime;//创建时间
     *     private Date updatetime;//修改时间
     */
    private int wid;//流水id
    private int aid;
    private Date trdate;//交易时间
    private String tradekind;//0转账；1居家；2食品；3交通；4投资；5还款
    private String trtype;//交易类型0出金；1入金
    private String waccount;
    private String waccname;
    private BigDecimal trnum;//交易金额
    private String remark;
    private Date createtime;//创建时间
    private Date updatetime;//修改时间
    private BeanAccount beanAccount;

    public BeanAccount getBeanAccount() {
        return beanAccount;
    }

    public void setBeanAccount(BeanAccount beanAccount) {
        this.beanAccount = beanAccount;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public Date getTrdate() {
        return trdate;
    }

    public void setTrdate(Date trdate) {
        this.trdate = trdate;
    }

    public String getTradekind() {
        return tradekind;
    }

    public void setTradekind(String tradekind) {
        this.tradekind = tradekind;
    }

    public String getTrtype() {
        return trtype;
    }

    public void setTrtype(String trtype) {
        this.trtype = trtype;
    }

    public String getWaccount() {
        return waccount;
    }

    public void setWaccount(String waccount) {
        this.waccount = waccount;
    }

    public String getWaccname() {
        return waccname;
    }

    public void setWaccname(String waccname) {
        this.waccname = waccname;
    }

    public BigDecimal getTrnum() {
        return trnum;
    }

    public void setTrnum(BigDecimal trnum) {
        this.trnum = trnum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {

        BeanWater beanWater = new BeanWater();
        beanWater.setAid(resultSet.getInt("AID"));
        beanWater.setWid(resultSet.getInt("WID"));
        beanWater.setTrdate( resultSet.getDate("TRDATE"));
      //  beanWater.setTradekind( resultSet.getString("TRADEKIND"));
        beanWater.setTrtype( resultSet.getString("TRTYPE"));
        beanWater.setRemark( resultSet.getString("REMARK"));
      //  beanWater.setCreatetime( resultSet.getDate("CREATETIME"));
        beanWater.setUpdatetime( resultSet.getDate("UPDATETIME"));
        beanWater.setTrnum(resultSet.getBigDecimal("TRNUM"));

        return beanWater;
    }
}
