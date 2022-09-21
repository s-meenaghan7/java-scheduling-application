CREATE TABLE users (
	User_ID int(10) PRIMARY KEY NOT NULL auto_increment,
    User_Name varchar(50) UNIQUE,
    Password TEXT,
    Create_Date DATETIME,
    Created_By varchar(50),
    Last_Update TIMESTAMP,
    Last_Updated_By varchar(50)
);