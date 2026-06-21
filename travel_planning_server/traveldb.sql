/*
 Navicat Premium Data Transfer

 Source Server         : Windows_Mysql
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost:3306
 Source Schema         : traveldb

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 21/06/2026 13:26:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '城市表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of city
-- ----------------------------
INSERT INTO `city` VALUES (1, '上海');
INSERT INTO `city` VALUES (2, '南京');
INSERT INTO `city` VALUES (3, '北京');
INSERT INTO `city` VALUES (4, '广州');
INSERT INTO `city` VALUES (5, '深圳');
INSERT INTO `city` VALUES (6, '杭州');
INSERT INTO `city` VALUES (7, '苏州');
INSERT INTO `city` VALUES (8, '天津');
INSERT INTO `city` VALUES (9, '重庆');
INSERT INTO `city` VALUES (10, '武汉');
INSERT INTO `city` VALUES (11, '成都');
INSERT INTO `city` VALUES (12, '西安');
INSERT INTO `city` VALUES (13, '郑州');
INSERT INTO `city` VALUES (14, '长沙');
INSERT INTO `city` VALUES (15, '沈阳');
INSERT INTO `city` VALUES (16, '青岛');
INSERT INTO `city` VALUES (17, '济南');
INSERT INTO `city` VALUES (18, '哈尔滨');
INSERT INTO `city` VALUES (19, '福州');
INSERT INTO `city` VALUES (20, '厦门');
INSERT INTO `city` VALUES (21, '昆明');
INSERT INTO `city` VALUES (22, '合肥');
INSERT INTO `city` VALUES (23, '太原');
INSERT INTO `city` VALUES (24, '石家庄');
INSERT INTO `city` VALUES (25, '呼和浩特');
INSERT INTO `city` VALUES (26, '长春');
INSERT INTO `city` VALUES (27, '兰州');
INSERT INTO `city` VALUES (28, '贵阳');
INSERT INTO `city` VALUES (29, '南宁');
INSERT INTO `city` VALUES (30, '海口');
INSERT INTO `city` VALUES (31, '银川');
INSERT INTO `city` VALUES (32, '西宁');
INSERT INTO `city` VALUES (33, '乌鲁木齐');
INSERT INTO `city` VALUES (34, '拉萨');
INSERT INTO `city` VALUES (35, '南昌');
INSERT INTO `city` VALUES (36, '无锡');
INSERT INTO `city` VALUES (37, '宁波');
INSERT INTO `city` VALUES (38, '温州');
INSERT INTO `city` VALUES (39, '佛山');
INSERT INTO `city` VALUES (40, '东莞');
INSERT INTO `city` VALUES (41, '唐山');
INSERT INTO `city` VALUES (42, '烟台');
INSERT INTO `city` VALUES (43, '泉州');
INSERT INTO `city` VALUES (44, '南通');
INSERT INTO `city` VALUES (45, '常州');
INSERT INTO `city` VALUES (46, '徐州');
INSERT INTO `city` VALUES (47, '潍坊');
INSERT INTO `city` VALUES (48, '绍兴');
INSERT INTO `city` VALUES (49, '嘉兴');
INSERT INTO `city` VALUES (50, '泰州');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `pwd` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `logo` blob NULL COMMENT '头像',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('15079793432', 'Tom', 'tomcat999', NULL);

SET FOREIGN_KEY_CHECKS = 1;
