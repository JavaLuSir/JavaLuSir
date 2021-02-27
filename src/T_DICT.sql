/*
Navicat MySQL Data Transfer

Source Server         : 81.70.76.218
Source Server Version : 50731
Source Host           : 81.70.76.218:3306
Source Database       : account

Target Server Type    : MYSQL
Target Server Version : 50731
File Encoding         : 65001

Date: 2021-02-27 17:11:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `T_DICT`
-- ----------------------------
DROP TABLE IF EXISTS `T_DICT`;
CREATE TABLE `T_DICT` (
  `KID` tinyint(255) NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `DICKEY` varchar(128) NOT NULL COMMENT '字典key',
  `DICVAL` varchar(255) DEFAULT NULL COMMENT '字典value',
  `TEAM` varchar(20) NOT NULL COMMENT '分组',
  `ORDERNUM` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`KID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_DICT
-- ----------------------------
INSERT INTO `T_DICT` VALUES ('1', '00', '转账', 'A', '0');
INSERT INTO `T_DICT` VALUES ('2', '10', '居家', 'A', '0');
INSERT INTO `T_DICT` VALUES ('3', '20', '食品', 'A', '0');
INSERT INTO `T_DICT` VALUES ('4', '30', '交通', 'A', '0');
INSERT INTO `T_DICT` VALUES ('5', '40', '投资', 'A', '0');
INSERT INTO `T_DICT` VALUES ('6', '50', '还房贷', 'A', '0');
INSERT INTO `T_DICT` VALUES ('7', '61', '工资', 'A', '0');
INSERT INTO `T_DICT` VALUES ('8', '71', '投资收益', 'A', '0');
INSERT INTO `T_DICT` VALUES ('9', '81', '其他收益', 'A', '0');
INSERT INTO `T_DICT` VALUES ('10', '90', '投资亏损', 'A', '0');
