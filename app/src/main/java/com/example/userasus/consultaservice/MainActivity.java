package com.example.userasus.consultaservice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView campo;
    String IP = "http://192.168.0.3/WebServicesAlumnos/obtener_alumnos.php";
    ManejarWebService task=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        campo =(TextView) findViewById(R.id.textView);
    }

    public void consultar(View v){
        AlertDialog.Builder checkWindowd = new AlertDialog.Builder(this);
        checkWindowd.setTitle("Iniciar Servicio");
        checkWindowd.setMessage("¿ Desea iniciar el servicio ?");
        checkWindowd.setCancelable(false);
        checkWindowd.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface checkWindowd, int id) {
                consultarServicio();
            }
        });
        checkWindowd.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface checkWindowd, int id) {
                checkWindowd.dismiss();
            }
        });
        checkWindowd.show();
    }

    public void consultarServicio(){
        task = new ManejarWebService();
        task.execute();
    }
    public void limpiarCampos(View v){
        campo.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class ManejarWebService extends AsyncTask<Void,String,String>{

       @Override
        protected String doInBackground(Void... voids) {
           String resultAux="-";
           URL url = null;
           try {
               url = new URL(IP);
               HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
               connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                       " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
               //connection.setHeader("content-type", "application/json");

               int respuesta = connection.getResponseCode();
               StringBuilder result = new StringBuilder();

               if (respuesta == HttpURLConnection.HTTP_OK){

                   InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada
                   BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                   String line;
                   while ((line = reader.readLine()) != null) {
                       result.append(line);        // toda la entrada al StringBuilder
                   }

                   //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                   JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                   //Accedemos al vector de resultados

                   String resultJSON = respuestaJSON.getString("estado");   // estado es el nombre del campo en el JSON

                   if (resultJSON=="1"){      // hay alumnos a mostrar
                       JSONArray alumnosJSON = respuestaJSON.getJSONArray("alumnos");   // estado es el nombre del campo en el JSON
                       for(int i=0;i<alumnosJSON.length();i++){
                           resultAux = resultAux + alumnosJSON.getJSONObject(i).getString("idAlumno") + " " +
                                   alumnosJSON.getJSONObject(i).getString("nombre") + " " +
                                   alumnosJSON.getJSONObject(i).getString("direccion") + "\n";
                           //publishProgress(resultAux);
                       }
                   }
                   else if (resultJSON=="2"){
                       resultAux = "No hay alumnos";
                   }

                   //publishProgress(resultJSON);

               }
           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           } catch (JSONException e) {
               e.printStackTrace();
           }
           return resultAux;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            campo.append(""+values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            campo.append(""+s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
