DROP DATABASE IF EXISTS telesal;
CREATE DATABASE telesal;
USE telesal;

CREATE TABLE `call_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `now` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `caller` varbinary(16) DEFAULT NULL,
  `callee` varbinary(16) DEFAULT NULL,
  `activity` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `now` (`now`),
  KEY `caller` (`caller`),
  KEY `callee` (`callee`),
  KEY `activity` (`activity`)
) ENGINE=InnoDB AUTO_INCREMENT=3160 DEFAULT CHARSET=latin1;

--
-- Table structure for table `caller_event_log`
--
CREATE TABLE `caller_event_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `now` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `hall` varchar(16) DEFAULT NULL,
  `caller` varbinary(16) DEFAULT NULL,
  `activity` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `now` (`now`),
  KEY `hall` (`hall`),
  KEY `caller` (`caller`),
  KEY `activity` (`activity`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Table structure for table `callers`
--
CREATE TABLE `callers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updater` varchar(16) NOT NULL,
  `phone` varbinary(16) NOT NULL,
  `label` varbinary(256) NOT NULL,
  `sip_id` varbinary(16) DEFAULT NULL,
  `sip_pw` varbinary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `sip_id` (`sip_id`),
  KEY `updated` (`updated`)
) ENGINE=InnoDB AUTO_INCREMENT=192 DEFAULT CHARSET=latin1;


--
-- Table structure for table `contacts`
--
DROP TABLE IF EXISTS `contacts`;
CREATE TABLE `contacts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sip_id` varbinary(16) DEFAULT NULL,
  `hall_country` varchar(3) DEFAULT NULL,
  `hall_zip` varchar(9) DEFAULT NULL,
  `hall_number` varchar(2) DEFAULT NULL,
  `contact_name` varbinary(256) DEFAULT NULL,
  `contact_email` varbinary(256) DEFAULT NULL,
  `contact_phone` varbinary(16) DEFAULT NULL,
  `user_comment` varbinary(256) DEFAULT NULL,
  `admin_comment` varbinary(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sip_id` (`sip_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;

--
-- Table structure for table `mail_queue`
--
CREATE TABLE `mail_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `e_mail` varchar(256) DEFAULT NULL,
  `e_mail_subject` text,
  `e_mail_body` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Table structure for table `sipid_queue`
--
CREATE TABLE `sipid_queue` (
  `id` int(11) NOT NULL,
  `sip_id` varchar(16) DEFAULT NULL,
  `sip_pw` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DELIMITER ;;
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
END ;;
DELIMITER ;

DELIMITER ;;
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

	IF LENGTH(contact_email) >= 5 THEN  # If a valid contact person is found for the hall number
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
		SET email_subject = HEX(email_subject); # Convert to hex as BASH can't do unicode
		SET email_body = HEX(email_body);
		SET req_email_body = HEX(req_email_body);
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
		SET contact_email = 'support@telesal.org';
		SET email_subject = HEX('Failed request for password');
		SET email_body = HEX(CONCAT('Failed request entered by: ',req_name,', ',req_phone,', ',req_email,'. Entered hall and phone: ',hallcity,' ',hallphone));
		SET req_email_body = HEX(CONCAT('The information you entered cannot be validated. You may contact support@telesal.org.'));
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
	END IF;
END ;;
DELIMITER ;

DELIMITER ;;
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
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `verify_hall_login`(
	user 	VARCHAR(16),
	h_pw	VARCHAR(40), # Hashed value = sha1(<password> + " " + user)
	key_ 	VARCHAR(40)
) RETURNS bit(1)
BEGIN
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));
	DECLARE c_sip_pw VARBINARY(16);
	DECLARE h_sip_pw VARCHAR(40);
	DECLARE hall_prefix CHAR DEFAULT "H";

	IF LOCATE(hall_prefix,user) <> 1 THEN # If not a hall
		RETURN FALSE;
	ELSE
		SELECT sip_pw INTO c_sip_pw	FROM callers WHERE sip_id = AES_ENCRYPT(user,s_key);
		SET h_sip_pw = CONVERT(SHA1(CONCAT(AES_DECRYPT(c_sip_pw,s_key)," ",user)) USING ascii); # salted hash
		IF h_pw = h_sip_pw THEN
			RETURN TRUE;
		ELSE
			RETURN FALSE;
		END IF;
	END IF;
END ;;
DELIMITER ;

DELIMITER ;;
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
END ;;
DELIMITER ;

DELIMITER ;;
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
END ;;
DELIMITER ;

DELIMITER ;;
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
END ;;
DELIMITER ;

DELIMITER ;;
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
	DECLARE alength 		INT DEFAULT 9;			# SIP account length
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

# Check if valid hall id
	IF hall NOT REGEXP regex_hall THEN
		CALL ERROR_not_valid_hall_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if valid email
	IF e_mail NOT REGEXP regex_email THEN
		CALL ERROR_not_valid_email_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Encrypt 
	SET c_phone = AES_ENCRYPT(e164,s_key);

# Ensure that the phone number is not the number of a hall
	IF ((SELECT LEFT(CONVERT(AES_DECRYPT(sip_id,s_key) USING utf8),1) FROM callers WHERE phone = c_phone) = hallprefix) THEN
		CALL ERROR_hall_number_not_allowed_here; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Create a random, unique SIP account number; first digit(s) is the phone country code
	SET ccode = replace(substr(hall,'1',locate(zipprefix,hall)-1),hallprefix,''); # Get phone country code from hall id - Example: "H45-1234" returns "45"
	REPEAT
		SET @l = alength - LENGTH(ccode);			# Length of random number
		SET @a = FLOOR(RAND() * REPEAT('9',@l));	# Random number
		IF LENGTH(@a) < @l THEN
			SET @a = LPAD(@a,@l,'0');
		END IF;
		SET new_sip_id = CONCAT(listenerprefix,ccode,@a);
		SET c_sipid = AES_ENCRYPT(new_sip_id,s_key);			
	UNTIL (SELECT id FROM callers WHERE sip_id = c_sipid) IS NULL END REPEAT; # Be sure it is unique
																			# WARNING! can be endless if db full, should have an error exit
# Create SIP secret
	SET pass = create_password(slength);

# Encrypt 
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

# Add to list of new SIP records to be created/updated in Asterisk
	INSERT INTO sipid_queue (id,sip_id,sip_pw) VALUES (
		db_index,
		new_sip_id,
		pass
	)
	ON DUPLICATE KEY UPDATE
		sip_id = new_sip_id,
		sip_pw = pass;

# Generate URL parameter for SIP credentials retrivial
	SET url_param = CONV(CRC32(RIGHT(e164,mlength))+db_index,10,36);

# Add to mail queue
	SET email_subject = CONCAT(SUBSTRING_INDEX(name,' ',1),' (',SUBSTR(hall,2),')'); # Firstname and hall ID
	SET mailtext = CONCAT(mailtext,'\n\n',httpstr,@@hostname,"?",tokenstr,"=",url_param,'\n');
	SET email_subject = HEX(email_subject); # Convert to hex as BASH can't do unicode
	SET mailtext = HEX(mailtext);
	INSERT INTO mail_queue (e_mail,e_mail_subject,e_mail_body) VALUES (
		e_mail,
		email_subject,
		mailtext
	);
END ;;
DELIMITER ;

DELIMITER ;;
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
END ;;
DELIMITER ;

DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_caller_event_log`(
	IN days INT,			# How many days to look back from today
	IN key_ VARCHAR(40)
)
BEGIN
	DECLARE s_key VARBINARY(20);
	SET s_key = UNHEX(SHA(key_));

	SELECT 
		now,
		hall,
		CONVERT(AES_DECRYPT(caller,s_key) USING utf8) AS caller,
		activity
	FROM caller_event_log WHERE now BETWEEN DATE_SUB(CURDATE(), INTERVAL days DAY) AND NOW() ORDER BY now DESC;
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_call_log`(
	IN days INT,			# How many days to look back from today
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
	FROM call_log WHERE now BETWEEN DATE_SUB(CURDATE(), INTERVAL days DAY) AND NOW() ORDER BY now DESC;
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_new_accounts`(
#	IN key_ VARCHAR(40)
	)
BEGIN
#	DECLARE s_key VARBINARY(20);
#	SET s_key = UNHEX(SHA(key_));

	SELECT 
		id,
		sip_id,
		sip_pw
	FROM sipid_queue;
END ;;
DELIMITER ;


DELIMITER ;;
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
END ;;
DELIMITER ;


DELIMITER ;;
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
	IF contact_phone_ NOT REGEXP regex_phone AND contact_phone_ <> "" THEN
		CALL ERROR_not_E.164_number_format; # erstat med SIGNAL i MySQL 5.5+
	END IF;

# Check if valid email format
	IF contact_email_ NOT REGEXP regex_email AND contact_email_ <> "" THEN
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

END ;;
DELIMITER ;


DELIMITER ;;
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
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_call_durations`(
	IN callID VARCHAR(16),	# Caller ID, or first part of it, e.g. H45 or +45
	IN days INT,			# How many days to look back from today
	IN key_ VARCHAR(40)
)
BEGIN
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));

	CREATE TEMPORARY TABLE calls_temp AS (
		SELECT 
			now,
			CONVERT(AES_DECRYPT(caller,s_key) USING utf8) AS callerID,
			CONVERT(AES_DECRYPT(callee,s_key) USING utf8) AS calleeID,
			activity
		FROM call_log WHERE activity = 'call' AND now BETWEEN DATE_SUB(CURDATE(), INTERVAL days DAY) AND NOW() ORDER BY now ASC
	);

	CREATE TEMPORARY TABLE hangups_temp AS (
		SELECT 
			now,
			CONVERT(AES_DECRYPT(caller,s_key) USING utf8) AS callerID,
			CONVERT(AES_DECRYPT(callee,s_key) USING utf8) AS calleeID,
			activity
		FROM call_log WHERE activity = 'hangup' AND now BETWEEN DATE_SUB(CURDATE(), INTERVAL days DAY) AND NOW() ORDER BY now ASC
	);

	SELECT
		calls_temp.callerID,
		calls_temp.calleeID,
		TIMEDIFF(hangups_temp.now,calls_temp.now) AS duration,
		calls_temp.now AS `call`,
		hangups_temp.now AS hangup
	FROM calls_temp
	LEFT JOIN hangups_temp
	ON calls_temp.callerID = hangups_temp.callerID AND calls_temp.calleeID = hangups_temp.calleeID AND calls_temp.now < hangups_temp.now
	WHERE calls_temp.callerID LIKE CONCAT(callID,'%')
	GROUP BY calls_temp.callerID, calls_temp.calleeID, calls_temp.now
	ORDER BY calls_temp.now;

	DROP TABLE calls_temp, hangups_temp;
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `pop_new_account`(
	IN erase BOOLEAN  # FALSE for debug to avoid deleting from queue
)
BEGIN
	SELECT 							# Return the oldest entry from the queue
		id,
		sip_id,
		sip_pw
	FROM sipid_queue
	ORDER BY id ASC LIMIT 1;
		
	IF erase = TRUE THEN
		DELETE FROM					# Delete the oldest entry from the queue
			sipid_queue
		WHERE id IS NOT NULL		# WHERE nessesary because of safe mode
		ORDER BY id ASC LIMIT 1;  
	END IF;
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `read_call_log`(
	IN phone VARCHAR(16),
	IN key_ VARCHAR(40) 
)
BEGIN
	DECLARE c_phone VARBINARY(16);
	DECLARE s_key VARBINARY(20);
	SET s_key = UNHEX(SHA(key_));

	SET c_phone	= AES_ENCRYPT(phone,s_key);
	SELECT * FROM call_log WHERE
		caller = c_phone OR callee = c_phone;
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `regex_patterns`(
	OUT regex_phone VARCHAR(64),
	OUT regex_computer VARCHAR(64),
	OUT regex_hall VARCHAR(64),
	OUT regex_email VARCHAR(64)
)
BEGIN
	SET regex_phone		= "^\\+[0-9]{5,15}$";								#Example: +46123456789
	SET regex_computer	= "^L[0-9]{9}$";									#Example: L12345678
	SET regex_hall		= "^H[0-9]{1,3}-[A-Z0-9]{3,9}(-([A-Z0-9]{1,2}))?$";	#Example: H45-8654, H45-2400-10, H45-2400-C
	SET regex_email		= "^[^@ ]{1,}@{1}[^@ ]{1,}[\.]{1}[^@ \.]{2,}$";		#Example: a.b@c.d.ef
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `write_caller_events_log`(
	IN hall VARCHAR(16),
	IN caller VARCHAR(16),
	IN activity VARCHAR(16),
	IN key_ VARCHAR(40) 
)
BEGIN
	DECLARE s_key VARBINARY(20);
	SET s_key = UNHEX(SHA(key_));

	INSERT INTO caller_event_log (now,hall,caller,activity) VALUES (
		CURRENT_TIMESTAMP,
		hall,
		AES_ENCRYPT(caller,s_key),
		activity
	);
END ;;
DELIMITER ;


DELIMITER ;;
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
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DEBUG_show_number_of_calls`(
	IN hall VARCHAR(16),	# Hall ID, or first part of it, e.g. H45
	IN days INT,			# How many days to look back from today
	IN key_ VARCHAR(40)
)
BEGIN
	DECLARE s_key VARBINARY(20) DEFAULT UNHEX(SHA(key_));

	CREATE TEMPORARY TABLE halls_temp AS (
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
		ON contacts.sip_id = callers.sip_id
	);

	CREATE TEMPORARY TABLE calls_temp AS (
		SELECT 
			now,
			CONVERT(AES_DECRYPT(caller,s_key) USING utf8) AS caller,
			CONVERT(AES_DECRYPT(callee,s_key) USING utf8) AS callee,
			activity
		FROM call_log WHERE now BETWEEN DATE_SUB(CURDATE(), INTERVAL days DAY) AND NOW() ORDER BY now DESC
	);

	SELECT
		sip_id,
		city,
		SUM(CASE WHEN caller = sip_id AND activity = 'call' THEN 1 ELSE 0 END) AS hall_calls,
		SUM(CASE WHEN LEFT(caller,1) = '+' AND activity = 'call' THEN 1 ELSE 0 END) AS phone_calls,
		SUM(CASE WHEN LEFT(caller,1) = 'L' AND activity = 'call' THEN 1 ELSE 0 END) AS computer_calls,
		name,
		email,
		contact_phone,
		user_comment,
		admin_comment
	FROM halls_temp
	LEFT JOIN calls_temp
	ON halls_temp.dialin_phone = calls_temp.callee
	WHERE sip_id LIKE CONCAT(hall,'%')
	GROUP BY sip_id
	ORDER BY sip_id;

	DROP TABLE halls_temp, calls_temp;
END ;;
DELIMITER ;


DELIMITER ;;
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
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_hall_info`(
	IN sipid VARCHAR(16), #  "H45-8654"
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
	DECLARE c_sipid VARBINARY(16);			# Encrypted sip id
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
END ;;
DELIMITER ;


DELIMITER ;;
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
END ;;
DELIMITER ;


DELIMITER ;;
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
END ;;
DELIMITER ;


DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `show_call_log`(
	IN key_ VARCHAR(40)
)
BEGIN
	DECLARE caller_call VARBINARY(16);
	DECLARE callee_call VARBINARY(16);
	DECLARE begin_call TIMESTAMP;
	DECLARE end_call TIMESTAMP;
	DECLARE dummy VARBINARY(16);
	DECLARE s_key VARBINARY(20);
	SET s_key = UNHEX(SHA(key_));


	DROP TABLE IF EXISTS call_log_calls;
	DROP TABLE IF EXISTS call_log_hangups;
	CREATE TABLE IF NOT EXISTS call_log_calls ENGINE=MEMORY SELECT * FROM call_log WHERE activity ='call' ORDER BY now ASC;
	CREATE TABLE IF NOT EXISTS call_log_hangups ENGINE=MEMORY SELECT * FROM call_log WHERE activity ='hangup' ORDER BY now ASC;

	CREATE TABLE IF NOT EXISTS call_log_aggr ENGINE=MEMORY 
	SELECT
		call_log_calls.id,
		call_log_calls.now AS `Start`,
		call_log_hangups.now AS `End`,
		TIMESTAMPDIFF(second, call_log_calls.now, call_log_hangups.now) / 60 AS Minutes,
#		CONVERT(AES_DECRYPT(call_log_calls.caller,s_key) USING utf8) AS `From`,
		call_log_calls.caller,
#		CONVERT(AES_DECRYPT(call_log_calls.callee,s_key) USING utf8) AS `To`
		call_log_calls.callee
		FROM call_log_calls
	LEFT JOIN call_log_hangups
	ON (call_log_calls.caller = call_log_hangups.caller) AND (call_log_calls.callee = call_log_hangups.callee) AND (call_log_calls.now < call_log_hangups.now)
	GROUP BY call_log_calls.id;

	DROP TABLE IF EXISTS call_log_calls;
	DROP TABLE IF EXISTS call_log_hangups;

	SELECT
		call_log_aggr.`Start`,
		call_log_aggr.`End`,
		call_log_aggr.Minutes,
#		call_log_aggr.caller,
		CONVERT(AES_DECRYPT(call_log_aggr.caller,s_key) USING utf8) AS `From`,
#		call_log_aggr.callee,
		CONVERT(AES_DECRYPT(call_log_aggr.callee,s_key) USING utf8) AS `To`,
#		callers.sip_id,
		CONVERT(AES_DECRYPT(callers.sip_id,s_key) USING utf8) AS `ID`,
#		callers.label
		CONVERT(AES_DECRYPT(callers.label,s_key) USING utf8) AS `Name`
	FROM call_log_aggr
	LEFT JOIN callers
	ON (call_log_aggr.callee = callers.phone) OR (call_log_aggr.caller = callers.phone);


	DROP TABLE IF EXISTS call_log_aggr;
	
END ;;
DELIMITER ;