# dbms_airlineBookingSystem

Welome to Airline Booking System
						

Database used here is Oracle 12c .
 * To install it, go to http://www.oracle.com/technetwork/database/enterprise-edition/downloads/index.html
 * The project is not backward compatible, hence please use oracle 12c only
<br>
JavaFx is used for front-end development. It can be run on Netbeans.

Steps to run the project :- 
 * Download and install Oracle 12c.
 * login on it as sysdba
   For windows machine, follow the following steps :-
   * Open command prompt, type sqlplus Sys as sysdba, and enter the password you typed at the time of installation
   * Type : create user c##airlinedb identified by HOLY2pass;
            grant connect to c##airlinedb;
            grant all privileges to c##airlinedb;
            connect c##airlinedb/HOLY2pass;
 * Now, run the script provided , airlinedbms.sql, or copy paste entire file to sqlplus command prompt.
 * Back end is configured, now lets configure front end.
 * go to AirlineBookingSystem/src/test.java and navigate to line 44 
   *  con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", USER, PASS);  
   * Here, enter your port. 1521 is default, to find your port, go to command prompt type lsnrctl status. Search for "Connecting to (DESCRIPTION=....... PORT =1521 ) . here your actual port will be displayed in place of 1521.
   * Enter your SID, default is orcl, to find yours, login to sqlplus as sysdba, by following steps above, and query :- select instance from v$thread; 
     Enter SID accordingly.
 * Now, run the project from netbeans.
 * For officials , enter following credentials :-
   * username = op_sushil01
   * password = sushilkr
 * For common people, signup and create a new account.
 * Note :- Flight between small cities is not on daily basis, and international flights can only be found from Mumbai or Delhi
 * NOTE :- Description of all the tables can be found in airlinedbms.sql file. Comments are present in that file, to explain everything that's  happening at backend.

<br>
Thank You
