package com.luciaia.heladeriamvc.gui.base;

public enum Topping {
    AZUCARGLASS("Azúcar glass"),
    MIEL("Miel"),
    NUTELLA("Nutella"),
    SIROPEDEFRESA("Sirope de fresa"),
    CARAMELO("Caramelo"),
    SIROPEDEARCE("Sirope de arce (maple)"),
    FRESAS("Fresas"),
    PLATANO("Plátano"),
    NATA("Nata"),
    PISATCHOS("Pistachos"),
    OREO("Oreo"),
    LOTUS("Lotus"),
    LACASITOS("Lacasitos");

    private String valor;

    Topping(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
