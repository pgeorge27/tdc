package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.adapters.Actividad;
import cl.tdc.felipe.tdc.adapters.Actividades;
import cl.tdc.felipe.tdc.adapters.Maintenance;
import cl.tdc.felipe.tdc.extras.Funciones;
import cl.tdc.felipe.tdc.extras.LocalText;
import cl.tdc.felipe.tdc.objects.FormularioCheck;
import cl.tdc.felipe.tdc.objects.Maintenance.Agenda;
import cl.tdc.felipe.tdc.objects.Maintenance.MainSystem;
import cl.tdc.felipe.tdc.preferences.MaintenanceReg;
import cl.tdc.felipe.tdc.preferences.PreferencesTDC;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.SoapRequestCheckLists;
import cl.tdc.felipe.tdc.webservice.SoapRequestTDC;
import cl.tdc.felipe.tdc.webservice.XMLParser;
import cl.tdc.felipe.tdc.webservice.XMLParserChecklists;
import cl.tdc.felipe.tdc.webservice.dummy;

public class AgendaActivity extends Activity {
    private static int REQUEST_FORMULARIO = 0;
    private static int RESULT_OK = 0;
    private static int RESULT_NOK = 1;
    private static int RESULT_CANCELED = 1;

    public static Activity actividad;
    private String IMEI;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<Maintenance> m;
    MaintenanceReg pref;
    Button checklist;
    ProgressBar progressBar;
    ImageButton complete;

    int ultimaMantencion = -1;

    public static ArrayList<String> idsActivities = new ArrayList<>();
    public static ArrayList<String> idsActivities2 = new ArrayList<>();
    public static String idActivitiePeriodo;
    public static Set<String> hs = new HashSet<>();

    private String name;
    private String query;
    private String queryT;
    private String idMain, periodo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_new);
        actividad = this;
        pref = new MaintenanceReg(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();
        mPager = (ViewPager) findViewById(R.id.agenda_contentPager);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        init();
        init_ImageLoader();
    }

    private void init() {
        /*checklist = (Button) findViewById(R.id.checklist);
        checklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pref.getChecklistState()) {
                    CheckListTask task = new CheckListTask(actividad);
                    task.execute();
                }else{
                    Toast.makeText(actividad.getApplicationContext(), "Ya ha finalizado el Checklist de Manteniemiento.\nPuede continuar con el cierre del Mantenimiento.",Toast.LENGTH_LONG).show();
                }
            }
        });*/
        AgendaTask agenda = new AgendaTask(this);
        agenda.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init_ImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheOnDisk(true) // default
                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.defaultDisplayImageOptions(options);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    // TODO: funcion onClick del botón apagar.

    public void onClick_apagar(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Seguro que desea salir de Agenda?");
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
        //super.onBackPressed();
        onClick_back(null);
    }

    public void onClick_back(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Seguro que desea salir de Agenda?");
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

    private class CompletarActividad extends AsyncTask<String, String, FormularioCheck> {
        private String CTAG = "COMPLETARACTIVIDAD";
        Context mContext;
        ProgressDialog progressDialog;
        int idMaintenance;
        String queryCopy;

        public CompletarActividad(Context c, int ID) {
            this.mContext = c;
            this.idMaintenance = ID;
            progressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected FormularioCheck doInBackground(String... strings) {
            try {
                /*publishProgress("Abriendo formulario...");
                String query = SoapRequest.typeActivity(IMEI, idMaintenance);
                Log.d(CTAG, "TYPE\n" + query);
                ArrayList<String> parse = XMLParser.getTypeActivity(query);
                Log.d(CTAG, "TYPE\n" + parse.toString());
                */
                publishProgress("Cierre de Actividad...");
                String query = SoapRequest.FormPrev(IMEI, idMaintenance);
                FormularioCheck parse = XMLParser.getForm(query);
                queryCopy = query;
                return parse;
            } catch (Exception e) {
                Log.e(CTAG, e.getMessage() + ":\n" + e.getCause());
                return null;
            }
        }

        @Override
        protected void onPostExecute(FormularioCheck s) {
            if (s != null) {
                if (s.getCode().compareTo("0") == 0) {
                    Toast.makeText(mContext, s.getDescription(), Toast.LENGTH_LONG).show();
                    Intent n = new Intent(AgendaActivity.this, FormCheckActivity.class);
                    n.putExtra("RESPONSE", queryCopy);
                    n.putExtra("MANTENIMIENTO", idMaintenance);
                    n.putStringArrayListExtra("ACTIVIDADES", idsActivities);
                    startActivity(n);
                } else {
                    Toast.makeText(mContext, s.getDescription(), Toast.LENGTH_LONG).show();
                }
            }

            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private class CheckListTask extends AsyncTask<String, String, String> {
        Context tContext;
        ProgressDialog dialog;
        boolean ok = false;

        private CheckListTask(Context tContext) {
            this.tContext = tContext;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(tContext);
            dialog.setMessage("Solicitando Checklist de Mantenimiento...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String query = SoapRequestCheckLists.getMainChecklist(pref.getID());

                String[] code = XMLParserChecklists.getResultCode(query).split(";");
                if (code[0].compareTo("0") == 0) {
                    ok = true;
                    return query;
                } else {
                    ok = false;
                    return code[1];
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Ha ocurrido un error (" + e.getMessage() + "). Por favor reintente.";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (ok) {

                Intent check = new Intent(actividad, FormCheckMaintenance.class);
                check.putExtra("RESPONSE", s);
                check.putExtra("ID", pref.getID());
                startActivity(check);

            } else {
                Toast.makeText(tContext, s, Toast.LENGTH_LONG).show();
            }
            if (dialog.isShowing()) dialog.dismiss();
        }
    }

    private class AgendaTask extends AsyncTask<String, String, Agenda> {
        ProgressDialog progressDialog;
        Context tContext;
        String ATAG = "AGENDATASK";


        String message = "";
        Boolean connected = false;

        public AgendaTask(Context context) {
            this.tContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(tContext);
            progressDialog.setTitle("Espere por favor...");
            progressDialog.show();
        }


        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected Agenda doInBackground(String... strings) {
            try {
                publishProgress("Cargando Actividades...");

                LocalText localT = new LocalText();

                if (!getIntent().getStringExtra("LOCAL").equalsIgnoreCase("local")) {         //En caso de tener conexion a internet creamos el archivo planing-mantience localmente
                    query = SoapRequestTDC.getPlanningMaintenance(IMEI);
                    Log.d("FRAGMENT", query);

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna("planing-mantience", query);
                }

                String mantenimientoLocal = localT.leerFicheroMemoriaExterna("planing-mantience");

                //Agenda agenda = XMLParser.getMaintenance(query);
                //Agenda agenda = XMLParser.getMaintenance("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Configurationwsdl\"><SOAP-ENV:Body><ns1:planningMaintenanceResponse xmlns:ns1=\"urn:Configurationwsdl\"><ResponsePlan xsi:type=\"tns:ResponsePlan\"><Information xsi:type=\"tns:Information\"><Maintenance xsi:type=\"tns:Maintenance\"><Date xsi:type=\"xsd:string\">2016-02-24 19:14:00</Date><Latitude xsi:type=\"xsd:string\">-12.1271</Latitude><Longitude xsi:type=\"xsd:string\">-77.0267</Longitude><Address xsi:type=\"xsd:string\">bulnes</Address><Station xsi:type=\"xsd:string\">Sitio_de_prueba</Station><Status xsi:type=\"xsd:string\">Asignado</Status><IdMaintenance xsi:type=\"xsd:string\">6305</IdMaintenance><Type xsi:type=\"xsd:string\">Preventivo</Type><IdType xsi:type=\"xsd:string\">1</IdType><Colocation xsi:type=\"xsd:string\">American Tower</Colocation><Antecedent xsi:type=\"xsd:string\">Sin antecedentes</Antecedent><Flag xsi:type=\"xsd:string\">0</Flag><SystemsPlan xsi:type=\"tns:SystemsPlan\"><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">iDEN EBTS Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de iDEN EBTS Electronics</NameActivity><IdActivity xsi:type=\"xsd:string\">1</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">3G EBTS Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de 3G EBTS Electronics</NameActivity><IdActivity xsi:type=\"xsd:string\">2</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">WiMax eBTS Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de WIMAX</NameActivity><IdActivity xsi:type=\"xsd:string\">10</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">PDH MW Radio Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventido de Pdh</NameActivity><IdActivity xsi:type=\"xsd:string\">11</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">DC POWER SYSTEM</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de DC POWER SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">4</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">AC POWER SYSTEM</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de AC POWER SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">9</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">SYSTEM GROUND</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de SYSTEM GROUND</NameActivity><IdActivity xsi:type=\"xsd:string\">5</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">STATIONARY AND PORTABLE POWER GENERATOR / FUEL SYSTEM / FUEL OPERATIONS</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de Grupo electrogeno</NameActivity><IdActivity xsi:type=\"xsd:string\">8</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">AIR CONDITIONING SYSTEM</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de AIR SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">6</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">RF &amp; TRANSPORT SITE INFRASTRUCTURE</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de TRANSPORT SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">7</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan></SystemsPlan></Maintenance><Maintenance xsi:type=\"tns:Maintenance\"><Date xsi:type=\"xsd:string\">2016-02-24 19:17:00</Date><Latitude xsi:type=\"xsd:string\">-12.1271</Latitude><Longitude xsi:type=\"xsd:string\">-77.0267</Longitude><Address xsi:type=\"xsd:string\">bulnes</Address><Station xsi:type=\"xsd:string\">Sitio_de_prueba</Station><Status xsi:type=\"xsd:string\">Asignado</Status><IdMaintenance xsi:type=\"xsd:string\">6306</IdMaintenance><Type xsi:type=\"xsd:string\">Emergencia</Type><IdType xsi:type=\"xsd:string\">3</IdType><Colocation xsi:type=\"xsd:string\">American Tower</Colocation><Antecedent xsi:type=\"xsd:string\">Y</Antecedent><Flag xsi:type=\"xsd:string\">0</Flag><SystemsPlan xsi:type=\"tns:SystemsPlan\"><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">Sistema Radiante</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">&quot;Cell Unavailable Tercer Sector(LTE) , BBU Optical Module Transmit/Receive Fault</NameActivity><IdActivity xsi:type=\"xsd:string\">158</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan></SystemsPlan></Maintenance></Information><Code xsi:type=\"xsd:string\">0</Code><Description xsi:type=\"xsd:string\">Datos consultados exitosamente.</Description><Flag xsi:type=\"xsd:string\">191</Flag><Element xsi:type=\"xsd:string\">PLANNING</Element><OperationType xsi:type=\"xsd:string\">GET</OperationType></ResponsePlan></ns1:planningMaintenanceResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>");
               Agenda agenda = XMLParser.getMaintenance(mantenimientoLocal);
               //Agenda agenda = XMLParser.getMaintenance("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Configurationwsdl\"><SOAP-ENV:Body><ns1:planningMaintenanceResponse xmlns:ns1=\"urn:Configurationwsdl\"><ResponsePlan xsi:type=\"tns:ResponsePlan\"><Information xsi:type=\"tns:Information\"><Maintenance xsi:type=\"tns:Maintenance\"><Date xsi:type=\"xsd:string\">2016-02-05 09:44:00</Date><Latitude xsi:type=\"xsd:string\">-5.63417</Latitude><Longitude xsi:type=\"xsd:string\">-78.5406</Longitude><Address xsi:type=\"xsd:string\">Carretera Acceso al Cementerio de Bagua Sector Vista Hermosa</Address><Station xsi:type=\"xsd:string\">0132114_AZ_Bagua_Ciudad</Station><Status xsi:type=\"xsd:string\">Asignado</Status><IdMaintenance xsi:type=\"xsd:string\">5082</IdMaintenance><Type xsi:type=\"xsd:string\">Faena de combustible</Type><IdType xsi:type=\"xsd:string\">5</IdType><Colocation xsi:type=\"xsd:string\">American Tower</Colocation><Antecedent xsi:type=\"xsd:string\">Sin antecedentes</Antecedent><Flag xsi:type=\"xsd:string\">0</Flag><SystemsPlan xsi:type=\"tns:SystemsPlan\"><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">FAENA DE COMBUSTIBLE / CAMBIO DE FILTRO</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar faena de combustible / cambio de filtro</NameActivity><IdActivity xsi:type=\"xsd:string\">3</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan></SystemsPlan></Maintenance><Maintenance xsi:type=\"tns:Maintenance\"><Date xsi:type=\"xsd:string\">2016-02-11 10:57:00</Date><Latitude xsi:type=\"xsd:string\">-12.1271</Latitude><Longitude xsi:type=\"xsd:string\">-77.0267</Longitude><Address xsi:type=\"xsd:string\">bulnes</Address><Station xsi:type=\"xsd:string\">Sitio_de_prueba</Station><Status xsi:type=\"xsd:string\">Asignado</Status><IdMaintenance xsi:type=\"xsd:string\">5461</IdMaintenance><Type xsi:type=\"xsd:string\">Emergencia</Type><IdType xsi:type=\"xsd:string\">3</IdType><Colocation xsi:type=\"xsd:string\">American Tower</Colocation><Antecedent xsi:type=\"xsd:string\">Sin antecedentes</Antecedent><Flag xsi:type=\"xsd:string\">0</Flag><SystemsPlan xsi:type=\"tns:SystemsPlan\"><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">Corte de energia</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Mains Input Out of Range</NameActivity><IdActivity xsi:type=\"xsd:string\">1</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan></SystemsPlan></Maintenance><Maintenance xsi:type=\"tns:Maintenance\"><Date xsi:type=\"xsd:string\">2016-02-25 14:45:00</Date><Latitude xsi:type=\"xsd:string\">-5.95144</Latitude><Longitude xsi:type=\"xsd:string\">-78.0317</Longitude><Address xsi:type=\"xsd:string\">C PITA (Comunidad de Chosgon)</Address><Station xsi:type=\"xsd:string\">0132132_AZ_Carretera_Chosgon</Station><Status xsi:type=\"xsd:string\">Asignado</Status><IdMaintenance xsi:type=\"xsd:string\">6364</IdMaintenance><Type xsi:type=\"xsd:string\">Preventivo</Type><IdType xsi:type=\"xsd:string\">1</IdType><Colocation xsi:type=\"xsd:string\">American Tower</Colocation><Antecedent xsi:type=\"xsd:string\">Sin antecedentes</Antecedent><Flag xsi:type=\"xsd:string\">0</Flag><SystemsPlan xsi:type=\"tns:SystemsPlan\"><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">iDEN EBTS Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de iDEN EBTS Electronics</NameActivity><IdActivity xsi:type=\"xsd:string\">1</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">3G EBTS Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de 3G EBTS Electronics</NameActivity><IdActivity xsi:type=\"xsd:string\">2</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">WiMax eBTS Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de WIMAX</NameActivity><IdActivity xsi:type=\"xsd:string\">10</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">PDH MW Radio Electronics</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventido de Pdh</NameActivity><IdActivity xsi:type=\"xsd:string\">11</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">Agregador</NameSystem></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">DC POWER SYSTEM</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de DC POWER SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">4</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">AC POWER SYSTEM</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de AC POWER SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">9</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">SYSTEM GROUND</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de SYSTEM GROUND</NameActivity><IdActivity xsi:type=\"xsd:string\">5</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">STATIONARY AND PORTABLE POWER GENERATOR / FUEL SYSTEM / FUEL OPERATIONS</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de Grupo electrogeno</NameActivity><IdActivity xsi:type=\"xsd:string\">8</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">AIR CONDITIONING SYSTEM</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de AIR SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">6</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan><SystemPlan xsi:type=\"tns:SystemPlan\"><NameSystem xsi:type=\"xsd:string\">RF &amp; TRANSPORT SITE INFRASTRUCTURE</NameSystem><Activities xsi:type=\"tns:Activities\"><Activity xsi:type=\"tns:Activity\"><NameActivity xsi:type=\"xsd:string\">Realizar el mantenimiento preventivo de TRANSPORT SYSTEM</NameActivity><IdActivity xsi:type=\"xsd:string\">7</IdActivity><Description xsi:nil=\"true\" xsi:type=\"xsd:string\"/></Activity></Activities></SystemPlan></SystemsPlan></Maintenance></Information><Code xsi:type=\"xsd:string\">0</Code><Description xsi:type=\"xsd:string\">Datos consultados exitosamente.</Description><Flag xsi:type=\"xsd:string\">105</Flag><Element xsi:type=\"xsd:string\">PLANNING</Element><OperationType xsi:type=\"xsd:string\">GET</OperationType></ResponsePlan></ns1:planningMaintenanceResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>");



                return agenda;
            } catch (IOException e) {
                Log.e("ELEMENTS", e.getMessage() + ": \n" + e.getCause());
                message = dummy.ERROR_CONNECTION;
            } catch (SAXException | XPathExpressionException | ParserConfigurationException e) {
                e.printStackTrace();
                message = dummy.ERROR_PARSE;
            } catch (Exception e) {
                e.printStackTrace();
                message = dummy.ERROR_GENERAL;
            }
               return null;
        }

        @Override
        protected void onPostExecute(final Agenda s) {

            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (s == null) {
                Toast.makeText(tContext, message, Toast.LENGTH_LONG).show();
                finish();
            } else {
                if (s.getCode().compareTo("0") == 0) {
                    mPagerAdapter = new PagerAdapter() {
                        @Override
                        public int getCount() {
                            return s.getMaintenanceList().size();
                        }

                        @Override
                        public CharSequence getPageTitle(int position ) {
                            /*if (position == 0) {
                                return "ASIGNADA";
                            } else {
                                return "FINALIZADA " + position;
                            }*/
                            Log.e("getPageTitleN",Integer.toString(position));
                            Log.e("getPageTitle+1",Integer.toString(position+1));

                            final cl.tdc.felipe.tdc.objects.Maintenance.Maintenance m = s.getMaintenanceList().get(position);
                            Log.e("m",s.getMaintenanceList().get(position).toString());
                            return "Mantenimiento "+ m.getIdMaintenance();
                        }

                        @Override
                        public boolean isViewFromObject(View view, Object object) {
                            return view == object;
                        }

                        @Override
                        public void destroyItem(ViewGroup container, int position, Object object) {
                            //super.destroyItem(container, position, object);
                            ((ViewPager) container).removeView((View) object);

                        }


                        @Override
                        public Object instantiateItem(ViewGroup container, int position) {
                            final cl.tdc.felipe.tdc.objects.Maintenance.Maintenance m = s.getMaintenanceList().get(position);
                            Log.e("position",Integer.toString(position));
                            Log.e("m",s.getMaintenanceList().get(position).toString());
                            final Boolean terminated;
                            MaintenanceReg registro = new MaintenanceReg(getApplicationContext());
                            registro.newMaintenance(m.getIdMaintenance(), s.getFlag());
                            /*if (m.getStatus().compareTo("TERMINATED") == 0)*/
                                terminated = true;
                            /*else
                                terminated = false;*/
                            View rootView = LayoutInflater.from(tContext).inflate(R.layout.agenda_view, null, false);
                            TextView tAddress = (TextView) rootView.findViewById(R.id.tAddress);
                            TextView tStation = (TextView) rootView.findViewById(R.id.tStation);
                            TextView tType = (TextView) rootView.findViewById(R.id.tType);

                            final ImageButton bComplete = (ImageButton) rootView.findViewById(R.id.bComplete);
                            complete = bComplete;
                            bComplete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    if(pref.getChecklistState()) {
//                                        CompletarActividad c = new CompletarActividad(tContext, Integer.parseInt(m.getIdMaintenance()));
//                                        c.execute();
//                                    }else{
//                                        Toast.makeText(actividad.getApplicationContext(), "Debe completar el Checklist de Mantenimiento antes de continuar.",Toast.LENGTH_LONG).show();
//                                    }
                                    Intent c = new Intent(actividad, ActividadCierreActivity.class);
                                    c.putExtra("MAINTENANCE", m.getIdMaintenance());

                                    startActivity(c);
                                }
                            });
                            bComplete.setEnabled(false);
                            bComplete.bringToFront();

                            //final ImageButton bMap = (ImageButton) rootView.findViewById(R.id.bMap);
                            //bMap.setVisibility(View.GONE);

                            ImageView iMap = (ImageView) rootView.findViewById(R.id.iMap);
                            LinearLayout lActivities = (LinearLayout) rootView.findViewById(R.id.lActivities);
                            final ProgressBar pProgress = (ProgressBar) rootView.findViewById(R.id.pProgress);

                            Double desplazamiento = 0.012;
                            Double desplazamientoy = 0.006;
                            String url = "http://maps.google.com/maps/api/staticmap?center=" +
                                    (Double.parseDouble(m.getLatitude()) - desplazamientoy) +
                                    "," +
                                    (Double.parseDouble(m.getLongitude()) - desplazamiento) +
                                    "&zoom=14&size=600x350&maptype=roadmap&markers=color:red|color:red|label:P|" +
                                    m.getLatitude() +
                                    "," +
                                    m.getLongitude() + "" +
                                    "&sensor=false";
                            ImageLoader.getInstance().displayImage(url, iMap);

                            if(m.getSystemList() != null) {
                                int max = Funciones.getNumActivities(m.getSystemList());
                                pProgress.setMax(max);
                                Log.d("AGENDA", "MAX" + max);
//                                int i = 0;
                                for (MainSystem system : m.getSystemList()) {

                                    TextView systemName = new TextView(tContext);
                                    systemName.setText(system.getNameSystem());
                                    systemName.setGravity(Gravity.CENTER_HORIZONTAL);

                                    systemName.setBackgroundResource(R.drawable.fondo_general_top);
                                    systemName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    lActivities.addView(systemName);

                                    LinearLayout sytemLayout = new LinearLayout(tContext);
                                    sytemLayout.setBackgroundResource(R.drawable.fondo_general_bottom);
                                    sytemLayout.setOrientation(LinearLayout.VERTICAL);
                                    sytemLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    for (final cl.tdc.felipe.tdc.objects.Maintenance.Activity a : system.getActivitieList()) {

                                        idsActivities.add(a.getIdActivity());
                                        idsActivities2.add(m.getType() + "," + a.getIdActivity() + "," + m.getIdMaintenance());//Usado para determinar que botones mostrar en ActividadCierreActivity
                                        idActivitiePeriodo = m.getPeriodo();//Usado para determinar que botones mostrar en ActividadCierreActivity
                                        idMain = m.getIdMaintenance();
                                        periodo = m.getPeriodo();


                                        crearFicherosLocal task = new crearFicherosLocal(m.getType() + "," + a.getIdActivity(),idMain); //creamos cada  archivo check localmente
                                        task.execute();


                                        View vista = LayoutInflater.from(tContext).inflate(R.layout.activity_view, null, false);
                                        ((TextView) vista.findViewById(R.id.tName)).setText(a.getNameActivity());
                                        ((TextView) vista.findViewById(R.id.tDescription)).setText(a.getDescription());
                                        CheckBox checkBox = (CheckBox) vista.findViewById(R.id.chCompleted);
                                        if (terminated) {
                                            checkBox.setEnabled(true);
                                            checkBox.setChecked(true);
                                            pProgress.setProgress(pProgress.getMax());
                                            bComplete.setEnabled(true);
                                            /*bComplete.setEnabled(false);*/
                                        }
                                        /*if (!terminated) {*/
                                            checkBox.setEnabled(true);
                                        /*}*/
////////// AQUI SE HACE EL CAMBIO DE TENERLOS TODOS CHEQUEADOS PARA QUE SOLO LE DE AL BOTON Y PASAR AL DETALLE DE MANTENIMIENTO .............
                                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                int progress = pProgress.getProgress();
                                                if (b) {
                                                    pProgress.setProgress(progress + 1);
                                                    if (!terminated)
                                                        pref.stateActivity(a, true);
                                                    Log.d("FRAGMENT", "Actividad: " + a.getNameActivity() + " estado completada");
                                                } else {
                                                    pProgress.setProgress(progress - 1);
                                                    if (!terminated)
                                                        pref.stateActivity(a, false);
                                                    Log.d("FRAGMENT", "Actividad: " + a.getNameActivity() + " estado no completada");
                                                }

                                                if (pProgress.getProgress() == pProgress.getMax()) {
                                                    bComplete.setEnabled(true);
                                                } else {
                                                    bComplete.setEnabled(false);
                                                }

                                            }
                                        });

                                        if (!terminated && pref.isCompleteActivity(a)) {
                                            checkBox.setChecked(true);
                                            if (pProgress.getProgress() == pProgress.getMax()) {
                                                bComplete.setEnabled(true);
                                            } else {
                                                bComplete.setEnabled(false);
                                            }
                                        }
                                        sytemLayout.addView(vista);
                                    }

                                    hs.addAll(idsActivities2);                          //Agregamos idsAct2 a hs (No agrega duplicados)
                                    idsActivities2.clear();                             //Limpiamos el idsActivities2
                                    idsActivities2.addAll(hs);                          //Agregamos hs a idsAct2

                                    lActivities.addView(sytemLayout);

                                }
                            }

                            tAddress.setText("Dirección: " + m.getAddress());
                            tStation.setText("Estación : " + m.getStation());
                            tType.setText(m.getType());

                            ((ViewPager) container).addView(rootView);

                            return rootView;
                        }
                    };
                    mPager.setAdapter(null);
                    mPager.setAdapter(mPagerAdapter);

                    Log.d(ATAG, s.toString());
                } else {
                    Toast.makeText(tContext, s.getDescription(), Toast.LENGTH_LONG).show();
                    AgendaActivity.this.finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FORMULARIO) {
            if (resultCode == RESULT_OK) {
                AgendaTask agenda = new AgendaTask(this);
                agenda.execute();
            }
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private String getAction(String type) {
        if (type.equals("Preventivo,1")) return SoapRequestTDC.ACTION_IDEN;
        if (type.equals("Preventivo,2")) return SoapRequestTDC.ACTION_3G;
        if (type.equals("Preventivo,9")) return SoapRequestTDC.ACTION_AC;
        if (type.equals("Preventivo,4")) return SoapRequestTDC.ACTION_DC;
        if (type.equals("Preventivo,5")) return SoapRequestTDC.ACTION_SG;
        if (type.equals("Preventivo,6")) return SoapRequestTDC.ACTION_AIR;
        if (type.equals("Faena de combustible,3")) return SoapRequestTDC.ACTION_FAENA;
        if (type.equals("Preventivo,7")) return SoapRequestTDC.ACTION_TRANSPORTE;
        if (type.equals("Preventivo,8")) return SoapRequestTDC.ACTION_GE;
        //if (type.equals("Emergencia,1")) return SoapRequestTDC.ACTION_EMERG;
        if (type.startsWith("Emergency")) return SoapRequestTDC.ACTION_EMERG;
        if (type.startsWith("Emergencia")) return SoapRequestTDC.ACTION_EMERG;
        if (type.equals("Preventivo,10")) return SoapRequestTDC.ACTION_WIMAX; //aqui
        if (type.equals("Preventivo,11")) return SoapRequestTDC.ACTION_PDH;
        if (type.equals("Preventivo,12")) return SoapRequestTDC.ACTION_AGREGADOR;
        if (type.equals("Preventivo,13")) return SoapRequestTDC.ACTION_SEMESTRAL;
        if (type.equals("Preventivo,14")) return SoapRequestTDC.ACTION_INSPECCION;
        if (type.equals("Preventivo,15")) return SoapRequestTDC.ACTION_ANUAL;

        if (type.equals("Preventivo G/E,8")) return SoapRequestTDC.ACTION_GE;
        else return "";
    }


    private class crearFicherosLocal extends AsyncTask<String, String, String> {

        String type;
        String query;
        String idMain;

        public crearFicherosLocal(String type, String idMain) {
            this.type = type;
            this.idMain = idMain;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                query = SoapRequestTDC.getFormularioCierre(IMEI, idMain, getAction(type));          //Creamos los check localmente
                LocalText localT = new LocalText();
                if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                    localT.escribirFicheroMemoriaExterna(idMain + "," + getAction(type), query);

            } catch (IOException e) {
                e.printStackTrace();
                return "Se agotó el tiempo de conexión. Por favor reintente.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }




}
