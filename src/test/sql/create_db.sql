DROP DATABASE IF EXISTS telesal;
CREATE DATABASE telesal;
USE telesal;

# All users who have called or accounts which was created
#DROP TABLE callers;
CREATE TABLE callers (
	id INT NOT NULL AUTO_INCREMENT,
	updated TIMESTAMP,						# PK index
	updater VARCHAR(16) NOT NULL,
	phone VARBINARY(16) NOT NULL UNIQUE,	# unique index
	label VARBINARY(256) NOT NULL,
	sip_id VARBINARY(16) UNIQUE,			# unique index
	sip_pw VARBINARY(16),
	PRIMARY KEY (id),
	INDEX (updated)
);

# SIP accounts which must be created or given a new password
#DROP TABLE new_accounts;
CREATE TABLE new_accounts (
	id INT NOT NULL UNIQUE,
	sip_id VARCHAR(16),
	sip_pw VARCHAR(16),
	countrycode VARCHAR(4),
	PRIMARY KEY (id)
);

#DROP TABLE mail_queue;
CREATE TABLE mail_queue (
	id INT NOT NULL AUTO_INCREMENT,
	e_mail VARCHAR(256),
	e_mail_subject TEXT,
	e_mail_body TEXT,
	PRIMARY KEY (id)
);

# Log of all calls
#DROP TABLE call_log;
CREATE TABLE call_log (
	id BIGINT NOT NULL AUTO_INCREMENT,
	now TIMESTAMP,
	caller VARBINARY(16),
	callee VARBINARY(16),
	activity VARCHAR(16),
	PRIMARY KEY (id),
	INDEX (now),
	INDEX (caller),
	INDEX (callee),
	INDEX (activity)
);

# List of contact persons
#DROP TABLE contacts;
CREATE TABLE contacts (					# Examples
	id INT NOT NULL AUTO_INCREMENT,
	updated TIMESTAMP,
	sip_id VARBINARY(16) UNIQUE,		# H46-12345-C ; H45-1234 ; H298-100
	hall_country VARCHAR(3),			# halls phone country code, 45=Denmark
	hall_zip VARCHAR(9),				# halls postal zip code
	hall_number VARCHAR(2),				# if more halls in same building or zip area; 0,1,2,99 or A,B,C
	contact_name VARBINARY(256),		# Testu Testusson
	contact_email VARBINARY(256),		# testu@testmail.tst
	contact_phone VARBINARY(16),		# +9991234567
	user_comment VARBINARY(256),		# (Comment visible in users web interface)
	admin_comment VARBINARY(256),		# (Comment not visible in users web interface)	
	PRIMARY KEY (id)
);

# ======== Regex patterns
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `regex_patterns`(
	OUT regex_phone VARCHAR(64),
	OUT regex_computer VARCHAR(64),
	OUT regex_hall VARCHAR(64),
	OUT regex_email VARCHAR(64)
)
BEGIN
	SET regex_phone		= "^\\+[0-9]{5,15}$";								#Example: +46123456789
	SET regex_computer	= "^L[0-9]{8}$";									#Example: L12345678
	SET regex_hall		= "^H[0-9]{1,3}-[A-Z0-9]{3,9}(-([A-Z0-9]{1,2}))?$";	#Example: H45-8654, H45-2400-10, H45-2400-C
	SET regex_email		= "^[^@ ]{1,}@{1}[^@ ]{1,}[\.]{1}[^@ \.]{2,}$";		#Example: a.b@c.d.ef
END$$

# ======== DEBUG Show all callers
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_all_callers`(key_ VARCHAR(40))
BEGIN
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));

	SELECT
		id,
		updated,
		updater,
		CONVERT(AES_DECRYPT(phone,s_key) USING utf8) AS phone,
		CONVERT(AES_DECRYPT(label,s_key) USING utf8) AS label,
		CONVERT(AES_DECRYPT(sip_id,s_key) USING utf8) AS sip_id,
		CONVERT(AES_DECRYPT(sip_pw,s_key) USING utf8) AS sip_pw
	FROM callers;
END$$

# ======== DEBUG Show all halls
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_all_halls`(
		IN key_ VARCHAR(40)
)
BEGIN
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));

	SELECT
		contacts.id,
		contacts.updated,
		CONVERT(AES_DECRYPT(callers.label,s_key) USING utf8) AS city,
		CONVERT(AES_DECRYPT(contacts.sip_id,s_key) USING utf8) AS sip_id,
		CONVERT(AES_DECRYPT(callers.sip_pw,s_key) USING utf8) AS sip_pw,
		CONVERT(AES_DECRYPT(callers.phone,s_key) USING utf8) AS dialin_phone,
		CONVERT(AES_DECRYPT(contacts.contact_name,s_key) USING utf8) AS name,
		CONVERT(AES_DECRYPT(contacts.contact_email,s_key) USING utf8) AS email,
		CONVERT(AES_DECRYPT(contacts.contact_phone,s_key) USING utf8) AS contact_phone,
		CONVERT(AES_DECRYPT(contacts.user_comment,s_key) USING utf8) AS user_comment,
		CONVERT(AES_DECRYPT(contacts.admin_comment,s_key) USING utf8) AS admin_comment
	FROM contacts
	INNER JOIN callers
	ON contacts.sip_id = callers.sip_id;
END$$

# ======== DEBUG Show call log
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_call_log`(
	IN key_ VARCHAR(40)
)
BEGIN
	DECLARE s_key VARBINARY(20);
	SET s_key = UNHEX(SHA(key_));

	SELECT 
		now,
		CONVERT(AES_DECRYPT(caller,s_key) USING utf8) AS caller,
		CONVERT(AES_DECRYPT(callee,s_key) USING utf8) AS callee,
		activity
	FROM call_log;
END$$

# ======== DEBUG show new accounts
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_new_accounts`(
#	IN key_ VARCHAR(40)
	)
BEGIN
#	DECLARE s_key VARBINARY(20);
#	SET s_key = UNHEX(SHA(key_));

	SELECT 
		id,
		e_mail,
		url_param,
		sip_id,
		sip_pw
	FROM new_accounts;
END$$

# ======== Create hall
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_hall`(
# OBS - denne runine opretter kun i MySQL, sætter ikke i kø til oprettelse i Asterisk!
# Oprettelse i Asterisk skal derfor ske manuelt.
	IN country_code VARCHAR(3),				# "45", "299"
	IN zip_code	VARCHAR(9),					# "123", "12345", "12345"
	IN city VARCHAR(256) CHARACTER SET utf8,
	IN hall_number_ VARCHAR(2),				# "2", "B", "10"
	IN dialin_number VARBINARY(16),			# "88801234" ; "0123456789" ; "123456" ; "1234567"
	IN contact_name_ VARCHAR(256) CHARACTER SET utf8,	# Technical responsible
	IN contact_email_ VARCHAR(256) CHARACTER SET utf8,
	IN contact_phone_ VARCHAR(16),
	IN user_comment_ VARCHAR(256) CHARACTER SET utf8,
	IN admin_comment_ VARCHAR(256) CHARACTER SET utf8,
	IN creator VARCHAR(16),
	IN key_ VARCHAR(40)
)
BEGIN
	DECLARE slength INT DEFAULT 12;			# SIP secret length
	DECLARE pass VARCHAR(16) DEFAULT "";	# Random SIP pw
	DECLARE sipid VARCHAR(16);
	DECLARE c_city VARBINARY(256);			# Encrypted city name
	DECLARE c_dialin VARBINARY(16);			# Encrypted dialin number
	DECLARE c_pass VARBINARY(16);			# Encrypted SIP pw
	DECLARE c_sipid VARBINARY(16);			# Encrypted SIP id
	DECLARE c_contact_name VARBINARY(256);
	DECLARE c_contact_email VARBINARY(256);
	DECLARE c_contact_phone VARBINARY(16);
	DECLARE c_user_comment VARBINARY(256);
	DECLARE c_admin_comment VARBINARY(256);
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));	# Hash of encryption key
	DECLARE regex_phone VARCHAR(64);
	DECLARE regex_hall VARCHAR(64);
	DECLARE regex_email VARCHAR(64);

# Initialize regex patterns
	CALL regex_patterns(regex_phone,@notused,regex_hall,regex_email);

# Create SIP id
	SET sipid = CONCAT("H",TRIM(country_code),"-",TRIM(zip_code));
	IF TRIM(hall_number_) != "" THEN
		SET sipid = CONCAT(sipid,"-",hall_number_);
	END IF;

# Check if hall SIP format
	IF sipid NOT REGEXP regex_hall THEN
		CALL ERROR_not_hall_SIP_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if E.164 phone numbers
	IF dialin_number NOT REGEXP regex_phone THEN
		CALL ERROR_not_E.164_number_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;
	IF contact_phone_ NOT REGEXP regex_phone THEN
		CALL ERROR_not_E.164_number_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if valid email format
	IF contact_email_ NOT REGEXP regex_email THEN
		CALL ERROR_not_valid_email_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Create SIP secret
	SET pass = create_password(slength);

# Encrypt 
	SET c_sipid = AES_ENCRYPT(sipid,s_key);
	SET c_city = AES_ENCRYPT(city,s_key);
	SET c_dialin = AES_ENCRYPT(dialin_number,s_key);
	SET c_pass = AES_ENCRYPT(pass,s_key);
	SET c_contact_name = AES_ENCRYPT(contact_name_,s_key);
	SET c_contact_email = AES_ENCRYPT(contact_email_,s_key);
	SET c_contact_phone = AES_ENCRYPT(contact_phone_,s_key);
	SET c_user_comment = AES_ENCRYPT(user_comment_,s_key);
	SET c_admin_comment = AES_ENCRYPT(admin_comment_,s_key);
	SET @now = CURRENT_TIMESTAMP;

# Insert or update records in callers db
	INSERT INTO callers (updated,updater,phone,label,sip_id,sip_pw) VALUES (
		@now,
		creator,
		c_dialin,
		c_city,
		c_sipid,
		c_pass
	);

	INSERT INTO contacts (
		updated,sip_id,hall_country,hall_zip,hall_number,
		contact_name,contact_email,contact_phone,user_comment,admin_comment
	)
	VALUES (
		@now,
		c_sipid,
		country_code,
		zip_code,
		hall_number_,
		c_contact_name,
		c_contact_email,
		c_contact_phone,
		c_user_comment,
		c_admin_comment
	);

END$$

# ======== Create computer caller (DEPRECATED)
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_computer_caller`(
	IN e164 			VARCHAR(16),  	# National phone number, leading zero allowed
	IN name 			VARCHAR(256) CHARACTER SET utf8,
	IN e_mail 			VARCHAR(256) CHARACTER SET utf8,
	IN hall 			VARCHAR(16),
	IN key_ 			VARCHAR(40)
)
BEGIN
	DECLARE slength 		INT DEFAULT 12;			# SIP secret length
	DECLARE alength 		INT DEFAULT 8;			# SIP account length
	DECLARE mlength 		INT DEFAULT 6;			# Minimum match of x last digits in phone number when doing SIP credential retrivial
	DECLARE listenerprefix	CHAR(1) DEFAULT "L";	# Listeners SIP URIs starts with this
	DECLARE hallprefix		CHAR(1) DEFAULT "H";	# Halls SIP URIs starts with this
	DECLARE zipprefix		CHAR(1) DEFAULT "-";	# Halls SIP URIs has this between country code and zip code
	DECLARE pass 			VARCHAR(16) DEFAULT "";	# Random SIP pw
	DECLARE new_sip_id 		VARCHAR(16);			# Random SIP user
	DECLARE db_index		INT;					# id in db
	DECLARE url_param 		VARCHAR(16);			# URL parameter to get SIP credentials
	DECLARE c_phone 		VARBINARY(16);			# Encrypted phone
	DECLARE c_name 			VARBINARY(256);			# Encrypted name
	DECLARE c_pass 			VARBINARY(16);			# Encrypted SIP pw
	DECLARE c_sipid 		VARBINARY(16);			# Encrypted SIP id
	DECLARE old_sip_id		VARCHAR(16);
	DECLARE s_key 			VARBINARY(20) DEFAULT UNHEX(SHA(key_));	# Hash of encryption key
	DECLARE regex_phone 	VARCHAR(64);
	DECLARE regex_hall  	VARCHAR(64);
	DECLARE regex_email 	VARCHAR(64);
	DECLARE ccode			VARCHAR(4);
	CALL regex_patterns(regex_phone,@notused1,regex_hall,regex_email);

# Check if valid phone number
	IF e164 NOT REGEXP regex_phone THEN
		CALL ERROR_not_E.164_number_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

	IF hall NOT REGEXP regex_hall THEN
		CALL ERROR_not_valid_hall_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if valid email
	IF e_mail NOT REGEXP regex_email THEN
		CALL ERROR_not_valid_email_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Create SIP secret
	SET pass = create_password(slength);

# Create a random, unique SIP account number 
	REPEAT
		SET @a = FLOOR(RAND()*99999999);
		IF LENGTH(@a) < alength THEN
			SET @a = LPAD(@a,alength,'0');
		END IF;
		SET new_sip_id = CONCAT(listenerprefix,@a);
		SET c_sipid = AES_ENCRYPT(new_sip_id,s_key);
	UNTIL (SELECT id FROM callers WHERE sip_id = c_sipid) IS NULL END REPEAT; # Be sure it is unique

# Encrypt 
	SET c_phone = AES_ENCRYPT(e164,s_key);
	SET c_name = AES_ENCRYPT(TRIM(name),s_key);
	SET c_pass = AES_ENCRYPT(pass,s_key);

# Insert or update records in callers db
	INSERT INTO callers (updated,updater,phone,label,sip_id,sip_pw) VALUES (
		CURRENT_TIMESTAMP,
		hall,
		c_phone,
		c_name,
		c_sipid,
		c_pass
	)
	ON DUPLICATE KEY UPDATE  # new_sip_id (c_sipid) is not used if existing phone number, old sip_id remain
		updated = CURRENT_TIMESTAMP,
		updater = hall,
		label = c_name,
		sip_pw = c_pass;


# Get index number in callers db
	SET db_index = (SELECT id FROM callers WHERE phone = c_phone);

# Get existing sip_id (or null if none)
    SET old_sip_id = (SELECT sip_id FROM callers WHERE id = db_index);

# If sipid is null store new sipid (if phonenumber was alredy present, but without sipid)
	IF (old_sip_id) IS NULL THEN
		UPDATE callers SET sip_id = c_sipid WHERE id = db_index; # insert new sip_id
	ELSE
		SET new_sip_id = AES_DECRYPT(old_sip_id,s_key); # Get old sip_id
	END IF;

# Generate URL parameter for SIP credentials retrivial
	SET url_param = CONV(CRC32(RIGHT(e164,mlength))+db_index,10,36);

# Get phone country code from hall id - Example: "H45-1234" returns "45"
	SET ccode = replace(substr(hall,'1',locate(zipprefix,hall)-1),hallprefix,'');

# Add to list of new SIP records to be created/updated in Asterisk and email to be sent
	INSERT INTO new_accounts (id,e_mail,url_param,sip_id,sip_pw,countrycode,mail_text) VALUES (
		db_index,
		e_mail,
		url_param,
		new_sip_id,
		pass,
		ccode,
		"x"
	)
	ON DUPLICATE KEY UPDATE
		e_mail = e_mail,
		url_param = url_param,
		sip_id = new_sip_id,
		sip_pw = pass,
		countrycode = ccode,
		mail_text = "x";
END$$

# ======== Create password
delimiter $$
CREATE DEFINER=`root`@`localhost` FUNCTION `create_password`(
	slength INT # password length 4-16
) RETURNS text CHARSET latin1
BEGIN
	DECLARE pass VARCHAR(16) DEFAULT "";	# Random SIP pw

	SET @icap = FLOOR(RAND() * slength) + 1; # index of capital letter
	REPEAT
		SET @cap = CONV(10+FLOOR(RAND()*26),10,36); # capital letter
	UNTIL @cap NOT REGEXP "[IO]" END REPEAT;		# avoid capital i and o
	REPEAT
		SET @isym = FLOOR(RAND() * (slength - 2)) + 2; # index of symbol
	UNTIL @isym != @icap END REPEAT;
	CASE FLOOR(RAND()*5)
		WHEN '0' THEN SET @sym = "&"; # Allowed symbols according to RFC 3261
		WHEN '1' THEN SET @sym = "=";
		WHEN '2' THEN SET @sym = "+";
		WHEN '3' THEN SET @sym = "$";
		WHEN '4' THEN SET @sym = ",";
	END CASE;
	REPEAT
		SET @len = slength;
		SET pass = "";
		WHILE @len > 0 DO
			CASE @len
				WHEN @icap THEN SET pass = CONCAT(pass,@cap);
				WHEN @isym THEN SET pass = CONCAT(pass,@sym);
				ELSE
					REPEAT
						SET @noncap = LOWER(CONV(FLOOR(RAND()*36),10,36));
					UNTIL @noncap NOT REGEXP "[01ol]" END REPEAT; # avoid 0,1,o,l
					SET pass = CONCAT(pass,CONVERT(@noncap USING utf8));
			END CASE;
			SET @len = @len - 1;
		END WHILE;
	UNTIL pass REGEXP "[0-9]" AND pass REGEXP BINARY "[a-z]" END REPEAT; # Be sure there are digits and lowercase letters
RETURN pass;
END$$


# ======== Get call info
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_call_info`(
	IN num VARCHAR(16),
	IN hall VARCHAR(16),
	IN key_ VARCHAR(40),
	OUT e164 VARCHAR(16),
	OUT namestr VARCHAR(256) CHARACTER SET utf8,
	OUT type_ CHAR(1)
)
BEGIN
	DECLARE n_id INTEGER;
	DECLARE s_key VARBINARY(20);
	DECLARE c_num VARBINARY(16);
	DECLARE c_name VARBINARY(256);
	DECLARE start_char CHAR(1);
	DECLARE regex_phone VARCHAR(64);
	DECLARE regex_computer VARCHAR(64);
	DECLARE regex_hall VARCHAR(64);
	CALL regex_patterns(regex_phone,regex_computer,regex_hall,@notused);

	IF num NOT REGEXP BINARY CONCAT(regex_phone,"|",regex_computer,"|",regex_hall) THEN
		SET namestr = "*ERROR* not a valid number format";
		SET type_ = "E"; # Error
	ELSE
		SET s_key = UNHEX(SHA(key_));
		SET c_num = AES_ENCRYPT(num,s_key);
		# If phone call
		IF num REGEXP regex_phone THEN
			SELECT id INTO n_id FROM callers WHERE phone = c_num;
			IF n_id IS NULL THEN # If phone number is not in db
				INSERT INTO callers (updated,updater,phone,label) VALUES (
					CURRENT_TIMESTAMP,
					hall,
					c_num,
					AES_ENCRYPT("",s_key) # missing name, empty string
				);
			ELSE # If phone number is in db
				UPDATE callers SET updated = CURRENT_TIMESTAMP WHERE phone = c_num;
			END IF;
			SELECT label INTO c_name FROM callers WHERE phone = c_num;
			SET namestr = AES_DECRYPT(c_name,s_key);
			SET e164 = num;
			SET type_= "P"; # Phone
		# If computer call or hall
		ELSE
			SELECT id INTO n_id FROM callers WHERE sip_id = c_num;
			IF n_id IS NULL THEN # If SIP number is not in db
				SET namestr = "*ERROR* SIP account not in db";
				SET type_ = "E"; # Error
			ELSE
				UPDATE callers SET updated = CURRENT_TIMESTAMP WHERE sip_id = c_num;
				SELECT phone, label INTO c_num, c_name FROM callers WHERE sip_id = c_num;
				SET namestr = AES_DECRYPT(c_name,s_key);
				SET e164 = AES_DECRYPT(c_num,s_key);
				IF num REGEXP regex_computer THEN
					SET type_ = "C"; # Computer
				ELSEIF num REGEXP regex_hall THEN
					SET type_ = "H"; # Hall
				ELSE
					SET type_ = "E"; # Error
					SET namestr = "*ERROR* Mismatch in regex";
				END IF;
			END IF;
		END IF;
	END IF;
	#SELECT e164,namestr,type_;
END$$

# ======== Get hall info
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_hall_info`(
	IN sipid VARCHAR(16), #  "#45-8654"
	IN key_ VARCHAR(40),
	OUT hall_country_ VARCHAR(10), # phone country code
	OUT hall_zip_ VARCHAR(9),
	OUT hall_city VARCHAR(256) CHARACTER SET utf8,
	OUT hall_number_ VARCHAR(2),
	OUT dialin VARCHAR(16),
	OUT name VARCHAR(256) CHARACTER SET utf8,
	OUT email VARCHAR(256) CHARACTER SET utf8,
	OUT phone VARCHAR(16),
	OUT comment VARCHAR(256) CHARACTER SET utf8
)
BEGIN
	DECLARE c_sipid VARBINARY(16);			# Encrypted name
	DECLARE c_name VARBINARY(256);			# Encrypted name
	DECLARE c_email VARBINARY(256);			# Encrypted email
	DECLARE c_phone VARBINARY(16);			# Encrypted phone
	DECLARE c_user_txt VARBINARY(256);		# Encrypted comment
	DECLARE c_city VARBINARY(256);
	DECLARE c_dialin VARBINARY(16);
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));	# Hash of encryption key

	SET c_sipid = AES_ENCRYPT(sipid,s_key);
	SELECT
		contacts.contact_name,
		contacts.contact_email,
		contacts.contact_phone,
		contacts.user_comment,
		callers.label,
		contacts.hall_country,
		contacts.hall_zip,
		contacts.hall_number,
		callers.phone
	INTO
		c_name,
		c_email,
		c_phone,
		c_user_txt,
		c_city,
		hall_country_,
		hall_zip_,
		hall_number_,
		c_dialin
	FROM contacts 
	INNER JOIN callers
	ON contacts.sip_id = callers.sip_id
	WHERE contacts.sip_id = c_sipid;
	SET name = AES_DECRYPT(c_name,s_key);
	SET email = AES_DECRYPT(c_email,s_key);
	SET phone = AES_DECRYPT(c_phone,s_key);
	SET comment = AES_DECRYPT(c_user_txt,s_key);
	SET hall_city = AES_DECRYPT(c_city,s_key);
	SET dialin = AES_DECRYPT(c_dialin,s_key);

	#SELECT hall_country_,hall_zip_,hall_city,hall_number_,dialin,name,email,phone,comment;
END$$

# ======== Get SIP credentials
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_sip_credentials`(
	IN input_phone VARCHAR(32),
	IN url_param VARCHAR(16),
	IN key_ VARCHAR(40),
	OUT user_phone VARCHAR(16),
	OUT user_name VARCHAR(256),
	OUT user_sip_id VARCHAR(16),
	OUT user_sip_pw VARCHAR(16)
)
BEGIN
	DECLARE mlength INT DEFAULT 6;	# Minimum match of x last digits in phone number
	DECLARE i_phone VARCHAR(16);
	DECLARE c_phone VARBINARY(16);
	DECLARE c_name VARBINARY(256);
	DECLARE c_sip_id VARBINARY(16);
	DECLARE c_sip_pw VARBINARY(16);
	DECLARE db_index INT;
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));

	SET i_phone = trim_phone_number(input_phone);
	SET db_index = (CONV(url_param,36,10) - CRC32(RIGHT(i_phone,mlength)));
	SELECT
		phone,
		label,
		sip_id,
		sip_pw
	INTO
		c_phone,
		c_name,
		c_sip_id,
		c_sip_pw
	FROM callers WHERE id = db_index;
	SET user_phone = AES_DECRYPT(c_phone,s_key);
	IF (c_phone IS NOT NULL) AND (RIGHT(user_phone,LENGTH(i_phone)) = i_phone) THEN
		SET user_name = CONVERT(AES_DECRYPT(c_name,s_key) USING utf8);
		SET user_sip_id = AES_DECRYPT(c_sip_id,s_key);
		SET user_sip_pw = AES_DECRYPT(c_sip_pw,s_key);
	ELSE
		SET user_phone = NULL;
		SET user_name = NULL;
		SET user_sip_id = NULL;
		SET user_sip_pw = NULL;
	END IF;
	#SELECT user_phone,user_name,user_sip_id,user_sip_pw;
END$$

# ======== Pop new account
delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `pop_new_account`(
	IN erase BOOLEAN  # FALSE for debug to avoid deleting from queue
)
BEGIN
	SELECT 							# Return the oldest entry from the queue
		id,
		sip_id,
		sip_pw,
		countrycode
	FROM new_accounts
	ORDER BY id ASC LIMIT 1;
		
	IF erase = TRUE THEN
		DELETE FROM					# Delete the oldest entry from the queue
			new_accounts
		WHERE id IS NOT NULL		# WHERE nessesary because of safe mode
		ORDER BY id ASC LIMIT 1;  
	END IF;
END$$

# ======== Rename caller
delimiter $$
CREATE DEFINER=`root`@`localhost` FUNCTION `rename_caller`(
	e164 VARCHAR(16),
	newname VARCHAR(256) CHARACTER SET utf8,
	hall VARCHAR(16),
	key_ VARCHAR(40)
) RETURNS varchar(256) CHARSET utf8
BEGIN
	DECLARE n_id INTEGER;
	DECLARE nameout VARCHAR(256) CHARACTER SET utf8;
	DECLARE c_name VARBINARY(256);
	DECLARE s_key VARBINARY(20);
	DECLARE c_e164 VARBINARY(16);
	# Allow "+12345", "+123456789012345"
	DECLARE e164_match VARCHAR(256) DEFAULT "^\\+[0-9]{5,15}$";

	IF e164 NOT REGEXP BINARY e164_match THEN  # Check e164 syntax
		SET nameout = "*ERROR* Not valid phone number";
	ELSE
		SET s_key = UNHEX(SHA(key_));
		SET c_e164 = AES_ENCRYPT(e164,s_key);
		SELECT id INTO n_id FROM callers WHERE phone = c_e164;
		IF n_id IS NULL THEN # If phone number is not in db
			SET nameout = "*ERROR* Phone number not in db";
		ELSE # If phone number is in db
			UPDATE callers SET
				updated = CURRENT_TIMESTAMP,
				updater = hall,
				label = AES_ENCRYPT(TRIM(newname),s_key)
			WHERE phone = c_e164;
			SELECT label INTO c_name FROM callers WHERE phone = c_e164;
			SET nameout = AES_DECRYPT(c_name,s_key);
		END IF;
	END IF;
RETURN nameout; 
END$$

# ======== Trim phone number
delimiter $$
CREATE DEFINER=`root`@`localhost` FUNCTION `trim_phone_number`(
	in_num VARCHAR(32)
) RETURNS varchar(16) CHARSET latin1
BEGIN
	DECLARE s VARCHAR(32) DEFAULT "";
	DECLARE i INT;
	DECLARE c CHAR;
	DECLARE out_num VARCHAR(16) DEFAULT "";

	SET s = TRIM(in_num);
	SET c = LEFT(s,1);
	IF c = "+" THEN		# Keep +
		SET out_num = "+";
		SET s = RIGHT(s,LENGTH(s)-1);
	END IF;
	SET i = LENGTH(s);
	REPEAT
		SET c = LEFT(s,1);
		IF c REGEXP '[0-9]' THEN	# Strip all non-digits
			SET out_num = CONCAT(out_num,c);
		END IF;
		SET s = RIGHT(s,LENGTH(s)-1);
		SET i = i-1;
	UNTIL i <= 0 END REPEAT;
	IF LEFT(out_num,1) = "0" THEN # Strip leading 0
		SET out_num = RIGHT(out_num,LENGTH(out_num)-1);
	END IF;
RETURN out_num;
END$$

# ======== Update admin comment
delimiter $$
CREATE DEFINER=`root`@`localhost` FUNCTION `update_admin_comment`(
	sip_id_			VARCHAR(16),
	admin_comment_	VARCHAR(256) CHARACTER SET utf8,
	key_			VARCHAR(40)
) RETURNS int(11)
BEGIN
	DECLARE c_sipid VARBINARY(16);			# Encrypted SIP id
	DECLARE c_admin_comment VARBINARY(256);
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));	# Hash of encryption key
	DECLARE regex_hall VARCHAR(64);
	DECLARE row_cnt INT;

# Initialize regex patterns
	CALL regex_patterns(@notused1,@notused2,regex_hall,@notused3);

# Check if valid hall id
	IF sip_id_ NOT REGEXP regex_hall THEN
		CALL ERROR_not_valid_hall_id_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

	SET c_sipid = AES_ENCRYPT(sip_id_,s_key);
	SET c_admin_comment = AES_ENCRYPT(admin_comment_,s_key);

	UPDATE contacts SET
		admin_comment = c_admin_comment
	WHERE sip_id = c_sipid;
	SET row_cnt = (SELECT ROW_COUNT()); # One row expected

RETURN row_cnt;
END$$

# ======== Update contact
delimiter $$
CREATE DEFINER=`root`@`localhost` FUNCTION `update_contact`(
	sip_id_			VARCHAR(16),
	contact_name_	VARCHAR(256) CHARACTER SET utf8,	# Technical responsible
	contact_email_	VARCHAR(256) CHARACTER SET utf8,
	contact_phone_	VARCHAR(16),
	user_comment_	VARCHAR(256) CHARACTER SET utf8,
	key_			VARCHAR(40)
) RETURNS int(11)
BEGIN
	DECLARE c_sipid VARBINARY(16);			# Encrypted SIP id
	DECLARE c_contact_name VARBINARY(256);
	DECLARE c_contact_email VARBINARY(256);
	DECLARE c_contact_phone VARBINARY(16);
	DECLARE c_user_comment VARBINARY(256);
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));	# Hash of encryption key
	DECLARE regex_phone VARCHAR(64);
	DECLARE regex_hall VARCHAR(64);
	DECLARE regex_email VARCHAR(64);
	DECLARE row_cnt INT;

# Initialize regex patterns
	CALL regex_patterns(regex_phone,@notused1,regex_hall,regex_email);

# Check if valid hall id
	IF sip_id_ NOT REGEXP regex_hall THEN
		CALL ERROR_not_valid_hall_id_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if valid email
	IF contact_email_ NOT REGEXP regex_email THEN
		CALL ERROR_not_valid_email_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if E.164 phone number
	IF contact_phone_ NOT REGEXP regex_phone THEN
		CALL ERROR_not_E.164_number_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

	SET c_sipid = AES_ENCRYPT(sip_id_,s_key);
	SET c_contact_name = AES_ENCRYPT(contact_name_,s_key);
	SET c_contact_email = AES_ENCRYPT(contact_email_,s_key);
	SET c_contact_phone = AES_ENCRYPT(contact_phone_,s_key);
	SET c_user_comment = AES_ENCRYPT(user_comment_,s_key);

	UPDATE contacts SET
		updated = CURRENT_TIMESTAMP,
		contact_name = c_contact_name,
		contact_email = c_contact_email,
		contact_phone = c_contact_phone,
		user_comment = c_user_comment
	WHERE sip_id = c_sipid;
	SET row_cnt = (SELECT ROW_COUNT()); # One row expected

RETURN row_cnt;
END$$

# ======== Verify hall login
delimiter $$
CREATE DEFINER=`root`@`localhost` FUNCTION `verify_hall_login`(
	user 	VARCHAR(16),
	h_pw	VARCHAR(40), # Hashed password
	key_ 	VARCHAR(40)
) RETURNS bit(1)
BEGIN
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));
	DECLARE c_sip_pw VARBINARY(16);
	DECLARE h_sip_pw VARCHAR(40);

	SELECT sip_pw INTO c_sip_pw	FROM callers WHERE sip_id = AES_ENCRYPT(user,s_key);
	SET h_sip_pw = CONVERT(SHA1(CONCAT(AES_DECRYPT(c_sip_pw,s_key)," ",user)) USING ascii); # salted hash
	IF h_pw = h_sip_pw THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;
END$$

# ======== Write call log
delimiter $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `write_call_log`(
	IN caller VARCHAR(16),
	IN callee VARCHAR(16),
	IN activity VARCHAR(16),
	IN key_ VARCHAR(40) 
)
BEGIN
	DECLARE s_key VARBINARY(20);
	SET s_key = UNHEX(SHA(key_));

	INSERT INTO call_log (now,caller,callee,activity) VALUES (
		CURRENT_TIMESTAMP,
		AES_ENCRYPT(caller,s_key),
		AES_ENCRYPT(callee,s_key),
		activity
	);
END$$

# ======== Create Computer Listener
delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `create_computer_listener`(
	IN e164 			VARCHAR(16),  	# National phone number
	IN name 			VARCHAR(256) CHARACTER SET utf8,
	IN e_mail 			VARCHAR(256) CHARACTER SET utf8,
	IN hall 			VARCHAR(16),
	IN mailtext			TEXT CHARACTER SET utf8,
	IN key_ 			VARCHAR(40)
)
BEGIN
	DECLARE slength 		INT DEFAULT 12;			# SIP secret length
	DECLARE alength 		INT DEFAULT 8;			# SIP account length
	DECLARE mlength 		INT DEFAULT 6;			# Minimum match of x last digits in phone number when doing SIP credential retrivial
	DECLARE listenerprefix	CHAR(1) DEFAULT "L";	# Listeners SIP URIs starts with this
	DECLARE hallprefix		CHAR(1) DEFAULT "H";	# Halls SIP URIs starts with this
	DECLARE zipprefix		CHAR(1) DEFAULT "-";	# Halls SIP URIs has this between country code and zip code
	DECLARE	tokenstr		CHAR(5) DEFAULT "token";
	DECLARE httpstr			CHAR(7) DEFAULT "http://";
	DECLARE pass 			VARCHAR(16) DEFAULT "";	# Random SIP pw
	DECLARE new_sip_id 		VARCHAR(16);			# Random SIP user
	DECLARE db_index		INT;					# id in db
	DECLARE url_param 		VARCHAR(16);			# URL parameter to get SIP credentials
	DECLARE c_phone 		VARBINARY(16);			# Encrypted phone
	DECLARE c_name 			VARBINARY(256);			# Encrypted name
	DECLARE c_pass 			VARBINARY(16);			# Encrypted SIP pw
	DECLARE c_sipid 		VARBINARY(16);			# Encrypted SIP id
	DECLARE old_sip_id		VARCHAR(16);
	DECLARE s_key 			VARBINARY(20) DEFAULT UNHEX(SHA(key_));	# Hash of encryption key
	DECLARE regex_phone 	VARCHAR(64);
	DECLARE regex_hall  	VARCHAR(64);
	DECLARE regex_email 	VARCHAR(64);
	DECLARE ccode			VARCHAR(4);
	DECLARE emailurl		VARCHAR(256);
	DECLARE email_subject	TEXT CHARACTER SET utf8;
	CALL regex_patterns(regex_phone,@notused1,regex_hall,regex_email);

# Check if valid phone number
	IF e164 NOT REGEXP regex_phone THEN
		CALL ERROR_not_E.164_number_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

	IF hall NOT REGEXP regex_hall THEN
		CALL ERROR_not_valid_hall_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if valid email
	IF e_mail NOT REGEXP regex_email THEN
		CALL ERROR_not_valid_email_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Create SIP secret
	SET pass = create_password(slength);

# Create a random, unique SIP account number 
	REPEAT
		SET @a = FLOOR(RAND()*99999999);
		IF LENGTH(@a) < alength THEN
			SET @a = LPAD(@a,alength,'0');
		END IF;
		SET new_sip_id = CONCAT(listenerprefix,@a);
		SET c_sipid = AES_ENCRYPT(new_sip_id,s_key);
	UNTIL (SELECT id FROM callers WHERE sip_id = c_sipid) IS NULL END REPEAT; # Be sure it is unique

# Encrypt 
	SET c_phone = AES_ENCRYPT(e164,s_key);
	SET c_name = AES_ENCRYPT(TRIM(name),s_key);
	SET c_pass = AES_ENCRYPT(pass,s_key);

# Insert or update records in callers db
	INSERT INTO callers (updated,updater,phone,label,sip_id,sip_pw) VALUES (
		CURRENT_TIMESTAMP,
		hall,
		c_phone,
		c_name,
		c_sipid,
		c_pass
	)
	ON DUPLICATE KEY UPDATE  # new_sip_id (c_sipid) is not used if existing phone number, old sip_id remain
		updated = CURRENT_TIMESTAMP,
		updater = hall,
		label = c_name,
		sip_pw = c_pass;


# Get index number in callers db
	SET db_index = (SELECT id FROM callers WHERE phone = c_phone);

# Get existing sip_id (or null if none)
    SET old_sip_id = (SELECT sip_id FROM callers WHERE id = db_index);

# If sipid is null store new sipid (if phonenumber was alredy present, but without sipid)
	IF (old_sip_id) IS NULL THEN
		UPDATE callers SET sip_id = c_sipid WHERE id = db_index; # insert new sip_id
	ELSE
		SET new_sip_id = AES_DECRYPT(old_sip_id,s_key); # Get old sip_id
	END IF;

# Generate URL parameter for SIP credentials retrivial
	SET url_param = CONV(CRC32(RIGHT(e164,mlength))+db_index,10,36);

# Get phone country code from hall id - Example: "H45-1234" returns "45"
	SET ccode = replace(substr(hall,'1',locate(zipprefix,hall)-1),hallprefix,'');

# Add to list of new SIP records to be created/updated in Asterisk
	INSERT INTO new_accounts (id,sip_id,sip_pw,countrycode) VALUES (
		db_index,
		new_sip_id,
		pass,
		ccode
	)
	ON DUPLICATE KEY UPDATE
		sip_id = new_sip_id,
		sip_pw = pass,
		countrycode = ccode;

# Add to mail queue
	SET email_subject = CONCAT(SUBSTRING_INDEX(name,' ',1),' (',REPLACE(hall,hallprefix,''),')');
	SET mailtext = CONCAT(mailtext,'\n\n',httpstr,@@hostname,"?",tokenstr,"=",url_param,'\n');
	INSERT INTO mail_queue (e_mail,e_mail_subject,e_mail_body) VALUES (
		e_mail,
		email_subject,
		mailtext
	);
END$$

delimiter $$

CREATE DEFINER=`root`@`localhost` FUNCTION `recover_password`(
	req_name			TEXT CHARACTER SET utf8,	# requesters name
	req_phone			VARCHAR(16),  				# requesters phone no.
	req_email			VARCHAR(255),				# requesters email
	hallcity			TEXT CHARACTER SET utf8,	# city where hall is located
	hallphone			VARCHAR(16),  				# telesal phone no. including plus and countrycode
	email_subject		TEXT CHARACTER SET utf8,	# {%1}=hallcity
	email_body			TEXT CHARACTER SET utf8,	# {%1}=name, {%2}=req_phone, {%3}=req_email, {%4}=hallphone, {%5}=sip_pw
	req_email_body		TEXT CHARACTER SET utf8,	# {%1}=hallphone, {%2}=sip_id, {%3}=contact_name, {%4}=contact_phone, {%5}=contact_email
	key_				VARCHAR(40)
) RETURNS int(11)
BEGIN
	DECLARE c_dialin_phone VARBINARY(16); # Encrypted dialin no.
	DECLARE city VARCHAR(256);
	DECLARE sip_pw VARCHAR(16);
	DECLARE name VARCHAR(256);
	DECLARE contact_email VARCHAR(256);
	DECLARE contact_name VARCHAR(256);
	DECLARE user_comment VARCHAR(256);
	DECLARE sip_id VARCHAR(16);
	DECLARE contact_phone VARCHAR(16);

	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));	# Hash of encryption key

	SET c_dialin_phone = AES_ENCRYPT(hallphone,s_key);
	SELECT
		CONVERT(AES_DECRYPT(callers.label,s_key) USING utf8),
		CONVERT(AES_DECRYPT(contacts.sip_id,s_key) USING utf8),
		CONVERT(AES_DECRYPT(callers.sip_pw,s_key) USING utf8),
		CONVERT(AES_DECRYPT(contacts.contact_name,s_key) USING utf8),
		CONVERT(AES_DECRYPT(contacts.contact_email,s_key) USING utf8),
		CONVERT(AES_DECRYPT(contacts.contact_phone,s_key) USING utf8)
	INTO
		city,
		sip_id,
		sip_pw,
		contact_name,
		contact_email,
		contact_phone
	FROM contacts
	INNER JOIN callers
	ON contacts.sip_id = callers.sip_id
	WHERE callers.phone = c_dialin_phone;

	IF contact_email IS NOT NULL THEN	# If a valid hall phone number and contact person is found
		SET email_subject = REPLACE(email_subject,'{%1}',hallcity);
		SET email_body = REPLACE(email_body,'{%1}',req_name);
		SET email_body = REPLACE(email_body,'{%2}',req_phone);
		SET email_body = REPLACE(email_body,'{%3}',req_email);
		SET email_body = REPLACE(email_body,'{%4}',hallphone);
		SET email_body = REPLACE(email_body,'{%5}',sip_pw);
		SET req_email_body = REPLACE(req_email_body,'{%1}',hallphone);
		SET req_email_body = REPLACE(req_email_body,'{%2}',sip_id);
		SET req_email_body = REPLACE(req_email_body,'{%3}',contact_name);
		SET req_email_body = REPLACE(req_email_body,'{%4}',contact_phone);
		SET req_email_body = REPLACE(req_email_body,'{%5}',contact_email);
		INSERT INTO mail_queue (e_mail,e_mail_subject,e_mail_body) VALUES (
			contact_email,
			email_subject,
			email_body
		);
		INSERT INTO mail_queue (e_mail,e_mail_subject,e_mail_body) VALUES (
			req_email,
			email_subject,
			req_email_body
		);
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;
END$$

delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `pop_mail_queue`(
	IN erase BOOLEAN  # FALSE for debug to avoid deleting from queue
)
BEGIN
	SELECT 							# Return the oldest entry from the queue
		id,
		e_mail,
		e_mail_subject,
		e_mail_body
	FROM mail_queue
	ORDER BY id ASC LIMIT 1;
		
	IF erase = TRUE THEN
		DELETE FROM					# Delete the oldest entry from the queue
			mail_queue
		WHERE id IS NOT NULL		# WHERE nessesary because of safe mode
		ORDER BY id ASC LIMIT 1;  
	END IF;
END$$



delimiter $$

CREATE DEFINER=`root`@`localhost` PROCEDURE `search_hall`(
	IN search VARCHAR(256),
	IN key_ VARCHAR(40)
)
BEGIN
	DECLARE search_str VARCHAR(256);
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));

	CREATE TEMPORARY TABLE IF NOT EXISTS tmp_list AS SELECT
		contacts.id,
		contacts.updated,
		CONVERT(AES_DECRYPT(callers.label,s_key) USING utf8) AS city,
		CONVERT(AES_DECRYPT(contacts.sip_id,s_key) USING utf8) AS sip_id,
		CONVERT(AES_DECRYPT(callers.sip_pw,s_key) USING utf8) AS sip_pw,
		CONVERT(AES_DECRYPT(callers.phone,s_key) USING utf8) AS dialin_phone,
		CONVERT(AES_DECRYPT(contacts.contact_name,s_key) USING utf8) AS contact_name,
		CONVERT(AES_DECRYPT(contacts.contact_email,s_key) USING utf8) AS contact_email,
		CONVERT(AES_DECRYPT(contacts.contact_phone,s_key) USING utf8) AS contact_phone,
		CONVERT(AES_DECRYPT(contacts.user_comment,s_key) USING utf8) AS user_comment,
		CONVERT(AES_DECRYPT(contacts.admin_comment,s_key) USING utf8) AS admin_comment
	FROM contacts
	INNER JOIN callers
	ON contacts.sip_id = callers.sip_id;
	
	SET search_str = REPLACE(search,' ',''); -- Strip spaces before compare
	SET search_str = CONCAT('%',search_str,'%'); -- Put wildcards at the ends

	SELECT updated, sip_id, dialin_phone, contact_name, contact_email, contact_phone, user_comment, admin_comment
	FROM tmp_list
	WHERE
		REPLACE(city,' ','') LIKE search_str OR -- Strip spaces before compare
		REPLACE(sip_id,' ','') LIKE search_str OR
		REPLACE(dialin_phone,' ','') LIKE search_str OR
		REPLACE(contact_name,' ','') LIKE search_str OR
		REPLACE(contact_email,' ','') LIKE search_str OR
		REPLACE(contact_phone,' ','') LIKE search_str OR
		REPLACE(user_comment,' ','') LIKE search_str OR
		REPLACE(admin_comment,' ','') LIKE search_str OR
		sip_pw LIKE search	-- Excact match on pw!
	ORDER BY sip_id;

	DROP TABLE tmp_list;
END$$