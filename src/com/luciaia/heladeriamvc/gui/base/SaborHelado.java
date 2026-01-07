package com.luciaia.heladeriamvc.gui.base;

public enum SaborHelado {
    VAINILLA("Vainilla"),
    CHOCOLATE("Chocolate"),
    FRESA("Fresa"),
    LIMON("Limón"),
    NATA("Nata"),
    MENTA("Menta"),
    MANGO("Mango"),
    MARACUYA("Maracuyá"),
    COCO("Coco"),
    PLATANO("Plátano"),
    FRAMBUESA("Frambuesa"),
    PINNA("Piña"),
    DULCEDELECHE("Dulce de leche"),
    OREO("Oreo"),
    BROWNIE("Brownie"),
    TIRAMISU("Tiramisú"),
    CHESEECAKE("Cheesecake"),
    CARAMELOSALADO("Caramelo salado"),
    PISTACHO("Pistacho"),
    MATCHA("Matcha"),
    CAFE("Café");

    private String valor;

    SaborHelado(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
