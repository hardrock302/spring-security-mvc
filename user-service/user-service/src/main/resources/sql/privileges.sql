drop table if exists `privileges`;
Create table privileges (
	`id` Int auto_increment,
    `title` VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
)