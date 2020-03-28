package com.luxinx.bean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BeanWaterVO {
    private int aid;//账户主键
    private String prop;//账户类别 1，资产，2负债
    private String owner;//所有者，中文名称
    private String accname;//账户中文名称
    private String account;//账户号码
    private BigDecimal balance;//余额
    private String mtype;//币种CNY|USD|HKD
    private String remark;//备注
    private String operater;//操作者
    List<BeanWater> beanWaterList; //操作流水

    public List<BeanWater> getBeanWaterList() {
        return beanWaterList;
    }

    public void setBeanWaterList(List<BeanWater> beanWaterList) {
        this.beanWaterList = beanWaterList;
    }

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

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public boolean equals(BeanWaterVO o) {
        if(this.aid==o.getAid()){
            return true;
        }else{
            return false;
        }
    }
}
