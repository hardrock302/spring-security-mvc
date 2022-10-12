drop table if exists `permissions`;
Create table permissions (
	`id` Int auto_increment,
    `name` VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
)