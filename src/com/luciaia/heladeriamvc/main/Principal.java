package com.luciaia.heladeriamvc.main;

import com.luciaia.heladeriamvc.gui.Controlador;
import com.luciaia.heladeriamvc.gui.Modelo;
import com.luciaia.heladeriamvc.gui.Vista;

public class Principal {
    public static void main(String[] args) {
        Modelo modelo = new Modelo();
        Vista vista = new Vista();
        Controlador controlador = new Controlador(modelo,vista);
    }
}
