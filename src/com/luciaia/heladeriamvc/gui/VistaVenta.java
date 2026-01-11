package com.luciaia.heladeriamvc.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VistaVenta extends JDialog {
    private JPanel panel1;
    private Frame owner;

    JTextField txtEmpleadoVenta;
    JTextField txtClienteVenta;
    JComboBox comboProducto;
    JTextField txtCantidad;
    JButton btnAnnadir;
    JButton btnCancelar;
    JButton btnGuardar;
    JButton btnEditar;
    JButton btnEliminar;
    JButton btnBorrarBBDDVentaProducto;

    // tablas
    JTable tableVentaProducto;
    DefaultTableModel dtmVentaProducto;

    public VistaVenta(Frame owner) {
        super(owner, "Venta-Producto", true);
        this.owner = owner;
        initDialog();
    }

    public void initDialog() {
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image icono = Toolkit.getDefaultToolkit().getImage(
                getClass().getClassLoader().getResource("Icono.png")
        );

        setIconImage(icono);
        botonesVisibles();

        //cargo table models
        setTableModel();

        pack();
        setVisible(false);
        setLocationRelativeTo(owner);
    }

    private void botonesVisibles() {
        btnCancelar.setVisible(false);
        btnGuardar.setVisible(false);
    }

    private void setTableModel() {
        dtmVentaProducto = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVentaProducto.setModel(dtmVentaProducto);
    }


}
