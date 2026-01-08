package com.luciaia.heladeriamvc.util;

import javax.swing.*;
import java.time.LocalDate;

public class Util {
    public static void mensajeError(String msg, String titulo) {
        JOptionPane.showMessageDialog(null, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public static void mensajeInfo(String msg, String titulo) {
        JOptionPane.showMessageDialog(null, msg, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static int mensajeConfirmación(String msg, String titulo) {
        return JOptionPane.showConfirmDialog(null, msg, titulo, JOptionPane.YES_NO_OPTION);
    }

    public static boolean esFloat(String numero) {
        try {
            Float.parseFloat(numero);
            return true;
        } catch (NumberFormatException ne) {
            return false;
        }
    }

    public static boolean esEntero(String numero) {
        try {
            Integer.parseInt(numero);
            return true;
        } catch (NumberFormatException ne) {
            return false;
        }
    }

    public static boolean longitudCorrecta(String texto, int longitud) {
        return texto == null || texto.length() <= longitud;
    }

    public static boolean validarTelefono(String telefono) {
        return telefono == null || telefono.matches("\\d{9}");
    }

    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailClean = email.trim();
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return emailClean.matches(regex);
    }

    public static boolean consultaValida(String input) {
        return !input.contains(";") && !input.toLowerCase().contains("delete");
    }

    public static boolean fechaValida(String input) {
        return input.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    }

    public static boolean rangoFechasValido(LocalDate fechaInicio, LocalDate fechaFin) {
        if (!fechaInicio.isBefore(fechaFin)) {
            System.out.println("La fecha de inicio debe ir antes que la de fin");
            return false;
        }
        return true;
    }
}
