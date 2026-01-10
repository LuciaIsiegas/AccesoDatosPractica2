/*drop database if exists heladeria;*/

create database if not exists heladeria;
--
USE heladeria;
--
CREATE TABLE IF NOT EXISTS proveedor (
id int auto_increment primary key,
nombre varchar(50) not null,
persona_contacto varchar(50) not null,
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
email varchar(100) not null,
telefono varchar(9),
activo bool not null default true
);
--
CREATE TABLE IF NOT EXISTS cliente (
id int auto_increment primary key,
nombre varchar(50) not null,
apellidos varchar(100),
email varchar(100) not null,
telefono varchar(9),
activo bool not null default true
);
--
CREATE TABLE IF NOT EXISTS producto (
id int auto_increment primary key,
nombre varchar(50) not null,
precio float not null,
fecha_apertura date,
fecha_caducidad date not null,
tipo varchar(50) not null,
activo bool not null default true,
id_proveedor int not null,
foreign key(id_proveedor) references proveedor(id)
);
--
CREATE TABLE IF NOT EXISTS helado (
id int auto_increment primary key,
id_producto int,
sabor varchar(50) not null,
azucar bool not null,
litros float not null,
foreign key(id_producto) references producto(id)
);
--
CREATE TABLE IF NOT EXISTS gofre (
id int auto_increment primary key,
id_producto int,
topping varchar(50) not null,
gluten bool not null,
tipo_masa varchar(50) not null,
foreign key(id_producto) references producto(id)
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
CREATE TABLE IF NOT EXISTS venta_producto (
id int auto_increment primary key,
cantidad int not null,
precio_total float,
id_venta int not null,
id_producto int not null,
foreign key(id_venta) references venta(id),
foreign key(id_producto) references producto(id)
);
--
drop procedure if exists pEliminarProveedor;
--
create procedure pEliminarProveedor(pid_proveedor int)
begin
	if exists (select id from proveedor where id = pid_proveedor) then
		if exists (select id from producto where id_proveedor = pid_proveedor) then
			update proveedor
            set activo = false
            where id = pid_proveedor;
		else
			delete from proveedor
			where id = pid_proveedor;
		end if;
	else
		select "El proveedor no existe";
    end if;
end;
--
drop procedure if exists pEliminarProducto;
--
create procedure pEliminarProducto(pid_producto int)
begin
	if exists (select id from producto where id = pid_producto) then
		if exists (select id from venta_producto where id_producto = pid_producto) then
			update producto
            set activo = false
            where id = pid_producto;
		else
			delete from helado
            where id_producto = pid_producto;
            delete from gofre
            where id_producto = pid_producto;
			delete from producto
			where id = pid_producto;
		end if;
	else
		select "El producto no existe";
    end if;
end;
--
drop procedure if exists pEliminarCliente;
--
create procedure pEliminarCliente(pid_cliente int)
begin
	if exists (select id from cliente where id = pid_cliente) then
		if exists (select id from venta where id_cliente = pid_cliente) then
			update cliente
            set activo = false
            where id = pid_cliente;
		else
			delete from cliente
			where id = pid_cliente;
		end if;
	else
		select "El cliente no existe";
    end if;
end;
--
drop procedure if exists pEliminarEmpleado;
--
create procedure pEliminarEmpleado(pid_empleado int)
begin
	if exists (select id from empleado where id = pid_empleado) then
		if exists (select id from venta where id_empleado = pid_empleado) then
			update empleado
            set activo = false
            where id = pid_empleado;
		else
			delete from empleado
			where id = pid_empleado;
		end if;
	else
		select "El empleado no existe";
    end if;
end;
--
drop procedure if exists pEliminarVentaProducto;
--
create procedure pEliminarVentaProducto(pid_venta_producto int)
begin
	if exists (select id from venta_producto where id = pid_venta_producto) then
		delete from venta_producto
        where id = pid_venta_producto;
	else
		select "La venta no existe";
    end if;
end;
--
drop procedure if exists pEliminarVenta;
--
create procedure pEliminarVenta(pid_venta int)
begin
	if exists (select id from venta where id like pid_venta) then
		delete from venta_producto
        where id_venta = pid_venta;
        delete from venta
        where id = pid_venta;
	else
		select "La venta no existe";
    end if;
end;
--
drop procedure if exists pCrearHelado;
--
create procedure pCrearHelado(pnombre varchar(50), pprecio float, pfecha_apertura date, pfecha_caducidad date, ptipo varchar(50),
								pid_proveedor int, psabor varchar(50), pazucar bool, plitros float)
begin
	declare pid_producto int;
	if not exists (select nombre from producto where nombre like pnombre) then
		insert into producto(nombre, precio, fecha_apertura, fecha_caducidad, tipo, id_proveedor)
        values(pnombre, pprecio, pfecha_apertura, pfecha_caducidad, ptipo, pid_proveedor);
        
		set pid_producto = (select id from producto where nombre like pnombre);
        insert into helado(id_producto, sabor, azucar, litros)
        values(pid_producto, psabor, pazucar, plitros);
    end if;
end;
--
drop procedure if exists pCrearGofre;
--
create procedure pCrearGofre(pnombre varchar(50), pprecio float, pfecha_apertura date, pfecha_caducidad date, ptipo varchar(50),
								pid_proveedor int, ptopping varchar(50), pgluten bool, ptipo_masa varchar(50))
begin
	declare pid_producto int;
	if not exists (select nombre from producto where nombre like pnombre) then
		insert into producto(nombre, precio, fecha_apertura, fecha_caducidad, tipo, id_proveedor)
        values(pnombre, pprecio, pfecha_apertura, pfecha_caducidad, ptipo, pid_proveedor);
        
		set pid_producto = (select id from producto where nombre like pnombre);
        insert into gofre(id_producto, topping, gluten, tipo_masa)
        values(pid_producto, ptopping, pgluten, ptipo_masa);
    end if;
end;
--
drop procedure if exists pGenerarVenta;
--
create procedure pGenerarVenta(pid_venta int)
begin
	if exists (select id from venta where id = pid_venta) then
		update venta
        set cantidad = (select sum(cantidad) from venta_producto where id_venta = pid_venta),
			precio_total = (select sum(precio_total) from venta_producto where id_venta = pid_venta)
		where id = pid_venta;
    end if;
end;
--
drop procedure if exists pInsertarVentaProduto;
--
create procedure pInsertarVentaProduto(pcantidad int, pid_venta int, pid_producto int)
begin
	if exists (select id from venta where id = pid_venta) 
    and exists (select id from producto where id = pid_producto) then
		insert into venta_producto(cantidad, precio_total, id_venta, id_producto)
        values(pcantidad, (select precio from producto where id = pid_producto)*cantidad, pid_venta, pid_producto);
    end if;
end;
--
drop procedure if exists pModificarHelado;
--
create procedure pModificarHelado(pid_producto int, pprecio float, pfecha_apertura date, pfecha_caducidad date, 
								pid_proveedor int, psabor varchar(50), pazucar bool, plitros float)
begin
	if exists (select id from producto where id = pid_producto and tipo = 'helado') then
		update producto
        set precio = pprecio, fecha_apertura = pfecha_apertura, 
			fecha_caducidad = pfecha_caducidad, id_proveedor = pid_proveedor
		where id = pid_producto;
        
        update helado
        set sabor = psabor, azucar = pazucar, litros = plitros
        where id_producto = pid_producto;
    end if;
end;
--
drop procedure if exists pModificarGofre;
--
create procedure pModificarGofre(pid_producto int, pprecio float, pfecha_apertura date, pfecha_caducidad date, 
								pid_proveedor int, ptopping varchar(50), pgluten bool, ptipo_masa varchar(50))
begin
	if exists (select id from producto where id = pid_producto and tipo = 'gofre') then
		update producto
        set precio = pprecio, fecha_apertura = pfecha_apertura, 
			fecha_caducidad = pfecha_caducidad, id_proveedor = pid_proveedor
		where id = pid_producto;
        
        update gofre
        set topping = ptopping, gluten = pgluten, tipo_masa = ptipo_masa
        where id_producto = pid_producto;
    end if;
end;
--
drop procedure if exists pModificarVentaProduto;
--
create procedure pModificarVentaProduto(pid int, pcantidad int, pid_venta int, pid_producto int)
begin
	if exists (select id from venta where id = pid_venta) 
    and exists (select id from producto where id = pid_producto) then
		update venta_producto
        set cantidad = pcantidad, id_producto = pid_producto, 
			precio_total = (select precio from producto where id = pid_producto)*cantidad
        where id = pid;
    end if;
end;
--
drop procedure if exists pLimpiarProveedor;
--
create procedure pLimpiarProveedor()
begin
	UPDATE proveedor p
	SET activo = FALSE
	WHERE EXISTS (
		SELECT id FROM producto pr
		WHERE pr.id_proveedor = p.id
	);

	DELETE FROM proveedor
	WHERE id NOT IN (
		SELECT DISTINCT id_proveedor FROM producto
	);
end;
--
drop procedure if exists pLimpiarProducto;
--
create procedure pLimpiarProducto()
begin
	UPDATE producto p
	SET activo = FALSE
	WHERE EXISTS (
		SELECT id FROM venta_producto vp
		WHERE vp.id_producto = p.id
	);

    DELETE h FROM helado h
    WHERE NOT EXISTS (
        SELECT id
        FROM venta_producto vp
        WHERE vp.id_producto = h.id_producto
    );
    DELETE g FROM gofre g
    WHERE NOT EXISTS (
        SELECT id
        FROM venta_producto vp
        WHERE vp.id_producto = g.id_producto
    );
    DELETE p FROM producto p
    WHERE NOT EXISTS (
        SELECT id
        FROM venta_producto vp
        WHERE vp.id_producto = p.id
    );
end;
--
drop procedure if exists pLimpiarCliente;
--
create procedure pLimpiarCliente()
begin
	UPDATE cliente c
	SET activo = FALSE
	WHERE EXISTS (
		SELECT id FROM venta v
		WHERE v.id_cliente = c.id
	);
    DELETE FROM cliente
    WHERE id NOT IN (SELECT DISTINCT id_cliente FROM venta);
end;
--
drop procedure if exists pLimpiarEmpleado;
--
create procedure pLimpiarEmpleado()
begin
	UPDATE empleado e
	SET activo = FALSE
	WHERE EXISTS (
		SELECT id FROM venta v
		WHERE v.id_empleado = e.id
	);
    DELETE FROM empleado
    WHERE id NOT IN (SELECT DISTINCT id_empleado FROM venta);
end;
--
drop procedure if exists pLimpiarVentaProducto;
--
create procedure pLimpiarVentaProducto(pid_venta int)
begin
	DELETE FROM venta_producto
    WHERE id_venta = pid_venta;
    DELETE FROM venta
    WHERE id = pid_venta;
end;
--
drop procedure if exists pLimpiarVenta;
--
create procedure pLimpiarVenta()
begin
	DELETE FROM venta_producto;
    DELETE FROM venta;
end;
