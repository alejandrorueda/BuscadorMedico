package com.example.alex.buscadormedico;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public class ver_mensaje extends ActionBarActivity {
    private static EditText asunto,destinatario;
    private static TextView mensaj;
    private Button enviar,borrarMensaje;
    private String correcto="0";
    private ListView drawer;
    private DrawerLayout drawerLayout;
    private String mostrar(int tipo){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://hypocondriapp.esy.es/obtenerMensaje.php");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("idmensaje", Globals.f.get_id()));
        if(tipo==2) {

            pairs.add(new BasicNameValuePair("operacion", "2"));
        }
        if(tipo==3){
            pairs.add(new BasicNameValuePair("operacion", "3"));
        }

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

    private int filtrarDatos(int tipo){

        String data=mostrar(tipo);
        int tamano=0;
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try {
                System.out.println("datos:"+data);
                String mensajesInfo=data.substring(data.indexOf("{"));
                System.out.println("datos:"+mensajesInfo);
                json = new JSONObject(mensajesInfo);

                JSONArray jsonArray = json.optJSONArray("mensajes");

                correcto =json.optString("correcto");

                if(tipo==2) {
                ver_mensaje.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        // myTextBox.setText("my text");

                         if (correcto.equals("1")) {
                             Toast.makeText(getApplicationContext(), "Error al borrar", Toast.LENGTH_SHORT).show();
                         } else if (correcto.equals("0")) {
                             Toast.makeText(getApplicationContext(), "Borrado correctamente", Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(ver_mensaje.this, mensajes.class);
                             startActivity(intent);
                         } else {
                             Toast.makeText(getApplicationContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                         }
                     }


                    });
                }



            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return tamano;
        }
        return -1;
    }

    class Borrar extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos(2);

            return null;
        }
    }

    class Leer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos(3);

            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mensaje);
        Globals.actualActivity=0;
        try {
            drawer = (ListView) findViewById(R.id.drawer);

            final String[] opciones = {"Bandeja de entrada", "Bandeja de salida", "Nuevo mensaje","Menu principal"};
            drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));
            borrarMensaje = (Button) findViewById(R.id.borrar);
            destinatario = (EditText) findViewById(R.id.destinatario);
            asunto = (EditText) findViewById(R.id.asunto);
            mensaj = (TextView) findViewById(R.id.mensaje);
            enviar = (Button) findViewById(R.id.enviar);
            destinatario.setEnabled(false);
            asunto.setEnabled(false);
            destinatario.setText(Globals.f.get_destinatario());
            asunto.setText(Globals.f.get_asunto());
            mensaj.setText(Globals.f.get_mensaje());

            new Leer().execute();
            drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    try {
                        System.out.println(arg2);
                        if(arg2==0) {
                            Intent intent0 = new Intent(ver_mensaje.this, mensajes.class);
                            startActivity(intent0);
                        }
                        else if (arg2==1) {
                            Intent intent = new Intent(ver_mensaje.this, salida.class);
                            startActivity(intent);
                        }
                        else if(arg2==2){
                            Intent intent2 = new Intent(ver_mensaje.this, envio.class);
                            startActivity(intent2);
                        }
                        else{
                            Intent intent3 = new Intent(ver_mensaje.this, servicios.class);
                            startActivity(intent3);
                        }



                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            borrarMensaje.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      new Borrar().execute();




                }

            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ver_mensaje, menu);
        return true;
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
