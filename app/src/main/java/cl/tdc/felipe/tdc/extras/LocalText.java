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

/**
 * Created by georgeperez on 9/12/15.
 * clase que crea ficheros locales para trabajar offline
 */
public class LocalText {

    private boolean DisponibleSD = false;
    private boolean AccesoEscrituraSD = false;

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

    public void escribirFicheroMemoriaExterna(String query) {

        FileOutputStream flujo=null;
        OutputStreamWriter escritor = null;
        try
        {
            File ruta = Environment.getExternalStorageDirectory();
            File fichero = new File(ruta.getAbsolutePath(), "planing-mantience.txt");
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

    public String leerFicheroMemoriaExterna() {
        InputStreamReader flujo=null;
        BufferedReader lector=null;
        try
        {
            File ruta = Environment.getExternalStorageDirectory();
            File fichero = new File(ruta.getAbsolutePath(), "planing-mantience.txt");
            flujo= new InputStreamReader(new FileInputStream(fichero));
            lector= new BufferedReader(flujo);
            String texto = lector.readLine();
            String texto2 = "";
            while(texto!=null)
            {
                Log.i("AQUIIIIII", "Mensaje: " + texto);
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
