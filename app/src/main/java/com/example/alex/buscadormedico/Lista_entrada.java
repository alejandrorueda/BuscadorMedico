package com.example.alex.buscadormedico;

/**
 * Created by Alex on 15/12/2014.
 */
public class Lista_entrada {
    private String textoEncima;
    private String textoDebajo;

    public Lista_entrada (String textoEncima, String textoDebajo) {
        this.textoEncima = textoEncima;
        this.textoDebajo = textoDebajo;
    }

    public String get_textoEncima() {
        return textoEncima;
    }

    public String get_textoDebajo() {
        return textoDebajo;
    }


}
