package com.example.alex.buscadormedico;

import android.app.ListFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class servicios extends ActionBarActivity {
     private TextView error;
     private Button busqueda,mensajes,prueba;
    static TextView error2 ;
    servicios service;
    private String correcto="0",nuevomensaje="0";

        private ListView drawer;
        private DrawerLayout drawerLayout;
        private ActionBarDrawerToggle toggle ;

       private static int veces=0;

    private String mostrar(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://hypocondriapp.esy.es/obtenerMensaje.php");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();

        pairs.add(new BasicNameValuePair("usuario", Globals.usu.usuario));
        pairs.add(new BasicNameValuePair("operacion","4"));

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
                String mensajesInfo=data.substring(data.indexOf("{"));

                json = new JSONObject(mensajesInfo);

                JSONArray jsonArray = json.optJSONArray("mensajes");
                nuevomensaje = json.optString("nuevomensaje");


                servicios.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        // myTextBox.setText("my text");
                        if(nuevomensaje.equals("1") && Globals.actualActivity!=1 && Globals.leido!=1) {
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            int icon = R.drawable.app_icono_local2;

                            CharSequence tickerText = "Hipocondriapp";
                            long when = System.currentTimeMillis();

                            Notification notification = new Notification(icon, tickerText, when);

                            Context context = getApplicationContext();
                            CharSequence contentTitle = "Nuevo mensaje";
                            CharSequence contentText = "Consulte su bandeja de entrada";

//Agregando sonido
                            notification.defaults |= Notification.DEFAULT_SOUND;
//Agregando viintbración
                            notification.defaults |= Notification.DEFAULT_VIBRATE;
                            notification.ledARGB = Color.BLUE;
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notification.ledOnMS = 1000;
                            notification.flags |= Notification.FLAG_SHOW_LIGHTS;

                            Intent notificationIntent = new Intent(servicios.this, mensajes.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(servicios.this, 0, notificationIntent, 0);
                            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

                            mNotificationManager.notify(0, notification);
                            nuevomensaje="0";
                            Globals.leido=1;


                        }

                        if(nuevomensaje.equals("1") && Globals.actualActivity==1){
                            System.out.println("borrate");
                            try {
                                Intent intent = new Intent(servicios.this, mensajes.class);

                                //llamamos a la actividad
                                startActivity(intent);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
    class Nuevo extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos();

            return null;
        }
    }

    class Actualizacion extends TimerTask {
        servicios service2;



        @Override
        public void run() {
            new Nuevo().execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_actividades);

        Globals.actualActivity= 0;
        error2 = (TextView) findViewById(R.id.error2);
          Globals.timer=new Timer();
          Actualizacion sub = new Actualizacion();
          Globals.timer.scheduleAtFixedRate(sub, 0, 15000);
            drawer = (ListView) findViewById(R.id.drawer);

            error = (TextView) findViewById(R.id.error);
            busqueda = (Button) findViewById(R.id.busqueda);
            mensajes = (Button) findViewById(R.id.mensajes);
            prueba = (Button) findViewById(R.id.button3);
            error.setText("Bienvenido " + Globals.usu.nombre);
            error.setMinimumHeight(10);
            final String[] opciones = {"Opción 1", "Opción 2", "Opción 3"};
            drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));



        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(servicios.this,  ver_enfermedad.class);
                startActivity(intent);


            }
        });



            busqueda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
             try {
                 Intent intent = new Intent(servicios.this, MainActivity.class);
                 startActivity(intent);
             }catch(Exception e){
                 e.printStackTrace();
             }


                }
            });
            mensajes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(servicios.this, mensajes.class);
                    startActivity(intent);


                }
            });
            drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Toast.makeText(servicios.this, "Pulsado: " + opciones[arg2], Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                }
            });



    }

        public static void estadoConectado(){
            error2.setText("Conectado");
            error2.setTextColor(Color.GREEN);
        }
    public static void estadoDesconectado(){
        error2.setText("Desconectado");
        error2.setTextColor(Color.RED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_servicios, menu);
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
