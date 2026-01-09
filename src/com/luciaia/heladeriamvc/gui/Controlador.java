package com.luciaia.heladeriamvc.gui;

import com.luciaia.heladeriamvc.util.Util;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

public class Controlador implements ActionListener, ItemListener, ListSelectionListener, WindowListener {
    private Modelo modelo;
    private Vista vista;
    private VistaVenta vistaVenta;
    boolean refrescar;
    private int idVenta;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vistaVenta = new VistaVenta(vista);
        this.idVenta = 0;

        modelo.conectar();
        setOptions();
        addActionListeners(this);
        //addItemListeners(this);
        addWindowListeners(this);
        refrescarTodo();
        //iniciar();
    }

    // COMPROBAR CAMPOS OBLIGATORIOS ---------------------------------------------------------------------------------------
    private boolean hayCamposVaciosProducto() {
        try {
            if (!vista.txtNombreProducto.getText().isEmpty()
                    && !vista.txtPrecioProducto.getText().isEmpty()
                    && !vista.dateCaducidad.getText().isEmpty()
                    && !vista.comboProveedor.getSelectedItem().toString().isEmpty()) {
                return false;
            }
            return true;
        } catch (NullPointerException ne) {
            return true;
        }
    }

    private boolean hayCamposVaciosHelado() {
        try {
            if (!vista.panelHelado.comboSabor.getSelectedItem().toString().isEmpty()
                    && !vista.panelHelado.litrosHeladoTxt.getText().isEmpty()) {
                return false;
            }
            return true;
        } catch (NullPointerException ne) {
            return true;
        }
    }

    private boolean hayCamposVaciosGofre() {
        try {
            if (!vista.panelGofre.comboToppingGofre.getSelectedItem().toString().isEmpty()
                    && !vista.panelGofre.tipoMasaComboBox.getSelectedItem().toString().isEmpty()) {
                return false;
            }
            return true;
        } catch (NullPointerException ne) {
            return true;
        }
    }

    private boolean hayCamposVaciosEmpleado() {
        return vista.txtNombreEmpeado.getText().isEmpty() || vista.txtEmailEmpleado.getText().isEmpty();
    }

    private boolean hayCamposVaciosCliente() {
        return vista.txtNombreCliente.getText().isEmpty() || vista.txtEmailCliente.getText().isEmpty();
    }

    private boolean hayCamposVaciosProveedor() {
        return vista.txtNombreProveedor.getText().isEmpty()
                || vista.txtContactoProveedor.getText().isEmpty()
                || vista.txtEmailProveedor.getText().isEmpty();
    }

    private boolean hayCamposVaciosVenta() {
        try {
            if (!vista.comboEmpleado.getSelectedItem().toString().isEmpty()
                    && !vista.comboCliente.getSelectedItem().toString().isEmpty()) {
                return false;
            }
            return true;
        } catch (NullPointerException ne) {
            return true;
        }
    }

    private boolean hayCamposVaciosVentaProducto() {
        try {
            if (!vistaVenta.comboProducto.getSelectedItem().toString().isEmpty()
                    && !vistaVenta.txtCantidad.getText().isEmpty()) {
                return false;
            }
            return true;
        } catch (NullPointerException ne) {
            return true;
        }
    }

    private boolean validarCamposProducto() {
        // Consultas válidas
        if (!Util.consultaValida(vista.txtNombreProducto.getText())
                || !Util.consultaValida(vista.txtPrecioProducto.getText())
                || !Util.consultaValida(vista.txtNombreProducto.getText())
                || (vista.radioHelado.isSelected() && !Util.consultaValida(vista.panelHelado.litrosHeladoTxt.getText()))) {
            Util.mensajeError("Por favor introduzca correctamente los datos", "Campos Erróneos");
            return false;
        }

        if (hayCamposVaciosProducto()
                || !Util.longitudCorrecta(vista.txtNombreProducto.getText(), 50)
                || modelo.productoExiste(vista.txtNombreProducto.getText())
                || !Util.esFloat(vista.txtPrecioProducto.getText())) {
            Util.mensajeError("Por favor compruebe los datos de nuevo: \n" +
                    "- Campos obligatorios (*)\n" +
                    "- Nombre del producto repetido (máximo 50 carácteres)\n" +
                    "- Precio en formato de número decimal o entero (positivo)", "Campos Incorrectos");
            return false;
        }
        if (vista.radioHelado.isSelected() && hayCamposVaciosHelado()
                && !Util.esFloat(vista.panelHelado.litrosHeladoTxt.getText())) {
            Util.mensajeError("Por favor compruebe los datos de nuevo: \n" +
                    "- Campos obligatorios (*)\n" +
                    "- Litros en formato de número decimal o entero (positivo)", "Campos Incorrectos");
            return false;
        }
        if (vista.radioGofre.isSelected() && hayCamposVaciosGofre()) {
            Util.mensajeError("Por favor compruebe los datos de nuevo: \n" +
                    "- Campos obligatorios (*)", "Campos Incorrectos");
            return false;
        }
        return true;
    }

    private boolean validarCamposEmpleado() {
        // Consultas válidas
        if (!Util.consultaValida(vista.txtNombreEmpeado.getText())
                || !Util.consultaValida(vista.txtApellidosEmpleado.getText())
                || !Util.consultaValida(vista.txtEmailEmpleado.getText())
                || !Util.consultaValida(vista.txtTelefonoEmpleado.getText())) {
            Util.mensajeError("Por favor introduzca correctamente los datos", "Campos Erróneos");
            return false;
        }

        if (hayCamposVaciosEmpleado()
                || !Util.longitudCorrecta(vista.txtNombreEmpeado.getText(), 50)
                || !Util.longitudCorrecta(vista.txtApellidosEmpleado.getText(), 100)
                || !Util.longitudCorrecta(vista.txtEmailEmpleado.getText(), 100)
                || modelo.empleadoExiste(vista.txtEmailEmpleado.getText())
                || !Util.validarEmail(vista.txtEmailEmpleado.getText())
                || !Util.validarTelefono(vista.txtTelefonoEmpleado.getText())) {
            Util.mensajeError("Por favor compruebe los datos de nuevo: \n" +
                    "- Campos obligatorios (*)\n" +
                    "- Nombre no repetido (máximo 50 carácteres)\n" +
                    "- Apellidos (máximo 100 carácteres)\n" +
                    "- Email (máximo 100 carácteres)\n" +
                    "- Teléfono válido (9 dígitos)", "Campos Incorrectos");
            return false;
        }
        return true;
    }

    private boolean validarCamposCliente() {
        // Consultas válidas
        if (!Util.consultaValida(vista.txtNombreCliente.getText())
                || !Util.consultaValida(vista.txtApellidosCliente.getText())
                || !Util.consultaValida(vista.txtEmailCliente.getText())
                || !Util.consultaValida(vista.txtTelefonoCliente.getText())) {
            Util.mensajeError("Por favor introduzca correctamente los datos", "Campos Erróneos");
            return false;
        }

        if (hayCamposVaciosCliente()
                || !Util.longitudCorrecta(vista.txtNombreCliente.getText(), 50)
                || !Util.longitudCorrecta(vista.txtApellidosCliente.getText(), 100)
                || !Util.longitudCorrecta(vista.txtEmailCliente.getText(), 100)
                || !Util.validarEmail(vista.txtEmailCliente.getText())
                || modelo.clienteExiste(vista.txtEmailCliente.getText())
                || !Util.validarTelefono(vista.txtTelefonoCliente.getText())) {
            Util.mensajeError("Por favor compruebe los datos de nuevo: \n" +
                    "- Campos obligatorios (*)\n" +
                    "- Nombre no repetido (máximo 50 carácteres)\n" +
                    "- Apellidos (máximo 100 carácteres)\n" +
                    "- Email (máximo 100 carácteres)\n" +
                    "- Teléfono válido (9 dígitos)", "Campos Incorrectos");
            return false;
        }
        return true;
    }

    private boolean validarCamposProveedor() {
        // Consultas válidas
        if (!Util.consultaValida(vista.txtNombreProveedor.getText())
                || !Util.consultaValida(vista.txtContactoProveedor.getText())
                || !Util.consultaValida(vista.txtEmailProveedor.getText())
                || !Util.consultaValida(vista.txtTelefonoProveedor.getText())
                || !Util.consultaValida(vista.txtDireccionProveedor.getText())) {
            Util.mensajeError("Por favor introduzca correctamente los datos", "Campos Erróneos");
            return false;
        }

        if (hayCamposVaciosProveedor()
                || !Util.longitudCorrecta(vista.txtNombreProveedor.getText(), 50)
                || !Util.longitudCorrecta(vista.txtContactoProveedor.getText(), 50)
                || !Util.longitudCorrecta(vista.txtEmailProveedor.getText(), 100)
                || !Util.longitudCorrecta(vista.txtDireccionProveedor.getText(), 200)
                || !Util.validarEmail(vista.txtEmailProveedor.getText())
                || modelo.proveedorExiste(vista.txtNombreProveedor.getText())
                || !Util.validarTelefono(vista.txtTelefonoProveedor.getText())) {
            Util.mensajeError("Por favor compruebe los datos de nuevo: \n" +
                    "- Campos obligatorios (*)\n" +
                    "- Nombre no repetido (máximo 50 carácteres)\n" +
                    "- Apellidos (máximo 100 carácteres)\n" +
                    "- Email (máximo 100 carácteres)\n" +
                    "- Teléfono válido (9 dígitos)\n" +
                    "- Direccion (máximo 200 carácteres)", "Campos Incorrectos");
            return false;
        }
        return true;
    }

    private boolean validarCamposVentaProducto() {
        // Consultas válidas
        if (!Util.consultaValida(vistaVenta.txtCantidad.getText())) {
            return false;
        }

        if (hayCamposVaciosVentaProducto()
                || !Util.esEntero(vistaVenta.txtCantidad.getText())) {
            return false;
        }
        return true;
    }
    // COMPROBAR CAMPOS OBLIGATORIOS FIN ---------------------------------------------------------------------------------------


    // LIMPIAR PANELES ---------------------------------------------------------------------------------------
    private void resetearProducto() {
        vista.txtNombreProducto.setEditable(true);
        vista.radioHelado.setEnabled(true);
        vista.radioGofre.setEnabled(true);

        vista.tableProducto.setEnabled(true);
        vista.btnGuardarProducto.setVisible(false);
        vista.btnCancelarProducto.setVisible(false);
        vista.btnEditaProducto.setVisible(true);
        vista.btnNuevoProducto.setVisible(true);
        vista.btnLimpiarProducto.setVisible(true);
        vista.btnEliminarProducto.setVisible(true);
        vista.btnBorrarBBDDProducto.setVisible(true);
        vista.txtBusquedaProducto.setText(null);

        limpiarCamposProducto();
        limpiarCamposHelado();
        limpiarCamposGofre();
        refrescarProducto();
    }

    private void resetearEmpleado() {
        vista.tableEmpleado.setEnabled(true);
        vista.btnGuardarEmpleado.setVisible(false);
        vista.btnCancelarEmpleado.setVisible(false);
        vista.btnEditarEmpleado.setVisible(true);
        vista.btnNuevoEmpleado.setVisible(true);
        vista.btnLimpiarEmpleado.setVisible(true);
        vista.btnEliminarEmpleado.setVisible(true);
        vista.btnBorrarBBDDEmpleado.setVisible(true);
        vista.txtBuscarEmpleado.setText(null);

        limpiarCamposEmpleado();
        refrescarEmpleado();
    }

    private void resetearCliente() {
        vista.tableCliente.setEnabled(true);
        vista.btnGuardarCliente.setVisible(false);
        vista.btnCancelarCliente.setVisible(false);
        vista.btnEditarCliente.setVisible(true);
        vista.btnNuevoCliente.setVisible(true);
        vista.btnLimpiarCliente.setVisible(true);
        vista.btnEliminarCliente.setVisible(true);
        vista.btnBorrarBBDDCliente.setVisible(true);
        vista.txtBuscarCliente.setText(null);

        limpiarCamposCliente();
        refrescarCliente();
    }

    private void resetearProveedor() {
        vista.tableProveedor.setEnabled(true);
        vista.btnGuardarProveedor.setVisible(false);
        vista.btnCancelarProveedor.setVisible(false);
        vista.btnEditarProveedor.setVisible(true);
        vista.btnNuevoProveedor.setVisible(true);
        vista.btnLimpiarProveedor.setVisible(true);
        vista.btnEliminarProveedor.setVisible(true);
        vista.btnBorrarBBDDProveedor.setVisible(true);
        vista.txtBuscarProveedor.setText(null);

        limpiarCamposProveedor();
        refrescarProveedor();
    }

    private void resetearVenta() {
        vista.tableVenta.setEnabled(true);
        limpiarCamposVenta();
        refrescarVenta();
    }

    private void resetearVentaProducto() {
        vistaVenta.tableVentaProducto.setEnabled(true);
        vistaVenta.btnGuardar.setVisible(false);
        vistaVenta.btnCancelar.setVisible(false);
        vistaVenta.btnEditar.setVisible(true);
        vistaVenta.btnAnnadir.setVisible(true);
        vistaVenta.btnEliminar.setVisible(true);
        vistaVenta.btnBorrarBBDDVentaProducto.setVisible(true);

        limpiarCamposVentaProducto();
        refrescarVentaProducto(idVenta);
    }

    private void limpiarCamposProducto() {
        vista.radioHelado.doClick();
        vista.radioGofre.setSelected(false);
        vista.txtNombreProducto.setText(null);
        vista.txtPrecioProducto.setText(null);
        vista.dateApertura.setText(null);
        vista.dateCaducidad.setText(null);
    }

    private void limpiarCamposHelado() {
        vista.panelHelado.comboSabor.setSelectedIndex(0);
        vista.panelHelado.conAzucarRadioButton.setSelected(true);
        vista.panelHelado.litrosHeladoTxt.setText(null);
    }

    private void limpiarCamposGofre() {
        vista.panelGofre.comboToppingGofre.setSelectedIndex(0);
        vista.panelGofre.tipoMasaComboBox.setSelectedIndex(0);
        vista.panelGofre.conGlutenRadioButton.setSelected(true);
    }

    private void limpiarCamposEmpleado() {
        vista.txtNombreEmpeado.setText(null);
        vista.txtApellidosEmpleado.setText(null);
        vista.txtEmailEmpleado.setText(null);
        vista.txtTelefonoEmpleado.setText(null);
    }

    private void limpiarCamposCliente() {
        vista.txtNombreCliente.setText(null);
        vista.txtApellidosCliente.setText(null);
        vista.txtEmailCliente.setText(null);
        vista.txtTelefonoCliente.setText(null);
    }

    private void limpiarCamposProveedor() {
        vista.txtNombreProveedor.setText(null);
        vista.txtContactoProveedor.setText(null);
        vista.txtEmailProveedor.setText(null);
        vista.txtTelefonoProveedor.setText(null);
        vista.txtDireccionProveedor.setText(null);
    }

    private void limpiarCamposVenta() {
        vista.comboEmpleado.setSelectedIndex(-1);
        vista.comboCliente.setSelectedIndex(-1);
    }

    private void limpiarCamposVentaProducto() {
        vistaVenta.txtEmpleadoVenta.setText(String.valueOf(vista.comboEmpleado.getSelectedItem()));
        vistaVenta.txtClienteVenta.setText(String.valueOf(vista.comboCliente.getSelectedItem()));
        vistaVenta.comboProducto.setSelectedIndex(-1);
        vistaVenta.txtCantidad.setText(null);
    }
    // LIMPIAR PANELES FIN ---------------------------------------------------------------------------------------


    // REFRESCAR TABLAS ---------------------------------------------------------------------------------------
    private void refrescarTodo() {
        refrescarProveedor();
        refrescarEmpleado();
        refrescarCliente();
        refrescarProducto();
        refrescarVenta();
        refrescarVentaProducto(idVenta);
        refrescar = false;
    }

    private void refrescarProveedor() {
        try {
            vista.tableProveedor.setModel(construirTableModeloProveedor(modelo.consultarProveedor()));
            vista.comboProveedor.removeAllItems();
            for (int i = 0; i < vista.dtmProveedor.getRowCount(); i++) {
                vista.comboProveedor.addItem(vista.dtmProveedor.getValueAt(i, 0) + " - " +
                        vista.dtmProveedor.getValueAt(i, 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refrescarEmpleado() {
        try {
            vista.tableEmpleado.setModel(construirTableModeloEmpleado(modelo.consultarEmpleado()));
            vista.comboEmpleado.removeAllItems();
            for (int i = 0; i < vista.dtmEmpleado.getRowCount(); i++) {
                vista.comboEmpleado.addItem(vista.dtmEmpleado.getValueAt(i, 0) + " - " +
                        vista.dtmEmpleado.getValueAt(i, 3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refrescarCliente() {
        try {
            vista.tableCliente.setModel(construirTableModeloCliente(modelo.consultarCliente()));
            vista.comboCliente.removeAllItems();
            for (int i = 0; i < vista.dtmCliente.getRowCount(); i++) {
                vista.comboCliente.addItem(vista.dtmCliente.getValueAt(i, 0) + " - " +
                        vista.dtmCliente.getValueAt(i, 3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refrescarProducto() {
        try {
            vista.tableProducto.setModel(construirTableModeloProducto(modelo.consultarProducto()));
            vistaVenta.comboProducto.removeAllItems();
            for (int i = 0; i < vista.dtmProducto.getRowCount(); i++) {
                vistaVenta.comboProducto.addItem(vista.dtmProducto.getValueAt(i, 0) + " - " +
                        vista.dtmProducto.getValueAt(i, 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refrescarVenta() {
        try {
            vista.tableVenta.setModel(construirTableModeloVenta(modelo.consultarVenta()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refrescarVentaProducto(int idVenta) {
        try {
            vistaVenta.tableVentaProducto.setModel(construirTableModeloVentaProducto(modelo.consultarVentaProducto(idVenta)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // REFRESCAR TABLAS FIN ---------------------------------------------------------------------------------------


    // TABLE MODEL  ---------------------------------------------------------------------------------------
    private DefaultTableModel construirTableModeloProveedor(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        setDataVector(rs, columnCount, data);
        vista.dtmProveedor.setDataVector(data, columnNames);
        return vista.dtmProveedor;
    }

    private DefaultTableModel construirTableModeloEmpleado(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        setDataVector(rs, columnCount, data);
        vista.dtmEmpleado.setDataVector(data, columnNames);
        return vista.dtmEmpleado;
    }

    private DefaultTableModel construirTableModeloCliente(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        setDataVector(rs, columnCount, data);
        vista.dtmCliente.setDataVector(data, columnNames);
        return vista.dtmCliente;
    }

    private DefaultTableModel construirTableModeloProducto(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        setDataVector(rs, columnCount, data);
        vista.dtmProducto.setDataVector(data, columnNames);
        return vista.dtmProducto;
    }

    private DefaultTableModel construirTableModeloVenta(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        setDataVector(rs, columnCount, data);
        vista.dtmVenta.setDataVector(data, columnNames);
        return vista.dtmVenta;
    }

    private DefaultTableModel construirTableModeloVentaProducto(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        setDataVector(rs, columnCount, data);
        vistaVenta.dtmVentaProducto.setDataVector(data, columnNames);
        return vistaVenta.dtmVentaProducto;
    }
    // TABLE MODEL FIN ---------------------------------------------------------------------------------------


    // NUEVO -----------------------------------------------------------------------------------------
    private void nuevoProducto() {
        if (!validarCamposProducto()) {
            return;
        }

        if (vista.radioHelado.isSelected()) {
            modelo.insertarHelado(
                    vista.txtNombreProducto.getText(), Float.parseFloat(vista.txtPrecioProducto.getText()),
                    vista.dateApertura.getDate(), vista.dateCaducidad.getDate(), "helado",
                    Integer.parseInt(String.valueOf(vista.comboProveedor.getSelectedItem()).split(" ")[0]),
                    String.valueOf(vista.panelHelado.comboSabor.getSelectedItem()),
                    vista.panelHelado.conAzucarRadioButton.isSelected(),
                    Float.parseFloat(vista.panelHelado.litrosHeladoTxt.getText())
            );
        } else if (vista.radioGofre.isSelected()) {
            modelo.insertarGofre(
                    vista.txtNombreProducto.getText(), Float.parseFloat(vista.txtPrecioProducto.getText()),
                    vista.dateApertura.getDate(), vista.dateCaducidad.getDate(), "gofre",
                    Integer.parseInt(String.valueOf(vista.comboProveedor.getSelectedItem()).split(" ")[0]),
                    String.valueOf(vista.panelGofre.comboToppingGofre.getSelectedItem()),
                    vista.panelGofre.conGlutenRadioButton.isSelected(),
                    String.valueOf(vista.panelGofre.tipoMasaComboBox.getSelectedItem())
            );
        }

        resetearProducto();
        Util.mensajeInfo("Se ha añadido un nuevo producto", "Nuevo Producto");
    }

    private void nuevoEmpleado() {
        if (!validarCamposEmpleado()) {
            return;
        }

        modelo.insertarEmpleado(
                vista.txtNombreEmpeado.getText(), vista.txtApellidosEmpleado.getText(),
                vista.txtEmailEmpleado.getText(), vista.txtTelefonoEmpleado.getText()
        );

        resetearEmpleado();
        Util.mensajeInfo("Se ha añadido un nuevo empleado", "Nuevo Empleado");
    }

    private void nuevoCliente() {
        if (!validarCamposCliente()) {
            return;
        }

        modelo.insertarCliente(
                vista.txtNombreCliente.getText(), vista.txtApellidosCliente.getText(),
                vista.txtEmailCliente.getText(), vista.txtTelefonoCliente.getText()
        );

        resetearCliente();
        Util.mensajeInfo("Se ha añadido un nuevo cliente", "Nuevo Cliente");
    }

    private void nuevoProveedor() {
        if (!validarCamposProveedor()) {
            return;
        }

        modelo.insertarProveedor(
                vista.txtNombreProveedor.getText(), vista.txtContactoProveedor.getText(), vista.txtEmailProveedor.getText(),
                vista.txtTelefonoProveedor.getText(), vista.txtDireccionProveedor.getText()
        );
        resetearProveedor();
        Util.mensajeInfo("Se ha añadido un nuevo proveedor", "Nuevo Proveedor");
    }

    private void nuevoVenta() {
        if (hayCamposVaciosVenta()) {
            return;
        }
        modelo.crearVenta(
                Integer.parseInt(String.valueOf(vista.comboCliente.getSelectedItem()).split(" ")[0]),
                Integer.parseInt(String.valueOf(vista.comboEmpleado.getSelectedItem()).split(" ")[0])
        );
        try {
            idVenta = modelo.idUltimaVenta();
            vistaVenta.setVisible(true);
            resetearVentaProducto();
            limpiarCamposVentaProducto();
            refrescarVentaProducto(idVenta);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void nuevoVentaProducto() {
        if (!validarCamposVentaProducto()) {
            return;
        }
        modelo.insertarVentaProducto(
                Integer.parseInt(vistaVenta.txtCantidad.getText()), idVenta,
                Integer.parseInt(String.valueOf(vistaVenta.comboProducto.getSelectedItem()).split(" ")[0])
        );
        modelo.generarVenta(idVenta);
        resetearVentaProducto();
        refrescarVenta();
    }
    // NUEVO FIN -----------------------------------------------------------------------------------------


    // EDITAR -----------------------------------------------------------------------------------------
    private void editarProducto() {
        int fila = vista.tableProducto.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún producto seleccionado", "Selecciona un producto");
            return;
        }
        int idProducto = (int) vista.tableProducto.getValueAt(fila, 0);

        vista.txtNombreProducto.setText((String) vista.tableProducto.getValueAt(fila, 1));
        vista.txtPrecioProducto.setText(String.valueOf(vista.tableProducto.getValueAt(fila, 2)));
        vista.dateApertura.setDate(LocalDate.parse(String.valueOf(vista.tableProducto.getValueAt(fila, 4))));
        vista.dateCaducidad.setDate(LocalDate.parse(String.valueOf(vista.tableProducto.getValueAt(fila, 5))));
        vista.comboProveedor.setSelectedItem(vista.tableProducto.getValueAt(fila, 6));

        try {
            if (((String) vista.tableProducto.getValueAt(fila, 3)).equals("helado")) {
                vista.radioHelado.doClick();

                ResultSet helado = modelo.consultarHelado(idProducto);
                if (helado.next()) {
                    vista.panelHelado.comboSabor.setSelectedItem(helado.getString(2));
                    if (helado.getBoolean(3)) {
                        vista.panelHelado.conAzucarRadioButton.setSelected(true);
                    } else {
                        vista.panelHelado.sinAzucarRadioButton.setSelected(true);
                    }
                    vista.panelHelado.litrosHeladoTxt.setText(String.valueOf(helado.getFloat(4)));
                }

            } else if (((String) vista.tableProducto.getValueAt(fila, 3)).equals("gofre")) {
                vista.radioGofre.doClick();

                ResultSet gofre = modelo.consultarGofre(idProducto);
                if (gofre.next()) {
                    vista.panelGofre.comboToppingGofre.setSelectedItem(gofre.getString(2));
                    if (gofre.getBoolean(3)) {
                        vista.panelGofre.conGlutenRadioButton.setSelected(true);
                    } else {
                        vista.panelGofre.sinGlutenRadioButton.setSelected(true);
                    }
                    vista.panelGofre.tipoMasaComboBox.setSelectedItem(gofre.getString(4));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        vista.tableProducto.setEnabled(false);
        vista.txtNombreProducto.setEditable(false);
        vista.radioHelado.setEnabled(false);
        vista.radioGofre.setEnabled(false);

        vista.btnGuardarProducto.setVisible(true);
        vista.btnCancelarProducto.setVisible(true);
        vista.btnEditaProducto.setVisible(false);
        vista.btnNuevoProducto.setVisible(false);
        vista.btnBorrarBBDDProducto.setVisible(false);
        vista.btnLimpiarProducto.setVisible(false);
    }

    private void editarEmpleado() {
        int fila = vista.tableEmpleado.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún empleado seleccionado", "Selecciona un empleado");
            return;
        }

        vista.txtNombreEmpeado.setText((String) vista.tableEmpleado.getValueAt(fila, 1));
        vista.txtApellidosEmpleado.setText((String) vista.tableEmpleado.getValueAt(fila, 2));
        vista.txtEmailEmpleado.setText((String) vista.tableEmpleado.getValueAt(fila, 3));
        vista.txtTelefonoEmpleado.setText((String) vista.tableEmpleado.getValueAt(fila, 4));

        vista.tableEmpleado.setEnabled(false);
        vista.btnGuardarEmpleado.setVisible(true);
        vista.btnCancelarEmpleado.setVisible(true);
        vista.btnEditarEmpleado.setVisible(false);
        vista.btnNuevoEmpleado.setVisible(false);
        vista.btnBorrarBBDDEmpleado.setVisible(false);
        vista.btnLimpiarEmpleado.setVisible(false);
    }

    private void editarCliente() {
        int fila = vista.tableCliente.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún cliente seleccionado", "Selecciona un cliente");
            return;
        }

        vista.txtNombreCliente.setText((String) vista.tableCliente.getValueAt(fila, 1));
        vista.txtApellidosCliente.setText((String) vista.tableCliente.getValueAt(fila, 2));
        vista.txtEmailCliente.setText((String) vista.tableCliente.getValueAt(fila, 3));
        vista.txtTelefonoCliente.setText((String) vista.tableCliente.getValueAt(fila, 4));

        vista.tableCliente.setEnabled(false);
        vista.btnGuardarCliente.setVisible(true);
        vista.btnCancelarCliente.setVisible(true);
        vista.btnEditarCliente.setVisible(false);
        vista.btnNuevoCliente.setVisible(false);
        vista.btnBorrarBBDDCliente.setVisible(false);
        vista.btnLimpiarCliente.setVisible(false);
    }

    private void editarProveedor() {
        int fila = vista.tableProveedor.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún proveedor seleccionado", "Selecciona un proveedor");
            return;
        }

        vista.txtNombreProveedor.setText((String) vista.tableProveedor.getValueAt(fila, 1));
        vista.txtContactoProveedor.setText((String) vista.tableProveedor.getValueAt(fila, 2));
        vista.txtEmailProveedor.setText((String) vista.tableProveedor.getValueAt(fila, 3));
        vista.txtTelefonoProveedor.setText((String) vista.tableProveedor.getValueAt(fila, 4));
        vista.txtDireccionProveedor.setText((String) vista.tableProveedor.getValueAt(fila, 5));

        vista.tableProveedor.setEnabled(false);
        vista.btnGuardarProveedor.setVisible(true);
        vista.btnCancelarProveedor.setVisible(true);
        vista.btnEditarProveedor.setVisible(false);
        vista.btnNuevoProveedor.setVisible(false);
        vista.btnBorrarBBDDProveedor.setVisible(false);
        vista.btnLimpiarProveedor.setVisible(false);
    }

    private void editarVenta() {
        int fila = vista.tableVenta.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ninguna venta seleccionado", "Selecciona una venta");
            return;
        }

        idVenta = (int) vista.tableVenta.getValueAt(fila, 0);
        vista.comboEmpleado.setSelectedItem(vista.tableVenta.getValueAt(fila, 1));
        vista.comboCliente.setSelectedItem(vista.tableVenta.getValueAt(fila, 2));
        vistaVenta.setVisible(true);
        resetearVentaProducto();
    }

    private void editarVentaProducto() {
        int fila = vistaVenta.tableVentaProducto.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ninguna línea seleccionado", "Selecciona una línea");
            return;
        }

        vistaVenta.txtEmpleadoVenta.setText(String.valueOf(vista.comboEmpleado.getSelectedItem()));
        vistaVenta.txtClienteVenta.setText(String.valueOf(vista.comboCliente.getSelectedItem()));
        vistaVenta.comboProducto.setSelectedItem(
                vistaVenta.tableVentaProducto.getValueAt(fila, 1) + " - "
                        + vistaVenta.tableVentaProducto.getValueAt(fila, 2)
        );
        vistaVenta.txtCantidad.setText(String.valueOf(vistaVenta.tableVentaProducto.getValueAt(fila, 4)));
        vistaVenta.tableVentaProducto.setEnabled(false);
        vistaVenta.btnGuardar.setVisible(true);
        vistaVenta.btnCancelar.setVisible(true);
        vistaVenta.btnEditar.setVisible(false);
        vistaVenta.btnAnnadir.setVisible(false);
        vistaVenta.btnBorrarBBDDVentaProducto.setVisible(false);
    }
    // EDITAR FIN -----------------------------------------------------------------------------------------


    // ELIMINAR -----------------------------------------------------------------------------------------
    private void eliminarProducto() {
        int fila = vista.tableProducto.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún producto seleccionado", "Selecciona un producto");
            return;
        }

        String producto = (String) vista.tableProducto.getValueAt(fila, 1);

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + producto + "\"?", "Eliminar producto");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarProducto((Integer) vista.tableProducto.getValueAt(fila, 0));
            refrescarProducto();
            Util.mensajeInfo("Se ha eliminado \"" + producto + "\"", "Producto Eliminado");
        }
    }

    private void eliminarEmpleado() {
        int fila = vista.tableEmpleado.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún empleado seleccionado", "Selecciona un empleado");
            return;
        }

        String empleado = ((String) vista.tableEmpleado.getValueAt(fila, 0)) + " - "
                + ((String) vista.tableEmpleado.getValueAt(fila, 1)) + " "
                + ((String) vista.tableEmpleado.getValueAt(fila, 2));

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + empleado + "\"?", "Eliminar empleado");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarEmpleado((Integer) vista.tableEmpleado.getValueAt(fila, 0));
            refrescarEmpleado();
            Util.mensajeInfo("Se ha eliminado \"" + empleado + "\"", "Empleado Eliminado");
        }
    }

    private void eliminarCliente() {
        int fila = vista.tableCliente.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún cliente seleccionado", "Selecciona un cliente");
            return;
        }

        String cliente = ((String) vista.tableCliente.getValueAt(fila, 0)) + " - "
                + ((String) vista.tableCliente.getValueAt(fila, 1)) + " "
                + ((String) vista.tableCliente.getValueAt(fila, 2));

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + cliente + "\"?", "Eliminar cliente");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarrCliente((Integer) vista.tableCliente.getValueAt(fila, 0));
            refrescarCliente();
            Util.mensajeInfo("Se ha eliminado \"" + cliente + "\"", "Cliente Eliminado");
        }
    }

    private void eliminarProveedor() {
        int fila = vista.tableProveedor.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún proveedor seleccionado", "Selecciona un proveedor");
            return;
        }

        String proveedor = (String) vista.tableProveedor.getValueAt(fila, 1);

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + proveedor + "\"?", "Eliminar proveedor");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarProveedor((Integer) vista.tableProveedor.getValueAt(fila, 0));
            refrescarProveedor();
            Util.mensajeInfo("Se ha eliminado \"" + proveedor + "\"", "Proveedor Eliminado");
        }
    }

    private void eliminarVenta() {
        int fila = vista.tableVenta.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ninguna venta seleccionada", "Selecciona una venta");
            return;
        }
        int venta = (Integer) vista.tableVenta.getValueAt(fila, 0);

        int resp = Util.mensajeConfirmación("¿Desea eliminar venta nº \"" + venta + "\"?", "Eliminar venta");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarVenta(venta);
            refrescarVenta();
            Util.mensajeInfo("Se ha eliminado venta nº \"" + venta + "\"", "Venta Eliminada");
        }
    }

    private void eliminarVentaProducto() {
        int fila = vistaVenta.tableVentaProducto.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ninguna línea seleccionada", "Selecciona una línea");
            return;
        }
        int linea = (Integer) vistaVenta.tableVentaProducto.getValueAt(fila, 0);

        int resp = Util.mensajeConfirmación("¿Desea eliminar línea nº \"" + linea + "\"?", "Eliminar línea");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarVentaProducto(linea);
            refrescarVentaProducto(idVenta);
            Util.mensajeInfo("Se ha eliminado línea nº \"" + linea + "\"", "Línea Eliminada");
        }
        modelo.generarVenta(idVenta);
        refrescarVentaProducto(idVenta);
        refrescarVenta();
    }
    // ELIMINAR FIN -----------------------------------------------------------------------------------------


    // BORRAR BBDD -----------------------------------------------------------------------------------------
    private void borrarBBDDProducto() {
        try {
            if (!modelo.consultarProducto().next()) {
                Util.mensajeInfo("No existen productos a borrar", "Borrar productos");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int resp1 = Util.mensajeConfirmación("¿Desea borrar todos los productos?", "Borrar Productos");
        if (resp1 == JOptionPane.OK_OPTION) {
            modelo.limpiarBBDDProducto();
            refrescarProducto();
        }
    }

    private void borrarBBDDEmpleado() {
        try {
            if (!modelo.consultarEmpleado().next()) {
                Util.mensajeInfo("No existen empleados a borrar", "Borrar Empleados");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int resp1 = Util.mensajeConfirmación("¿Desea borrar todos los empleados?", "Borrar Empleados");
        if (resp1 == JOptionPane.OK_OPTION) {
            modelo.limpiarBBDDEmpleado();
            refrescarEmpleado();
        }
    }

    private void borrarBBDDCliente() {
        try {
            if (!modelo.consultarCliente().next()) {
                Util.mensajeInfo("No existen clientes a borrar", "Borrar Clientes");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int resp1 = Util.mensajeConfirmación("¿Desea borrar todos los clientes?", "Borrar Clientes");
        if (resp1 == JOptionPane.OK_OPTION) {
            modelo.limpiarBBDDCliente();
            refrescarCliente();
        }
    }

    private void borrarBBDDProveedor() {
        try {
            if (!modelo.consultarProveedor().next()) {
                Util.mensajeInfo("No existen proveedores a borrar", "Borrar Proveedores");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int resp1 = Util.mensajeConfirmación("¿Desea borrar todos los proveedores?", "Borrar Proveedores");
        if (resp1 == JOptionPane.OK_OPTION) {
            modelo.limpiarBBDDProveedor();
            refrescarProveedor();
        }
    }

    private void borrarBBDDVenta() {
        try {
            if (!modelo.consultarVenta().next()) {
                Util.mensajeInfo("No existen ventas a borrar", "Borrar Ventas");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int resp1 = Util.mensajeConfirmación("¿Desea borrar todas las ventas?", "Borrar Ventas");
        if (resp1 == JOptionPane.OK_OPTION) {
            modelo.limpiarBBDDVenta();
            refrescarVenta();
        }
    }

    private void borrarBBDDVentaProducto() {
        try {
            if (!modelo.consultarVentaProducto(idVenta).next()) {
                Util.mensajeInfo("No existen productos a borrar", "Borrar detalle venta");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int resp1 = Util.mensajeConfirmación("¿Desea borrar todos los productos?", "Borrar detalle venta");
        if (resp1 == JOptionPane.OK_OPTION) {
            modelo.limpiarBBDDVentaProducto(idVenta);
            refrescarVentaProducto(idVenta);
        }
    }
    // BORRAR BBDD FIN -----------------------------------------------------------------------------------------


    // METODOS VARIOS ---------------------------------------------------------------------------------------
    private void setOptions() {
        vista.optionDialog.txtIP.setText(modelo.getIp());
        vista.optionDialog.txtUsuario.setText(modelo.getUser());
        vista.optionDialog.pfPass.setText(modelo.getPassword());
        vista.optionDialog.pfAdmin.setText(modelo.getAdminPassword());
    }

    private void setDataVector(ResultSet rs, int columnCount, Vector<Vector<Object>> data) throws SQLException {
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
    }
    // METODOS VARIOS FIN ---------------------------------------------------------------------------------------


    // LISTENERS --------------------------------------------------------------------------------------------
    private void addWindowListeners(WindowListener listener) {
        vista.addWindowListener(listener);
    }

    private void addActionListeners(ActionListener listener) {
        // PanelCard
        vista.radioHelado.addActionListener(listener);
        vista.radioHelado.setActionCommand("Helado");
        vista.radioGofre.addActionListener(listener);
        vista.radioGofre.setActionCommand("Gofre");

        // Limpiar
        vista.btnLimpiarProducto.addActionListener(listener);
        vista.btnLimpiarProducto.setActionCommand("limpiarProducto");
        vista.btnLimpiarEmpleado.addActionListener(listener);
        vista.btnLimpiarEmpleado.setActionCommand("limpiarEmpleado");
        vista.btnLimpiarCliente.addActionListener(listener);
        vista.btnLimpiarCliente.setActionCommand("limpiarCliente");
        vista.btnLimpiarProveedor.addActionListener(listener);
        vista.btnLimpiarProveedor.setActionCommand("limpiarProveedor");

        // Nuevo
        vista.btnNuevoProducto.addActionListener(listener);
        vista.btnNuevoProducto.setActionCommand("anadirProducto");
        vista.btnNuevoEmpleado.addActionListener(listener);
        vista.btnNuevoEmpleado.setActionCommand("anadirEmpleado");
        vista.btnNuevoCliente.addActionListener(listener);
        vista.btnNuevoCliente.setActionCommand("anadirCliente");
        vista.btnNuevoProveedor.addActionListener(listener);
        vista.btnNuevoProveedor.setActionCommand("anadirProveedor");
        vista.btnNuevoVenta.addActionListener(listener);
        vista.btnNuevoVenta.setActionCommand("anadirVenta");
        vistaVenta.btnAnnadir.addActionListener(listener);
        vistaVenta.btnAnnadir.setActionCommand("anadirVentaProducto");

        // Editar
        vista.btnEditaProducto.addActionListener(listener);
        vista.btnEditaProducto.setActionCommand("editarProducto");
        vista.btnEditarEmpleado.addActionListener(listener);
        vista.btnEditarEmpleado.setActionCommand("editarEmpleado");
        vista.btnEditarCliente.addActionListener(listener);
        vista.btnEditarCliente.setActionCommand("editarCliente");
        vista.btnEditarProveedor.addActionListener(listener);
        vista.btnEditarProveedor.setActionCommand("editarProveedor");
        vista.btnEditarVenta.addActionListener(listener);
        vista.btnEditarVenta.setActionCommand("editarVenta");
        vistaVenta.btnEditar.addActionListener(listener);
        vistaVenta.btnEditar.setActionCommand("editarVentaProducto");

        // Eliminar
        vista.btnEliminarProducto.addActionListener(listener);
        vista.btnEliminarProducto.setActionCommand("eliminarProducto");
        vista.btnEliminarEmpleado.addActionListener(listener);
        vista.btnEliminarEmpleado.setActionCommand("eliminarEmpleado");
        vista.btnEliminarCliente.addActionListener(listener);
        vista.btnEliminarCliente.setActionCommand("eliminarCliente");
        vista.btnEliminarProveedor.addActionListener(listener);
        vista.btnEliminarProveedor.setActionCommand("eliminarProveedor");
        vista.btnEliminarVenta.addActionListener(listener);
        vista.btnEliminarVenta.setActionCommand("eliminarVenta");
        vistaVenta.btnEliminar.addActionListener(listener);
        vistaVenta.btnEliminar.setActionCommand("eliminarVentaProducto");

        // Borrar BBDD
        vista.btnBorrarBBDDProducto.addActionListener(listener);
        vista.btnBorrarBBDDProducto.setActionCommand("borrarProducto");
        vista.btnBorrarBBDDEmpleado.addActionListener(listener);
        vista.btnBorrarBBDDEmpleado.setActionCommand("borrarEmpleado");
        vista.btnBorrarBBDDCliente.addActionListener(listener);
        vista.btnBorrarBBDDCliente.setActionCommand("borrarCliente");
        vista.btnBorrarBBDDProveedor.addActionListener(listener);
        vista.btnBorrarBBDDProveedor.setActionCommand("borrarProveedor");
        vista.btnBorrarBBDDVenta.addActionListener(listener);
        vista.btnBorrarBBDDVenta.setActionCommand("borrarVenta");
        vistaVenta.btnBorrarBBDDVentaProducto.addActionListener(listener);
        vistaVenta.btnBorrarBBDDVentaProducto.setActionCommand("borrarVentaProducto");

        // Guardar
        vista.btnGuardarProducto.addActionListener(listener);
        vista.btnGuardarProducto.setActionCommand("guardarProducto");
        vista.btnGuardarEmpleado.addActionListener(listener);
        vista.btnGuardarEmpleado.setActionCommand("guardarEmpleado");
        vista.btnGuardarCliente.addActionListener(listener);
        vista.btnGuardarCliente.setActionCommand("guardarCliente");
        vista.btnGuardarProveedor.addActionListener(listener);
        vista.btnGuardarProveedor.setActionCommand("guardarProveedor");
        vistaVenta.btnGuardar.addActionListener(listener);
        vistaVenta.btnGuardar.setActionCommand("guardarVentaProducto");

        // Cancelar
        vista.btnCancelarProducto.addActionListener(listener);
        vista.btnCancelarProducto.setActionCommand("cancelarProducto");
        vista.btnCancelarEmpleado.addActionListener(listener);
        vista.btnCancelarEmpleado.setActionCommand("cancelarEmpleado");
        vista.btnCancelarCliente.addActionListener(listener);
        vista.btnCancelarCliente.setActionCommand("cancelarCliente");
        vista.btnCancelarProveedor.addActionListener(listener);
        vista.btnCancelarProveedor.setActionCommand("cancelarProveedor");
        vistaVenta.btnCancelar.addActionListener(listener);
        vistaVenta.btnCancelar.setActionCommand("cancelarVentaProducto");

        // Refrescar
        vista.btnRefrescarVenta.addActionListener(listener);
        vista.btnRefrescarVenta.setActionCommand("refrescarVenta");


        vista.optionDialog.btnOpcionesGuardar.addActionListener(listener);
        vista.optionDialog.btnOpcionesGuardar.setActionCommand("guardarOpciones");
        vista.itemOpciones.addActionListener(listener);
        vista.itemSalir.addActionListener(listener);
        vista.itemDesconectar.addActionListener(listener);
        vista.btnValidate.addActionListener(listener);
    }
    // LISTENERS FIN --------------------------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommmand = e.getActionCommand();
        CardLayout cl = (CardLayout) (vista.panelCard.getLayout());

        switch (actionCommmand) {
            case "Opciones":
                vista.adminPasswordDialog.setVisible(true);
                break;
            case "Desconectar":
                modelo.desconectar();
                break;
            case "Salir":
                System.exit(0);
                break;
            case "abrirOpciones":
                if (String.valueOf(vista.adminPassword.getPassword()).equals(modelo.getAdminPassword())) {
                    vista.adminPassword.setText("");
                    vista.adminPasswordDialog.dispose();
                    vista.optionDialog.setVisible(true);
                } else {
                    Util.mensajeError("La contraseña introducida no es correcta.", "Error");
                }
                break;
            case "guardarOpciones":
                modelo.setPropValues(vista.optionDialog.txtIP.getText(), vista.optionDialog.txtUsuario.getText(),
                        String.valueOf(vista.optionDialog.pfPass.getPassword()), String.valueOf(vista.optionDialog.pfAdmin.getPassword()));
                vista.optionDialog.dispose();
                vista.dispose();
                new Controlador(new Modelo(), new Vista());
                break;

            // PANEL CARD
            case "Helado":
                limpiarCamposGofre();
                cl.show(vista.panelCard, "Helado");
                break;
            case "Gofre":
                limpiarCamposHelado();
                cl.show(vista.panelCard, "Gofre");
                break;

            // LIMPIAR
            case "limpiarProducto":
                limpiarCamposProducto();
                limpiarCamposHelado();
                limpiarCamposGofre();
                break;
            case "limpiarEmpleado":
                limpiarCamposEmpleado();
                break;
            case "limpiarCliente":
                limpiarCamposCliente();
                break;
            case "limpiarProveedor":
                limpiarCamposProveedor();
                break;

            // NUEVO
            case "anadirProducto":
                nuevoProducto();
                break;
            case "anadirEmpleado":
                nuevoEmpleado();
                break;
            case "anadirCliente":
                nuevoCliente();
                break;
            case "anadirProveedor":
                nuevoProveedor();
                break;
            case "anadirVenta":
                nuevoVenta();
                break;
            case "anadirVentaProducto":
                nuevoVentaProducto();
                break;

            // EDITAR
            case "editarProducto":
                editarProducto();
                break;
            case "editarEmpleado":
                editarEmpleado();
                break;
            case "editarCliente":
                editarCliente();
                break;
            case "editarProveedor":
                editarProveedor();
                break;
            case "editarVenta":
                editarVenta();
                break;
            case "editarVentaProducto":
                editarVentaProducto();
                break;

            // ELIMINAR
            case "eliminarProducto":
                eliminarProducto();
                break;
            case "eliminarEmpleado":
                eliminarEmpleado();
                break;
            case "eliminarCliente":
                eliminarCliente();
                break;
            case "eliminarProveedor":
                eliminarProveedor();
                break;
            case "eliminarVenta":
                eliminarVenta();
                break;
            case "eliminarVentaProducto":
                eliminarVentaProducto();
                break;

            // BORRAR BBDD
            case "borrarProducto":
                borrarBBDDProducto();
                break;
            case "borrarEmpleado":
                borrarBBDDEmpleado();
                break;
            case "borrarCliente":
                borrarBBDDCliente();
                break;
            case "borrarProveedor":
                borrarBBDDProveedor();
                break;
            case "borrarVenta":
                borrarBBDDVenta();
                break;
            case "borrarVentaProducto":
                borrarBBDDVentaProducto();
                break;

            // GUARDAR
            case "guardarProducto":
                break;
            case "guardarEmpleado":
                break;
            case "guardarCliente":
                break;
            case "guardarProveedor":
                break;
            case "guardarVentaProducto":
                break;

            // CANCELAR
            case "cancelarProducto":
                resetearProducto();
                break;
            case "cancelarEmpleado":
                resetearEmpleado();
                break;
            case "cancelarCliente":
                resetearCliente();
                break;
            case "cancelarProveedor":
                resetearProveedor();
                break;
            case "cancelarVentaProducto":
                resetearVentaProducto();
                break;

            case "refrescarVenta":
                resetearVenta();
                break;


            case "Guardar":
                //guardarCambiosProducto();
                break;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        int resp = Util.mensajeConfirmación("¿Desea cerrar la vetana?", "Salir");
        if (resp == JOptionPane.OK_OPTION || resp == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }
}
