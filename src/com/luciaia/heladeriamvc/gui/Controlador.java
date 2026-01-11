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
    boolean editando;
    int filaEditando;
    boolean filtroProducto;
    boolean filtroEmpleado;
    boolean filtroCliente;
    boolean filtroProveedor;
    boolean filtroVentaEmpleado;
    boolean filtroVentaCliente;
    boolean conectado;
    private int idVenta;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vistaVenta = new VistaVenta(vista);
        this.idVenta = 0;
        this.filaEditando = -1;
        this.filtroProducto = false;
        this.filtroEmpleado = false;
        this.filtroCliente = false;
        this.filtroProveedor = false;
        this.filtroVentaEmpleado = false;
        this.filtroVentaCliente = false;
        this.editando = false;

        modelo.conectar();
        conectado = true;
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

        if (hayCamposVaciosProducto() || (vista.radioHelado.isSelected() && hayCamposVaciosHelado()) || vista.radioGofre.isSelected() && hayCamposVaciosGofre()) {
            Util.mensajeError("Por favor rellene los campos obligatorios", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtNombreProducto.getText(), 50)) {
            Util.mensajeError("El nombre del producto excede el máximo de carácteres (max 50)", "Campos Incorrectos");
            return false;
        }

        if (!editando && modelo.productoExiste(vista.txtNombreProducto.getText())) {
            Util.mensajeError("El nombre del producto ya está en uso", "Campos Incorrectos");
            return false;
        }

        if (!Util.esFloat(vista.txtPrecioProducto.getText())) {
            Util.mensajeError("Precio en formato de número decimal o entero (positivo)", "Campos Incorrectos");
            return false;
        }

        if (vista.radioHelado.isSelected() && !Util.esFloat(vista.panelHelado.litrosHeladoTxt.getText())) {
            Util.mensajeError("Litros en formato de número decimal o entero (positivo)", "Campos Incorrectos");
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

        if (hayCamposVaciosEmpleado()) {
            Util.mensajeError("Por favor rellene los campos obligatorios", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtEmailEmpleado.getText(), 100)) {
            Util.mensajeError("El email del empleado excede el máximo de carácteres (max 100)", "Campos Incorrectos");
            return false;
        }

        if (!Util.validarEmail(vista.txtEmailEmpleado.getText())) {
            Util.mensajeError("El email del empleado no tiene un formato correcto", "Campos Incorrectos");
            return false;
        }

        if (!editando && modelo.empleadoExiste(vista.txtEmailEmpleado.getText())) {
            Util.mensajeError("El email del empleado ya está en uso", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtNombreEmpeado.getText(), 50)) {
            Util.mensajeError("El nombre del empleado excede el máximo de carácteres (max 50)", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtApellidosEmpleado.getText(), 100)) {
            Util.mensajeError("Los apellidos del empleado exceden el máximo de carácteres (max 100)", "Campos Incorrectos");
            return false;
        }

        if (!Util.validarTelefono(vista.txtTelefonoEmpleado.getText())) {
            Util.mensajeError("El teléfono del empleado no tiene un formato correcto (9 dígitos)", "Campos Incorrectos");
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

        if (hayCamposVaciosCliente()) {
            Util.mensajeError("Por favor rellene los campos obligatorios", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtEmailCliente.getText(), 100)) {
            Util.mensajeError("El email del cliente excede el máximo de carácteres (max 100)", "Campos Incorrectos");
            return false;
        }

        if (!Util.validarEmail(vista.txtEmailCliente.getText())) {
            Util.mensajeError("El email del cliente no tiene un formato correcto", "Campos Incorrectos");
            return false;
        }

        if (!editando && modelo.clienteExiste(vista.txtEmailCliente.getText())) {
            Util.mensajeError("El email del cliente ya está en uso", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtNombreCliente.getText(), 50)) {
            Util.mensajeError("El nombre del cliente excede el máximo de carácteres (max 50)", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtApellidosCliente.getText(), 100)) {
            Util.mensajeError("Los apellidos del cliente exceden el máximo de carácteres (max 100)", "Campos Incorrectos");
            return false;
        }

        if (!Util.validarTelefono(vista.txtTelefonoCliente.getText())) {
            Util.mensajeError("El teléfono del cliente no tiene un formato correcto (9 dígitos)", "Campos Incorrectos");
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

        if (hayCamposVaciosProveedor()) {
            Util.mensajeError("Por favor rellene los campos obligatorios", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtNombreProveedor.getText(), 50)) {
            Util.mensajeError("El nombre del proveedor excede el máximo de carácteres (max 50)", "Campos Incorrectos");
            return false;
        }

        if (!editando && modelo.proveedorExiste(vista.txtNombreProveedor.getText())) {
            Util.mensajeError("El nombre del proveedor ya está en uso", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtEmailProveedor.getText(), 100)) {
            Util.mensajeError("El email del proveedor excede el máximo de carácteres (max 100)", "Campos Incorrectos");
            return false;
        }

        if (!Util.validarEmail(vista.txtEmailProveedor.getText())) {
            Util.mensajeError("El email del proveedor no tiene un formato correcto", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtContactoProveedor.getText(), 50)) {
            Util.mensajeError("El contacto del proveedor exceden el máximo de carácteres (max 50)", "Campos Incorrectos");
            return false;
        }

        if (!Util.longitudCorrecta(vista.txtDireccionProveedor.getText(), 200)) {
            Util.mensajeError("La dirección del proveedor exceden el máximo de carácteres (max 200)", "Campos Incorrectos");
            return false;
        }

        if (!Util.validarTelefono(vista.txtTelefonoProveedor.getText())) {
            Util.mensajeError("El teléfono del proveedor no tiene un formato correcto (9 dígitos)", "Campos Incorrectos");
            return false;
        }
        return true;
    }

    private boolean validarCamposVentaProducto() {
        // Consultas válidas
        if (!Util.consultaValida(vistaVenta.txtCantidad.getText())) {
            Util.mensajeError("Por favor introduzca correctamente los datos", "Campos Erróneos");
            return false;
        }

        if (hayCamposVaciosVentaProducto()) {
            Util.mensajeError("Por favor rellene los campos obligatorios", "Campos Incorrectos");
            return false;
        }

        if (!Util.esEntero(vistaVenta.txtCantidad.getText())) {
            Util.mensajeError("Cantidad en formato de número entero (positivo)", "Campos Incorrectos");
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
        vista.btnEditaProducto.setEnabled(true);
        vista.btnNuevoProducto.setEnabled(true);
        vista.btnBorrarBBDDProducto.setEnabled(true);
        vista.btnLimpiarProducto.setEnabled(true);
        vista.btnBuscarProducto.setEnabled(true);
        vista.txtBusquedaProducto.setText(null);

        limpiarCamposProducto();
        limpiarCamposHelado();
        limpiarCamposGofre();
        refrescarProducto();
        editando = false;
    }

    private void resetearEmpleado() {
        vista.txtEmailEmpleado.setEditable(true);
        vista.tableEmpleado.setEnabled(true);
        vista.btnGuardarEmpleado.setVisible(false);
        vista.btnCancelarEmpleado.setVisible(false);
        vista.btnEditarEmpleado.setEnabled(true);
        vista.btnNuevoEmpleado.setEnabled(true);
        vista.btnLimpiarEmpleado.setEnabled(true);
        vista.btnBorrarBBDDEmpleado.setEnabled(true);
        vista.btnBuscarEmpleado.setEnabled(true);
        vista.txtBuscarEmpleado.setText(null);

        limpiarCamposEmpleado();
        refrescarEmpleado();
        editando = false;
    }

    private void resetearCliente() {
        vista.txtEmailCliente.setEditable(true);
        vista.tableCliente.setEnabled(true);
        vista.btnGuardarCliente.setVisible(false);
        vista.btnCancelarCliente.setVisible(false);
        vista.btnEditarCliente.setEnabled(true);
        vista.btnNuevoCliente.setEnabled(true);
        vista.btnLimpiarCliente.setEnabled(true);
        vista.btnBorrarBBDDCliente.setEnabled(true);
        vista.btnBuscarCliente.setEnabled(true);
        vista.txtBuscarCliente.setText(null);

        limpiarCamposCliente();
        refrescarCliente();
        editando = false;
    }

    private void resetearProveedor() {
        vista.txtNombreProveedor.setEditable(true);
        vista.tableProveedor.setEnabled(true);
        vista.btnGuardarProveedor.setVisible(false);
        vista.btnCancelarProveedor.setVisible(false);
        vista.btnEditarProveedor.setEnabled(true);
        vista.btnNuevoProveedor.setEnabled(true);
        vista.btnLimpiarProveedor.setEnabled(true);
        vista.btnBorrarBBDDProveedor.setEnabled(true);
        vista.btnBuscarProveedor.setEnabled(true);
        vista.txtBuscarProveedor.setText(null);

        limpiarCamposProveedor();
        refrescarProveedor();
        editando = false;
    }

    private void resetearVenta() {
        vista.tableVenta.setEnabled(true);
        limpiarCamposVenta();
        refrescarVenta();
        editando = false;
    }

    private void resetearVentaProducto() {
        limpiarCamposVentaProducto();
        refrescarVentaProducto(idVenta);
        vistaVenta.tableVentaProducto.setEnabled(true);
        vistaVenta.btnGuardar.setVisible(false);
        vistaVenta.btnCancelar.setVisible(false);
        vistaVenta.btnEditar.setEnabled(true);
        vistaVenta.btnAnnadir.setEnabled(true);
        vistaVenta.btnBorrarBBDDVentaProducto.setEnabled(true);
        editando = false;
    }

    private void limpiarCamposProducto() {
        vista.radioHelado.doClick();
        vista.radioGofre.setSelected(false);
        vista.txtNombreProducto.setText(null);
        vista.txtPrecioProducto.setText(null);
        vista.comboProveedor.setSelectedIndex(-1);
        vista.dateApertura.setText(null);
        vista.dateCaducidad.setText(null);
    }

    private void limpiarCamposHelado() {
        vista.panelHelado.comboSabor.setSelectedIndex(-1);
        vista.panelHelado.conAzucarRadioButton.setSelected(true);
        vista.panelHelado.litrosHeladoTxt.setText(null);
    }

    private void limpiarCamposGofre() {
        vista.panelGofre.comboToppingGofre.setSelectedIndex(-1);
        vista.panelGofre.tipoMasaComboBox.setSelectedIndex(-1);
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
    }

    private void refrescarProveedor() {
        try {
            filtroProveedor = false;
            vista.btnBuscarProveedor.setText("Buscar");
            vista.txtBuscarProveedor.setText(null);
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
            filtroEmpleado = false;
            vista.btnBuscarEmpleado.setText("Buscar");
            vista.txtBuscarEmpleado.setText(null);
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
            filtroCliente = false;
            vista.btnBuscarCliente.setText("Buscar");
            vista.txtBuscarCliente.setText(null);
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
            filtroProducto = false;
            vista.btnBuscarProducto.setText("Buscar");
            vista.txtBusquedaProducto.setText(null);
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
            vista.btnBuscarVentaEmpleado.setText("Buscar");
            vista.btnBuscarVentaCliente.setText("Buscar");
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
            columnNames.add(metaData.getColumnLabel(column));
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
            columnNames.add(metaData.getColumnLabel(column));
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
            columnNames.add(metaData.getColumnLabel(column));
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
            columnNames.add(metaData.getColumnLabel(column));
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
            columnNames.add(metaData.getColumnLabel(column));
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
            columnNames.add(metaData.getColumnLabel(column));
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
            Util.mensajeError("Por favor rellene los campos obligatorios", "Campos Incorrectos");
            return;
        }
        modelo.crearVenta(
                Integer.parseInt(String.valueOf(vista.comboCliente.getSelectedItem()).split(" ")[0]),
                Integer.parseInt(String.valueOf(vista.comboEmpleado.getSelectedItem()).split(" ")[0])
        );
        try {
            idVenta = modelo.idUltimaVenta();
            resetearVentaProducto();
            vistaVenta.setVisible(true);
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
        resetearVentaProducto();
        refrescarVenta();
        modelo.generarVenta(idVenta);
        Util.mensajeInfo("Se ha añadido un nuevo producto", "Nuevo Producto");
    }
    // NUEVO FIN -----------------------------------------------------------------------------------------


    // EDITAR -----------------------------------------------------------------------------------------
    private void editarProducto() {
        filaEditando = vista.tableProducto.getSelectedRow();
        if (filaEditando < 0) {
            Util.mensajeError("No hay ningún producto seleccionado", "Selecciona un producto");
            return;
        }
        int idProducto = (int) vista.tableProducto.getValueAt(filaEditando, 0);

        vista.txtNombreProducto.setText(String.valueOf( vista.tableProducto.getValueAt(filaEditando, 1)));
        vista.txtPrecioProducto.setText(String.valueOf(vista.tableProducto.getValueAt(filaEditando, 2)));
        vista.dateApertura.setDate(LocalDate.parse(String.valueOf(vista.tableProducto.getValueAt(filaEditando, 4))));
        vista.dateCaducidad.setDate(LocalDate.parse(String.valueOf(vista.tableProducto.getValueAt(filaEditando, 5))));
        vista.comboProveedor.setSelectedItem(vista.tableProducto.getValueAt(filaEditando, 6));

        try {
            if ((String.valueOf( vista.tableProducto.getValueAt(filaEditando, 3)).equals("helado"))) {
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

            } else if ((String.valueOf( vista.tableProducto.getValueAt(filaEditando, 3)).equals("gofre"))) {
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
        vista.btnEditaProducto.setEnabled(false);
        vista.btnNuevoProducto.setEnabled(false);
        vista.btnBorrarBBDDProducto.setEnabled(false);
        vista.btnLimpiarProducto.setEnabled(false);
        vista.btnBuscarProducto.setEnabled(false);
        editando = true;
    }

    private void editarEmpleado() {
        filaEditando = vista.tableEmpleado.getSelectedRow();
        if (filaEditando < 0) {
            Util.mensajeError("No hay ningún empleado seleccionado", "Selecciona un empleado");
            return;
        }

        vista.txtNombreEmpeado.setText(String.valueOf( vista.tableEmpleado.getValueAt(filaEditando, 1)));
        vista.txtApellidosEmpleado.setText(String.valueOf( vista.tableEmpleado.getValueAt(filaEditando, 2)));
        vista.txtEmailEmpleado.setText(String.valueOf( vista.tableEmpleado.getValueAt(filaEditando, 3)));
        vista.txtTelefonoEmpleado.setText(String.valueOf( vista.tableEmpleado.getValueAt(filaEditando, 4)));

        vista.tableEmpleado.setEnabled(false);
        vista.txtEmailEmpleado.setEditable(false);

        vista.btnGuardarEmpleado.setVisible(true);
        vista.btnCancelarEmpleado.setVisible(true);
        vista.btnEditarEmpleado.setEnabled(false);
        vista.btnNuevoEmpleado.setEnabled(false);
        vista.btnBorrarBBDDEmpleado.setEnabled(false);
        vista.btnLimpiarEmpleado.setEnabled(false);
        vista.btnBuscarEmpleado.setEnabled(false);
        editando = true;
    }

    private void editarCliente() {
        filaEditando = vista.tableCliente.getSelectedRow();
        if (filaEditando < 0) {
            Util.mensajeError("No hay ningún cliente seleccionado", "Selecciona un cliente");
            return;
        }

        vista.txtNombreCliente.setText(String.valueOf( vista.tableCliente.getValueAt(filaEditando, 1)));
        vista.txtApellidosCliente.setText(String.valueOf( vista.tableCliente.getValueAt(filaEditando, 2)));
        vista.txtEmailCliente.setText(String.valueOf( vista.tableCliente.getValueAt(filaEditando, 3)));
        vista.txtTelefonoCliente.setText(String.valueOf( vista.tableCliente.getValueAt(filaEditando, 4)));

        vista.tableCliente.setEnabled(false);
        vista.txtEmailCliente.setEditable(false);

        vista.btnGuardarCliente.setVisible(true);
        vista.btnCancelarCliente.setVisible(true);
        vista.btnEditarCliente.setEnabled(false);
        vista.btnNuevoCliente.setEnabled(false);
        vista.btnBorrarBBDDCliente.setEnabled(false);
        vista.btnLimpiarCliente.setEnabled(false);
        vista.btnBuscarCliente.setEnabled(false);
        editando = true;
    }

    private void editarProveedor() {
        filaEditando = vista.tableProveedor.getSelectedRow();
        if (filaEditando < 0) {
            Util.mensajeError("No hay ningún proveedor seleccionado", "Selecciona un proveedor");
            return;
        }

        vista.txtNombreProveedor.setText(String.valueOf( vista.tableProveedor.getValueAt(filaEditando, 1)));
        vista.txtContactoProveedor.setText(String.valueOf( vista.tableProveedor.getValueAt(filaEditando, 2)));
        vista.txtEmailProveedor.setText(String.valueOf( vista.tableProveedor.getValueAt(filaEditando, 3)));
        vista.txtTelefonoProveedor.setText(String.valueOf( vista.tableProveedor.getValueAt(filaEditando, 4)));
        vista.txtDireccionProveedor.setText(String.valueOf( vista.tableProveedor.getValueAt(filaEditando, 5)));

        vista.tableProveedor.setEnabled(false);
        vista.txtNombreProveedor.setEditable(false);

        vista.btnGuardarProveedor.setVisible(true);
        vista.btnCancelarProveedor.setVisible(true);
        vista.btnEditarProveedor.setEnabled(false);
        vista.btnNuevoProveedor.setEnabled(false);
        vista.btnBorrarBBDDProveedor.setEnabled(false);
        vista.btnLimpiarProveedor.setEnabled(false);
        vista.btnBuscarProveedor.setEnabled(false);
        editando = true;
    }

    private void editarVenta() {
        filaEditando = vista.tableVenta.getSelectedRow();
        if (filaEditando < 0) {
            Util.mensajeError("No hay ninguna venta seleccionado", "Selecciona una venta");
            return;
        }

        idVenta = (int) vista.tableVenta.getValueAt(filaEditando, 0);
        vista.comboEmpleado.setSelectedItem(vista.tableVenta.getValueAt(filaEditando, 1));
        vista.comboCliente.setSelectedItem(vista.tableVenta.getValueAt(filaEditando, 2));
        resetearVentaProducto();
        vistaVenta.setVisible(true);
    }

    private void editarVentaProducto() {
        filaEditando = vistaVenta.tableVentaProducto.getSelectedRow();
        if (filaEditando < 0) {
            Util.mensajeError("No hay ninguna línea seleccionado", "Selecciona una línea");
            return;
        }

        vistaVenta.txtEmpleadoVenta.setText(String.valueOf(vista.comboEmpleado.getSelectedItem()));
        vistaVenta.txtClienteVenta.setText(String.valueOf(vista.comboCliente.getSelectedItem()));
        vistaVenta.comboProducto.setSelectedItem(
                vistaVenta.tableVentaProducto.getValueAt(filaEditando, 1) + " - "
                        + vistaVenta.tableVentaProducto.getValueAt(filaEditando, 2)
        );
        vistaVenta.txtCantidad.setText(String.valueOf(vistaVenta.tableVentaProducto.getValueAt(filaEditando, 4)));
        vistaVenta.tableVentaProducto.setEnabled(false);
        vistaVenta.btnGuardar.setVisible(true);
        vistaVenta.btnCancelar.setVisible(true);
        vistaVenta.btnEditar.setEnabled(false);
        vistaVenta.btnAnnadir.setEnabled(false);
        vistaVenta.btnBorrarBBDDVentaProducto.setEnabled(false);
        editando = true;
    }
    // EDITAR FIN -----------------------------------------------------------------------------------------


    // GUARDAR -----------------------------------------------------------------------------------------
    private void guardarProducto() {
        if (!validarCamposProducto()) {
            return;
        }

        if (vista.radioHelado.isSelected()) {
            modelo.modificarHelado(
                    Integer.parseInt(String.valueOf(vista.dtmProducto.getValueAt(filaEditando, 0))),
                    Float.parseFloat(vista.txtPrecioProducto.getText()),
                    vista.dateApertura.getDate(), vista.dateCaducidad.getDate(),
                    Integer.parseInt(String.valueOf(vista.comboProveedor.getSelectedItem()).split(" ")[0]),
                    String.valueOf(vista.panelHelado.comboSabor.getSelectedItem()),
                    vista.panelHelado.conAzucarRadioButton.isSelected(),
                    Float.parseFloat(vista.panelHelado.litrosHeladoTxt.getText())
            );
        } else if (vista.radioGofre.isSelected()) {
            modelo.modificarGofre(
                    Integer.parseInt(String.valueOf(vista.dtmProducto.getValueAt(filaEditando, 0))),
                    Float.parseFloat(vista.txtPrecioProducto.getText()),
                    vista.dateApertura.getDate(), vista.dateCaducidad.getDate(),
                    Integer.parseInt(String.valueOf(vista.comboProveedor.getSelectedItem()).split(" ")[0]),
                    String.valueOf(vista.panelGofre.comboToppingGofre.getSelectedItem()),
                    vista.panelGofre.conGlutenRadioButton.isSelected(),
                    String.valueOf(vista.panelGofre.tipoMasaComboBox.getSelectedItem())
            );
        }

        resetearProducto();
        Util.mensajeInfo("Cambios guardados con éxito", "Editar Producto");
    }

    private void guardarEmpleado() {
        if (!validarCamposEmpleado()) {
            return;
        }

        modelo.modificarEmpleado(
                Integer.parseInt(String.valueOf(vista.dtmEmpleado.getValueAt(filaEditando, 0))),
                vista.txtNombreEmpeado.getText(), vista.txtApellidosEmpleado.getText(),
                vista.txtEmailEmpleado.getText(), vista.txtTelefonoEmpleado.getText()
        );

        resetearEmpleado();
        Util.mensajeInfo("Cambios guardados con éxito", "Editar Empleado");
    }

    private void guardarCliente() {
        if (!validarCamposCliente()) {
            return;
        }

        modelo.modificarCliente(
                Integer.parseInt(String.valueOf(vista.dtmCliente.getValueAt(filaEditando, 0))),
                vista.txtNombreCliente.getText(), vista.txtApellidosCliente.getText(),
                vista.txtEmailCliente.getText(), vista.txtTelefonoCliente.getText()
        );

        resetearCliente();
        Util.mensajeInfo("Cambios guardados con éxito", "Editar Cliente");
    }

    private void guardarProveedor() {
        if (!validarCamposProveedor()) {
            return;
        }

        modelo.modificarProveedor(
                Integer.parseInt(String.valueOf(vista.dtmProveedor.getValueAt(filaEditando, 0))),
                vista.txtNombreProveedor.getText(), vista.txtContactoProveedor.getText(), vista.txtEmailProveedor.getText(),
                vista.txtTelefonoProveedor.getText(), vista.txtDireccionProveedor.getText()
        );
        resetearProveedor();
        Util.mensajeInfo("Cambios guardados con éxito", "Editar Proveedor");
    }

    private void guardarVentaProducto() {
        if (!validarCamposVentaProducto()) {
            return;
        }
        modelo.modificarVentaProducto(
                Integer.parseInt(String.valueOf(vistaVenta.dtmVentaProducto.getValueAt(filaEditando, 0))),
                Integer.parseInt(vistaVenta.txtCantidad.getText()), idVenta,
                Integer.parseInt(String.valueOf(vistaVenta.comboProducto.getSelectedItem()).split(" ")[0])
        );
        resetearVentaProducto();
        refrescarVenta();
        modelo.generarVenta(idVenta);
        Util.mensajeInfo("Cambios guardados con éxito", "Editar Venta - Producto");
    }
    // GUARDAR FIN -----------------------------------------------------------------------------------------


    // ELIMINAR -----------------------------------------------------------------------------------------
    private void eliminarProducto() {
        int fila = vista.tableProducto.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún producto seleccionado", "Selecciona un producto");
            return;
        }

        String producto = String.valueOf(vista.tableProducto.getValueAt(fila, 1));

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + producto + "\"?", "Eliminar producto");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarProducto((Integer) vista.tableProducto.getValueAt(fila, 0));
            refrescarProducto();
            if (editando) {
                resetearProducto();
            }
            Util.mensajeInfo("Se ha eliminado \"" + producto + "\"", "Producto Eliminado");
        }
    }

    private void eliminarEmpleado() {
        int fila = vista.tableEmpleado.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún empleado seleccionado", "Selecciona un empleado");
            return;
        }

        String empleado = (String.valueOf( vista.tableEmpleado.getValueAt(fila, 0))) + " - "
                + (String.valueOf( vista.tableEmpleado.getValueAt(fila, 1))) + " "
                + (String.valueOf( vista.tableEmpleado.getValueAt(fila, 2)));

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + empleado + "\"?", "Eliminar empleado");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarEmpleado((Integer) vista.tableEmpleado.getValueAt(fila, 0));
            refrescarEmpleado();
            if (editando) {
                resetearEmpleado();
            }
            Util.mensajeInfo("Se ha eliminado \"" + empleado + "\"", "Empleado Eliminado");
        }
    }

    private void eliminarCliente() {
        int fila = vista.tableCliente.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún cliente seleccionado", "Selecciona un cliente");
            return;
        }

        String cliente = (String.valueOf( vista.tableCliente.getValueAt(fila, 0))) + " - "
                + (String.valueOf( vista.tableCliente.getValueAt(fila, 1))) + " "
                + (String.valueOf( vista.tableCliente.getValueAt(fila, 2)));

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + cliente + "\"?", "Eliminar cliente");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarrCliente((Integer) vista.tableCliente.getValueAt(fila, 0));
            refrescarCliente();
            if (editando) {
                resetearCliente();
            }
            Util.mensajeInfo("Se ha eliminado \"" + cliente + "\"", "Cliente Eliminado");
        }
    }

    private void eliminarProveedor() {
        int fila = vista.tableProveedor.getSelectedRow();
        if (fila < 0) {
            Util.mensajeError("No hay ningún proveedor seleccionado", "Selecciona un proveedor");
            return;
        }

        String proveedor = String.valueOf( vista.tableProveedor.getValueAt(fila, 1));

        int resp = Util.mensajeConfirmación("¿Desea eliminar \"" + proveedor + "\"?", "Eliminar proveedor");
        if (resp == JOptionPane.OK_OPTION) {
            modelo.eliminarProveedor((Integer) vista.tableProveedor.getValueAt(fila, 0));
            refrescarProveedor();
            if (editando) {
                resetearProveedor();
            }
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
            if (editando) {
                resetearVentaProducto();
            }
            Util.mensajeInfo("Se ha eliminado línea nº \"" + linea + "\"", "Línea Eliminada");
        }
        refrescarVentaProducto(idVenta);
        refrescarVenta();
        modelo.generarVenta(idVenta);
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
            modelo.generarVenta(idVenta);
        }
    }
    // BORRAR BBDD FIN -----------------------------------------------------------------------------------------


    // BUSCAR -----------------------------------------------------------------------------------------
    private void buscarProducto() {
        try {
            if (!filtroProducto) {
                vista.tableProducto.setModel(construirTableModeloProducto(modelo.buscarProducto(vista.txtBusquedaProducto.getText())));
                vista.btnBuscarProducto.setText("Todos");
                filtroProducto = true;
            } else {
                vista.tableProducto.setModel(construirTableModeloProducto(modelo.consultarProducto()));
                vista.btnBuscarProducto.setText("Buscar");
                vista.txtBusquedaProducto.setText(null);
                filtroProducto = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarEmpleado() {
        try {
            if (!filtroEmpleado) {
                vista.tableEmpleado.setModel(construirTableModeloEmpleado(modelo.buscarEmpleado(vista.txtBuscarEmpleado.getText())));
                vista.btnBuscarEmpleado.setText("Todos");
                filtroEmpleado = true;
            } else {
                vista.tableEmpleado.setModel(construirTableModeloEmpleado(modelo.consultarEmpleado()));
                vista.btnBuscarEmpleado.setText("Buscar");
                vista.txtBuscarEmpleado.setText(null);
                filtroEmpleado = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarCliente() {
        try {
            if (!filtroCliente) {
                vista.tableCliente.setModel(construirTableModeloCliente(modelo.buscarCliente(vista.txtBuscarCliente.getText())));
                vista.btnBuscarCliente.setText("Todos");
                filtroCliente = true;
            } else {
                vista.tableCliente.setModel(construirTableModeloCliente(modelo.consultarCliente()));
                vista.btnBuscarCliente.setText("Buscar");
                vista.txtBuscarCliente.setText(null);
                filtroCliente = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarProveedor() {
        try {
            if (!filtroProveedor) {
                vista.tableProveedor.setModel(construirTableModeloProveedor(modelo.buscarProveedor(vista.txtBuscarProveedor.getText())));
                vista.btnBuscarProveedor.setText("Todos");
                filtroProveedor = true;
            } else {
                vista.tableProveedor.setModel(construirTableModeloProveedor(modelo.consultarProveedor()));
                vista.btnBuscarProveedor.setText("Buscar");
                vista.txtBuscarProveedor.setText(null);
                filtroProveedor = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarVentaEmpleado() {
        try {
            if (!filtroVentaEmpleado && vista.comboEmpleado.getSelectedIndex() > -1) {
                vista.tableVenta.setModel(construirTableModeloVenta(modelo.buscarVentaEmpleado(Integer.parseInt(String.valueOf(vista.comboEmpleado.getSelectedItem()).split(" ")[0]))));
                vista.btnBuscarVentaEmpleado.setText("Todos");
                filtroVentaEmpleado = true;
                vista.btnBuscarVentaCliente.setText("Buscar");
                filtroVentaCliente = false;
            } else {
                vista.tableVenta.setModel(construirTableModeloVenta(modelo.consultarVenta()));
                vista.btnBuscarVentaEmpleado.setText("Buscar");
                filtroVentaEmpleado = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarVentaCliente() {
        try {
            if (!filtroVentaCliente && vista.comboCliente.getSelectedIndex() > -1) {
                vista.tableVenta.setModel(construirTableModeloVenta(modelo.buscarVentaCliente(Integer.parseInt(String.valueOf(vista.comboCliente.getSelectedItem()).split(" ")[0]))));
                vista.btnBuscarVentaCliente.setText("Todos");
                filtroVentaCliente = true;
                vista.btnBuscarVentaEmpleado.setText("Buscar");
                filtroVentaEmpleado = false;
            } else {
                vista.tableVenta.setModel(construirTableModeloVenta(modelo.consultarVenta()));
                vista.btnBuscarVentaCliente.setText("Buscar");
                filtroVentaCliente = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // BUSCAR FIN -----------------------------------------------------------------------------------------


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
        vistaVenta.addWindowListener(listener);
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

        // Buscar
        vista.btnBuscarProducto.addActionListener(listener);
        vista.btnBuscarProducto.setActionCommand("buscarProducto");
        vista.btnBuscarEmpleado.addActionListener(listener);
        vista.btnBuscarEmpleado.setActionCommand("buscarEmpleado");
        vista.btnBuscarCliente.addActionListener(listener);
        vista.btnBuscarCliente.setActionCommand("buscarCliente");
        vista.btnBuscarProveedor.addActionListener(listener);
        vista.btnBuscarProveedor.setActionCommand("buscarProveedor");
        vista.btnBuscarVentaEmpleado.addActionListener(listener);
        vista.btnBuscarVentaEmpleado.setActionCommand("buscarVentaEmpleado");
        vista.btnBuscarVentaCliente.addActionListener(listener);
        vista.btnBuscarVentaCliente.setActionCommand("buscarVentaCliente");

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
                if (conectado) {
                    modelo.desconectar();
                    vista.itemDesconectar.setText("Conectar");
                    conectado = false;
                } else {
                    modelo.conectar();
                    vista.itemDesconectar.setText("Desconectar");
                    conectado = true;
                }
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
                guardarProducto();
                break;
            case "guardarEmpleado":
                guardarEmpleado();
                break;
            case "guardarCliente":
                guardarCliente();
                break;
            case "guardarProveedor":
                guardarProveedor();
                break;
            case "guardarVentaProducto":
                guardarVentaProducto();
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

            // BUSCAR
            case "buscarProducto":
                buscarProducto();
                break;
            case "buscarEmpleado":
                buscarEmpleado();
                break;
            case "buscarCliente":
                buscarCliente();
                break;
            case "buscarProveedor":
                buscarProveedor();
                break;
            case "buscarVentaEmpleado":
                buscarVentaEmpleado();
                break;
            case "buscarVentaCliente":
                buscarVentaCliente();
                break;

            case "refrescarVenta":
                resetearVenta();
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
        Window ventana = e.getWindow();

        if (ventana == vista) {
            int resp = Util.mensajeConfirmación("¿Desea cerrar la vetana?", "Salir");
            if (resp == JOptionPane.OK_OPTION || resp == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }

        if (ventana == vistaVenta) {
            refrescarVentaProducto(idVenta);
            modelo.generarVenta(idVenta);
            resetearVenta();
            vistaVenta.dispose();
            Util.mensajeInfo("Se ha añadido una venta", "Venta - Producto");
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
