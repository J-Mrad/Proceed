/*==============================================================*/
/* DBMS name:      Microsoft SQL Server 2012                    */
/* Created on:     1/13/2019 7:32:28 PM                         */
/*==============================================================*/

CREATE DATABASE Proceed
go

USE Proceed
go

/*==============================================================*/
/* Table: ACCOUNT                                               */
/*==============================================================*/
create table ACCOUNT (
   PID10                int   IDENTITY(1,1)  not null,
   LEVEL                int                  not null,
   DATECREATED          datetime             null,
   CURRENCY             char(3)              null,
   constraint PK_ACCOUNT primary key nonclustered (PID10)
)
go

/*==============================================================*/
/* Index: ISOFLEVEL_FK                                          */
/*==============================================================*/
create index ISOFLEVEL_FK on ACCOUNT (
LEVEL ASC
)
go

/*==============================================================*/
/* Table: ACCOUNTLEVEL                                          */
/*==============================================================*/
create table ACCOUNTLEVEL (
   LEVEL                int                  not null,
   MINBALANCE           int                  not null,
   MAXDUE               int                  not null,
   MAXWITHDRAW          int                  not null,
   INTERESTDEBIT        int                  null,
   INTERESTGAIN         int                  null,
   TAXCREDIT            int                  null,
   constraint PK_ACCOUNTLEVEL primary key nonclustered (LEVEL)
)
go


/*==============================================================*/
/* Table: CLIENT                                                */
/*==============================================================*/
create table CLIENT (
   CID                  int  IDENTITY(1,1)   not null,
   NAME                 varchar(20)          not null,
   PHONE                varchar(15)          not null,
   EMAIL                varchar(20)          null,
   DOB                  datetime             null,
   TOTALVALUE           int                  not null,
   TOTALACCOUNTS        int                  null,
   TOTALLOANS           int                  null,
   CLIENTSINCE          datetime             not null,
   ISPREMIUM            bit                  not null,
   constraint PK_CLIENT primary key (CID)
)
go

/*==============================================================*/
/* Table: CLIENTACCOUNT                                         */
/*==============================================================*/
create table CLIENTACCOUNT (
   PID10                int                  not null,
   CID                  int                  not null,
   EID                  int                  null,
   ISACTIVE             bit                  null,
   ISSHARED             bit                  null,
   constraint PK_CLIENTACCOUNT primary key (PID10, CID)
)
go

/*==============================================================*/
/* Index: CLIENTACCOUNT_FK                                      */
/*==============================================================*/
create index CLIENTACCOUNT_FK on CLIENTACCOUNT (
PID10 ASC
)
go

/*==============================================================*/
/* Index: CLIENTACCOUNT2_FK                                     */
/*==============================================================*/
create index CLIENTACCOUNT2_FK on CLIENTACCOUNT (
CID ASC
)
go

/*==============================================================*/
/* Table: CREDITACCOUNT                                         */
/*==============================================================*/
create table CREDITACCOUNT (
   PID10                int                  not null,
   CYCLEDAYS            int                  not null,
   AMMOUNTDUE           int                  not null,
   NEXTDUEDATE          datetime             not null,
   ISOVERDUE            bit                  not null,
   constraint PK_CREDITACCOUNT primary key (PID10)
)
go

/*==============================================================*/
/* Table: DEBITACCOUNT                                          */
/*==============================================================*/
create table DEBITACCOUNT (
   PID10                int                  not null,
   BALANCE              int                  not null,
   TOTALDEPOSIT         int                  not null,
   TOTALWITHDRAW        int                  not null,
   constraint PK_DEBITACCOUNT primary key (PID10)
)
go

/*==============================================================*/
/* Table: EMPLOYEE                                              */
/*==============================================================*/
create table EMPLOYEE (
   EID                  int   IDENTITY(1,1)  not null,
   POSID                int                  not null,
   NAME                 varchar(20)          not null,
   PHONE                varchar(15)          not null,
   EMAIL                varchar(20)          null,
   DOB                  datetime             null,
   SHIFTDURATION        int                  not null,
   DATEHIRED            datetime             not null,
   constraint PK_EMPLOYEE primary key (EID)
)
go

/*==============================================================*/
/* Index: HAS_FK                                                */
/*==============================================================*/
create index HAS_FK on EMPLOYEE (
POSID ASC
)
go

/*==============================================================*/
/* Table: LOAN                                                  */
/*==============================================================*/
create table LOAN (
   PID12                int   IDENTITY(1,1)  not null,
   CID                  int                  not null,
   DATETAKEN            datetime             not null,
   NEXTPAYMENT         	int                  not null,
   SPLITINTO            int                  null,
   CYCLEDAYS            int                  null,
   TOTALPAYMENTS        int                  null,
   PAIDSOFAR            int                  null,
   REMAININGDUE         int                  null,
   constraint PK_LOAN primary key nonclustered (PID12)
)
go

/*==============================================================*/
/* Index: TAKES_FK                                              */
/*==============================================================*/
create index TAKES_FK on LOAN (
CID ASC
)
go

/*==============================================================*/
/* Table: LOG                                                   */
/*==============================================================*/
create table LOG (
   LOGID                int   IDENTITY(1,1)  not null,
   EMP                  varchar(512)         null,
   TYPE                 varchar(32)          null,
   DETAILS              varchar(256)         null,
   DATE                 datetime             null,
   constraint PK_LOG primary key (LOGID)
)
go

/*==============================================================*/
/* Table: POSITION                                              */
/*==============================================================*/
create table POSITION (
   POSID                int   IDENTITY(1,1)  not null,
   POSNAME              varchar(10)          null,
   SALARY               int                  not null,
   BONUS                int                  null,
   BONUSCYCLEDAYS       int                  null,
   constraint PK_POSITION primary key nonclustered (POSID)
)
go

/*==============================================================*/
/* Table: RETIREMENTACCOUNT                                     */
/*==============================================================*/
create table RETIREMENTACCOUNT (
   PID10                int                  not null,
   BALANCE              int                  null,
   DUEDATE              datetime             null,
   constraint PK_RETIREMENTACCOUNT primary key (PID10)
)
go

/*==============================================================*/
/* Table: SAVINGSACCOUNT                                        */
/*==============================================================*/
create table SAVINGSACCOUNT (
   PID10                int                  not null,
   BALANCE              int                  null,
   WITHDRAWALS          int                  null,
   WITHDRAWLIMIT        int                  null,
   CYCLEDAYS            int                  null,
   NEXTREFRESH          datetime             null,
   constraint PK_SAVINGSACCOUNT primary key (PID10)
)
go

/*==============================================================*/
/* Table: YOUTHACCOUNT                                          */
/*==============================================================*/
create table YOUTHACCOUNT (
   PID10                int                  not null,
   SWITCHDATE           datetime             null,
   constraint PK_YOUTHACCOUNT primary key (PID10)
)
go

alter table ACCOUNT
   add constraint FK_ACCOUNT_ISOFLEVEL_ACCOUNTL foreign key (LEVEL)
      references ACCOUNTLEVEL (LEVEL)
go

alter table CLIENTACCOUNT
   add constraint FK_CLIENTAC_CLIENTACC_ACCOUNT foreign key (PID10)
      references ACCOUNT (PID10)
go

alter table CLIENTACCOUNT
   add constraint FK_CLIENTAC_CLIENTACC_CLIENT foreign key (CID)
      references CLIENT (CID)
go

alter table CLIENTACCOUNT
   add constraint FK_CLIENTAC_REFERENCE_EMPLOYEE foreign key (EID)
      references EMPLOYEE (EID)
go

alter table CREDITACCOUNT
   add constraint FK_CREDITAC_INHERITAN_ACCOUNT foreign key (PID10)
      references ACCOUNT (PID10)
go

alter table DEBITACCOUNT
   add constraint FK_DEBITACC_INHERITAN_ACCOUNT foreign key (PID10)
      references ACCOUNT (PID10)
go

alter table EMPLOYEE
   add constraint FK_EMPLOYEE_HAS_POSITION foreign key (POSID)
      references POSITION (POSID)
go

alter table LOAN
   add constraint FK_LOAN_TAKES_CLIENT foreign key (CID)
      references CLIENT (CID)
go

alter table RETIREMENTACCOUNT
   add constraint FK_RETIREME_INHERITAN_ACCOUNT foreign key (PID10)
      references ACCOUNT (PID10)
go

alter table SAVINGSACCOUNT
   add constraint FK_SAVINGSA_INHERITAN_ACCOUNT foreign key (PID10)
      references ACCOUNT (PID10)
go

alter table YOUTHACCOUNT
   add constraint FK_YOUTHACC_INHERITAN_DEBITACC foreign key (PID10)
      references DEBITACCOUNT (PID10)
go

