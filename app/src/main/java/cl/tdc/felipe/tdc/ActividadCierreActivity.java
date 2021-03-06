package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.daemon.PositionTrackerTDC;
import cl.tdc.felipe.tdc.daemon.WifiTrackerTDC;
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

import com.getbase.floatingactionbutton.FloatingActionButton;

public class ActividadCierreActivity extends Activity implements View.OnClickListener {

    protected PowerManager.WakeLock wakelock;

    private static String TITLE = "Cierre de Actividad";
    private static String IMEI;
    public static Intent service_wifi, service_pos;
    String idMain;
    boolean cerrarMant;
    LocalText local = new LocalText();
    ArrayList<SYSTEM> SYSTEMS;
    ProgressDialog p;

    TextView PAGETITLE;
    public static Activity actividad;
    Context mContext;
    ProgressDialog dialog;
    ProgressDialog dialogVerif;
    ArrayList<PHOTO> fotosEnviar;

    FormCierreReg REG, IDENREG, TRESGREG, FAENAREG, TRANSPREG, ACREG, SGREG, DCREG, AIRREG, GEREG, EMERGREG,WIMAXREG, PDHREG, AGREGAREG, SEMESTRALREG, INSPECCIONREG, ANUALREG ;
    MaintenanceReg MAINREG;

    Button IDEN, TRESG, AC, DC, SG, AIR, FAENA, TRANSPORTE, GE, RAN, EMERG, WIMAX, PDH, AGREGADOR, SEMESTRAL, INSPECCION, ANUAL;

    String log;
    FloatingActionButton btn_Enviar;

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
        WIMAXREG = new FormCierreReg(this, "WIMAX");
        PDHREG = new FormCierreReg(this, "PDH");
        AGREGAREG = new FormCierreReg(this, "AGREGADOR");
        SEMESTRALREG = new FormCierreReg(this, "SEMESTRAL");
        INSPECCIONREG = new FormCierreReg(this, "INSPECCION");
        ANUALREG = new FormCierreReg(this, "ANUAL");
        MAINREG = new MaintenanceReg(this);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        PAGETITLE = (TextView) this.findViewById(R.id.header_actual);
        PAGETITLE.setText(TITLE);


        btn_Enviar = (FloatingActionButton) findViewById(R.id.button2);
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
        WIMAX = (Button) this.findViewById(R.id.WIMAX);
        PDH = (Button) this.findViewById(R.id.PDH);
        AGREGADOR = (Button) this.findViewById(R.id.AGREGA);
        SEMESTRAL = (Button) this.findViewById(R.id.SEMESTRAL);
        INSPECCION = (Button) this.findViewById(R.id.INSPECCION);
        ANUAL = (Button) this.findViewById(R.id.ANUAL);

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
        WIMAX.setOnClickListener(this);
        PDH.setOnClickListener(this);
        AGREGADOR.setOnClickListener(this);
        SEMESTRAL.setOnClickListener(this);
        INSPECCION.setOnClickListener(this);
        ANUAL.setOnClickListener(this);

        boolean state = REG.getBoolean("IDEN" + idMain);
        if (state) {
            IDEN.setEnabled(false);
            IDEN.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("3G"+idMain);
        if(state){
            TRESG.setEnabled(false);
            TRESG.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("AC"+idMain);
        if(state){
            AC.setEnabled(false);
            AC.setBackgroundColor(Color.GRAY);
        }
        state = REG.getBoolean("DC"+idMain);
        if(state){
            DC.setEnabled(false);
            DC.setBackgroundColor(Color.GRAY);
        }
        state = REG.getBoolean("SG"+idMain);
        if(state){
            SG.setEnabled(false);
            SG.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("AIR"+idMain);
        if(state){
            AIR.setEnabled(false);
            AIR.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("FAENA"+idMain);
        if(state){
            FAENA.setEnabled(false);
            FAENA.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("INSPECTION" + idMain);
        if(state){
            INSPECCION.setEnabled(false);
            INSPECCION.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("ANUAL" + idMain);
        if(state){
            ANUAL.setEnabled(false);
            ANUAL.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("TRANSPORTE"+idMain);
        if(state){
            TRANSPORTE.setEnabled(false);
            TRANSPORTE.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("GE" + idMain);
        if(state){
            GE.setEnabled(false);
            GE.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("EMERGENCY" + idMain);
        if(state){
            EMERG.setEnabled(false);
            EMERG.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("WIMAX" + idMain);
        if(state){
            WIMAX.setEnabled(false);
            WIMAX.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("PDH"+idMain);
        if(state){
            PDH.setEnabled(false);
            PDH.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("AGREGADOR"+idMain);
        if(state){
            AGREGADOR.setEnabled(false);
            AGREGADOR.setBackgroundColor(Color.GRAY);
        }

        state = REG.getBoolean("SEMESTRAL"+idMain);
        if(state){
            SEMESTRAL.setEnabled(false);
            SEMESTRAL.setBackgroundColor(Color.GRAY);
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
            if (temp.equalsIgnoreCase("Preventivo,11," + idMain)) {
                PDH.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("Preventivo,10," + idMain)) {
                WIMAX.setVisibility(View.VISIBLE);
            }

            if (temp.equalsIgnoreCase("Preventivo,12," + idMain)) {
                AGREGADOR.setVisibility(View.VISIBLE);
            }

            if (num == 1 ) {
                if (temp.startsWith("Emergencia") && temp.endsWith(idMain)){
                    EMERG.setVisibility(View.VISIBLE);
                }else if (!temp.startsWith("Emergencia") && temp.endsWith(idMain)){
                    EMERG.setVisibility(View.GONE);
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

                if (temp.equalsIgnoreCase("Preventivo,10," + idMain)) {
                    WIMAX.setVisibility(View.VISIBLE);
                }
                if (temp.equalsIgnoreCase("Preventivo,11," + idMain)) {
                    PDH.setVisibility(View.VISIBLE);
                }

                if (temp.equalsIgnoreCase("Faena de combustible,3," + idMain)) {
                    FAENA.setVisibility(View.VISIBLE);
                }

                if (temp.equalsIgnoreCase("Preventivo G/E,8," + idMain)) {
                    GE.setVisibility(View.VISIBLE);
                }

                 if (temp.equalsIgnoreCase("Preventivo,13," + idMain)) {
                     SEMESTRAL.setVisibility(View.VISIBLE);
                 }

                if (temp.equalsIgnoreCase("Preventivo,14," + idMain)) {
                    INSPECCION.setVisibility(View.VISIBLE);
                }

                if (temp.equalsIgnoreCase("Preventivo,15," + idMain)) {
                    ANUAL.setVisibility(View.VISIBLE);
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
        if (view.getId() == R.id.WIMAX) {
            buscar_form task = new buscar_form("WIMAX");
            task.execute();
        }
        if (view.getId() == R.id.PDH) { //MATIAS
            buscar_form task = new buscar_form("PDH");
            task.execute();
        }
        if (view.getId() == R.id.AGREGA) {
            buscar_form task = new buscar_form("AGREGADOR");
            task.execute();
        }
        if (view.getId() == R.id.SEMESTRAL) {
            buscar_form task = new buscar_form("SEMESTRAL");
            task.execute();
        }
        if (view.getId() == R.id.INSPECCION) {
            buscar_form task = new buscar_form("INSPECTION");
            task.execute();
        }
        if (view.getId() == R.id.ANUAL) {
            buscar_form task = new buscar_form("ANUAL");
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
        if (type.equals("WIMAX")) return SoapRequestTDC.ACTION_WIMAX;
        if (type.equals("PDH")) return SoapRequestTDC.ACTION_PDH;
        if (type.equals("AGREGADOR")) return SoapRequestTDC.ACTION_AGREGADOR;
        if (type.equals("SEMESTRAL")) return SoapRequestTDC.ACTION_SEMESTRAL;
        if (type.equals("INSPECTION")) return SoapRequestTDC.ACTION_INSPECCION;
        if (type.equals("ANUAL")) return SoapRequestTDC.ACTION_ANUAL;
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
                }else if(type.equals("WIMAX")) {
                    code = 11;
                }else if(type.equals("PDH")) {
                    code = 12;
                }else if(type.equals("AGREGADOR")) {
                    code = 13;
                }else if(type.equals("SEMESTRAL")) {
                    code = 14;
                }else if(type.equals("INSPECTION")) {
                    code = 15;
                }else if(type.equals("ANUAL")) {
                    code = 16;
                }else
                    code = -1;

                    startActivityForResult(intent, code);

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
                IDEN.setBackgroundColor(Color.GRAY);
                REG.addValue("IDEN" + idMain, true);
            }
            if (requestCode == 1) {
                TRESG.setEnabled(false);
                TRESG.setBackgroundColor(Color.GRAY);
                REG.addValue("3G" + idMain, true);
            }
            if (requestCode == 3) {
                DC.setEnabled(false);
                DC.setBackgroundColor(Color.GRAY);
                REG.addValue("DC" + idMain, true);
            }
            if (requestCode == 4) {
                SG.setEnabled(false);
                SG.setBackgroundColor(Color.GRAY);
                REG.addValue("SG" + idMain, true);
            }
            if (requestCode == 5) {
                AIR.setEnabled(false);
                REG.addValue("AIR" + idMain, true);
            }
            if (requestCode == 6) {
                FAENA.setEnabled(false);
                AIR.setBackgroundColor(Color.GRAY);
                REG.addValue("FAENA" + idMain, true);
            }
            if (requestCode == 7) {
                TRANSPORTE.setEnabled(false);
                TRANSPORTE.setBackgroundColor(Color.GRAY);
                REG.addValue("TRANSPORTE" + idMain, true);
            }
            if (requestCode == 9) {
                AC.setEnabled(false);
                AC.setBackgroundColor(Color.GRAY);
                REG.addValue("AC" + idMain, true);
            }
            if (requestCode == 8) {
                GE.setEnabled(false);
                GE.setBackgroundColor(Color.GRAY);
                REG.addValue("GE" + idMain, true);
            }
            if (requestCode == 10) {
                EMERG.setEnabled(false);
                EMERG.setBackgroundColor(Color.GRAY);
                REG.addValue("EMERGENCY" + idMain, true);
            }
            if (requestCode == 11) {
                WIMAX.setEnabled(false);
                WIMAX.setBackgroundColor(Color.GRAY);
                REG.addValue("WIMAX" + idMain, true);
            }
            if (requestCode == 12) {
                PDH.setEnabled(false);
                PDH.setBackgroundColor(Color.GRAY);
                REG.addValue("PDH" + idMain, true);
            }
            if (requestCode == 13) {
                AGREGADOR.setEnabled(false);
                AGREGADOR.setBackgroundColor(Color.GRAY);
                REG.addValue("AGREGADOR" + idMain, true);
            }
            if (requestCode == 14) {
                SEMESTRAL.setEnabled(false);
                SEMESTRAL.setBackgroundColor(Color.GRAY);
                REG.addValue("SEMESTRAL" + idMain, true);
            }
            if (requestCode == 15) {
                INSPECCION.setEnabled(false);
                INSPECCION.setBackgroundColor(Color.GRAY);
                REG.addValue("INSPECTION" + idMain, true);
            }
            if (requestCode == 16) {
                ANUAL.setEnabled(false);
                ANUAL.setBackgroundColor(Color.GRAY);
                REG.addValue("ANUAL" + idMain, true);
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        NetworkInfo m4G = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        int networkType = m4G.getSubtype();


        if (mWifi.isConnected()) {
            return true;
        } else if (networkType == TelephonyManager.NETWORK_TYPE_LTE || (networkType ==TelephonyManager.NETWORK_TYPE_UMTS) || (networkType == TelephonyManager.NETWORK_TYPE_EVDO_0)
                || (networkType == TelephonyManager.NETWORK_TYPE_EVDO_A)|| (networkType ==  TelephonyManager.NETWORK_TYPE_HSDPA) || (networkType == TelephonyManager.NETWORK_TYPE_HSUPA)
                || (networkType == TelephonyManager.NETWORK_TYPE_HSPA) || (networkType ==  TelephonyManager.NETWORK_TYPE_EVDO_B) //api<9 : replace by 14
                || (networkType == TelephonyManager.NETWORK_TYPE_EHRPD)  //api<11 : replace by 12
                || (networkType ==  TelephonyManager.NETWORK_TYPE_HSPAP)) {
            return true;
        }

        return false;
    }

    public void enviar(View v) {
       // final FloatingActionButton btn_Enviar = (FloatingActionButton) findViewById(R.id.button2);
        if (isOnline()) {

            local.listarFicheros(idMain);
            //  Log.e("titulo main", "El id maain es ::::" + idMain);
            local.crearListaEnvio("answer");
            if (local.itemAnsw.size() > 0) {
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                b.setMessage("¿Desea cerrar el mantenimiento?");
                b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    btn_Enviar.setEnabled(false);
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
            } else {
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
        } else {
            btn_Enviar.setEnabled(true);
            Toast.makeText(ActividadCierreActivity.this, "Verifique si tiene plan de data móvil o Wifi", Toast.LENGTH_LONG).show();
        }
    }

    private class Cierre extends AsyncTask<String, String, String> {
        //ProgressDialog p;
        boolean ok = false;

        private Cierre() {

        }

        @Override
        protected void onPreExecute() {
            //p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            LocalText localT = new LocalText();
            String query = "";

            try {
                String response = SoapRequestTDC.cerrarMantenimiento(IMEI, idMain);
                query = SoapRequestTDC.getPlanningMaintenance(IMEI);
                if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                    localT.escribirFicheroMemoriaExterna("planing-mantience", query);
                ArrayList<String> parse = XMLParser.getReturnCode2(response);
                if(parse.get(0).equals("0")){
                    ok = true;
                    REG.clearPreferences();
                    return parse.get(1);
                }else{
                    return "Error Code: "+parse.get(0)+"\n"+parse.get(1);
                }
            } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
                return "Problema al recibir la respuesta";
            } catch (IOException e) {
                return "Debe conectarse a una red wifi o tener plan de data móvil";
            } catch (Exception e) {
                return "Se agotó el tiempo de conexión";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (p.isShowing()) p.dismiss();
            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);//ok
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
                        WIMAXREG.clearPreferences();
                        PDHREG.clearPreferences();
                        AGREGAREG.clearPreferences();
                        SEMESTRALREG.clearPreferences();
                        ANUALREG.clearPreferences();

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

           // local.eliminarFicheroMant(); //para borrar fichero
             b.show();//ok
        }
    }

    private class EnviarMantOff extends AsyncTask<String, String, String> {                                //probando para enviar localmente al servidor
        boolean ok = false;
        String xml;
        String preg;

        private EnviarMantOff() {
            p = new ProgressDialog(actividad);
            p.setMessage("Enviando Mantenimientos...");
            p.setCanceledOnTouchOutside(false);
            p.setCancelable(false);
        }

        @Override
        protected void onPreExecute() { p.show(); }

        @Override
        protected String doInBackground(String... strings) {
            String resp = "";                                                                       //Leeremos todos los archivos para enviar y cerrar el mantenimiento
            cerrarMant=true;
            System.out.println("El valor es " + idMain);
            log +="\n El valor es " + idMain;
            if (local.itemAnsw.size() > 0) {
                for (int j = 0; j < local.itemAnsw.size(); j++) {
                    System.out.println("Enviaremos " + local.itemAnsw.get(j));
                    log +="Enviaremos " + local.itemAnsw.get(j);
                    if (local.itemAnsw.get(j).contains(",")) {                                      //Verificamos que tenga coma, el archivo tiene por nombre IDMAIN,Nombre.txt
                        String[] parts = local.itemAnsw.get(j).split(",");                          //separamos el archivo antes y despues de la coma
                        String nombreArch = parts[1];                                               // nombre del archivo despues de la coma
                        System.out.println("Parte despues de la coma: " + nombreArch);
                        if (nombreArch.contains(".")) {                                             //Verificamos si contiene punto el nombre
                            String[] nombre = nombreArch.split("\\.");                              //separamos antes y despues del punto
                            String accion = nombre[0];                                              //nombre del archivo antes del punto = a la accion
                            System.out.println("La Accion seria: " + accion);
                            log +="La Accion seria: " + accion;
                            String[] nomArch = local.itemAnsw.get(j).split("\\.");                  //separamos el nombre del archivo antes y despues del punto
                            String nomArchSinTxt = nomArch[0];                                      //Extraemos el nombre sin la extension .txt
                            log +="\n leerFicheroMemoriaExterna(" +nomArchSinTxt +")";
                            xml = local.leerFicheroMemoriaExterna(nomArchSinTxt);                   //leemos el archivo del tlf
                            System.out.println("antes: " + xml);
                            String subS = accion.substring(6);
                            String check = idMain + ",check" + subS;
                            log +="check=" + check;
                            preg = local.leerFicheroMemoriaExterna(check);                          //leemos el archivo del tlf
                            try {                                                                   //Enviamos la petioncion al servidor SOAP (ESTO SE REALIZABA POR CADA CHECK ANTERIORMENTE AL PULSAR SOBRE ENVIAR)
                                String response = SoapRequestTDC.sendAll(xml, accion);
                                log +="\n response = SoapRequestTDC.sendAll(xml, accion)::" +response ;
                                resp = "Datos exitosamente ingresados!";
                            } catch (IOException e) {
                                cerrarMant=false;
                                return "Se agotó el tiempo de conexión.";
                            }  catch (Exception e) {
                                log += "Exception e" + e;
                                cerrarMant=false;
                                return "Error al enviar la respuesta.";
                            }
                        } else {
                            cerrarMant=false;
                            resp = "String " + nombreArch + " No contiene limitador . ";
                        }
                    } else {
                        cerrarMant=false;
                        resp = "String " + local.itemAnsw.get(j) + " No contiene limitador , ";
                    }
                }
            } else {
                cerrarMant=false;
            }
            log += "return resp" + resp;
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            p.dismiss();
            if(cerrarMant){
                int fin = local.itemAnsw.size();
                fotosEnviar = new ArrayList<>();
                for (int j = 0; j < fin; j++) {
                    String[] parts = local.itemAnsw.get(j).split(",");                       //separamos el archivo antes y despues de la coma
                    String nombreArch = parts[1];                                           // nombre del archivo despues de la coma
                    String[] nombre = nombreArch.split("\\.");                              //separamos antes y despues del punto
                    String accion = nombre[0];                                              //nombre del archivo antes del punto = a la accion
                    String subS = accion.substring(6);
                    subir_fotos(subS);
                }
                if (fotosEnviar.size() > 0) {
                   /* for (int i = 0; i < fotosEnviar.size(); i++){
                        redimencionarImagen(fotosEnviar.get(i).getNamePhoto());
                    }*/
                    UploadImage up = new UploadImage(fotosEnviar);
                    up.execute(dummy.URL_UPLOAD_IMG_MAINTENANCE);
                } else {
                    Cierre t = new Cierre();
                    t.execute();
                }
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                b.setTitle("Cerrar Mantenimiento");
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setMessage("No se pudo cerrar el mantenimiento");

                b.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        actividad.finish();
                    }
                });
                b.show();
            }
            log += "" + s;
            local.escribirFicheroMemoriaExterna("LogSubirXml"+ idMain ,log);
        }
    }

    public void subir_fotos(String form) {
        //ArrayList<PHOTO> fotosEnviar = new ArrayList<>();

        Log.d("LOG",form);

        if (form.equalsIgnoreCase("emerg")){
            form = "Emergency";
        } else if (form.equalsIgnoreCase("ge")){
            form = "Grupo Electrogen";      //STATIONARY AND PORTABLE POWER GENERATOR / FUEL SYSTEM / FUEL OPERATIONS
        } else if (form.equalsIgnoreCase("agregator")){
            form = "Agregador";
        }else if (form.equalsIgnoreCase("transport")){
            form = "Transporte";
        } else if (form.equalsIgnoreCase("system")) {
            form = "System Ground";
        }
        log = " Iniciamos con el formulario: " + form;

        for (SYSTEM S : ActividadCierreFormActivity.SYSTEMSMAP.get(idMain+","+form.toUpperCase())) {
                log += " \n Obteniendo SYSTEM: " + idMain+","+form + " de SYSTEMSMAP";
            for (AREA A : S.getAreas()) {
                for (ITEM I : A.getItems()) {
                    if (I.getIdType().equals(Constantes.PHOTO)) {
                        if (I.getPhoto() != null) {
                            fotosEnviar.add(I.getPhoto());
                        }
                        if (I.getFotos() != null) {
                            for (PHOTO P : I.getFotos()) {
                                fotosEnviar.add(P);
                            }
                        }
                    }
                    if (I.getQuestions() != null) {
                        for (QUESTION Q : I.getQuestions()) {
                            if (Q.getFoto() != null) {
                                fotosEnviar.add(Q.getFoto());
                            }

                            if (Q.getFotos() != null) {
                                for (PHOTO P : Q.getFotos()) {
                                    fotosEnviar.add(P);
                                }
                            }
                            if (Q.getQuestions() != null){
                                for (QUESTION QQ : Q.getQuestions()){
                                    if (QQ.getFoto() != null) {
                                        fotosEnviar.add(QQ.getFoto());
                                    }
                                    if (QQ.getFotos() != null) {
                                        for (PHOTO P : QQ.getFotos()) {
                                            fotosEnviar.add(P);
                                        }
                                    }
                                }
                            }
                            if (Q.getValues() != null){
                                for (VALUE V : Q.getValues()) {
                                    if (V.getQuestions() != null) {
                                        for (QUESTION QQ : V.getQuestions()) {
                                            if (QQ.getFoto() != null) {
                                                fotosEnviar.add(QQ.getFoto());
                                            }
                                            if (QQ.getFotos() != null) {
                                                for (PHOTO P : QQ.getFotos()) {
                                                    fotosEnviar.add(P);
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
                                                    fotosEnviar.add(Q.getFoto());
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO P : Q.getFotos()) {
                                                        fotosEnviar.add(P);
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
                                                    fotosEnviar.add(Q.getFoto());
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO P : Q.getFotos()) {
                                                        fotosEnviar.add(P);
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
            log += " \nEliminamos " + idMain+","+form.toUpperCase();
            //ActividadCierreFormActivity.SYSTEMSMAP.remove(idMain+","+form.toUpperCase());
        }
    }

    private void redimencionarImagen(String dir) {
        //File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        log += " \n Entramos en el metodo redimencionarImagen la ruta es " + dir;
        Bitmap b = BitmapFactory.decodeFile(dir);
        Bitmap out = Bitmap.createScaledBitmap(b, b.getHeight() / 2, b.getWidth()/2, false);

        String[] textos = dir.split("/");
        String nombreImagen = textos[textos.length-1];
        String rutaImagen  = dir.replace(nombreImagen, "");

        log += " \n Nombre de la imagen:  " + nombreImagen;
        log += " \n Ruta de la imagen:  " + rutaImagen;

        File file = new File(rutaImagen, nombreImagen);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.JPEG, 50, fOut); //60
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {
            log += " \n Error en el proceso de crear nuevo archivo de imagen:  " + e;
        }
    }

    private class UploadImage extends AsyncTask<String, String, String> {

        ArrayList<PHOTO> allPhotos;
        boolean subioFoto;

        public UploadImage(ArrayList<PHOTO> ps) {
            this.allPhotos = ps;
            this.subioFoto = true;

            p = new ProgressDialog(actividad);
            p.setMessage("Subiendo Imagenes...");
            p.setCanceledOnTouchOutside(false);
            p.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            p.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            log += " \n Dentro del metodo doInBackground ";

            DateFormat timestamp_name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

            for (PHOTO p : allPhotos) {
                log += " \n iteramos sobre la variable allPhotos ";
                try {
                    String fileName = p.getNamePhoto();
                    log += " \n El archivo es: " + fileName;
                    Log.i("ENVIANDO", fileName);
                    HttpURLConnection conn;
                    DataOutputStream dos;
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    int bytesRead, bytesAvailable, bufferSize;

                    File done = new File(fileName);

                    if (!done.isFile()) {
                        log += " \n El archivo: " + done + " no existe";
                        Log.e("DownloadManager", "no existe");
                    } else {
                        log += " \n FileInputStream: " + done;
                        FileInputStream fileInputStream = new FileInputStream(done);
                        URL url = new URL(dummy.URL_UPLOAD_IMG_MAINTENANCE);
                        log += " \n url: " + url;

                        conn = (HttpURLConnection) url.openConnection();
                        log += " \n Abrimos coneccion con url.openConnection() ";
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

                        Log.e("Valor com", conn.toString());

                        Log.i("UploadManager", "HTTP response is: " + serverResponseMessage + ": " + serverResponseCode);
                        log += " \n UploadManage HTTP response is: " + serverResponseMessage + ": " + serverResponseCode;

                        fileInputStream.close();

                        log += " \n fileInputStream.close() ";
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

                        if(!serverResponseMessage.equalsIgnoreCase("OK")){
                            subioFoto = false;
                        }
                        Log.d("IMAGENES", p.getNamePhoto() + "   \n" + response);
                        log += "IMAGENES " + p.getNamePhoto() + "   \n" + response;
                    }

                } catch (Exception e) {
                    Log.d("TAG", "Error: " + e.getMessage());
                    subioFoto = false;
                    log += "Error --- " + e.getMessage(); //error de conec
                    response = "ERROR";
                }
            }
            log += "Response: " + response;
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            p.dismiss();
            Log.d("VALOR DE S EN EL POST", s);
            local.escribirFicheroMemoriaExterna("LogSubirFoto"+ idMain ,log);
            if(subioFoto){
                Log.e("Cerramos", "Cerramos Mant");
                //Aqui verificarlo
               /* Cierre t = new Cierre();
                t.execute();*/
                VerificationPhotosTask verificationPhotosTaskTask = new VerificationPhotosTask(actividad);
                verificationPhotosTaskTask.execute(idMain);
            } else {
                Log.e("Reenviamos", "Reenviamos Mant");
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                b.setTitle("Cerrar Mantenimiento");
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setMessage("No se pudo cerrar el mantenimiento");

                b.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        btn_Enviar.setEnabled(true);
                        actividad.finish();
                    }
                });
                b.show();
            }
           // REG.clearPreferences();
            setResult(RESULT_OK);
            super.onPostExecute(s);
        }
    }

    private class VerificationPhotosTask extends AsyncTask<String, String, String> {
        Context tContext;
        ProgressDialog dialog;
        String message;
        boolean ok = false;

        private VerificationPhotosTask(Context tContext) {
            this.tContext = tContext;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(tContext);
            dialog.setMessage("Verificando fotos en el servidor...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String query = SoapRequest.verificationPhotos(idMain);
                ArrayList<String> parse = XMLParser.getReturnCode2(query);

                ok = parse.get(0).equals("0");

                if (ok)

                    return query;
                else
                    return parse.get(1);
            } catch (SAXException | ParserConfigurationException | XPathExpressionException e) {
                e.printStackTrace();
                message = dummy.ERROR_PARSE;
            } catch (IOException e) {
                e.printStackTrace();
                message = dummy.ERROR_CONNECTION;
            } catch (Exception e) {
                e.printStackTrace();
                message = dummy.ERROR_GENERAL;
            }
            return dummy.ERROR_GENERAL;
        }


        @Override
        protected void onPostExecute(String s) {
            if (ok) {
                Cierre t = new Cierre();
                t.execute();
               // Toast.makeText(ActividadCierreActivity.this, "Coordenadas enviadas Correctamente", Toast.LENGTH_SHORT).show();

            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                b.setTitle("Verificación de fotos");
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setMessage("No se recibio el número de fotos correctas");
                b.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        actividad.finish();
                    }
                });
                b.show();
            }
            if (dialog.isShowing()) dialog.dismiss();
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
