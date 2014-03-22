use telesal;

call create_hall('45', '0000','Test Hall 0000','','+4500000000','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0001','Test Hall 0001','','+4500000001','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0002','Test Hall 0002','','+4500000002','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0003','Test Hall 0003','','+4500000003','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');
call create_hall('45', '0004','Test Hall 0004','','+4500000004','Test Admin','admin@test.dk','+4510000000','User Comment','Admin Comment','Admin','enckey');

update callers set sip_pw=AES_ENCRYPT('Test-12345', UNHEX(SHA('enckey')))