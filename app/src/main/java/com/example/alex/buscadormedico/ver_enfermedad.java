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
import android.widget.TextView;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import oracle.jdbc.OracleTypes;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;


public class ver_enfermedad extends Activity {
    private static String mensajes="",usuario="",idmensaje="",asunto="",fecha="",mensaje="",destinatario="",leido="",ids="";
    private static String[] mensajes1,usuario1,idmensaje1,asunto1,fecha1,leido1,destinatario1;
    private static ListView lista ;
    private ListView drawer;
    private Button buscar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle ;
    private AutoCompleteTextView cadenaEnf;
    private Timer timer;
    private static int correcto=0,nuevomensaje=0,veces=0;
    private static ArrayList<Enfermedad> listaEnfermedades = new ArrayList<Enfermedad>();


    public static void obtenerPatologia(String busqueda,final Context TV){

        ArrayList<Lista_bandeja> e = new ArrayList<Lista_bandeja>();
        try {
            listaEnfermedades = new ArrayList<Enfermedad>();

            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());


            Connection vDatabaseConnection = DriverManager.getConnection("jdbc:oracle:thin:@//medicserver.no-ip.org:1521/xe", "SYSTEM", "frende55711");
            final String typeName = "patologiaobj";
            final String typeTableName = "T_DEMO_OBJECTS";
            final StructDescriptor structDescriptor = StructDescriptor.createDescriptor(typeName.toUpperCase(), vDatabaseConnection);
            final ResultSetMetaData metaData = structDescriptor.getMetaData();

            // PREPARE PL/SQL FUNCTION CALL ("?" IS A PLACEHOLDER FOR RETURN VALUES AND PARAMETER VALUES, NUMBERED FROM LEFT TO RIGHT)
            CallableStatement cs = vDatabaseConnection.prepareCall("begin ? := encontrarEnfermedad( ? ); end;");


            // USE EITHER THE "BLOCK" SYNTAX ABOVE, OR "ANSI 92" SYNTAX BELOW
            //CallableStatement vStatement = vDatabaseConnection.prepareCall( "{ ? = call javatest( ?, ? ) }" );

            // SET INPUT PARAMETERS AND DECLARE TYPES FOR RETURN VALUES
            cs.registerOutParameter(1, OracleTypes.ARRAY, "PATOLOGIA1_NTABTYP");
            cs.setString(2,busqueda);// declare type of function return value "begin ? ..."

                       // set value of first function parameter "... javatest( ?, ..."
            /*cs.registerOutParameter(2, Types.STRUCT);*/

            // CALL THE PL/SQL FUNCTION

            cs.executeUpdate();

            // GET THE FUNCTION RETURN VALUE AND THE OUT PARAMETER VALUE
            // get the "?" in "begin ? ..."
            Object[] data = (Object[]) ((Array) cs.getObject(1)).getArray();
            Object[] sintomas2;

            for(Object tmp : data) {
                Struct row = (Struct) tmp;
                // Attributes are index 1 based...
                int idx = 1;
                Enfermedad patologia = new Enfermedad();
                for(Object attribute : row.getAttributes()) {


                    if(idx==2) {
                        sintomas2 = (Object[]) ((Array) attribute).getArray();
                        for (Object sintoma : sintomas2) {
                            Struct row2 = (Struct) sintoma;
                            for (Object nombre : row2.getAttributes()) {
                                System.out.println(nombre.toString());
                                patologia.agregarSintoma(nombre.toString());

                            }
                        }
                    }
                    else  patologia = new Enfermedad(attribute.toString());
                    ++idx;
                }
                listaEnfermedades.add(patologia);
                System.out.println("---");
            }
            System.out.println(cs.getObject(1).getClass());



            // CLOSE THE DATABASE CONNECTION
            vDatabaseConnection.close();



        } catch (Exception E) {
            E.printStackTrace();

        }

        lista.setAdapter(new Lista_adaptador(TV, R.layout.entradas_bandeja, listaEnfermedades) {
            @Override
            public void onEntrada(Object entrada, View view) {

                if (entrada != null) {
                    TextView asunto = (TextView) view.findViewById(R.id.asunto);

                    if (asunto != null)
                        asunto.setText(" ");

                    TextView destinatario = (TextView) view.findViewById(R.id.destinatario);
                    if (destinatario != null)
                        destinatario.setText(((Enfermedad) entrada).get_nombre());
                    TextView fecha = (TextView) view.findViewById(R.id.fechaaa);
                    if (fecha != null)
                        fecha.setText(" ");
                    try {
                        LinearLayout leido = (LinearLayout) view.findViewById(R.id.colorfondo);


                    }catch(Exception e){
                        e.printStackTrace();
                    }



                }
            }
        });


    }

    public static String[] obtenerInformacion(String cadena) {

        String palabra="";
        ArrayList<String> list = new ArrayList<String>();

        for (int i=1;i<(cadena.length());i++){
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
        setContentView(R.layout.activity_ver_enfermedad);
        Globals.tipomensaje=0;
        buscar = (Button)findViewById(R.id.buscarPat);
        lista = (ListView) findViewById(R.id.list4);
        cadenaEnf = (AutoCompleteTextView)findViewById(R.id.cadenaBusqueda);
        try {

            Globals.actualActivity= 1;
            lista = (ListView) findViewById(R.id.list4);

            final Context TV = this;


            drawer = (ListView) findViewById(R.id.drawer);


            final String[] opciones = {"Bandeja de entrada", "Bandeja de salida", "Nuevo mensaje","Menu principal"};
            drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));








            buscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obtenerPatologia(cadenaEnf.getText().toString(),TV);
                }
            });

            drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    try {
                        System.out.println(arg2);
                        if(arg2==0) {
                            Intent intent0 = new Intent(ver_enfermedad.this, mensajes.class);
                            startActivity(intent0);
                        }
                        else if (arg2==1) {
                            Intent intent = new Intent(ver_enfermedad.this, salida.class);
                            startActivity(intent);
                        }
                        else if(arg2==2){
                            Intent intent2 = new Intent(ver_enfermedad.this, envio.class);
                            startActivity(intent2);
                        }
                        else{
                            Intent intent3 = new Intent(ver_enfermedad.this, servicios.class);
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

                Globals.patologiaActual = (Enfermedad) arg0.getItemAtPosition(arg2);

                Intent intent = new Intent(ver_enfermedad.this, ver_patologia.class);
                startActivity(intent);
            }
        });

/*
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    for (int i = 0; i < lista.getAdapter().getCount(); i++) {
                        Lista_bandeja f = (Lista_bandeja) lista.getItemAtPosition(i);
                       ViewGroup row = (ViewGroup )lista.getChildAt(i).getClass();

                        CheckBox tvTest = (CheckBox) row.findViewById(R.id.seleccionar2);
                        if (tvTest.isChecked()) {
                            ids = ids + " " + f.get_id();
                        }
                    }
                    borrar(ids);
                    ids = "";
                    if (correcto == 0) {
                        Toast.makeText(getApplicationContext(), "No se borraron", Toast.LENGTH_SHORT).show();
                    } else if (correcto == 1) {
                        Toast.makeText(getApplicationContext(), "Borrados correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(mensajes.this, mensajes.class);
                        startActivity(intent1);

                    } else {
                        Toast.makeText(getApplicationContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){

                     e.printStackTrace();
                    }

            }
        });*/

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
