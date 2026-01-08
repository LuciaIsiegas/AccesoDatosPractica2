package com.luciaia.heladeriamvc.gui;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class Controlador implements ActionListener, ItemListener, ListSelectionListener, WindowListener {
    private Modelo modelo;
    private Vista vista;
    private VistaVenta vistaVenta;
    boolean refrescar;
    int idVenta;

    public Controlador(Modelo modelo, Vista vista, VistaVenta vistaVenta) {
        this.modelo = modelo;
        this.vista = vista;
        this.vistaVenta = vistaVenta;
        this.idVenta = 0;
        modelo.conectar();
        setOptions();
        addActionListeners(this);
        addItemListeners(this);
        addWindowListeners(this);
        refrescarTodo();
        iniciar();
    }

    // COMPROBAR CAMPOS OBLIGATORIOS ---------------------------------------------------------------------------------------
    private boolean hayCamposVaciosProducto() {
        try {
            if (!vista.txtNombreProducto.getText().isEmpty()
                    && !vista.txtPrecioProducto.getText().isEmpty()
                    && !vista.dateCaducidad.getText().isEmpty()
                    && !vista.comboProveedor.getSelectedItem().toString().isEmpty()) {
                if (vista.radioHelado.isSelected()) {
                    if (!vista.panelHelado.comboSabor.getSelectedItem().toString().isEmpty()
                            && !vista.panelHelado.litrosHeladoTxt.getText().isEmpty()) {
                        return false;
                    }
                } else {
                    if (!vista.panelGofre.comboToppingGofre.getSelectedItem().toString().isEmpty()
                            && !vista.panelGofre.tipoMasaComboBox.getSelectedItem().toString().isEmpty()) {
                        return false;
                    }
                }
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
    // COMPROBAR CAMPOS OBLIGATORIOS FIN ---------------------------------------------------------------------------------------


    // LIMPIAR PANELES ---------------------------------------------------------------------------------------
    private void resetearProducto() {
        vista.txtNombreProducto.setEditable(true);
        vista.radioHelado.setEnabled(true);
        vista.radioGofre.setEnabled(true);

        vista.btnGuardarProducto.setVisible(false);
        vista.btnCancelarProducto.setVisible(false);
        vista.btnEditaProducto.setVisible(true);
        vista.btnNuevoProducto.setVisible(true);
        vista.btnLimpiarProducto.setVisible(true);
        vista.btnEliminarProducto.setVisible(true);
        vista.btnBorrarBBDDProducto.setVisible(true);
        vista.txtBusquedaProducto.setText(null);

        limpiarCamposProducto();
        refrescarProducto();
    }

    private void resetearEmpleado() {
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
        limpiarCamposVenta();
        refrescarVenta();
    }

    private void resetearVentaProducto() {
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

        vista.panelHelado.comboSabor.setSelectedIndex(0);
        vista.panelHelado.conAzucarRadioButton.setSelected(true);
        vista.panelHelado.litrosHeladoTxt.setText(null);

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


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

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
