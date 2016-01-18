package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.extras.Constantes;
import cl.tdc.felipe.tdc.extras.LocalText;
import cl.tdc.felipe.tdc.objects.FormularioCierre.AREA;
import cl.tdc.felipe.tdc.objects.FormularioCierre.ITEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.PHOTO;
import cl.tdc.felipe.tdc.objects.FormularioCierre.QUESTION;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SET;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SYSTEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.VALUE;
import cl.tdc.felipe.tdc.objects.Maintenance.Agenda;
import cl.tdc.felipe.tdc.preferences.FormCierreReg;
import cl.tdc.felipe.tdc.preferences.MaintenanceReg;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.SoapRequestTDC;
import cl.tdc.felipe.tdc.webservice.XMLParser;
import cl.tdc.felipe.tdc.webservice.XMLParserTDC;
import cl.tdc.felipe.tdc.webservice.dummy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

public class ActividadCierreActivity extends Activity implements View.OnClickListener {

    protected PowerManager.WakeLock wakelock;

    private static String TITLE = "Cierre de Actividad";
    private static String IMEI;
    String idMain;
    boolean cerrarMant=true;
    LocalText local = new LocalText();
    ArrayList<SYSTEM> SYSTEMS;
    ProgressDialog p;

    TextView PAGETITLE;
    public static Activity actividad;
    Context mContext;
    ProgressDialog dialog;

    FormCierreReg REG, IDENREG, TRESGREG, FAENAREG, TRANSPREG, ACREG, SGREG, DCREG, AIRREG, GEREG, EMERGREG;
    MaintenanceReg MAINREG;

    Button IDEN, TRESG, AC, DC, SG, AIR, FAENA, TRANSPORTE, GE, RAN, EMERG;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre_actividad);
        actividad = this;
        mContext = this;

        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        idMain = getIntent().getStringExtra("MAINTENANCE");

        REG = new FormCierreReg(this, "LISTADO");
        IDENREG = new FormCierreReg(this, "IDEN");
        TRESGREG = new FormCierreReg(this, "3G");
        FAENAREG = new FormCierreReg(this, "FAENA");
        TRANSPREG = new FormCierreReg(this, "TRANSPORTE");
        SGREG = new FormCierreReg(this, "SG");
        DCREG = new FormCierreReg(this, "DC");
        AIRREG = new FormCierreReg(this, "AIR");
        GEREG = new FormCierreReg(this, "GE");
        ACREG = new FormCierreReg(this, "AC");
        EMERGREG = new FormCierreReg(this, "EMERGENCY");
        MAINREG = new MaintenanceReg(this);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        PAGETITLE = (TextView) this.findViewById(R.id.header_actual);
        PAGETITLE.setText(TITLE);

        IDEN = (Button) this.findViewById(R.id.IDEN);
        TRESG = (Button) this.findViewById(R.id.TRESG);
        AC = (Button) this.findViewById(R.id.AC);
        DC = (Button) this.findViewById(R.id.DC);
        SG = (Button) this.findViewById(R.id.SG);
        AIR = (Button) this.findViewById(R.id.AIR);
        FAENA = (Button) this.findViewById(R.id.FAENA);
        TRANSPORTE = (Button) this.findViewById(R.id.TRANSPORTE);
        GE = (Button) this.findViewById(R.id.GE);
        RAN = (Button) this.findViewById(R.id.RAN);
        EMERG = (Button) this.findViewById(R.id.EMERG);
        IDEN.setOnClickListener(this);
        TRESG.setOnClickListener(this);
        AC.setOnClickListener(this);
        DC.setOnClickListener(this);
        SG.setOnClickListener(this);
        AIR.setOnClickListener(this);
        FAENA.setOnClickListener(this);
        TRANSPORTE.setOnClickListener(this);
        GE.setOnClickListener(this);
        RAN.setOnClickListener(this);
        EMERG.setOnClickListener(this);

        boolean state = REG.getBoolean("IDEN" + idMain);
        if (state)
            IDEN.setEnabled(false);

        state = REG.getBoolean("3G"+idMain);
        if(state){
            TRESG.setEnabled(false);
        }

        state = REG.getBoolean("AC"+idMain);
        if(state){
            AC.setEnabled(false);
        }
        state = REG.getBoolean("DC"+idMain);
        if(state){
            DC.setEnabled(false);
        }
        state = REG.getBoolean("SG"+idMain);
        if(state){
            SG.setEnabled(false);
        }

        state = REG.getBoolean("AIR"+idMain);
        if(state){
            AIR.setEnabled(false);
        }

        state = REG.getBoolean("FAENA"+idMain);
        if(state){
            FAENA.setEnabled(false);
        }

        state = REG.getBoolean("TRANSPORTE"+idMain);
        if(state){
            TRANSPORTE.setEnabled(false);
        }

        state = REG.getBoolean("GE" + idMain);
        if(state){
            GE.setEnabled(false);
        }

        state = REG.getBoolean("EMERGENCY" + idMain);
        if(state){
            EMERG.setEnabled(false);
        }

        mostrarBotonesAzules(1);

    }

    //Iteramos sobre idsActivities2 y obtenemos las actividades, deacuerdo a las actividades y el idMain hacemos visible los botones de mantenimiento
    private void mostrarBotonesAzules(int num) {
        for (String temp : AgendaActivity.idsActivities2) {
            System.out.println(temp + " idM " + idMain);
            if (temp.equalsIgnoreCase("Preventivo,1," + idMain)) {
                IDEN.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("Preventivo,2," + idMain)) {
                TRESG.setVisibility(View.VISIBLE);
            }

            if (num == 1) {

            if( (temp.equalsIgnoreCase("Emergencia,1," + idMain )) || (temp.equalsIgnoreCase("Emergencia,2," + idMain )) ||
                    (temp.equalsIgnoreCase("Emergencia,3," + idMain ))||(temp.equalsIgnoreCase("Emergencia,4," + idMain ))||
                    (temp.equalsIgnoreCase("Emergencia,5," + idMain ))||(temp.equalsIgnoreCase("Emergencia,6," + idMain ))) {
                EMERG.setVisibility(View.VISIBLE);
            }
            //Determinamos si mostramos el boton de RAN o no

            if (temp.equalsIgnoreCase("Preventivo,4," + idMain) || temp.equalsIgnoreCase("Preventivo,5," + idMain)
                    || temp.equalsIgnoreCase("Preventivo,6," + idMain) || temp.equalsIgnoreCase("Preventivo,7," + idMain)
                    || temp.equalsIgnoreCase("Preventivo,8," + idMain) || temp.equalsIgnoreCase("Preventivo,9," + idMain))
                muestraRan();

                if (temp.equalsIgnoreCase("Preventivo,1," + idMain)) {
                    IDEN.setVisibility(View.VISIBLE);
                }

                if (temp.equalsIgnoreCase("Preventivo,2," + idMain)) {
                    TRESG.setVisibility(View.VISIBLE);
                }

                if (temp.equalsIgnoreCase("Faena de combustible,3," + idMain)) {
                    FAENA.setVisibility(View.VISIBLE);
                }

                if ((temp.equalsIgnoreCase("Emergencia,1," + idMain)) || (temp.equalsIgnoreCase("Emergencia,2," + idMain)) ||
                        (temp.equalsIgnoreCase("Emergencia,3," + idMain)) || (temp.equalsIgnoreCase("Emergencia,4," + idMain)) ||
                        (temp.equalsIgnoreCase("Emergencia,5," + idMain)) || (temp.equalsIgnoreCase("Emergencia,6," + idMain))) {
                    EMERG.setVisibility(View.VISIBLE);
                }
                //Determinamos si mostramos el boton de RAN o no

                if (temp.equalsIgnoreCase("Preventivo,4," + idMain) || temp.equalsIgnoreCase("Preventivo,5," + idMain)
                        || temp.equalsIgnoreCase("Preventivo,6," + idMain) || temp.equalsIgnoreCase("Preventivo,7," + idMain)
                        || temp.equalsIgnoreCase("Preventivo,8," + idMain) || temp.equalsIgnoreCase("Preventivo,9," + idMain))
                    muestraRan();
            }

            //Al pulsar sobre RAN
            else {
                if (temp.equalsIgnoreCase("Preventivo,4," + idMain)) {
                    if (DC.getVisibility() == View.GONE) {
                        DC.setVisibility(View.VISIBLE);
                    } else {
                        DC.setVisibility(View.GONE);
                    }
                }
                else if (temp.equalsIgnoreCase("Preventivo,5," + idMain)) {
                    if (SG.getVisibility() == View.GONE) {
                        SG.setVisibility(View.VISIBLE);
                    } else {
                        SG.setVisibility(View.GONE);
                    }
                }
                else if (temp.equalsIgnoreCase("Preventivo,6," + idMain)) {
                    if (AIR.getVisibility() == View.GONE) {
                        AIR.setVisibility(View.VISIBLE);
                    } else {
                        AIR.setVisibility(View.GONE);
                    }
                }
                else if (temp.equalsIgnoreCase("Preventivo,7," + idMain)) {
                    if (TRANSPORTE.getVisibility() == View.GONE) {
                        TRANSPORTE.setVisibility(View.VISIBLE);
                    } else {
                        TRANSPORTE.setVisibility(View.GONE);
                    }
                }
                else if (temp.equalsIgnoreCase("Preventivo,8," + idMain)) {
                    if (GE.getVisibility() == View.GONE) {
                        GE.setVisibility(View.VISIBLE);
                    } else {
                        GE.setVisibility(View.GONE);
                    }
                }
                else if (temp.equalsIgnoreCase("Preventivo,9," + idMain)) {
                    if (AC.getVisibility() == View.GONE) {
                        AC.setVisibility(View.VISIBLE);
                    } else {
                        AC.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void muestraRan(){
        if (RAN.getVisibility() == View.GONE)
            RAN.setVisibility(View.VISIBLE);
    }

    public void onClick_apagar(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Seguro que desea salir de TDC?");
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (AgendaActivity.actividad != null)
                    AgendaActivity.actividad.finish();
                if (MainActivity.actividad != null)
                    MainActivity.actividad.finish();
                actividad.finish();
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        b.show();
    }

    @Override
    public void onBackPressed() {
        onClick_back(null);
    }

    public void onClick_back(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Seguro que desea salir de Cierre de Actividad?");
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                actividad.finish();
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        b.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.IDEN) {
            buscar_form task = new buscar_form("IDEN");
            task.execute();
        }
        if (view.getId() == R.id.TRESG) {
            buscar_form task = new buscar_form("3G");
            task.execute();
        }
        if (view.getId() == R.id.AC) {
            buscar_form task = new buscar_form("AC");
            task.execute();
        }

        if (view.getId() == R.id.DC) {
            buscar_form task = new buscar_form("DC");
            task.execute();
        }
        if (view.getId() == R.id.SG) {
            buscar_form task = new buscar_form("SYSTEM GROUND");
            task.execute();
        }
        if (view.getId() == R.id.AIR) {
            buscar_form task = new buscar_form("AIR");
            task.execute();
        }
        if (view.getId() == R.id.FAENA) {
            buscar_form task = new buscar_form("FAENA");
            task.execute();
        }
        if (view.getId() == R.id.TRANSPORTE) {
            buscar_form task = new buscar_form("TRANSPORTE");
            task.execute();
        }
        if (view.getId() == R.id.GE) {
            buscar_form task = new buscar_form("GRUPO ELECTROGEN");
            task.execute();
        }
        if (view.getId() == R.id.EMERG) {
            buscar_form task = new buscar_form("EMERGENCY");
            task.execute();
        }
        if (view.getId() == R.id.RAN) {
            mostrarBotonesAzules(2);
        }
    }

    private String getAction(String type) {
        if (type.equals("IDEN")) return SoapRequestTDC.ACTION_IDEN;
        if (type.equals("3G")) return SoapRequestTDC.ACTION_3G;
        if (type.equals("AC")) return SoapRequestTDC.ACTION_AC;
        if (type.equals("DC")) return SoapRequestTDC.ACTION_DC;
        if (type.equals("SYSTEM GROUND")) return SoapRequestTDC.ACTION_SG;
        if (type.equals("AIR")) return SoapRequestTDC.ACTION_AIR;
        if (type.equals("FAENA")) return SoapRequestTDC.ACTION_FAENA;
        if (type.equals("TRANSPORTE")) return SoapRequestTDC.ACTION_TRANSPORTE;
        if (type.equals("GRUPO ELECTROGEN")) return SoapRequestTDC.ACTION_GE;
        if (type.equals("EMERGENCY")) return SoapRequestTDC.ACTION_EMERG;
        else return "";
    }

    private class buscar_form extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        String type;
        String query;
        boolean flag = false;

        public buscar_form(String type) {
            this.type = type;
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Buscando formulario " + type);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                //query = SoapRequestTDC.getFormularioCierre(IMEI, idMain, getAction(type));
                query = new LocalText().leerFicheroMemoriaExterna(idMain + "," + getAction(type));

                ArrayList<String> returnCode = XMLParser.getReturnCode2(query);

                if (returnCode.get(0).equals("0"))
                    flag = true;
                return returnCode.get(1);
            } catch (SAXException | ParserConfigurationException | XPathExpressionException e) {
                e.printStackTrace();
                return "Error en el XML.";
            } catch (IOException e) {
                e.printStackTrace();
                return "Se agotó el tiempo de conexión. Por favor reintente.";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) dialog.dismiss();
            if (flag) {
                final Intent intent = new Intent(actividad, ActividadCierreFormActivity.class);
                intent.putExtra("TITULO", this.type);
                intent.putExtra("ID", idMain);
                intent.putExtra("XML", query);
                final int code;
                if (type.equals("IDEN")) {
                    code = 0;
                }else if(type.equals("3G")) {
                    code = 1;
                }else if(type.equals("DC")) {
                    code = 3;
                }else if(type.equals("SYSTEM GROUND")) {
                    code = 4;
                }else if(type.equals("AIR")) {
                    code = 5;
                }else if(type.equals("FAENA")) {
                    code = 6;
                }else if(type.equals("TRANSPORTE")) {
                    code = 7;
                }else if(type.equals("AC")) {
                    code = 9;
                }else if(type.equals("GRUPO ELECTROGEN")) {
                    code = 8;
                }else if(type.equals("EMERGENCY")) {
                    code = 10;
                }else
                    code = -1;

//                if(type.equals("AIR")){
//                    AlertDialog.Builder b = new AlertDialog.Builder(actividad);
//                    final EditText nText = new EditText(mContext);
//                    nText.setBackgroundResource(R.drawable.fondo_edittext);
//                    nText.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//                    String text = REG.getString("NAIR");
//                    nText.setText(text);
//                    b.setTitle("Ingrese cantidad de aires acondicionados");
//                    b.setView(nText);
//                    b.setPositiveButton("OK", null);
//                    b.setNegativeButton("SALIR", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    final AlertDialog d = b.create();
//                    d.show();
//
//                    d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if(nText.getText().length() > 0){
//                                intent.putExtra("NAIR", nText.getText().toString());
//                                REG.addValue("NAIR", nText.getText().toString());
//                                d.dismiss();
//                                startActivityForResult(intent, code);
//                            }else{
//                                Toast.makeText(mContext,"Debe ingresar un número para continuar", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }else {
                    startActivityForResult(intent, code);
//                }
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                b.setTitle("Error");
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setMessage(s);
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                IDEN.setEnabled(false);
                REG.addValue("IDEN" + idMain, true);
            }
            if (requestCode == 1) {
                TRESG.setEnabled(false);
                REG.addValue("3G" + idMain, true);
            }
            if (requestCode == 3) {
                DC.setEnabled(false);
                REG.addValue("DC" + idMain, true);
            }
            if (requestCode == 4) {
                SG.setEnabled(false);
                REG.addValue("SG" + idMain, true);
            }
            if (requestCode == 5) {
                AIR.setEnabled(false);
                REG.addValue("AIR" + idMain, true);
            }
            if (requestCode == 6) {
                FAENA.setEnabled(false);
                REG.addValue("FAENA" + idMain, true);
            }
            if (requestCode == 7) {
                TRANSPORTE.setEnabled(false);
                REG.addValue("TRANSPORTE" + idMain, true);
            }
            if (requestCode == 9) {
                AC.setEnabled(false);
                REG.addValue("AC" + idMain, true);
            }
            if (requestCode == 8) {
                GE.setEnabled(false);
                REG.addValue("GE" + idMain, true);
            }
            if (requestCode == 10) {
                EMERG.setEnabled(false);
                REG.addValue("EMERGENCY" + idMain, true);
            }
        }
    }

    public void enviar(View v) {
        local.listarFicheros(idMain);
        local.crearListaEnvio("answer");
        if (local.itemAnsw.size() > 0) {
            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage("¿Desea cerrar el mantenimiento?");
            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                EnviarMantOff env = new EnviarMantOff();
                env.execute();
                }
            });
            b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            b.setCancelable(false);
            b.show();
        }else{
            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage("Por favor llene algún check");
            b.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            b.setCancelable(false);
            b.show();
        }
    }

    private class Cierre extends AsyncTask<String, String, String> {
        //ProgressDialog p;
        boolean ok = false;

        private Cierre() {
            /*p = new ProgressDialog(actividad);
            p.setMessage("Cerrando Mantenimiento...");
            p.setCanceledOnTouchOutside(false);
            p.setCancelable(false);*/
        }

        @Override
        protected void onPreExecute() {
            //p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String response = SoapRequestTDC.cerrarMantenimiento(IMEI, idMain);
                ArrayList<String> parse = XMLParser.getReturnCode2(response);
                if(parse.get(0).equals("0")){
                    ok = true;
                    return parse.get(1);
                }else{
                    return "Error Code: "+parse.get(0)+"\n"+parse.get(1);
                }
            } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
                return "Problema al recibir la respuesta";
            } catch (IOException e) {
                return "Debe conectarse a una red wifi";
            } catch (Exception e) {
                return "Se agotó el tiempo de conexión";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (p.isShowing()) p.dismiss();
            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            if(ok){
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        REG.clearPreferences();
                        MAINREG.clearPreferences();
                        IDENREG.clearPreferences();
                        TRESGREG.clearPreferences();
                        FAENAREG.clearPreferences();
                        TRANSPREG.clearPreferences();
                        SGREG.clearPreferences();
                        DCREG.clearPreferences();
                        AIRREG.clearPreferences();
                        GEREG.clearPreferences();
                        ACREG.clearPreferences();
                        EMERGREG.clearPreferences();
                        if(AgendaActivity.actividad != null)
                            AgendaActivity.actividad.finish();
                        actividad.finish();
                    }
                });
            }else{
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
            local.eliminarFicheroMant();
            b.show();
        }
    }

    private class EnviarMantOff extends AsyncTask<String, String, String> {                                //probando para enviar localmente al servidor
        boolean ok = false;
        String xml;
        String preg;

        private EnviarMantOff() {
            p = new ProgressDialog(actividad);
            p.setMessage("Cerrando Mantenimiento...");
            p.setCanceledOnTouchOutside(false);
            p.setCancelable(false);
        }

        @Override
        protected void onPreExecute() { p.show(); }

        @Override
        protected String doInBackground(String... strings) {
            String resp = "";                                                                       //Leeremos todos los archivos para enviar y cerrar el mantenimiento
            System.out.println("El valor es " + idMain);
            if (local.itemAnsw.size() > 0) {
                for (int j = 0; j < local.itemAnsw.size(); j++) {
                    System.out.println("Enviaremos " + local.itemAnsw.get(j));
                    if (local.itemAnsw.get(j).contains(",")) {                                      //Verificamos que tenga coma, el archivo tiene por nombre IDMAIN,Nombre.txt
                        String[] parts = local.itemAnsw.get(j).split(",");                          //separamos el archivo antes y despues de la coma
                        String nombreArch = parts[1];                                               // nombre del archivo despues de la coma
                        System.out.println("Parte despues de la coma: " + nombreArch);
                        if (nombreArch.contains(".")) {                                              //Verificamos si contiene punto el nombre
                            String[] nombre = nombreArch.split("\\.");                              //separamos antes y despues del punto
                            String accion = nombre[0];                                              //nombre del archivo antes del punto = a la accion
                            System.out.println("La Accion seria: " + accion);
                            String[] nomArch = local.itemAnsw.get(j).split("\\.");                  //separamos el nombre del archivo antes y despues del punto
                            String nomArchSinTxt = nomArch[0];                                      //Extraemos el nombre sin la extension .txt
                            xml = local.leerFicheroMemoriaExterna(nomArchSinTxt);                   //leemos el archivo del tlf
                            System.out.println("antes: " + xml);
                            String subS = accion.substring(6);
                            String check = idMain + ",check" + subS;
                            preg = local.leerFicheroMemoriaExterna(check);                          //leemos el archivo del tlf
                            try {                                                                   //Enviamos la petioncion al servidor SOAP (ESTO SE REALIZABA POR CADA CHECK ANTERIORMENTE AL PULSAR SOBRE ENVIAR)
                                String response = SoapRequestTDC.sendAll(xml, accion);
                                //ArrayList<String> parse = XMLParser.getReturnCode2(response);
                                cerrarMant = true;
                                resp = "Datos exitosamente ingresados!";
                                subir_fotos(resp,subS);                                             //subS es el nombre del checklist Iden 3g etc
                            } catch (IOException e) {
                                return "Se agotó el tiempo de conexión.";
                            }  catch (Exception e) {
                                return "Error al enviar la respuesta.";
                            }
                        } else {
                            resp = "String " + nombreArch + " No contiene limitador . ";
                        }
                    } else {
                        resp = "String " + local.itemAnsw.get(j) + " No contiene limitador , ";
                    }
                }
            }  else {
                cerrarMant=false;
            }

            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            if(cerrarMant){
                Cierre t = new Cierre();
                t.execute();
            }
        }
    }

    public void subir_fotos(String mensaje, String form) {
        ArrayList<PHOTO> p = new ArrayList<>();
        for (SYSTEM S : ActividadCierreFormActivity.SYSTEMSMAP.get(idMain+","+form.toUpperCase())) {
            for (AREA A : S.getAreas()) {
                for (ITEM I : A.getItems()) {
                    if (I.getIdType().equals(Constantes.PHOTO)) {
                        if (I.getPhoto() != null) {
                            p.add(I.getPhoto());
                        }
                    }
                    if (I.getQuestions() != null) {
                        for (QUESTION Q : I.getQuestions()) {
                            if (Q.getFoto() != null) {
                                p.add(Q.getFoto());
                            }

                            if (Q.getFotos() != null) {
                                for (PHOTO P : Q.getFotos()) {
                                    p.add(P);
                                }
                            }
                            if (Q.getQuestions() != null){
                                for (QUESTION QQ : Q.getQuestions()){
                                    if (QQ.getFoto() != null) {
                                        p.add(QQ.getFoto());
                                    }
                                    if (QQ.getFotos() != null) {
                                        for (PHOTO P : QQ.getFotos()) {
                                            p.add(P);
                                        }
                                    }
                                }
                            }
                            if (Q.getValues() != null){
                                for (VALUE V : Q.getValues()) {
                                    if (V.getQuestions() != null) {
                                        for (QUESTION QQ : V.getQuestions()) {
                                            if (QQ.getFoto() != null) {
                                                p.add(QQ.getFoto());
                                            }
                                            if (QQ.getFotos() != null) {
                                                for (PHOTO P : QQ.getFotos()) {
                                                    p.add(P);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (I.getSetlistArrayList() != null && I.getValues() != null) {
                        if (I.getIdType().equals(Constantes.TABLE)) {
                            for (CheckBox c : I.getCheckBoxes()) {
                                if (c.isChecked()) {
                                    for (SET Set : I.getSetlistArrayList().get(I.getCheckBoxes().indexOf(c))) {
                                        if (Set.getQuestions() != null) {
                                            for (QUESTION Q : Set.getQuestions()) {
                                                if (Q.getFoto() != null) {
                                                    p.add(Q.getFoto());
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO P : Q.getFotos()) {
                                                        p.add(P);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (I.getIdType().equals(Constantes.RADIO)) {
                            RadioGroup rg = (RadioGroup) I.getView();
                            if (rg.getCheckedRadioButtonId() != -1) {
                                RadioButton rb = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                int n = rg.indexOfChild(rb) + 1;
                                for (int i = 0; i < n; i++) {
                                    for (SET Set : I.getSetlistArrayList().get(i)) {
                                        if (Set.getQuestions() != null) {
                                            for (QUESTION Q : Set.getQuestions()) {
                                                if (Q.getFoto() != null) {
                                                    p.add(Q.getFoto());
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO P : Q.getFotos()) {
                                                        p.add(P);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ActividadCierreFormActivity.SYSTEMSMAP.remove(idMain+","+form.toUpperCase());
        }
        if (p.size() > 0) {
            UploadImage up = new UploadImage(p, mensaje);
            up.execute(dummy.URL_UPLOAD_IMG_MAINTENANCE);
        }
    }

    //TODO UPLOAD PHOTOS
    private class UploadImage extends AsyncTask<String, String, String> {

        ArrayList<PHOTO> allPhotos;
        String mensaje;

        public UploadImage(ArrayList<PHOTO> ps, String msj) {
            this.allPhotos = ps;
            this.mensaje = msj;
        }

        @Override
        protected void onPreExecute() {
            /*dialog = new ProgressDialog(mContext);
            dialog.setMessage("Subiendo imagenes...");
            dialog.setCancelable(false);
            dialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            DateFormat timestamp_name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

            for (PHOTO p : allPhotos) {
                try {
                    String fileName = p.getNamePhoto();

                    Log.i("ENVIANDO", fileName);
                    HttpURLConnection conn;
                    DataOutputStream dos;
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    int bytesRead, bytesAvailable, bufferSize;

                    File done = new File(fileName);

                    if (!done.isFile())
                        Log.e("DownloadManager", "no existe");
                    else {
                        FileInputStream fileInputStream = new FileInputStream(done);
                        URL url = new URL(params[0]);

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("uploaded_file", done.getName());

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + done.getName() + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, 1 * 1024 * 1024);
                        byte[] buf = new byte[bufferSize];

                        bytesRead = fileInputStream.read(buf, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buf, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, 1 * 1024 * 1024);
                            bytesRead = fileInputStream.read(buf, 0, bufferSize);

                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();

                        Log.i("UploadManager", "HTTP response is: " + serverResponseMessage + ": " + serverResponseCode);

                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        InputStream responseStream = new BufferedInputStream(conn.getInputStream());

                        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                        String line = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((line = responseStreamReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        responseStreamReader.close();

                        response = stringBuilder.toString();

                        Log.d("IMAGENES", p.getNamePhoto() + "   \n" + response);
                    }

                } catch (Exception e) {
                    Log.d("TAG", "Error: " + e.getMessage());
                    response = "ERROR";
                }
            }
            return response;
        }


        @Override
        protected void onPostExecute(String s) {
            /*if (dialog.isShowing())
                dialog.dismiss();
            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(mensaje);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    REG.clearPreferences();
                    setResult(RESULT_OK);
                    actividad.finish();

                }
            });
            b.show();*/

            REG.clearPreferences();
            setResult(RESULT_OK);

            super.onPostExecute(s);
        }

    }

    protected void onDestroy(){
        super.onDestroy();
        this.wakelock.release();
    }

    protected void onResume(){
        super.onResume();
        wakelock.acquire();
    }

    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        this.wakelock.release();
    }

}
