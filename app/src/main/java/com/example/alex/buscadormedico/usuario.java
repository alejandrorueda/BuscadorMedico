package com.example.alex.buscadormedico;

/**
 * Created by Alex on 06/07/2015.
 */
public class usuario {
    public String usuario,nombre,contrasena,estado,ocupacion,email;

    public usuario(){

    }
    public usuario(String usuario,String nombre,String contrasena,String estado,String ocupacion,String email){
        this.usuario=usuario;
        this.nombre=nombre;
        this.contrasena=contrasena;
        this.estado=estado;
        this.ocupacion=ocupacion;
        this.email=email;
    }
}
