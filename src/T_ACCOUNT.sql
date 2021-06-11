/*
Navicat MySQL Data Transfer

Source Server         : 81.70.76.218
Source Server Version : 50731
Source Host           : 81.70.76.218:3306
Source Database       : account

Target Server Type    : MYSQL
Target Server Version : 50731
File Encoding         : 65001

Date: 2021-03-29 04:00:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `T_ACCOUNT`
-- ----------------------------
DROP TABLE IF EXISTS `T_ACCOUNT`;
CREATE TABLE `T_ACCOUNT` (
  `AID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `PROP` varchar(4) NOT NULL DEFAULT '1' COMMENT '账户类别 1，资产，2负债，3股票，4基金',
  `OWNER` varchar(255) NOT NULL COMMENT '所有者，中文名称',
  `ACCNAME` varchar(255) DEFAULT NULL COMMENT '账户中文名称',
  `ACCOUNT` varchar(20) NOT NULL COMMENT '账户',
  `BALANCE` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `MTYPE` varchar(10) NOT NULL DEFAULT 'CNY' COMMENT '币种CNY|USD|HKD',
  `REMARK` varchar(255) DEFAULT NULL,
  `CREATETIME` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATETIME` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `OPERATER` varchar(128) NOT NULL COMMENT '操作者',
  `IFUSE` varchar(2) NOT NULL DEFAULT '0' COMMENT '是否删除了0正常；-1已经删除',
  `ORDERNUM` int(3) NOT NULL DEFAULT '0' COMMENT '页面排序',
  PRIMARY KEY (`AID`),
  KEY `aaa` (`AID`) USING BTREE,
  KEY `qaz` (`ORDERNUM`,`UPDATETIME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_ACCOUNT
-- ----------------------------
INSERT INTO `T_ACCOUNT` VALUES ('33', '1', '寇', '兴业银行', '622908703001203317', '365386.34', 'CNY', '工资卡', '2021-02-27 16:02:21', '2021-02-27 16:02:21', 'admin', '0', '4');
INSERT INTO `T_ACCOUNT` VALUES ('34', '1', '芦', '支付宝(芦)', '18310120297', '318028.41', 'CNY', '支付宝', '2021-02-27 16:02:21', '2021-02-27 16:02:21', 'admin', '0', '5');
INSERT INTO `T_ACCOUNT` VALUES ('35', '1', '芦', '广发银行', '6225683721002338848', '4199.83', 'CNY', '广发银行卡', '2021-02-26 19:24:26', '2021-02-26 19:24:26', 'admin', '0', '7');
INSERT INTO `T_ACCOUNT` VALUES ('36', '1', '芦', '招商银行', '6214 8301 0833 7477', '19957.99', 'CNY', '招商银行', '2021-02-27 16:02:21', '2021-02-27 16:02:21', 'admin', '0', '6');
INSERT INTO `T_ACCOUNT` VALUES ('37', '1', '寇', '华泰证券', '666600088951', '0.00', 'CNY', '涨了财富网', '2020-05-20 13:14:08', '2020-05-20 13:14:08', 'admin', '-1', '10');
INSERT INTO `T_ACCOUNT` VALUES ('38', '1', '芦', '第一创业', '159008256', '35358.38', 'CNY', '第一创业证券', '2021-02-27 16:02:21', '2021-02-27 16:02:21', 'admin', '0', '11');
INSERT INTO `T_ACCOUNT` VALUES ('39', '1', '寇', '光大银行', '6226620207381245', '22939.95', 'CNY', '光大银行', '2021-02-27 16:02:21', '2021-02-27 16:02:21', 'admin', '0', '9');
INSERT INTO `T_ACCOUNT` VALUES ('40', '1', '寇', '廊坊银行', '6230730026573585', '396.44', 'CNY', '廊坊银行还房贷', '2021-02-22 22:01:42', '2021-02-22 22:01:42', 'admin', '0', '8');
INSERT INTO `T_ACCOUNT` VALUES ('41', '1', '余额宝(寇)', '余额宝(寇)', '15811359637', '110.12', 'CNY', '20200330', '2020-12-27 09:43:44', '2020-12-27 09:43:44', 'admin', '0', '5');
INSERT INTO `T_ACCOUNT` VALUES ('42', '1', '寇', '支付宝基金', '15811359637', '896.19', 'CNY', '20200330', '2020-04-11 00:42:02', '2020-04-11 00:42:02', 'admin', '0', '5');
INSERT INTO `T_ACCOUNT` VALUES ('43', '2', '寇', '房贷', '15811359637', '1449474.85', 'CNY', '20200330', '2021-02-22 22:01:42', '2021-02-22 22:01:42', 'admin', '0', '1');
INSERT INTO `T_ACCOUNT` VALUES ('44', '2', '寇', '京东白条', '15811359637', '1252.11', 'CNY', '20200330', '2020-03-31 09:00:49', '2020-03-31 09:00:49', 'admin', '-1', '0');
INSERT INTO `T_ACCOUNT` VALUES ('45', '2', '寇', '京东白条', '15811359637', '0.00', 'CNY', '20200330', '2020-10-19 11:30:28', '2020-10-19 11:30:28', 'admin', '-1', '2');
INSERT INTO `T_ACCOUNT` VALUES ('46', '2', '寇', '花贝', '15811359637', '0.00', 'CNY', '20200330', '2020-10-19 11:30:20', '2020-10-19 11:30:20', 'admin', '-1', '3');
INSERT INTO `T_ACCOUNT` VALUES ('47', '1', '芦鑫', '工商银行', '6212260200033744794', '0.00', 'CNY', '工资卡', '2021-02-04 09:23:59', '2021-02-04 09:23:59', 'admin', '0', '16');
INSERT INTO `T_ACCOUNT` VALUES ('48', '1', '芦', '京东金融', '18310120297', '5000.00', 'CNY', '理财', '2020-04-16 11:57:01', '2020-04-16 11:57:01', 'admin', '-1', '0');
INSERT INTO `T_ACCOUNT` VALUES ('49', '1', '芦', '京东金融', '18310120297', '-44985.64', 'CNY', '京东金融', '2020-05-17 09:18:54', '2020-05-17 09:18:54', 'admin', '-1', '12');
INSERT INTO `T_ACCOUNT` VALUES ('50', '1', '芦', '京东金融', '18310120297', '0.00', 'CNY', '京东金融', '2021-02-04 06:56:47', '2021-02-04 06:56:47', 'admin', '-1', '13');
INSERT INTO `T_ACCOUNT` VALUES ('51', '1', '寇', '华泰证券', '666600088951', '0.00', 'CNY', '理财', '2020-10-19 11:35:24', '2020-10-19 11:35:24', 'admin', '-1', '0');
INSERT INTO `T_ACCOUNT` VALUES ('52', '1', '寇', '华泰证券', '666600088951', '19301.72', 'CNY', '理财', '2021-02-27 16:02:21', '2021-02-27 16:02:21', 'admin', '0', '14');
INSERT INTO `T_ACCOUNT` VALUES ('53', '1', '芦', '天天基金', '18310120297', '12632.84', 'CNY', '天天基金', '2021-02-27 16:02:21', '2021-02-27 16:02:21', 'admin', '0', '15');
INSERT INTO `T_ACCOUNT` VALUES ('54', '1', '芦鑫', '农业银行', '6228480019032625170', '0.00', 'CNY', '工资卡', '2021-02-20 17:49:32', '2021-02-20 17:49:32', 'admin', '0', '13');
INSERT INTO `T_ACCOUNT` VALUES ('55', '1', '芦', '比特币', 'lx6416214@gmail.com', '2265.34', 'CNY', '比特币', '2021-02-27 15:32:01', '2021-02-27 15:32:01', 'admin', '0', '17');
INSERT INTO `T_ACCOUNT` VALUES ('56', '1', '芦', '民生银行', '6226220146095475', '0.00', 'CNY', '民生银行', '2021-03-29 03:57:55', '2021-03-29 03:57:55', 'admin', '0', '0');
