package com.luciaia.heladeriamvc.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VistaVenta extends JFrame {
    private JPanel panel1;

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

    public VistaVenta() {
        super("Venta-Producto");
        initFrame();
    }

    public void initFrame() {
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon("Icono.png").getImage());
        botonesVisibles();

        //cargo table models
        setTableModel();

        pack();
        setVisible(false);
        setLocationRelativeTo(null);
    }

    private void botonesVisibles() {
        btnCancelar.setVisible(false);
        btnGuardar.setVisible(false);
    }

    private void setTableModel() {
        dtmVentaProducto = new DefaultTableModel();
        tableVentaProducto.setModel(dtmVentaProducto);
    }


}
