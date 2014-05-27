use telesal;

# Create halls in DB
call create_hall('45', '0000','Andeby','1','+4500000000','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0001','Andeby','2','+4500000001','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0002','Gåserup','','+4500000002','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0003','Langbortistan','1','+4500000003','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0004','Langbortistan','2','+4500000004','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');

# Create computer caller in DB

call create_computer_listener('+4599999900','John Doe','john@doe.dk','H45-0000', '', 'enckey');
call create_computer_listener('+4599999901','John Doe','john@doe.dk','H45-0001', '', 'enckey');
call create_computer_listener('+4599999902','John Doe','john@doe.dk','H45-0002', '', 'enckey');
call create_computer_listener('+4599999903','John Doe','john@doe.dk','H45-0003', '', 'enckey');
call create_computer_listener('+4599999904','John Doe','john@doe.dk','H45-0004', '', 'enckey');
call create_computer_listener('+4599999905','John Doe','john@doe.dk','H45-0005', '', 'enckey');
call create_computer_listener('+4599999906','John Doe','john@doe.dk','H45-0006', '', 'enckey');
call create_computer_listener('+4599999907','John Doe','john@doe.dk','H45-0007', '', 'enckey');
call create_computer_listener('+4599999908','John Doe','john@doe.dk','H45-0008', '', 'enckey');
call create_computer_listener('+4599999909','John Doe','john@doe.dk','H45-0009', '', 'enckey');


# We don't have the methods needed for proper integration tests so we need to adjust the data manually. #

# Adjust sip-id's for test purposes
update callers set sip_id=AES_ENCRYPT('L00000000', UNHEX(SHA('enckey'))) where updater='H45-0000';
update callers set sip_id=AES_ENCRYPT('L00000001', UNHEX(SHA('enckey'))) where updater='H45-0001';
update callers set sip_id=AES_ENCRYPT('L00000002', UNHEX(SHA('enckey'))) where updater='H45-0002';
update callers set sip_id=AES_ENCRYPT('L00000003', UNHEX(SHA('enckey'))) where updater='H45-0003';
update callers set sip_id=AES_ENCRYPT('L00000004', UNHEX(SHA('enckey'))) where updater='H45-0004';
update callers set sip_id=AES_ENCRYPT('L00000005', UNHEX(SHA('enckey'))) where updater='H45-0005';
update callers set sip_id=AES_ENCRYPT('L00000006', UNHEX(SHA('enckey'))) where updater='H45-0006';
update callers set sip_id=AES_ENCRYPT('L00000007', UNHEX(SHA('enckey'))) where updater='H45-0007';
update callers set sip_id=AES_ENCRYPT('L00000008', UNHEX(SHA('enckey'))) where updater='H45-0008';
update callers set sip_id=AES_ENCRYPT('L00000009', UNHEX(SHA('enckey'))) where updater='H45-0009';

# Reset passwords for test purposes
update callers set sip_pw=AES_ENCRYPT('12345', UNHEX(SHA('enckey')))