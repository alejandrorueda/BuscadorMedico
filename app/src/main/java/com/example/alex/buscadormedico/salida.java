package com.example.alex.buscadormedico;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.ArrayList;


public class salida extends Activity {
    private static String mensajes="",usuario="",idmensaje="",asunto="",fecha="",mensaje="",destinatario="",leido="";
    private static String[] mensajes1,usuario1,idmensaje1,asunto1,fecha1,leido1,destinatario1;
    private ListView lista;
    private ListView drawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle ;

    public static ArrayList<Lista_bandeja> obtenerMensajes(){

        ArrayList<Lista_bandeja> e = new ArrayList<Lista_bandeja>();
        try {

            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());


            Connection vDatabaseConnection = DriverManager.getConnection("jdbc:oracle:thin:@//medicserver.no-ip.org:1521/xe", "SYSTEM", "frende55711");

            // PREPARE PL/SQL FUNCTION CALL ("?" IS A PLACEHOLDER FOR RETURN VALUES AND PARAMETER VALUES, NUMBERED FROM LEFT TO RIGHT)
            CallableStatement vStatement = vDatabaseConnection.prepareCall("begin ? := obtenerMensajeSalida( ?, ?, ?, ?, ?, ? ); end;");


            // USE EITHER THE "BLOCK" SYNTAX ABOVE, OR "ANSI 92" SYNTAX BELOW
            //CallableStatement vStatement = vDatabaseConnection.prepareCall( "{ ? = call javatest( ?, ? ) }" );

            // SET INPUT PARAMETERS AND DECLARE TYPES FOR RETURN VALUES
            vStatement.registerOutParameter(1, Types.VARCHAR);   // declare type of function return value "begin ? ..."

            vStatement.setString(2, Globals.usu.usuario);              // set value of first function parameter "... javatest( ?, ..."
            vStatement.registerOutParameter(2, Types.VARCHAR);
            vStatement.registerOutParameter(3, Types.VARCHAR);
            vStatement.registerOutParameter(4, Types.VARCHAR);
            vStatement.registerOutParameter(5, Types.VARCHAR);
            vStatement.registerOutParameter(6, Types.VARCHAR);
            vStatement.registerOutParameter(7, Types.VARCHAR);

            // CALL THE PL/SQL FUNCTION
            vStatement.executeUpdate();

            // GET THE FUNCTION RETURN VALUE AND THE OUT PARAMETER VALUE
            // get the "?" in "begin ? ..."
            asunto  = vStatement.getString(1);
            idmensaje  = vStatement.getString(3);
            fecha = vStatement.getString(4);
            mensaje = vStatement.getString(5);
            leido = vStatement.getString(6);
            destinatario = vStatement.getString(7);

            // CLOSE THE DATABASE CONNECTION
            vDatabaseConnection.close();

            ;
            asunto1 = obtenerInformacion(asunto);
            idmensaje1 = obtenerInformacion(idmensaje);
            fecha1 = obtenerInformacion(fecha);
            mensajes1 = obtenerInformacion(mensaje);
            leido1 = obtenerInformacion(leido);

            destinatario1 = obtenerInformacion(destinatario);
            System.out.println(idmensaje1.length);
            System.out.println(asunto1.length);

            for (int i = 0; i <idmensaje1.length; i++) {
                e.add(new Lista_bandeja(idmensaje1[i],destinatario1[i], asunto1[i], mensajes1[i], fecha1[i], Globals.usu.usuario,Integer.parseInt(leido1[1])));
            }
        } catch (Exception E) {
            E.printStackTrace();

        }
        return e;

    }

    public static String[] obtenerInformacion(String cadena) {

        String palabra="";
        ArrayList<String> list = new ArrayList<String>();

        for (int i=1;i<(cadena.length()-1);i++){
            palabra = palabra + cadena.charAt(i);
            if (cadena.charAt(i+1)==';') {
                list.add(palabra);

                palabra ="";
                i++;

            }

        }

        return list.toArray(new String[list.size()]);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajes);
        Globals.actualActivity= 0;
        Globals.tipomensaje=1;
        try {
            lista = (ListView) findViewById(R.id.list);
            final Context TV = this;

            ArrayList<Lista_bandeja> datos = obtenerMensajes();
            drawer = (ListView) findViewById(R.id.drawer);


            final String[] opciones = {"Bandeja de entrada", "Bandeja de salida", "Nuevo mensaje","Menu principal"};
            drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));

            lista.setAdapter(new Lista_adaptador(TV, R.layout.entradas_bandeja, datos) {
                @Override
                public void onEntrada(Object entrada, View view) {
                    if (entrada != null) {
                        TextView asunto = (TextView) view.findViewById(R.id.asunto);

                        if (asunto != null)
                            asunto.setText(((Lista_bandeja) entrada).get_asunto());

                        TextView destinatario = (TextView) view.findViewById(R.id.destinatario);
                        if (destinatario != null)
                            destinatario.setText(((Lista_bandeja) entrada).get_destinatario());
                        TextView fecha = (TextView) view.findViewById(R.id.fechaaa);
                        if (fecha != null)
                            fecha.setText(((Lista_bandeja) entrada).get_fecha());


                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

        drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    System.out.println(arg2);
                    if(arg2==0) {
                        Intent intent0 = new Intent(salida.this, mensajes.class);
                        startActivity(intent0);
                    }
                    else if (arg2==1) {
                        Intent intent = new Intent(salida.this, salida.class);
                        startActivity(intent);
                    }
                    else if(arg2==2){
                        Intent intent2 = new Intent(salida.this, envio.class);
                        startActivity(intent2);
                    }
                    else{
                        Intent intent3 = new Intent(salida.this, servicios.class);
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

                Lista_bandeja f = (Lista_bandeja)  arg0.getItemAtPosition(arg2);
                Globals.f=f;
                Intent intent = new Intent(salida.this,ver_mensaje.class);
                System.out.println("sdfghfdsfghg");
                startActivity(intent);
            }
        });

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
