package com.rrvq.carterapersonalpro;

class GrupoPadre {
    // Clase para modelar el Padre.


    // Variables gasto.
    private final int imgPadre;
    private final String nombrePadre;
    private final float montoPadre;
    private final int gastoIngreso;

    // Constructores.
    public GrupoPadre(int imgPadre, String nombrePadre, float montoPadre, int gastoIngreso) {
        this.imgPadre = imgPadre;
        this.nombrePadre = nombrePadre;
        this.montoPadre = montoPadre;
        this.gastoIngreso = gastoIngreso;
    }

    // RETORNAMOS los valores.
    public int getimgPadre() {
        return imgPadre;
    }

    public String getnombrePadre() {
        return nombrePadre;
    }

    public float getmontoPadre() {
        return montoPadre;
    }

    public int getgastoIngreso() {
        return gastoIngreso;
    }
}