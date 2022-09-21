CREATE TABLE appointments (
	Appointment_ID int(10) PRIMARY KEY NOT NULL auto_increment,
    Title varchar(50),
    Description varchar(50),
    Location varchar(50),
    Type varchar(50),
    Start DATETIME,
    End DATETIME,
    Create_Date DATETIME,
    Created_By varchar(50),
    Last_Update TIMESTAMP,
    Last_Updated_By varchar(50),
    Customer_ID int(10),
    User_ID int(10),
    Contact_ID int(10),
    FOREIGN KEY (Customer_ID) REFERENCES customers(Customer_ID),
    FOREIGN KEY (User_ID) REFERENCES users(User_ID),
    FOREIGN KEY (Contact_ID) REFERENCES contacts(Contact_ID)
);