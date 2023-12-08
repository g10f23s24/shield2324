
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Users')
BEGIN
    CREATE TABLE Users 
    (
        UserID nvarchar(50),
        UserName nvarchar(100),
        FullName nvarchar(100),
        Email nvarchar(100),
        FacialData varbinary(max),
        LockUnlock int,
        AccessTime int,
     
    );
END;




DECLARE @UnixTimestamp INT = 1696128400;


-- Insert data into the Users table and set AccessTime to the current local time
	INSERT INTO Users (UserID, Username, Fullname, Email, LockUnlock, FacialData, AccessTime)
		VALUES (2,  'Sara02', 'Sara Khouri', 'sk0167@uah.edu', '1',
		(SELECT * FROM OPENROWSET(BULK 'C:\Users\thomp\Desktop\mysql\sara.jpg', SINGLE_BLOB) AS T),
		DATEDIFF(SECOND, '1970-01-01', GETDATE()) --GETDATE() -- Set AccessTime to the current local time
		);


-- Insert another record
	INSERT INTO Users (UserID, Username, Fullname, Email, LockUnlock, FacialData, AccessTime)
		 VALUES ( 5, 'Dan05',  'Dan Otieno', 'dpo0002@uah.edu', '1',
		(SELECT * FROM OPENROWSET(BULK 'C:\Users\thomp\Desktop\mysql\dan.jpg', SINGLE_BLOB) AS T),
		 DATEDIFF(SECOND, '1970-01-01', GETDATE())-- Set AccessTime to the current local time
		);

-- Insert another record
	INSERT INTO Users (UserID, Username, Fullname, Email, LockUnlock, FacialData,  AccessTime)
		VALUES (  4, 'Tuyen04',  'Tuyen Alexander', 'tp0093@uah.edu', '0',
		(SELECT * FROM OPENROWSET(BULK 'C:\Users\thomp\Desktop\mysql\tuyen.jpg', SINGLE_BLOB) AS T),
		 DATEDIFF(SECOND, '1970-01-01', GETDATE())-- Set AccessTime to the current local time
		);

-- Insert another record
	INSERT INTO Users (UserID, Username, Fullname, Email, LockUnlock, FacialData, AccessTime)
		VALUES (  3,  'Alec03', 'Alec Miller', 'am0227@uah.edu', '0',
		 (SELECT * FROM OPENROWSET(BULK 'C:\Users\thomp\Desktop\mysql\alec.jpg', SINGLE_BLOB) AS T),
		DATEDIFF(SECOND, '1970-01-01', GETDATE())-- Set AccessTime to the current local time
		);

-- Insert another record
	INSERT INTO Users (UserID, Username, Fullname, Email, LockUnlock, FacialData, AccessTime)
		VALUES ( 1, 'Thom01', 'Thom Pham', 'ttp0008@uah.edu', '1',
		(SELECT * FROM OPENROWSET(BULK 'C:\Users\thomp\Desktop\mysql\thom.jpg', SINGLE_BLOB) AS T),
		DATEDIFF(SECOND, '1970-01-01', GETDATE())
		);




DECLARE @NewUserID INT;
SELECT @NewUserID = ISNULL(MAX(UserID), 0) + 1 FROM Users;


-- Insert the new user into the Users table with an empty FacialData column
INSERT INTO Users (UserID, Username, Fullname, Email, LockUnlock, FacialData, AccessTime)
VALUES (
    @NewUserID,   -- Use the unique UserID determined above
    'NewUser',    -- Set a username for the new user
    'New User Full Name', -- Set the full name
    'newuser@example.com', -- Set the email
    '0',           -- LockUnlock (you can change this to '0' if needed)
    NULL, -- No image, set FacialData to NULL
        -- Set the DateRegister (a valid UNIX timestamp)
    --GETDATE() -- Set AccessTime to the current local time
	DATEDIFF(SECOND, '1970-01-01', GETDATE())
);




