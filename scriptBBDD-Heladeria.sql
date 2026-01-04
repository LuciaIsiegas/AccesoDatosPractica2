drop database if exists heladeria;
--
create database if not exists heladeria;
--
USE heladeria;
--
CREATE TABLE IF NOT EXISTS proveedor (
id int auto_increment primary key,
nombre varchar(50) not null,
persona_contacto varchar(150) not null,
email varchar(100) not null,
telefono varchar(9),
direccion varchar(200)
);
--
CREATE TABLE IF NOT EXISTS empleado (
id int auto_increment primary key,
nombre varchar(50) not null,
apellidos varchar(100),
email varchar(100) not null,
telefono varchar(9)
);
--
CREATE TABLE IF NOT EXISTS cliente (
id int auto_increment primary key,
nombre varchar(50) not null,
apellidos varchar(100),
email varchar(100) not null,
telefono varchar(9)
);
--
CREATE TABLE IF NOT EXISTS helado (
id int auto_increment primary key,
nombre varchar(50) unique not null,
precio int not null,
fecha_apertura date,
fecha_caducidad date not null,
sabor varchar(50) not null,
azucar bool not null,
litros int not null
);
--
CREATE TABLE IF NOT EXISTS venta (
id int auto_increment primary key,
cantidad int,
precio_total float,
id_cliente int not null,
id_empleado int not null,
foreign key(id_cliente) references cliente(id),
foreign key(id_empleado) references empleado(id)
);
--
CREATE TABLE IF NOT EXISTS venta_helado (
id int auto_increment primary key,
cantidad int not null,
precio_total float,
id_venta int not null,
id_helado int not null,
foreign key(id_venta) references venta(id),
foreign key(id_helado) references helado(id)
);
--
/*
delimiter ||
create function existeIsbn(f_isbn varchar(40))
returns bit
begin
	declare i int;
	set i=0;
	while (i<(select max(idlibro) from libros)) do
	if ((select isbn from libros 
		 where idlibro=(i+1)) like f_isbn) 
	then return 1;
	end if;
	set i=i+1;
	end while;
	return 0;
end; ||
delimiter ;
--
delimiter ||
create function existeNombreEditorial (f_name varchar(50))
returns bit
begin
	declare i int;
	set i=0;
	while (i<(select max(ideditorial) from editoriales)) do
	if ((select editorial from editoriales 
	     where ideditorial = (i+1)) like f_name) 
	then return 1;
	end if;
	set i=i+1;
	end while;
	return 0;
end; ||
delimiter ;
--
delimiter ||
create function existeNombreAutor (f_name varchar(50))
returns bit
begin
	declare i int;
	set i=0;
	while (i<(select max(idautor) from autores)) do
	if ((select concat(apellidos,', ',nombre) from autores
		where idautor = (i+1)) like f_name)
	then return 1;
	end if;
	set i=i+1;
	end while;
	return 0;
end; ||
delimiter ;
*/

