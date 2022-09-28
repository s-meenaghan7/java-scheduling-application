# Appointment Scheduling System
Timekeeping/appointment scheduling system built for a fictitious consulting company, written in Java.

*updated to a Maven project in September 2022*

---
### Summary
This Java application is used to keep track of customer information and their appointments for a consulting company specializing in two separate service types: project management and financial consulting. The application connects to a MySQL database to retrieve relevant data regarding customer and appointment information. User login data (credentials) is also held in the database. Data access objects (DAO) are responsible for performing CRUD operations on the database tables to create, retrieve, update, and delete information used in the application.

---

### About

This project served as my capstone project for graduation from Western Governors University Bachelor of Science in Software Development degree program. The project had many requirements for completion such as connecting to a remote database to perform CRUD operations, password protection, using object-oriented programming principles such as inheritance and encapsulation, and many, many more (unfortunately, I no longer have access to the full list of requirements).

#### Remote Database & JSch
For remote database access, I used an AWS RDS instance. However, this database instance is no longer active; I took down the database after graduation, mainly due to financial reasons (keeping that RDS instance running isn't free, ya know!). Connecting to that database also required me to run an EC2 instance on the same VPC (Virtual Private Cloud) as a bastion host, or "jump server". The RDS database was a private database, so I could not simply connect to it directly with the username and password for the database. By running an EC2 instance on the VPC, I could use Jsch (Java Secure Channel) in my application, allowing the program to connect to the EC2 instance with SSH. At that point, my program could use JDBC to connect to the RDS database on the same VPC as the EC2. Although it is not currently being utilized, the JSChConnection class found in the utils package contains the code I wrote and used to connect to that EC2 server over the SSH tunnel.

For more information and more in-depth explanation of the above, you can read about it on AWS's website here: <a href="https://aws.amazon.com/premiumsupport/knowledge-center/rds-connect-ec2-bastion-host/">How can I connect to a private Amazon RDS DB instance from a local machine using an Amazon EC2 instance as a bastion host?</a>

---
