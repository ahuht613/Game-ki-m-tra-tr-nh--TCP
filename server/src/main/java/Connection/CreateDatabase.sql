/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Thu Ha
 * Created: Nov 4, 2024
 */
CREATE DATABASE IF NOT EXISTS ltm_nhom12;
USE ltm_nhom12;

-- User table to store player information
CREATE TABLE `users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `full_name` VARCHAR(50) NOT NULL,
  `total_score` FLOAT NOT NULL DEFAULT 0,
  `status` ENUM('Online', 'Offline', 'Ingame') NOT NULL DEFAULT 'Offline',
  PRIMARY KEY (`id`)
);

CREATE TABLE `btl_nhom1`.`results` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `userId1` VARCHAR(20) NOT NULL,
  `userId2` VARCHAR(20) NOT NULL,
  `UserWin` ENUM('USER1', 'USER2', 'HOA') NOT NULL,
  PRIMARY KEY (`id`)
);