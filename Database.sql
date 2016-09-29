-- phpMyAdmin SQL Dump
-- version 3.5.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 12, 2016 at 04:53 AM
-- Server version: 5.5.29
-- PHP Version: 5.4.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `watering`
--

-- --------------------------------------------------------

--
-- Table structure for table `pump`
--

CREATE TABLE `pump` (
  `ID` int(11) NOT NULL,
  `min` int(11) NOT NULL,
  `max` int(11) NOT NULL,
  `mode` tinyint(4) NOT NULL,
  `name` varchar(30) NOT NULL,
  `last_run` text NOT NULL,
  `status` tinyint(4) NOT NULL,
  `dates_on` text NOT NULL,
  `current_moisture` int(11) NOT NULL DEFAULT '0',
  `time_on` varchar(6) NOT NULL,
  `safety` int(11) NOT NULL,
  `max_timemode` int(11) NOT NULL,
  `master_switch` tinyint(4) NOT NULL,
  `weekly` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pump`
--

INSERT INTO `pump` (`ID`, `min`, `max`, `mode`, `name`, `last_run`, `status`, `dates_on`, `current_moisture`, `time_on`, `safety`, `max_timemode`, `master_switch`, `weekly`) VALUES
(1, 13, 60, 0, 'Cabbage Garden', '11:22 AM on Mon 28th', 0, '1:1:1:1:1:0:1', 110, '23:59', 1, 83, 0, 1),
(2, 16, 55, 1, 'Outdoor flower pots', '11:23 AM on Mon 28th', 0, '1:1:0:0:0:1:1', 32, '01:24', 3, 100, 0, 1);
