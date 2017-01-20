package com.example.alex.buscadormedico;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.*;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.MatrixCursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;
import android.speech.*;
import android.content.Intent;

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

public class MainActivity extends Activity{

        private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
        private Button buscar, bt_start;
        private String mensaje="";
        private TextView anuncio;
        private MultiAutoCompleteTextView buscarT;
       private String cadena, enfermedades;
        private ListView lista;
        private String[] vector = new String[10];
       private static String sintomas;
       private ArrayList<Lista_entrada> datos = new ArrayList<Lista_entrada>();
       private ArrayList<Lista_entrada> limpieza = new ArrayList<Lista_entrada>();





    private String mostrar(int tipo){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://hypocondriapp.esy.es/algoritmoBusqueda.php");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();

        //pairs.add(new BasicNameValuePair("usuario", Globals.usu.usuario));
        if(tipo==9) {

            pairs.add(new BasicNameValuePair("operacion", "9"));
        }
        if(tipo==8) {
            httppost = new HttpPost("http://hypocondriapp.esy.es/algoritmoBusquedaBueno.php");
            pairs.add(new BasicNameValuePair("operacion", "8"));
            pairs.add(new BasicNameValuePair("sintomas", buscarT.getText().toString()));
            System.out.println("jajajajaj "+buscarT.getText().toString());
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
        } catch (Exception e) {
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
                System.out.println("error "+data);
                //String mensajesInfo=data.substring(data.indexOf("{"));

                json = new JSONObject(data);

               if(tipo==9) {
                   JSONArray jsonArray = json.optJSONArray("sintomas");
                   ArrayList<String> list = new ArrayList<String>();

                   for (int i = 0; i < jsonArray.length(); i++) {
                       JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                       tamano = jsonArray.length();
                       list.add(jsonArrayChild.optString("Nombre"));
                   }


                   Globals.words = list.toArray(new String[list.size()]);
               }
                if(tipo==8) {
                    JSONArray jsonArray = json.optJSONArray("patologias");
                    datos.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                        System.out.println(jsonArrayChild.optString("Nombre"));
                        datos.add(new Lista_entrada(jsonArrayChild.optString("Numero"),jsonArrayChild.optString("Nombre")));
                    }
                    Collections.sort(datos, new Comparator<Lista_entrada>() {
                        @Override
                        public int compare(Lista_entrada p1, Lista_entrada p2) {
                            return new Integer(p2.get_textoEncima()).compareTo(new Integer(p1.get_textoEncima()));
                        }
                    });

                }





            if(tipo==9) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            ArrayAdapter<String> aaStr = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, Globals.words);
                            buscarT.setAdapter(aaStr);
                            buscarT.setTokenizer(new SpaceTokenizer());
                            buscar = (Button) findViewById(R.id.buscarB);
                        }

                });
            }
            if(tipo==8) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarSintomas();
                    }

                });
            }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        datos.clear();
                        mostrarSintomas();
                        mensaje="No se encontraron resultados";
                        anuncio.setText(mensaje);
                    }

                });

            }
            return tamano;
        }
        return -1;
    }

    class Sintomas extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos(9);

            return null;
        }
    }

    class Buscador extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int resul=filtrarDatos(8);

            return null;
        }
    }

    private void startVoiceRecognitionActivity() {
        // Definición del intent para realizar en análisis del mensaje
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Indicamos el modelo de lenguaje para el intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Definimos el mensaje que aparecerá
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Diga, los sintomas ...");
        // Lanzamos la actividad esperando resultados
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    @Override
    //Recogemos los resultados del reconocimiento de voz
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Si el reconocimiento a sido bueno
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
            //El intent nos envia un ArrayList aunque en este caso solo
            //utilizaremos la pos.0
            ArrayList<String> matches = data.getStringArrayListExtra
                    (RecognizerIntent.EXTRA_RESULTS);
            //Separo el texto en palabras.
            String [ ] palabras = matches.get(0).toString().split(" ");
            //Si la primera palabra es LLAMAR

                for(int a=0;a<palabras.length;a++){
                    //Busco el nombre que es la tercera posicion (LLAMAR A LORENA)
                   buscarT.setText(buscarT.getText().toString()+" "+palabras[a]);
                }
            }
        }

    public void mostrarSintomas(){
        try {
            lista = (ListView) findViewById(R.id.list);

            mensaje="";
            anuncio.setText(" ");
            lista.setAdapter(new Lista_adaptador(MainActivity.this, R.layout.entrada, datos) {
                @Override
                public void onEntrada(Object entrada, View view) {
                    if (entrada != null) {
                        TextView texto_superior_entrada = (TextView) view.findViewById(R.id.textView_superior);

                        if (texto_superior_entrada != null)
                            texto_superior_entrada.setText(((Lista_entrada) entrada).get_textoEncima());

                        TextView texto_inferior_entrada = (TextView) view.findViewById(R.id.textView_inferior);
                        if (texto_inferior_entrada != null)
                            texto_inferior_entrada.setText(((Lista_entrada) entrada).get_textoDebajo());


                    }
                }
            });
        }catch(Exception e){
           e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buscarT = (MultiAutoCompleteTextView) findViewById(R.id.editText);
        anuncio = (TextView) findViewById(R.id.mensajeEstado);
        final Context TV = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        bt_start = (Button)findViewById(R.id.button1);

        new Sintomas().execute();

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lanzamos el reconoimiento de voz
                startVoiceRecognitionActivity();
            }
        });
        //Recogemos todos los telefonos y nombre en los
        //vetores: nombres y telefonos



        buscar = (Button) findViewById(R.id.buscarB);
        buscarT = (MultiAutoCompleteTextView) findViewById(R.id.editText);
        /*ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Globals.words);
        buscarT.setAdapter(aaStr);
        buscarT.setTokenizer(new SpaceTokenizer());*/
        lista = (ListView) findViewById(R.id.list);



        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadena=buscarT.getText().toString();
                anuncio.setText("Buscando...");
                new Buscador().execute();

            }
        });


            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {

                  Lista_entrada f = (Lista_entrada)  arg0.getItemAtPosition(arg2);
                    System.out.println("sdfgf"+f.get_textoDebajo());
                }
            });

       buscarT.setOnKeyListener(new View.OnKeyListener() {
           @Override
           public boolean onKey(View v, int keyCode, KeyEvent event) {
               if (keyCode == KeyEvent.KEYCODE_ENTER) {

                   buscar.callOnClick();

                   return true;
               }
               else return false;
           }
       });

        }catch(Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
