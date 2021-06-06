package com.rrvq.carterapersonalpro;

class GrupoHijo {
    // Variables gasto.
    private final int rowid;
    private final float montoHijo;
    private final String descripcionHijo;
    private final String fechaHijo;
    private final String tipo;

    // Constructores.
    public GrupoHijo(int rowid, float montoHijo, String descripcionHijo, String fechaHijo, String tipo) {
        this.rowid = rowid;
        this.montoHijo = montoHijo;
        this.descripcionHijo = descripcionHijo;
        this.fechaHijo = fechaHijo;
        this.tipo = tipo;
    }

    // RETORNAMOS los valores.
    public float getRowid() {
        return rowid;
    }

    public float getMontoHijo() {
        return montoHijo;
    }

    public String getDescripcionHijo() {
        return descripcionHijo;
    }

    public String getFechaHijo() {
        return fechaHijo;
    }

    public String getTipo() {
        return tipo;
    }
}