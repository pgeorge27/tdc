package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.daemon.PositionTrackerTDC;
import cl.tdc.felipe.tdc.extras.Constantes;
import cl.tdc.felipe.tdc.extras.Funciones;
import cl.tdc.felipe.tdc.objects.ControSeguridadDiario.Elemento;
import cl.tdc.felipe.tdc.objects.ControSeguridadDiario.Modulo;
import cl.tdc.felipe.tdc.objects.ControSeguridadDiario.SubModulo;
import cl.tdc.felipe.tdc.objects.FormularioCierre.AREA;
import cl.tdc.felipe.tdc.objects.FormularioCierre.ITEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.PHOTO;
import cl.tdc.felipe.tdc.objects.FormularioCierre.QUESTION;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SET;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SYSTEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.VALUE;
import cl.tdc.felipe.tdc.preferences.FormCheckReg;
import cl.tdc.felipe.tdc.preferences.FormCheckSecurityReg;
import cl.tdc.felipe.tdc.preferences.FormCierreReg;
import cl.tdc.felipe.tdc.webservice.SoapRequestCheckLists;
import cl.tdc.felipe.tdc.webservice.SoapRequestTDC;
import cl.tdc.felipe.tdc.webservice.UploadImage;
import cl.tdc.felipe.tdc.webservice.XMLParserChecklists;
import cl.tdc.felipe.tdc.webservice.XMLParserTDC;
import cl.tdc.felipe.tdc.webservice.dummy;

public class FormCheckSecurity extends Activity {
    Context mContext;
    String Response, QUERY;
    ArrayList<Modulo> formulario;
    ArrayList<Modulo> modulos;
    ScrollView scrollViewMain;
    Bitmap firma;
    public static Activity actividad;
    Bundle savedInstances;
    String RESPONSEGLOBAL;

    FormCheckReg reg;

    ArrayList<View> vistas = new ArrayList<>();

    //VARIABLES NUEVAS
    public static ArrayList<SYSTEM> SYSTEMS;
    public static HashMap<String, ArrayList<SYSTEM>> SYSTEMSMAP = new HashMap<String, ArrayList<SYSTEM>>();
    String IDMAIN, NAIR;
    private static String TITLE;
    FormCierreReg REG;

    ITEM itemTMP;
    QUESTION questionTMP;
    PHOTO photoTMP;
    private String imgName;
    private String name;
    Date fecha_global = new Date();

    LinearLayout CONTENIDO;
    public LinearLayout layquest2;

    private static int TAKE_PICTURE = 1;
    private static int TAKE_PICTURES = 2;
    private static int SELECT_FILE = 3;

    boolean formSended = false;

    int contador_firma = 0;

    private PositionTrackerTDC trackerTDC;
    protected PowerManager.WakeLock wakelock;
    ProgressDialog dialog;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        setContentView(R.layout.activity_formchecksecurity);

        reg = new FormCheckReg(this,"SECURITYREG");

        actividad = this;

        if(savedInstanceState != null){
            savedInstances = savedInstanceState;
        }else{
            savedInstances = null;
        }
        mContext = this;
        scrollViewMain = (ScrollView)findViewById(R.id.cerca_content);
        CONTENIDO = (LinearLayout) this.findViewById(R.id.contenido);
        QUERY = getIntent().getStringExtra("RESPONSE");

        TITLE = "SECURITY";

        REG = new FormCierreReg(mContext, TITLE + IDMAIN);

        Response = getIntent().getStringExtra("RESPONSE");
        ObtenerFormulario init = new ObtenerFormulario(this);

        name = Environment.getExternalStorageDirectory() + "/TDC@/" + TITLE + "/";
        File dir = new File(name);
        if (!dir.exists())
            if (dir.mkdirs()) {
                Log.d(TITLE, "Se creo el directorio " + name);
            } else
                Log.d(TITLE, "No se pudo crear el directorio " + name);

        init.execute();
    }

    //LINEAR LAYOUT DE DEISEÑO
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
        //setLayout.setBackgroundResource(R.drawable.fondo_general);
        setLayout.setPadding(10, 10, 10, 10);
        return setLayout;
    }

    private LinearLayout firma_setLayout() {

        LinearLayout firmaLayout = new LinearLayout(this);
        firmaLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        firmaLayout.setOrientation(LinearLayout.HORIZONTAL);
        firmaLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        return firmaLayout;

    }

    private void tomarFotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int code = TAKE_PICTURES;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String timeStamp1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        photoTMP.setDateTime(timeStamp);
        imgName = name + questionTMP.getIdQuestion() + "_" + timeStamp1 + ".jpg";
        Uri output = Uri.fromFile(new File(imgName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, code);
    }

    private void seleccionarFotoGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione"), SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CODE", "resultcode" + resultCode);
        if (resultCode == -1) {
            if (requestCode == TAKE_PICTURE) {
                AlertDialog.Builder b = new AlertDialog.Builder(actividad);
                final EditText titulo = new EditText(this);

                b.setCancelable(false);
                titulo.setHint("Título");
                b.setTitle("Información Fotografía1");
                b.setView(titulo);
                b.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                Uri selectedImageUri = data.getData();
                String[] projection = { MediaStore.MediaColumns.DATA };
                CursorLoader cursorLoader = new CursorLoader(this,selectedImageUri, projection, null, null,
                        null);
                Cursor cursor =cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                final String selectedImagePath = cursor.getString(column_index);
                final Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;

                while (options.outWidth / scale / 4 >= REQUIRED_SIZE  && options.outHeight / scale / 4 >= REQUIRED_SIZE)
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
                        String timeStamp = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
                        photoTMP.setDateTime(timeStamp);
                        photoTMP.setTitlePhoto(titulo.getText().toString());
                        photoTMP.setCoordX(String.valueOf(trackerTDC.gps.getLatitude()));
                        photoTMP.setCoordY(String.valueOf(trackerTDC.gps.getLongitude()));
                        photoTMP.setNamePhoto(selectedImagePath);
                        photoTMP.setBitmap(bm);
                        dialogInterface.dismiss();
                        if (questionTMP!=null)
                            questionTMP.addFoto(photoTMP);
                        if (itemTMP!=null) {
                            itemTMP.addFoto(photoTMP);
                            // buttonTMP.setEnabled(true);
                            // buttonTMP.setFocusable(true);
                        }
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

    public void onClick_apagar(View v) {AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("¿Seguro que desea salir del CheckList?");
        b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveData();
                ((Activity) mContext).finish();
                if (AgendaActivity.actividad != null)
                    AgendaActivity.actividad.finish();
                if (MainActivity.actividad != null)
                    MainActivity.actividad.finish();
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
        InputMethodManager imm = (InputMethodManager) actividad.getSystemService(Context.INPUT_METHOD_SERVICE);
        View foco = actividad.getCurrentFocus();
        if (foco == null || !imm.hideSoftInputFromWindow(foco.getWindowToken(), 0)){
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("¿Seguro que desea salir del CheckList?");
            b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveData();
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

    }

    public void enviar_form(View v){
        if(firma == null) {
            Toast.makeText(mContext, "Por favor registre su firma.", Toast.LENGTH_LONG).show();
            return;
        }

        Enviar task = new Enviar(this);
        task.execute();

    }

    private void makeOnlyOneCheckable(final List<CheckBox> cbs) {
        final List<CheckBox> copy = cbs;

        for (int i = 0; i < cbs.size(); i++) {
            final CheckBox check = cbs.get(i);
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int j = 0; j < cbs.size(); j++) {
                            if (copy.get(j) != check) {
                                copy.get(j).setChecked(false);
                            }

                        }
                    }
                }
            });

        }
    }

    /*
    TODO DIBUJAR VISTA
     */
    public void init() {
        try {
            SYSTEMS = XMLParserChecklists.getChecklistDaily(QUERY);
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

                            ArrayList<QUESTION> listadoQ = new ArrayList<>();

                            for (final QUESTION Q : I.getQuestions()) {
                                LinearLayout layquest = create_questionLayout();

                                if (Q.getPhoto().equals("OK")) { //Si requiere fotos agregamos el boton foto
                                    ImageButton photo = create_photoButton(Q);
                                    layquest.addView(photo);
                                }

                                //INICIO CREAR EL BOTON FIRMAR Y AGREGO SUS CARACTERISTICAS
                                Button firmar = new Button(this);
                                final Button verFirma = new Button(this);

                                firmar.setBackgroundResource(R.drawable.custom_button_rounded_green);
                                verFirma.setBackgroundResource(R.drawable.custom_button_rounded_green);

                                firmar.setText("Firmar");
                                firmar.setTextColor(Color.parseColor("#FFFFFF"));
                                verFirma.setText("Ver Firma");
                                verFirma.setTextColor(Color.parseColor("#FFFFFF"));
                                verFirma.setEnabled(false);
                                //FIN CREAR BOTON FIRMAR

                                LinearLayout setLayout = create_setLayout();

                                LinearLayout firmaLayout =  firma_setLayout();

                                //PREGUNTO EN CONTADOR GLOBAL, PARA AGREGAR LOS BOTONES FIRMA A LA VISTA
                                if (contador_firma == 1){

                                    firmaLayout.addView(firmar);
                                    firmaLayout.addView(verFirma);
                                }

                                contador_firma = contador_firma + 1;

                                layquest.addView(Q.getTitle(mContext));
                                View question = Q.generateView(mContext);
                                if (question != null) {
                                    String tag = S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-" + Q.getIdQuestion() + "-" + Q.getNameQuestion();

                                    if (Q.getPhoto().equals("OK")) { //Si requiere foto|, buscamos si hay fotos gardadas
                                        cargar_fotos(Q, tag);
                                    }

                                    if (Q.getIdType().equals(Constantes.TEXT)) {
                                        String text = REG.getString("TEXT" + tag);
                                        ((TextView) Q.getView()).setText(text);
                                    }
                                    Log.e("REG", "REGG: " + REG);

                                    if (Q.getIdType().equals(Constantes.RADIO)) {
                                        int pos = REG.getInt("RADIO" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem() + "-"  );
                                        if (pos != -100) {
                                            ((RadioButton) ((RadioGroup) Q.getView()).getChildAt(pos)).setChecked(true);
                                        }
                                    }

                                    setLayout.addView(question);

                                }

                                //INICIO FUNCION PARA CREAR LA FIRMA
                                firmar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        View v = LayoutInflater.from(FormCheckSecurity.this).inflate(R.layout.view_signature, null, false);

                                        final SignaturePad pad = (SignaturePad) v.findViewById(R.id.signature_pad);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                        builder.setView(v);
                                        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                                                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                                                firma = pad.getSignatureBitmap();
                                                //Le doy el nombre y formato a la firma
                                                Q.setFileName(telephonyManager.getDeviceId() + "_" + format.format(fecha_global) + ".png");
                                                Q.setFirma(firma);
                                                verFirma.setEnabled(true);

                                            }
                                        });
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });

                                        builder.setNeutralButton("Borrar", null);
                                        final AlertDialog dialog = builder.create();
                                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                            @Override
                                            public void onShow(DialogInterface d) {

                                                Button b = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                                                b.setOnClickListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View view) {
                                                        pad.clear();
                                                    }
                                                });
                                            }
                                        });

                                        dialog.show();

                                    }
                                });
                                //FIN FUNCION CREAR FIRMA

                                //INICIO FUNCION VER FIRMA
                                verFirma.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                                        b.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        b.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                firma = null;

                                                Q.setFirma(null);
                                                Q.setFileName(null);
                                                verFirma.setEnabled(false);
                                                Toast.makeText(mContext, " Se  ha eliminado la firma.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        View v = LayoutInflater.from(mContext).inflate(R.layout.view_signature_preview, null, false);
                                        ImageView IVpreview = (ImageView) v.findViewById(R.id.preview);
                                        IVpreview.setImageBitmap(firma);
                                        b.setView(v);
                                        b.setTitle("Vista Previa Firma");
                                        b.show();
                                    }
                                });
                                //FIN FUNCION VER FIRMA

                                String tmp = reg.getString("FIRMA");
                                if(!tmp.equals("")){
                                    firma = Funciones.decodeBase64(tmp);
                                    verFirma.setEnabled(true);
                                }

                                // ACÁ AGREGAMOS LOS ELEMENTOS A LA VISTA

                                setLayout.addView(firmaLayout);

                                itemLayout.addView(layquest);

                                itemLayout.addView(setLayout);

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
                        //PHOTO p = I.getPhoto();

                          /*  if (p != null) {
                                File tmp = new File(p.getNamePhoto());
                                if (tmp.exists()) {
                                    REG.addValue("PHOTONAME" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), p.getNamePhoto());
                                    REG.addValue("PHOTOTITLE" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), p.getTitlePhoto());
                                    REG.addValue("PHOTODATE" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), p.getDateTime());
                                    REG.addValue("PHOTOCOORDX" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), p.getCoordX());
                                    REG.addValue("PHOTOCOORDY" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), p.getCoordY());
                                }
                            }*/

                        //ArrayList<PHOTO> fotos = Q.getFotos();
                        if (fotos != null) {
                            for (int as = 0; as < fotos.size(); as++) {
                                PHOTO f = fotos.get(as);
                                Log.d("GUARDANDO", "foto: " + f.getTitlePhoto());
                                REG.addValue("PHOTONAME" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), f.getNamePhoto());
                                REG.addValue("PHOTOTITLE" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), f.getTitlePhoto());
                                REG.addValue("PHOTODATE" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), f.getDateTime());
                                REG.addValue("PHOTOCOORDX" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), f.getCoordX());
                                REG.addValue("PHOTOCOORDY" + S.getIdSystem() + "-" + A.getIdArea() + "-" + I.getIdItem(), f.getCoordY());
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


    private void redimencionarImagen(String dir) {
        //File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Bitmap b = BitmapFactory.decodeFile(dir);
        Bitmap out = Bitmap.createScaledBitmap(b, b.getHeight() / 2, b.getWidth() / 2, false);

        String[] textos = dir.split("/");
        String nombreImagen = textos[textos.length-1];
        String rutaImagen  = dir.replace(nombreImagen, "");

        File file = new File(rutaImagen, nombreImagen);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {}
    }

    /*
    TODO Obtener Formulario
     */
    private class ObtenerFormulario extends AsyncTask<String, String, ArrayList<SYSTEM>> {
        private final String ASYNCTAG = "OBTENERFORMULARIO";
        Context context;
        ProgressDialog dialog;
        String mensaje = "";

        public ObtenerFormulario(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Obteniendo Checklist de Seguridad...");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<SYSTEM> doInBackground(String... params) {
            try {
                Log.w("FORMCHECK", QUERY);
                ArrayList<SYSTEM> m = XMLParserChecklists.getChecklistDaily(Response);
                return m;
            } catch (IOException e) {
                e.printStackTrace();
                mensaje = dummy.ERROR_CONNECTION;
                Log.e(ASYNCTAG, e.getMessage() + ": " + e.getCause());
            } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
                mensaje = dummy.ERROR_PARSE;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SYSTEM> response) {
            if (response != null) {
                init();
            } else {
                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                FormCheckSecurity.this.finish();
            }
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    /*
   TODO Enviar Formulario
    */
    private class Enviar extends AsyncTask<String, String, String> {
        private final String ASYNCTAG = "ENVIARFORMULARIO";
        Context context;
        ProgressDialog dialog;
        boolean ok = false;

        public Enviar(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Enviando Formulario...");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String request = SoapRequestCheckLists.sendDailyActivities(telephonyManager.getDeviceId(), IDMAIN, SYSTEMS, fecha_global);
                String parse = XMLParserChecklists.getResultCode(request);

                String accion = "answerCheckSecurity";
                //ENVIO DE LOS DATOS HACÍA EL SERVICIO
                String RESPONSEGLOBAL = SoapRequestTDC.sendAll(request, accion);

                subir_fotos(request);

                if(parse.compareTo("0")==0){
                    ok = true;
                    parse = "Se ha realizado correctamente";
                    return parse;
                }else{
                    ok = false;
                    return parse;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(ASYNCTAG, e.getMessage() + ": " + e.getCause());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
            if(ok){
                for(SYSTEM S: SYSTEMS ){
                    for(AREA A: S.getAreas()){
                        for(ITEM I: A.getItems()) {
                            for (QUESTION Q : I.getQuestions()) {
                                if (Q.getNameType().equals("FIRMA")) {
                                    UploadImageSec uis = new UploadImageSec(mContext, Q.getFileName(), Q.getFirma());
                                    uis.execute(dummy.URL_UPLOAD_TO_TAG);

                                }
                            }
                        }
                    }
                }
                reg.clearPreferences();
            }

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    public void subir_fotos(String response) {

        for(SYSTEM S: SYSTEMS ){
            for(AREA A: S.getAreas()){
                for(ITEM I: A.getItems()) {
                    for (QUESTION Q : I.getQuestions()) {
                        if (Q.getIdType().equals("5")) {
                            String mensaje= "mensaje";
                            UploadImageFoto uis = new UploadImageFoto(mensaje, Q.getFotos());
                            uis.execute(dummy.URL_UPLOAD_IMG_MAINTENANCE);

                        }
                    }
                }
            }
        }
    }

    //CLASE PARA SUBIR LA FIRMA
    private class UploadImageSec extends AsyncTask<String, String, String> {

        private Context mContext;
        Bitmap imagen;
        String Filename;
        ProgressDialog dialog;


        public UploadImageSec(Context mContext, String FileName, Bitmap img) {
            this.mContext = mContext;
            this.imagen = img;
            this.Filename = FileName;
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {

                Log.i("ENVIANDO", Filename);
                HttpURLConnection conn;
                DataOutputStream dos;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;

                //GENERO EL '.PNG' DE LA FIRMA EN LA RUTA INDICADA
                File done = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TDC@/" + TITLE + "/", Filename);
                done.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                imagen.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(done);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();


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
                    conn.setRequestProperty("uploaded_file", Filename);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + Filename + "\"" + lineEnd);
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

                }

            } catch (Exception e) {
                Log.d("TAG", "Error: " + e.getMessage());
                response = "ERROR";
            }

            return response;

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Subiendo imagenes...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing())
                dialog.dismiss();
            ((Activity)mContext).finish();
            super.onPostExecute(s);
        }
    }

    //CLASE PARA SUBIR LAS FOTOS
    private class UploadImageFoto extends AsyncTask<String, String, String> {

        ArrayList<PHOTO> allPhotos;
        String mensaje;

        public UploadImageFoto(String msj,ArrayList<PHOTO> ps) {
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
            REG.clearPreferences();
            setResult(RESULT_OK);
            super.onPostExecute(s);
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
}