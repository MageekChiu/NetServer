/*
Navicat MySQL Data Transfer

Source Server         : mageek
Source Server Version : 50557
Source Host           : mageek.cn:3306
Source Database       : NetServer

Target Server Type    : MYSQL
Target Server Version : 50557
File Encoding         : 65001

Date: 2018-04-02 20:07:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for history
-- ----------------------------
DROP TABLE IF EXISTS `history`;
CREATE TABLE `history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `signal` float(8,3) NOT NULL,
  `power` float(8,3) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of history
-- ----------------------------

-- ----------------------------
-- Table structure for info
-- ----------------------------
DROP TABLE IF EXISTS `info`;
CREATE TABLE `info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mac` char(20) COLLATE utf8_unicode_ci NOT NULL,
  `signal` float(8,3) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=390 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of info
-- ----------------------------
INSERT INTO `info` VALUES ('1', 'csad334', '23.400', '2018-04-01 23:30:22');
INSERT INTO `info` VALUES ('2', '33d', '12.300', '2017-12-02 22:02:23');
