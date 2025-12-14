INSERT INTO DISCOGRAFICAS(nombre)
    values ('Black light'),('The Reaper');

INSERT INTO ALBUMES(nombre,anio,banda, genero,precio,discografica_id,uuid)
    VALUES ('The End', '2004','Ad Hominem','Black Metal','15.50',1,UUID());

INSERT INTO ALBUMES (nombre,anio,banda, genero,precio,discografica_id,uuid)
    VALUES ('Altar Madness', '1989','Morbid Angel','Death Metal','18.50',2,UUID());

-- Primero insertamos el usuario 1 (Admin)
-- Contraseña: Admin1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Admin', 'Administrador', 'admin', 'admin@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.');

-- Insertamos sus roles
insert into USER_ROLES (user_id, roles)
values (1, 'USER');
insert into USER_ROLES (user_id, roles)
values (1, 'ADMIN');

-- Usuario 2 (Jose)
-- Contraseña: User1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Jose', 'Jose User', 'jose', 'user@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.');

insert into USER_ROLES (user_id, roles)
values (2, 'USER');

-- Usuario 3 (Test)
-- Contraseña: Test1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Test', 'Test Test', 'test', 'test@prueba.net',
        '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu');

insert into USER_ROLES (user_id, roles)
values (3, 'USER');

-- Usuario 4 (María)
-- Contraseña: Otro1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('María', 'María Otro', 'maría', 'otro@prueba.net',
        '$2a$12$3Q4.UZbvBMBEvIwwjGEjae/zrIr6S50NusUlBcCNmBd2382eyU0bS');

insert into USER_ROLES (user_id, roles)
values (4, 'USER');
