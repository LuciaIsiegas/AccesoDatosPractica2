package com.luciaia.heladeriamvc.gui;

import com.github.lgooddatepicker.components.DatePicker;
import com.luciaia.heladeriamvc.gui.base.SaborHelado;
import com.luciaia.heladeriamvc.gui.base.TipoMasa;
import com.luciaia.heladeriamvc.gui.base.Topping;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Vista extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private final static String TITULO_FRAME = "Heladería MVC";

    // productos
    JPanel JPanelProducto;
    JRadioButton radioHelado;
    JRadioButton radioGofre;
    JButton btnLimpiarProducto;
    JButton btnCancelarProducto;
    JButton btnGuardarProducto;
    JButton btnNuevoProducto;
    JButton btnEditaProducto;
    JButton btnEliminarProducto;
    JButton btnBorrarBBDDProducto;
    JPanel panelCard;
    PanelHelado panelHelado;
    PanelGofre panelGofre;
    JTextField txtNombreProducto;
    JTextField txtPrecioProducto;
    DatePicker dateCaducidad;
    DatePicker dateApertura;
    JComboBox comboProveedor;

    // empleados
    JPanel JPanelEmpleado;
    JTextField txtNombreEmpeado;
    JTextField txtApellidosEmpleado;
    JTextField txtEmailEmpleado;
    JTextField txtTelefonoEmpleado;
    JButton btnLimpiarEmpleado;
    JButton btnCancelarEmpleado;
    JButton btnGuardarEmpleado;
    JButton btnNuevoEmpleado;
    JButton btnEditarEmpleado;
    JButton btnEliminarEmpleado;
    JButton btnBorrarBBDDEmpleado;

    // clientes
    JPanel JPanelCliente;
    JTextField txtNombreCliente;
    JTextField txtApellidosCliente;
    JTextField txtEmailCliente;
    JTextField txtTelefonoCliente;
    JButton btnLimpiarCliente;
    JButton btnNuevoCliente;
    JButton btnEditarCliente;
    JButton btnEliminarCliente;
    JButton btnBorrarBBDDCliente;
    JButton btnCancelarCliente;
    JButton btnGuardarCliente;

    // Proveedores
    JPanel JPanelProveedor;
    JTextField txtNombreProveedor;
    JTextField txtContactoProveedor;
    JTextField txtEmailProveedor;
    JTextField txtTelefonoProveedor;
    JTextField txtDireccionProveedor;
    JButton btnLimpiarProveedor;
    JButton btnNuevoProveedor;
    JButton btnEditarProveedor;
    JButton btnEliminarProveedor;
    JButton btnBorrarBBDDProveedor;
    JButton btnCancelarProveedor;
    JButton btnGuardarProveedor;

    // Ventas
    JPanel JPanelVenta;
    JComboBox comboEmpleado;
    JComboBox comboCliente;
    JButton btnNuevoVenta;
    JButton btnEditarVenta;
    JButton btnEliminarVenta;
    JButton btnBorrarBBDDVenta;

    // Busqueda
    JTextField txtBusquedaProducto;
    JButton btnBuscarProducto;
    JTextField txtBuscarEmpleado;
    JButton btnBuscarEmpleado;
    JTextField txtBuscarCliente;
    JButton btnBuscarCliente;
    JTextField txtBuscarProveedor;
    JButton btnBuscarProveedor;
    JButton btnBuscarVentaEmpleado;
    JButton btnBuscarVentaCliente;

    // tablas
    JTable tableProducto;
    JTable tableEmpleado;
    JTable tableCliente;
    JTable tableProveedor;
    JTable tableVenta;
    JButton btnRefrescarVenta;

    DefaultTableModel dtmProducto;
    DefaultTableModel dtmEmpleado;
    DefaultTableModel dtmCliente;
    DefaultTableModel dtmProveedor;
    DefaultTableModel dtmVenta;

    //menubar
    JMenuItem itemOpciones;
    JMenuItem itemDesconectar;
    JMenuItem itemSalir;

    //cuadro dialogo
    OptionDialog optionDialog;
    JDialog adminPasswordDialog;
    JButton btnValidate;
    JPasswordField adminPassword;


    public Vista() {
        super(TITULO_FRAME);
        initFrame();
    }

    public void initFrame() {
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Image icono = Toolkit.getDefaultToolkit().getImage(
                getClass().getClassLoader().getResource("Icono.png")
        );

        setIconImage(icono);

        radioHelado.setSelected(true);
        crearPanelCard();
        botonesVisibles();

        CardLayout cl = (CardLayout) (panelCard.getLayout());
        cl.show(panelCard, "Helado");

        //creo cuadro dialogo
        optionDialog = new OptionDialog(this);

        //llamo menu
        setMenu();

        //llamo cuadro dialogo admin
        setAdminDialog();

        //cargo enumerados
        setEnumComboBox();

        //cargo table models
        setTableModels();

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void setMenu() {
        JMenuBar mbBar = new JMenuBar();
        JMenu menu = new JMenu("Archivo");
        itemOpciones = new JMenuItem("Opciones");
        itemOpciones.setActionCommand("Opciones");
        itemDesconectar = new JMenuItem("Desconectar");
        itemDesconectar.setActionCommand("Desconectar");
        itemSalir = new JMenuItem("Salir");
        itemSalir.setActionCommand("Salir");

        mbBar.setBackground(new Color(255, 203, 133));
        itemOpciones.setBackground(new Color(255, 203, 133));
        itemDesconectar.setBackground(new Color(255, 203, 133));
        itemSalir.setBackground(new Color(255, 203, 133));

        menu.add(itemOpciones);
        menu.add(itemDesconectar);
        menu.add(itemSalir);
        mbBar.add(menu);
        mbBar.add(Box.createHorizontalGlue());
        this.setJMenuBar(mbBar);
    }

    private void setAdminDialog() {
        btnValidate = new JButton("Validar");
        btnValidate.setActionCommand("abrirOpciones");
        adminPassword = new JPasswordField();

        //dimension al cuadro de texto
        adminPassword.setPreferredSize(new Dimension(100, 26));
        Object[] options = new Object[]{adminPassword, btnValidate};
        JOptionPane jop = new JOptionPane("Introduce la contraseña", JOptionPane.WARNING_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, options);
        adminPasswordDialog = new JDialog(this, "Opciones", true);
        adminPasswordDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        adminPasswordDialog.setContentPane(jop);
        adminPasswordDialog.pack();
        adminPasswordDialog.setLocationRelativeTo(this);
    }

    private void setEnumComboBox() {
        for (SaborHelado constant : SaborHelado.values()) {
            panelHelado.comboSabor.addItem(constant.getValor());
        }
        panelHelado.comboSabor.setSelectedIndex(-1);

        for (TipoMasa constant : TipoMasa.values()) {
            panelGofre.tipoMasaComboBox.addItem(constant.getValor());
        }
        panelGofre.tipoMasaComboBox.setSelectedIndex(-1);

        for (Topping constant : Topping.values()) {
            panelGofre.comboToppingGofre.addItem(constant.getValor());
        }
        panelGofre.comboToppingGofre.setSelectedIndex(-1);

        for (TipoMasa constant : TipoMasa.values()) {
            panelGofre.tipoMasaComboBox.addItem(constant.getValor());
        }
        panelGofre.tipoMasaComboBox.setSelectedIndex(-1);
    }

    private void setTableModels() {
        dtmProducto = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProducto.setModel(dtmProducto);
        dtmEmpleado = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableEmpleado.setModel(dtmEmpleado);
        dtmCliente = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCliente.setModel(dtmCliente);
        dtmProveedor = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProveedor.setModel(dtmProveedor);
        dtmVenta = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVenta.setModel(dtmVenta);
    }

    private void botonesVisibles() {
        btnCancelarProducto.setVisible(false);
        btnGuardarProducto.setVisible(false);
        btnCancelarEmpleado.setVisible(false);
        btnGuardarEmpleado.setVisible(false);
        btnCancelarCliente.setVisible(false);
        btnGuardarCliente.setVisible(false);
        btnCancelarProveedor.setVisible(false);
        btnGuardarProveedor.setVisible(false);
    }

    public void crearPanelCard() {
        panelHelado = new PanelHelado();
        panelHelado.conAzucarRadioButton.setSelected(true);
        panelCard.add(panelHelado.panel1, "Helado");

        panelGofre = new PanelGofre();
        panelGofre.conGlutenRadioButton.setSelected(true);
        panelCard.add(panelGofre.panel1, "Gofre");
    }

    public void bloquearVista() {
        setEnabledRecursivo(panel1, false);
    }

    public void desbloquearVista() {
        setEnabledRecursivo(panel1, true);
    }

    private void setEnabledRecursivo(Container container, boolean enabled) {
        for (Component c : container.getComponents()) {
            c.setEnabled(enabled);
            if (c instanceof Container) {
                setEnabledRecursivo((Container) c, enabled);
            }
        }
    }

    public void bloquearVistaExceptoMenu() {
        bloquearVista();
        itemDesconectar.setEnabled(true);
        itemSalir.setEnabled(true);
    }
}
