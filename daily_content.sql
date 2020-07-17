/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50714
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50714
 File Encoding         : 65001

 Date: 06/06/2020 00:33:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for daily_content
-- ----------------------------
DROP TABLE IF EXISTS `daily_content`;
CREATE TABLE `daily_content`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日常内容编号',
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `product` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属产品',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工作内容',
  `progress` tinyint(3) NULL DEFAULT 100 COMMENT '工作进度',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备注',
  `insert_dt` datetime(0) NOT NULL COMMENT '插入时间',
  `delete_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '日报内容表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
