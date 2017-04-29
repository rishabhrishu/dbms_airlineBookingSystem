
#definition of all tables


-- stores login credentials of common users
create table login_record(
	username varchar2(50) PRIMARY KEY NOT NULL,
	password varchar2(255) NOT NULL
);

-- stores personal details of all the users
create table user_details(
	aadhar varchar2(16) PRIMARY KEY NOT NULL,
	username varchar2(50) NOT NULL,
	first_name varchar2(50) NOT NULL,
	last_name varchar2(50),
	dob DATE,
	mobile number(12),
	email varchar2(50),
	address varchar2(255)
);

-- username should be present in user_login_record
ALTER TABLE user_details
ADD CONSTRAINT fk_username1
   FOREIGN KEY (username)
   REFERENCES login_record (username);

-- stores login credentials foe employees
create table emp_login_record(
	emp_id varchar2(50) PRIMARY KEY NOT NULL,
	password varchar2(255) NOT NULL
);

-- stores details of employees
create table emp_detail(
	emp_id varchar2(50) PRIMARY KEY NOT NULL,
	first_name varchar2(50),
	last_name varchar2(50),
	salary number(8,2),
	mobile number(12),
	email varchar2(20)
);
-- emp_id should be in employee login record
ALTER TABLE emp_detail
	ADD CONSTRAINT fk_emp_id1
	FOREIGN KEY (emp_id)
	REFERENCES emp_login_record(emp_id);

--stores details of all the aeroplanes that we have
create table aeroplane_detail(
	plane_id varchar2(10) primary key not null,
	max_seats number (3) not null,
	name_of_airline varchar2(20) not null,
	price_multiplier number(7,3) not null
);

-- stores weekly schedule of all the flights
create table flight_schedule(
	flight_id varchar2(10) primary key not null,
	day number,
	arrival varchar2(10) not null,
	departure varchar2(10) not null,
	SRC varchar2(5),
	DEST varchar2(5)
);

-- used to store mapping of a flight_id to a plane_id
create table flight_map(
	flight_id varchar2(10) unique not null,
	plane_id varchar2(10) not null
); 

-- plane id should be present in transactions
ALTER TABLE flight_map
ADD CONSTRAINT fk_planeid
   FOREIGN KEY (plane_id)
   REFERENCES aeroplane_detail (plane_id);
-- flight id should be present in flight_schedule
ALTER TABLE flight_map
ADD CONSTRAINT fk_flightid
   FOREIGN KEY (flight_id)
   REFERENCES flight_schedule (flight_id);

-- used to store all the ticket transactions happening through our app
-- contains records of tickets of last 90 days of date_of_journey 
create table transactions(
   pnr number GENERATED ALWAYS AS IDENTITY(START WITH 12222230),
    booking_date TIMESTAMP,
	username varchar2(50),
	flight_id varchar2(10),
	fare number(10,3),
	no_of_seats_booked number(3),
	reservation_status varchar2(15) check (reservation_status IN ('CANCELLED','BOOKED')),
	date_of_journey DATE not null
);
-- username should be present in login record
ALTER TABLE transactions 
	ADD CONSTRAINT fk_username2
	FOREIGN KEY (username)
	REFERENCES login_record(username);
-- flight_id should be present in flight_schedule
ALTER TABLE transactions
	ADD CONSTRAINT fk_flightid2
	FOREIGN KEY (flight_id)
	REFERENCES flight_schedule(flight_id);

--list of all the cities supported by our app
create table cities(
	city_code varchar2(5) PRIMARY KEY NOT NULL,
	city_name varchar2(15) NOT NULL
);
-- stores distance between two cities
create table distances (
	SRC varchar2(5) NOT NULL,
	DEST varchar2(5) NOT NULL,
	distance NUMBER(7,2) NOT NULL
);
-- any source code should be from cities table
ALTER TABLE distances
	ADD CONSTRAINT fk_source
	FOREIGN KEY (SRC)
	REFERENCES cities(city_code);
-- any destination code should be from cities table
ALTER TABLE distances 
	ADD CONSTRAINT fk_dest
	FOREIGN KEY (DEST) 
	REFERENCES cities (city_code);

-- temporary table to hold the result of a join 
create table tempTable1 (
	airlines varchar2(20),
	flight_pkey varchar2(10),
	departure varchar2(10),
	arrival varchar2(10),
	fare integer
);

--any flight that becomes late, or dealyed, gets stored in this table, cleared on a weekly basis
create table current_runnning_status( 
	flight_id varchar2(10),
	dt DATE,
	status varchar2(15) check (status IN('DELAYED','CANCELLED','ON TIME')),
	delayduration number(4));

--this table stores the details of persons booked on a pnr
create table details_of_person_booked(
	pnr number primary key,
	name_passenger1 varchar2(50) not null,
	age_passenger1 number(2) not null,
	sex_passenger1 varchar(1) not null,
	name_passenger2 varchar2(50) null,
	age_passenger2 number(2) ,
	sex_passenger2 varchar(1) ,
	name_passenger3 varchar2(50) null,
	age_passenger3 number(2) ,
	sex_passenger3 varchar(1) ,
	name_passenger4 varchar2(50) null,
	age_passenger4 number(2) ,
	sex_passenger4 varchar(1) ,
	name_passenger5 varchar2(50) null,
	age_passenger5 number(2) ,
	sex_passenger5 varchar(1) 
);

commit;

--helper procedure used to create a new user row in  corresponding tables
create or replace procedure insert_user
	(
		adh IN varchar2,
		usn IN varchar2,
		fn IN varchar2,
		ln IN varchar2,
		db IN DATE,
		mobl IN number,
		eml IN varchar2,
		add IN varchar2,
		pwd IN varchar2
	)
	AS 
	BEGIN
		INSERT INTO login_record values (usn,pwd);
		COMMIT;
		INSERT INTO user_details values(adh,usn,fn,ln,db,mobl,eml,add);
		COMMIT;
	END insert_user;
	/

--this procedure is used to find remaining seats in a given flight on a particular day
create or replace procedure find_seats(
	flightid IN varchar2,
	dt IN DATE,
	left OUT number
) IS
	booked number;
	maxm number;
BEGIN
	left := 0;
	booked := 0;
	maxm := 0;
	select NVL(sum(transactions.no_of_seats_booked),0) into booked from transactions where transactions.flight_id=flightid
	and transactions.date_of_journey=dt and reservation_status='BOOKED';
	select max_seats into maxm from aeroplane_detail where aeroplane_detail.plane_id = (select flight_map.plane_id 
	from flight_map where flight_map.flight_id=flightid);
	left := maxm - booked;
END;




/* this procedure returns a cursor to all the tickets a particular user can cancel
 * ensures that user cant cancel tickets once he has already travelled
 */
create or replace procedure toCancel(
	usr in varchar2,
	curr out sys_refcursor 
) is
begin
	open curr for select pnr,date_of_journey,no_of_seats_booked,flight_id from transactions where date_of_journey > sysdate-1 and username = usr and reservation_status='BOOKED';
end;
/

--this procedure calculates a dynamic fare of your journey
create or replace procedure calc_fare(
	flightid in varchar2,
	dt in DATE,
	srce in varchar2,
	destn in varchar2,
	fare OUT integer
) IS
	var1 number(7,3);
	lft number;
	dst number;
BEGIN
	find_seats(flightid,dt,lft);
	select distance into dst from distances where ( (src=srce AND dest=destn) OR (src=destn AND dest=srce) );
	select price_multiplier into var1 from aeroplane_detail where plane_id=(select plane_id from flight_map where flight_id=flightid);
	fare := 2500 + var1*750 + 1.7 * dst + 15000 / (4+lft);

	
END;
/

--this procedure returns a cursor to list of all the available flights, given source, destination, day and no. of seats
create or replace procedure get_all_flights(
	srce IN varchar2,
	dstn IN varchar2,
	dt in DATE,
	dayday in number,
	noFseat in number,
	ret_cur OUT sys_refcursor
) IS
	remainSeat number;
	flightid varchar2(10);
	arline varchar2(20);
	fre integer;

BEGIN
	execute immediate 'truncate table tempTable1';
		FOR flightrow IN (select * from flight_schedule where src=srce and dest=dstn and day=dayday)
		LOOP
			find_seats(flightrow.flight_id,dt,remainSeat);
			IF remainSeat >= noFseat then
				select name_of_airline into arline from aeroplane_detail where plane_id=(select plane_id from flight_map where flight_id=flightrow.flight_id);
				calc_fare(flightrow.flight_id,dt,srce,dstn,fre);
				insert into tempTable1 values(arline,flightrow.flight_id,flightrow.departure,flightrow.arrival,fre);
			end if;
		end loop;
	open ret_cur for select * from tempTable1;
END;
/

--this function checks if a flight is on time or not
create or replace function check_flight_status(
	flightid in varchar2,
	dat in date
) return varchar2
is
	stat varchar2(15);
begin
	select status into stat from current_runnning_status where flight_id=flightid and dt=dat;
	return stat;
exception
when no_data_found then return 'ON TIME';
end;
/
/*
 * this procedure is used to delay the flight, given flightId,date and delay duration in hhmm format
 * note-delay duration is always given wrt scheduled time
 */
create or replace procedure delayby(
	flightid in varchar2,
	dat in date,
	del in number
) is
	cnt number;
begin
	select count(*) into cnt from current_runnning_status where flight_id = flightid;
	if cnt = 0
	then
		insert into current_runnning_status values(flightid,dat,'DELAYED',del);
	else
		update current_runnning_status set delayduration = del where flight_id=flightid;
	end if;
end;
/

--this procedure is used by operator to cancel the flight, given flight_id and day
create or replace procedure cancel_flight(
	flightid in varchar2,
	dat in date
) is
	cnt number;
begin
select count(*) into cnt from current_runnning_status where flight_id = flightid;
	if cnt = 0
	then
	 insert into current_runnning_status values(flightid,dat,'CANCELLED',0);
	else 
		update current_runnning_status set status='CANCELLED' where flight_id=flightid;
	end if;
end;
/

/*
 * this is the logic how ticket is booked. This ensures that one seat is given to only one person
 * similar to how tatkal reservation system works, suppose if there is only one seat remaining, then only one user gets it, even when many persons are trying
 * implements locking and transaction management of oracle 12c
 */
create or replace procedure ticketbookingPNR (
	fid IN varchar2,
	dat IN DATE,
	reqSeats IN number,
	usr IN varchar2,
	faretaken IN number,
	ppnnr OUT number
) 
is
	lft number;
	seats_not_available EXCEPTION;

BEGIN
	commit;
	set transaction read write name 'book_ticket'; 
	savepoint before_lock;
	lock table transactions in exclusive mode;
	find_seats(fid,dat,lft);
	if lft < reqSeats then
		RAISE seats_not_available;		
	else 
		insert into transactions(booking_date,username,flight_id,fare,no_of_seats_booked,reservation_status,date_of_journey) values 
			(CURRENT_TIMESTAMP,usr,fid,faretaken,reqSeats,'BOOKED',dat);
		commit;

	end if;
	select pnr into ppnnr from transactions where username = usr order by booking_date desc fetch first 1 rows only;
	commit;
EXCEPTION 
	when seats_not_available then
	dbms_output.put_line('SEATS NOT LEFT');
		rollback;
		
end;
/	



--this procedure tells the user status of their flight, for pnr number
create or replace procedure check_pnr_status(
	ppnnr in number,
	stat out varchar2,
	delaay out varchar2
)is
	fid varchar2(10);
	dat date;
	del number;
	gotStatus varchar2(15);
begin 
	delaay := 0;
	select flight_id,date_of_journey into fid,dat from transactions where pnr = ppnnr;
	stat := check_flight_status(fid,dat);
	if stat = 'DELAYED'
	then
		select delayduration into del from current_runnning_status where flight_id = fid and dt = dat;
		delaay := to_char(del,'0000');
	end if;
end;
/

--this procedure cancels the ticket, given pnr and returns the fare 
create or replace procedure cancelticket(
	ppnnr in varchar2,
	returnedFare out number
)is
	dt DATE;
	f number(8,2);
begin
	select fare into f from transactions where pnr=ppnnr;
	select date_of_journey into dt from transactions where pnr=ppnnr;
	update transactions set reservation_status = 'CANCELLED' where pnr = ppnnr;
	if dt > sysdate + 3 
	then
		returnedFare := f * 0.7 ;
	else 
		returnedFare := f * 0.4;
	end if;
end;
/

--this trigger deletes all the entries from transactions table, whose date of journey was 90 days before current date
create or replace trigger delete_transactions
	after
		insert 
	on transactions
begin
	delete from transactions where date_of_journey  < (select sysdate-90 from dual);
end;
/

--if a flight is cancelled, this trigger cancels all the tickets booked for that flight
create or replace trigger cancelTktWhenFightCancel
	after insert or update on current_runnning_status
	for each row 
	declare 
	fare_ret number;
	begin
		for row in (select transactions.pnr from transactions where transactions.flight_id = :NEW.FLIGHT_ID)
		loop 
			cancelticket(row.pnr,fare_ret);
		end loop;
	end;
/


-- dba inserts details of an employee
INSERT INTO emp_login_record values('op_sushil01','sushilkr');
INSERT INTO emp_detail values ('op_sushil01','Sushil','Kumar',10000,9431256065,'sushil_kr@airindia.com');



--table of all the cities currently supported by our application
INSERT INTO cities VALUES ('DEL','Delhi');
INSERT INTO cities VALUES ('BOM','Mumbai');
INSERT INTO cities VALUES ('JAI','Jaipur');
INSERT INTO cities VALUES ('KOL','Kolkata');
INSERT INTO cities VALUES ('BLR','Bangalore');
INSERT INTO cities VALUES ('AMD','Ahmedabad');
INSERT INTO cities VALUES ('DXB','Dubai');
INSERT INTO cities VALUES ('MAA','Chennai');
INSERT INTO cities VALUES ('PNQ','Pune');
INSERT INTO cities VALUES ('HYD','Hyderabad');

--distances between all the cities
insert into distances values('DEL','BOM',1148);
insert into distances values('DEL','KOL',1307);
insert into distances values('DEL','BLR',1740);
insert into distances values('DEL','MAA',1760);
insert into distances values('DEL','HYD',1253);
insert into distances values('DEL','JAI',241);
insert into distances values('DEL','AMD',775);
insert into distances values('DEL','DXB',2203);
insert into distances values('DEL','PNQ',1173);
insert into distances values('BOM','KOL',1639);
insert into distances values('BOM','BLR',842);
insert into distances values('BOM','MAA',1028);
insert into distances values('BOM','HYD',617);
insert into distances values('BOM','JAI',914);
insert into distances values('BOM','AMD',441);
insert into distances values('BOM','Dubai',1941);
insert into distances values('BOM','PNQ',118); 
insert into distances values('JAI','BLR',1556);
insert into distances values('JAI','MAA',1606);
insert into distances values('JAI','HYD',1088);
insert into distances values('JAI','KOL',1359);
insert into distances values('JAI','AMD',534);
insert into distances values('JAI','DXB',2058);
insert into distances values('JAI','PNQ',948);
insert into distances values('BLR','MAA',284);
insert into distances values('BLR','HYD',587);
insert into distances values('BLR','AMD',1235);
insert into distances values('BLR','DXB',2694);
insert into distances values('BLR','PNQ',734);
insert into distances values('BLR','KOL',1560);
insert into distances values('MAA','HYD',520);
insert into distances values('MAA','AMD',1371);
insert into distances values('MAA','DXB',2945);
insert into distances values('MAA','PNQ',912);
insert into distances values('MAA','KOL',1366)
insert into distances values('KOL','HYD',1180);
insert into distances values('KOL','DXB',3380);
insert into distances values('KOL','AMD',1806);
insert into distances values('KOL','PNQ',1575);
insert into distances values('HYD','AMD','876');
insert into distances values('HYD','DXB','2556');
insert into distances values('HYD','PNQ','504');
insert into distances values('AMD','DXB',1782);
insert into distances values('AMD','PNQ',518);
insert into distances values('DXB','PNQ',2035);


#list of all the actual aeroplanes that we have, and their description
insert into aeroplane_detail values ('pid001',100 ,'JetBlue',0.145);
insert into aeroplane_detail values ('pid002',110,'SpiceJet',0.751);
insert into aeroplane_detail values ('pid003',130 ,'JetBlue',0.485);
insert into aeroplane_detail values ('pid004',105 ,'Indigo',0.584);
insert into aeroplane_detail values ('pid005',80 ,'GoAir',1.098);
insert into aeroplane_detail values ('pid006',95 ,'AirIndia',1.374);
insert into aeroplane_detail values ('pid007',110 ,'Vistara',0.589);
insert into aeroplane_detail values ('pid008',110,'SpiceJet',0.478);
insert into aeroplane_detail values ('pid009',130 ,'GoAir',1.324);
insert into aeroplane_detail values ('pid010',120 ,'Vistara',1.846);
insert into aeroplane_detail values ('pid011',110 ,'FlyEmirates',1.378);
insert into aeroplane_detail values ('pid012',100 ,'AirAsia',0.619);
insert into aeroplane_detail values ('pid013',120 ,'Indigo',1.174);
insert into aeroplane_detail values ('pid014',130 ,'AirIndia',0.321);
insert into aeroplane_detail values ('pid015',100 ,'JetBlue',0.417);
insert into aeroplane_detail values ('pid016',100 ,'GoAir',1.145);
insert into aeroplane_detail values ('pid017',150 ,'Vistara',0.31);
insert into aeroplane_detail values ('pid018',140 ,'SpiceJet',0.321);
insert into aeroplane_detail values ('pid019',140 ,'Indigo',0.379);
insert into aeroplane_detail values ('pid020',30 ,'JetBlue',0.86);
insert into aeroplane_detail values ('pid021',80 ,'GoAir',0.86);
insert into aeroplane_detail values ('pid022',95 ,'AirIndia',1.36);
insert into aeroplane_detail values ('pid023',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid024',110,'SpiceJet',0.23);
insert into aeroplane_detail values ('pid025',130 ,'GoAir',0.16);
insert into aeroplane_detail values ('pid026',120 ,'Vistara',1.39);
insert into aeroplane_detail values ('pid027',110 ,'FlyEmirates',1.66);
insert into aeroplane_detail values ('pid028',100 ,'AirAsia',1.56);
insert into aeroplane_detail values ('pid029',120 ,'Indigo',0.63);
insert into aeroplane_detail values ('pid030',130 ,'AirIndia',0.81);
insert into aeroplane_detail values ('pid041',80 ,'GoAir',0.86);
insert into aeroplane_detail values ('pid042',95 ,'AirIndia',1.36);
insert into aeroplane_detail values ('pid043',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid044',110,'SpiceJet',0.23);
insert into aeroplane_detail values ('pid045',130 ,'GoAir',0.16);
insert into aeroplane_detail values ('pid046',120 ,'Vistara',1.39);
insert into aeroplane_detail values ('pid047',110 ,'FlyEmirates',1.66);
insert into aeroplane_detail values ('pid048',100 ,'AirAsia',1.56);
insert into aeroplane_detail values ('pid049',120 ,'Indigo',0.63);
insert into aeroplane_detail values ('pid050',130 ,'AirIndia',0.81);
insert into aeroplane_detail values ('pid054',110,'SpiceJet',0.23);
insert into aeroplane_detail values ('pid055',130 ,'GoAir',0.16);
insert into aeroplane_detail values ('pid056',120 ,'Vistara',1.39);
insert into aeroplane_detail values ('pid057',110 ,'FlyEmirates',1.66);
insert into aeroplane_detail values ('pid058',100 ,'AirAsia',1.56);
insert into aeroplane_detail values ('pid059',120 ,'Indigo',0.63);
insert into aeroplane_detail values ('pid060',130 ,'AirIndia',0.81);
insert into aeroplane_detail values ('pid111',80 ,'GoAir',0.86);
insert into aeroplane_detail values ('pid112',95 ,'AirIndia',1.36);
insert into aeroplane_detail values ('pid113',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid114',110,'SpiceJet',0.23);
insert into aeroplane_detail values ('pid115',130 ,'GoAir',0.16);
insert into aeroplane_detail values ('pid116',120 ,'Vistara',1.39);
insert into aeroplane_detail values ('pid117',110 ,'FlyEmirates',1.66);
insert into aeroplane_detail values ('pid031',80 ,'GoAir',0.86);
insert into aeroplane_detail values ('pid032',95 ,'AirIndia',1.36);
insert into aeroplane_detail values ('pid033',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid034',110,'SpiceJet',0.23);
insert into aeroplane_detail values ('pid035',130 ,'GoAir',0.16);
insert into aeroplane_detail values ('pid036',120 ,'Vistara',1.39);
insert into aeroplane_detail values  ('pid037',110 ,'FlyEmirates',1.66);
insert into aeroplane_detail values ('pid038',100 ,'AirAsia',1.56);
insert into aeroplane_detail values ('pid039',120 ,'Indigo',0.63);
insert into aeroplane_detail values ('pid040',130 ,'AirIndia',0.81);
insert into aeroplane_detail values ('pid061',80 ,'GoAir',0.86);
insert into aeroplane_detail values ('pid062',95 ,'AirIndia',1.36);
insert into aeroplane_detail values ('pid063',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid064',110,'SpiceJet',0.23);
insert into aeroplane_detail values ('pid065',130 ,'GoAir',0.16);
insert into aeroplane_detail values ('pid066',120 ,'Vistara',1.39);
insert into aeroplane_detail values ('pid067',110 ,'FlyEmirates',1.66);
insert into aeroplane_detail values ('pid068',100 ,'AirAsia',1.56);
insert into aeroplane_detail values ('pid069',120 ,'Indigo',0.63);
insert into aeroplane_detail values ('pid070',130 ,'AirIndia',0.81);
insert into aeroplane_detail values ('pid071',90 ,'GoAir',0.70);
insert into aeroplane_detail values ('pid072',70 ,'AirIndia',1.6);
insert into aeroplane_detail values ('pid073',130 ,'JetBlue',0.23);
insert into aeroplane_detail values ('pid074',105 ,'Indigo',0.40);
insert into aeroplane_detail values ('pid075',80 ,'GoAir',0.56);
insert into aeroplane_detail values ('pid076',95 ,'AirIndia',1.26);
insert into aeroplane_detail values ('pid077',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid078',110,'SpiceJet',0.43);
insert into aeroplane_detail values ('pid079',130 ,'GoAir',0.460);
insert into aeroplane_detail values ('pid080',120 ,'Vistara',1.49);
insert into aeroplane_detail values ('pid081',80 ,'FlyEmirates',1.76);
insert into aeroplane_detail values ('pid082',100 ,'AirAsia',1.56);
insert into aeroplane_detail values ('pid083',120 ,'Indigo',0.453);
insert into aeroplane_detail values ('pid084',130 ,'AirIndia',1.11);
insert into aeroplane_detail values ('pid085',100 ,'JetBlue',0.56);
insert into aeroplane_detail values ('pid086',100 ,'GoAir',0.67);
insert into aeroplane_detail values ('pid087',150 ,'Vistara',1.315);
insert into aeroplane_detail values ('pid088',140 ,'SpiceJet',6.66);
insert into aeroplane_detail values ('pid089',140 ,'Indigo',0.496);
insert into aeroplane_detail values ('pid090',130 ,'JetBlue',0.36);
insert into aeroplane_detail values ('pid091',90 ,'GoAir',0.70);
insert into aeroplane_detail values ('pid092',70 ,'AirIndia',1.6);
insert into aeroplane_detail values ('pid093',130 ,'JetBlue',0.323);
insert into aeroplane_detail values ('pid094',105 ,'Indigo',0.430);
insert into aeroplane_detail values ('pid095',80 ,'GoAir',0.56);
insert into aeroplane_detail values ('pid096',95 ,'AirIndia',1.346);
insert into aeroplane_detail values ('pid097',110 ,'Vistara',1.456);
insert into aeroplane_detail values ('pid098',110,'SpiceJet',0.423);
insert into aeroplane_detail values ('pid099',130 ,'GoAir',0.400);
insert into aeroplane_detail values ('pid100',120 ,'Vistara',1.39);
insert into aeroplane_detail values ('pid101',80 ,'GoAir',0.86);
insert into aeroplane_detail values ('pid102',95 ,'AirIndia',1.36);
insert into aeroplane_detail values ('pid103',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid104',110,'SpiceJet',0.23);
insert into aeroplane_detail values ('pid105',130 ,'GoAir',0.16);
insert into aeroplane_detail values ('pid106',120 ,'Vistara',1.39);
insert into aeroplane_detail values  ('pid107',110 ,'FlyEmirates',1.66);
insert into aeroplane_detail values ('pid108',100 ,'AirAsia',1.56);
insert into aeroplane_detail values ('pid109',120 ,'Indigo',0.63);
insert into aeroplane_detail values ('pid110',130 ,'AirIndia',0.81);
insert into aeroplane_detail values ('pid051',110 ,'Vistara',1.56);
insert into aeroplane_detail values ('pid052',110,'SpiceJet',0.43);

/*
 * this table contains all the flights in a week that fly between two cities. 
 * note that this table does not contain actual aeroplanes that are going to fly,
 * it just contains tentative flights. These flights will be assigned to actual planes later
 */
insert into flight_schedule values ('6E-901',1,'0700','0500','BOM','DEL');
insert into flight_schedule values ('6E-902',1,'0845','0600','BOM','KOL');
insert into flight_schedule values ('6E-903',1,'1200','1000','DEL','BOM');
insert into flight_schedule values ('6E-904',1,'1510','1300','KOL','DEL');
insert into flight_schedule values ('6E-905',1,'1050','0800','MAA','DEL');
insert into flight_schedule values ('6E-906',1,'0950','0900','PNQ','BOM');
insert into flight_schedule values ('6E-907',1,'1300','1100','HYD','DEL');
insert into flight_schedule values ('6E-908',1,'1510','1400','AMD','BOM');
insert into flight_schedule values ('6E-909',1,'1300','1200','JAI','DEL');
insert into flight_schedule values ('6E-910',1,'1415','1230','BLR','BOM');    
insert into flight_schedule values ('6E-911',1,'0805','0730','MAA','BLR');
insert into flight_schedule values ('6E-912',1,'1155','1045','HYD','BLR');  
insert into flight_schedule values ('6E-913',1,'1605','1400','DEL','PNQ');
insert into flight_schedule values ('6E-914',1,'1540','1300','BLR','DEL'); 
insert into flight_schedule values ('6E-915',1,'1725','1600','HYD','BOM');
insert into flight_schedule values ('6F-015',1,'1845','1500','BOM','BLR');
insert into flight_schedule values ('6F-016',1,'2300','2100','BOM','DEL');
insert into flight_schedule values ('6F-017',1,'0945','0700','BOM','KOL');
insert into flight_schedule values ('6F-018',1,'0850','0700','BOM','MAA');
insert into flight_schedule values ('6F-019',1,'1900','1300','KOL','BLR');
insert into flight_schedule values ('6F-020',1,'1745','1500','KOL','BOM');
insert into flight_schedule values ('6F-021',1,'1000','0500','KOL','MAA');	
insert into flight_schedule values ('6F-022',1,'0835','0700','MAA','BLR');
insert into flight_schedule values ('6F-023',1,'1650','1500','MAA','BOM');
insert into flight_schedule values ('6F-024',1,'1050','0800','MAA','DEL');
insert into flight_schedule values ('6F-025',1,'1540','1300','DEL','BLR');
insert into flight_schedule values ('6F-026',1,'1510','1300','DEL','KOL');
insert into flight_schedule values ('6F-027',1,'0700','0500','DEL','BOM');	
insert into flight_schedule values ('6F-028',1,'1550','0900','BLR','DXB');
insert into flight_schedule values ('6F-029',1,'1340','1100','BLR','DEL');
insert into flight_schedule values ('6F-030',1,'1545','1400','BLR','BOM');
insert into flight_schedule values ('6F-031',1,'1535','1400','BLR','MAA');
insert into flight_schedule values ('6F-032',1,'1650','1400','DEL','MAA');
insert into flight_schedule values ('6F-033',1,'1610','1400','KOL','DEL');
insert into flight_schedule values ('6E-916',2,'1100','0900','DEL','HYD');
insert into flight_schedule values ('6E-917',2,'1500','1400','DEL','JAI');
insert into flight_schedule values ('6E-918',2,'0930','0600','DEL','DXB');
insert into flight_schedule values ('6E-919',2,'0825','0700','BOM','HYD');
insert into flight_schedule values ('6E-920',2,'0800','1900','BOM','DXB');
insert into flight_schedule values ('6E-921',2,'0720','0600','PNQ','AMD');
insert into flight_schedule values ('6E-922',2,'1505','1300','KOL','HYD');
insert into flight_schedule values ('6E-923',2,'2015','1900','HYD','MAA');
insert into flight_schedule values ('6E-924',2,'0800','0600','DEL','HYD');
insert into flight_schedule values ('6E-925',2,'1630','1400','KOL','PNQ');
insert into flight_schedule values ('6E-926',2,'1055','0900','JAI','HYD');
insert into flight_schedule values ('6E-927',2,'1410','1300','BLR','HYD');   
insert into flight_schedule values ('6E-928',2,'1925','1700','MAA','KOL');
insert into flight_schedule values ('6E-929',2,'2145','2000','AMD','HYD');
insert into flight_schedule values ('6E-930',2,'1240','1000','DEL','BLR');      
insert into flight_schedule values ('6F-034',2,'1845','1500','BOM','BLR');
insert into flight_schedule values ('6F-035',2,'2300','2100','BOM','DEL');
insert into flight_schedule values ('6F-036',2,'0945','0700','BOM','KOL');
insert into flight_schedule values ('6F-037',2,'0850','0700','BOM','MAA');
insert into flight_schedule values ('6F-038',2,'1900','1300','KOL','BLR');
insert into flight_schedule values ('6F-039',2,'1745','1500','KOL','BOM');
insert into flight_schedule values ('6F-040',2,'1000','0500','KOL','MAA');	
insert into flight_schedule values ('6F-041',2,'0835','0700','MAA','BLR');
insert into flight_schedule values ('6F-042',2,'1650','1500','MAA','BOM');
insert into flight_schedule values ('6F-043',2,'1050','0800','MAA','DEL');
insert into flight_schedule values ('6F-044',2,'1540','1300','DEL','BLR');
insert into flight_schedule values ('6F-045',2,'1510','1300','DEL','KOL');
insert into flight_schedule values ('6F-046',2,'0700','0500','DEL','BOM');	
insert into flight_schedule values ('6F-047',2,'1550','0900','BLR','DXB');
insert into flight_schedule values ('6F-048',2,'1340','1100','BLR','DEL');
insert into flight_schedule values ('6F-049',2,'1545','1400','BLR','BOM');
insert into flight_schedule values ('6F-050',2,'1535','1400','BLR','MAA');
insert into flight_schedule values ('6F-051',2,'1650','1400','DEL','MAA');
insert into flight_schedule values ('6F-052',2,'1610','1400','KOL','DEL');
insert into flight_schedule values ('6E-931',3,'2130','1800','DXB','DEL');
insert into flight_schedule values ('6E-932',3,'2300','2000','DXB','BOM');
insert into flight_schedule values ('6E-933',3,'1035','0930','MAA','BLR');
insert into flight_schedule values ('6E-934',3,'1605','1400','BLR','AMD');
insert into flight_schedule values ('6E-935',3,'1225','1100','PNQ','BLR'); 
insert into flight_schedule values ('6E-936',3,'1005','0800','DEL','PNQ'); 
insert into flight_schedule values ('6E-937',3,'0710','0600','JAI','AMD');
insert into flight_schedule values ('6E-938',3,'1045','0900','BOM','JAI');  
insert into flight_schedule values ('6E-939',3,'1635','1430','HYD','KOL');
insert into flight_schedule values ('6E-940',3,'1745','1500','BOM','KOL');   
insert into flight_schedule values ('6E-941',3,'0945','0700','KOL','BOM');  
insert into flight_schedule values ('6E-942',3,'2135','1900','KOL','AMD'); 
insert into flight_schedule values ('6E-943',3,'1525','1300','BLR','JAI');   
insert into flight_schedule values ('6E-944',3,'0800','0600','DEL','BOM');
insert into flight_schedule values ('6E-945',3,'1230','1130','JAI','DEL');  
insert into flight_schedule values ('6F-053',3,'1845','1500','BOM','BLR');
insert into flight_schedule values ('6F-054',3,'2300','2100','BOM','DEL');
insert into flight_schedule values ('6F-055',3,'0945','0700','BOM','KOL');
insert into flight_schedule values ('6F-056',3,'0850','0700','BOM','MAA');
insert into flight_schedule values ('6F-057',3,'1900','1300','KOL','BLR');
insert into flight_schedule values ('6F-058',3,'1745','1500','KOL','BOM');
insert into flight_schedule values ('6F-059',3,'1000','0500','KOL','MAA');	
insert into flight_schedule values ('6F-300',3,'0835','0700','MAA','BLR');
insert into flight_schedule values ('6F-301',3,'1650','1500','MAA','BOM');
insert into flight_schedule values ('6F-302',3,'1050','0800','MAA','DEL');
insert into flight_schedule values ('6F-303',3,'1540','1300','DEL','BLR');
insert into flight_schedule values ('6F-304',3,'1510','1300','DEL','KOL');
insert into flight_schedule values ('6F-305',3,'0700','0500','DEL','BOM');	
insert into flight_schedule values ('6F-306',3,'1550','0900','BLR','DXB');
insert into flight_schedule values ('6F-307',3,'1340','1100','BLR','DEL');
insert into flight_schedule values ('6F-308',3,'1545','1400','BLR','BOM');
insert into flight_schedule values ('6F-309',3,'1535','1400','BLR','MAA');
insert into flight_schedule values ('6F-310',3,'1650','1400','DEL','MAA');
insert into flight_schedule values ('6F-311',3,'1610','1400','KOL','DEL');
insert into flight_schedule values ('6E-946',4,'0700','0500','BOM','DEL');
insert into flight_schedule values ('6E-947',4,'0845','0600','BOM','KOL');
insert into flight_schedule values ('6E-948',4,'1200','1000','DEL','BOM');
insert into flight_schedule values ('6E-949',4,'1510','1300','KOL','DEL');
insert into flight_schedule values ('6E-950',4,'1050','0800','MAA','DEL');
insert into flight_schedule values ('6E-951',4,'2145','2000','AMD','HYD'); 
insert into flight_schedule values ('6E-952',4,'1820','1700','PNQ','AMD'); 
insert into flight_schedule values ('6E-953',4,'1300','1100','HYD','DEL');
insert into flight_schedule values ('6E-954',4,'1510','1400','AMD','BOM');
insert into flight_schedule values ('6E-955',4,'1415','1230','BLR','BOM');    
insert into flight_schedule values ('6E-956',4,'0805','0730','MAA','BLR');
insert into flight_schedule values ('6E-957',4,'1155','1045','HYD','BLR');  
insert into flight_schedule values ('6E-958',4,'1605','1400','DEL','PNQ');
insert into flight_schedule values ('6E-959',4,'1540','1300','BLR','DEL'); 
insert into flight_schedule values ('6E-960',4,'1725','1600','HYD','BOM');
insert into flight_schedule values ('6E-961',5,'1145','0900','JAI','MAA');
insert into flight_schedule values ('6E-962',5,'0800','0700','DEL','JAI');
insert into flight_schedule values ('6E-963',5,'1930','1600','DEL','DXB');
insert into flight_schedule values ('6E-964',5,'1945','1900','MAA','JAI');
insert into flight_schedule values ('6E-965',5,'2200','1900','BOM','DXB');
insert into flight_schedule values ('6E-966',5,'0720','0600','PNQ','AMD');
insert into flight_schedule values ('6E-967',5,'1505','1300','KOL','HYD');
insert into flight_schedule values ('6E-968',5,'2015','1900','HYD','MAA');
insert into flight_schedule values ('6E-969',5,'0800','0600','DEL','HYD');
insert into flight_schedule values ('6E-970',5,'1630','1400','KOL','PNQ');
insert into flight_schedule values ('6E-971',5,'1055','0900','JAI','HYD');
insert into flight_schedule values ('6E-972',5,'1410','1300','BLR','HYD');   
insert into flight_schedule values ('6E-973',5,'1925','1700','MAA','KOL');
insert into flight_schedule values ('6E-974',5,'2235','2000','AMD','KOL');
insert into flight_schedule values ('6E-975',5,'1240','1000','DEL','BLR');      
insert into flight_schedule values ('6E-976',6,'0805','0600','HYD','KOL');
insert into flight_schedule values ('6E-977',6,'0845','0600','BOM','KOL');
insert into flight_schedule values ('6E-978',6,'1750','1500','DEL','MAA');   
insert into flight_schedule values ('6E-979',6,'1510','1300','KOL','DEL');
insert into flight_schedule values ('6E-980',6,'1050','0800','MAA','DEL');   
insert into flight_schedule values ('6E-981',6,'0950','0900','PNQ','BOM');
insert into flight_schedule values ('6E-982',6,'1300','1100','HYD','DEL');
insert into flight_schedule values ('6E-983',6,'1510','1400','AMD','BOM');
insert into flight_schedule values ('6E-984',6,'1300','1200','JAI','DEL');
insert into flight_schedule values ('6E-985',6,'1415','1230','BLR','BOM');    
insert into flight_schedule values ('6E-986',6,'0805','0730','MAA','BLR');
insert into flight_schedule values ('6E-987',6,'1155','1045','HYD','BLR');  
insert into flight_schedule values ('6E-988',6,'1605','1400','DEL','PNQ');
insert into flight_schedule values ('6E-989',6,'1540','1300','BLR','DEL'); 
insert into flight_schedule values ('6E-990',6,'1825','1600','JAI','BLR');
insert into flight_schedule values ('6F-001',7,'2030','1700','DXB','DEL');
insert into flight_schedule values ('6F-002',7,'2100','1800','DXB','BOM');
insert into flight_schedule values ('6F-003',7,'1035','0930','BLR','MAA');
insert into flight_schedule values ('6F-004',7,'1605','1400','BLR','AMD');
insert into flight_schedule values ('6F-005',7,'1225','1100','PNQ','BLR'); 
insert into flight_schedule values ('6F-006',7,'1005','0800','DEL','PNQ'); 
insert into flight_schedule values ('6F-007',7,'0710','0600','JAI','AMD');
insert into flight_schedule values ('6F-008',7,'1045','0900','BOM','JAI');  
insert into flight_schedule values ('6F-009',7,'0650','0500','MAA','BOM');
insert into flight_schedule values ('6F-010',7,'1745','1500','BOM','KOL');  
insert into flight_schedule values ('6F-011',7,'0945','0700','KOL','BOM');   
insert into flight_schedule values ('6F-012',7,'2135','1900','KOL','AMD');  
insert into flight_schedule values ('6F-013',7,'1525','1300','BLR','JAI');   
insert into flight_schedule values ('6F-014',7,'0800','0600','DEL','BOM');
insert into flight_schedule values ('6F-015',7,'1230','1130','JAI','DEL');  
insert into flight_schedule values ('6F-060',4,'0700','0500','BOM','DEL');
insert into flight_schedule values ('6F-061',4,'1400','1200','BOM','DEL');
insert into flight_schedule values ('6F-062',4,'0935','0800','BOM','BLR');
insert into flight_schedule values ('6F-063',4,'1505','1330','BOM','BLR');
insert into flight_schedule values ('6F-064',4,'1000','0900','BOM','DEL');
insert into flight_schedule values ('6F-065',4,'1935','1800','BOM','BLR');
insert into flight_schedule values ('6F-066',4,'1045','0800','BOM','KOL');
insert into flight_schedule values ('6F-067',4,'1445','1200','BOM','KOL');
insert into flight_schedule values ('6F-068',4,'2245','2000','BOM','KOL');
insert into flight_schedule values ('6F-069',4,'1250','1100','BOM','MAA');
insert into flight_schedule values ('6F-070',4,'0650','0500','BOM','MAA');
insert into flight_schedule values ('6F-071',4,'1650','1500','BOM','MAA');
insert into flight_schedule values ('6F-072',4,'0800','0600','DEL','BOM');
insert into flight_schedule values ('6F-073',4,'1200','1000','DEL','BOM');
insert into flight_schedule values ('6F-074',4,'2100','1900','DEL','BOM');
insert into flight_schedule values ('6F-075',4,'0940','0700','DEL','BLR');
insert into flight_schedule values ('6F-076',4,'1240','1000','DEL','BLR');
insert into flight_schedule values ('6F-077',4,'2240','2000','DEL','BLR');
insert into flight_schedule values ('6F-078',4,'1010','0800','DEL','KOL');
insert into flight_schedule values ('6F-079',4,'1710','1500','DEL','KOL');
insert into flight_schedule values ('6F-080',4,'1310','1100','DEL','KOL');
insert into flight_schedule values ('6F-081',4,'0850','0600','DEL','MAA');
insert into flight_schedule values ('6F-082',4,'1350','1100','DEL','MAA');
insert into flight_schedule values ('6F-083',4,'1650','1400','DEL','MAA');
insert into flight_schedule values ('6F-084',4,'0810','0530','BLR','DEL');
insert into flight_schedule values ('6F-085',4,'1100','0820','BLR','DEL');
insert into flight_schedule values ('6F-086',4,'0635','0500','BLR','BOM');
insert into flight_schedule values ('6F-087',4,'1835','1700','BLR','BOM');
insert into flight_schedule values ('6F-088',4,'1335','1200','BLR','BOM');
insert into flight_schedule values ('6F-089',4,'1240','1000','BLR','DEL');
insert into flight_schedule values ('6F-090',4,'1020','0800','BLR','KOL');
insert into flight_schedule values ('6F-091',4,'1320','1100','BLR','KOL');
insert into flight_schedule values ('6F-092',4,'2120','1900','BLR','KOL');
insert into flight_schedule values ('6F-093',4,'0605','0500','BLR','MAA');
insert into flight_schedule values ('6F-094',4,'1005','0900','BLR','MAA');
insert into flight_schedule values ('6F-095',4,'1405','1500','BLR','MAA');
insert into flight_schedule values ('6F-096',4,'0745','0500','KOL','BOM');
insert into flight_schedule values ('6F-097',4,'1345','1100','KOL','BOM');
insert into flight_schedule values ('6F-098',4,'1745','1500','KOL','BOM');
insert into flight_schedule values ('6F-099',4,'1220','1000','KOL','BLR');
insert into flight_schedule values ('6F-100',4,'1520','1300','KOL','BLR');
insert into flight_schedule values ('6F-101',4,'2220','2000','KOL','BLR');
insert into flight_schedule values ('6F-102',4,'0810','0600','KOL','DEL');
insert into flight_schedule values ('6F-103',4,'1710','1500','KOL','DEL');
insert into flight_schedule values ('6F-104',4,'2110','1900','KOL','DEL');
insert into flight_schedule values ('6F-105',4,'0825','0600','KOL','MAA');
insert into flight_schedule values ('6F-106',4,'1225','1000','KOL','MAA');
insert into flight_schedule values ('6F-107',4,'2225','2000','KOL','MAA');
insert into flight_schedule values ('6F-108',4,'0700','0500','MAA','BOM');
insert into flight_schedule values ('6F-109',4,'1050','0900','MAA','BOM');
insert into flight_schedule values ('6F-110',4,'1650','1500','MAA','BOM');
insert into flight_schedule values ('6F-111',4,'2250','2100','MAA','BLR');
insert into flight_schedule values ('6F-112',4,'0705','0600','MAA','BLR');
insert into flight_schedule values ('6F-113',4,'1205','1100','MAA','BLR');
insert into flight_schedule values ('6F-114',4,'1225','1000','MAA','KOL');
insert into flight_schedule values ('6F-115',4,'1625','1400','MAA','KOL');
insert into flight_schedule values ('6F-116',4,'2025','1800','MAA','KOL');
insert into flight_schedule values ('6F-117',4,'0950','0700','MAA','DEL');
insert into flight_schedule values ('6F-118',4,'1150','0900','MAA','DEL');
insert into flight_schedule values ('6F-119',4,'2350','2100','MAA','DEL');
insert into flight_schedule values ('6F-120',5,'0700','0500','BOM','DEL');
insert into flight_schedule values ('6F-121',5,'1400','1200','BOM','DEL');
insert into flight_schedule values ('6F-122',5,'0935','0800','BOM','BLR');
insert into flight_schedule values ('6F-123',5,'1505','1330','BOM','BLR');
insert into flight_schedule values ('6F-124',5,'1000','0900','BOM','DEL');
insert into flight_schedule values ('6F-125',5,'1935','1800','BOM','BLR');
insert into flight_schedule values ('6F-126',5,'1045','0800','BOM','KOL');
insert into flight_schedule values ('6F-127',5,'1445','1200','BOM','KOL');
insert into flight_schedule values ('6F-128',5,'2245','2000','BOM','KOL');
insert into flight_schedule values ('6F-129',5,'1250','1100','BOM','MAA');
insert into flight_schedule values ('6F-130',5,'0650','0500','BOM','MAA');
insert into flight_schedule values ('6F-131',5,'1650','1500','BOM','MAA');
insert into flight_schedule values ('6F-132',5,'0800','0600','DEL','BOM');
insert into flight_schedule values ('6F-133',5,'1200','1000','DEL','BOM');
insert into flight_schedule values ('6F-134',5,'2100','1900','DEL','BOM');
insert into flight_schedule values ('6F-135',5,'0940','0700','DEL','BLR');
insert into flight_schedule values ('6F-136',5,'1240','1000','DEL','BLR');
insert into flight_schedule values ('6F-137',5,'2240','2000','DEL','BLR');
insert into flight_schedule values ('6F-138',5,'1010','0800','DEL','KOL');
insert into flight_schedule values ('6F-139',5,'1710','1500','DEL','KOL');
insert into flight_schedule values ('6F-140',5,'1310','1100','DEL','KOL');
insert into flight_schedule values ('6F-141',5,'0850','0600','DEL','MAA');
insert into flight_schedule values ('6F-142',5,'1350','1100','DEL','MAA');
insert into flight_schedule values ('6F-143',5,'1650','1400','DEL','MAA');
insert into flight_schedule values ('6F-144',5,'0810','0530','BLR','DEL');
insert into flight_schedule values ('6F-145',5,'1100','0820','BLR','DEL');
insert into flight_schedule values ('6F-146',5,'0635','0500','BLR','BOM');
insert into flight_schedule values ('6F-147',5,'1835','1700','BLR','BOM');
insert into flight_schedule values ('6F-148',5,'1335','1200','BLR','BOM');
insert into flight_schedule values ('6F-149',5,'1240','1000','BLR','DEL');
insert into flight_schedule values ('6F-150',5,'1020','0800','BLR','KOL');
insert into flight_schedule values ('6F-151',5,'1320','1100','BLR','KOL');
insert into flight_schedule values ('6F-152',5,'2120','1900','BLR','KOL');
insert into flight_schedule values ('6F-153',5,'0605','0500','BLR','MAA');
insert into flight_schedule values ('6F-154',5,'1005','0900','BLR','MAA');
insert into flight_schedule values ('6F-155',5,'1405','1500','BLR','MAA');
insert into flight_schedule values ('6F-156',5,'0745','0500','KOL','BOM');
insert into flight_schedule values ('6F-157',5,'1345','1100','KOL','BOM');
insert into flight_schedule values ('6F-158',5,'1745','1500','KOL','BOM');
insert into flight_schedule values ('6F-159',5,'1220','1000','KOL','BLR');
insert into flight_schedule values ('6F-160',5,'1520','1300','KOL','BLR');
insert into flight_schedule values ('6F-161',5,'2220','2000','KOL','BLR');
insert into flight_schedule values ('6F-162',5,'0810','0600','KOL','DEL');
insert into flight_schedule values ('6F-163',5,'1710','1500','KOL','DEL');
insert into flight_schedule values ('6F-164',5,'2110','1900','KOL','DEL');
insert into flight_schedule values ('6F-165',5,'0825','0600','KOL','MAA');
insert into flight_schedule values ('6F-166',5,'1225','1000','KOL','MAA');
insert into flight_schedule values ('6F-167',5,'2225','2000','KOL','MAA');
insert into flight_schedule values ('6F-168',5,'0700','0500','MAA','BOM');
insert into flight_schedule values ('6F-169',5,'1050','0900','MAA','BOM');
insert into flight_schedule values ('6F-170',5,'1650','1500','MAA','BOM');
insert into flight_schedule values ('6F-171',5,'2250','2100','MAA','BLR');
insert into flight_schedule values ('6F-172',5,'0705','0600','MAA','BLR');
insert into flight_schedule values ('6F-173',5,'1205','1100','MAA','BLR');
insert into flight_schedule values ('6F-174',5,'1225','1000','MAA','KOL');
insert into flight_schedule values ('6F-175',5,'1625','1400','MAA','KOL');
insert into flight_schedule values ('6F-176',5,'2025','1800','MAA','KOL');
insert into flight_schedule values ('6F-177',5,'0950','0700','MAA','DEL');
insert into flight_schedule values ('6F-178',5,'1150','0900','MAA','DEL');
insert into flight_schedule values ('6F-179',5,'2350','2100','MAA','DEL');
insert into flight_schedule values ('6F-180',6,'0700','0500','BOM','DEL');
insert into flight_schedule values ('6F-181',6,'1400','1200','BOM','DEL');
insert into flight_schedule values ('6F-182',6,'0935','0800','BOM','BLR');
insert into flight_schedule values ('6F-183',6,'1505','1330','BOM','BLR');
insert into flight_schedule values ('6F-184',6,'1000','0900','BOM','DEL');
insert into flight_schedule values ('6F-185',6,'1935','1800','BOM','BLR');
insert into flight_schedule values ('6F-186',6,'1045','0800','BOM','KOL');
insert into flight_schedule values ('6F-187',6,'1445','1200','BOM','KOL');
insert into flight_schedule values ('6F-188',6,'2245','2000','BOM','KOL');
insert into flight_schedule values ('6F-189',6,'1250','1100','BOM','MAA');
insert into flight_schedule values ('6F-190',6,'0650','0500','BOM','MAA');
insert into flight_schedule values ('6F-191',6,'1650','1500','BOM','MAA');
insert into flight_schedule values ('6F-192',6,'0800','0600','DEL','BOM');
insert into flight_schedule values ('6F-193',6,'1200','1000','DEL','BOM');
insert into flight_schedule values ('6F-194',6,'2100','1900','DEL','BOM');
insert into flight_schedule values ('6F-195',6,'0940','0700','DEL','BLR');
insert into flight_schedule values ('6F-196',6,'1240','1000','DEL','BLR');
insert into flight_schedule values ('6F-197',6,'2240','2000','DEL','BLR');
insert into flight_schedule values ('6F-198',6,'1010','0800','DEL','KOL');
insert into flight_schedule values ('6F-199',6,'1710','1500','DEL','KOL');
insert into flight_schedule values ('6F-200',6,'1310','1100','DEL','KOL');
insert into flight_schedule values ('6F-201',6,'0850','0600','DEL','MAA');
insert into flight_schedule values ('6F-202',6,'1350','1100','DEL','MAA');
insert into flight_schedule values ('6F-203',6,'1650','1400','DEL','MAA');
insert into flight_schedule values ('6F-204',6,'0810','0530','BLR','DEL');
insert into flight_schedule values ('6F-205',6,'1100','0820','BLR','DEL');
insert into flight_schedule values ('6F-206',6,'0635','0500','BLR','BOM');
insert into flight_schedule values ('6F-207',6,'1835','1700','BLR','BOM');
insert into flight_schedule values ('6F-208',6,'1335','1200','BLR','BOM');
insert into flight_schedule values ('6F-209',6,'1240','1000','BLR','DEL');
insert into flight_schedule values ('6F-210',6,'1020','0800','BLR','KOL');
insert into flight_schedule values ('6F-211',6,'1320','1100','BLR','KOL');
insert into flight_schedule values ('6F-212',6,'2120','1900','BLR','KOL');
insert into flight_schedule values ('6F-213',6,'0605','0500','BLR','MAA');
insert into flight_schedule values ('6F-214',6,'1005','0900','BLR','MAA');
insert into flight_schedule values ('6F-215',6,'1405','1500','BLR','MAA');
insert into flight_schedule values ('6F-216',6,'0745','0500','KOL','BOM');
insert into flight_schedule values ('6F-217',6,'1345','1100','KOL','BOM');
insert into flight_schedule values ('6F-218',6,'1745','1500','KOL','BOM');
insert into flight_schedule values ('6F-219',6,'1220','1000','KOL','BLR');
insert into flight_schedule values ('6F-220',6,'1520','1300','KOL','BLR');
insert into flight_schedule values ('6F-221',6,'2220','2000','KOL','BLR');
insert into flight_schedule values ('6F-222',6,'0810','0600','KOL','DEL');
insert into flight_schedule values ('6F-223',6,'1710','1500','KOL','DEL');
insert into flight_schedule values ('6F-224',6,'2110','1900','KOL','DEL');
insert into flight_schedule values ('6F-225',6,'0825','0600','KOL','MAA');
insert into flight_schedule values ('6F-226',6,'1225','1000','KOL','MAA');
insert into flight_schedule values ('6F-227',6,'2225','2000','KOL','MAA');
insert into flight_schedule values ('6F-228',6,'0700','0500','MAA','BOM');
insert into flight_schedule values ('6F-229',6,'1050','0900','MAA','BOM');
insert into flight_schedule values ('6F-230',6,'1650','1500','MAA','BOM');
insert into flight_schedule values ('6F-231',6,'2250','2100','MAA','BLR');
insert into flight_schedule values ('6F-232',6,'0705','0600','MAA','BLR');
insert into flight_schedule values ('6F-233',6,'1205','1100','MAA','BLR');
insert into flight_schedule values ('6F-234',6,'1225','1000','MAA','KOL');
insert into flight_schedule values ('6F-235',6,'1625','1400','MAA','KOL');
insert into flight_schedule values ('6F-236',6,'2025','1800','MAA','KOL');
insert into flight_schedule values ('6F-237',6,'0950','0700','MAA','DEL');
insert into flight_schedule values ('6F-238',6,'1150','0900','MAA','DEL');
insert into flight_schedule values ('6F-239',6,'2350','2100','MAA','DEL');
insert into flight_schedule values ('6F-240',7,'0700','0500','BOM','DEL');
insert into flight_schedule values ('6F-241',7,'1400','1200','BOM','DEL');
insert into flight_schedule values ('6F-242',7,'0935','0800','BOM','BLR');
insert into flight_schedule values ('6F-243',7,'1505','1330','BOM','BLR');
insert into flight_schedule values ('6F-244',7,'1000','0900','BOM','DEL');
insert into flight_schedule values ('6F-245',7,'1935','1800','BOM','BLR');
insert into flight_schedule values ('6F-246',7,'1045','0800','BOM','KOL');
insert into flight_schedule values ('6F-247',7,'1445','1200','BOM','KOL');
insert into flight_schedule values ('6F-248',7,'2245','2000','BOM','KOL');
insert into flight_schedule values ('6F-249',7,'1250','1100','BOM','MAA');
insert into flight_schedule values ('6F-250',7,'0650','0500','BOM','MAA');
insert into flight_schedule values ('6F-251',7,'1650','1500','BOM','MAA');
insert into flight_schedule values ('6F-252',7,'0800','0600','DEL','BOM');
insert into flight_schedule values ('6F-253',7,'1200','1000','DEL','BOM');
insert into flight_schedule values ('6F-254',7,'2100','1900','DEL','BOM');
insert into flight_schedule values ('6F-255',7,'0940','0700','DEL','BLR');
insert into flight_schedule values ('6F-256',7,'1240','1000','DEL','BLR');
insert into flight_schedule values ('6F-257',7,'2240','2000','DEL','BLR');
insert into flight_schedule values ('6F-258',7,'1010','0800','DEL','KOL');
insert into flight_schedule values ('6F-259',7,'1710','1500','DEL','KOL');
insert into flight_schedule values ('6F-260',7,'1310','1100','DEL','KOL');
insert into flight_schedule values ('6F-261',7,'0850','0600','DEL','MAA');
insert into flight_schedule values ('6F-262',7,'1350','1100','DEL','MAA');
insert into flight_schedule values ('6F-263',7,'1650','1400','DEL','MAA');
insert into flight_schedule values ('6F-264',7,'0810','0530','BLR','DEL');
insert into flight_schedule values ('6F-265',7,'1100','0820','BLR','DEL');
insert into flight_schedule values ('6F-266',7,'0635','0500','BLR','BOM');
insert into flight_schedule values ('6F-267',7,'1835','1700','BLR','BOM');
insert into flight_schedule values ('6F-268',7,'1335','1200','BLR','BOM');
insert into flight_schedule values ('6F-269',7,'1240','1000','BLR','DEL');
insert into flight_schedule values ('6F-270',7,'1020','0800','BLR','KOL');
insert into flight_schedule values ('6F-271',7,'1320','1100','BLR','KOL');
insert into flight_schedule values ('6F-272',7,'2120','1900','BLR','KOL');
insert into flight_schedule values ('6F-273',7,'0605','0500','BLR','MAA');
insert into flight_schedule values ('6F-274',7,'1005','0900','BLR','MAA');
insert into flight_schedule values ('6F-275',7,'1405','1500','BLR','MAA');
insert into flight_schedule values ('6F-276',7,'0745','0500','KOL','BOM');
insert into flight_schedule values ('6F-277',7,'1345','1100','KOL','BOM');
insert into flight_schedule values ('6F-278',7,'1745','1500','KOL','BOM');
insert into flight_schedule values ('6F-279',7,'1220','1000','KOL','BLR');
insert into flight_schedule values ('6F-280',7,'1520','1300','KOL','BLR');
insert into flight_schedule values ('6F-281',7,'2220','2000','KOL','BLR');
insert into flight_schedule values ('6F-282',7,'0810','0600','KOL','DEL');
insert into flight_schedule values ('6F-283',7,'1710','1500','KOL','DEL');
insert into flight_schedule values ('6F-284',7,'2110','1900','KOL','DEL');
insert into flight_schedule values ('6F-285',7,'0825','0600','KOL','MAA');
insert into flight_schedule values ('6F-286',7,'1225','1000','KOL','MAA');
insert into flight_schedule values ('6F-287',7,'2225','2000','KOL','MAA');
insert into flight_schedule values ('6F-288',7,'0700','0500','MAA','BOM');
insert into flight_schedule values ('6F-289',7,'1050','0900','MAA','BOM');
insert into flight_schedule values ('6F-290',7,'1650','1500','MAA','BOM');
insert into flight_schedule values ('6F-291',7,'2250','2100','MAA','BLR');
insert into flight_schedule values ('6F-292',7,'0705','0600','MAA','BLR');
insert into flight_schedule values ('6F-293',7,'1205','1100','MAA','BLR');
insert into flight_schedule values ('6F-294',7,'1225','1000','MAA','KOL');
insert into flight_schedule values ('6F-295',7,'1625','1400','MAA','KOL');
insert into flight_schedule values ('6F-296',7,'2025','1800','MAA','KOL');
insert into flight_schedule values ('6F-297',7,'0950','0700','MAA','DEL');
insert into flight_schedule values ('6F-298',7,'1150','0900','MAA','DEL');
insert into flight_schedule values ('6F-299',7,'2350','2100','MAA','DEL');


/*
 * in this table, a flight_id  gets a plane_id.
 * informally, here we assign the plane which will fly as the given flight_id, on that day
 * hence, we assign a virtual entity flight to an actual aeroplane.
 */
insert into flight_map values ('6E-912','pid001');
insert into flight_map values ('6E-910','pid001');
insert into flight_map values ('6E-901','pid003');
insert into flight_map values ('6E-902','pid004');
insert into flight_map values ('6E-903','pid005');
insert into flight_map values ('6E-904','pid006');
insert into flight_map values ('6E-905','pid007');
insert into flight_map values ('6E-906','pid008');
insert into flight_map values ('6E-907','pid009');
insert into flight_map values ('6E-908','pid010');
insert into flight_map values ('6E-909','pid011');
insert into flight_map values ('6E-911','pid012');
insert into flight_map values ('6E-913','pid013');
insert into flight_map values ('6E-914','pid014');
insert into flight_map values ('6E-915','pid015');
insert into flight_map values ('6E-930','pid001');
insert into flight_map values ('6E-927','pid001');
insert into flight_map values ('6E-916','pid003');
insert into flight_map values ('6E-917','pid004');
insert into flight_map values ('6E-918','pid005');
insert into flight_map values ('6E-919','pid006');
insert into flight_map values ('6E-920','pid007');
insert into flight_map values ('6E-921','pid008');
insert into flight_map values ('6E-922','pid009');
insert into flight_map values ('6E-923','pid010');
insert into flight_map values ('6E-924','pid011');
insert into flight_map values ('6E-925','pid012');
insert into flight_map values ('6E-926','pid013');
insert into flight_map values ('6E-928','pid014');
insert into flight_map values ('6E-929','pid015');
insert into flight_map values ('6E-936','pid001');
insert into flight_map values ('6E-935','pid001');
insert into flight_map values ('6E-938','pid002');
insert into flight_map values ('6E-945','pid002');
insert into flight_map values ('6E-940','pid003');
insert into flight_map values ('6E-942','pid003');
insert into flight_map values ('6E-931','pid007');
insert into flight_map values ('6E-932','pid008');
insert into flight_map values ('6E-933','pid009');
insert into flight_map values ('6E-934','pid010');
insert into flight_map values ('6E-937','pid011');
insert into flight_map values ('6E-939','pid012');
insert into flight_map values ('6E-941','pid013');
insert into flight_map values ('6E-943','pid014');
insert into flight_map values ('6E-944','pid015');
insert into flight_map values ('6E-951','pid001');
insert into flight_map values ('6E-952','pid001');
insert into flight_map values ('6E-955','pid002');
insert into flight_map values ('6E-957','pid002');
insert into flight_map values ('6E-946','pid005');
insert into flight_map values ('6E-947','pid006');
insert into flight_map values ('6E-948','pid007');
insert into flight_map values ('6E-949','pid008');
insert into flight_map values ('6E-950','pid009');
insert into flight_map values ('6E-953','pid010');
insert into flight_map values ('6E-954','pid011');
insert into flight_map values ('6E-956','pid012');
insert into flight_map values ('6E-958','pid013');
insert into flight_map values ('6E-959','pid014');
insert into flight_map values ('6E-960','pid015');
insert into flight_map values ('6E-961','pid001');
insert into flight_map values ('6E-962','pid002');
insert into flight_map values ('6E-963','pid003'); 
insert into flight_map values ('6E-964','pid004');
insert into flight_map values ('6E-965','pid005');
insert into flight_map values ('6E-966','pid006');
insert into flight_map values ('6E-967','pid007');
insert into flight_map values ('6E-968','pid008');
insert into flight_map values ('6E-969','pid009');
insert into flight_map values ('6E-970','pid010');
insert into flight_map values ('6E-971','pid011');
insert into flight_map values ('6E-972','pid015');
insert into flight_map values ('6E-973','pid013');
insert into flight_map values ('6E-974','pid014');
insert into flight_map values ('6E-975','pid015');
insert into flight_map values ('6E-976','pid001');
insert into flight_map values ('6E-977','pid002');
insert into flight_map values ('6E-978','pid003'); 
insert into flight_map values ('6E-979','pid004');
insert into flight_map values ('6E-980','pid005');
insert into flight_map values ('6E-981','pid006');
insert into flight_map values ('6E-982','pid007');
insert into flight_map values ('6E-983','pid008');
insert into flight_map values ('6E-984','pid009');
insert into flight_map values ('6E-985','pid010');
insert into flight_map values ('6E-986','pid011');
insert into flight_map values ('6E-987','pid010');
insert into flight_map values ('6E-988','pid013');
insert into flight_map values ('6E-989','pid014');
insert into flight_map values ('6E-990','pid015');
insert into flight_map values ('6F-001','pid001');
insert into flight_map values ('6F-002','pid002');
insert into flight_map values ('6F-003','pid003');
insert into flight_map values ('6F-004','pid004');
insert into flight_map values ('6F-005','pid005');
insert into flight_map values ('6F-006','pid005');
insert into flight_map values ('6F-007','pid007');
insert into flight_map values ('6F-008','pid008');
insert into flight_map values ('6F-009','pid009');
insert into flight_map values ('6F-010','pid010');
insert into flight_map values ('6F-011','pid011');
insert into flight_map values ('6F-012','pid010');
insert into flight_map values ('6F-013','pid013');
insert into flight_map values ('6F-014','pid014');
insert into flight_map values ('6F-015','pid008');
insert into flight_map values ('6F-030','pid021');
insert into flight_map values ('6F-031','pid042');
insert into flight_map values ('6F-032','pid022');
insert into flight_map values ('6F-033','pid023');
insert into flight_map values ('6F-034','pid024');
insert into flight_map values ('6F-035','pid025');
insert into flight_map values ('6F-036','pid026');
insert into flight_map values ('6F-037','pid027');
insert into flight_map values ('6F-038','pid028');
insert into flight_map values ('6F-039','pid029');
insert into flight_map values ('6F-040','pid030');
insert into flight_map values ('6F-041','pid041');
insert into flight_map values ('6F-042','pid043');
insert into flight_map values ('6F-043','pid044');
insert into flight_map values ('6F-044','pid045');
insert into flight_map values ('6F-045','pid046');
insert into flight_map values ('6F-046','pid047');
insert into flight_map values ('6F-047','pid048');
insert into flight_map values ('6F-048','pid049');
insert into flight_map values ('6F-049','pid050');
insert into flight_map values ('6F-050','pid021');
insert into flight_map values ('6F-051','pid022');
insert into flight_map values ('6F-052','pid023');
insert into flight_map values ('6F-053','pid024');
insert into flight_map values ('6F-054','pid025');
insert into flight_map values ('6F-055','pid026');
insert into flight_map values ('6F-056','pid027');
insert into flight_map values ('6F-057','pid028');
insert into flight_map values ('6F-058','pid029');
insert into flight_map values ('6F-059','pid030');
insert into flight_map values ('6F-300','pid041');
insert into flight_map values ('6F-301','pid042');
insert into flight_map values ('6F-302','pid043');
insert into flight_map values ('6F-303','pid044');
insert into flight_map values ('6F-304','pid045');
insert into flight_map values ('6F-305','pid046');
insert into flight_map values ('6F-306','pid047');
insert into flight_map values ('6F-307','pid048');
insert into flight_map values ('6F-308','pid049');
insert into flight_map values ('6F-309','pid050');
insert into flight_map values ('6F-310','pid051'); 
insert into flight_map values ('6F-311','pid052'); 
insert into flight_map values ('6F-016','pid054');
insert into flight_map values ('6F-017','pid055');
insert into flight_map values ('6F-018','pid056');
insert into flight_map values ('6F-019','pid057');
insert into flight_map values ('6F-020','pid058');
insert into flight_map values ('6F-021','pid059');
insert into flight_map values ('6F-022','pid060');
insert into flight_map values ('6F-023','pid111');
insert into flight_map values ('6F-024','pid112');
insert into flight_map values ('6F-025','pid113');
insert into flight_map values ('6F-026','pid114');
insert into flight_map values ('6F-027','pid115');
insert into flight_map values ('6F-028','pid116');
insert into flight_map values ('6F-029','pid117');
insert into flight_map values ('6F-060','pid031');
insert into flight_map values ('6F-061','pid032');
insert into flight_map values ('6F-062','pid033');
insert into flight_map values ('6F-063','pid034');
insert into flight_map values ('6F-064','pid035');
insert into flight_map values ('6F-065','pid036');
insert into flight_map values ('6F-066','pid037');
insert into flight_map values ('6F-067','pid038');
insert into flight_map values ('6F-068','pid039');
insert into flight_map values ('6F-069','pid040');
insert into flight_map values ('6F-070','pid061');
insert into flight_map values ('6F-071','pid062');
insert into flight_map values ('6F-072','pid063');
insert into flight_map values ('6F-073','pid064');
insert into flight_map values ('6F-074','pid065');
insert into flight_map values ('6F-075','pid066');
insert into flight_map values ('6F-076','pid067');
insert into flight_map values ('6F-077','pid068');
insert into flight_map values ('6F-078','pid069');
insert into flight_map values ('6F-079','pid070');
insert into flight_map values ('6F-080','pid071');
insert into flight_map values ('6F-081','pid072');
insert into flight_map values ('6F-082','pid073');
insert into flight_map values ('6F-083','pid074');
insert into flight_map values ('6F-084','pid075');
insert into flight_map values ('6F-085','pid076');
insert into flight_map values ('6F-086','pid077');
insert into flight_map values ('6F-087','pid078');
insert into flight_map values ('6F-088','pid079');
insert into flight_map values ('6F-089','pid080');
insert into flight_map values ('6F-090','pid081');
insert into flight_map values ('6F-091','pid082');
insert into flight_map values ('6F-092','pid083');
insert into flight_map values ('6F-093','pid084');
insert into flight_map values ('6F-094','pid085');
insert into flight_map values ('6F-095','pid086');
insert into flight_map values ('6F-096','pid087');
insert into flight_map values ('6F-097','pid088');
insert into flight_map values ('6F-098','pid089');
insert into flight_map values ('6F-099','pid090');
insert into flight_map values ('6F-100','pid091');
insert into flight_map values ('6F-101','pid092');
insert into flight_map values ('6F-102','pid093');
insert into flight_map values ('6F-103','pid094');
insert into flight_map values ('6F-104','pid095');
insert into flight_map values ('6F-105','pid096');
insert into flight_map values ('6F-106','pid097');
insert into flight_map values ('6F-107','pid098');
insert into flight_map values ('6F-108','pid099');
insert into flight_map values ('6F-109','pid100');
insert into flight_map values ('6F-110','pid101');
insert into flight_map values ('6F-111','pid102');
insert into flight_map values ('6F-112','pid103');
insert into flight_map values ('6F-113','pid104');
insert into flight_map values ('6F-114','pid105');
insert into flight_map values ('6F-115','pid106');
insert into flight_map values ('6F-116','pid107');
insert into flight_map values ('6F-117','pid108'); 
insert into flight_map values ('6F-118','pid109');
insert into flight_map values ('6F-119','pid110');
insert into flight_map values ('6F-120','pid031');
insert into flight_map values ('6F-121','pid032');
insert into flight_map values ('6F-122','pid033');
insert into flight_map values ('6F-123','pid034');
insert into flight_map values ('6F-124','pid035');
insert into flight_map values ('6F-125','pid036');
insert into flight_map values ('6F-126','pid037');
insert into flight_map values ('6F-127','pid038');
insert into flight_map values ('6F-128','pid039');
insert into flight_map values ('6F-129','pid040');
insert into flight_map values ('6F-130','pid061');
insert into flight_map values ('6F-131','pid062');
insert into flight_map values ('6F-132','pid063');
insert into flight_map values ('6F-133','pid064');
insert into flight_map values ('6F-134','pid065');
insert into flight_map values ('6F-135','pid066');
insert into flight_map values ('6F-136','pid067');
insert into flight_map values ('6F-137','pid068');
insert into flight_map values ('6F-138','pid069');
insert into flight_map values ('6F-139','pid070');
insert into flight_map values ('6F-140','pid071');
insert into flight_map values ('6F-141','pid072');
insert into flight_map values ('6F-142','pid073');
insert into flight_map values ('6F-143','pid074');
insert into flight_map values ('6F-144','pid075');
insert into flight_map values ('6F-145','pid076');
insert into flight_map values ('6F-146','pid077');
insert into flight_map values ('6F-147','pid078');
insert into flight_map values ('6F-148','pid079');
insert into flight_map values ('6F-149','pid080');
insert into flight_map values ('6F-150','pid081');
insert into flight_map values ('6F-151','pid082');
insert into flight_map values ('6F-152','pid083');
insert into flight_map values ('6F-153','pid084');
insert into flight_map values ('6F-154','pid085');
insert into flight_map values ('6F-155','pid086');
insert into flight_map values ('6F-156','pid087');
insert into flight_map values ('6F-157','pid088');
insert into flight_map values ('6F-158','pid089');
insert into flight_map values ('6F-159','pid090');
insert into flight_map values ('6F-160','pid091');
insert into flight_map values ('6F-161','pid092');
insert into flight_map values ('6F-162','pid093');
insert into flight_map values ('6F-163','pid094');
insert into flight_map values ('6F-164','pid095');
insert into flight_map values ('6F-165','pid096');
insert into flight_map values ('6F-166','pid097');
insert into flight_map values ('6F-167','pid098');
insert into flight_map values ('6F-168','pid099');
insert into flight_map values ('6F-169','pid100');
insert into flight_map values ('6F-170','pid101');
insert into flight_map values ('6F-171','pid102');
insert into flight_map values ('6F-172','pid103');
insert into flight_map values ('6F-173','pid104');
insert into flight_map values ('6F-174','pid105');
insert into flight_map values ('6F-175','pid106');
insert into flight_map values ('6F-176','pid107');
insert into flight_map values ('6F-177','pid108');
insert into flight_map values ('6F-178','pid109');
insert into flight_map values ('6F-179','pid110');
insert into flight_map values ('6F-180','pid031');
insert into flight_map values ('6F-181','pid032');
insert into flight_map values ('6F-182','pid033');
insert into flight_map values ('6F-183','pid034');
insert into flight_map values ('6F-184','pid035');
insert into flight_map values ('6F-185','pid036');
insert into flight_map values ('6F-186','pid037');
insert into flight_map values ('6F-187','pid038');
insert into flight_map values ('6F-188','pid039');
insert into flight_map values ('6F-189','pid040');
insert into flight_map values ('6F-190','pid061');
insert into flight_map values ('6F-191','pid062');
insert into flight_map values ('6F-192','pid063');
insert into flight_map values ('6F-193','pid064');
insert into flight_map values ('6F-194','pid065');
insert into flight_map values ('6F-195','pid066');
insert into flight_map values ('6F-196','pid067');
insert into flight_map values ('6F-197','pid068');
insert into flight_map values ('6F-198','pid069');
insert into flight_map values ('6F-199','pid070');
insert into flight_map values ('6F-200','pid071');
insert into flight_map values ('6F-201','pid072');
insert into flight_map values ('6F-202','pid073');
insert into flight_map values ('6F-203','pid074');
insert into flight_map values ('6F-204','pid075');
insert into flight_map values ('6F-205','pid076');
insert into flight_map values ('6F-206','pid077');
insert into flight_map values ('6F-207','pid078');
insert into flight_map values ('6F-208','pid079');
insert into flight_map values ('6F-209','pid080');
insert into flight_map values ('6F-210','pid081');
insert into flight_map values ('6F-211','pid082');
insert into flight_map values ('6F-212','pid083');
insert into flight_map values ('6F-213','pid084');
insert into flight_map values ('6F-214','pid085');
insert into flight_map values ('6F-215','pid086');
insert into flight_map values ('6F-216','pid087');
insert into flight_map values ('6F-217','pid088');
insert into flight_map values ('6F-218','pid089');
insert into flight_map values ('6F-219','pid090');
insert into flight_map values ('6F-220','pid091');
insert into flight_map values ('6F-221','pid092');
insert into flight_map values ('6F-222','pid093');
insert into flight_map values ('6F-223','pid094');
insert into flight_map values ('6F-224','pid095');
insert into flight_map values ('6F-225','pid096');
insert into flight_map values ('6F-226','pid097');
insert into flight_map values ('6F-227','pid098');
insert into flight_map values ('6F-228','pid099');
insert into flight_map values ('6F-229','pid100');
insert into flight_map values ('6F-230','pid101');
insert into flight_map values ('6F-231','pid102');
insert into flight_map values ('6F-232','pid103');
insert into flight_map values ('6F-233','pid104');
insert into flight_map values ('6F-234','pid105');
insert into flight_map values ('6F-235','pid106');
insert into flight_map values ('6F-236','pid107');
insert into flight_map values ('6F-237','pid108');
insert into flight_map values ('6F-238','pid109');
insert into flight_map values ('6F-239','pid110');
insert into flight_map values ('6F-240','pid031');
insert into flight_map values ('6F-241','pid032');
insert into flight_map values ('6F-242','pid033');
insert into flight_map values ('6F-243','pid034');
insert into flight_map values ('6F-244','pid035');
insert into flight_map values ('6F-245','pid036');
insert into flight_map values ('6F-246','pid037');
insert into flight_map values ('6F-247','pid038');
insert into flight_map values ('6F-248','pid039');
insert into flight_map values ('6F-249','pid040');
insert into flight_map values ('6F-250','pid061');
insert into flight_map values ('6F-251','pid062');
insert into flight_map values ('6F-252','pid063');
insert into flight_map values ('6F-253','pid064');
insert into flight_map values ('6F-254','pid065');
insert into flight_map values ('6F-255','pid066');
insert into flight_map values ('6F-256','pid067');
insert into flight_map values ('6F-257','pid068');
insert into flight_map values ('6F-258','pid069');
insert into flight_map values ('6F-259','pid070');
insert into flight_map values ('6F-260','pid071');
insert into flight_map values ('6F-261','pid072');
insert into flight_map values ('6F-262','pid073');
insert into flight_map values ('6F-263','pid074');
insert into flight_map values ('6F-264','pid075');
insert into flight_map values ('6F-265','pid076');
insert into flight_map values ('6F-266','pid077');
insert into flight_map values ('6F-267','pid078');
insert into flight_map values ('6F-268','pid079');
insert into flight_map values ('6F-269','pid080');
insert into flight_map values ('6F-270','pid081');
insert into flight_map values ('6F-271','pid082');
insert into flight_map values ('6F-272','pid083');
insert into flight_map values ('6F-273','pid084');
insert into flight_map values ('6F-274','pid085');
insert into flight_map values ('6F-275','pid086');
insert into flight_map values ('6F-276','pid087');
insert into flight_map values ('6F-277','pid088');
insert into flight_map values ('6F-278','pid089');
insert into flight_map values ('6F-279','pid090');
insert into flight_map values ('6F-280','pid091');
insert into flight_map values ('6F-281','pid092');
insert into flight_map values ('6F-282','pid093');
insert into flight_map values ('6F-283','pid094');
insert into flight_map values ('6F-284','pid095');
insert into flight_map values ('6F-285','pid096');
insert into flight_map values ('6F-286','pid097');
insert into flight_map values ('6F-287','pid098');
insert into flight_map values ('6F-288','pid099');
insert into flight_map values ('6F-289','pid100');
insert into flight_map values ('6F-290','pid101');
insert into flight_map values ('6F-291','pid102');
insert into flight_map values ('6F-292','pid103');
insert into flight_map values ('6F-293','pid104');
insert into flight_map values ('6F-294','pid105');
insert into flight_map values ('6F-295','pid106');
insert into flight_map values ('6F-296','pid107');
insert into flight_map values ('6F-297','pid108');
insert into flight_map values ('6F-298','pid109');
insert into flight_map values ('6F-299','pid110');



