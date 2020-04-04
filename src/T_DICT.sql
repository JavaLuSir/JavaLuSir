
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_DICT
-- ----------------------------
DROP TABLE IF EXISTS `T_DICT`;
CREATE TABLE `T_DICT` (
  `KID` tinyint(255) NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `DICKEY` varchar(128) NOT NULL COMMENT '字典key',
  `DICVAL` varchar(255) DEFAULT NULL COMMENT '字典value',
  `TEAM` varchar(20) NOT NULL COMMENT '分组',
  `ORDERNUM` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`KID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
