-- 导出 happymatch 的数据库结构
CREATE DATABASE IF NOT EXISTS `happymatch` /*!40100 DEFAULT CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `happymatch`;

-- 导出  表 happymatch.items 结构
CREATE TABLE IF NOT EXISTS `items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8mb3_unicode_ci,
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  `price` int DEFAULT NULL,
  `imageURL` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- 数据导出被取消选择。

-- 导出  表 happymatch.property 结构
CREATE TABLE IF NOT EXISTS `property` (
  `userId` int DEFAULT NULL,
  `itemId` int DEFAULT NULL,
  `count` int DEFAULT NULL,
  UNIQUE KEY `user_item` (`userId`,`itemId`),
  KEY `userId_itemId` (`userId`,`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- 数据导出被取消选择。

-- 导出  表 happymatch.stage 结构
CREATE TABLE IF NOT EXISTS `stage` (
  `stageId` int DEFAULT NULL,
  `stageType` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  `passTime` int DEFAULT NULL,
  `score` int DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- 数据导出被取消选择。

-- 导出  表 happymatch.user 结构
CREATE TABLE IF NOT EXISTS `user` (
  `uid` int NOT NULL AUTO_INCREMENT,
  `userName` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `pwd` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `avatarURL` char(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '/res/avatar/01.jpg',
  `rank` int DEFAULT '0',
  `email` char(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `experience` int DEFAULT '0',
  `level` int DEFAULT '1',
  `coins` int DEFAULT '200',
  `registerTime` timestamp NULL DEFAULT NULL,
  `loginTime` timestamp NULL DEFAULT NULL,
  `signIn` date DEFAULT NULL,
  `role` int DEFAULT '0',
  `token` char(32) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `isActivated` enum('T','F') COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `verifyCode` text COLLATE utf8mb3_unicode_ci,
  `verifyCodeTime` timestamp NULL DEFAULT NULL,
  `savedMap` text COLLATE utf8mb3_unicode_ci,
  KEY `uid` (`uid`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
