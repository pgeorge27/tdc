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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

public class ActividadCierreActivity extends Activity implements View.OnClickListener {

    protected PowerManager.WakeLock wakelock;

    private static String TITLE = "Cierre de Actividad";
    private static String IMEI;
    String idMain;

    TextView PAGETITLE;
    public static Activity actividad;
    Context mContext;

    FormCierreReg REG, IDENREG, TRESGREG, FAENAREG, TRANSPREG, SGREG, DCREG, AIRREG, GEREG, EMERGREG;
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
        EMERGREG = new FormCierreReg(this, "EMERG");
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

        state = REG.getBoolean("EMERG" + idMain);
        if(state){
            EMERG.setEnabled(false);
        }

        mostrarBotonesAzules(1);

    }

    //Iteramos sobre idsActivities2 y obtenemos las actividades, deacuerdo a las actividades y el idMain hacemos visible los botones de mantenimiento
    private void mostrarBotonesAzules(int num) {
        for (String temp : AgendaActivity.idsActivities2) {
//            System.out.println(temp + " idM " + idMain);
            if (temp.equalsIgnoreCase("Preventivo,1," + idMain)) {
                IDEN.setVisibility(View.VISIBLE);
            }
            if (temp.equalsIgnoreCase("Preventivo,2," + idMain)) {
                TRESG.setVisibility(View.VISIBLE);
            }

            if (temp.equalsIgnoreCase("Faena de combustible,3," + idMain)) {
                FAENA.setVisibility(View.VISIBLE);
            }

            if (temp.equalsIgnoreCase("Emergencia,1," + idMain )) {
                EMERG.setVisibility(View.VISIBLE);
            }
            //Determinamos si mostramos el boton de RAN o no

            if (temp.equalsIgnoreCase("Preventivo,4," + idMain) || temp.equalsIgnoreCase("Preventivo,5," + idMain)
                    || temp.equalsIgnoreCase("Preventivo,6," + idMain) || temp.equalsIgnoreCase("Preventivo,7," + idMain)
                    || temp.equalsIgnoreCase("Preventivo,8," + idMain) || temp.equalsIgnoreCase("Preventivo,9," + idMain))
                muestraRan();

            //Al pulsar sobre RAN
            if (num == 2) {
                if (temp.equalsIgnoreCase("Preventivo,4," + idMain)) {
                    if (DC.getVisibility() == View.GONE) {
                        DC.setVisibility(View.VISIBLE);
                    } else {
                        DC.setVisibility(View.GONE);
                    }
                }
                if (temp.equalsIgnoreCase("Preventivo,5," + idMain)) {
                    if (SG.getVisibility() == View.GONE) {
                        SG.setVisibility(View.VISIBLE);
                    } else {
                        SG.setVisibility(View.GONE);
                    }
                }
                if (temp.equalsIgnoreCase("Preventivo,6," + idMain)) {
                    if (AIR.getVisibility() == View.GONE) {
                        AIR.setVisibility(View.VISIBLE);
                    } else {
                        AIR.setVisibility(View.GONE);
                    }
                }
                if (temp.equalsIgnoreCase("Preventivo,7," + idMain)) {
                    if (TRANSPORTE.getVisibility() == View.GONE) {
                        TRANSPORTE.setVisibility(View.VISIBLE);
                    } else {
                        TRANSPORTE.setVisibility(View.GONE);
                    }
                }
                if (temp.equalsIgnoreCase("Preventivo,8," + idMain)) {
                    if (GE.getVisibility() == View.GONE) {
                        GE.setVisibility(View.VISIBLE);
                    } else {
                        GE.setVisibility(View.GONE);
                    }
                }
<<<<<<< HEAD
         /*       if (temp.equalsIgnoreCase("9," + idMain)) {
=======
                if (temp.equalsIgnoreCase("Preventivo,9," + idMain)) {
>>>>>>> 07116daae787bf5931a5a201900213dcefd27515
                    if (AC.getVisibility() == View.GONE) {
                        AC.setVisibility(View.VISIBLE);
                    } else {
                        AC.setVisibility(View.GONE);
                    }
                }*/


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
            buscar_form task = new buscar_form("EMERGENCIA");
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
        if (type.equals("EMERGENCIA")) return SoapRequestTDC.ACTION_EMERG;
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
