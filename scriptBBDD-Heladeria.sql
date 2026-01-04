drop database if exists heladeria;
--
create database if not exists heladeria;
--
USE heladeria;
--
CREATE TABLE IF NOT EXISTS proveedor (
id int auto_increment primary key,
nombre varchar(50) unique not null,
persona_contacto varchar(150) not null,
email varchar(100) not null,
telefono varchar(9),
direccion varchar(200),
activo bool not null default true
);
--
CREATE TABLE IF NOT EXISTS empleado (
id int auto_increment primary key,
nombre varchar(50) not null,
apellidos varchar(100),
email varchar(100) unique not null,
telefono varchar(9),
activo bool not null default true
);
--
CREATE TABLE IF NOT EXISTS cliente (
id int auto_increment primary key,
nombre varchar(50) not null,
apellidos varchar(100),
email varchar(100) unique not null,
telefono varchar(9),
activo bool not null default true
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
litros int not null,
activo bool not null default true,
id_proveedor int not null,
foreign key(id_proveedor) references proveedor(id)
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
drop function if exists fExisteProveedor;
delimiter ||
create function fExisteProveedor(pnombre_proveedor varchar(50))
returns int
begin
	if exists (select nombre from proveedor where nombre = pnombre_proveedor) then
		return 1;
    else
		return 0;
    end if;
end ||
delimiter ;
--
drop function if exists fExisteHelado;
delimiter ||
create function fExisteHelado(pnombre_helado varchar(50))
returns int
begin
	if exists (select nombre from helado where nombre = pnombre_helado) then
		return 1;
    else
		return 0;
    end if;
end ||
delimiter ;
--
drop function if exists fExisteEmpleado;
delimiter ||
create function fExisteEmpleado(pemail_empleado varchar(100))
returns int
begin
	if exists (select email from empleado where email = pemail_empleado) then
		return 1;
    else
		return 0;
    end if;
end ||
delimiter ;
--
drop function if exists fExisteCliente;
delimiter ||
create function fExisteCliente(pemail_cliente varchar(100))
returns int
begin
	if exists (select email from cliente where email = pemail_cliente) then
		return 1;
    else
		return 0;
    end if;
end ||
delimiter ;
--
drop procedure if exists pEliminarProveedor;
delimiter ||
create procedure pEliminarProveedor(pnombre_proveedor varchar(50))
begin
	if exists (select nombre from proveedor where nombre like pnombre_proveedor) then
		if exists (select id from helado where id_proveedor = (select id from proveedor where nombre like pnombre_proveedor)) then
			update proveedor
            set activo = false
            where nombre like pnombre_proveedor;
		else
			delete from proveedor
			where nombre like pnombre_proveedor;
		end if;
	else
		select "El proveedor no existe";
    end if;
end ||
delimiter ;
--
drop procedure if exists pEliminarHelado;
delimiter ||
create procedure pEliminarHelado(pnombre_helado varchar(100))
begin
	if exists (select nombre from helado where nombre like pnombre_helado) then
		if exists (select id from venta_helado where id_helado = (select id from helado where nombre like pnombre_helado)) then
			update helado
            set activo = false
            where nombre like pnombre_helado;
		else
			delete from helado
			where nombre like pnombre_helado;
		end if;
	else
		select "El helado no existe";
    end if;
end ||
delimiter ;
--
drop procedure if exists pEliminarCliente;
delimiter ||
create procedure pEliminarCliente(pemail_cliente varchar(50))
begin
	if exists (select email from cliente where email like pemail_cliente) then
		if exists (select id from venta where id_cliente = (select id from cliente where email like pemail_cliente)) then
			update cliente
            set activo = false
            where email like pemail_cliente;
		else
			delete from cliente
			where email like pemail_cliente;
		end if;
	else
		select "El cliente no existe";
    end if;
end ||
delimiter ;
--
drop procedure if exists pEliminarEmpleado;
delimiter ||
create procedure pEliminarEmpleado(pemail_empleado varchar(50))
begin
	if exists (select email from empleado where email like pemail_empleado) then
		if exists (select id from venta where id_empleado = (select id from empleado where email like pemail_empleado)) then
			update empleado
            set activo = false
            where email like pemail_empleado;
		else
			delete from empleado
			where email like pemail_empleado;
		end if;
	else
		select "El empleado no existe";
    end if;
end ||
delimiter ;
--
drop procedure if exists pEliminarVenta;
delimiter ||
create procedure pEliminarVenta(pid_venta int)
begin
	if exists (select id from venta where id like pid_venta) then
		delete from venta_helado
        where id_venta = pid_venta;
        delete from venta
        where id = pid_venta;
	else
		select "La venta no existe";
    end if;
end ||
delimiter ;

