-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: 2016-04-21 07:14:55
-- 服务器版本： 10.1.10-MariaDB
-- PHP Version: 5.6.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `spider`
--

-- --------------------------------------------------------

--
-- 表的结构 `domain`
--

CREATE TABLE `domain` (
  `id` int(10) UNSIGNED NOT NULL,
  `domain` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `md5` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `weight` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- 表的结构 `link`
--

CREATE TABLE `link` (
  `id` int(10) UNSIGNED NOT NULL,
  `from_url` varchar(512) CHARACTER SET utf8 NOT NULL,
  `to_url` varchar(512) CHARACTER SET utf8 NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `from_id` int(11) NOT NULL,
  `to_id` int(11) NOT NULL,
  `text` text CHARACTER SET utf8,
  `md5` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `show_times` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- 表的结构 `page`
--

CREATE TABLE `page` (
  `id` int(10) UNSIGNED NOT NULL,
  `url_id` int(10) UNSIGNED DEFAULT NULL,
  `page_path` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `doc_type` varchar(32) CHARACTER SET utf8 DEFAULT 'HTML',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `JS_handled` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- 表的结构 `url`
--

CREATE TABLE `url` (
  `id` int(10) UNSIGNED NOT NULL,
  `url` varchar(2048) CHARACTER SET utf8 DEFAULT NULL,
  `status` int(11) DEFAULT '0',
  `need_hand_JS` tinyint(1) DEFAULT '0',
  `page_id` int(10) UNSIGNED DEFAULT NULL,
  `weight` int(11) DEFAULT '0',
  `retry_time` int(11) DEFAULT '0',
  `deepth` int(11) DEFAULT '0',
  `domain` varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  `out_link_amount` int(11) NOT NULL DEFAULT '0',
  `to_link_amount` int(11) NOT NULL DEFAULT '0',
  `page_rank` int(11) DEFAULT '0',
  `is_seed` tinyint(1) DEFAULT NULL,
  `max_deepth` int(11) DEFAULT '3',
  `md5` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `domain`
--
ALTER TABLE `domain`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `link`
--
ALTER TABLE `link`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `page`
--
ALTER TABLE `page`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `url`
--
ALTER TABLE `url`
  ADD PRIMARY KEY (`id`),
  ADD KEY `url` (`url`(255));

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `domain`
--
ALTER TABLE `domain`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `link`
--
ALTER TABLE `link`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21814;
--
-- 使用表AUTO_INCREMENT `page`
--
ALTER TABLE `page`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=112;
--
-- 使用表AUTO_INCREMENT `url`
--
ALTER TABLE `url`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10290;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
