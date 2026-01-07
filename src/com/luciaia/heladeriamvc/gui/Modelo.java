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

    void insertarGofre(String nombre, int precio, LocalDate fechaApertura, LocalDate fechaCaducidad, String tipo, int idProveedor, String topping, boolean gluten, String tipoMasa) {
        String sentenciaSql = "call pCrearGofre(?,?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setString(1, nombre);
            sentencia.setInt(2, precio);
            sentencia.setDate(3, Date.valueOf(fechaApertura));
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

    void modificarHelado(int id, int precio, LocalDate fechaApertura, LocalDate fechaCaducidad, int idProveedor, String sabor, boolean azucar, float litros) {
        String sentenciaSql = "call pModificarHelado(?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.setInt(2, precio);
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

    void modificarGofre(int id, int precio, LocalDate fechaApertura, LocalDate fechaCaducidad, int idProveedor, String topping, boolean gluten, String tipoMasa) {
        String sentenciaSql = "call pCrearGofre(?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.setInt(2, precio);
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

    void modificarGofre(int id, int precio, LocalDate fechaApertura, LocalDate fechaCaducidad, int idProveedor, String topping, boolean gluten, String tipoMasa) {
        String sentenciaSql = "call pCrearGofre(?,?,?,?,?,?,?,?)";
        CallableStatement sentencia = null;

        try {
            sentencia = conexion.prepareCall(sentenciaSql);
            sentencia.setInt(1, id);
            sentencia.setInt(2, precio);
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


}
