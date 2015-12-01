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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.objects.Maintenance.Agenda;
import cl.tdc.felipe.tdc.preferences.FormCierreReg;
import cl.tdc.felipe.tdc.preferences.MaintenanceReg;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.SoapRequestTDC;
import cl.tdc.felipe.tdc.webservice.XMLParser;

public class ActividadCierreActivity extends Activity implements View.OnClickListener {
    private static String TITLE = "Cierre de Actividad";
    private static String IMEI;
    String idMain;
    TextView PAGETITLE;
    public static Activity actividad;
    Context mContext;

    FormCierreReg REG, IDENREG, TRESGREG, FAENAREG, TRANSPREG, SGREG, DCREG, AIRREG, GEREG;
    MaintenanceReg MAINREG;

    Button IDEN, TRESG, AC, DC, SG, AIR, FAENA, TRANSPORTE, GE;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre_actividad);
        actividad = this;
        mContext = this;

        REG = new FormCierreReg(this, "LISTADO");
        IDENREG = new FormCierreReg(this, "IDEN");
        TRESGREG = new FormCierreReg(this, "3G");
        FAENAREG = new FormCierreReg(this, "FAENA");
        TRANSPREG = new FormCierreReg(this, "TRANSPORTE");
        SGREG = new FormCierreReg(this, "SG");
        DCREG = new FormCierreReg(this, "DC");
        AIRREG = new FormCierreReg(this, "AIR");
        GEREG = new FormCierreReg(this, "GE");
        MAINREG = new MaintenanceReg(this);

        idMain = getIntent().getStringExtra("MAINTENANCE");

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
        IDEN.setOnClickListener(this);
        TRESG.setOnClickListener(this);
        AC.setOnClickListener(this);
        DC.setOnClickListener(this);
        SG.setOnClickListener(this);
        AIR.setOnClickListener(this);
        FAENA.setOnClickListener(this);
        TRANSPORTE.setOnClickListener(this);
        GE.setOnClickListener(this);

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

        mostrarBotonesAzules();

    }

    //Iteramos sobre idsActivities2 y obtenemos las actividades, deacuerdo a las actividades y el idMain hacemos visible los botones de mantenimiento
    private void mostrarBotonesAzules() {
        for (String temp : AgendaActivity.idsActivities2) {
            System.out.println(temp + " idM " + idMain);

            if (temp.equalsIgnoreCase("1,"+idMain)){
                IDEN.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("2,"+idMain)){
                TRESG.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("3,"+idMain)){
                FAENA.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("4,"+idMain)){
                DC.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("5,"+idMain)){
                SG.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("6,"+idMain)){
                AIR.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("7,"+idMain)){
                TRANSPORTE.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("8,"+idMain)){
                AC.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("9,"+idMain)){
                GE.setVisibility(View.VISIBLE);
            }
        }
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
        else return "";
    }


    private class buscar_form extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        String type;
        String query;
        boolean flag = false;

        private buscar_form(String type) {
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
                query = SoapRequestTDC.getFormularioCierre(IMEI, idMain, getAction(type));
//                query = "<SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Configurationwsdl\"><SOAP-ENV:Body><ns1:checkTransportResponse xmlns:ns1=\"urn:Configurationwsdl\"><Response xsi:type=\"tns:Response\"><Code xsi:type=\"xsd:string\">0</Code><Description xsi:type=\"xsd:string\">Se ha realizado correctamente la operacion</Description><Check xsi:type=\"tns:Check\"><Systems xsi:type=\"tns:Systems\"><System xsi:type=\"tns:System\"><IdSystem xsi:type=\"xsd:string\">14</IdSystem><NameSystem xsi:type=\"xsd:string\">RF &amp; TRANSPORT SITE INFRASTRUCTURE</NameSystem><Areas xsi:type=\"tns:Areas\"><Area xsi:type=\"tns:Area\"><IdArea xsi:type=\"xsd:string\">21</IdArea><NameArea xsi:type=\"xsd:string\">Base</NameArea><Items xsi:type=\"tns:Items\"><Item xsi:type=\"tns:Item\"><IdItem xsi:type=\"xsd:string\">47</IdItem><IdType xsi:nil=\"true\" xsi:type=\"xsd:string\"/><NameType xsi:nil=\"true\" xsi:type=\"xsd:string\"/><NameItem xsi:type=\"xsd:string\">Impacto Ambiental</NameItem><Answer xsi:nil=\"true\" xsi:type=\"xsd:string\"/><Questions xsi:type=\"tns:Questions\"><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">323</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Existencia de aceites para retiro</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">324</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Existencia de filtros para retiro</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">325</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Existencia cilindros refrigerante para retiro</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">326</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Existencia de trapos, envaces y materiales contaminados o tóxicos</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">327</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Existencia de repuestos reemplazados e insumos utilizados para retiro</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">328</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Existencia de derrames de combustible, aceites u otros tóxicos</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">329</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Observaciones</NameQuestion><NameType xsi:type=\"xsd:string\">TEXT</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">3</IdType></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">330</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Fotografia del perimetro o desechos</NameQuestion><NameType xsi:type=\"xsd:string\">PHOTO</NameType><Photo xsi:type=\"xsd:string\">OK</Photo><NumberPhoto xsi:type=\"xsd:string\">3</NumberPhoto><IdType xsi:type=\"xsd:string\">5</IdType></Question></Questions></Item><Item xsi:type=\"tns:Item\"><IdItem xsi:type=\"xsd:string\">48</IdItem><IdType xsi:nil=\"true\" xsi:type=\"xsd:string\"/><NameType xsi:nil=\"true\" xsi:type=\"xsd:string\"/><NameItem xsi:type=\"xsd:string\">Estructura Accesos y Seguridad</NameItem><Answer xsi:nil=\"true\" xsi:type=\"xsd:string\"/><Questions xsi:type=\"tns:Questions\"><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">331</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Control corrosión y reparación puertas, rejillas ventilación, cerraduras,bisagras,llaves, candados, rejas, perímetro y sistema drenaje.</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">332</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Limpieza interior y exterior del Site</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">333</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Control de drenaje de las filtraciones de aire acondicionado</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">334</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Control de plagas de insectos y roedores (fumigacion, si es necesario)</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">335</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Reemplazos de extintores</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">336</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Mantenimiento de torre. Incluye pintado y suministro de pintura, si es necesario</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">337</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Mantenimiento de bases de torres</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">338</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Revisar y reemplazar la perneria de la torre, si es necesario</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">339</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Revisar y reemplazar elementos de sujeccion, abrazaderas, abarcones, perneria oxidados y soporte de elementos en torre</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">340</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Mantenimiento de drenajes</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">341</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Verificar la verticalidad de la torre</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">342</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Cuaderno de Novedades (anotar última visita)</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">343</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Caminos de acceso al Site</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">344</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Luminaria peímetro del Site y en Sala de GE (reemplazar si es necesario)</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">345</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Luz de emergencia del shelter (reemplazar si es necesario)</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">346</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Funcionamiento de luz de balizaje (reemplazar si es necesario)</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">347</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Mimetizado del Site</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">348</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Prueba de alarmas de entorno (puerta abierta, la temperatura, etc)</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">349</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Extintor</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue><IdValueAlteraVisible xsi:type=\"xsd:string\">350</IdValueAlteraVisible></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">350</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Fecha de vencimiento</NameQuestion><NameType xsi:type=\"xsd:string\">DATE</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">10</IdType><Visible xsi:type=\"xsd:string\">false</Visible></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">351</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Linea de vida</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">352</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Otros</NameQuestion><NameType xsi:type=\"xsd:string\">RADIO</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">1</IdType><Values xsi:type=\"tns:Values\"><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">SI</NameValue><IdValue xsi:type=\"xsd:string\">3</IdValue></Value><Value xsi:type=\"tns:Value\"><NameValue xsi:type=\"xsd:string\">NO</NameValue><IdValue xsi:type=\"xsd:string\">4</IdValue></Value></Values></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">353</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Observaciones</NameQuestion><NameType xsi:type=\"xsd:string\">TEXT</NameType><Photo xsi:type=\"xsd:string\">NOK</Photo><NumberPhoto xsi:type=\"xsd:string\">0</NumberPhoto><IdType xsi:type=\"xsd:string\">3</IdType></Question><Question xsi:type=\"tns:Question\"><IdQuestion xsi:type=\"xsd:string\">354</IdQuestion><NameQuestion xsi:type=\"xsd:string\">Fotografias</NameQuestion><NameType xsi:type=\"xsd:string\">PHOTO</NameType><Photo xsi:type=\"xsd:string\">OK</Photo><NumberPhoto xsi:type=\"xsd:string\">10</NumberPhoto><IdType xsi:type=\"xsd:string\">5</IdType></Question></Questions></Item></Items></Area></Areas></System></Systems></Check></Response></ns1:checkTransportResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
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
                }else if(type.equals("AC")) {
                    code = 2;
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
                }else if(type.equals("GRUPO ELECTROGEN")) {
                    code = 8;
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
            if (requestCode == 8) {
                GE.setEnabled(false);
                REG.addValue("GE" + idMain, true);
            }
        }
    }

    public void enviar(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(actividad);
        b.setMessage("¿Desea cerrar el mantenimiento?");
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Cierre t = new Cierre();
                t.execute();
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
    }

    private class Cierre extends AsyncTask<String, String, String> {
        ProgressDialog p;
        boolean ok = false;

        private Cierre() {
            p = new ProgressDialog(actividad);
            p.setMessage("Cerrando Mantenimiento...");
            p.setCanceledOnTouchOutside(false);
            p.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            p.show();
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
                return "Problema al recibir la respuesta";
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
            b.show();
        }
    }


}
