CREATE TABLE customers (
	Customer_ID int(10) PRIMARY KEY NOT NULL auto_increment,
    Customer_Name varchar(50),
    Address varchar(100),
    Postal_Code varchar(50),
    Phone varchar(50),
    Special varchar(50),
    Create_Date DATETIME,
    Created_By varchar(50),
    Last_Update TIMESTAMP,
    Last_Updated_By varchar(50),
    Division_ID int(10),
    FOREIGN KEY (Division_ID) REFERENCES first_level_divisions(Division_ID)
);