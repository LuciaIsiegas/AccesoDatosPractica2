package com.luciaia.heladeriamvc.gui;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

public class Modelo {
    private String ip;
    private String user;
    private String password;
    private String adminPassword;

    public Modelo() {
        getPropValues();
    }

    public String getIp() {
        return ip;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    private Connection conexion;

    void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/heladeria", user, password);
        } catch (SQLException sqle) {
            try {
                // En caso de no tener la BBDD creada la crea por defecto
                conexion = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/", user, password);

                PreparedStatement statement = null;
                String code = leerFichero();
                String[] query = code.split("--");
                for (String aQuery : query) {
                    statement = conexion.prepareStatement(aQuery);
                    statement.executeUpdate();
                }
                assert statement != null;
                statement.close();

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String leerFichero() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("scriptBBDD-Heladeria.sql"));
        String linea;
        StringBuilder stringBuilder = new StringBuilder();
        while ((linea = reader.readLine()) != null) {
            stringBuilder.append(linea);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    void desconectar() {
        try {
            conexion.close();
            conexion = null;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    // INSERT -------------------------------------------------------------------------------------------
    void insertarProveedor(String nombre, String personaContacto, String email, String telefono, String direccion) {
        String sentenciaSql = "INSERT INTO proveedor (nombre, persona_contacto, email, telefono, direccion) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, personaContacto);
            sentencia.setString(3, email);
            sentencia.setString(4, telefono);
            sentencia.setString(5, direccion);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void insertarEmpleado(String nombre, String apellidos, String email, String telefono) {
        String sentenciaSql = "INSERT INTO empleado (nombre, apellidos, email, telefono) VALUES (?, ?, ?, ?)";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, email);
            sentencia.setString(4, telefono);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void insertarCliente(String nombre, String apellidos, String email, String telefono) {
        String sentenciaSql = "INSERT INTO cliente (nombre, apellidos, email, telefono) VALUES (?, ?, ?, ?)";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, email);
            sentencia.setString(4, telefono);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void insertarHelado(String nombre, float precio, LocalDate fechaApertura, LocalDate fechaCaducidad, String tipo, int idProveedor, String sabor, boolean azucar, float litros) {
        String sentenciaSql = "call pCrearHelado(?,?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setFloat(2, precio);
            sentencia.setDate(3, Date.valueOf(fechaApertura));
            sentencia.setDate(4, Date.valueOf(fechaCaducidad));
            sentencia.setString(5, tipo);
            sentencia.setInt(6, idProveedor);
            sentencia.setString(7, sabor);
            sentencia.setBoolean(8, azucar);
            sentencia.setFloat(9, litros);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void insertarGofre(String nombre, float precio, LocalDate fechaApertura, LocalDate fechaCaducidad, String tipo, int idProveedor, String topping, boolean gluten, String tipoMasa) {
        String sentenciaSql = "call pCrearGofre(?,?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setFloat(2, precio);
            sentencia.setDate(3, fechaApertura == null ? Date.valueOf(LocalDate.now()) : Date.valueOf(fechaApertura));
            sentencia.setDate(4, Date.valueOf(fechaCaducidad));
            sentencia.setString(5, tipo);
            sentencia.setInt(6, idProveedor);
            sentencia.setString(7, topping);
            sentencia.setBoolean(8, gluten);
            sentencia.setString(9, tipoMasa);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void crearVenta(int idCliente, int idEmpleado) {
        String sentenciaSql = "INSERT INTO venta (id_cliente, id_empleado) VALUES (?, ?)";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sentenciaSql);
            sentencia.setInt(1, idCliente);
            sentencia.setInt(2, idEmpleado);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void insertarVentaProducto(int cantidad, int idVenta, int idProducto) {
        String sentenciaSql = "call pInsertarVentaProduto(?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, cantidad);
            sentencia.setInt(2, idVenta);
            sentencia.setInt(3, idProducto);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }
    // FIN INSERT -----------------------------------------------------------------------------------------------------


    // UPDATE -------------------------------------------------------------------------------------------
    void modificarProveedor(int id, String nombre, String personaContacto, String email, String telefono, String direccion) {
        String sentenciaSql = "UPDATE proveedor SET nombre = ?, persona_contacto = ?, email = ?, telefono = ?, direccion = ? WHERE id = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, personaContacto);
            sentencia.setString(3, email);
            sentencia.setString(4, telefono);
            sentencia.setString(5, direccion);
            sentencia.setInt(6, id);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void modificarEmpleado(int id, String nombre, String apellidos, String email, String telefono) {
        String sentenciaSql = "UPDATE empleado SET nombre = ?, apellidos = ?, email = ?, telefono = ? WHERE id = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, email);
            sentencia.setString(4, telefono);
            sentencia.setInt(5, id);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void modificarCliente(int id, String nombre, String apellidos, String email, String telefono) {
        String sentenciaSql = "UPDATE cliente SET nombre = ?, apellidos = ?, email = ?, telefono = ? WHERE id = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = conexion.prepareStatement(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, email);
            sentencia.setString(4, telefono);
            sentencia.setInt(5, id);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void modificarHelado(int id, float precio, LocalDate fechaApertura, LocalDate fechaCaducidad, int idProveedor, String sabor, boolean azucar, float litros) {
        String sentenciaSql = "call pModificarHelado(?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.setFloat(2, precio);
            sentencia.setDate(3, Date.valueOf(fechaApertura));
            sentencia.setDate(4, Date.valueOf(fechaCaducidad));
            sentencia.setInt(5, idProveedor);
            sentencia.setString(6, sabor);
            sentencia.setBoolean(7, azucar);
            sentencia.setFloat(8, litros);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    void modificarGofre(int id, float precio, LocalDate fechaApertura, LocalDate fechaCaducidad, int idProveedor, String topping, boolean gluten, String tipoMasa) {
        String sentenciaSql = "call pCrearGofre(?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.setFloat(2, precio);
            sentencia.setDate(3, Date.valueOf(fechaApertura));
            sentencia.setDate(4, Date.valueOf(fechaCaducidad));
            sentencia.setInt(5, idProveedor);
            sentencia.setString(6, topping);
            sentencia.setBoolean(7, gluten);
            sentencia.setString(8, tipoMasa);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void modificarVentaProducto(int id, int cantidad, int idVenta, int idProducto) {
        String sentenciaSql = "call pModificarVentaProduto(?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.setInt(2, cantidad);
            sentencia.setInt(3, idVenta);
            sentencia.setInt(4, idProducto);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void generarVenta(int idVenta) {
        String sentenciaSql = "call pGenerarVenta(?)";
        CallableStatement call = null;
        try {
            call = conexion.prepareCall(sentenciaSql);
            call.setInt(1, idVenta);
            call.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (call != null) {
                try {
                    call.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }
    // FIN UPDATE -----------------------------------------------------------------------------------------------------


    // DELETE -------------------------------------------------------------------------------------------
    void eliminarProveedor(int id) {
        String sentenciaSql = "call pEliminarProveedor(?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void eliminarEmpleado(int id) {
        String sentenciaSql = "call pEliminarEmpleado(?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void eliminarrCliente(int id) {
        String sentenciaSql = "call pEliminarCliente(?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void eliminarProducto(int id) {
        String sentenciaSql = "call pEliminarProducto(?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void eliminarVentaProducto(int id) {
        String sentenciaSql = "call pEliminarVentaProducto(?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void eliminarVenta(int id) {
        String sentenciaSql = "call pEliminarVenta(?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }
    // FIN DELETE -----------------------------------------------------------------------------------------------------


    // SELECT -------------------------------------------------------------------------------------------
    public int idUltimaVenta() throws SQLException {
        String consulta = "select id from venta;";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ResultSet res = ps.executeQuery();

        return res.last() ? res.getInt(1) : null;
    }

    ResultSet consultarProveedor() throws SQLException {
        String sentenciaSql = "SELECT id as 'ID', " +
                "nombre as 'Proveedor', " +
                "persona_contacto as 'Contacto', " +
                "email as 'Correo Electrónico', " +
                "telefono as 'Teléfono', " +
                "direccion as 'Dirección' " +
                "FROM proveedor " +
                "WHERE activo";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet consultarEmpleado() throws SQLException {
        String sentenciaSql = "SELECT id as 'ID', " +
                "nombre as 'Nombre', " +
                "apellidos as 'Apellidos', " +
                "email as 'Correo Electrónico', " +
                "telefono as 'Teléfono' " +
                "FROM empleado " +
                "WHERE activo";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet consultarCliente() throws SQLException {
        String sentenciaSql = "SELECT id as 'ID', " +
                "nombre as 'Nombre', " +
                "apellidos as 'Apellidos', " +
                "email as 'Correo Electrónico', " +
                "telefono as 'Teléfono' " +
                "FROM cliente " +
                "WHERE activo";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet consultarProducto() throws SQLException {
        String sentenciaSql = "SELECT pd.id as 'ID', " +
                "pd.nombre as 'Nombre', " +
                "pd.precio as 'Precio', " +
                "pd.tipo as 'Tipo', " +
                "pd.fecha_apertura as 'Fecha Apertura', " +
                "pd.fecha_caducidad as 'Fecha Caducidad', " +
                "concat(pv.id, ' - ', pv.nombre) as 'Proveedor' " +
                "FROM producto pd " +
                "JOIN proveedor pv on pd.id_proveedor = pv.id " +
                "WHERE pd.activo";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet consultarHelado(int idProducto) throws SQLException {
        String sentenciaSql = "SELECT h.id as 'ID', " +
                "h.sabor as 'Sabor', " +
                "h.azucar as 'Azucar', " +
                "h.litros as 'Litros' " +
                "FROM helado h " +
                "JOIN producto p on h.id_producto = p.id " +
                "WHERE h.id_producto = ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setInt(1, idProducto);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet consultarGofre(int idProducto) throws SQLException {
        String sentenciaSql = "SELECT g.id as 'ID', " +
                "g.topping as 'Topping', " +
                "g.gluten as 'Gluten', " +
                "g.tipo_masa as 'Tipo Masa' " +
                "FROM gofre g " +
                "JOIN producto p on g.id_producto = p.id " +
                "WHERE g.id_producto = ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setInt(1, idProducto);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet consultarVenta() throws SQLException {
        String sentenciaSql = "SELECT v.id as 'ID', " +
                "concat(e.id, ' - ', e.email) as 'Empleado', " +
                "concat(c.id, ' - ', c.email) as 'Cliente', " +
                "v.cantidad as 'Cantidad', " +
                "v.precio_total as 'Precio Total' " +
                "FROM venta v " +
                "JOIN cliente c on c.id = v.id_cliente " +
                "JOIN empleado e on e.id = v.id_empleado";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet consultarVentaProducto(int idVenta) throws SQLException {
        String sentenciaSql = "SELECT vp.id as 'ID', " +
                "vp.id_producto as 'ID Producto', " +
                "p.nombre as 'Producto', " +
                "p.precio as 'Precio Unidad', " +
                "vp.cantidad as 'Cantidad', " +
                "vp.precio_total as 'Total (€)' " +
                "FROM venta_producto vp " +
                "JOIN producto p on p.id = vp.id_producto " +
                "WHERE vp.id_venta = ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setInt(1, idVenta);
        resultado = sentencia.executeQuery();
        return resultado;
    }
    // FIN SELECT -----------------------------------------------------------------------------------------------------


    // BUSCAR -------------------------------------------------------------------------------------------
    ResultSet buscarProveedor(String nombre) throws SQLException {
        String sentenciaSql = "SELECT id as 'ID', " +
                "nombre as 'Proveedor', " +
                "persona_contacto as 'Contacto', " +
                "email as 'Correo Electrónico', " +
                "telefono as 'Teléfono', " +
                "direccion as 'Dirección' " +
                "FROM proveedor " +
                "WHERE activo " +
                "AND nombre like ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setString(1, "%" + nombre + "%");
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet buscarEmpleado(String emailEmpleado) throws SQLException {
        String sentenciaSql = "SELECT id as 'ID', " +
                "nombre as 'Nombre', " +
                "apellidos as 'Apellidos', " +
                "email as 'Correo Electrónico', " +
                "telefono as 'Teléfono' " +
                "FROM empleado " +
                "WHERE activo " +
                "AND email like ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setString(1, "%" + emailEmpleado +"%");
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet buscarCliente(String emailCliente) throws SQLException {
        String sentenciaSql = "SELECT id as 'ID', " +
                "nombre as 'Nombre', " +
                "apellidos as 'Apellidos', " +
                "email as 'Correo Electrónico', " +
                "telefono as 'Teléfono' " +
                "FROM cliente " +
                "WHERE activo " +
                "AND email like ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setString(1, "%" + emailCliente + "%");
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet buscarProducto(String nombreProducto) throws SQLException {
        String sentenciaSql = "SELECT pd.id as 'ID', " +
                "pd.nombre as 'Nombre', " +
                "pd.precio as 'Precio', " +
                "pd.tipo as 'Tipo', " +
                "pd.fecha_apertura as 'Fecha Apertura', " +
                "pd.fecha_caducidad as 'Fecha Caducidad' " +
                "pv.nombre as 'Proveedor' " +
                "FROM producto pd " +
                "JOIN proveedor pv on pd.id_proveedor = pv.id" +
                "WHERE pd.activo " +
                "AND pd.nombre like ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setString(1, "%" + nombreProducto + "%");
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet buscarVentaEmpleado(int idEmpleado) throws SQLException {
        String sentenciaSql = "SELECT v.id as 'ID', " +
                "v.id_empleado as 'Empleado', " +
                "c.email as 'Cliente', " +
                "v.cantidad as 'Cantidad', " +
                "v.precio_total as 'Precio Total' " +
                "FROM venta v " +
                "JOIN cliente c on c.id = v.id_cliente " +
                "WHERE v.id_empleado = ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setInt(1, idEmpleado);
        resultado = sentencia.executeQuery();
        return resultado;
    }

    ResultSet buscarVentaProducto(int idCliente) throws SQLException {
        String sentenciaSql = "SELECT v.id as 'ID', " +
                "v.id_empleado as 'Empleado', " +
                "c.email as 'Cliente', " +
                "v.cantidad as 'Cantidad', " +
                "v.precio_total as 'Precio Total' " +
                "FROM venta v " +
                "JOIN cliente c on c.id = v.id_cliente " +
                "WHERE v.id_cliente = ?";
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        sentencia = conexion.prepareStatement(sentenciaSql);
        sentencia.setInt(1, idCliente);
        resultado = sentencia.executeQuery();
        return resultado;
    }
    // FIN BUSCAR -----------------------------------------------------------------------------------------------------


    // LIMPIAR ------------------------------------------------------------------------------------------
    void limpiarBBDDProveedor() {
        String sentenciaSql = "call pLimpiarProveedor()";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void limpiarBBDDEmpleado() {
        String sentenciaSql = "call pLimpiarEmpleado()";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void limpiarBBDDCliente() {
        String sentenciaSql = "call pLimpiarCliente()";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void limpiarBBDDProducto() {
        String sentenciaSql = "call pLimpiarProducto()";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void limpiarBBDDVentaProducto(int idVenta) {
        String sentenciaSql = "call pLimpiarVentaProducto(?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, idVenta);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    void limpiarBBDDVenta() {
        String sentenciaSql = "call pLimpiarVenta()";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }
    // FIN LIMPIAR -----------------------------------------------------------------------------------------------------


    // EXISTE ------------------------------------------------------------------------------------------
    public boolean proveedorExiste(String nombreProveedor) {
        String proveedorConsult = "SELECT fExisteProveedor(?)";
        PreparedStatement function;
        boolean proveedorExists = false;
        try {
            function = conexion.prepareStatement(proveedorConsult);
            function.setString(1, nombreProveedor);
            ResultSet rs = function.executeQuery();
            rs.next();

            proveedorExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedorExists;
    }

    public boolean empleadoExiste(String emailEmpleado) {
        String empleadoConsult = "SELECT fExisteEmpleado(?)";
        PreparedStatement function;
        boolean empleadoExists = false;
        try {
            function = conexion.prepareStatement(empleadoConsult);
            function.setString(1, emailEmpleado);
            ResultSet rs = function.executeQuery();
            rs.next();

            empleadoExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empleadoExists;
    }

    public boolean clienteExiste(String emailCliente) {
        String clienteConsult = "SELECT fExisteCliente(?)";
        PreparedStatement function;
        boolean clienteExists = false;
        try {
            function = conexion.prepareStatement(clienteConsult);
            function.setString(1, emailCliente);
            ResultSet rs = function.executeQuery();
            rs.next();

            clienteExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clienteExists;
    }

    public boolean productoExiste(String nombreProducto) {
        String productoConsult = "SELECT fExisteProducto(?)";
        PreparedStatement function;
        boolean productoExists = false;
        try {
            function = conexion.prepareStatement(productoConsult);
            function.setString(1, nombreProducto);
            ResultSet rs = function.executeQuery();
            rs.next();

            productoExists = rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productoExists;
    }
    // FIN EXISTE -----------------------------------------------------------------------------------------------------


    private void getPropValues() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = new FileInputStream(propFileName);

            prop.load(inputStream);
            ip = prop.getProperty("ip");
            user = prop.getProperty("user");
            password = prop.getProperty("pass");
            adminPassword = prop.getProperty("admin");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setPropValues(String ip, String user, String pass, String adminPass) {
        try {
            Properties prop = new Properties();
            prop.setProperty("ip", ip);
            prop.setProperty("user", user);
            prop.setProperty("pass", pass);
            prop.setProperty("admin", adminPass);
            OutputStream out = new FileOutputStream("config.properties");
            prop.store(out, null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.ip = ip;
        this.user = user;
        this.password = pass;
        this.adminPassword = adminPass;
    }


}
