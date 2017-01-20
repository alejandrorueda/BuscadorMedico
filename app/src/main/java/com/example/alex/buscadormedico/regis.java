package com.example.alex.buscadormedico;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import java.sql.*;
import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.MatrixCursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;

public class regis extends Activity{

    private Button buscar;
    private EditText buscarT;
    private String cadena,enfermedades;
    private ListView lista;
    private String  []vector = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buscar = (Button) findViewById(R.id.buscarB);
        buscarT = (EditText) findViewById(R.id.editText);
        final Context TV = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build();
        StrictMode.setThreadPolicy(policy);


        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadena=buscarT.getText().toString();

                try {

                    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());



                    Connection vDatabaseConnection = DriverManager.getConnection("jdbc:oracle:thin:@//medicserver.no-ip.org:1521/xe", "SYSTEM", "frende5557111");
                    System.out.println("no se ");
                    // PREPARE PL/SQL FUNCTION CALL ("?" IS A PLACEHOLDER FOR RETURN VALUES AND PARAMETER VALUES, NUMBERED FROM LEFT TO RIGHT)
                    CallableStatement vStatement = vDatabaseConnection.prepareCall("begin ? := ENCONTRAR( ? ); end;");

                    // USE EITHER THE "BLOCK" SYNTAX ABOVE, OR "ANSI 92" SYNTAX BELOW
                    //CallableStatement vStatement = vDatabaseConnection.prepareCall( "{ ? = call javatest( ?, ? ) }" );

                    // SET INPUT PARAMETERS AND DECLARE TYPES FOR RETURN VALUES
                    vStatement.registerOutParameter( 1, Types.VARCHAR);   // declare type of function return value "begin ? ..."
                    System.out.println(cadena);
                    vStatement.setString( 2,cadena);              // set value of first function parameter "... javatest( ?, ..."


                    // CALL THE PL/SQL FUNCTION
                    vStatement.executeUpdate();

                    // GET THE FUNCTION RETURN VALUE AND THE OUT PARAMETER VALUE
                    enfermedades = vStatement.getString( 1 );   // get the "?" in "begin ? ..."
                    System.out.println(enfermedades+"-------------------------------");

                    // CLOSE THE DATABASE CONNECTION
                    vDatabaseConnection.close();






                }

                catch (Exception E) {
                    E.printStackTrace();

                }

                ArrayList<Lista_entrada> datos = new ArrayList<Lista_entrada>();


                int k =0;
                vector[0]=" ";
                for(int i= 0;i<vector.length;i++){
                    vector[i]=" ";
                }

                for (int i=0;i<enfermedades.length();i++){
                    if (enfermedades.charAt(i)==';' && k<10) {

                        k++;

                    }
                    else
                        vector[k]=vector[k]+enfermedades.charAt(i);


                }

                for (int i=1 ; i< 10; i++){

                    datos.add(new Lista_entrada( ""+i, vector[i]));
                }

                lista = (ListView) findViewById(R.id.list);

                lista.setAdapter(new Lista_adaptador(TV, R.layout.entrada, datos){
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
            }
        });


        buscarT.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    cadena=buscarT.getText().toString();
                    System.out.println(cadena);
                    return true;
                }
                else return false;
            }
        });
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
