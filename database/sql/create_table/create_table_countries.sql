DROP TABLE IF EXISTS countries;
CREATE TABLE countries (
	Country_ID int(10) PRIMARY KEY NOT NULL auto_increment,
    Country varchar(50),
    Create_Date DATETIME,
    Created_By varchar(50),
    Last_Update TIMESTAMP,
    Last_Updated_By varchar(50)
);