# this script initializes the users table with 2 users

INSERT INTO users (User_Name, Password, Create_Date, Created_By, Last_Update, Last_Updated_By)
VALUES ("admin", "fb001dfcffd1c899f3297871406242f097aecf1a5342ccf3ebcd116146188e4b", now(), "script", now(), "script");

INSERT INTO users (User_Name, Password, Create_Date, Created_By, Last_Update, Last_Updated_By)
VALUES ("test", "36f028580bb02cc8272a9a020f4200e346e276ae664e45ee80745574e2f5ab80", now(), "script", now(), "script");