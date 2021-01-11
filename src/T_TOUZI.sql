/*
Navicat MySQL Data Transfer

Source Server         : 81.70.76.218
Source Server Version : 50731
Source Host           : 81.70.76.218:3306
Source Database       : account

Target Server Type    : MYSQL
Target Server Version : 50731
File Encoding         : 65001

Date: 2021-01-12 00:01:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `T_TOUZI`
-- ----------------------------
DROP TABLE IF EXISTS `T_TOUZI`;
CREATE TABLE `T_TOUZI` (
  `TID` bigint(5) NOT NULL AUTO_INCREMENT COMMENT '投资账户ID',
  `AID` bigint(20) DEFAULT NULL COMMENT '账户ID',
  `TNAME` varchar(255) DEFAULT NULL COMMENT '基金或股票名字',
  `TCODE` varchar(255) DEFAULT NULL COMMENT '基金或股票编码',
  `TNUM` decimal(10,2) DEFAULT NULL COMMENT '持有数量',
  `TTYPE` tinyint(2) DEFAULT NULL COMMENT '类型：1股票；2基金',
  `TBASE` decimal(10,4) DEFAULT NULL COMMENT '成本',
  `TNPRICE` decimal(6,4) DEFAULT NULL COMMENT '当前现价',
  `TLPRICE` decimal(6,4) DEFAULT NULL COMMENT '昨日价格',
  `TEARN` decimal(8,2) DEFAULT NULL COMMENT '当天收益',
  `TOTALEARN` decimal(8,2) DEFAULT NULL COMMENT '总收益',
  PRIMARY KEY (`TID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_TOUZI
-- ----------------------------
INSERT INTO `T_TOUZI` VALUES ('1', '53', '易方达蓝筹精选组合', '005827', '3274.74', '2', '3.0352', '2.9898', '3.0306', '-133.61', '-148.67');
INSERT INTO `T_TOUZI` VALUES ('2', '53', '易方达中小混合', '110011', '205.48', '2', '9.7334', '9.5644', '9.7189', '-31.75', '-34.73');
