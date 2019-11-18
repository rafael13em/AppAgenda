/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  rafae
 * Created: 23-oct-2019
 */

CREATE TABLE PROVINCIA(
ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
CODIGO CHAR(2),
NOMBRE VARCHAR(20) NOT NULL,
CONSTRAINT ID_PROVINCIA_PK PRIMARY KEY(ID)
);
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('VI', 'ÁLAVA');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('AB', 'ALBACETE');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('A', 'ALICANTE');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('AL', 'ALMERIA');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('AV', 'AVILA');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('BA', 'BADAJOZ');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('IB', 'BALEARES');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('B', 'BARCELONA');
INSERT INTO PROVINCIA (CODIGO,NOMBRE) VALUES ('MA', 'MÁLAGA');
CREATE TABLE PERSONA (
ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, -- Id autonumérico
NOMBRE VARCHAR(20) NOT NULL,
APELLIDOS VARCHAR(40) NOT NULL,
TELEFONO VARCHAR(15),
EMAIL VARCHAR(30),
PROVINCIA INTEGER NOT NULL,
FECHA_NACIMIENTO DATE,
NUM_HIJOS SMALLINT,
ESTADO_CIVIL CHAR(1),
SALARIO DECIMAL(7,2),
JUBILADO BOOLEAN,
FOTO VARCHAR(30),
CONSTRAINT ID_PERSONA_PK PRIMARY KEY (ID),
CONSTRAINT PROV_PERSONA_FK FOREIGN KEY (PROVINCIA) REFERENCES PROVINCIA
(ID)
);
INSERT INTO PERSONA (NOMBRE,APELLIDOS,EMAIL,PROVINCIA) VALUES ('Isabel',
 'Jiménez','ijimenez@gmail.com',9);
INSERT INTO PERSONA (NOMBRE,APELLIDOS,EMAIL,PROVINCIA) VALUES ('Luisa',
 'López','llopez@gmail.com',4);
INSERT INTO PERSONA (NOMBRE,APELLIDOS,EMAIL,PROVINCIA) VALUES ('Eva',
'García','egarcia@gmail.com',4);
INSERT INTO PERSONA (NOMBRE,APELLIDOS,EMAIL,PROVINCIA) VALUES ('Alicia',
'Pérez','aperez@gmail.com',4);
INSERT INTO PERSONA (NOMBRE,APELLIDOS,EMAIL,PROVINCIA) VALUES ('Lola',
'Bermúdez','lbermudez@gmail.com',4);