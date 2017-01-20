package com.example.alex.buscadormedico;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class registro extends ActionBarActivity {
    private Spinner spinner1;
    private List<String> lista;
    private Button registrase;
    private EditText usuario;
    private TextView mensaje;
    private EditText contrasena,confContrasena,email,nombre;
    String ocupacion=" ";
    private String correcto="1";


    private String mostrar(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://hypocondriapp.esy.es/login.php");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("usuario", usuario.getText().toString()));
        pairs.add(new BasicNameValuePair("operacion","6"));
        pairs.add(new BasicNameValuePair("contrasena", contrasena.getText().toString()));
        pairs.add(new BasicNameValuePair("email",Globals.usu.usuario));
        pairs.add(new BasicNameValuePair("estado",ocupacion));
        pairs.add(new BasicNameValuePair("nombre",nombre.getText().toString()));
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


                registro.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        // myTextBox.setText("my text");
                        System.out.println("ddd "+correcto);
                        if (correcto.equals("0")){
                            Toast.makeText(getApplicationContext(),"Registrado correctamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(registro.this,login.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
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
    class Registro extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos();

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registro);
    spinner1 = (Spinner) findViewById(R.id.estados);
            Globals.actualActivity= 0;
           registrase = (Button) findViewById(R.id.Registro);
            usuario = (EditText) findViewById(R.id.usuarioT);
            nombre = (EditText) findViewById(R.id.nombre);
            contrasena = (EditText) findViewById(R.id.contrasenaT);
            confContrasena = (EditText) findViewById(R.id.confCon);
            email = (EditText) findViewById(R.id.email);
            mensaje = (TextView) findViewById(R.id.error);
   /* lista = new ArrayList<String>();
     lista.add("Estudiando");
     lista.add("Graduado");


     ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista);
     adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     spinner1.setAdapter(adaptador);*/

        }catch (Exception e){
            mensaje.setText("Error al conectar con el servidor");
            e.printStackTrace();
        }

     registrase.setOnClickListener(new View.OnClickListener() {
         String usuar=" ",con=" ",confCon=" ",email1=" ",name="";
         @Override
         public void onClick(View v) {
             usuar=usuario.getText().toString();
             name = nombre.getText().toString();
              if (usuar.isEmpty() || name.isEmpty()){
                 mensaje.setText("Falta por rellenar el campo usuario y/o nombre");
              }
              else{
                  con=contrasena.getText().toString();
              if(con.isEmpty()) {
                  mensaje.setText("Debe escribir una contraseña");
              }
              else{

                 if(!confContrasena.getText().toString().equals(con)){
                     mensaje.setText("La contraseña no es la misma en el campo Confirmar contraseña");
                 }
                     else{
                     email1= email.getText().toString();
                     if(email1.isEmpty() || !email1.contains("@")){
                         mensaje.setText("Debe escribir un email");
                     }
                     else{
                         spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                             @Override
                             public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                 Toast.makeText(arg0.getContext(), "Seleccionado: " + arg0.getItemAtPosition(arg2).toString(), Toast.LENGTH_SHORT).show();

                             }
                             @Override
                             public void onNothingSelected(AdapterView<?> arg0) {
                             }
                         });
                         ocupacion = spinner1.getSelectedItem().toString();

                         StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                 .permitNetwork().build();
                         StrictMode.setThreadPolicy(policy);
                         new Registro().execute();


                     }
                 }
              }

              }


         }
     });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro, menu);
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
