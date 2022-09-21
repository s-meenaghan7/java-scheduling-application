DROP TABLE IF EXISTS first_level_divisions;
CREATE TABLE first_level_divisions (
	Division_ID int(10) PRIMARY KEY NOT NULL auto_increment,
    Division varchar(50),
    Create_Date DATETIME,
    Created_By varchar(50),
    Last_Update TIMESTAMP,
    Last_Updated_By varchar(50),
	Country_ID int(10),
    FOREIGN KEY (Country_ID) REFERENCES countries(Country_ID)
);