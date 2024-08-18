package com.example.examen3erp_victormadrid;

import java.util.Date;

public class Entrevista {
    int idOrden;
    String descripcion, periodista;
    Date fecha;
    byte[] imagen, audio;

    public Entrevista() {
    }

    public int getIdOrden() {
        return idOrden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPeriodista() {
        return periodista;
    }

    public Date getFecha() {
        return fecha;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }
}
