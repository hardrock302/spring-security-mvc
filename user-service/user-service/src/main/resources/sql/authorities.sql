DROP TABLE IF EXISTS `authorities`;
CREATE TABLE `authorities` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `authorities_idx_1` (`email`,`authority`),
  CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`email`) REFERENCES `users` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;