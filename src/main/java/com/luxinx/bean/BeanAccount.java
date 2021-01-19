package com.luxinx.bean;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 账户实体类与流水类一对多关联T_ACCOUNT
 */
public class BeanAccount implements RowMapper<BeanAccount> {
    private int aid;
    private String prop;
    private String owner;
    private String accname;
    private String account;
    private BigDecimal balance;
    private String mtype;
    private String remark;
    private Date createtime;
    private Date updatetime;
    private String operater;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAccname() {
        return accname;
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getBalance() {
        balance.setScale(2);
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
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

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    @Override
    public BeanAccount mapRow(ResultSet resultSet, int i) throws SQLException {

        BeanAccount account = new BeanAccount();
        account.setAid(resultSet.getInt("AID"));
        account.setProp(resultSet.getString("PROP"));
        account.setOwner(resultSet.getString("OWNER"));
        account.setAccname(resultSet.getString("ACCNAME"));
        account.setAccount(resultSet.getString("ACCOUNT"));
        account.setBalance(resultSet.getBigDecimal("BALANCE"));
        account.setMtype(resultSet.getString("MTYPE"));
        account.setRemark(resultSet.getString("REMARK"));
        account.setCreatetime(resultSet.getDate("CREATETIME"));
        account.setUpdatetime(resultSet.getDate("UPDATETIME"));
        account.setOperater(resultSet.getString("OPERATER"));

        return account;
    }

    @Override
    public String toString() {
        return "BeanAccount{" +
                "aid=" + aid +
                ", prop='" + prop + '\'' +
                ", owner='" + owner + '\'' +
                ", accname='" + accname + '\'' +
                ", account='" + account + '\'' +
                ", balance=" + balance +
                ", mtype='" + mtype + '\'' +
                ", remark='" + remark + '\'' +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                ", operater='" + operater + '\'' +
                '}';
    }
}
