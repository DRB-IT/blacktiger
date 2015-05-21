use telesal;

# Create halls in DB
call create_hall('45', '0000','Andeby','1','+4522000000','Test Admin','admin@test.dk','+4530000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0001','Andeby','2','+4522000001','Test Admin','admin@test.dk','+4530000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0002','Gåserup','','+4522000002','Test Admin','admin@test.dk','+4530000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0003','Langbortistan','1','+4522000003','Test Admin','admin@test.dk','+4530000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0004','Langbortistan','2','+4522000004','Test Admin','admin@test.dk','+4530000000','User Comment','Admin Comment','Admin','enckey');

# Create computer caller in DB

call create_computer_listener('+4599999900','John Doe','john@doe.dk','H45-0000-1', '', 'enckey');
call create_computer_listener('+4599999901','John Doe','john@doe.dk','H45-0001-2', '', 'enckey');
call create_computer_listener('+4599999902','John Doe','john@doe.dk','H45-0002', '', 'enckey');
call create_computer_listener('+4599999903','John Doe','john@doe.dk','H45-0003-1', '', 'enckey');
call create_computer_listener('+4599999904','John Doe','john@doe.dk','H45-0004-2', '', 'enckey');


# We don't have the methods needed for proper integration tests so we need to adjust the data manually. #

# Adjust sip-id's for test purposes
update callers set sip_id=AES_ENCRYPT('L000000000', UNHEX(SHA('enckey'))) where updater='H45-0000-1';
update callers set sip_id=AES_ENCRYPT('L000000001', UNHEX(SHA('enckey'))) where updater='H45-0001-2';
update callers set sip_id=AES_ENCRYPT('L000000002', UNHEX(SHA('enckey'))) where updater='H45-0002';
update callers set sip_id=AES_ENCRYPT('L000000003', UNHEX(SHA('enckey'))) where updater='H45-0003-1';
update callers set sip_id=AES_ENCRYPT('L000000004', UNHEX(SHA('enckey'))) where updater='H45-0004-2';

# Reset passwords for test purposes
update callers set sip_pw=AES_ENCRYPT('12345', UNHEX(SHA('enckey')))