package com.example.alex.buscadormedico;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ver_patologia extends Activity {
    private static String mensajes="",usuario="",idmensaje="",fecha="",mensaje="",destinatario="",leido="",ids="";
    private static String[] mensajes1,usuario1,idmensaje1,asunto1,fecha1,leido1,destinatario1;
    private ListView lista;
    private ListView drawer;
    private Button borrar,agregar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle ;
    private Timer timer;
    private static int asunto;
    private static AutoCompleteTextView buscarT;
    private static Sintoma sintomaBorrar = new Sintoma();
    private static int correcto=0,nuevomensaje=0,veces=0;



    public static void agregarSintoma(){

        ArrayList<Lista_bandeja> e = new ArrayList<Lista_bandeja>();
        try {

            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());


            Connection vDatabaseConnection = DriverManager.getConnection("jdbc:oracle:thin:@//medicserver.no-ip.org:1521/xe", "SYSTEM", "frende55711");

            // PREPARE PL/SQL FUNCTION CALL ("?" IS A PLACEHOLDER FOR RETURN VALUES AND PARAMETER VALUES, NUMBERED FROM LEFT TO RIGHT)
            CallableStatement vStatement = vDatabaseConnection.prepareCall("begin ? := personalizarPatologia( ?, ?, ?); end;");


            // USE EITHER THE "BLOCK" SYNTAX ABOVE, OR "ANSI 92" SYNTAX BELOW
            //CallableStatement vStatement = vDatabaseConnection.prepareCall( "{ ? = call javatest( ?, ? ) }" );

            // SET INPUT PARAMETERS AND DECLARE TYPES FOR RETURN VALUES
            vStatement.registerOutParameter(1, Types.NUMERIC);   // declare type of function return value "begin ? ..."

            vStatement.setString(2, Globals.usu.usuario);              // set value of first function parameter "... javatest( ?, ..."
            vStatement.setString(3, Globals.patologiaActual.get_nombre());
            vStatement.setString(4, buscarT.getText().toString());

            // CALL THE PL/SQL FUNCTION
            vStatement.executeUpdate();

            // GET THE FUNCTION RETURN VALUE AND THE OUT PARAMETER VALUE
            // get the "?" in "begin ? ..."
            asunto  = vStatement.getInt(1);


            // CLOSE THE DATABASE CONNECTION
            vDatabaseConnection.close();


        } catch (Exception E) {
            E.printStackTrace();

        }


    }

    public static void borrarSintoma(){

        ArrayList<Lista_bandeja> e = new ArrayList<Lista_bandeja>();
        try {

            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());


            Connection vDatabaseConnection = DriverManager.getConnection("jdbc:oracle:thin:@//medicserver.no-ip.org:1521/xe", "SYSTEM", "frende55711");

            // PREPARE PL/SQL FUNCTION CALL ("?" IS A PLACEHOLDER FOR RETURN VALUES AND PARAMETER VALUES, NUMBERED FROM LEFT TO RIGHT)
            CallableStatement vStatement = vDatabaseConnection.prepareCall("begin ? := personalizarPatologiaB( ?, ?, ?); end;");


            // USE EITHER THE "BLOCK" SYNTAX ABOVE, OR "ANSI 92" SYNTAX BELOW
            //CallableStatement vStatement = vDatabaseConnection.prepareCall( "{ ? = call javatest( ?, ? ) }" );

            // SET INPUT PARAMETERS AND DECLARE TYPES FOR RETURN VALUES
            vStatement.registerOutParameter(1, Types.NUMERIC);   // declare type of function return value "begin ? ..."

            vStatement.setString(2, Globals.usu.usuario);              // set value of first function parameter "... javatest( ?, ..."
            vStatement.setString(3, Globals.patologiaActual.get_nombre());
            vStatement.setString(3, sintomaBorrar.get_nombre());

            // CALL THE PL/SQL FUNCTION
            vStatement.executeUpdate();

            // GET THE FUNCTION RETURN VALUE AND THE OUT PARAMETER VALUE
            // get the "?" in "begin ? ..."
            asunto  = vStatement.getInt(1);


            // CLOSE THE DATABASE CONNECTION
            vDatabaseConnection.close();


        } catch (Exception E) {
            E.printStackTrace();

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        setContentView(R.layout.activity_ver_patologia);

        Globals.tipomensaje=0;
        borrar = (Button) findViewById(R.id.borrarPatologia);
        agregar = (Button) findViewById(R.id.agregar);
        buscarT = (AutoCompleteTextView) findViewById(R.id.sintomaNuevo);
/*        ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Globals.words);
        buscarT.setAdapter(aaStr);*/


            Globals.actualActivity= 1;
            lista = (ListView) findViewById(R.id.list);

            final Context TV = this;


            drawer = (ListView) findViewById(R.id.drawer);


            final String[] opciones = {"Bandeja de entrada", "Bandeja de salida", "Nuevo mensaje","Menu principal"};
            drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));



            lista.setAdapter(new Lista_adaptador(TV, R.layout.entradapatologia,Globals.patologiaActual.get_sintoma()) {
                @Override
                public void onEntrada(Object entrada, View view) {

                    if (entrada != null) {
                        TextView asunto = (TextView) view.findViewById(R.id.asunto);

                        if (asunto != null)
                            asunto.setText(" ");

                        TextView destinatario = (TextView) view.findViewById(R.id.nombrePatologia);
                        if (destinatario != null)
                            destinatario.setText(((Sintoma)entrada).get_nombre());
                        TextView fecha = (TextView) view.findViewById(R.id.fechaaa);
                        if (fecha != null)
                            fecha.setText(" ");
                       /* try {
                            LinearLayout leido = (LinearLayout) view.findViewById(R.id.colorfondo);

                            if (((Lista_bandeja) entrada).get_leido() == 0)
                            {

                                leido.setBackgroundColor(Color.GRAY);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/



                    }
                }
            });




            drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    try {
                        System.out.println(arg2);
                        if(arg2==0) {
                            Intent intent0 = new Intent(ver_patologia.this, mensajes.class);
                            startActivity(intent0);
                        }
                        else if (arg2==1) {
                            Intent intent = new Intent(ver_patologia.this, salida.class);
                            startActivity(intent);
                        }
                        else if(arg2==2){
                            Intent intent2 = new Intent(ver_patologia.this, envio.class);
                            startActivity(intent2);
                        }
                        else{
                            Intent intent3 = new Intent(ver_patologia.this, servicios.class);
                            startActivity(intent3);
                        }



                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            });

       lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {

               sintomaBorrar = (Sintoma) arg0.getItemAtPosition(arg2);


           }
       });

           agregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     agregarSintoma();
                }
            });



        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                borrarSintoma();

            }
        });

        }catch(Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mensajes, menu);
        return true;
    }
    @Override
    protected void onPostCreate (Bundle savedInstanceState){
        try{
            super.onPostCreate(savedInstanceState);
            toggle.syncState();
        }catch(Exception e){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
