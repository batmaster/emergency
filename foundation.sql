-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- โฮสต์: localhost
-- เวลาในการสร้าง: 09 พ.ค. 2017  03:42น.
-- เวอร์ชั่นของเซิร์ฟเวอร์: 5.5.50-0ubuntu0.14.04.1
-- รุ่นของ PHP: 5.5.9-1ubuntu4.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- ฐานข้อมูล: `foundation`
--
CREATE DATABASE IF NOT EXISTS `foundation` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `foundation`;

-- --------------------------------------------------------

--
-- โครงสร้างตาราง `accident`
--

CREATE TABLE IF NOT EXISTS `accident` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `type_id` int(10) NOT NULL,
  `title` varchar(64) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `officer_id` int(10) NOT NULL,
  `location_x` varchar(20) NOT NULL,
  `location_y` varchar(20) NOT NULL,
  `status` int(1) NOT NULL,
  `date` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=119 ;

-- --------------------------------------------------------

--
-- โครงสร้างตาราง `accident_image`
--

CREATE TABLE IF NOT EXISTS `accident_image` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `accident_id` int(10) NOT NULL,
  `image` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=58 ;

-- --------------------------------------------------------

--
-- โครงสร้างตาราง `accident_type`
--

CREATE TABLE IF NOT EXISTS `accident_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `title` varchar(20) NOT NULL,
  `color` varchar(7) NOT NULL,
  `image` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- โครงสร้างตาราง `officer`
--

CREATE TABLE IF NOT EXISTS `officer` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
