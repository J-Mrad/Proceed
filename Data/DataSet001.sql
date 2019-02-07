/*	Positions		*/

INSERT INTO POSITION(POSNAME,SALARY,BONUS,BONUSCYCLEDAYS)
VALUES('Fired',0,0,0),
	('Intern',0,100,30),
	('Part-Time',800,200,90),
	('Full-Time',1200,400,90)
GO
/*	Default Employee	*/

INSERT INTO EMPLOYEE(POSID,NAME,PHONE,EMAIL,DOB,SHIFTDURATION,DATEHIRED)
VALUES(1,'None','00000','employee@removed',null,0,0-0-0000)


GO
/* 	Account Levels		*/


INSERT INTO ACCOUNTLEVEL(LEVEL,MINBALANCE,MAXDUE,MAXWITHDRAW,INTERESTGAIN,TAXCREDIT,INTERESTDEBIT)
VALUES(0,0,100,200,1,2,2),
	(1,100,100,200,1,2,2),
	(2,500,200,500,2,4,3),
	(3,1000,400,500,2,4,3),
	(4,5000,800,500,2,4,3),
	(5,10000,1600,1000,3,6,4),
	(6,100000,3200,1000,4,8,5)

GO
/*	Some Clients		*/


INSERT INTO CLIENT(NAME,PHONE,DOB,TOTALVALUE,TOTALACCOUNTS,TOTALLOANS,CLIENTSINCE,ISPREMIUM)
VALUES()


