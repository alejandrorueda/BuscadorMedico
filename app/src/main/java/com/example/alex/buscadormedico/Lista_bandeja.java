package com.example.alex.buscadormedico;

/**
 * Created by Alex on 09/07/2015.
 */
public class Lista_bandeja {
    private String destinatario,id;
    private String asunto;
    private String mensaje;
    private String fecha;
    private String emisor;
    private int leido;
    public Lista_bandeja(){

    }
    public Lista_bandeja(String id,String destinatario, String asunto, String mensaje, String fecha, String emiso,int leido) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.emisor = emisor;
        this.id=id;
        this.leido=leido;

    }

    public String get_asunto() {
        return asunto;
    }


    public String get_destinatario() {
        return destinatario;
    }

    public String get_emsior() {
        return emisor;
    }

    public String get_fecha() {
        return fecha;
    }

    public String get_mensaje() {
        return mensaje;
    }
    public String get_id() {
        return id;
    }
    public int get_leido() {
        return leido;
    }




}