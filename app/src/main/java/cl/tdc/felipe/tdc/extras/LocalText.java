package cl.tdc.felipe.tdc.extras;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by georgeperez on 9/12/15.
 * clase que crea ficheros locales para trabajar offline
 */
public class LocalText {

    private boolean DisponibleSD = false;
    private boolean AccesoEscrituraSD = false;
    private List<String> item = new ArrayList<String>();
    private List<String> itemFiltrado = new ArrayList<String>();
    public List<String> itemAnsw = new ArrayList<String>();

    public LocalText() {
        comprobarSD();
    }

    public void comprobarSD() {
        String estado = Environment.getExternalStorageState();

        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            DisponibleSD = true;
            AccesoEscrituraSD = true;
            return;
        }

        if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            DisponibleSD = true;
            AccesoEscrituraSD = false;
            return;
        }

        else
        {
            DisponibleSD = false;
            AccesoEscrituraSD = false;
            return;
        }
    }

    public void escribirFicheroMemoriaExterna(String nombreArchi, String query) {

        FileOutputStream flujo=null;
        OutputStreamWriter escritor = null;
        try
        {
           // File ruta = Environment.getExternalStorageDirectory();
            File fichero = new File(Environment.getExternalStorageDirectory() + "/TDC@", nombreArchi+".txt");
            flujo=new FileOutputStream(fichero);
            escritor=new OutputStreamWriter(flujo);
            escritor.write(query);
        }
        catch (Exception e)
        {
            Log.e("ERROR", "Error al escribir fichero a tarjeta SD");

        }
        finally
        {
            try {
                if(escritor!=null)
                    escritor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String leerFicheroMemoriaExterna(String nombreArchi) {
        InputStreamReader flujo=null;
        BufferedReader lector=null;
        try
        {
            //File ruta = Environment.getExternalStorageDirectory();
            File fichero = new File(Environment.getExternalStorageDirectory() + "/TDC@", nombreArchi+".txt");
            flujo= new InputStreamReader(new FileInputStream(fichero));
            lector= new BufferedReader(flujo);
            String texto = lector.readLine();
            String texto2 = "";
            while(texto!=null)
            {
                texto2 += texto;
                texto=lector.readLine();
            }
            System.out.println(texto2);

            return texto2;
        }
        catch (Exception ex)
        {
            Log.e("ivan", "Error al leer fichero desde tarjeta SD");
        }
        finally
        {
            try {
                if(lector!=null)
                    lector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void listarFicheros(String filtroMant){
        // Array TEXTO donde guardaremos los nombres de los ficheros
        //Defino la ruta donde busco los ficheros
        File f = new File(Environment.getExternalStorageDirectory() + "/TDC@");
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();

        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++) {
            //Sacamos del array files un fichero
            File file = files[i];
            //Si es fichero...
            if (file.isFile()) {
                item.add(file.getName());
                System.out.println("Agregado: " + file.getName());
            }
        }

        for (int j = 0; j < item.size(); j++) {//iteramos sobre todos los elementos en item
            if (item.get(j).toString().startsWith(filtroMant)){//evaluamos si la lista item continiene elemento que comience con la palabra recibida con filtroMant
                itemFiltrado.add(item.get(j));//Agregamos a la lista itemFiltrado solo los mantenimientos recibidos con filtroMant
            }
        }
    }

    public void crearListaEnvio(String filtro){//Iteramos sobre los archivos locales y sacamos los que se van a enviar
        for (int j = 0; j < itemFiltrado.size(); j++) {//Normalmente el filtro deberia ser por la palabra "answer".
            if (itemFiltrado.get(j).toString().indexOf(filtro) > 0){
                System.out.println("Agregado " + itemFiltrado.get(j).toString() + " a itemAnsw");
                itemAnsw.add(itemFiltrado.get(j));
            }
        }
    }

    public void elimnarFicherosEnviados(String filtro){//Iteramos sobre los archivos locales y sacamos los que se van a enviar
        for (int j = 0; j < itemFiltrado.size(); j++) {
            System.out.println("Valor de Item: " + itemFiltrado.get(j).toString());
            if (itemFiltrado.get(j).toString().indexOf(filtro) > 0){
                System.out.println("Agregado: " + itemFiltrado.get(j).toString() + " a itemAnsw List");
                itemAnsw.add(itemFiltrado.get(j));
            }
        }
    }



    public boolean isDisponibleSD() {
        return DisponibleSD;
    }

    public void setDisponibleSD(boolean disponibleSD) {
        DisponibleSD = disponibleSD;
    }

    public boolean isAccesoEscrituraSD() {
        return AccesoEscrituraSD;
    }

    public void setAccesoEscrituraSD(boolean accesoEscrituraSD) {
        AccesoEscrituraSD = accesoEscrituraSD;
    }
}
