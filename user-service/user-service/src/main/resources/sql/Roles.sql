CREATE TABLE Roles(
	id  int  AUTO_INCREMENT,
	access ENUM('ADMIN''),
    privilege int, 
    FOREIGN KEY(id) REFERENCES privileges(id),
    PRIMARY KEY(id)
)