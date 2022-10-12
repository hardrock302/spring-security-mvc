CREATE TABLE users (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` binary(60) NOT NULL,
  `phone_number` varchar(16) DEFAULT NULL,
   `enabled` int  NOT NULL DEFAULT 0,
   `failed_logins` int NOT NULL DEFAULT 0,
   `membership_level` varchar(7) DEFAULT "UNPAID",
   `membership_expiry_date` DATE DEFAULT NULL,
   UNIQUE(`email`),
   UNIQUE(`phone_number`),
   CONSTRAINT ch_active check (enabled in (0, 1, 2)),
   CONSTRAINT ch_phone check (phone_number regexp '^[0-9]{0,3}-[0-9]{3}-[0-9]{3}-[0-9]{4}'),
   CONSTRAINT ch_mem check ((membership_level REGEXP "^UNPAID$" and membership_expiry_date is NULL) or membership_level REGEXP "^GOLD$|^DIAMOND$" and membership_expiry_date is not NULL),
   CONSTRAINT ch_email check (email regexp '^[a-zA-Z0-9][a-zA-Z0-9.!#$%&\'*+-/=?^_`{|}~]*?[a-zA-Z0-9._-]?@[a-zA-Z0-9][a-zA-Z0-9._-]*?[a-zA-Z0-9]?\\.[a-zA-Z]{2,63}$'),
  PRIMARY KEY (`id`),
  FULLTEXT KEY(first_name, last_name, email, membership_level, membership_expiry_date),
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
