package cl.tdc.felipe.tdc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.daemon.PositionTrackerTDC;
import cl.tdc.felipe.tdc.extras.Constantes;
import cl.tdc.felipe.tdc.extras.LocalText;
import cl.tdc.felipe.tdc.objects.FormularioCierre.AREA;
import cl.tdc.felipe.tdc.objects.FormularioCierre.ITEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.PHOTO;
import cl.tdc.felipe.tdc.objects.FormularioCierre.QUESTION;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SET;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SYSTEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.VALUE;
import cl.tdc.felipe.tdc.preferences.FormCierreReg;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.SoapRequestTDC;
import cl.tdc.felipe.tdc.webservice.XMLParser;
import cl.tdc.felipe.tdc.webservice.XMLParserDownloadTDC;
import cl.tdc.felipe.tdc.webservice.XMLParserTDC;
import cl.tdc.felipe.tdc.webservice.dummy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;



public class ActividadCierreFormActivity extends Activity {
    protected PowerManager.WakeLock wakelock;

    private static int TAKE_PICTURE = 1;
    private static int TAKE_PICTURES = 2;
    private static int SELECT_FILE = 3;
    private static int PHOTO_FROM_GALLERY_KITKAT = 4;

    boolean formSended = false;

    private PositionTrackerTDC trackerTDC;
    FormCierreReg REG;
    private static String TITLE;
    String QUERY, IDMAIN, NAIR;
    TextView PAGETITLE;
    public static Activity actividad;
    Context mContext;
    LinearLayout CONTENIDO;

    public static String IMEI;

    private String name;
    private String name_orig;

    private String imgName;
    PHOTO photoTMP;
    QUESTION questionTMP;
    ITEM itemTMP;
    Button buttonTMP;
    public static ArrayList<SYSTEM> SYSTEMS;
    public static HashMap<String, ArrayList<SYSTEM>> SYSTEMSMAP = new HashMap<String, ArrayList<SYSTEM>>();

    ProgressDialog dialog;
    public LinearLayout layquest2,layquest3;
    public View question2, question3;
    public boolean agregar = false;
    public boolean agregar2 = false;
    public boolean agregar3 = false;

    Map<Integer,LinearLayout> ln = new HashMap<Integer,LinearLayout>();
    Map<Integer,View> vw = new HashMap<Integer,View>();
    Map<Integer,RadioGroup> rgm = new HashMap<Integer,RadioGroup>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public LinearLayout repeatLayout;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AVERIA", "onResume");
        Intent intent = new Intent(this, PositionTrackerTDC.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        wakelock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("AVERIA", "onPause");
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PositionTrackerTDC.MyBinder b = (PositionTrackerTDC.MyBinder) iBinder;
            trackerTDC = b.getService();
            Log.d("AVERIA", "SERVICE CONNECTED");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            trackerTDC = null;
            Log.d("AVERIA", "SERVICE DISCONNECTED");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        setContentView(R.layout.activity_cierre_actividad_form);
        actividad = this;
        mContext = this;
        CONTENIDO = (LinearLayout) this.findViewById(R.id.contenido);
        TITLE = getIntent().getStringExtra("TITULO");

        dialog = null;

        if (TITLE.equals("AIR")) {
            NAIR = getIntent().getStringExtra("NAIR");
        }

        QUERY = getIntent().getStringExtra("XML");
        IDMAIN = getIntent().getStringExtra("ID");

        REG = new FormCierreReg(mContext, TITLE + IDMAIN);

        PAGETITLE = (TextView) this.findViewById(R.id.header_actual);
        PAGETITLE.setText(TITLE);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        name = Environment.getExternalStorageDirectory() + "/TDC@/" + TITLE + "/";
        name_orig = Environment.getExternalStorageDirectory() + "/TDC@/ORIGINAL/" + TITLE + "/";
        File dir = new File(name);
        if (!dir.exists())
            if (dir.mkdirs()) {
                Log.d(TITLE, "Se creo el directorio " + name);
            } else
                Log.d(TITLE, "No se pudo crear el directorio " + name);

       File dir_orig = new File(name_orig);
        if (!dir_orig.exists())
            if (dir_orig.mkdirs()) {
                Log.d(TITLE, "Se creo el directorio " + name_orig);
            } else
                Log.d(TITLE, "No se pudo crear el directorio " + name_orig);

        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onClick_apagar(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Seguro que desea salir de TDC?");
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveData();
                if (ActividadCierreActivity.actividad != null)
                    ActividadCierreActivity.actividad.finish();
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

    //Boton volver 'atrás' del equipo.
    @Override
    public void onBackPressed() {
        //onClick_back(null);
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Desea salir del formualario sin guardar?");
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //saveData();
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
        //actividad.finish();

    }

    //Imagen guardar del Formulario
    public void onClick_back(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Desea guardar la información?");

        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveData();
                //actividad.finish();

                Toast.makeText(ActividadCierreFormActivity.this, "Datos Guardados Exitosamente", Toast.LENGTH_SHORT).show();

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

    //Imagen descargar del Formulario
    public void onClick_download(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Desea descargar la información del último checklist?");

        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // saveData();
                //actividad.finish();
                DownloadChecklistTask downloadChecklistTask = new DownloadChecklistTask(mContext);
                downloadChecklistTask.execute(IDMAIN, IMEI);
               // Toast.makeText(ActividadCierreFormActivity.this, "Datos Guardados Exitosamente", Toast.LENGTH_SHORT).show();
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

    private LinearLayout create_itemLayout() {
        LinearLayout itemLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams itemLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutParam.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
        itemLayoutParam.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
        itemLayoutParam.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        itemLayout.setBackgroundResource(R.drawable.fondo_spinner1);
        itemLayout.setLayoutParams(itemLayoutParam);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(6, 6, 6, 6);
        itemLayout.setFocusable(true);                                       //Con estas 2 lineas corregimos el problema de perder el foco al tomar una foto
        itemLayout.setFocusableInTouchMode(true);
        return itemLayout;
    }

    private LinearLayout create_questionLayout() {
        LinearLayout layquest = new LinearLayout(mContext);
        layquest.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams questionLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionLayoutParam.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
        questionLayoutParam.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        layquest.setLayoutParams(questionLayoutParam);
        layquest.setGravity(Gravity.CENTER_VERTICAL);
        return layquest;
    }

    private LinearLayout create_setLayout() {
        LinearLayout.LayoutParams questionLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionLayoutParam.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        questionLayoutParam.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        LinearLayout setLayout = new LinearLayout(mContext);
        setLayout.setLayoutParams(questionLayoutParam);
        setLayout.setOrientation(LinearLayout.VERTICAL);
        setLayout.setBackgroundResource(R.drawable.fondo_general);
        setLayout.setPadding(10, 10, 10, 10);
        return setLayout;
    }

    private ImageButton create_photoButton(final QUESTION Q) {
        ImageButton photo = new ImageButton(mContext);
        photo.setLayoutParams(new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics())));
        photo.setImageResource(R.drawable.ic_camerawhite);
        photo.setPadding(10, 10, 10, 10);
        photo.setBackgroundResource(R.drawable.button_gray_rounded);
        photo.setFocusable(true);                                       //Con estas 2 lineas corregimos el problema de perder el foco al tomar una foto
        photo.setFocusableInTouchMode(true);                            //El detalle es que debemos pulsar 2 veces el boton :-(
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionTMP = Q;

                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                final ArrayList<PHOTO> fotos = Q.getFotos();
                int n_fotos = 0;
                if (fotos != null) {
                    n_fotos = fotos.size();
                }
                b.setTitle("Actualmente tiene " + n_fotos + " fotos");
                b.setItems(new CharSequence[]{"Tomar Fotografía", "Buscar en Galería", "Ver Fotografías"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            photoTMP = new PHOTO();
                            tomarFotos();
                        } else if (i == 1) {
                            seleccionarFotoGaleria();
                        } else {
                            if (fotos != null && fotos.size() > 0)
                                verFotos();
                            else
                                Toast.makeText(mContext, "No tiene fotografías", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                b.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.show();
            }
        });

        return photo;
    }

    private ImageButton create_photoButtonItem(final ITEM I) {
        ImageButton photo = new ImageButton(mContext);
        photo.setLayoutParams(new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics())));
        photo.setImageResource(R.drawable.ic_camerawhite);
        photo.setPadding(10, 10, 10, 10);
        photo.setBackgroundResource(R.drawable.button_gray_rounded);
        photo.setFocusable(true);                                       //Con estas 2 lineas corregimos el problema de perder el foco al tomar una foto
        photo.setFocusableInTouchMode(true);                            //El detalle es que debemos pulsar 2 veces el boton :-(
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemTMP = I;

                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                final ArrayList<PHOTO> fotos = I.getFotos();
                int n_fotos = 0;
                if (fotos != null) {
                    n_fotos = fotos.size();
                }
                b.setTitle("Actualmente tiene " + n_fotos + " fotos");
                b.setItems(new CharSequence[]{"Tomar Fotografía", "Buscar en Galería", "Ver Fotografías"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            photoTMP = new PHOTO();
                            tomarFoto();
                        } else if (i == 1) {
                            seleccionarFotoGaleria();
                        } else {
                            if (fotos != null && fotos.size() > 0)
                                verFotosItem();
                            else
                                Toast.makeText(mContext, "No tiene fotografías", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                b.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.show();
            }
        });

        return photo;
    }

    private LinearLayout create_normalVerticalLayout() {
        LinearLayout l = new LinearLayout(mContext);
        l.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private void cargar_fotos(QUESTION Q, String tag) {
        int as = 0;
        ArrayList<PHOTO> fotos = new ArrayList<>();
        String name;
        while (!(name = REG.getString("PHOTONAME" + tag + as)).equals("")) {
            File tmp = new File(name);
            if (tmp.exists()) {
                PHOTO f = new PHOTO();
                f.setNamePhoto(REG.getString("PHOTONAME" + tag + as));
                f.setTitlePhoto(REG.getString("PHOTOTITLE" + tag + as));
                f.setDateTime(REG.getString("PHOTODATE" + tag + as));
                f.setCoordX(REG.getString("PHOTOCOORDX" + tag + as));
                f.setCoordY(REG.getString("PHOTOCOORDY" + tag + as));

                fotos.add(f);
            }
            as++;
        }

        if (fotos.size() > 0) Q.setFotos(fotos);
    }

    private void cargar_fotosI(ITEM I, String name, String tag) {
        int as = 0;
        ArrayList<PHOTO> fotos = new ArrayList<>();

        while (!(name = REG.getString("PHOTONAME" + tag + as)).equals("")) {
            File tmp = new File(name);
            if (tmp.exists()) {
                PHOTO f = new PHOTO();
                f.setNamePhoto(REG.getString("PHOTONAME" + tag + as));
                f.setTitlePhoto(REG.getString("PHOTOTITLE" + tag + as));
                f.setDateTime(REG.getString("PHOTODATE" + tag + as));
                f.setCoordX(REG.getString("PHOTOCOORDX" + tag + as));
                f.setCoordY(REG.getString("PHOTOCOORDY" + tag + as));

                fotos.add(f);
            }
            as++;
        }

        if (fotos.size() > 0) I.setFotos(fotos);
    }

    private Button crear_botonRepeat() {
        LinearLayout.LayoutParams botonparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        botonparam.setMargins(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),   //left
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()),   //top
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()),   //right
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics())    //bottom
        );
        Button boton = new Button(mContext);
        boton.setBackgroundResource(R.drawable.button_gray);
        boton.setLayoutParams(botonparam);
        boton.setTextColor(Color.WHITE);
        boton.setTypeface(Typeface.DEFAULT_BOLD);
        boton.setVisibility(View.GONE);
        return boton;
    }

    private QUESTION copiar_question(QUESTION Q) {
        QUESTION qAux = new QUESTION();
        qAux.setIdQuestion(Q.getIdQuestion());
        qAux.setPhoto(Q.getPhoto());
        qAux.setNumberPhoto(Q.getNumberPhoto());
        qAux.setNameType(Q.getNameType());
        qAux.setNameQuestion(Q.getNameQuestion());
        qAux.setIdType(Q.getIdType());
        qAux.setValues(Q.getValues());
        return qAux;
    }

    public void init() {
        try {
            SYSTEMS = XMLParserTDC.parseFormulario(QUERY);
            SYSTEMSMAP.put(IDMAIN+","+TITLE, SYSTEMS);

            for (final SYSTEM S : SYSTEMS) {
                /**      CABECERA SYSTEMS  **/
                CONTENIDO.addView(S.generateView(mContext));

                for (final AREA A : S.getAreas()) {
                    /**         CABECERA AREAS **/
                    CONTENIDO.addView(A.generateView(mContext));
                    if (A.getItems() == null) {
                        continue;
                    }
                    for (final ITEM I : A.getItems()) {
                        /**         CABECERA ITEMS **/
                        CONTENIDO.addView(I.getTitle(mContext));
                        final LinearLayout itemLayout = create_itemLayout();

                        View v = I.generateView(mContext);
                        if (v != null) {
                            itemLayout.addView(v);
                        }
                        if (I.getQuestions() != null) {

                            for (final QUESTION Q : I.getQuestions()) {
                                LinearLayout layquest = create_questionLayout();
                                if (Q.getPhoto().equals("OK")) { //Si requiere fotos agregamos el boton foto
                                    ImageButton photo = create_photoButton(Q);
                                    layquest.addView(photo);
                                }
                                layquest.addView(Q.getTitle(mContext));

                                View question = Q.generateView(mContext);
                                if (question != null) {
                                    agregar2 = false;
                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                    if (Q.getPhoto().equals("OK")) { //Si requiere foto|, buscamos si hay fotos gardadas
                                        cargar_fotos(Q, tag);
                                    }

                                    if (Q.getIdType().equals(Constantes.RADIO)) {
                                        int pos = REG.getInt("RADIO" + tag);
                                        if (pos != -100) {
                                            ((RadioButton) ((RadioGroup) Q.getView()).getChildAt(pos)).setChecked(true);
                                        }


                                        if (Q.getValues().size() > 0 && TITLE.equalsIgnoreCase("emergency")) {

                                            itemLayout.addView(layquest);
                                            itemLayout.addView(question);
                                            agregar2 = true;


                                            /** Generar vista de Emergencia **/
                                            final ArrayList<View> repeatContentList = new ArrayList<>();
                                            final ArrayList<Button> repeatButtontList = new ArrayList<>();

                                            final LinearLayout repeatLayout = create_normalVerticalLayout();

                                            ArrayList<QUESTION> listaAuxSet = new ArrayList<>();

                                            if (Q.getIdType().equals(Constantes.RADIO)) {

                                                for (int x = 0; x < Q.getValues().size(); x++) {
                                                    VALUE value = Q.getValues().get(x);

                                                    Button boton = crear_botonRepeat();
                                                    boton.setText(value.getNameValue());

                                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                                    for (QUESTION set : value.getQuestions()) {

                                                        QUESTION setAux = new QUESTION();
                                                        setAux.setIdQuestion(set.getIdQuestion());
                                                        setAux.setNameQuestion(set.getNameQuestion());
                                                        setAux.setNameType(set.getNameType());
                                                        setAux.setFoto(set.getFoto());
                                                        setAux.setNumberPhoto(set.getNumberPhoto());
                                                        setAux.setValues(set.getValues());
                                                        setAux.setIdType(set.getIdType());

                                                        LinearLayout setLayout = create_setLayout();

                                                        LinearLayout questTitle = create_questionLayout();

                                                        questTitle.addView(setAux.getTitle(mContext));
                                                        questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                        question2 = setAux.generateView(mContext);
                                                        if (question2 != null) {

                                                            tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdQuestion() + setAux.getNameQuestion();
                                                            Log.d("BUSCANDOEN", "Init: " + tag);
                                                            if (setAux.getIdType().equals(Constantes.CHECK)) {
                                                                ArrayList<CheckBox> ch = setAux.getCheckBoxes();
                                                                for (int j = 0; j < ch.size(); j++) {
                                                                    Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                                    ch.get(j).setChecked(check);
                                                                }
                                                            }

                                                            setLayout.addView(questTitle);
                                                            if (!setAux.getIdType().equals(Constantes.PHOTO))
                                                                setLayout.addView(question2);
                                                        }
                                                        listaAuxSet.add(setAux);

                                                        contentSetLayout.addView(setAux.getTitle(mContext));
                                                        contentSetLayout.addView(setLayout);
                                                    }
                                                    value.setQuestions(listaAuxSet);

                                                    //I.setQuestions(listaAuxSet);
                                                    //Q.setQuestions(listaAuxSet);


                                                    boton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                                for (View layu : repeatContentList) {
                                                                    if (!layu.equals(contentSetLayout)) {
                                                                        layu.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                            } else {
                                                                contentSetLayout.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });

                                                    contentSetLayout.setVisibility(View.GONE);
                                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                                    repeatButtontList.add(boton);
                                                    repeatLayout.addView(boton);
                                                    repeatLayout.addView(contentSetLayout);

                                                }

                                                RadioGroup group = (RadioGroup) Q.getView();

                                                itemLayout.addView(repeatLayout);

                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                        for (View l : repeatContentList) {
                                                            l.setVisibility(View.GONE);
                                                        }
                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                        int position = rg.indexOfChild(btn);
                                                        for (Button b : repeatButtontList) {
                                                            b.setVisibility(View.GONE);
                                                        }
                                                        //for (int i = 0; i < position; i++) {
                                                        repeatButtontList.get(position).setVisibility(View.VISIBLE);
                                                        //}

                                                    }
                                                });

                                                if (pos != -100) {
                                                    repeatButtontList.get(pos).setVisibility(View.VISIBLE);
                                                }

                                            }
                                        }

                                        if (Q.getValues().size() > 0 && TITLE.equalsIgnoreCase("wimax")) {
                                            itemLayout.addView(layquest);
                                            itemLayout.addView(question);
                                            agregar2 = true;
                                            for (VALUE V : Q.getValues()) {
                                                if (V.getQuestions() != null) {
                                                    int max2 = V.getQuestions().size();
                                                    //for (QUESTION QES : V.getQuestions()) {
                                                    for (int ikj = 0; ikj < max2; ikj ++) {
                                                        QUESTION QES = V.getQuestions().get(0);
                                                        layquest2 = create_questionLayout();
                                                        layquest2.addView(QES.getTitle(mContext));
                                                        layquest2.setVisibility(View.GONE);
                                                        question2 = QES.generateView(mContext);
                                                        question2.setVisibility(View.GONE);
                                                        if (question2 != null) {
                                                            if (QES.getIdType().equals(Constantes.RADIO)) {
                                                                if (pos != -100) {
                                                                    ((RadioButton) ((RadioGroup) QES.getView()).getChildAt(pos)).setChecked(true);
                                                                }

                                                                RadioGroup group = (RadioGroup) Q.getView();

                                                                int id;
                                                                int max = group.getChildCount();

                                                                for (int i = 0; i < max; i++){
                                                                    id = group.getChildAt(i).getId();
                                                                    ln.put(id, layquest2);
                                                                    vw.put(id, question2);
                                                                    rgm.put(id, group);
                                                                }

                                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                                    @Override
                                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                                        int position = rg.indexOfChild(btn) + 1;

                                                                        layquest2 = ln.get(id);
                                                                        question2 = vw.get(id);

                                                                        Log.d("MAP", ln.get(id) + "");
                                                                        Log.d("MAP", vw.get(id) + "");

                                                                        if (position == 2) {
                                                                            layquest2.setVisibility(View.GONE);
                                                                            question2.setVisibility(View.GONE);
                                                                        } else {
                                                                            layquest2.setVisibility(View.VISIBLE);
                                                                            question2.setVisibility(View.VISIBLE);
                                                                        }
                                                                    }
                                                                });
                                                                //agregar = true;
                                                                itemLayout.addView(layquest2);
                                                                itemLayout.addView(question2);
                                                            }
                                                        }

                                                        for (VALUE V2 : QES.getValues()) {
                                                            if (V2.getQuestions() != null) {
                                                                max2 = V2.getQuestions().size();
                                                                //for (QUESTION QES2 : V2.getQuestions()) {
                                                                for (int ikj2 = 0; ikj2 < max2; ikj2 ++) {
                                                                    QUESTION QES2 = V2.getQuestions().get(0);
                                                                    layquest3 = create_questionLayout();
                                                                    layquest3.addView(QES2.getTitle(mContext));
                                                                    layquest3.setVisibility(View.GONE);
                                                                    question3 = QES2.generateView(mContext);
                                                                    question3.setVisibility(View.GONE);

                                                                    if (question3 != null) {
                                                                        if (QES2.getIdType().equals(Constantes.RADIO)) {
                                                                            if (pos != -100) {
                                                                                ((RadioButton) ((RadioGroup) QES2.getView()).getChildAt(pos)).setChecked(true);
                                                                            }

                                                                            RadioGroup group2 = (RadioGroup) QES.getView();

                                                                            int id;
                                                                            int max = group2.getChildCount();

                                                                            for (int i = 0; i < max; i++){
                                                                                id = group2.getChildAt(i).getId();
                                                                                ln.put(id, layquest3);
                                                                                vw.put(id, question3);
                                                                                rgm.put(id, group2);
                                                                            }

                                                                            group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                                                @Override
                                                                                public void onCheckedChanged(RadioGroup rg, int id) {
                                                                                    RadioButton btn = (RadioButton) rg.findViewById(id);
                                                                                    int position = rg.indexOfChild(btn) + 1;

                                                                                    layquest3 = ln.get(id);
                                                                                    question3 = vw.get(id);

                                                                                    Log.d("MAP", ln.get(id) + "");
                                                                                    Log.d("MAP", vw.get(id) + "");

                                                                                    if (position == 2) {
                                                                                        layquest3.setVisibility(View.GONE);
                                                                                        question3.setVisibility(View.GONE);
                                                                                    } else {
                                                                                        layquest3.setVisibility(View.VISIBLE);
                                                                                        question3.setVisibility(View.VISIBLE);
                                                                                    }
                                                                                }
                                                                            });
                                                                            //agregar = true;
                                                                            itemLayout.addView(layquest3);
                                                                            itemLayout.addView(question3);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (Q.getValues().size() > 0 && TITLE.equalsIgnoreCase("transporte")) {         //Este bloque es para sacar la pregunta interna que existe en Transporte 349 y 350
                                            for (VALUE V : Q.getValues()) {                                           //Iteramos sobre las respuesta, como es radio y sabemos que al pulsar en NO muestra la otro pregunta
                                                if (V.getQuestions() != null) {                                        //evaluamos la posicion del boton y mostramos y ocultamos
                                                    for (QUESTION QV : V.getQuestions()) {
                                                        layquest2 = create_questionLayout();
                                                        layquest2.addView(QV.getTitle(mContext));
                                                        layquest2.setVisibility(View.GONE);
                                                        question2 = QV.generateView(mContext);
                                                        question2.setVisibility(View.GONE);

                                                        if (question2 != null) {

                                                            String tag2 = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" +
                                                                    QV.getIdQuestion() + "-" +
                                                                    QV.getNameQuestion();

                                                            if (QV.getIdType().equals(Constantes.DATE)) {
                                                                final EditText t2 = QV.getEditTexts().get(0);
                                                                String text = REG.getString("DATE" + tag2);
                                                                t2.setText(text);
                                                                t2.setVisibility(View.GONE);

                                                                RadioGroup group = (RadioGroup) Q.getView();
                                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                                    @Override
                                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                                        int position = rg.indexOfChild(btn) + 1;

                                                                        if (position == 2) {
                                                                            layquest2.setVisibility(View.VISIBLE);
                                                                            t2.setVisibility(View.VISIBLE);
                                                                            t2.setText("");
                                                                            question2.setVisibility(View.VISIBLE);
                                                                        } else {
                                                                            layquest2.setVisibility(View.GONE);
                                                                            t2.setVisibility(View.GONE);
                                                                            question2.setVisibility(View.GONE);
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            itemLayout.addView(layquest2);
                                                            itemLayout.addView(question2);
                                                            //agregar = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (Q.getQuestions() != null && TITLE.equalsIgnoreCase("ac")) {

                                            itemLayout.addView(layquest);
                                            itemLayout.addView(question);
                                            agregar2 = true;

                                            //Generar vista del RepeatQuestion
                                            final ArrayList<View> repeatContentList = new ArrayList<>();
                                            final ArrayList<Button> repeatButtontList = new ArrayList<>();

                                            final LinearLayout repeatLayout = create_normalVerticalLayout();
                                            ArrayList<QUESTION> listaAuxSet = new ArrayList<>();

                                            if (Q.getIdType().equals(Constantes.RADIO)) {
                                                ArrayList<VALUE> listadoV = new ArrayList<>();
                                                for (int x = 0; x < Q.getValues().size(); x++) {
                                                    VALUE value = Q.getValues().get(x);

                                                    Button boton = crear_botonRepeat();
                                                    boton.setText(value.getNameValue());

                                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                                    ArrayList<QUESTION> listadoQ = new ArrayList<>();


                                                    LinearLayout setLayout = create_setLayout();

                                                    //if (set.getQuestions() != null) {

                                                    for (final QUESTION Q2 : Q.getQuestions()) {
                                                        QUESTION qAux = copiar_question(Q2);
                                                        LinearLayout questTitle = create_questionLayout();

                                                        question2 = qAux.generateView(mContext);
                                                        if (question2 != null) {

                                                            tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion() + "-" + value.getIdValue() + value.getNameValue() + "-" + Q2.getIdQuestion() + Q2.getNameQuestion();

                                                            Log.d("BUSCANDOEN", "saveData: " + tag);

                                                            if (qAux.getPhoto().equals("OK")) {
                                                                final ImageButton photo = create_photoButton(qAux);
                                                                questTitle.addView(photo);
                                                            }

                                                            if (qAux.getPhoto().equals("OK")) {
                                                                cargar_fotos(qAux, tag);
                                                            }

                                                            questTitle.addView(qAux.getTitle(mContext));
                                                            questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                            if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                                //pos = REG.getInt("RADIO" + tag);
                                                                if (pos != -100) {
                                                                    ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                                }
                                                            }
                                                            if (qAux.getIdType().equals(Constantes.NUM)) {
                                                                String text = REG.getString("NUM" + tag);
                                                                ((TextView) qAux.getView()).setText(text);
                                                            }
                                                            if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                                String text = REG.getString("TEXT" + tag);
                                                                ((TextView) qAux.getView()).setText(text);
                                                            }

                                                            setLayout.addView(questTitle);
                                                            if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                                setLayout.addView(question2);
                                                        }
                                                        listadoQ.add(qAux);
                                                        value.setQuestions(listadoQ);
                                                    }


                                                    contentSetLayout.addView(Q.getTitle(mContext));
                                                    contentSetLayout.addView(setLayout);
                                                    //}

                                                    //listaAuxSet.add(set);
                                                    //}

                                                    boton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                                for (View layu : repeatContentList) {
                                                                    if (!layu.equals(contentSetLayout)) {
                                                                        layu.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                            } else {
                                                                contentSetLayout.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });

                                                    contentSetLayout.setVisibility(View.GONE);
                                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                                    repeatButtontList.add(boton);
                                                    repeatLayout.addView(boton);
                                                    repeatLayout.addView(contentSetLayout);

                                                    listadoV.add(value);
                                                    //Q.setValues(listadoV);
                                                    //Q.setQuestions(listadoQ);
                                                }

                                                RadioGroup group = (RadioGroup) Q.getView();

                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                        for (View l : repeatContentList) {
                                                            l.setVisibility(View.GONE);
                                                        }
                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                        int position = rg.indexOfChild(btn) + 1;
                                                        for (Button b : repeatButtontList) {
                                                            b.setVisibility(View.GONE);
                                                        }
                                                        for (int i = 0; i < position; i++) {
                                                            repeatButtontList.get(i).setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                });


                                                if (pos != -100) {                                                                  //Mostramos los botones activos
                                                    ((RadioButton) ((RadioGroup) Q.getView()).getChildAt(pos)).setChecked(true);
                                                    for (int i = 0; i < pos + 1; i++) {
                                                        repeatButtontList.get(i).setVisibility(View.VISIBLE);
                                                    }
                                                }


                                                //Q.setQuestions(listaAuxSet);
                                                //listaAuxSet.add(Q);

                                            }
                                            //I.setQuestions(listaAuxSet);
                                            itemLayout.addView(repeatLayout);
                                        }

/////////////////////////////////////////////////HASTA AQUI//////////////////////////////////////////////////////////
                                    }

                                    if (Q.getIdType().equals(Constantes.NUM)) {
                                        String text = REG.getString("NUM" + tag);
                                        ((TextView) Q.getView()).setText(text);
                                    }
                                    if (Q.getIdType().equals(Constantes.TEXT)) {
                                        String text = REG.getString("TEXT" + tag);
                                        ((TextView) Q.getView()).setText(text);
                                    }
                                    if (Q.getIdType().equals(Constantes.CHECK)) {
                                        ArrayList<CheckBox> ch = Q.getCheckBoxes();

                                        for (int j = 0; j < ch.size(); j++) {
                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                            ch.get(j).setChecked(check);
                                        }

                                    }
                                    if (Q.getIdType().equals(Constantes.DIV)) {
                                        String textL = REG.getString("DIVL" + tag);
                                        String textR = REG.getString("DIVR" + tag);

                                        EditText left = Q.getEditTexts().get(0);
                                        EditText right = Q.getEditTexts().get(1);

                                        left.setText(textL);
                                        right.setText(textR);

                                    }
                                    if (Q.getIdType().equals(Constantes.DATE)) {
                                        EditText t = Q.getEditTexts().get(0);
                                        String text = REG.getString("DATE" + tag);
                                        t.setText(text);
                                    }

                                    if (!agregar2) {

                                        itemLayout.addView(layquest);

                                        if (!Q.getIdType().equals(Constantes.PHOTO))
                                            itemLayout.addView(question);
                                    }

                                    if (agregar)
                                    {
                                        itemLayout.addView(layquest2);
                                        itemLayout.addView(question2);
                                        agregar=false;
                                    }
                                    if (agregar3) {
                                        itemLayout.addView(layquest3);
                                        itemLayout.addView(question3);
                                        agregar3 = false;
                                    }
                                    /*if (agregar2)
                                    {
                                        itemLayout.addView(repeatLayout);
                                        itemLayout.addView(question2);
                                        agregar2=false;
                                    }*/

                                }

                            }

                        }

                        if (I.getSetArrayList() != null) {
                            /** Generar vista del IteamRepeat **/
                            final ArrayList<View> repeatContentList = new ArrayList<>();
                            final ArrayList<Button> repeatButtontList = new ArrayList<>();

                            final LinearLayout repeatLayout = create_normalVerticalLayout();

                            if (I.getIdType().equals(Constantes.TABLE)) {
                                //Preparamos la tabla o lo que sea
                                for (int x = 0; x < I.getValues().size(); x++) {
                                    VALUE value = I.getValues().get(x);
                                    Button boton = crear_botonRepeat();
                                    boton.setText(value.getNameValue());

                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                    ArrayList<SET> listaAuxSet = new ArrayList<>();
                                    for (SET set : I.getSetArrayList()) {
//values a los items
                                        SET setAux = new SET();
                                        setAux.setIdSet(set.getIdSet());
                                        setAux.setNameSet(set.getNameSet());
                                        setAux.setValueSet(set.getValueSet());

                                        LinearLayout setLayout = create_setLayout();
                                        if (set.getQuestions() != null) {
                                            //si los values tienen questions
                                            ArrayList<QUESTION> listadoQ = new ArrayList<>();

                                            for (final QUESTION Q : set.getQuestions()) {
                                                final QUESTION qAux = copiar_question(Q);
                                                LinearLayout questTitle = create_questionLayout();

                                                if (qAux.getPhoto().equals("OK")) {
                                                    final ImageButton photo = create_photoButton(qAux);
                                                    questTitle.addView(photo);
                                                }
                                                questTitle.addView(qAux.getTitle(mContext));

                                                View question = qAux.generateView(mContext);

                                                if (question != null) {
                                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdSet() + setAux.getNameSet() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                                    if (qAux.getPhoto().equals("OK")) {
                                                        cargar_fotos(qAux, tag);
                                                    }

                                                    if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                        int pos = REG.getInt("RADIO" + tag);
                                                        if (pos != -100) {
                                                            ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.NUM)) {
                                                        String text = REG.getString("NUM" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                        String text = REG.getString("TEXT" + tag);
                                                        ((TextView) qAux.getView()).setText(text);

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.CHECK)) {
                                                        ArrayList<CheckBox> ch = qAux.getCheckBoxes();

                                                        for (int j = 0; j < ch.size(); j++) {
                                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                            ch.get(j).setChecked(check);
                                                        }

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DIV)) {
                                                        String textL = REG.getString("DIVL" + tag);
                                                        String textR = REG.getString("DIVR" + tag);

                                                        EditText left = qAux.getEditTexts().get(0);
                                                        EditText right = qAux.getEditTexts().get(1);

                                                        left.setText(textL);
                                                        right.setText(textR);


                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DATE)) {
                                                        EditText t = qAux.getEditTexts().get(0);
                                                        String text = REG.getString("DATE" + tag);
                                                        t.setText(text);
                                                    }


                                                    setLayout.addView(questTitle);
                                                    if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                        setLayout.addView(question);
                                                }
                                                listadoQ.add(qAux);


                                            }
                                            setAux.setQuestions(listadoQ);
                                            contentSetLayout.addView(setAux.getTitle(mContext));
                                            contentSetLayout.addView(setLayout);
                                        }


                                        listaAuxSet.add(setAux);
                                    }
                                    I.addListSet(listaAuxSet);


                                    boton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                for (View layu : repeatContentList) {
                                                    if (!layu.equals(contentSetLayout)) {
                                                        layu.setVisibility(View.GONE);
                                                    }
                                                }
                                            } else {
                                                contentSetLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    contentSetLayout.setVisibility(View.GONE);
                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                    repeatButtontList.add(boton);       //para que al mostrar uno se oculten los demas
                                    repeatLayout.addView(boton);
                                    repeatLayout.addView(contentSetLayout);

                                }
                                itemLayout.addView(repeatLayout);

                                for (final CheckBox c : I.getCheckBoxes()) {
                                    final int pos = I.getCheckBoxes().indexOf(c);
                                    c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            repeatContentList.get(pos).setVisibility(View.GONE);
                                            if (b) {
                                                repeatButtontList.get(pos).setVisibility(View.VISIBLE);
                                            } else {
                                                repeatButtontList.get(pos).setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    boolean check = REG.getBoolean("TABLE" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + pos);
                                    c.setChecked(check);
                                }

                            }

                            if (I.getIdType().equals(Constantes.RADIO)) {
                                for (int x = 0; x < I.getValues().size(); x++) {
                                    VALUE value = I.getValues().get(x);

                                    Button boton = crear_botonRepeat();
                                    boton.setText(value.getNameValue());

                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                    ArrayList<SET> listaAuxSet = new ArrayList<>();
                                    for (SET set : I.getSetArrayList()) {

                                        SET setAux = new SET();
                                        setAux.setIdSet(set.getIdSet());
                                        setAux.setNameSet(set.getNameSet());
                                        setAux.setValueSet(set.getValueSet());
                                        LinearLayout setLayout = create_setLayout();

                                        if (set.getQuestions() != null) {
                                            ArrayList<QUESTION> listadoQ = new ArrayList<>();
//pasa aqui
                                            for (final QUESTION Q : set.getQuestions()) {
                                                QUESTION qAux = copiar_question(Q);
                                                LinearLayout questTitle = create_questionLayout();

                                                if (qAux.getPhoto().equals("OK")) {
                                                    final ImageButton photo = create_photoButton(qAux);
                                                    questTitle.addView(photo);
                                                }

                                                questTitle.addView(qAux.getTitle(mContext));
                                                questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                View question = qAux.generateView(mContext);
                                                if (question != null) {
                                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdSet() + setAux.getNameSet() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                                    if (qAux.getPhoto().equals("OK")) {
                                                        cargar_fotos(qAux, tag);
                                                    }

                                                    if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                        int pos = REG.getInt("RADIO" + tag);
                                                        if (pos != -100) {
                                                            ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.NUM)) {
                                                        String text = REG.getString("NUM" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                        String text = REG.getString("TEXT" + tag);
                                                        ((TextView) qAux.getView()).setText(text);

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.CHECK)) {
                                                        ArrayList<CheckBox> ch = qAux.getCheckBoxes();
                                                        for (int j = 0; j < ch.size(); j++) {
                                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                            ch.get(j).setChecked(check);
                                                        }

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DIV)) {
                                                        String textL = REG.getString("DIVL" + tag);
                                                        String textR = REG.getString("DIVR" + tag);

                                                        EditText left = qAux.getEditTexts().get(0);
                                                        EditText right = qAux.getEditTexts().get(1);

                                                        left.setText(textL);
                                                        right.setText(textR);


                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DATE)) {
                                                        EditText t = qAux.getEditTexts().get(0);
                                                        String text = REG.getString("DATE" + tag);
                                                        t.setText(text);
                                                    }

                                                    setLayout.addView(questTitle);
                                                    if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                        setLayout.addView(question);
                                                }
                                                listadoQ.add(qAux);


                                            }
                                            setAux.setQuestions(listadoQ);
                                            contentSetLayout.addView(setAux.getTitle(mContext));
                                            contentSetLayout.addView(setLayout);
                                        }

                                        listaAuxSet.add(setAux);
                                    }
                                    I.addListSet(listaAuxSet);


                                    boton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                for (View layu : repeatContentList) {
                                                    if (!layu.equals(contentSetLayout)) {
                                                        layu.setVisibility(View.GONE);
                                                    }
                                                }
                                            } else {
                                                contentSetLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    contentSetLayout.setVisibility(View.GONE);
                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                    repeatButtontList.add(boton);
                                    repeatLayout.addView(boton);
                                    repeatLayout.addView(contentSetLayout);

                                }

                                RadioGroup group = (RadioGroup) I.getView();

                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                        for (View l : repeatContentList) {
                                            l.setVisibility(View.GONE);
                                        }
                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                        int position = rg.indexOfChild(btn) + 1;
                                        for (Button b : repeatButtontList) {
                                            b.setVisibility(View.GONE);
                                        }
                                        for (int i = 0; i < position; i++) {
                                            repeatButtontList.get(i).setVisibility(View.VISIBLE);
                                        }

                                    }
                                });

                                int pos = REG.getInt("RADIO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                Log.d("POSITION", "RADIO" + I.getIdItem() + " -pos: " + pos);
                                if (pos != -100) {
                                    ((RadioButton) ((RadioGroup) I.getView()).getChildAt(pos)).setChecked(true);
                                }
                                itemLayout.addView(repeatLayout);
                            }


                            if (I.getIdType().equals(Constantes.ADD)) {
                                ((TextView) I.getView()).setText("Aires Acondicionados: " + NAIR);

                                int n = Integer.parseInt(NAIR);
                                for (int x = 0; x < n; x++) {
                                    Button boton = crear_botonRepeat();
                                    boton.setVisibility(View.VISIBLE);
                                    boton.setText("Aire Acondicionado " + (x + 1));

                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                    ArrayList<SET> listaAuxSet = new ArrayList<>();
                                    for (SET set : I.getSetArrayList()) {
                                        SET setAux = new SET();
                                        setAux.setIdSet(set.getIdSet());
                                        setAux.setNameSet(set.getNameSet());
                                        setAux.setQuestions(set.getQuestions());

                                        LinearLayout setLayout = create_setLayout();

                                        if (setAux.getQuestions() != null) {
                                            ArrayList<QUESTION> listadoQ = new ArrayList<>();

                                            for (final QUESTION Q : setAux.getQuestions()) {
                                                QUESTION qAux = copiar_question(Q);
                                                LinearLayout questTitle = create_questionLayout();

                                                if (qAux.getPhoto().equals("OK")) {
                                                    final ImageButton photo = create_photoButton(qAux);
                                                    questTitle.addView(photo);
                                                }

                                                questTitle.addView(qAux.getTitle(mContext));
                                                questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                View question = qAux.generateView(mContext);
                                                if (question != null) {
                                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + "AIR" + x + "-" + setAux.getIdSet() + setAux.getNameSet() + "-" + qAux.getIdQuestion() + "-" + qAux.getNameQuestion();

                                                    if (qAux.getPhoto().equals("OK")) {
                                                        cargar_fotos(qAux, tag);
                                                    }

                                                    if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                        int pos = REG.getInt("RADIO" + tag);
                                                        if (pos != -100) {
                                                            ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.NUM)) {
                                                        String text = REG.getString("NUM" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                        String text = REG.getString("TEXT" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.CHECK)) {
                                                        ArrayList<CheckBox> ch = qAux.getCheckBoxes();
                                                        for (int j = 0; j < ch.size(); j++) {
                                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                            ch.get(j).setChecked(check);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DIV)) {
                                                        String textL = REG.getString("DIVL" + tag);
                                                        String textR = REG.getString("DIVR" + tag);

                                                        EditText left = qAux.getEditTexts().get(0);
                                                        EditText right = qAux.getEditTexts().get(1);

                                                        left.setText(textL);
                                                        right.setText(textR);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DATE)) {
                                                        EditText t = qAux.getEditTexts().get(0);
                                                        String text = REG.getString("DATE" + tag);
                                                        t.setText(text);
                                                    }
                                                    setLayout.addView(questTitle);
                                                    if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                        setLayout.addView(question);
                                                }
                                                listadoQ.add(qAux);
                                            }
                                            setAux.setQuestions(listadoQ);
                                            contentSetLayout.addView(setAux.getTitle(mContext));
                                            contentSetLayout.addView(setLayout);
                                        }
                                        listaAuxSet.add(setAux);
                                    }
                                    I.addListSet(listaAuxSet);

                                    boton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                for (View layu : repeatContentList) {
                                                    if (!layu.equals(contentSetLayout)) {
                                                        layu.setVisibility(View.GONE);
                                                    }
                                                }
                                            } else {
                                                contentSetLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    contentSetLayout.setVisibility(View.GONE);
                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                    repeatButtontList.add(boton);

                                    repeatLayout.addView(boton);
                                    repeatLayout.addView(contentSetLayout);
                                }
                                itemLayout.addView(repeatLayout);
                            }
                        } else {
                            if (I.getIdType().equals(Constantes.RADIO)) {
                                int pos = REG.getInt("RADIO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                Log.d("POSITION", "RADIO" + I.getIdItem() + " -pos: " + pos);
                                if (pos != -100) {
                                    ((RadioButton) ((RadioGroup) I.getView()).getChildAt(pos)).setChecked(true);
                                }
                            }
                            if (I.getIdType().equals(Constantes.CHECK)) {
                                for (CheckBox c : I.getCheckBoxes()) {
                                    boolean check = REG.getBoolean("CHECK" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + I.getCheckBoxes().indexOf(c));
                                    c.setChecked(check);
                                }
                            }
                            if (I.getIdType().equals(Constantes.CHECK_PHOTO)) {
                                for (CheckBox c : I.getCheckBoxes()) {
                                    boolean check = REG.getBoolean("CHECK-PHOTO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + I.getCheckBoxes().indexOf(c));
                                    c.setChecked(check);
                                }
                            }
                            if (I.getIdType().equals(Constantes.HOUR)) {
                                String hora = REG.getString("HOUR" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                I.getEditText().setText(hora);
                            }
                            if (I.getIdType().equals(Constantes.PHOTO)) {
                                String tagI = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem();

                                ImageButton photo = create_photoButtonItem(I);
                                itemLayout.addView(photo);
                                String name = REG.getString("PHOTONAME" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                cargar_fotosI(I,name,tagI);
                            }
                            if (I.getIdType().equals(Constantes.TEXT)) {
                                String text = REG.getString("TEXT" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                ((EditText) I.getView()).setText(text);
                            }
                            if (I.getIdType().equals(Constantes.NUM)) {
                                String text = REG.getString("NUM" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                ((EditText) I.getView()).setText(text);
                            }
                        }
                        CONTENIDO.addView(itemLayout);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException |
                IOException e
                )

        {
            e.printStackTrace();
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Error en XML");
            b.setIcon(android.R.drawable.ic_dialog_alert);
            b.setMessage("No se pudo generar el formulario");

            b.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    actividad.finish();
                }
            });
            b.show();

        }
    }

    private void saveData() {
        for (SYSTEM S : SYSTEMS) {
            for (AREA A : S.getAreas()) {
                if (A.getItems() == null) continue;
                for (ITEM I : A.getItems()) {
                    String preId = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem();

                    if (I.getQuestions() != null) {
                        for (QUESTION Q : I.getQuestions()) {
                            String tagid = preId + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                            if (Q.getPhoto().equals("OK")) {
                                ArrayList<PHOTO> fotos = Q.getFotos();
                                if (fotos != null) {
                                    for (int as = 0; as < fotos.size(); as++) {
                                        PHOTO f = fotos.get(as);
                                        REG.addValue("PHOTONAME" + tagid + as, f.getNamePhoto());
                                        REG.addValue("PHOTOTITLE" + tagid + as, f.getTitlePhoto());
                                        REG.addValue("PHOTODATE" + tagid + as, f.getDateTime());
                                        REG.addValue("PHOTOCOORDX" + tagid + as, f.getCoordX());
                                        REG.addValue("PHOTOCOORDY" + tagid + as, f.getCoordY());
                                    }
                                }
                            }

                            if (Q.getIdType().equals(Constantes.RADIO)) {
                                int id = ((RadioGroup) Q.getView()).getCheckedRadioButtonId();
                                if (id != -1) {
                                    RadioButton b = (RadioButton) Q.getView().findViewById(id);
                                    int pos = ((RadioGroup) Q.getView()).indexOfChild(b);
                                    int n = pos + 1;
                                    REG.addValue("RADIO" + tagid, pos);

                                    if (Q.getValues() != null && TITLE.equalsIgnoreCase("emergency")) { // Cambios para Energia Desde Aqui

                                        for (VALUE value : Q.getValues()) {
                                            if (value.getQuestions() != null) {
                                                for (QUESTION setAux : value.getQuestions()) {
                                                    tagid = preId + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdQuestion() + setAux.getNameQuestion();
                                                    Log.d("GUARDADOEN", "saveData: " + tagid);
                                                    if (setAux.getIdType().equals(Constantes.CHECK)) {
                                                        ArrayList<CheckBox> ch = setAux.getCheckBoxes();
                                                        for (int j = 0; j < ch.size(); j++) {
                                                            if (ch.get(j).isChecked()) {
                                                                REG.addValue("CHECK" + tagid + j, true);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (Q.getValues() != null && TITLE.equalsIgnoreCase("ac")) { // Cambios para ac Desde Aqui

                                        for (VALUE value : Q.getValues()) {
                                            if (value.getQuestions() != null) {

                                                for (QUESTION setAux : value.getQuestions()) {
                                                    tagid = preId + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdQuestion() + setAux.getNameQuestion();
                                                    Log.d("GUARDADOEN", "saveData: " + tagid);

                                                    if (setAux.getIdType().equals(Constantes.NUM)) {
                                                        String text = ((TextView) setAux.getView()).getText().toString();
                                                        if (text.length() > 0) {
                                                            REG.addValue("NUM" + tagid, text);
                                                        }
                                                    }
                                                    if (setAux.getIdType().equals(Constantes.TEXT)) {
                                                        String text = ((TextView) setAux.getView()).getText().toString();
                                                        Log.d("GUARDANDO", text);
                                                        if (text.length() > 0) {
                                                            REG.addValue("TEXT" + tagid, text);
                                                        }
                                                    }

                                                    if (setAux.getIdType().equals(Constantes.RADIO)) {
                                                        id = ((RadioGroup) setAux.getView()).getCheckedRadioButtonId();
                                                        if (id != -1) {
                                                            b = (RadioButton) setAux.getView().findViewById(id);
                                                            pos = ((RadioGroup) setAux.getView()).indexOfChild(b);
                                                            REG.addValue("RADIO" + tagid, pos);
                                                            Log.d("GUARDANDO", "Seleccionado-> " + pos);
                                                        } else {
                                                            Log.d("GUARDANDO", "nada seleccionado");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (Q.getIdType().equals(Constantes.NUM)) {
                                String text = ((TextView) Q.getView()).getText().toString();
                                if (text.length() > 0) {
                                    REG.addValue("NUM" + tagid, text);
                                }
                            }
                            if (Q.getIdType().equals(Constantes.TEXT)) {
                                String text = ((TextView) Q.getView()).getText().toString();
                                Log.d("GUARDANDO", text);
                                if (text.length() > 0) {
                                    REG.addValue("TEXT" + tagid, text);
                                }
                            }
                            if (Q.getIdType().equals(Constantes.DIV)) {
                                EditText left = Q.getEditTexts().get(0);
                                EditText right = Q.getEditTexts().get(1);
                                String textL = left.getText().toString();
                                String textR = right.getText().toString();
                                Log.d("GUARDANDO", textL);
                                Log.d("GUARDANDO", textR);
                                if (textL.length() > 0) {
                                    REG.addValue("DIVL" + tagid, textL);
                                }
                                if (textR.length() > 0) {
                                    REG.addValue("DIVR" + tagid, textR);
                                }
                            }
                            if (Q.getIdType().equals(Constantes.DATE)) {
                                EditText t = Q.getEditTexts().get(0);
                                String text = t.getText().toString();
                                Log.d("GUARDANDO", text);
                                if (text.length() > 0) {
                                    REG.addValue("DATE" + tagid, text);
                                }
                            }

                            if (Q.getIdType().equals(Constantes.CHECK)) {
                                ArrayList<CheckBox> ch = Q.getCheckBoxes();
                                for (int j = 0; j < ch.size(); j++) {
                                    if (ch.get(j).isChecked()) {
                                        REG.addValue("CHECK" + tagid + j, true);
                                    } else {
                                        REG.addValue("CHECK" + tagid + j, false);
                                    }
                                }
                            }
                        }
                    }

                    if (I.getIdType().equals(Constantes.CHECK)) {
                        for (int i = 0; i < I.getValues().size(); i++) {
                            CheckBox c = I.getCheckBoxes().get(i);
                            REG.addValue("CHECK" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + i, c.isChecked()); //GUARDAMOS LA SELECCION DE SECTORES
                        }
                    }
                    if (I.getIdType().equals(Constantes.CHECK_PHOTO)) {
                        for (int i = 0; i < I.getValues().size(); i++) {
                            CheckBox c = I.getCheckBoxes().get(i);
                            REG.addValue("CHECK-PHOTO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + i, c.isChecked()); //GUARDAMOS LA SELECCION DE SECTORES
                        }
                    }

                    if (I.getIdType().equals(Constantes.HOUR)) {
                        REG.addValue("HOUR" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), I.getEditText().getText().toString());
                    }

                    //modifique aqui
                    if (I.getIdType().equals(Constantes.PHOTO)) {
                        ArrayList<PHOTO> fotos = I.getFotos();

                        if (fotos != null) {
                            for (int as = 0; as < fotos.size(); as++) {
                                PHOTO f = fotos.get(as);
                                Log.d("GUARDANDO", "foto: " + f.getTitlePhoto());
                                //String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();
                                //String preId = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem();
                                // REG.addValue("PHOTONAME" + preId + as, f.getNamePhoto());
                                REG.addValue("PHOTONAME" + preId + as, f.getNamePhoto());
                                REG.addValue("PHOTOTITLE" + preId + as, f.getTitlePhoto());
                                REG.addValue("PHOTODATE" + preId + as, f.getDateTime());
                                REG.addValue("PHOTOCOORDX" + preId + as,f.getCoordX());
                                REG.addValue("PHOTOCOORDY" + preId + as, f.getCoordY());
                            }
                        }

                    }
                    if (I.getIdType().equals(Constantes.TEXT)) {
                        REG.addValue("TEXT" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), ((EditText) I.getView()).getText().toString());
                    }
                    if (I.getIdType().equals(Constantes.NUM)) {
                        REG.addValue("NUM" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), ((EditText) I.getView()).getText().toString());
                    }

                    if (I.getIdType().equals(Constantes.TABLE)) {
                        for (int i = 0; i < I.getValues().size(); i++) {
                            CheckBox c = I.getCheckBoxes().get(i);

                            REG.addValue("TABLE" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + i, c.isChecked()); //GUARDAMOS LA SELECCION DE SECTORES
                            ArrayList<SET> list_i = I.getSetlistArrayList().get(i);
                            VALUE valor = I.getValues().get(i);
                            for (SET SeT : list_i) {
                                for (QUESTION Q : SeT.getQuestions()) {

                                    String tagid = preId + "-" + valor.getIdValue() + valor.getNameValue() + "-" + SeT.getIdSet() + SeT.getNameSet() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                    if (Q.getPhoto().equals("OK")) {
                                        ArrayList<PHOTO> fotos = Q.getFotos();
                                        if (fotos != null) {
                                            for (int as = 0; as < fotos.size(); as++) {
                                                PHOTO f = fotos.get(as);
                                                Log.d("GUARDANDO", "foto: " + f.getTitlePhoto());
                                                REG.addValue("PHOTONAME" + tagid + as, f.getNamePhoto());
                                                REG.addValue("PHOTOTITLE" + tagid + as, f.getTitlePhoto());
                                                REG.addValue("PHOTODATE" + tagid + as, f.getDateTime());
                                                REG.addValue("PHOTOCOORDX" + tagid + as, f.getCoordX());
                                                REG.addValue("PHOTOCOORDY" + tagid + as, f.getCoordY());
                                                //REG.addValue("PHOTOBMP" + tagid + as, Funciones.encodeTobase64(f.getBitmap()));
                                            }
                                        }
                                    }

                                    if (Q.getIdType().equals(Constantes.RADIO)) {
                                        int id = ((RadioGroup) Q.getView()).getCheckedRadioButtonId();
                                        if (id != -1) {
                                            RadioButton b = (RadioButton) Q.getView().findViewById(id);
                                            int pos = ((RadioGroup) Q.getView()).indexOfChild(b);
                                            REG.addValue("RADIO" + tagid, pos);
                                            Log.d("GUARDANDO", "Seleccionado-> " + pos);
                                        } else {
                                            Log.d("GUARDANDO", "nada seleccionado");
                                        }
                                    }
                                    if (Q.getIdType().equals(Constantes.PHOTO)) {
                                        PHOTO p = Q.getFoto();
                                        if (p != null) {
                                            Log.d("GUARDANDO", "foto: " + p.getTitlePhoto());
                                            REG.addValue("PHOTONAME" + tagid, p.getNamePhoto());
                                            REG.addValue("PHOTOTITLE" + tagid, p.getTitlePhoto());
                                            REG.addValue("PHOTODATE" + tagid, p.getDateTime());
                                            REG.addValue("PHOTOCOORDX" + tagid, p.getCoordX());
                                            REG.addValue("PHOTOCOORDY" + tagid, p.getCoordY());
                                            //REG.addValue("PHOTOBMP" + tagid, Funciones.encodeTobase64(p.getBitmap()));
                                        }

                                    }
                                    if (Q.getIdType().equals(Constantes.NUM)) {
                                        String text = ((TextView) Q.getView()).getText().toString();
                                        Log.d("GUARDANDO", text);
                                        if (text.length() > 0) {
                                            REG.addValue("NUM" + tagid, text);
                                        }
                                    }
                                    if (Q.getIdType().equals(Constantes.TEXT)) {
                                        String text = ((TextView) Q.getView()).getText().toString();
                                        Log.d("GUARDANDO", text);
                                        if (text.length() > 0) {
                                            REG.addValue("TEXT" + tagid, text);
                                        }
                                    }
                                    if (Q.getIdType().equals(Constantes.DIV)) {
                                        EditText left = Q.getEditTexts().get(0);
                                        EditText right = Q.getEditTexts().get(1);
                                        String textL = left.getText().toString();
                                        String textR = right.getText().toString();
                                        Log.d("GUARDANDO", textL);
                                        Log.d("GUARDANDO", textR);
                                        if (textL.length() > 0) {
                                            REG.addValue("DIVL" + tagid, textL);
                                        }
                                        if (textR.length() > 0) {
                                            REG.addValue("DIVR" + tagid, textR);
                                        }
                                    }
                                    if (Q.getIdType().equals(Constantes.DATE)) {
                                        EditText t = Q.getEditTexts().get(0);
                                        String text = t.getText().toString();
                                        Log.d("GUARDANDO", text);
                                        if (text.length() > 0) {
                                            REG.addValue("DATE" + tagid, text);
                                        }
                                    }
                                    if (Q.getIdType().equals(Constantes.CHECK)) {
                                        ArrayList<CheckBox> ch = Q.getCheckBoxes();

                                        for (int j = 0; j < ch.size(); j++) {
                                            if (ch.get(j).isChecked()) {
                                                REG.addValue("CHECK" + tagid + j, true);
                                            } else {
                                                REG.addValue("CHECK" + tagid + j, false);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (I.getIdType().equals(Constantes.ADD)) {
                        if (I.getSetArrayList() != null) {
                            for (int i = 0; i < Integer.parseInt(NAIR); i++) {

                                ArrayList<SET> list_i = I.getSetlistArrayList().get(i);
                                for (SET SeT : list_i) {
                                    for (QUESTION Q : SeT.getQuestions()) {
                                        String tagid = preId + "-" + "AIR" + i + "-" + SeT.getIdSet() + SeT.getNameSet() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                        if (Q.getPhoto().equals("OK")) {
                                            ArrayList<PHOTO> fotos = Q.getFotos();
                                            if (fotos != null) {
                                                for (int as = 0; as < fotos.size(); as++) {
                                                    PHOTO f = fotos.get(as);
                                                    Log.d("GUARDANDO", "foto: " + f.getTitlePhoto());
                                                    REG.addValue("PHOTONAME" + tagid + as, f.getNamePhoto());
                                                    REG.addValue("PHOTOTITLE" + tagid + as, f.getTitlePhoto());
                                                    REG.addValue("PHOTODATE" + tagid + as, f.getDateTime());
                                                    REG.addValue("PHOTOCOORDX" + tagid + as, f.getCoordX());
                                                    REG.addValue("PHOTOCOORDY" + tagid + as, f.getCoordY());
                                                    //REG.addValue("PHOTOBMP" + tagid + as, Funciones.encodeTobase64(f.getBitmap()));


                                                }
                                            }
                                        }

                                        if (Q.getIdType().equals(Constantes.RADIO)) {
                                            int id = ((RadioGroup) Q.getView()).getCheckedRadioButtonId();
                                            if (id != -1) {
                                                RadioButton b = (RadioButton) Q.getView().findViewById(id);
                                                int pos = ((RadioGroup) Q.getView()).indexOfChild(b);
                                                REG.addValue("RADIO" + tagid, pos);
                                                Log.d("GUARDANDO", "Seleccionado-> " + pos);
                                            } else {
                                                Log.d("GUARDANDO", "nada seleccionado");
                                            }
                                        }
                                        if (Q.getIdType().equals(Constantes.PHOTO)) {
                                            PHOTO p = Q.getFoto();
                                            if (p != null) {
                                                Log.d("GUARDANDO", "foto: " + p.getTitlePhoto());
                                                REG.addValue("PHOTONAME" + tagid, p.getNamePhoto());
                                                REG.addValue("PHOTOTITLE" + tagid, p.getTitlePhoto());
                                                REG.addValue("PHOTODATE" + tagid, p.getDateTime());
                                                REG.addValue("PHOTOCOORDX" + tagid, p.getCoordX());
                                                REG.addValue("PHOTOCOORDY" + tagid, p.getCoordY());
                                                //REG.addValue("PHOTOBMP" + tagid, Funciones.encodeTobase64(p.getBitmap()));
                                            }

                                        }
                                        if (Q.getIdType().equals(Constantes.NUM)) {
                                            String text = ((TextView) Q.getView()).getText().toString();
                                            Log.d("GUARDANDO", text);
                                            if (text.length() > 0) {
                                                REG.addValue("NUM" + tagid, text);
                                            }
                                        }
                                        if (Q.getIdType().equals(Constantes.TEXT)) {
                                            String text = ((TextView) Q.getView()).getText().toString();
                                            Log.d("GUARDANDO", text);
                                            if (text.length() > 0) {
                                                REG.addValue("TEXT" + tagid, text);
                                            }
                                        }
                                        if (Q.getIdType().equals(Constantes.DIV)) {
                                            EditText left = Q.getEditTexts().get(0);
                                            EditText right = Q.getEditTexts().get(1);
                                            String textL = left.getText().toString();
                                            String textR = right.getText().toString();
                                            Log.d("GUARDANDO", textL);
                                            Log.d("GUARDANDO", textR);
                                            if (textL.length() > 0) {
                                                REG.addValue("DIVL" + tagid, textL);
                                            }
                                            if (textR.length() > 0) {
                                                REG.addValue("DIVR" + tagid, textR);
                                            }
                                        }
                                        if (Q.getIdType().equals(Constantes.DATE)) {
                                            EditText t = Q.getEditTexts().get(0);
                                            String text = t.getText().toString();
                                            Log.d("GUARDANDO", text);
                                            if (text.length() > 0) {
                                                REG.addValue("DATE" + tagid, text);
                                            }
                                        }
                                        if (Q.getIdType().equals(Constantes.CHECK)) {
                                            ArrayList<CheckBox> ch = Q.getCheckBoxes();
                                            for (int j = 0; j < ch.size(); j++) {
                                                if (ch.get(j).isChecked()) {
                                                    REG.addValue("CHECK" + tagid + j, true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

//aqui
                    if (I.getIdType().equals(Constantes.RADIO)) {
                        ArrayList<VALUE> values = I.getValues();
                        RadioGroup radioGroup = (RadioGroup) I.getView();
                        if (radioGroup.getCheckedRadioButtonId() != -1) {

                            RadioButton btn = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

                            int position = radioGroup.indexOfChild(btn);
                            int n = position + 1;

                            REG.addValue("RADIO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), position); //GUARDAMOS LA SELECCION DE SECTORES

                            if (I.getSetArrayList() != null) {
                                for (int i = 0; i < n; i++) {

                                    ArrayList<SET> list_i = I.getSetlistArrayList().get(i);
                                    VALUE valor = values.get(i);
                                    for (SET SeT : list_i) {
                                        for (QUESTION Q : SeT.getQuestions()) {
                                            String tagid = preId + "-" + valor.getIdValue() + valor.getNameValue() + "-" + SeT.getIdSet() + SeT.getNameSet() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                            if (Q.getPhoto().equals("OK")) {
                                                ArrayList<PHOTO> fotos = Q.getFotos();
                                                if (fotos != null) {
                                                    for (int as = 0; as < fotos.size(); as++) {
                                                        PHOTO f = fotos.get(as);
                                                        Log.d("GUARDANDO", "foto: " + f.getTitlePhoto());
                                                        REG.addValue("PHOTONAME" + tagid + as, f.getNamePhoto());
                                                        REG.addValue("PHOTOTITLE" + tagid + as, f.getTitlePhoto());
                                                        REG.addValue("PHOTODATE" + tagid + as, f.getDateTime());
                                                        REG.addValue("PHOTOCOORDX" + tagid + as, f.getCoordX());
                                                        REG.addValue("PHOTOCOORDY" + tagid + as, f.getCoordY());
                                                        //REG.addValue("PHOTOBMP" + tagid + as, Funciones.encodeTobase64(f.getBitmap()));

                                                    }
                                                }
                                            }

                                            if (Q.getIdType().equals(Constantes.RADIO)) {
                                                int id = ((RadioGroup) Q.getView()).getCheckedRadioButtonId();
                                                if (id != -1) {
                                                    RadioButton b = (RadioButton) Q.getView().findViewById(id);
                                                    int pos = ((RadioGroup) Q.getView()).indexOfChild(b);
                                                    REG.addValue("RADIO" + tagid, pos);
                                                    Log.d("GUARDANDO", "Seleccionado-> " + pos);
                                                } else {
                                                    Log.d("GUARDANDO", "nada seleccionado");
                                                }
                                            }
                                            if (Q.getIdType().equals(Constantes.PHOTO)) {
                                                PHOTO p = Q.getFoto();
                                                if (p != null) {
                                                    Log.d("GUARDANDO", "foto: " + p.getTitlePhoto());
                                                    REG.addValue("PHOTONAME" + tagid, p.getNamePhoto());
                                                    REG.addValue("PHOTOTITLE" + tagid, p.getTitlePhoto());
                                                    REG.addValue("PHOTODATE" + tagid, p.getDateTime());
                                                    REG.addValue("PHOTOCOORDX" + tagid, p.getCoordX());
                                                    REG.addValue("PHOTOCOORDY" + tagid, p.getCoordY());
                                                    //REG.addValue("PHOTOBMP" + tagid, Funciones.encodeTobase64(p.getBitmap()));
                                                }

                                            }
                                            if (Q.getIdType().equals(Constantes.NUM)) {
                                                String text = ((TextView) Q.getView()).getText().toString();
                                                Log.d("GUARDANDO", text);
                                                if (text.length() > 0) {
                                                    REG.addValue("NUM" + tagid, text);
                                                }
                                            }
                                            if (Q.getIdType().equals(Constantes.TEXT)) {
                                                String text = ((TextView) Q.getView()).getText().toString();
                                                Log.d("GUARDANDO", text);
                                                if (text.length() > 0) {
                                                    REG.addValue("TEXT" + tagid, text);
                                                }
                                            }
                                            if (Q.getIdType().equals(Constantes.DIV)) {
                                                EditText left = Q.getEditTexts().get(0);
                                                EditText right = Q.getEditTexts().get(1);
                                                String textL = left.getText().toString();
                                                String textR = right.getText().toString();
                                                Log.d("GUARDANDO", textL);
                                                Log.d("GUARDANDO", textR);
                                                if (textL.length() > 0) {
                                                    REG.addValue("DIVL" + tagid, textL);
                                                }
                                                if (textR.length() > 0) {
                                                    REG.addValue("DIVR" + tagid, textR);
                                                }
                                            }
                                            if (Q.getIdType().equals(Constantes.DATE)) {
                                                EditText t = Q.getEditTexts().get(0);
                                                String text = t.getText().toString();
                                                Log.d("GUARDANDO", text);
                                                if (text.length() > 0) {
                                                    REG.addValue("DATE" + tagid, text);
                                                }
                                            }
                                            if (Q.getIdType().equals(Constantes.CHECK)) {
                                                ArrayList<CheckBox> ch = Q.getCheckBoxes();
                                                for (int j = 0; j < ch.size(); j++) {
                                                    if (ch.get(j).isChecked()) {
                                                        REG.addValue("CHECK" + tagid + j, true);
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
        }
    }
    //TODO FIN SAVEDATA

    //TODO DOWNLOAD ARCHIVO
    public void initD() {
        try {
            SYSTEMS = XMLParserDownloadTDC.parseFormularioDownload(QUERY);
            SYSTEMSMAP.put(IDMAIN+","+TITLE, SYSTEMS);

            for (final SYSTEM S : SYSTEMS) {
                /**      CABECERA SYSTEMS  **/
                CONTENIDO.addView(S.generateView(mContext));

                for (final AREA A : S.getAreas()) {
                    /**         CABECERA AREAS **/
                    CONTENIDO.addView(A.generateView(mContext));
                    if (A.getItems() == null) {
                        continue;
                    }
                    for (final ITEM I : A.getItems()) {
                        /**         CABECERA ITEMS **/
                        CONTENIDO.addView(I.getTitle(mContext));
                        final LinearLayout itemLayout = create_itemLayout();

                        View v = I.generateView(mContext);
                        if (v != null) {
                            itemLayout.addView(v);
                        }
                        if (I.getQuestions() != null) {

                            for (final QUESTION Q : I.getQuestions()) {
                                LinearLayout layquest = create_questionLayout();
                                if (Q.getPhoto().equals("OK")) { //Si requiere fotos agregamos el boton foto
                                    ImageButton photo = create_photoButton(Q);
                                    layquest.addView(photo);
                                }
                                layquest.addView(Q.getTitle(mContext));

                                View question = Q.generateView(mContext);
                                if (question != null) {
                                    agregar2 = false;
                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                    if (Q.getPhoto().equals("OK")) { //Si requiere foto|, buscamos si hay fotos gardadas
                                        cargar_fotos(Q, tag);
                                    }

                                    if (Q.getIdType().equals(Constantes.RADIO)) {
                                        int pos = REG.getInt("RADIO" + tag);
                                        if (pos != -100) {
                                            ((RadioButton) ((RadioGroup) Q.getView()).getChildAt(pos)).setChecked(true);
                                        }

                                        if (Q.getValues().size() > 0 && TITLE.equalsIgnoreCase("emergency")) {

                                            itemLayout.addView(layquest);
                                            itemLayout.addView(question);
                                            agregar2 = true;

                                            /** Generar vista de Emergencia **/
                                            final ArrayList<View> repeatContentList = new ArrayList<>();
                                            final ArrayList<Button> repeatButtontList = new ArrayList<>();

                                            final LinearLayout repeatLayout = create_normalVerticalLayout();

                                            ArrayList<QUESTION> listaAuxSet = new ArrayList<>();

                                            if (Q.getIdType().equals(Constantes.RADIO)) {

                                                for (int x = 0; x < Q.getValues().size(); x++) {
                                                    VALUE value = Q.getValues().get(x);

                                                    Button boton = crear_botonRepeat();
                                                    boton.setText(value.getNameValue());

                                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                                    for (QUESTION set : value.getQuestions()) {

                                                        QUESTION setAux = new QUESTION();
                                                        setAux.setIdQuestion(set.getIdQuestion());
                                                        setAux.setNameQuestion(set.getNameQuestion());
                                                        setAux.setNameType(set.getNameType());
                                                        setAux.setFoto(set.getFoto());
                                                        setAux.setNumberPhoto(set.getNumberPhoto());
                                                        setAux.setValues(set.getValues());
                                                        setAux.setIdType(set.getIdType());

                                                        LinearLayout setLayout = create_setLayout();

                                                        LinearLayout questTitle = create_questionLayout();

                                                        questTitle.addView(setAux.getTitle(mContext));
                                                        questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                        question2 = setAux.generateView(mContext);
                                                        if (question2 != null) {

                                                            tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdQuestion() + setAux.getNameQuestion();
                                                            Log.d("BUSCANDOEN", "Init: " + tag);
                                                            if (setAux.getIdType().equals(Constantes.CHECK)) {
                                                                ArrayList<CheckBox> ch = setAux.getCheckBoxes();
                                                                for (int j = 0; j < ch.size(); j++) {
                                                                    Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                                    ch.get(j).setChecked(check);
                                                                }
                                                            }

                                                            setLayout.addView(questTitle);
                                                            if (!setAux.getIdType().equals(Constantes.PHOTO))
                                                                setLayout.addView(question2);
                                                        }
                                                        listaAuxSet.add(setAux);

                                                        contentSetLayout.addView(setAux.getTitle(mContext));
                                                        contentSetLayout.addView(setLayout);
                                                    }
                                                    value.setQuestions(listaAuxSet);
                                                    //I.setQuestions(listaAuxSet);
                                                    //Q.setQuestions(listaAuxSet);


                                                    boton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                                for (View layu : repeatContentList) {
                                                                    if (!layu.equals(contentSetLayout)) {
                                                                        layu.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                            } else {
                                                                contentSetLayout.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });

                                                    contentSetLayout.setVisibility(View.GONE);
                                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                                    repeatButtontList.add(boton);
                                                    repeatLayout.addView(boton);
                                                    repeatLayout.addView(contentSetLayout);

                                                }

                                                RadioGroup group = (RadioGroup) Q.getView();

                                                itemLayout.addView(repeatLayout);

                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                        for (View l : repeatContentList) {
                                                            l.setVisibility(View.GONE);
                                                        }
                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                        int position = rg.indexOfChild(btn);
                                                        for (Button b : repeatButtontList) {
                                                            b.setVisibility(View.GONE);
                                                        }
                                                        //for (int i = 0; i < position; i++) {
                                                        repeatButtontList.get(position).setVisibility(View.VISIBLE);
                                                        //}
                                                    }
                                                });

                                                if (pos != -100) {
                                                    repeatButtontList.get(pos).setVisibility(View.VISIBLE);
                                                }

                                            }
                                        }

                                        if (Q.getValues().size() > 0 && TITLE.equalsIgnoreCase("wimax")) {
                                            itemLayout.addView(layquest);
                                            itemLayout.addView(question);
                                            agregar2 = true;
                                            for (VALUE V : Q.getValues()) {
                                                if (V.getQuestions() != null) {
                                                    int max2 = V.getQuestions().size();
                                                    //for (QUESTION QES : V.getQuestions()) {
                                                    for (int ikj = 0; ikj < max2; ikj ++) {
                                                        QUESTION QES = V.getQuestions().get(0);
                                                        layquest2 = create_questionLayout();
                                                        layquest2.addView(QES.getTitle(mContext));
                                                        layquest2.setVisibility(View.GONE);
                                                        question2 = QES.generateView(mContext);
                                                        question2.setVisibility(View.GONE);
                                                        if (question2 != null) {
                                                            if (QES.getIdType().equals(Constantes.RADIO)) {
                                                                if (pos != -100) {
                                                                    ((RadioButton) ((RadioGroup) QES.getView()).getChildAt(pos)).setChecked(true);
                                                                }

                                                                RadioGroup group = (RadioGroup) Q.getView();

                                                                int id;
                                                                int max = group.getChildCount();

                                                                for (int i = 0; i < max; i++){
                                                                    id = group.getChildAt(i).getId();
                                                                    ln.put(id, layquest2);
                                                                    vw.put(id, question2);
                                                                    rgm.put(id, group);
                                                                }

                                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                                    @Override
                                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                                        int position = rg.indexOfChild(btn) + 1;

                                                                        layquest2 = ln.get(id);
                                                                        question2 = vw.get(id);

                                                                        Log.d("MAP", ln.get(id) + "");
                                                                        Log.d("MAP", vw.get(id) + "");

                                                                        if (position == 2) {
                                                                            layquest2.setVisibility(View.GONE);
                                                                            question2.setVisibility(View.GONE);
                                                                        } else {
                                                                            layquest2.setVisibility(View.VISIBLE);
                                                                            question2.setVisibility(View.VISIBLE);
                                                                        }
                                                                    }
                                                                });
                                                                //agregar = true;
                                                                itemLayout.addView(layquest2);
                                                                itemLayout.addView(question2);
                                                            }
                                                        }

                                                        for (VALUE V2 : QES.getValues()) {
                                                            if (V2.getQuestions() != null) {
                                                                max2 = V2.getQuestions().size();
                                                                //for (QUESTION QES2 : V2.getQuestions()) {
                                                                for (int ikj2 = 0; ikj2 < max2; ikj2 ++) {
                                                                    QUESTION QES2 = V2.getQuestions().get(0);
                                                                    layquest3 = create_questionLayout();
                                                                    layquest3.addView(QES2.getTitle(mContext));
                                                                    layquest3.setVisibility(View.GONE);
                                                                    question3 = QES2.generateView(mContext);
                                                                    question3.setVisibility(View.GONE);

                                                                    if (question3 != null) {
                                                                        if (QES2.getIdType().equals(Constantes.RADIO)) {
                                                                            if (pos != -100) {
                                                                                ((RadioButton) ((RadioGroup) QES2.getView()).getChildAt(pos)).setChecked(true);
                                                                            }

                                                                            RadioGroup group2 = (RadioGroup) QES.getView();

                                                                            int id;
                                                                            int max = group2.getChildCount();

                                                                            for (int i = 0; i < max; i++){
                                                                                id = group2.getChildAt(i).getId();
                                                                                ln.put(id, layquest3);
                                                                                vw.put(id, question3);
                                                                                rgm.put(id, group2);
                                                                            }

                                                                            group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                                                @Override
                                                                                public void onCheckedChanged(RadioGroup rg, int id) {
                                                                                    RadioButton btn = (RadioButton) rg.findViewById(id);
                                                                                    int position = rg.indexOfChild(btn) + 1;

                                                                                    layquest3 = ln.get(id);
                                                                                    question3 = vw.get(id);

                                                                                    Log.d("MAP", ln.get(id) + "");
                                                                                    Log.d("MAP", vw.get(id) + "");

                                                                                    if (position == 2) {
                                                                                        layquest3.setVisibility(View.GONE);
                                                                                        question3.setVisibility(View.GONE);
                                                                                    } else {
                                                                                        layquest3.setVisibility(View.VISIBLE);
                                                                                        question3.setVisibility(View.VISIBLE);
                                                                                    }
                                                                                }
                                                                            });
                                                                            //agregar = true;
                                                                            itemLayout.addView(layquest3);
                                                                            itemLayout.addView(question3);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (Q.getValues().size() > 0 && TITLE.equalsIgnoreCase("transporte")) {         //Este bloque es para sacar la pregunta interna que existe en Transporte 349 y 350
                                            for (VALUE V : Q.getValues()) {                                           //Iteramos sobre las respuesta, como es radio y sabemos que al pulsar en NO muestra la otro pregunta
                                                if (V.getQuestions() != null) {                                        //evaluamos la posicion del boton y mostramos y ocultamos
                                                    for (QUESTION QV : V.getQuestions()) {
                                                        layquest2 = create_questionLayout();
                                                        layquest2.addView(QV.getTitle(mContext));
                                                        layquest2.setVisibility(View.GONE);
                                                        question2 = QV.generateView(mContext);
                                                        question2.setVisibility(View.GONE);

                                                        if (question2 != null) {

                                                            String tag2 = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" +
                                                                    QV.getIdQuestion() + "-" +
                                                                    QV.getNameQuestion();

                                                            if (QV.getIdType().equals(Constantes.DATE)) {
                                                                final EditText t2 = QV.getEditTexts().get(0);
                                                                String text = REG.getString("DATE" + tag2);
                                                                t2.setText(text);
                                                                t2.setVisibility(View.GONE);

                                                                RadioGroup group = (RadioGroup) Q.getView();
                                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                                    @Override
                                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                                        int position = rg.indexOfChild(btn) + 1;

                                                                        if (position == 2) {
                                                                            layquest2.setVisibility(View.VISIBLE);
                                                                            t2.setVisibility(View.VISIBLE);
                                                                            t2.setText("");
                                                                            question2.setVisibility(View.VISIBLE);
                                                                        } else {
                                                                            layquest2.setVisibility(View.GONE);
                                                                            t2.setVisibility(View.GONE);
                                                                            question2.setVisibility(View.GONE);
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            itemLayout.addView(layquest2);
                                                            itemLayout.addView(question2);
                                                            //agregar = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (Q.getQuestions() != null && TITLE.equalsIgnoreCase("ac")) {

                                            itemLayout.addView(layquest);
                                            itemLayout.addView(question);
                                            agregar2 = true;

                                            //Generar vista del RepeatQuestion
                                            final ArrayList<View> repeatContentList = new ArrayList<>();
                                            final ArrayList<Button> repeatButtontList = new ArrayList<>();

                                            final LinearLayout repeatLayout = create_normalVerticalLayout();
                                            ArrayList<QUESTION> listaAuxSet = new ArrayList<>();

                                            if (Q.getIdType().equals(Constantes.RADIO)) {
                                                ArrayList<VALUE> listadoV = new ArrayList<>();
                                                for (int x = 0; x < Q.getValues().size(); x++) {
                                                    VALUE value = Q.getValues().get(x);

                                                    Button boton = crear_botonRepeat();
                                                    boton.setText(value.getNameValue());

                                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                                    ArrayList<QUESTION> listadoQ = new ArrayList<>();


                                                    LinearLayout setLayout = create_setLayout();

                                                    //if (set.getQuestions() != null) {

                                                    for (final QUESTION Q2 : Q.getQuestions()) {
                                                        QUESTION qAux = copiar_question(Q2);
                                                        LinearLayout questTitle = create_questionLayout();

                                                        question2 = qAux.generateView(mContext);
                                                        if (question2 != null) {

                                                            tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion() + "-" + value.getIdValue() + value.getNameValue() + "-" + Q2.getIdQuestion() + Q2.getNameQuestion();

                                                            Log.d("BUSCANDOEN", "saveData: " + tag);

                                                            if (qAux.getPhoto().equals("OK")) {
                                                                final ImageButton photo = create_photoButton(qAux);
                                                                questTitle.addView(photo);
                                                            }

                                                            if (qAux.getPhoto().equals("OK")) {
                                                                cargar_fotos(qAux, tag);
                                                            }

                                                            questTitle.addView(qAux.getTitle(mContext));
                                                            questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                            if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                                //pos = REG.getInt("RADIO" + tag);
                                                                if (pos != -100) {
                                                                    ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                                }
                                                            }
                                                            if (qAux.getIdType().equals(Constantes.NUM)) {
                                                                String text = REG.getString("NUM" + tag);
                                                                ((TextView) qAux.getView()).setText(text);
                                                            }
                                                            if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                                String text = REG.getString("TEXT" + tag);
                                                                ((TextView) qAux.getView()).setText(text);
                                                            }

                                                            setLayout.addView(questTitle);
                                                            if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                                setLayout.addView(question2);
                                                        }
                                                        listadoQ.add(qAux);
                                                        value.setQuestions(listadoQ);
                                                    }


                                                    contentSetLayout.addView(Q.getTitle(mContext));
                                                    contentSetLayout.addView(setLayout);
                                                    //}

                                                    //listaAuxSet.add(set);
                                                    //}

                                                    boton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                                for (View layu : repeatContentList) {
                                                                    if (!layu.equals(contentSetLayout)) {
                                                                        layu.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                            } else {
                                                                contentSetLayout.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    });

                                                    contentSetLayout.setVisibility(View.GONE);
                                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                                    repeatButtontList.add(boton);
                                                    repeatLayout.addView(boton);
                                                    repeatLayout.addView(contentSetLayout);

                                                    listadoV.add(value);
                                                    //Q.setValues(listadoV);
                                                    //Q.setQuestions(listadoQ);
                                                }

                                                RadioGroup group = (RadioGroup) Q.getView();

                                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                                        for (View l : repeatContentList) {
                                                            l.setVisibility(View.GONE);
                                                        }
                                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                                        int position = rg.indexOfChild(btn) + 1;
                                                        for (Button b : repeatButtontList) {
                                                            b.setVisibility(View.GONE);
                                                        }
                                                        for (int i = 0; i < position; i++) {
                                                            repeatButtontList.get(i).setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                });


                                                if (pos != -100) {                                                                  //Mostramos los botones activos
                                                    ((RadioButton) ((RadioGroup) Q.getView()).getChildAt(pos)).setChecked(true);
                                                    for (int i = 0; i < pos + 1; i++) {
                                                        repeatButtontList.get(i).setVisibility(View.VISIBLE);
                                                    }
                                                }


                                                //Q.setQuestions(listaAuxSet);
                                                //listaAuxSet.add(Q);

                                            }
                                            //I.setQuestions(listaAuxSet);
                                            itemLayout.addView(repeatLayout);
                                        }

/////////////////////////////////////////////////HASTA AQUI//////////////////////////////////////////////////////////
                                    }

                                    if (Q.getIdType().equals(Constantes.NUM)) {
                                        String text = REG.getString("NUM" + tag);
                                        ((TextView) Q.getView()).setText(text);
                                    }
                                    if (Q.getIdType().equals(Constantes.TEXT)) {
                                        String text = REG.getString("TEXT" + tag);
                                        ((TextView) Q.getView()).setText(text);
                                    }
                                    if (Q.getIdType().equals(Constantes.CHECK)) {
                                        ArrayList<CheckBox> ch = Q.getCheckBoxes();

                                        for (int j = 0; j < ch.size(); j++) {
                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                            ch.get(j).setChecked(check);
                                        }

                                    }
                                    if (Q.getIdType().equals(Constantes.DIV)) {
                                        String textL = REG.getString("DIVL" + tag);
                                        String textR = REG.getString("DIVR" + tag);

                                        EditText left = Q.getEditTexts().get(0);
                                        EditText right = Q.getEditTexts().get(1);

                                        left.setText(textL);
                                        right.setText(textR);

                                    }
                                    if (Q.getIdType().equals(Constantes.DATE)) {
                                        EditText t = Q.getEditTexts().get(0);
                                        String text = REG.getString("DATE" + tag);
                                        t.setText(text);
                                    }

                                    if (!agregar2) {

                                        itemLayout.addView(layquest);

                                        if (!Q.getIdType().equals(Constantes.PHOTO))
                                            itemLayout.addView(question);
                                    }

                                    if (agregar)
                                    {
                                        itemLayout.addView(layquest2);
                                        itemLayout.addView(question2);
                                        agregar=false;
                                    }
                                    if (agregar3) {
                                        itemLayout.addView(layquest3);
                                        itemLayout.addView(question3);
                                        agregar3 = false;
                                    }
                                    /*if (agregar2)
                                    {
                                        itemLayout.addView(repeatLayout);
                                        itemLayout.addView(question2);
                                        agregar2=false;
                                    }*/

                                }

                            }

                        }

                        if (I.getSetArrayList() != null) {
                            /** Generar vista del IteamRepeat **/
                            final ArrayList<View> repeatContentList = new ArrayList<>();
                            final ArrayList<Button> repeatButtontList = new ArrayList<>();

                            final LinearLayout repeatLayout = create_normalVerticalLayout();

                            if (I.getIdType().equals(Constantes.TABLE)) {
                                //Preparamos la tabla o lo que sea
                                for (int x = 0; x < I.getValues().size(); x++) {
                                    VALUE value = I.getValues().get(x);
                                    Button boton = crear_botonRepeat();
                                    boton.setText(value.getNameValue());

                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                    ArrayList<SET> listaAuxSet = new ArrayList<>();
                                    for (SET set : I.getSetArrayList()) {
//values a los items
                                        SET setAux = new SET();
                                        setAux.setIdSet(set.getIdSet());
                                        setAux.setNameSet(set.getNameSet());
                                        setAux.setValueSet(set.getValueSet());

                                        LinearLayout setLayout = create_setLayout();
                                        if (set.getQuestions() != null) {
                                            //si los values tienen questions
                                            ArrayList<QUESTION> listadoQ = new ArrayList<>();

                                            for (final QUESTION Q : set.getQuestions()) {
                                                final QUESTION qAux = copiar_question(Q);
                                                LinearLayout questTitle = create_questionLayout();

                                                if (qAux.getPhoto().equals("OK")) {
                                                    final ImageButton photo = create_photoButton(qAux);
                                                    questTitle.addView(photo);
                                                }
                                                questTitle.addView(qAux.getTitle(mContext));

                                                View question = qAux.generateView(mContext);

                                                if (question != null) {
                                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdSet() + setAux.getNameSet() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                                    if (qAux.getPhoto().equals("OK")) {
                                                        cargar_fotos(qAux, tag);
                                                    }

                                                    if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                        int pos = REG.getInt("RADIO" + tag);
                                                        if (pos != -100) {
                                                            ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.NUM)) {
                                                        String text = REG.getString("NUM" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                        String text = REG.getString("TEXT" + tag);
                                                        ((TextView) qAux.getView()).setText(text);

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.CHECK)) {
                                                        ArrayList<CheckBox> ch = qAux.getCheckBoxes();

                                                        for (int j = 0; j < ch.size(); j++) {
                                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                            ch.get(j).setChecked(check);
                                                        }

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DIV)) {
                                                        String textL = REG.getString("DIVL" + tag);
                                                        String textR = REG.getString("DIVR" + tag);

                                                        EditText left = qAux.getEditTexts().get(0);
                                                        EditText right = qAux.getEditTexts().get(1);

                                                        left.setText(textL);
                                                        right.setText(textR);


                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DATE)) {
                                                        EditText t = qAux.getEditTexts().get(0);
                                                        String text = REG.getString("DATE" + tag);
                                                        t.setText(text);
                                                    }


                                                    setLayout.addView(questTitle);
                                                    if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                        setLayout.addView(question);
                                                }
                                                listadoQ.add(qAux);


                                            }
                                            setAux.setQuestions(listadoQ);
                                            contentSetLayout.addView(setAux.getTitle(mContext));
                                            contentSetLayout.addView(setLayout);
                                        }


                                        listaAuxSet.add(setAux);
                                    }
                                    I.addListSet(listaAuxSet);


                                    boton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                for (View layu : repeatContentList) {
                                                    if (!layu.equals(contentSetLayout)) {
                                                        layu.setVisibility(View.GONE);
                                                    }
                                                }
                                            } else {
                                                contentSetLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    contentSetLayout.setVisibility(View.GONE);
                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                    repeatButtontList.add(boton);       //para que al mostrar uno se oculten los demas
                                    repeatLayout.addView(boton);
                                    repeatLayout.addView(contentSetLayout);

                                }
                                itemLayout.addView(repeatLayout);

                                for (final CheckBox c : I.getCheckBoxes()) {
                                    final int pos = I.getCheckBoxes().indexOf(c);
                                    c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            repeatContentList.get(pos).setVisibility(View.GONE);
                                            if (b) {
                                                repeatButtontList.get(pos).setVisibility(View.VISIBLE);
                                            } else {
                                                repeatButtontList.get(pos).setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    boolean check = REG.getBoolean("TABLE" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + pos);
                                    c.setChecked(check);
                                }

                            }

                            if (I.getIdType().equals(Constantes.RADIO)) {
                                for (int x = 0; x < I.getValues().size(); x++) {
                                    VALUE value = I.getValues().get(x);

                                    Button boton = crear_botonRepeat();
                                    boton.setText(value.getNameValue());

                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                    ArrayList<SET> listaAuxSet = new ArrayList<>();
                                    for (SET set : I.getSetArrayList()) {

                                        SET setAux = new SET();
                                        setAux.setIdSet(set.getIdSet());
                                        setAux.setNameSet(set.getNameSet());
                                        setAux.setValueSet(set.getValueSet());
                                        LinearLayout setLayout = create_setLayout();

                                        if (set.getQuestions() != null) {
                                            ArrayList<QUESTION> listadoQ = new ArrayList<>();
//pasa aqui
                                            for (final QUESTION Q : set.getQuestions()) {
                                                QUESTION qAux = copiar_question(Q);
                                                LinearLayout questTitle = create_questionLayout();

                                                if (qAux.getPhoto().equals("OK")) {
                                                    final ImageButton photo = create_photoButton(qAux);
                                                    questTitle.addView(photo);
                                                }

                                                questTitle.addView(qAux.getTitle(mContext));
                                                questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                View question = qAux.generateView(mContext);
                                                if (question != null) {
                                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + value.getIdValue() + value.getNameValue() + "-" + setAux.getIdSet() + setAux.getNameSet() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                                    if (qAux.getPhoto().equals("OK")) {
                                                        cargar_fotos(qAux, tag);
                                                    }

                                                    if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                        int pos = REG.getInt("RADIO" + tag);
                                                        if (pos != -100) {
                                                            ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.NUM)) {
                                                        String text = REG.getString("NUM" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                        String text = REG.getString("TEXT" + tag);
                                                        ((TextView) qAux.getView()).setText(text);

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.CHECK)) {
                                                        ArrayList<CheckBox> ch = qAux.getCheckBoxes();
                                                        for (int j = 0; j < ch.size(); j++) {
                                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                            ch.get(j).setChecked(check);
                                                        }

                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DIV)) {
                                                        String textL = REG.getString("DIVL" + tag);
                                                        String textR = REG.getString("DIVR" + tag);

                                                        EditText left = qAux.getEditTexts().get(0);
                                                        EditText right = qAux.getEditTexts().get(1);

                                                        left.setText(textL);
                                                        right.setText(textR);


                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DATE)) {
                                                        EditText t = qAux.getEditTexts().get(0);
                                                        String text = REG.getString("DATE" + tag);
                                                        t.setText(text);
                                                    }

                                                    setLayout.addView(questTitle);
                                                    if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                        setLayout.addView(question);
                                                }
                                                listadoQ.add(qAux);


                                            }
                                            setAux.setQuestions(listadoQ);
                                            contentSetLayout.addView(setAux.getTitle(mContext));
                                            contentSetLayout.addView(setLayout);
                                        }

                                        listaAuxSet.add(setAux);
                                    }
                                    I.addListSet(listaAuxSet);


                                    boton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                for (View layu : repeatContentList) {
                                                    if (!layu.equals(contentSetLayout)) {
                                                        layu.setVisibility(View.GONE);
                                                    }
                                                }
                                            } else {
                                                contentSetLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    contentSetLayout.setVisibility(View.GONE);
                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                    repeatButtontList.add(boton);
                                    repeatLayout.addView(boton);
                                    repeatLayout.addView(contentSetLayout);

                                }

                                RadioGroup group = (RadioGroup) I.getView();

                                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup rg, int id) {
                                        for (View l : repeatContentList) {
                                            l.setVisibility(View.GONE);
                                        }
                                        RadioButton btn = (RadioButton) rg.findViewById(id);
                                        int position = rg.indexOfChild(btn) + 1;
                                        for (Button b : repeatButtontList) {
                                            b.setVisibility(View.GONE);
                                        }
                                        for (int i = 0; i < position; i++) {
                                            repeatButtontList.get(i).setVisibility(View.VISIBLE);
                                        }

                                    }
                                });

                                int pos = REG.getInt("RADIO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                Log.d("POSITION", "RADIO" + I.getIdItem() + " -pos: " + pos);
                                if (pos != -100) {
                                    ((RadioButton) ((RadioGroup) I.getView()).getChildAt(pos)).setChecked(true);
                                }
                                itemLayout.addView(repeatLayout);
                            }


                            if (I.getIdType().equals(Constantes.ADD)) {
                                ((TextView) I.getView()).setText("Aires Acondicionados: " + NAIR);

                                int n = Integer.parseInt(NAIR);
                                for (int x = 0; x < n; x++) {
                                    Button boton = crear_botonRepeat();
                                    boton.setVisibility(View.VISIBLE);
                                    boton.setText("Aire Acondicionado " + (x + 1));

                                    final LinearLayout contentSetLayout = create_normalVerticalLayout();

                                    ArrayList<SET> listaAuxSet = new ArrayList<>();
                                    for (SET set : I.getSetArrayList()) {
                                        SET setAux = new SET();
                                        setAux.setIdSet(set.getIdSet());
                                        setAux.setNameSet(set.getNameSet());
                                        setAux.setQuestions(set.getQuestions());

                                        LinearLayout setLayout = create_setLayout();

                                        if (setAux.getQuestions() != null) {
                                            ArrayList<QUESTION> listadoQ = new ArrayList<>();

                                            for (final QUESTION Q : setAux.getQuestions()) {
                                                QUESTION qAux = copiar_question(Q);
                                                LinearLayout questTitle = create_questionLayout();

                                                if (qAux.getPhoto().equals("OK")) {
                                                    final ImageButton photo = create_photoButton(qAux);
                                                    questTitle.addView(photo);
                                                }

                                                questTitle.addView(qAux.getTitle(mContext));
                                                questTitle.setGravity(Gravity.CENTER_VERTICAL);

                                                View question = qAux.generateView(mContext);
                                                if (question != null) {
                                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + "AIR" + x + "-" + setAux.getIdSet() + setAux.getNameSet() + "-" + qAux.getIdQuestion() + "-" + qAux.getNameQuestion();

                                                    if (qAux.getPhoto().equals("OK")) {
                                                        cargar_fotos(qAux, tag);
                                                    }

                                                    if (qAux.getIdType().equals(Constantes.RADIO)) {
                                                        int pos = REG.getInt("RADIO" + tag);
                                                        if (pos != -100) {
                                                            ((RadioButton) ((RadioGroup) qAux.getView()).getChildAt(pos)).setChecked(true);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.NUM)) {
                                                        String text = REG.getString("NUM" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.TEXT)) {
                                                        String text = REG.getString("TEXT" + tag);
                                                        ((TextView) qAux.getView()).setText(text);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.CHECK)) {
                                                        ArrayList<CheckBox> ch = qAux.getCheckBoxes();
                                                        for (int j = 0; j < ch.size(); j++) {
                                                            Boolean check = REG.getBoolean("CHECK" + tag + j);
                                                            ch.get(j).setChecked(check);
                                                        }
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DIV)) {
                                                        String textL = REG.getString("DIVL" + tag);
                                                        String textR = REG.getString("DIVR" + tag);

                                                        EditText left = qAux.getEditTexts().get(0);
                                                        EditText right = qAux.getEditTexts().get(1);

                                                        left.setText(textL);
                                                        right.setText(textR);
                                                    }
                                                    if (qAux.getIdType().equals(Constantes.DATE)) {
                                                        EditText t = qAux.getEditTexts().get(0);
                                                        String text = REG.getString("DATE" + tag);
                                                        t.setText(text);
                                                    }
                                                    setLayout.addView(questTitle);
                                                    if (!qAux.getIdType().equals(Constantes.PHOTO))
                                                        setLayout.addView(question);
                                                }
                                                listadoQ.add(qAux);
                                            }
                                            setAux.setQuestions(listadoQ);
                                            contentSetLayout.addView(setAux.getTitle(mContext));
                                            contentSetLayout.addView(setLayout);
                                        }
                                        listaAuxSet.add(setAux);
                                    }
                                    I.addListSet(listaAuxSet);

                                    boton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (contentSetLayout.getVisibility() == View.GONE) {
                                                contentSetLayout.setVisibility(View.VISIBLE);
                                                for (View layu : repeatContentList) {
                                                    if (!layu.equals(contentSetLayout)) {
                                                        layu.setVisibility(View.GONE);
                                                    }
                                                }
                                            } else {
                                                contentSetLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    contentSetLayout.setVisibility(View.GONE);
                                    repeatContentList.add(contentSetLayout);//para que al mostrar uno se oculten los demas
                                    repeatButtontList.add(boton);

                                    repeatLayout.addView(boton);
                                    repeatLayout.addView(contentSetLayout);
                                }
                                itemLayout.addView(repeatLayout);
                            }
                        } else {
                            if (I.getIdType().equals(Constantes.RADIO)) {
                                int pos = REG.getInt("RADIO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                Log.d("POSITION", "RADIO" + I.getIdItem() + " -pos: " + pos);
                                if (pos != -100) {
                                    ((RadioButton) ((RadioGroup) I.getView()).getChildAt(pos)).setChecked(true);
                                }
                            }
                            if (I.getIdType().equals(Constantes.CHECK)) {
                                for (CheckBox c : I.getCheckBoxes()) {
                                    boolean check = REG.getBoolean("CHECK" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + I.getCheckBoxes().indexOf(c));
                                    c.setChecked(check);
                                }
                            }
                            if (I.getIdType().equals(Constantes.CHECK_PHOTO)) {
                                for (CheckBox c : I.getCheckBoxes()) {
                                    boolean check = REG.getBoolean("CHECK-PHOTO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + I.getCheckBoxes().indexOf(c));
                                    c.setChecked(check);
                                }
                            }
                            if (I.getIdType().equals(Constantes.HOUR)) {
                                String hora = REG.getString("HOUR" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                I.getEditText().setText(hora);
                            }
                            if (I.getIdType().equals(Constantes.PHOTO)) {
                                String tagI = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem();

                                ImageButton photo = create_photoButtonItem(I);
                                itemLayout.addView(photo);
                                String name = REG.getString("PHOTONAME" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                cargar_fotosI(I,name,tagI);
                            }
                            if (I.getIdType().equals(Constantes.TEXT)) {
                                String text = REG.getString("TEXT" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                ((EditText) I.getView()).setText(text);
                            }
                            if (I.getIdType().equals(Constantes.NUM)) {
                                String text = REG.getString("NUM" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem());
                                ((EditText) I.getView()).setText(text);
                            }
                        }
                        CONTENIDO.addView(itemLayout);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException |
                IOException e
                )

        {
            e.printStackTrace();
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Error en XML");
            b.setIcon(android.R.drawable.ic_dialog_alert);
            b.setMessage("No se pudo generar el formulario");

            b.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    actividad.finish();
                }
            });
            b.show();

        }
    }
    public void enviar(View v) {

        if (TITLE.equalsIgnoreCase("IDEN")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarIden e = new EnviarIden();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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
        else if (TITLE.equalsIgnoreCase("3G")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Enviar3G e = new Enviar3G();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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
        else if (TITLE.equalsIgnoreCase("FAENA")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Enviar e = new Enviar();
                    e.execute();
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

        else if (TITLE.equalsIgnoreCase("TRANSPORTE")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarTransport e = new EnviarTransport();
                    e.execute();
                }

            });
            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            b.show();

        } else if (TITLE.equalsIgnoreCase("SYSTEM GROUND")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarSG e = new EnviarSG();
                    e.execute();

                }

            });
            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            b.show();

        } else if (TITLE.equalsIgnoreCase("DC")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarDC e = new EnviarDC();
                    e.execute();
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

        else if (TITLE.equalsIgnoreCase("AIR")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarAir e = new EnviarAir();
                    e.execute();

                }

            });
            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            b.show();

        } else if (TITLE.equalsIgnoreCase("AC")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarAC e = new EnviarAC();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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

        else if (TITLE.equalsIgnoreCase("GRUPO ELECTROGEN")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarGE e = new EnviarGE();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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

        else if (TITLE.equalsIgnoreCase("EMERGENCY")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarEmergency e = new EnviarEmergency();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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

        else if (TITLE.equalsIgnoreCase("WIMAX")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarWimax e = new EnviarWimax();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

                }

            });
            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            b.show();

        } else if (TITLE.equalsIgnoreCase("PDH")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarPdh e = new EnviarPdh();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();
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
        else if (TITLE.equalsIgnoreCase("AGREGADOR")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarAgregator e = new EnviarAgregator();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();
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
        else if (TITLE.equalsIgnoreCase("SEMESTRAL")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarSemestral e = new EnviarSemestral();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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
        else if (TITLE.equalsIgnoreCase("INSPECTION")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarInspeccion e = new EnviarInspeccion();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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
        else if (TITLE.equalsIgnoreCase("ANUAL")) {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Desea enviar la información?");

            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EnviarAnual e = new EnviarAnual();
                    e.execute();

                    //Toast.makeText(ActividadCierreFormActivity.this, "Datos enviados Exitosamente", Toast.LENGTH_SHORT).show();

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
    }

    private void verFotos() {
        AlertDialog.Builder b = new AlertDialog.Builder(actividad);
        final ArrayList<PHOTO> fotos = questionTMP.getFotos();
        ArrayList<String> list = new ArrayList<>();
        for (PHOTO p : fotos) {
            list.add(p.getTitlePhoto());
        }

        b.setItems(list.toArray(new CharSequence[list.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                ImageView joto = new ImageView(mContext);
                final PHOTO f = fotos.get(i);
                joto.setImageBitmap(BitmapFactory.decodeFile(f.getNamePhoto()));
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                b.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        File delete = new File(f.getNamePhoto());
                        if (delete.exists())
                            if (delete.delete()) {
                                Log.d("FOTO", "Imagen eliminada");
                            }
                        fotos.remove(f);
                        Toast.makeText(mContext, "Imagen eliminada", Toast.LENGTH_SHORT).show();

                        dialogInterface.dismiss();
                    }
                });

                b.setView(joto);
                b.setTitle(fotos.get(i).getTitlePhoto());
                b.show();
            }
        });
        b.setTitle("Listado de Imágenes");
        b.show();
    }

    private void verFotosItem() {
        AlertDialog.Builder b = new AlertDialog.Builder(actividad);
        final ArrayList<PHOTO> fotos = itemTMP.getFotos();
        ArrayList<String> list = new ArrayList<>();
        for (PHOTO p : fotos) {
            list.add(p.getTitlePhoto());
        }

        b.setItems(list.toArray(new CharSequence[list.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                ImageView joto = new ImageView(mContext);
                final PHOTO f = fotos.get(i);
                joto.setImageBitmap(BitmapFactory.decodeFile(f.getNamePhoto()));
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                b.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        File delete = new File(f.getNamePhoto());
                        if (delete.exists())
                            if (delete.delete()) {
                                Log.d("FOTO", "Imagen eliminada");
                            }
                        fotos.remove(f);
                        Toast.makeText(mContext, "Imagen eliminada", Toast.LENGTH_SHORT).show();

                        dialogInterface.dismiss();
                    }
                });

                b.setView(joto);
                b.setTitle(fotos.get(i).getTitlePhoto());
                b.show();
            }
        });
        b.setTitle("Listado de Imágenes");
        b.show();

    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int code = TAKE_PICTURE;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String timeStamp1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        photoTMP.setDateTime(timeStamp);
        imgName = name + itemTMP.getIdItem() + "_" + timeStamp1 + ".jpg";
        Uri output = Uri.fromFile(new File(imgName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, code);
    }

    private void tomarFotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int code = TAKE_PICTURES;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String timeStamp1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        photoTMP.setDateTime(timeStamp);
        imgName = name + questionTMP.getIdQuestion() + "_" + timeStamp1 + ".jpg";
        //redimencionarImagen(imgName);
        Uri output = Uri.fromFile(new File(imgName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, code);
    }

    private void seleccionarFotoGaleria(){

        Log.e("VERSION DE ANDROID: ", String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Seleccione"),
                    SELECT_FILE);
        } else {
            Log.e("Entro en version menor","");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PHOTO_FROM_GALLERY_KITKAT);
        }

            /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Seleccione"), SELECT_FILE);*/
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CODE", "resultcode" + resultCode);
        if (resultCode == -1) {
            String filePath = null;
            Cursor cursor = null;
            Uri originalUri = null;
            if (requestCode == TAKE_PICTURE) {
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                final EditText titulo = new EditText(this);

                b.setCancelable(false);
                titulo.setHint("Título");
                b.setTitle("Información Fotografía");
                b.setView(titulo);
                b.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            copyFileOrig(imgName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        redimencionarImagen(imgName);
                        photoTMP.setTitlePhoto(titulo.getText().toString());
                        photoTMP.setCoordX(String.valueOf(trackerTDC.gps.getLatitude()));
                        photoTMP.setCoordY(String.valueOf(trackerTDC.gps.getLongitude()));
                        photoTMP.setNamePhoto(imgName);
                        itemTMP.addFoto(photoTMP);
                        //itemTMP.addFoto(photoTMP);
                        //buttonTMP.setEnabled(true);
                        //buttonTMP.setFocusable(true);
                        dialogInterface.dismiss();
                    }
                });

                b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photoTMP = null;
                        dialogInterface.dismiss();
                    }
                });
                b.show();
            }
            if (requestCode == TAKE_PICTURES) {
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                final EditText titulo = new EditText(this);

                b.setCancelable(false);
                titulo.setHint("Título");
                b.setTitle("Información Fotografía");
                b.setView(titulo);
                b.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                        copyFileOrig(imgName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                        redimencionarImagen(imgName);

                        photoTMP.setTitlePhoto(titulo.getText().toString());
                        photoTMP.setCoordX(String.valueOf(trackerTDC.gps.getLatitude()));
                        photoTMP.setCoordY(String.valueOf(trackerTDC.gps.getLongitude()));
                        photoTMP.setNamePhoto(imgName);
                        Log.d("PHOTO", "X " + photoTMP.getCoordX() + "  Y " + photoTMP.getCoordY());
                        questionTMP.addFoto(photoTMP);
                        //buttonTMP.setFocusable(true);
                        dialogInterface.dismiss();
                    }
                });
                b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photoTMP = null;
                        dialogInterface.dismiss();
                    }
                });
                b.show();
            }

            if (requestCode == SELECT_FILE) {
                originalUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                cursor =  actividad.getContentResolver().query(originalUri,filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                final String selectedImagePath = cursor.getString(columnIndex);
                cursor.close();
                final Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;

                while (options.outHeight / scale / 2 >= REQUIRED_SIZE  && options.outWidth / scale / 2 >= REQUIRED_SIZE)
                    scale *= 20;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                Log.e("selectedImagePath", "Valor que trae  bm = BitmapFactory.decodeFile(selectedImagePath, options) " + bm);

                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                final EditText titulo = new EditText(this);

                b.setCancelable(false);
                titulo.setHint("Título");
                b.setTitle("Información Fotografía");
                b.setView(titulo);
                b.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photoTMP = new PHOTO();
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String timeStamp1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
                        photoTMP.setDateTime(timeStamp);
                        photoTMP.setTitlePhoto(titulo.getText().toString());
                        photoTMP.setCoordX(String.valueOf(trackerTDC.gps.getLatitude()));
                        photoTMP.setCoordY(String.valueOf(trackerTDC.gps.getLongitude()));
                        imgName = name + "_" +timeStamp1+ ".jpg";
                        photoTMP.setNamePhoto(imgName.trim());
                        photoTMP.setBitmap(bm);
                        dialogInterface.dismiss();
                        if (questionTMP!=null)
                            questionTMP.addFoto(photoTMP);
                        if (itemTMP!=null) {
                            itemTMP.addFoto(photoTMP);
                        }
                        try {
                            copyFile(selectedImagePath,imgName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialogInterface.dismiss();
                    }
                });
                b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photoTMP = null;
                        itemTMP = null;
                        dialogInterface.dismiss();
                    }
                });
                b.show();

            } else if (requestCode == PHOTO_FROM_GALLERY_KITKAT) {

                originalUri = data.getData();
                int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //actividad.getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
                actividad.getContentResolver().takePersistableUriPermission(originalUri, (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));

                final String selectedImagePath = getPath(actividad, originalUri);
                final Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outHeight / scale / 2 >= REQUIRED_SIZE  && options.outWidth / scale / 2 >= REQUIRED_SIZE)
                    scale *= 20;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                Log.e("selectedImagePath", "Valor que trae  bm = BitmapFactory.decodeFile(selectedImagePath, options) " + bm);

                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                final EditText titulo = new EditText(this);

                b.setCancelable(false);
                titulo.setHint("Título");
                b.setTitle("Información Fotografía");
                b.setView(titulo);
                b.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photoTMP = new PHOTO();

                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String timeStamp1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
                        photoTMP.setDateTime(timeStamp);
                        photoTMP.setTitlePhoto(titulo.getText().toString());
                        photoTMP.setCoordX(String.valueOf(trackerTDC.gps.getLatitude()));
                        photoTMP.setCoordY(String.valueOf(trackerTDC.gps.getLongitude()));
                        imgName = name + "_" +timeStamp1+ ".jpg";
                        photoTMP.setNamePhoto(imgName.trim());
                        photoTMP.setBitmap(bm);
                        dialogInterface.dismiss();
                        if (questionTMP!=null)
                            questionTMP.addFoto(photoTMP);
                        if (itemTMP!=null) {
                            itemTMP.addFoto(photoTMP);
                        }
                        try {
                            copyFile(selectedImagePath,imgName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialogInterface.dismiss();
                    }
                });
                b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photoTMP = null;
                        itemTMP = null;
                        dialogInterface.dismiss();
                    }
                });
                b.show();

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActividadCierreForm Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cl.tdc.felipe.tdc/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActividadCierreForm Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cl.tdc.felipe.tdc/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void copyFile(String selectedImagePath, String string) throws IOException {
        InputStream in = new FileInputStream(selectedImagePath);
        OutputStream out = new FileOutputStream(string);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void copyFileOrig(String selectedImagePath) throws IOException {
        String[] textos = selectedImagePath.split("/");
        String nombreImagen = textos[textos.length-1];
        InputStream in = new FileInputStream(selectedImagePath);
        OutputStream out = new FileOutputStream(name_orig + nombreImagen);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    private void redimencionarImagen(String dir) {
        //File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Log.d("Entramos en el metodo redimencionarImagen la ruta es ",dir);
        Bitmap b = BitmapFactory.decodeFile(dir);
        Bitmap out = Bitmap.createScaledBitmap(b, b.getWidth() / 2, b.getHeight()/2, false);

        String[] textos = dir.split("/");
        String nombreImagen = textos[textos.length-1];
        String rutaImagen  = dir.replace(nombreImagen, "");

        Log.d("Nombre de la imagen:  ", nombreImagen);
        Log.d("Ruta de la imagen:  " , rutaImagen);

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
            Log.e("Error en el proceso de crear nuevo archivo de imagen:",e.toString());
        }
    }


    private class EnviarIden extends AsyncTask<String, String, String> {
        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarIden() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }


        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerIDEN(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();                                             //Desde Aqui guardamos el fichero local
                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerIden", response);

                    return "Datos exitosamente guardados";
                }


            } catch (IOException e) {
                return "Se agotó el tiempo de conexión.";
               /* } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
                    return "Error al leer XML";*/
            } catch (Exception e) {
                return "Error al enviar la respuesta";
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class Enviar3G extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private Enviar3G() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswer3G(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);

                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local 3g para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answer3G", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }


        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class Enviar extends AsyncTask<String, String, String> {

        boolean ok = false;

        private Enviar() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);
            bEnviar.setEnabled(false);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        private String getAction() {
            if (TITLE.equals("FAENA")) return SoapRequestTDC.ACTION_SEND_FAENA;
            else return "";
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswer(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS, getAction());
                LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local 3g para posteriormente ser enviado en Cierre ACtividad

                if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                    localT.escribirFicheroMemoriaExterna(IDMAIN + ",answer" + getAction(), response);

                return "Datos exitosamente guardados";

            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
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
            b.show();
        }
    }

    //Editado por S G
    private class EnviarTransport extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarTransport() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerTransport(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")) {
                    return "Debe llenar el formulario.";
                } else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerTransport", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarSG extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarSG() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerSG(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerSystem", response);

                    return "Datos exitosamente guardados";
                }

            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarDC extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarDC() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerDC(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);

                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerDC", response);

                    return "Datos exitosamente guardados";
                }

            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarAir extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarAir() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerAir(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerAir", response);

                    return "Datos exitosamente guardados";
                }

            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarAC extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarAC() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerAC(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerAC", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarGE extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarGE() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerGE(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerGE", response);

                    return "Datos exitosamente guardados";
                }

            } catch (Exception e) {
                return "Error al enviar la respuesta."; //aqui tambien entra

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();

            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarEmergency extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarEmergency() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerEmergency(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerEmerg", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarWimax extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);
        private EnviarWimax() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerWIMAX(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerWimax", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarAgregator extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarAgregator() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerAgregator(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerAgregator", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarSemestral extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarSemestral() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerSemestral(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerSemestral", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }
    //End S G

    private class EnviarPdh extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarPdh() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerPdh(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerPdh", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarInspeccion extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarInspeccion() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerInspeccion(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);
                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerInspection", response);

                    return "Datos exitosamente guardados";
                }
            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    private class EnviarAnual extends AsyncTask<String, String, String> {

        boolean ok = false;
        FloatingActionButton bEnviar = (FloatingActionButton) findViewById(R.id.btnEnviar);

        private EnviarAnual() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Enviando formulario...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String response = SoapRequestTDC.sendAnswerAnual(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS);

                if (response.equalsIgnoreCase("false")){
                    return "Debe llenar el formulario.";
                }else {
                    LocalText localT = new LocalText();      //Desde Aqui guardamos el fichero local para posteriormente ser enviado en Cierre ACtividad

                    if (localT.isDisponibleSD() && localT.isAccesoEscrituraSD())
                        localT.escribirFicheroMemoriaExterna(IDMAIN + ",answerAnuaL", response);

                    return "Datos exitosamente guardados";
                }

            } catch (Exception e) {
                return "Error al enviar la respuesta.";

            }
            //TODO AGREGAR CATCH GENERAL
        }

        @Override
        protected void onPostExecute(final String s) {
            if (dialog.isShowing()) dialog.dismiss();


            AlertDialog.Builder b = new AlertDialog.Builder(actividad);
            b.setMessage(s);
            b.setCancelable(false);
            b.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (s.equalsIgnoreCase("Datos exitosamente guardados")) {
                        bEnviar.setEnabled(false);
                        REG.clearPreferences();
                        setResult(RESULT_OK);
                        actividad.finish();
                    }
                }
            });
            b.show();
        }
    }

    // Matias

    //TODO UPLOAD PHOTOS

    private class DownloadChecklistTask extends AsyncTask<String, String, ArrayList<String>> {
        ProgressDialog progressDialog = null;
        Context tContext;
        String ATAG = "MAINTASK";
        String mensaje;

        public DownloadChecklistTask(Context context) {
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
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                publishProgress("Verificando Jornada...");
                String query = SoapRequest.getAnswerGE(IDMAIN,IMEI);
                return XMLParser.getReturnCode(query);
            } catch (IOException e) {

                LocalText localT = new LocalText();
                String mantenimientoLocal = localT.leerFicheroMemoriaExterna("planing-mantience");

                if (mantenimientoLocal!=null){
                    ArrayList<String> nombreArrayList = new ArrayList<String>();
                    nombreArrayList.add("0");
                    nombreArrayList.add("local");
                    return nombreArrayList;
                }else{
                    Log.e(ATAG, e.getMessage() + ":\n" + e.getCause());
                    mensaje = dummy.ERROR_CONNECTION;
                }

            } catch (SAXException | XPathExpressionException | ParserConfigurationException e) {
                e.printStackTrace();
                mensaje = dummy.ERROR_PARSE;
            } catch (Exception e) {
                e.printStackTrace();
                mensaje = dummy.ERROR_GENERAL;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (s == null) {
                Toast.makeText(tContext, mensaje, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(tContext, mensaje, Toast.LENGTH_LONG).show();
            } /* else {
                    Toast.makeText(getApplicationContext(), s.get(1), Toast.LENGTH_LONG).show();
                }*/


        }
    }

    @Override
    protected void onDestroy() {
        if (!formSended) {
            saveData();
        }
        super.onDestroy();
        this.wakelock.release();

    }

    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        this.wakelock.release();
    }

}