package com.example.alex.buscadormedico;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListFragment;


public class mensajes extends Activity {
    private static String mensajes2="",usuario="",idmensaje="",asunto="",fecha="",mensaje="",destinatario="",leido="",ids="";
    private static String[] mensajes1,usuario1,idmensaje1,asunto1,fecha1,leido1,destinatario1;
    private ListView lista;
    private ListView drawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle ;
    private Timer timer;
    private static int correcto=0,nuevomensaje=0,veces=0;
    private ArrayList<Lista_bandeja> e = new ArrayList<Lista_bandeja>();





    private String mostrar(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://hypocondriapp.esy.es/obtenerMensaje.php");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();

        pairs.add(new BasicNameValuePair("usuario", Globals.usu.usuario));
        pairs.add(new BasicNameValuePair("operacion","0"));

        String resultado="";
        HttpResponse response;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(pairs));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            resultado= convertStreamToString(instream);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultado;
    }

    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private int filtrarDatos(){

        String data=mostrar();
        int tamano=0;
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try {
                System.out.println("error "+data);
                //String mensajesInfo=data.substring(data.indexOf("{"));

                    json = new JSONObject(data);

                    JSONArray jsonArray = json.optJSONArray("mensajes");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                        tamano = jsonArray.length();

                        e.add(new Lista_bandeja(jsonArrayChild.optString("idmensaje"), jsonArrayChild.optString("emisor"), jsonArrayChild.optString("asunto"), jsonArrayChild.optString("mensaje"), jsonArrayChild.optString("fecha"), Globals.usu.usuario, Integer.parseInt(jsonArrayChild.optString("leido"))));

                    }




            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            mensajes.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    // myTextBox.setText("my text");
                    incorporarDatos();
                    Globals.leido = 0;
                }
            });
            return tamano;
        }
        return -1;
    }
    private void mostrarPersona(final int posicion){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
               /* Personas personas=listaPersonas.get(posicion);
                nombre.setText(personas.getNombre());
                dni.setText(personas.getDni());
                telefono.setText(personas.getTelefono());
                email.setText(personas.getEmail());*/
            }
        });
    }
    class Entrada extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos();

            return null;
        }
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

   public void incorporarDatos(){
       Globals.actualActivity= 1;
       lista = (ListView) findViewById(R.id.list);

       final Context TV = this;

       ArrayList<Lista_bandeja> datos = e;
       drawer = (ListView) findViewById(R.id.drawer);


       final String[] opciones = {"Bandeja de entrada", "Bandeja de salida", "Nuevo mensaje","Menu principal"};
       drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));


       lista.setAdapter(new Lista_adaptador(TV, R.layout.entradas_bandeja, datos) {
           @Override
           public void onEntrada(Object entrada, View view) {

               if (entrada != null) {
                   TextView asunto = (TextView) view.findViewById(R.id.asunto);

                   if (asunto != null) {
                       asunto.setText(((Lista_bandeja) entrada).get_asunto());

                   }
                   TextView destinatario = (TextView) view.findViewById(R.id.destinatario);
                   if (destinatario != null)
                       destinatario.setText(((Lista_bandeja) entrada).get_destinatario());
                   TextView fecha = (TextView) view.findViewById(R.id.fechaaa);
                   if (fecha != null)
                       fecha.setText(((Lista_bandeja) entrada).get_fecha());
                   try {
                       LinearLayout leido = (LinearLayout) view.findViewById(R.id.colorfondo);
                       if (((Lista_bandeja) entrada).get_leido() == 0)
                       {

                           leido.setBackgroundColor(Color.GRAY);
                       }
                   }catch(Exception e){
                       e.printStackTrace();
                   }



               }
           }
       });


       drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
               try {

                   if(arg2==0) {
                       Intent intent0 = new Intent(mensajes.this, mensajes.class);
                       startActivity(intent0);
                   }
                   else if (arg2==1) {
                       Intent intent = new Intent(mensajes.this, salida.class);
                       startActivity(intent);
                   }
                   else if(arg2==2){
                       Intent intent2 = new Intent(mensajes.this, envio.class);
                       startActivity(intent2);
                   }
                   else{
                       Intent intent3 = new Intent(mensajes.this, servicios.class);
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
               Intent intent = new Intent(mensajes.this,ver_mensaje.class);
               startActivity(intent);
           }
       });
   }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajes);
        Globals.tipomensaje=0;

        try {


            new Entrada().execute();
            /*
            Globals.actualActivity= 1;
            lista = (ListView) findViewById(R.id.list);

            final Context TV = this;
            ArrayList<Lista_bandeja> datos = e;
            drawer = (ListView) findViewById(R.id.drawer);


            final String[] opciones = {"Bandeja de entrada", "Bandeja de salida", "Nuevo mensaje","Menu principal"};
            drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));

            System.out.println("e "+e.size());
            lista.setAdapter(new Lista_adaptador(TV, R.layout.entradas_bandeja, datos) {
                @Override
                public void onEntrada(Object entrada, View view) {
                    System.out.println("dfghfgdh");
                    if (entrada != null) {
                        TextView asunto = (TextView) view.findViewById(R.id.asunto);

                        if (asunto != null) {
                            asunto.setText(((Lista_bandeja) entrada).get_asunto());
                            System.out.println("asdgdfg "+((Lista_bandeja) entrada).get_asunto());
                        }
                        TextView destinatario = (TextView) view.findViewById(R.id.destinatario);
                        if (destinatario != null)
                            destinatario.setText(((Lista_bandeja) entrada).get_destinatario());
                        TextView fecha = (TextView) view.findViewById(R.id.fechaaa);
                        if (fecha != null)
                            fecha.setText(((Lista_bandeja) entrada).get_fecha());
                        try {
                            LinearLayout leido = (LinearLayout) view.findViewById(R.id.colorfondo);

                            if (((Lista_bandeja) entrada).get_leido() == 0)
                            {

                                leido.setBackgroundColor(Color.GRAY);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }



                    }
                }
            });*/





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
