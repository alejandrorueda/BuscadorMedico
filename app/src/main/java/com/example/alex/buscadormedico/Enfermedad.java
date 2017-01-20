package com.example.alex.buscadormedico;

import java.util.ArrayList;

/**
 * Created by Alex on 12/07/2015.
 */
public class Enfermedad {
    private String nombre;
    private ArrayList<Sintoma> sintomas=new ArrayList<Sintoma>();

    public Enfermedad () {
        this.nombre = nombre;

    }
    public Enfermedad (String nombre) {
        this.nombre = nombre;

    }
    public Enfermedad (String nombre,ArrayList<Sintoma> sintomas) {
        this.nombre = nombre;
        this.sintomas = sintomas;

    }
    public void agregarSintoma (String nombre){
        sintomas.add(new Sintoma(nombre));
    }
    public String get_nombre() {
        return nombre;
    }

    public ArrayList<Sintoma> get_sintoma() {
        return sintomas;
    }


}