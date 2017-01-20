package com.example.alex.buscadormedico;

import java.sql.Connection;
import java.util.Timer;

/**
 * Created by Alex on 06/07/2015.
 */
public class Globals {
    public static usuario usu = new usuario("","","","","","");
    public static Connection usu2;
    public static Lista_bandeja f;
    public static int veces=0;
    public static Timer timer = null;
    public static int actualActivity=0,tipomensaje=0,leido=0;
    public static  Enfermedad patologiaActual = new Enfermedad();
    public static String[] words;
}
