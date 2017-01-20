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


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

public class login extends ActionBarActivity {
    private Button conectar;
    private Button registro1;
    private AutoCompleteTextView usuario;
    private EditText contrasena;
    private TextView mensaje;
    private String textoMensaje="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        conectar = (Button) findViewById(R.id.conectarB);
        usuario = (AutoCompleteTextView) findViewById(R.id.correo);
        contrasena = (EditText) findViewById(R.id.password1);
        registro1 = (Button) findViewById(R.id.registro);
        mensaje = (TextView) findViewById(R.id.mensaje);
        Globals.actualActivity=0;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        if(!Globals.usu.nombre.isEmpty()){
            System.out.println("cancelo");
            Globals.timer.cancel();
            Globals.timer.purge();
            Globals.timer = null;
        }

        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String log_usuario=usuario.getText().toString();
               String log_contra=contrasena.getText().toString();
                String nombre,email,estado;

               int contrasena=1,usuarioConf=0,conectarse=1;

                mensaje.setText("Conectandose");
                System.out.println(log_contra);
                new Login(log_usuario,log_contra).execute();


               // Toast.makeText(getApplicationContext(),textoMensaje, Toast.LENGTH_SHORT).show();



            }
        });


        registro1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(login.this,registro.class);
                    startActivity(intent);
                }catch (Exception e){
                    mensaje.setText("Error al acceder");
                    e.printStackTrace();
                }
            }
        });

    }

    private String mostrar(String usuario,String contrasena){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://hypocondriapp.esy.es/login.php");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("usuario", usuario));
        pairs.add(new BasicNameValuePair("contrasena", contrasena));
        pairs.add(new BasicNameValuePair("operacion","5"));

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

      private int filtrarDatos(String usuario,String contrasena){

        String data=mostrar(usuario,contrasena);
          int tamano=0;
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try {
                System.out.println(data);
               //String usuarioInfo=data.substring(data.indexOf("{"));
               // System.out.println("datos:"+usuarioInfo);
                json = new JSONObject(data);

                JSONArray jsonArray = json.optJSONArray("datos");
                tamano=jsonArray.length();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                    Globals.usu = new usuario(jsonArrayChild.optString("USUARIO"), jsonArrayChild.optString("nombre"), jsonArrayChild.optString("contrasena"), jsonArrayChild.optString("nombre"), jsonArrayChild.optString("email"), jsonArrayChild.optString("estado"));
                }



            } catch (JSONException e) {
                // TODO Auto-generated catch block
                mensaje.setText("");
                e.printStackTrace();
            }

        }

          final int finalTamano = tamano;
          login.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  // This code will always run on the UI thread, therefore is safe to modify UI elements.
                  // myTextBox.setText("my text");
                  mensaje.setText("");
                  if(finalTamano >0){
                      Intent intent = new Intent(login.this,servicios.class);
                      textoMensaje="Inicio de sesion correcto";
                      startActivity(intent);
                  }else{
                      textoMensaje="Datos erroneos";
                  }

                  Toast.makeText(getApplicationContext(),textoMensaje, Toast.LENGTH_SHORT).show();

              }
          });
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
    class Login extends AsyncTask<String,String,String> {
        String usuario;
        String contrasena;
        public Login(String usuario,String contrasena){
            this.contrasena=contrasena;
            this.usuario=usuario;
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos(this.usuario, this.contrasena);


            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
