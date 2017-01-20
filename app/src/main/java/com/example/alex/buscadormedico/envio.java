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


public class envio extends ActionBarActivity {
    private static EditText destinatario,asunto,mensaj;
    private Button enviar;
    private String correcto="0";
    private ListView drawer;
    private DrawerLayout drawerLayout;
  private static int clickCount=0;



    private String mostrar(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://hypocondriapp.esy.es/obtenerMensaje.php");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        System.out.println("dfgh"+Globals.usu.usuario);
        pairs.add(new BasicNameValuePair("usuario", Globals.usu.usuario));
        pairs.add(new BasicNameValuePair("operacion","1"));
        pairs.add(new BasicNameValuePair("asunto",asunto.getText().toString()));
        pairs.add(new BasicNameValuePair("emisor",Globals.usu.usuario));
        pairs.add(new BasicNameValuePair("destinatarios",destinatario.getText().toString()));
        pairs.add(new BasicNameValuePair("mensaje",mensaj.getText().toString()));
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
                System.out.println("datos:"+data);
               String mensajesInfo=data.substring(data.indexOf("{"));
                System.out.println("datos:"+mensajesInfo);
                json = new JSONObject(mensajesInfo);
                System.out.println("-------------------------------------------");
                JSONArray jsonArray = json.optJSONArray("enviado");


                    correcto=json.optString("correcto");
                    System.out.println("correcccc "+correcto);
                    //e.add(new Lista_bandeja(jsonArrayChild.optString("idmensaje"),jsonArrayChild.optString("destinatario"),jsonArrayChild.optString("asunto"),jsonArrayChild.optString("mensaje"),jsonArrayChild.optString("fecha"), Globals.usu.usuario, Integer.parseInt(jsonArrayChild.optString("leido"))));
                    //System.out.println("eett "+e.get(i).get_asunto());


                envio.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        // myTextBox.setText("my text");
                        System.out.println("ddd "+correcto);
                        if (correcto.equals("1")){
                            Toast.makeText(getApplicationContext(), "No existe el destinatario", Toast.LENGTH_SHORT).show();
                        }
                        else if(correcto.equals("0")){
                            Toast.makeText(getApplicationContext(),"Enviado correctamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(envio.this, mensajes.class);
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            } catch (Exception e) {
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
    class Envio extends AsyncTask<String,String,String> {

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


    public static void enviar(){




    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviar_mensaje);
        destinatario = (EditText) findViewById(R.id.destinatario);
        asunto = (EditText) findViewById(R.id.asunto);
        mensaj = (EditText) findViewById(R.id.mensaje);
        enviar= (Button) findViewById(R.id.enviar);
        drawer = (ListView) findViewById(R.id.drawer);

        final String[] opciones = {"Bandeja de entrada", "Bandeja de salida", "Nuevo mensaje"};
        drawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, opciones));
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new Envio().execute();

            }
        });
        drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    System.out.println(arg2);
                    if(arg2==0) {
                        Intent intent0 = new Intent(envio.this, mensajes.class);
                        startActivity(intent0);
                    }
                    else if (arg2==1) {
                        Intent intent = new Intent(envio.this, salida.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent2 = new Intent(envio.this, envio.class);
                        startActivity(intent2);
                    }



                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        mensaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCount == 0) {
                   mensaj.setText("");
                }
                clickCount = 1;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_envio, menu);
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
