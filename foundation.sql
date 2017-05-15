-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 16, 2017 at 01:47 AM
-- Server version: 5.5.50-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `foundation`
--
CREATE DATABASE IF NOT EXISTS `foundation` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `foundation`;

-- --------------------------------------------------------

--
-- Table structure for table `accident`
--

DROP TABLE IF EXISTS `accident`;
CREATE TABLE IF NOT EXISTS `accident` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `type_id` int(10) NOT NULL,
  `title` varchar(64) NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `officer_id` varchar(32) NOT NULL,
  `location_x` varchar(20) NOT NULL,
  `location_y` varchar(20) NOT NULL,
  `status` int(1) NOT NULL,
  `date` varchar(20) NOT NULL,
  `date_approve` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=135 ;

--
-- Dumping data for table `accident`
--

INSERT INTO `accident` (`id`, `type_id`, `title`, `user_id`, `officer_id`, `location_x`, `location_y`, `status`, `date`, `date_approve`) VALUES
(121, 1, 'testๆๆๆ', '1496141293739021', '', '8.170831694037938', '99.70472004264592', 0, '2017-05-10 17:22:48', ''),
(122, 1, 'เทสๆๆ', '1496141293739021', '', '8.1707954', '99.7049451', 0, '2017-05-10 17:23:12', ''),
(123, 1, 'เทสๆๆ2', '1496141293739021', '', '8.1707954', '99.7049451', 0, '2017-05-10 17:36:59', ''),
(128, 1, 'เทสๆๆ', '1496141293739021', '', '8.1707954', '99.7049451', 0, '2017-05-11 10:01:10', ''),
(129, 1, 'เทสๆ2', '1496141293739021', '', '8.1707954', '99.7049451', 0, '2017-05-11 10:01:41', ''),
(130, 1, 'test ๆ เช้า', '1496141293739021', '1496141293739021', '8.1707954', '99.7049451', 2, '2017-05-12 08:14:13', '2017-05-14 16:27:29'),
(131, 1, 'เทสๆๆ', '1496141293739021', '1496141293739021', '8.1707954', '99.7049451', 1, '2017-05-15 14:24:25', '2017-05-15 14:24:51'),
(132, 1, 'xcgt', '1573676312706180', '', '37.4219983', '-122.084', 0, '2017-05-15 14:43:48', ''),
(133, 1, 'ทดสอบ', '1734480096569399', '1496141293739021', '8.1619621', '99.7238333', 0, '2017-05-15 15:52:28', '2017-05-15 15:57:38'),
(134, 1, 'รถไฟชนกัน', '237679433383299', '', '15.33850767672816', '101.13435219973326', 0, '2017-05-16 00:20:34', '');

-- --------------------------------------------------------

--
-- Table structure for table `accident_image`
--

DROP TABLE IF EXISTS `accident_image`;
CREATE TABLE IF NOT EXISTS `accident_image` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `accident_id` int(10) NOT NULL,
  `image` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=70 ;

--
-- Dumping data for table `accident_image`
--

INSERT INTO `accident_image` (`id`, `accident_id`, `image`) VALUES
(60, 129, 'uploads/129_2017_05_11_10_01_I5G20170511100136.jpg'),
(63, 130, 'uploads/130_2017_05_12_08_14_I5G20170512081410.jpg'),
(64, 131, 'uploads/131_2017_05_15_14_24_I5G20170515142423.jpg'),
(65, 133, 'uploads/133_2017_05_15_15_52_I5G20170515155224.jpg'),
(66, 134, 'uploads/134_2017_05_16_00_20_20170515_185751.jpg'),
(67, 134, 'uploads/134_2017_05_16_00_20_20170515_185156.jpg'),
(68, 134, 'uploads/134_2017_05_16_00_20_P0OTO_20170509_200521.jpg'),
(69, 134, 'uploads/134_2017_05_16_00_20_P0OTO_20170509_200030.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `accident_type`
--

DROP TABLE IF EXISTS `accident_type`;
CREATE TABLE IF NOT EXISTS `accident_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `title` varchar(20) NOT NULL,
  `color` varchar(7) NOT NULL,
  `image` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `accident_type`
--

INSERT INTO `accident_type` (`id`, `title`, `color`, `image`) VALUES
(1, 'อุบัติเหตุจราจร', '#f4511e', 'uploads/car.png'),
(2, 'อุบัติเหตุอัคคีภัย', '#ffb300', 'uploads/fire.png'),
(3, 'อุบัติเหตุทั่วไป', '#00acc1', 'uploads/human.png'),
(4, 'เกี่ยวกับสัตว์', '#8c9eff', 'uploads/snake.png');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` varchar(32) NOT NULL,
  `current_name` varchar(64) NOT NULL,
  `type` int(1) NOT NULL,
  `status` int(1) NOT NULL DEFAULT '1',
  `register_date` varchar(20) NOT NULL,
  `last_use_date` varchar(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `current_name`, `type`, `status`, `register_date`, `last_use_date`) VALUES
('10213255603489040', 'Poramate Homprakob', 1, 0, '2017-05-12 00:18:48', '2017-05-12 00:18:48'),
('1496141293739021', 'Kritsada Suksom', 2, 1, '2017-05-15 23:04:52', '2017-05-15 23:04:52'),
('1573676312706180', 'Github Tit', 0, 1, '2017-05-15 23:07:57', '2017-05-15 23:07:57'),
('1653374238009517', 'Ple Prapapan Khongkeaw', 0, 1, '2017-05-12 00:18:48', '2017-05-12 00:18:48'),
('1734480096569399', 'Nam Kaewkorn', 0, 1, '2017-05-15 15:55:38', '2017-05-15 15:55:38'),
('1961235784106567', 'Sutap Pooklang', 0, 1, '2017-05-15 16:11:54', '2017-05-15 16:11:54'),
('227360921092126', 'Poramate ThePingpong', 0, 1, '2017-05-15 03:23:30', '2017-05-15 03:23:30'),
('237679433383299', 'แบตนิ รับทีตะ', 2, 1, '2017-04-16 00:24:22', '2017-05-16 01:47:13');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
