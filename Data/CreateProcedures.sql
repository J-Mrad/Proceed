/*	Backup & Restore	*/

CREATE PROCEDURE Backup_DB
@path varchar(50)
AS
BEGIN
	BACKUP DATABASE Proceed
	TO DISK=@path

END
GO


CREATE PROCEDURE Restore_DB
@path varchar(50)
AS
BEGIN
	
	DECLARE @pathA varchar(50);
	DECLARE @pathB varchar(50);

	SET @pathA = @path+'Proceed.mdf'
	SET @pathB = @path+'Proceed.ldf'

	RESTORE DATABASE Proceed
	FROM DISK=@path

	WITH
		MOVE 'Proceed_dat' TO @PathA,
		MOVE 'Proceed_log' TO @PathB
END
GO

/*	Procedure for admins to clear the logs	*/

CREATE PROCEDURE Clear_Log
AS
BEGIN

	DROP TRIGGER noDelete_LOG
	
	DELETE FROM LOG

	DECLARE @SQL varchar(MAX);
	SET @SQL = 
	'CREATE TRIGGER noDelete_LOG
	ON LOG
	INSTEAD OF DELETE
	AS
	BEGIN
		INSERT INTO LOG(EMP,TYPE,DETAILS,DATE)
		VALUES(SYSTEM_USER,''Deleting a locked value'',''LOG entries are permanent'',GETDATE())
		RAISERROR (''Logs can not be deleted!'',16,1);
	END'
	EXEC(@SQL);

END
GO

/*	Creating an account and login for a new employee	*/

CREATE PROCEDURE AddEmployee
@PosID INTEGER,
@Name varchar(MAX),
@Phone INTEGER,
@Email varchar(MAX),
@DOB varchar(MAX),
@Shift INTEGER,
@Hired varchar(MAX)
AS
BEGIN

	INSERT INTO EMPLOYEE(POSID,NAME,PHONE,EMAIL,DOB,SHIFTDURATION,DATEHIRED)
	VALUES(@PosID,@Name,@Phone,@Email,@DOB,@Shift,@Hired);

	DECLARE @Pass varchar(MAX);
	SET @Pass = SUBSTRING(@Email,1,3) + SUBSTRING(@Name,1,3)

	DECLARE @SQL varchar(MAX);
	SET @SQL = 'CREATE LOGIN [' + @Name + '] WITH PASSWORD=N''' + @Pass + ''', DEFAULT_DATABASE=[Proceed],
	 CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF';
	EXEC(@SQL);
	CREATE USER [@Name] FOR LOGIN [@Name] WITH DEFAULT_SCHEMA=[guest];
END
GO

/*	Transaction beteen two debit accounts		*/

CREATE PROCEDURE Cash_Transaction_Debit
@A INTEGER,
@B INTEGER,
@Value INTEGER
AS
BEGIN TRANSACTION

	DECLARE @OldA INTEGER;
	DECLARE @OldB INTEGER;
	DECLARE @NewA INTEGER;
	DECLARE @NewB INTEGER;

	SET @OldA = (SELECT BALANCE FROM DEBITACCOUNT WHERE PID10 = @A);
	SET @OldB = (SELECT BALANCE FROM DEBITACCOUNT WHERE PID10 = @B);

	SET @NewA = @OldA - @Value;
	SET @NewA = @OldB + @Value;

	IF(@NewA < 0)
	BEGIN
		ROLLBACK
	END
	
	UPDATE DEBITACCOUNT SET BALANCE = @NewA WHERE PID10 = @A;
	UPDATE DEBITACCOUNT SET BALANCE = @NewB WHERE PID10 = @B;
COMMIT
GO