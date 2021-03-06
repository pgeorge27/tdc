package cl.tdc.felipe.tdc.webservice;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.tdc.felipe.tdc.extras.Constantes;
import cl.tdc.felipe.tdc.objects.FormularioCierre.AREA;
import cl.tdc.felipe.tdc.objects.FormularioCierre.ITEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.PHOTO;
import cl.tdc.felipe.tdc.objects.FormularioCierre.QUESTION;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SET;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SYSTEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.VALUE;

public class SoapRequestTDC {

    public static final String ACTION_IDEN = "checkiDen";
    public static final String ACTION_3G = "check3G";
    public static final String ACTION_AC = "checkAC";
    public static final String ACTION_DC = "checkDC";
    public static final String ACTION_SG = "checkSystemGround";
    public static final String ACTION_AIR = "checkAir";
    public static final String ACTION_TRANSPORTE = "checkTransport";
    public static final String ACTION_GE = "checkGE";
    public static final String ACTION_FAENA = "checkFaena";
    public static final String ACTION_EMERG = "checkEmergency";
    public static final String ACTION_WIMAX = "checkWimax";
    public static final String ACTION_PDH = "checkPdh";
    public static final String ACTION_AGREGADOR = "checkAgregator";
    public static final String ACTION_SEMESTRAL = "checkSemestral";
    public static final String ACTION_SEND_FAENA = "Faena";
    public static final String ACTION_INSPECCION = "checkInspection";
    public static final String ACTION_ANUAL = "checkAnual";
    /*
     * Clase Principal de Conexion SSL a WDSL
	 */

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static String getPlanningMaintenance(String IMEI) throws Exception {
        final String SOAP_ACTION = "urn:Configurationwsdl#planningMaintenance";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:planningMaintenance soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Planning xsi:type=\"urn:Planning\">" +
                        "<RequestPlan xsi:type=\"urn:RequestPlan\">" +
                        "<!--Optional:-->" +
                        "<HeaderPlan xsi:type=\"urn:HeaderPlan\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<User xsi:type=\"xsd:string\">" + IMEI + "</User>" +
                        "</HeaderPlan>" +
                        "</RequestPlan>" +
                        "</Planning>" +
                        "</urn:planningMaintenance>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";

        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

    public static String getFormularioCierre(String IMEI, String ID_MAINTENANCE, String TYPE) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#" + TYPE;
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:" + TYPE + " soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Checklist xsi:type=\"urn:Checklist\">" +
                        "<Request xsi:type=\"urn:Request\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->" +
                        "<Form_Header xsi:type=\"urn:Form_Header\">" +
                        "<MaintenanceId xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</MaintenanceId>" +
                        "</Form_Header>" +
                        "</Request>" +
                        "</Checklist>" +
                        "</urn:" + TYPE + ">" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";

        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;
    }

    public static String sendAnswerIDEN(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerIden";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio= false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerIden soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerIden xsi:type=\"urn:RequestAnswerIden\">" +
                        "<RequestIden xsi:type=\"urn:RequestIden\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRpta xsi:type=\"urn:SystemsRpta\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItem xsi:type=\"urn:SetRptaItem\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItem xsi:type=\"urn:RptaItem\">" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";
                        if (I.getQuestions() != null) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio = true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + formatter.format(photo.getDateTime()) + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio = true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }

                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                        vacio = true;
                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>";
                                xml += "</AnswerQuestion>";
                            }
                            xml += "</SetAnswerQuestion>";
                        }

                        if (I.getSetArrayList() != null && I.getValues() != null) {
                            xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                            String answerXML = "";
                            int count = 0;
                            for (CheckBox checkBox : I.getCheckBoxes()) {
                                if (checkBox.isChecked()) {
                                    vacio = true;
                                    count += 1;
                                    int posChecked = I.getCheckBoxes().indexOf(checkBox);
                                    answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">" +
                                            "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(posChecked).getNameValue() + "</IdValue>" +
                                            "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                    ArrayList<SET> repeat = I.getSetlistArrayList().get(posChecked);
                                    for (SET set : repeat) {
                                        //answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                                        if (set.getQuestions() != null) {
                                            for (QUESTION Q : set.getQuestions()) {
                                                int countFoto = 0;
                                                String xmlphotos = "";
                                                if (Q.getFoto() != null) {
                                                    countFoto += 1;
                                                    PHOTO photo = Q.getFoto();
                                                    File file = new File(photo.getNamePhoto());
                                                    if (file.exists()) {
                                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                "</Photo>";
                                                    }
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO p : Q.getFotos()) {
                                                        File file = new File(p.getNamePhoto());
                                                        if (file.exists()) {
                                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                    "</Photo>";
                                                            countFoto += 1;
                                                        }
                                                    }
                                                }
                                                answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                        "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                                    vacio = true;
                                                answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                        xmlphotos +
                                                        "</SetPhotos>";
                                                answerXML += "</AnswerQuestion>";
                                            }

                                        }

                                    }
                                    answerXML += "</SetAnswerQuestion>";
                                    answerXML += "</SetAnswer>";
                                }
                            }
                            xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + count + "</CountAnswerSet>";
                            xml += answerXML;
                            xml += "</SetAnswerSet>";
                        }
                        xml += "</RptaItem>";
                    }


                }
                xml += "</SetRptaItem>";
            }
            xml += "</SystemsRpta>";
        }

        xml += "</RequestIden>" +
                "</RequestAnswerIden>" +
                "</urn:answerIden>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        //return xml;
        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
       // return "false";
    }

    public static String sendAnswerEmergency(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerEmerg";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio=false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;
        boolean bandera = false; //variable creada para impedir que se duplique la respuesta 527

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerEmerg soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerEmerg xsi:type=\"urn:RequestAnswerEmerg\">" +
                        "<Request3G xsi:type=\"urn:Request3G\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRpta xsi:type=\"urn:SystemsRpta\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItem xsi:type=\"urn:SetRptaItem\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItem xsi:type=\"urn:RptaItem\">" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                        String itemResponse = "";
                        String questionResponse = "";

                        if (I.getQuestions() != null) {
                            //xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio=true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }


                                questionResponse += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdSet xsi:type=\"xsd:string\" />" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";

                                if (!Q.getAswer3G().equalsIgnoreCase(""))
                                    vacio = true;
                                questionResponse+=
                                        "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>" +
                                        "</AnswerQuestion>";


                                if (Q.getValues() != null) {                                                    //sacamos la pregunta 527 1 sola vez con la variable bandera
                                    for (VALUE VQ : Q.getValues()) {
                                        if (VQ.getQuestions() != null) {
                                            for (QUESTION Q2 : VQ.getQuestions()) {
                                                if (Q.getIdType().equals(Constantes.RADIO) && !bandera) {

                                                    RadioGroup rg = (RadioGroup) Q.getView();
                                                    int selected = rg.getCheckedRadioButtonId();

                                                    if (selected != -1) {
                                                        vacio=true;
                                                        RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                                        int position = rg.indexOfChild(btn);

                                                        QUESTION QP = VQ.getQuestions().get(position);                                              //Obtenermos los valores deacuerdo al radio seleccionado

                                                        int countFoto2 = 0;
                                                        String xmlphotos2 = "";

                                                        questionResponse += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                                "<IdSet xsi:type=\"xsd:string\" />" +
                                                                "<IdQuestion xsi:type=\"xsd:string\">" + QP.getIdQuestion() + "</IdQuestion>" +
                                                                "<IdAnswer xsi:type=\"xsd:string\">" + QP.getAswer3G() + "</IdAnswer>" +
                                                                "<IdType xsi:type=\"xsd:string\">" + QP.getIdType() + "</IdType>" +
                                                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto2 + "</CountPhoto>";

                                                        if (!QP.getAswer3G().equalsIgnoreCase(""))
                                                            vacio = true;
                                                        questionResponse+=
                                                                "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                                xmlphotos2 +
                                                                "</SetPhotos>" +
                                                                "</AnswerQuestion>";

                                                        bandera = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!questionResponse.equals("") || !itemResponse.equals("")) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">" +
                                    itemResponse +
                                    questionResponse +
                                    "</SetAnswerQuestion>";
                        }
                        xml += "</RptaItem>";
                    }
                }
                xml += "</SetRptaItem>";
            }
            xml += "</SystemsRpta>";
        }

        xml += "</Request3G>" +
                "</RequestAnswerEmerg>" +
                "</urn:answerEmerg>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
       // return xml;
        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswer(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS, String ACTION) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answer" + ACTION;
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio=false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:answer" + ACTION + " soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<RequestAnswer" + ACTION + " xsi:type=\"urn:RequestAnswer" + ACTION + "\">" +
                "<Request" + ACTION + " xsi:type=\"urn:Request" + ACTION + "\">" +
                "<Header xsi:type=\"urn:Header\">" +
                "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                "</Header>";


        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaUni xsi:type=\"urn:SystemsRptaUni\">";
            for (AREA A : S.getAreas()) {
                String idArea = A.getIdArea();
                for (ITEM I : A.getItems()) {
                    String idItem = I.getIdItem();

                    String aAux = "";
                    int countFoto = 0;
                    String xmlphotos = "";

                    if (I.getFotos() != null) {

                        xml += "<SetRptaItemUni xsi:type=\"urn:SetRptaItemUni\">";
                        for (PHOTO photos : I.getFotos()) {

                            File file = new File(photos.getNamePhoto());
                            if (file.exists()) {
                                 vacio=true;
                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                        "<TitlePhoto xsi:type=\"xsd:string\">" + photos.getTitlePhoto() + "</TitlePhoto>" +
                                        "<DateTime xsi:type=\"xsd:string\">" + photos.getDateTime() + "</DateTime>" +
                                        "<CoordX xsi:type=\"xsd:string\">" + photos.getCoordX() + "</CoordX>" +
                                        "<CoordY xsi:type=\"xsd:string\">" + photos.getCoordY() + "</CoordY>" +
                                        "</Photo>";
                                countFoto += 1;
                            }
                        }
                        xml += "<IdArea xsi:type=\"xsd:string\">" + idArea + "</IdArea>" +
                                "<IdItem xsi:type=\"xsd:string\">" + idItem + "</IdItem>" +
                                "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                "<IdQuestion xsi:type=\"xsd:string\"></IdQuestion>" +
                                "<Answer xsi:type=\"xsd:string\">" + I.getAnswerFaena() + "</Answer>" +
                                "<AnswerAux xsi:type=\"xsd:string\">" + aAux + "</AnswerAux>" +
                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (countFoto > 0) {
                                   xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" + xmlphotos + "</SetPhotos>";
                                }
                        if (!I.getAnswerFaena().equalsIgnoreCase(""))
                            vacio=true;
                        xml += "</SetRptaItemUni>";

                    }

                    //String aAux = "";
                    if(I.getSetlistArrayList() != null){
                        aAux = I.getAnswerFaena();
                    }

                    xml += "<SetRptaItemUni xsi:type=\"urn:SetRptaItemUni\">" +
                            "<IdArea xsi:type=\"xsd:string\">" + idArea + "</IdArea>" +
                            "<IdItem xsi:type=\"xsd:string\">" + idItem + "</IdItem>" +
                            "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                            "<IdQuestion xsi:type=\"xsd:string\"></IdQuestion>" +
                            "<Answer xsi:type=\"xsd:string\">" + I.getAnswerFaena() + "</Answer>" +
                            "<AnswerAux xsi:type=\"xsd:string\">"+aAux+"</AnswerAux>" +
                            "</SetRptaItemUni>";
                    if (!I.getAnswerFaena().equalsIgnoreCase(""))
                        vacio=true;

                    if (I.getQuestions() != null) {
                        for (int i = 0; i < I.getQuestions().size(); i++) {
                            QUESTION Q = I.getQuestions().get(i);

                            xml += "<SetRptaItemUni xsi:type=\"urn:SetRptaItemUni\">" +
                                    "<IdArea xsi:type=\"xsd:string\">" + idArea + "</IdArea>" +
                                    "<IdItem xsi:type=\"xsd:string\">" + idItem + "</IdItem>" +
                                    "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                    "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                    "<Answer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</Answer>" +
                                    "<AnswerAux xsi:type=\"xsd:string\"></AnswerAux>" +
                                    "</SetRptaItemUni>";
                            if (!Q.getAswer3G().equalsIgnoreCase(""))
                                vacio=true;
                        }
                    }
                    if (I.getSetlistArrayList() != null) {
                        String ans = I.getAnswerFaena();
                        if(!ans.equals("")) {
                            int n = Integer.parseInt(ans);
                            for (int i = 0; i < n; i++) {
                                ArrayList<SET> setList = I.getSetlistArrayList().get(i);
                                for (SET set : setList) {
                                    if (set.getQuestions() != null) {
                                        for (QUESTION Q : set.getQuestions()) {
                                            xml += "<SetRptaItemUni xsi:type=\"urn:SetRptaItemUni\">" +
                                                    "<IdArea xsi:type=\"xsd:string\">" + idArea + "</IdArea>" +
                                                    "<IdItem xsi:type=\"xsd:string\">" + idItem + "</IdItem>" +
                                                    "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                    "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                    "<Answer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</Answer>" +
                                                    "<AnswerAux xsi:type=\"xsd:string\">" + (i + 1) + "</AnswerAux>" +
                                                    "</SetRptaItemUni>";
                                            if (!Q.getAswer3G().equalsIgnoreCase(""))
                                                vacio=true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            xml += "</SystemsRptaUni>";
        }
        xml += "</Request" + ACTION + ">" +
                "</RequestAnswer" + ACTION + ">" +
                "</urn:answer" + ACTION + ">" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    //Editado por S G

    public static String sendAnswerTransport(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerTransport";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerTransport soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerTransport xsi:type=\"urn:RequestAnswerTransport\">" +
                        "<RequestTrans xsi:type=\"urn:RequestTrans\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaTrans xsi:type=\"urn:SystemsRptaTrans\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItemTrans xsi:type=\"urn:SetRptaItemTrans\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItemTrans xsi:type=\"urn:RptaItemTrans\">" +
                                "<IdArea xsi:type=\"xsd:string\">"+ A.getIdArea() +"</IdArea>"+
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                        String itemResponse = "";
                        String questionResponse = "";

                        if (I.getQuestions() != null) {
                            //xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio=true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }

                                questionResponse += "<AnswerQuestionTrans xsi:type=\"urn:AnswerQuestionTrans\">" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (!Q.getAswer3G().equalsIgnoreCase(""))
                                    vacio=true;

                                questionResponse+=
                                        "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>";

                                if (Q.getIdQuestion().equalsIgnoreCase("349")) {
                                    questionResponse += "<AnswerAdic xsi:type=\"urn:AnswerAdic\">" +
                                            "<IdQuestion xsi:type=\"xsd:string\">" + Q.getValues().get(1).getQuestions().get(0).getIdQuestion() + "</IdQuestion>" +
                                            "<IdAnswer xsi:type=\"xsd:string\">" + Q.getValues().get(1).getQuestions().get(0).getAswer3G() + "</IdAnswer>" +
                                            "<IdType xsi:type=\"xsd:string\">" + Q.getValues().get(1).getQuestions().get(0).getIdType() + "</IdType>" +
                                            "</AnswerAdic>";
                                }

                                questionResponse += "</AnswerQuestionTrans>";
                            }

                        }

                        if (!questionResponse.equals("") || !itemResponse.equals("")) {
                            xml += "<SetAnswerQuestionTrans xsi:type=\"urn:SetAnswerQuestionTrans\">" +
                                    itemResponse +
                                    questionResponse +
                                    "</SetAnswerQuestionTrans>";
                        }


                        if (I.getSetArrayList() != null && I.getValues() != null) {

                            if (I.getIdType().equals(Constantes.RADIO)) {
                                xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                                String answerXML = "";

                                RadioGroup rg = (RadioGroup) I.getView();
                                int selected = rg.getCheckedRadioButtonId();

                                if (selected != -1) {
                                    vacio=true;
                                    RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                    int position = rg.indexOfChild(btn) + 1;

                                    xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + position + "</CountAnswerSet>";
                                    for (int i = 0; i < position; i++) {
                                        answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">";
                                        answerXML += "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(i).getNameValue() + "</IdValue>";
                                        if (I.getSetArrayList() != null) {
                                            answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                            ArrayList<SET> repeat = I.getSetlistArrayList().get(i);
                                            for (SET set : repeat) {
                                                if (set.getQuestions() != null) {
                                                    for (QUESTION Q : set.getQuestions()) {
                                                        int countFoto = 0;
                                                        String xmlphotos = "";
                                                        if (Q.getFoto() != null) {
                                                            countFoto += 1;
                                                            PHOTO photo = Q.getFoto();
                                                            File file = new File(photo.getNamePhoto());
                                                            if (file.exists()) {
                                                                vacio=true;
                                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                        "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                        "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                        "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                        "</Photo>";
                                                            }
                                                        }
                                                        if (Q.getFotos() != null) {
                                                            for (PHOTO p : Q.getFotos()) {
                                                                File file = new File(p.getNamePhoto());
                                                                if (file.exists()) {
                                                                    vacio=true;
                                                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                            "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                            "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                            "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                            "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                            "</Photo>";
                                                                    countFoto += 1;
                                                                }
                                                            }
                                                        }
                                                        answerXML += "<AnswerQuestionTrans xsi:type=\"urn:AnswerQuestionTrans\">" +
                                                                "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                                "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                                "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                                                "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                        if (!Q.getAswer3G().equalsIgnoreCase(""))
                                                            vacio=true;

                                                        answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                                xmlphotos +
                                                                "</SetPhotos>";

                                                        answerXML += "</AnswerQuestionTrans>";
                                                    }

                                                }

                                            }
                                        }
                                        answerXML += "</SetAnswerQuestionTrans>";
                                        answerXML += "</SetAnswer>";

                                    }

                                }
                                xml += answerXML;
                                xml += "</SetAnswerSet>";
                            }

                        }
                        xml += "</RptaItemTrans>";
                    }

                }
                xml += "</SetRptaItemTrans>";
            }
            xml += "</SystemsRptaTrans>";
        }

        xml += "</RequestTrans>" +
                "</RequestAnswerTransport>" +
                "</urn:answerTransport>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswerSG(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerSystem";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio=false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerSystem soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerTransport xsi:type=\"urn:RequestAnswerTransport\">" +
                        "<RequestTrans xsi:type=\"urn:RequestTrans\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaTrans xsi:type=\"urn:SystemsRptaTrans\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItemTrans xsi:type=\"urn:SetRptaItemTrans\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItemTrans xsi:type=\"urn:RptaItemTrans\">" +
                                "<IdArea xsi:type=\"xsd:string\">"+ A.getIdArea() +"</IdArea>"+
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                        String itemResponse = "";
                        String questionResponse = "";


                        if (I.getQuestions() != null) {
                            //xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio=true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }

                                questionResponse += "<AnswerQuestionTrans xsi:type=\"urn:AnswerQuestionTrans\">" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";

                                if (!Q.getAswer3G().equalsIgnoreCase(""))
                                    vacio=true;
                                questionResponse+=
                                        "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>";
                                // Anexando bloque entero
                                questionResponse +="<AnswerAdic xsi:type=\"urn:AnswerAdic\">" +
                                        "<IdQuestion xsi:type=\"xsd:string\">"+ Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" +  Q.getAswer3G() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" +  Q.getIdType() + "</IdType>" +
                                        "</AnswerAdic>"+
                                        "</AnswerQuestionTrans>";
                                if (!Q.getAswer3G().equalsIgnoreCase(""))
                                    vacio=true;
                            }

                        }

                        if (!questionResponse.equals("") || !itemResponse.equals("")) {
                            xml += "<SetAnswerQuestionTrans xsi:type=\"urn:SetAnswerQuestionTrans\">" +
                                    itemResponse +
                                    questionResponse +
                                    "</SetAnswerQuestionTrans>";
                        }


                        if (I.getSetArrayList() != null && I.getValues() != null) {

                            if (I.getIdType().equals(Constantes.RADIO)) {
                                xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                                String answerXML = "";

                                RadioGroup rg = (RadioGroup) I.getView();
                                int selected = rg.getCheckedRadioButtonId();

                                if (selected != -1) {
                                    vacio=true;
                                    RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                    int position = rg.indexOfChild(btn) + 1;

                                    xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + position + "</CountAnswerSet>";
                                    for (int i = 0; i < position; i++) {
                                        answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">";
                                        answerXML += "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(i).getNameValue() + "</IdValue>";
                                        if (I.getSetArrayList() != null) {
                                            answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                            ArrayList<SET> repeat = I.getSetlistArrayList().get(i);
                                            for (SET set : repeat) {
                                                if (set.getQuestions() != null) {
                                                    for (QUESTION Q : set.getQuestions()) {
                                                        int countFoto = 0;
                                                        String xmlphotos = "";
                                                        if (Q.getFoto() != null) {
                                                            countFoto += 1;
                                                            PHOTO photo = Q.getFoto();
                                                            File file = new File(photo.getNamePhoto());
                                                            if (file.exists()) {
                                                                vacio=true;
                                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                        "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                        "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                        "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                        "</Photo>";
                                                            }
                                                        }
                                                        if (Q.getFotos() != null) {
                                                            for (PHOTO p : Q.getFotos()) {
                                                                File file = new File(p.getNamePhoto());
                                                                if (file.exists()) {
                                                                    vacio=true;
                                                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                            "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                            "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                            "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                            "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                            "</Photo>";
                                                                    countFoto += 1;
                                                                }
                                                            }
                                                        }
                                                        answerXML += "<AnswerQuestionTrans xsi:type=\"urn:AnswerQuestionTrans\">" +
                                                                "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                                "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                                "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                                                "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                        if (!Q.getAswer3G().equalsIgnoreCase(""))
                                                            vacio=true;

                                                        answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                                xmlphotos +
                                                                "</SetPhotos>";
                                                        // Anexando bloque entero
                                                        answerXML +="<AnswerAdic xsi:type=\"urn:AnswerAdic\">" +
                                                                "<IdQuestion xsi:type=\"xsd:string\">"+ Q.getIdQuestion() + "</IdQuestion>" +
                                                                "<IdAnswer xsi:type=\"xsd:string\">" +  Q.getAswer3G() + "</IdAnswer>" +
                                                                "<IdType xsi:type=\"xsd:string\">" +  Q.getIdType() + "</IdType>" +
                                                                "</AnswerAdic>";

                                                        answerXML += "</AnswerQuestionTrans>";

                                                    }

                                                }

                                            }
                                        }
                                        answerXML += "</SetAnswerQuestionTrans>";
                                        answerXML += "</SetAnswer>";

                                    }
                                }
                                xml += answerXML;
                                xml += "</SetAnswerSet>";
                            }

                        }
                        xml += "</RptaItemTrans>";
                    }

                }
                xml += "</SetRptaItemTrans>";
            }
            xml += "</SystemsRptaTrans>";
        }

        xml += "</RequestTrans>" +
                "</RequestAnswerTransport>" +
                "</urn:answerSystem>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

       /* StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);*/


        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswerDC(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerDC";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerDC soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerDC xsi:type=\"urn:RequestAnswerDC\">" +
                        "<RequestDC xsi:type=\"urn:RequestDC\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaDC xsi:type=\"urn:SystemsRptaDC\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItemDC xsi:type=\"urn:SetRptaItemDC\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItemDC xsi:type=\"urn:RptaItemDC\">" +
                                "<IdArea xsi:type=\"xsd:string\">" + A.getIdArea() + "</IdArea>" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";
                        if (I.getQuestions() != null) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio=true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto()  + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }


                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdSet xsi:type=\"xsd:string\">" + "1" + "</IdSet>" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                    vacio=true;

                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>";
                                xml += "</AnswerQuestion>";
                            }
                            xml += "</SetAnswerQuestion>";
                        }

                        if (I.getSetArrayList() != null && I.getValues() != null) {
                            xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                            String answerXML = "";
                            int count = 0;
                            for (CheckBox checkBox : I.getCheckBoxes()) {
                                if (checkBox.isChecked()) {
                                    vacio=true;
                                    count += 1;
                                    int posChecked = I.getCheckBoxes().indexOf(checkBox);
                                    answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">" +
                                            "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(posChecked).getNameValue() + "</IdValue>" +
                                            "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                    ArrayList<SET> repeat = I.getSetlistArrayList().get(posChecked);
                                    for (SET set : repeat) {
                                        //answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                                        if (set.getQuestions() != null) {
                                            for (QUESTION Q : set.getQuestions()) {
                                                int countFoto = 0;
                                                String xmlphotos = "";
                                                if (Q.getFoto() != null) {
                                                    countFoto += 1;
                                                    PHOTO photo = Q.getFoto();
                                                    File file = new File(photo.getNamePhoto());
                                                    if (file.exists()) {
                                                        vacio=true;
                                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                "</Photo>";
                                                    }
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO p : Q.getFotos()) {
                                                        File file = new File(p.getNamePhoto());
                                                        if (file.exists()) {
                                                            vacio=true;
                                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto()  + "</TitlePhoto>" +
                                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                    "</Photo>";
                                                            countFoto += 1;
                                                        }
                                                    }
                                                }
                                                answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                        "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                                    vacio=true;
                                                answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                        xmlphotos +
                                                        "</SetPhotos>";
                                                answerXML += "</AnswerQuestion>";
                                            }

                                        }

                                    }
                                    answerXML += "</SetAnswerQuestion>";
                                    answerXML += "</SetAnswer>";
                                }
                            }
                            xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + count + "</CountAnswerSet>";
                            xml += answerXML;
                            xml += "</SetAnswerSet>";
                        }
                        xml += "</RptaItemDC>";
                    }


                }
                xml += "</SetRptaItemDC>";
            }
            xml += "</SystemsRptaDC>";
        }

        xml += "</RequestDC>" +
                "</RequestAnswerDC>" +
                "</urn:answerDC>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

     /*   StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);


        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;*/
        if (vacio) {
            return xml;
        }else{
            return "false";
        }
    }

    public static String sendAnswerAir(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerAir";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerAir soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerIden xsi:type=\"urn:RequestAnswerIden\">" +
                        "<RequestIden xsi:type=\"urn:RequestIden\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRpta xsi:type=\"urn:SystemsRpta\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItem xsi:type=\"urn:SetRptaItem\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItem xsi:type=\"urn:RptaItem\">" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";
                        if (I.getQuestions() != null) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }
                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdSet xsi:type=\"xsd:string\">" + "1"  + "</IdSet>" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";

                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>";
                                xml += "</AnswerQuestion>";
                            }
                            xml += "</SetAnswerQuestion>";
                        }

                        if (I.getSetArrayList() != null && I.getValues() != null) {
                            xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                            String answerXML = "";
                            int count = 0;
                            for (CheckBox checkBox : I.getCheckBoxes()) {
                                if (checkBox.isChecked()) {
                                    vacio=true;
                                    count += 1;
                                    int posChecked = I.getCheckBoxes().indexOf(checkBox);
                                    answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">" +
                                            "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(posChecked).getNameValue() + "</IdValue>" +
                                            "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                    ArrayList<SET> repeat = I.getSetlistArrayList().get(posChecked);
                                    for (SET set : repeat) {
                                        //answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                                        if (set.getQuestions() != null) {
                                            for (QUESTION Q : set.getQuestions()) {
                                                int countFoto = 0;
                                                String xmlphotos = "";
                                                if (Q.getFoto() != null) {
                                                    countFoto += 1;
                                                    PHOTO photo = Q.getFoto();
                                                    File file = new File(photo.getNamePhoto());
                                                    if (file.exists()) {
                                                        vacio=true;
                                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                "</Photo>";
                                                    }
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO p : Q.getFotos()) {
                                                        File file = new File(p.getNamePhoto());
                                                        if (file.exists()) {
                                                            vacio=true;
                                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                    "</Photo>";
                                                            countFoto += 1;
                                                        }
                                                    }
                                                }
                                                answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                        "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                                    vacio=true;
                                                answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                        xmlphotos +
                                                        "</SetPhotos>";
                                                answerXML += "</AnswerQuestion>";
                                            }
                                        }
                                    }
                                    answerXML += "</SetAnswerQuestion>";
                                    answerXML += "</SetAnswer>";
                                }
                            }
                            xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + count + "</CountAnswerSet>";
                            xml += answerXML;
                            xml += "</SetAnswerSet>";
                        }
                        xml += "</RptaItem>";
                    }
                }
                xml += "</SetRptaItem>";
            }
            xml += "</SystemsRpta>";
        }

        xml += "</RequestIden>" +
                "</RequestAnswerIden>" +
                "</urn:answerAir>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswerGE(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerGE";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio=false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerGE soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerGE xsi:type=\"urn:RequestAnswerGE\">" +
                        "<RequestIden xsi:type=\"urn:RequestIden\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";

        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRpta xsi:type=\"urn:SystemsRpta\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";

            if (S.getAreas() != null) {
                xml += "<SetRptaItem xsi:type=\"urn:SetRptaItem\">";

                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItem xsi:type=\"urn:RptaItem\">" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                        if (I.getQuestions() != null) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }
                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio=true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }

                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                    vacio=true;

                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>";
                                xml += "</AnswerQuestion>";
                            }
                            xml += "</SetAnswerQuestion>";
                        }

                        if (I.getSetArrayList() != null && I.getValues() != null) {
                            if (I.getIdType().equals(Constantes.RADIO)) {
                                xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                                String answerXML = "";

                                RadioGroup rg = (RadioGroup) I.getView();
                                int selected = rg.getCheckedRadioButtonId();

                                if (selected != -1) {
                                    vacio=true;
                                    RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                    int position = rg.indexOfChild(btn) + 1;

                                    xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + position + "</CountAnswerSet>";
                                    for (int i = 0; i < position; i++) {
                                        answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">";
                                        answerXML += "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(i).getNameValue() + "</IdValue>";
                                        if (I.getSetArrayList() != null) {
                                            answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                            ArrayList<SET> repeat = I.getSetlistArrayList().get(i);
                                            for (SET set : repeat) {
                                                if (set.getQuestions() != null) {
                                                    for (QUESTION Q : set.getQuestions()) {
                                                        int countFoto = 0;
                                                        String xmlphotos = "";
                                                        if (Q.getFoto() != null) {
                                                            countFoto += 1;
                                                            PHOTO photo = Q.getFoto();
                                                            File file = new File(photo.getNamePhoto());
                                                            if (file.exists()) {
                                                                vacio=true;
                                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                        "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                        "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                        "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                        "</Photo>";
                                                            }
                                                        }
                                                        if (Q.getFotos() != null) {
                                                            for (PHOTO p : Q.getFotos()) {
                                                                File file = new File(p.getNamePhoto());
                                                                if (file.exists()) {
                                                                    vacio=true;
                                                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                            "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                            "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                            "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                            "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                            "</Photo>";
                                                                    countFoto += 1;
                                                                }
                                                            }
                                                        }
                                                        answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                                "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                                "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                                "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                                                "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                        if (!Q.getAswer3G().equalsIgnoreCase(""))
                                                            vacio=true;

                                                        answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                                xmlphotos +
                                                                "</SetPhotos>";
                                                        answerXML += "</AnswerQuestion>";
                                                    }
                                                }

                                            }
                                        }
                                        answerXML += "</SetAnswerQuestion>";
                                        answerXML += "</SetAnswer>";
                                    }
                                }
                                xml += answerXML;
                                xml += "</SetAnswerSet>";
                            }
                        }
                        xml += "</RptaItem>";
                    }

                }
                xml += "</SetRptaItem>";
            }
            xml += "</SystemsRpta>";
        }

        xml += "</RequestIden>" +
                "</RequestAnswerGE>" +
                "</urn:answerGE>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswerAC(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerAC";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerAC soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerDC xsi:type=\"urn:RequestAnswerDC\">" +
                        "<RequestDC xsi:type=\"urn:RequestDC\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaDC xsi:type=\"urn:SystemsRptaDC\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItemDC xsi:type=\"urn:SetRptaItemDC\">";
                for (AREA A : S.getAreas()) {
                    int cont = 0;
                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItemDC xsi:type=\"urn:RptaItemDC\">" +
                                "<IdArea xsi:type=\"xsd:string\">" + A.getIdArea() + "</IdArea>" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                        if (I.getQuestions().size() > 0) {

                            for (QUESTION Q : I.getQuestions()) {

                                    if (Q.getIdType().equals(Constantes.RADIO)) {

                                        if (Q.getQuestions() != null) {

                                        xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                                        String answerXML = "";

                                        RadioGroup rg = (RadioGroup) Q.getView();
                                        int selected = rg.getCheckedRadioButtonId();

                                        if (selected != -1) {
                                            vacio=true;
                                            RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                            int position = rg.indexOfChild(btn) + 1;

                                            xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + position + "</CountAnswerSet>";
                                            for (int i = 0; i < position; i++) {
                                                answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">";
                                                answerXML += "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(i).getNameValue() + "</IdValue>";

                                                answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                                for (QUESTION QQ : Q.getValues().get(i).getQuestions()) {

                                                    int countFoto = 0;
                                                    String xmlphotos = "";
                                                    if (QQ.getFoto() != null) {
                                                        countFoto += 1;
                                                        PHOTO photo = Q.getFoto();
                                                        File file = new File(photo.getNamePhoto());
                                                        if (file.exists()) {
                                                            vacio = true;
                                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                    "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                    "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                    "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                    "</Photo>";
                                                        }
                                                    }
                                                    if (QQ.getFotos() != null) {
                                                        for (PHOTO p : QQ.getFotos()) {
                                                            File file = new File(p.getNamePhoto());
                                                            if (file.exists()) {
                                                                vacio = true;
                                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                        "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                        "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                        "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                        "</Photo>";
                                                                countFoto += 1;
                                                            }
                                                        }
                                                    }
                                                    answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                            "<IdSet xsi:type=\"xsd:string\">" + "" + "</IdSet>" +
                                                            "<IdQuestion xsi:type=\"xsd:string\">" + QQ.getIdQuestion() + "</IdQuestion>" +
                                                            "<IdAnswer xsi:type=\"xsd:string\">" + QQ.getAswer3G() + "</IdAnswer>" +
                                                            "<IdType xsi:type=\"xsd:string\">" + QQ.getIdType() + "</IdType>" +
                                                            "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                    if (!QQ.getAswer3G().equalsIgnoreCase(""))
                                                        vacio = true;

                                                    if (QQ.getFotos() != null) {
                                                        answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                                xmlphotos +
                                                                "</SetPhotos>";
                                                    }

                                                    answerXML += "</AnswerQuestion>";

                                                }

                                                answerXML += "</SetAnswerQuestion>";
                                                answerXML += "</SetAnswer>";

                                            }
                                        }
                                        xml += answerXML;
                                        xml += "</SetAnswerSet>";
                                    }
                                }
                            }

                            if (I.getQuestions().get(cont).getValues() != null && I.getQuestions().get(cont).getQuestions() == null) {
                                xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                                for (QUESTION Q : I.getQuestions()) {
                                    int countFoto = 0;
                                    String xmlphotos = "";
                                    if (Q.getFoto() != null) {
                                        countFoto += 1;
                                        PHOTO photo = Q.getFoto();
                                        File file = new File(photo.getNamePhoto());
                                        if (file.exists()) {
                                            vacio = true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                        }
                                    }
                                    if (Q.getFotos() != null) {
                                        for (PHOTO p : Q.getFotos()) {
                                            File file = new File(p.getNamePhoto());
                                            if (file.exists()) {
                                                vacio=true;
                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                        "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                        "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                        "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                        "</Photo>";
                                                countFoto += 1;
                                                }
                                            }

                                        }

                                    xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                            "<IdSet xsi:type=\"xsd:string\">" + "" + "</IdSet>" +
                                            "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                            "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                            "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                            "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                    if (!Q.getAswer3G().equalsIgnoreCase(""))
                                        vacio = true;

                                    if (Q.getFotos() != null) {
                                        xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                xmlphotos +
                                                "</SetPhotos>";
                                    }
                                    xml += "</AnswerQuestion>";

                                }
                                xml += "</SetAnswerQuestion>";
                            }

                            if (I.getQuestions().get(cont).getValues() == null) {
                                xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                                for (QUESTION Q : I.getQuestions()) {
                                    int countFoto = 0;
                                    String xmlphotos = "";
                                    if (Q.getFoto() != null) {
                                        countFoto += 1;
                                        PHOTO photo = Q.getFoto();
                                        File file = new File(photo.getNamePhoto());
                                        if (file.exists()) {
                                            vacio = true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                        }
                                    }
                                    if (Q.getFotos() != null) {
                                        for (PHOTO p : Q.getFotos()) {
                                            File file = new File(p.getNamePhoto());
                                            if (file.exists()) {
                                                vacio=true;
                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                        "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                        "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                        "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                        "</Photo>";
                                                countFoto += 1;
                                            }
                                        }
                                    }
                                    xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                            "<IdSet xsi:type=\"xsd:string\">" + "" + "</IdSet>" +
                                            "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                            "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                            "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                            "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                    if (!Q.getAswer3G().equalsIgnoreCase(""))
                                        vacio = true;

                                    if (Q.getFotos() != null) {
                                        xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                xmlphotos +
                                                "</SetPhotos>";
                                    }
                                    xml += "</AnswerQuestion>";

                                }
                                xml += "</SetAnswerQuestion>";
                            }
                                }

                        xml += "</RptaItemDC>";
                    }


                }
                xml += "</SetRptaItemDC>";
            }
            xml += "</SystemsRptaDC>";
        }

        xml += "</RequestDC>" +
                "</RequestAnswerDC>" +
                "</urn:answerAC>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        /*StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);


        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;*/
        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswer3G(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answer3G";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answer3G soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswer3G xsi:type=\"urn:RequestAnswerIden\">" +
                        "<Request3G xsi:type=\"urn:Request3G\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRpta xsi:type=\"urn:SystemsRpta\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItem xsi:type=\"urn:SetRptaItem\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItem xsi:type=\"urn:RptaItem\">" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                        String itemResponse = "";
                        String questionResponse = "";

                        if (I.getSetArrayList() == null && I.getValues() != null) {
                            //vacio = true;
                            itemResponse += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                    "<!--Optional:-->" +
                                    "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                    "<IdQuestion xsi:type=\"xsd:string\"></IdQuestion>" +
                                    "<IdAnswer xsi:type=\"xsd:string\">" + I.getAnswer3G() + "</IdAnswer>" +
                                    "<CountPhoto xsi:type=\"xsd:string\">0</CountPhoto>" +
                                    "</AnswerQuestion>";
                            if (!I.getAnswer3G().equalsIgnoreCase(""))
                                vacio = true;
                        }

                        if (I.getQuestions() != null) {
                            //xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio =true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio = true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }

                                questionResponse += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";

                                if (!Q.getAswer3G().equalsIgnoreCase(""))
                                    vacio = true;
                                questionResponse+= "<SetPhotos xsi:type=\"urn:SetPhotos\">" + xmlphotos + "</SetPhotos>" +
                                        "</AnswerQuestion>";
                            }
                        }

                        if (!questionResponse.equals("") || !itemResponse.equals("")) {

                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">" +
                                    itemResponse +
                                    questionResponse +
                                    "</SetAnswerQuestion>";
                        }

                        if (I.getSetArrayList() != null && I.getValues() != null) {

                            if (I.getIdType().equals(Constantes.RADIO)) {
                                xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                                String answerXML = "";

                                RadioGroup rg = (RadioGroup) I.getView();
                                int selected = rg.getCheckedRadioButtonId();

                                if (selected != -1) {
                                    vacio=true;
                                    RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                    int position = rg.indexOfChild(btn) + 1;

                                    xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + position + "</CountAnswerSet>";
                                    for (int i = 0; i < position; i++) {
                                        answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">";
                                        answerXML += "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(i).getNameValue() + "</IdValue>";
                                        if (I.getSetArrayList() != null) {
                                            answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                            ArrayList<SET> repeat = I.getSetlistArrayList().get(i);
                                            for (SET set : repeat) {
                                                if (set.getQuestions() != null) {
                                                    for (QUESTION Q : set.getQuestions()) {
                                                        int countFoto = 0;
                                                        String xmlphotos = "";
                                                        if (Q.getFoto() != null) {
                                                            countFoto += 1;
                                                            PHOTO photo = Q.getFoto();
                                                            File file = new File(photo.getNamePhoto());
                                                            if (file.exists()) {
                                                                vacio = true;
                                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                        "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                        "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                        "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                        "</Photo>";
                                                            }
                                                        }
                                                        if (Q.getFotos() != null) {
                                                            for (PHOTO p : Q.getFotos()) {
                                                                File file = new File(p.getNamePhoto());
                                                                if (file.exists()) {
                                                                    vacio= true;
                                                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                            "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                            "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                            "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                            "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                            "</Photo>";
                                                                    countFoto += 1;
                                                                }
                                                            }
                                                        }
                                                        answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                                "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                                "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                                "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                                                "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                        if (!Q.getAswer3G().equalsIgnoreCase(""))
                                                            vacio = true;
                                                        answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                                xmlphotos +
                                                                "</SetPhotos>";
                                                        answerXML += "</AnswerQuestion>";
                                                    }

                                                }

                                            }
                                        }
                                        answerXML += "</SetAnswerQuestion>";
                                        answerXML += "</SetAnswer>";

                                    }

                                }
                                xml += answerXML;
                                xml += "</SetAnswerSet>";
                            }

                        }
                        xml += "</RptaItem>";
                    }


                }
                xml += "</SetRptaItem>";
            }
            xml += "</SystemsRpta>";
        }

        xml += "</Request3G>" +
                "</RequestAnswer3G>" +
                "</urn:answer3G>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        //return xml;
        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswerWIMAX(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerWimax";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio=false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerWimax soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerDC xsi:type=\"urn:RequestAnswerDC\">" +
                        "<RequestDC xsi:type=\"urn:RequestDC\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaDC xsi:type=\"urn:SystemsRptaDC\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItemDC xsi:type=\"urn:SetRptaItemDC\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItemDC xsi:type=\"urn:RptaItemDC\">" +
                                "<IdArea xsi:type=\"xsd:string\">" + A.getIdArea() + "</IdArea>" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                        int countFoto = 0;
                        String xmlphotos = "";

                        vacio = true;

                        if (!I.getAnswer3G().equalsIgnoreCase("") || I.getFotos() != null) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                        }

                        if (I.getFotos() != null) {
                            for (PHOTO p : I.getFotos()) {
                                File file = new File(p.getNamePhoto());
                                if (file.exists()) {
                                    vacio = true;
                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    countFoto += 1;
                                }
                            }
                        }

                        if (I.getFotos() != null) {
                            xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                    "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                    "<IdQuestion xsi:type=\"xsd:string\"></IdQuestion>" +
                                    "<IdAnswer xsi:type=\"xsd:string\">PHOTO</IdAnswer>" +
                                    "<IdType xsi:type=\"xsd:string\">"+I.getIdType()+"</IdType>" +
                                    "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                            if (countFoto > 0){
                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" + xmlphotos + "</SetPhotos>";
                            }
                            xml += "</AnswerQuestion>"+
                                    "</SetAnswerQuestion>";

                        }

                        if (!I.getAnswer3G().equalsIgnoreCase("")) {
                            xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                    "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                    "<IdQuestion xsi:type=\"xsd:string\"></IdQuestion>" +
                                    "<IdAnswer xsi:type=\"xsd:string\">" + I.getAnswerFaena() + "</IdAnswer>" +
                                    "<IdType xsi:type=\"xsd:string\">" + I.getIdType() + "</IdType>" +
                                    "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";

                            if (countFoto > 0){
                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" + xmlphotos + "</SetPhotos>";
                            }

                            xml += "</AnswerQuestion>"+
                                  "</SetAnswerQuestion>";
                        }

                        if (I.getQuestions() != null) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {

                                if (Q.getIdType().equalsIgnoreCase(Constantes.RADIO)) {

                                    RadioGroup rg = (RadioGroup) Q.getView();
                                    int selected = rg.getCheckedRadioButtonId();

                                    //if (selected != -1) {
                                        vacio = true;

                                        xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                                "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                                "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>" +
                                                "</AnswerQuestion>";

                                        for (VALUE VQ : Q.getValues()) {
                                            if (VQ.getQuestions() != null) {
                                                for (QUESTION Q2 : VQ.getQuestions()) {
                                                    if (Q2.getIdType().equalsIgnoreCase(Constantes.RADIO)) {
                                                        rg = (RadioGroup) Q2.getView();
                                                        if (rg != null) {
                                                            selected = rg.getCheckedRadioButtonId();
                                                            //if (selected != -1) {
                                                                vacio = true;

                                                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                                        "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q2.getIdQuestion() + "</IdQuestion>" +
                                                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q2.getAswer3G() + "</IdAnswer>" +
                                                                        "<IdType xsi:type=\"xsd:string\">" + Q2.getIdType() + "</IdType>" +
                                                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>" +
                                                                        "</AnswerQuestion>";

                                                                for (VALUE VQ2 : Q2.getValues()) {
                                                                    if (VQ2.getQuestions() != null) {
                                                                        for (QUESTION Q22 : VQ2.getQuestions()) {
                                                                            if (Q22.getIdType().equalsIgnoreCase(Constantes.RADIO)) {
                                                                                rg = (RadioGroup) Q22.getView();
                                                                                if (rg != null) {
                                                                                    selected = rg.getCheckedRadioButtonId();
                                                                                   // if (selected != -1 || selected == 1) {
                                                                                        vacio = true;

                                                                                        xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                                                                "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                                                                                "<IdQuestion xsi:type=\"xsd:string\">" + Q22.getIdQuestion() + "</IdQuestion>" +
                                                                                                "<IdAnswer xsi:type=\"xsd:string\">" + Q22.getAswer3G() + "</IdAnswer>" +
                                                                                                "<IdType xsi:type=\"xsd:string\">" + Q22.getIdType() + "</IdType>" +
                                                                                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>" +
                                                                                                "</AnswerQuestion>";
                                                                                    //}
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                             //   }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                     //   }
                                    }

                                }

                            }


                            xml += "</SetAnswerQuestion>";
                        }
                        xml += "</RptaItemDC>";
                    }
                }
                xml += "</SetRptaItemDC>";
            }
            xml += "</SystemsRptaDC>";
        }

        xml += "</RequestDC>" +
                "</RequestAnswerDC>" +
                "</urn:answerWimax>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        // return xml;
        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }

    public static String sendAnswerAgregator(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerAgregator";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:answerAgregator soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<RequestAnswerDC xsi:type=\"urn:RequestAnswerDC\">" +
                        "<RequestDC xsi:type=\"urn:RequestDC\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                        "</Header>" +
                        "<!--Optional:-->";
        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaDC xsi:type=\"urn:SystemsRptaDC\">" +
                    "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
            if (S.getAreas() != null) {
                xml += "<SetRptaItemDC xsi:type=\"urn:SetRptaItemDC\">";
                for (AREA A : S.getAreas()) {

                    for (ITEM I : A.getItems()) {
                        xml += "<RptaItemDC xsi:type=\"urn:RptaItemDC\">" +
                                "<IdArea xsi:type=\"xsd:string\">" + A.getIdArea() + "</IdArea>" +
                                "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";
                        if (I.getQuestions() != null) {
                            xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                            for (QUESTION Q : I.getQuestions()) {
                                int countFoto = 0;
                                String xmlphotos = "";
                                if (Q.getFoto() != null) {
                                    countFoto += 1;
                                    PHOTO photo = Q.getFoto();
                                    File file = new File(photo.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                    }
                                }

                                if (Q.getFotos() != null) {
                                    for (PHOTO p : Q.getFotos()) {
                                        File file = new File(p.getNamePhoto());
                                        if (file.exists()) {
                                            vacio=true;
                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto()  + "</TitlePhoto>" +
                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                    "</Photo>";
                                            countFoto += 1;
                                        }
                                    }
                                }


                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdSet xsi:type=\"xsd:string\">" + "1" + "</IdSet>" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                    vacio=true;

                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                        xmlphotos +
                                        "</SetPhotos>";
                                xml += "</AnswerQuestion>";
                            }
                            xml += "</SetAnswerQuestion>";
                        }

                        if (I.getSetArrayList() != null && I.getValues() != null) {
                            xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                            String answerXML = "";
                            int count = 0;
                            for (CheckBox checkBox : I.getCheckBoxes()) {
                                if (checkBox.isChecked()) {
                                    vacio=true;
                                    count += 1;
                                    int posChecked = I.getCheckBoxes().indexOf(checkBox);
                                    answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">" +
                                            "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(posChecked).getNameValue() + "</IdValue>" +
                                            "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                    ArrayList<SET> repeat = I.getSetlistArrayList().get(posChecked);
                                    for (SET set : repeat) {
                                        //answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                                        if (set.getQuestions() != null) {
                                            for (QUESTION Q : set.getQuestions()) {
                                                int countFoto = 0;
                                                String xmlphotos = "";
                                                if (Q.getFoto() != null) {
                                                    countFoto += 1;
                                                    PHOTO photo = Q.getFoto();
                                                    File file = new File(photo.getNamePhoto());
                                                    if (file.exists()) {
                                                        vacio=true;
                                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                "</Photo>";
                                                    }
                                                }
                                                if (Q.getFotos() != null) {
                                                    for (PHOTO p : Q.getFotos()) {
                                                        File file = new File(p.getNamePhoto());
                                                        if (file.exists()) {
                                                            vacio=true;
                                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto()  + "</TitlePhoto>" +
                                                                    "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                    "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                    "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                    "</Photo>";
                                                            countFoto += 1;
                                                        }
                                                    }
                                                }
                                                answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                        "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                                    vacio=true;
                                                answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                        xmlphotos +
                                                        "</SetPhotos>";
                                                answerXML += "</AnswerQuestion>";
                                            }

                                        }

                                    }
                                    answerXML += "</SetAnswerQuestion>";
                                    answerXML += "</SetAnswer>";
                                }
                            }
                            xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + count + "</CountAnswerSet>";
                            xml += answerXML;
                            xml += "</SetAnswerSet>";
                        }
                        xml += "</RptaItemDC>";
                    }


                }
                xml += "</SetRptaItemDC>";
            }
            xml += "</SystemsRptaDC>";
        }

        xml += "</RequestDC>" +
                "</RequestAnswerDC>" +
                "</urn:answerAgregator>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

     /*   StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);


        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;*/
        if (vacio) {
            return xml;
        }else{
            return "false";
        }
    }

    public static String sendAnswerSemestral(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#answerSemestral";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();
        boolean vacio=false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:answerSemestral soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<RequestAnswerFaena xsi:type=\"urn:RequestAnswerFaena\">" +
                "<RequestFaena xsi:type=\"urn:RequestFaena\">" +
                "<Header xsi:type=\"urn:Header\">" +
                "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                "</Header>";


        for (SYSTEM S : SYSTEMS) {
            xml += "<SystemsRptaUni xsi:type=\"urn:SystemsRptaUni\">";
            for (AREA A : S.getAreas()) {
                String idArea = A.getIdArea();
                for (ITEM I : A.getItems()) {
                    String idItem = I.getIdItem();
                    //xml += "<SetRptaItemUni xsi:type=\"urn:SetRptaItemUni\">";
                    if (I.getQuestions() != null) {

                        for (QUESTION Q : I.getQuestions()) {
                            xml += "<SetRptaItemUni xsi:type=\"urn:SetRptaItemUni\">";
                            int countFoto = 0;
                            String xmlphotos = "";
                            if (Q.getFoto() != null) {
                                countFoto += 1;
                                PHOTO photo = Q.getFoto();
                                File file = new File(photo.getNamePhoto());
                                if (file.exists()) {
                                    vacio =true;
                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                            "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                            "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                            "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                            "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                            "</Photo>";
                                }
                            }

                            if (Q.getFotos() != null) {
                                for (PHOTO p : Q.getFotos()) {
                                    File file = new File(p.getNamePhoto());
                                    if (file.exists()) {
                                        vacio = true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                        countFoto += 1;
                                    }
                                }
                            }
                           // xml += "<SetRptaItemUni xsi:type=\"urn:SetRptaItemUni\">";
                            xml +=  "<IdArea xsi:type=\"xsd:string\">" + idArea + "</IdArea>" +
                                    "<IdItem xsi:type=\"xsd:string\">" + idItem + "</IdItem>" +
                                    "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                    "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                    "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                    "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                    "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";


                            if (!Q.getAswer3G().equalsIgnoreCase(""))
                                vacio = true;
                            if (countFoto > 0) {
                                xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" + xmlphotos + "</SetPhotos>";

                            }
                            xml += "</SetRptaItemUni>";
                        }
                        //xml += "</SetRptaItemUni>";
                    }

                }
                //xml += "</SetRptaItemUni>";
            }
            xml += "</SystemsRptaUni>";
        }
        xml += "</RequestFaena>" +
                "</RequestAnswerFaena>" +
                "</urn:answerSemestral>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        if (vacio){
            return xml;
        }
        else{
            return "false";
        }
    }


//Matias
public static String sendAnswerPdh(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
    final String SOAP_ACTION = "urn:Configurationwsdl#answerPdh";
    String response = null;
    String xml = null;
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date fecha = new Date();
    boolean vacio= false;

    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(dummy.URL_TDC);

    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    envelope.encodingStyle = SoapSerializationEnvelope.ENC;
    envelope.dotNet = false;
    envelope.implicitTypes = true;

    xml =
            "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                    "<soapenv:Header/>" +
                    "<soapenv:Body>" +
                    "<urn:answerPdh soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                    "<RequestAnswerIden xsi:type=\"urn:RequestAnswerIden\">" +
                    "<RequestIden xsi:type=\"urn:RequestIden\">" +
                    "<Header xsi:type=\"urn:Header\">" +
                    "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                    "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                    "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                    "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                    "</Header>" +
                    "<!--Optional:-->";
    for (SYSTEM S : SYSTEMS) {
        xml += "<SystemsRpta xsi:type=\"urn:SystemsRpta\">" +
                "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
        if (S.getAreas() != null) {
            xml += "<SetRptaItem xsi:type=\"urn:SetRptaItem\">";
            for (AREA A : S.getAreas()) {
                for (ITEM I : A.getItems()) {
                    xml += "<RptaItem xsi:type=\"urn:RptaItem\">" +
                            "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                    if (I.getIdItem() != null) {

                        xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                        //for (ITEM I : I.getIdItem()) {
                        int countFoto = 0;
                        String xmlphotos = "";
                        if (I.getPhoto()!= null) {
                            countFoto += 1;
                            PHOTO photo = I.getPhoto();
                            File file = new File(photo.getNamePhoto());
                            if (file.exists()) {
                                vacio = true;
                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                        "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                        "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                        "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                        "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                        "</Photo>";
                            }
                        }

                        if (I.getFotos() != null) {
                            for (PHOTO p : I.getFotos()) {
                                File file = new File(p.getNamePhoto());
                                if (file.exists()) {
                                    vacio = true;
                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                            "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                            "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                            "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                            "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                            "</Photo>";
                                    countFoto += 1;
                                }
                            }
                        }

                        xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                "<IdSet xsi:type=\"xsd:string\" />" +
                                "<IdQuestion xsi:type=\"xsd:string\"> </IdQuestion>" +
                                "<IdAnswer xsi:type=\"xsd:string\">" + I.getAnswerFaena() + "</IdAnswer>" +
                                "<IdType xsi:type=\"xsd:string\">" + I.getIdType() + "</IdType>" +
                                "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                        if (!I.getAnswerFaena().equalsIgnoreCase(""))
                            vacio = true;
                        xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                xmlphotos +
                                "</SetPhotos>";
                        xml += "</AnswerQuestion>";
                        //}
                        xml += "</SetAnswerQuestion>";
                    }

                    if (I.getSetArrayList() != null && I.getValues() != null) {

                        if (I.getIdType().equals(Constantes.RADIO)) {
                            xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                            String answerXML = "";

                            RadioGroup rg = (RadioGroup) I.getView();
                            int selected = rg.getCheckedRadioButtonId();

                            if (selected != -1) {
                                vacio=true;
                                RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                int position = rg.indexOfChild(btn) + 1;

                                xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + position + "</CountAnswerSet>";
                                for (int i = 0; i < position; i++) {
                                    answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">";
                                    answerXML += "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(i).getNameValue() + "</IdValue>";
                                    if (I.getSetArrayList() != null) {
                                        answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                        ArrayList<SET> repeat = I.getSetlistArrayList().get(i);
                                        for (SET set : repeat) {
                                            if (set.getQuestions() != null) {
                                                for (QUESTION Q : set.getQuestions()) {
                                                    int countFoto = 0;
                                                    String xmlphotos = "";
                                                    if (Q.getFoto() != null) {
                                                        countFoto += 1;
                                                        PHOTO photo = Q.getFoto();
                                                        File file = new File(photo.getNamePhoto());
                                                        if (file.exists()) {
                                                            vacio=true;
                                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                    "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                    "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                    "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                    "</Photo>";
                                                        }
                                                    }
                                                    if (Q.getFotos() != null) {
                                                        for (PHOTO p : Q.getFotos()) {
                                                            File file = new File(p.getNamePhoto());
                                                            if (file.exists()) {
                                                                vacio=true;
                                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                        "<DateTime xsi:type=\"xsd:string\">" +  p.getDateTime() + "</DateTime>" +
                                                                        "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                        "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                        "</Photo>";
                                                                countFoto += 1;
                                                            }
                                                        }
                                                    }
                                                    answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                            "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                            "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                            "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                                            "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                            "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                    if (!Q.getAswer3G().equalsIgnoreCase(""))
                                                        vacio=true;

                                                    answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                            xmlphotos +
                                                            "</SetPhotos>";

                                                    answerXML += "</AnswerQuestion>";
                                                }

                                            }

                                        }
                                    }
                                    answerXML += "</SetAnswerQuestion>";
                                    answerXML += "</SetAnswer>";

                                }

                            }
                            xml += answerXML;
                            xml += "</SetAnswerSet>";
                        }

                    }
                    xml += "</RptaItem>";
                }


            }
            xml += "</SetRptaItem>";
        }
        xml += "</SystemsRpta>";
    }

    xml += "</RequestIden>" +
            "</RequestAnswerIden>" +
            "</urn:answerPdh>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";
    //return xml;
    if (vacio ){
        return xml;
    }
    else{
        return "false";
    }
    // return "false";
}

public static String sendAnswerInspeccion(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
    final String SOAP_ACTION = "urn:Configurationwsdl#answerPdh";
    String response = null;
    String xml = null;
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date fecha = new Date();
    boolean vacio= false;

    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(dummy.URL_TDC);

    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    envelope.encodingStyle = SoapSerializationEnvelope.ENC;
    envelope.dotNet = false;
    envelope.implicitTypes = true;

    xml =
            "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                    "<soapenv:Header/>" +
                    "<soapenv:Body>" +
                    "<urn:answerInspection soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                    "<RequestAnswer3G xsi:type=\"urn:RequestAnswer3G\">" +
                    "<Request3G xsi:type=\"urn:Request3G\">" +
                    "<Header xsi:type=\"urn:Header\">" +
                    "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                    "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                    "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                    "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                    "</Header>" +
                    "<!--Optional:-->";
    for (SYSTEM S : SYSTEMS) {
        xml += "<SystemsRpta xsi:type=\"urn:SystemsRpta\">" +
                "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
        if (S.getAreas() != null) {
            xml += "<SetRptaItem xsi:type=\"urn:SetRptaItem\">";
            for (AREA A : S.getAreas()) {

                for (ITEM I : A.getItems()) {
                    xml += "<RptaItem xsi:type=\"urn:RptaItem\">" +
                            "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";

                    String itemResponse = "";
                    String questionResponse = "";

                    if (I.getQuestions() != null) {
                        //xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                        for (QUESTION Q : I.getQuestions()) {
                            int countFoto = 0;
                            String xmlphotos = "";
                            if (Q.getFoto() != null) {
                                countFoto += 1;
                                PHOTO photo = Q.getFoto();
                                File file = new File(photo.getNamePhoto());
                                if (file.exists()) {
                                    vacio =true;
                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                            "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                            "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                            "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                            "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                            "</Photo>";
                                }
                            }

                            if (Q.getFotos() != null) {
                                for (PHOTO p : Q.getFotos()) {
                                    File file = new File(p.getNamePhoto());
                                    if (file.exists()) {
                                        vacio = true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                        countFoto += 1;
                                    }
                                }
                            }

                            questionResponse += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                    //Acá falta un tag 'IdSet'
                                    "<IdSet xsi:type=\"xsd:string\"></IdSet>" +
                                    "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                    "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                    "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                    "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";

                            if (!Q.getAswer3G().equalsIgnoreCase(""))
                                vacio = true;
                            questionResponse+= "<SetPhotos xsi:type=\"urn:SetPhotos\">" + xmlphotos + "</SetPhotos>" +
                                    "</AnswerQuestion>";
                        }
                    }

                    if (!questionResponse.equals("") || !itemResponse.equals("")) {

                        xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">" +
                                itemResponse +
                                questionResponse +
                                "</SetAnswerQuestion>";
                    }

                    if (I.getSetArrayList() != null && I.getValues() != null) {

                        if (I.getIdType().equals(Constantes.RADIO)) {
                            xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";
                            String answerXML = "";

                            RadioGroup rg = (RadioGroup) I.getView();
                            int selected = rg.getCheckedRadioButtonId();

                            if (selected != -1) {
                                vacio=true;
                                RadioButton btn = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
                                int position = rg.indexOfChild(btn) + 1;

                                xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + position + "</CountAnswerSet>";
                                for (int i = 0; i < position; i++) {
                                    answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">";
                                    answerXML += "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(i).getNameValue() + "</IdValue>";
                                    if (I.getSetArrayList() != null) {
                                        answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                        ArrayList<SET> repeat = I.getSetlistArrayList().get(i);
                                        for (SET set : repeat) {
                                            if (set.getQuestions() != null) {
                                                for (QUESTION Q : set.getQuestions()) {
                                                    int countFoto = 0;
                                                    String xmlphotos = "";
                                                    if (Q.getFoto() != null) {
                                                        countFoto += 1;
                                                        PHOTO photo = Q.getFoto();
                                                        File file = new File(photo.getNamePhoto());
                                                        if (file.exists()) {
                                                            vacio = true;
                                                            xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                    "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                    "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                                    "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                                    "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                                    "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                                    "</Photo>";
                                                        }
                                                    }
                                                    if (Q.getFotos() != null) {
                                                        for (PHOTO p : Q.getFotos()) {
                                                            File file = new File(p.getNamePhoto());
                                                            if (file.exists()) {
                                                                vacio= true;
                                                                xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                        "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                        "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto() + "</TitlePhoto>" +
                                                                        "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                        "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                        "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                        "</Photo>";
                                                                countFoto += 1;
                                                            }
                                                        }
                                                    }
                                                    answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                            "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                            "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                            "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                                            "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                            "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                                    if (!Q.getAswer3G().equalsIgnoreCase(""))
                                                        vacio = true;
                                                    answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                            xmlphotos +
                                                            "</SetPhotos>";
                                                    answerXML += "</AnswerQuestion>";
                                                }

                                            }

                                        }
                                    }
                                    answerXML += "</SetAnswerQuestion>";
                                    answerXML += "</SetAnswer>";

                                }

                            }
                            xml += answerXML;
                            xml += "</SetAnswerSet>";
                        }

                    }
                    xml += "</RptaItem>";
                }


            }
            xml += "</SetRptaItem>";
        }
        xml += "</SystemsRpta>";
    }

    xml += "</Request3G>" +
            "</RequestAnswer3G>" +
            "</urn:answerInspection>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";
    //return xml;
    if (vacio ){
        return xml;
    }
    else{
        return "false";
    }
    // return "false";
}


public static String sendAnswerAnual(String IMEI, String ID_MAINTENANCE, ArrayList<SYSTEM> SYSTEMS) throws IOException {
    final String SOAP_ACTION = "urn:Configurationwsdl#answerAnual";
    String response = null;
    String xml = null;
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date fecha = new Date();
    boolean vacio = false;

    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(dummy.URL_TDC);

    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    envelope.encodingStyle = SoapSerializationEnvelope.ENC;
    envelope.dotNet = false;
    envelope.implicitTypes = true;

    xml =
            "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                    "<soapenv:Header/>" +
                    "<soapenv:Body>" +
                    "<urn:answerAnual soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                    "<RequestAnswerDC xsi:type=\"urn:RequestAnswerDC\">" +
                    "<RequestDC xsi:type=\"urn:RequestDC\">" +
                    "<Header xsi:type=\"urn:Header\">" +
                    "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                    "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                    "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                    "<Maintenance xsi:type=\"xsd:string\">" + ID_MAINTENANCE + "</Maintenance>" +
                    "</Header>" +
                    "<!--Optional:-->";
    for (SYSTEM S : SYSTEMS) {
        xml += "<SystemsRptaDC xsi:type=\"urn:SystemsRptaDC\">" +
                "<IdSystems xsi:type=\"xsd:string\">" + S.getIdSystem() + "</IdSystems>";
        if (S.getAreas() != null) {
            xml += "<SetRptaItemDC xsi:type=\"urn:SetRptaItemDC\">";
            for (AREA A : S.getAreas()) {

                for (ITEM I : A.getItems()) {
                    xml += "<RptaItemDC xsi:type=\"urn:RptaItemDC\">" +
                            "<IdArea xsi:type=\"xsd:string\">" + A.getIdArea() + "</IdArea>" +
                            "<IdItem xsi:type=\"xsd:string\">" + I.getIdItem() + "</IdItem>";
                    if (I.getQuestions() != null) {
                        xml += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                        for (QUESTION Q : I.getQuestions()) {
                            int countFoto = 0;
                            String xmlphotos = "";
                            if (Q.getFoto() != null) {
                                countFoto += 1;
                                PHOTO photo = Q.getFoto();
                                File file = new File(photo.getNamePhoto());
                                if (file.exists()) {
                                    vacio=true;
                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                            "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                            "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                            "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                            "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                            "</Photo>";
                                }
                            }

                            if (Q.getFotos() != null) {
                                for (PHOTO p : Q.getFotos()) {
                                    File file = new File(p.getNamePhoto());
                                    if (file.exists()) {
                                        vacio=true;
                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto()  + "</TitlePhoto>" +
                                                "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                "</Photo>";
                                        countFoto += 1;
                                    }
                                }
                            }

                            if(Q.getNameType().equals("DATE")){

                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdSet xsi:type=\"xsd:string\">" + "1" + "</IdSet>" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswer3G() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                    vacio=true;

                            }else{
                                xml += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                        "<IdSet xsi:type=\"xsd:string\">" + "1" + "</IdSet>" +
                                        "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                        "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                        "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                        "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                    vacio=true;
                            }

                            xml += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                    xmlphotos +
                                    "</SetPhotos>";
                            xml += "</AnswerQuestion>";
                        }
                        xml += "</SetAnswerQuestion>";
                    }

                    if (I.getSetArrayList() != null && I.getValues() != null) {
                        xml += "<SetAnswerSet xsi:type=\"urn:SetAnswerSet\">";

                        String answerXML = "";
                        int count = 0;
                        for (CheckBox checkBox : I.getCheckBoxes()) {
                            if (checkBox.isChecked()) {
                                vacio=true;
                                count += 1;
                                int posChecked = I.getCheckBoxes().indexOf(checkBox);
                                answerXML += "<SetAnswer xsi:type=\"urn:SetAnswer\">" +
                                        "<IdValue xsi:type=\"xsd:string\">" + I.getValues().get(posChecked).getNameValue() + "</IdValue>" +
                                        "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";

                                ArrayList<SET> repeat = I.getSetlistArrayList().get(posChecked);
                                for (SET set : repeat) {
                                    //answerXML += "<SetAnswerQuestion xsi:type=\"urn:SetAnswerQuestion\">";
                                    if (set.getQuestions() != null) {
                                        for (QUESTION Q : set.getQuestions()) {
                                            int countFoto = 0;
                                            String xmlphotos = "";
                                            if (Q.getFoto() != null) {
                                                countFoto += 1;
                                                PHOTO photo = Q.getFoto();
                                                File file = new File(photo.getNamePhoto());
                                                if (file.exists()) {
                                                    vacio=true;
                                                    xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                            "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                            "<TitlePhoto xsi:type=\"xsd:string\">" + photo.getTitlePhoto() + "</TitlePhoto>" +
                                                            "<DateTime xsi:type=\"xsd:string\">" + photo.getDateTime() + "</DateTime>" +
                                                            "<CoordX xsi:type=\"xsd:string\">" + photo.getCoordX() + "</CoordX>" +
                                                            "<CoordY xsi:type=\"xsd:string\">" + photo.getCoordY() + "</CoordY>" +
                                                            "</Photo>";
                                                }
                                            }
                                            if (Q.getFotos() != null) {
                                                for (PHOTO p : Q.getFotos()) {
                                                    File file = new File(p.getNamePhoto());
                                                    if (file.exists()) {
                                                        vacio=true;
                                                        xmlphotos += "<Photo xsi:type=\"urn:Photo\">" +
                                                                "<NamePhoto xsi:type=\"xsd:string\">" + file.getName() + "</NamePhoto>" +
                                                                "<TitlePhoto xsi:type=\"xsd:string\">" + p.getTitlePhoto()  + "</TitlePhoto>" +
                                                                "<DateTime xsi:type=\"xsd:string\">" + p.getDateTime() + "</DateTime>" +
                                                                "<CoordX xsi:type=\"xsd:string\">" + p.getCoordX() + "</CoordX>" +
                                                                "<CoordY xsi:type=\"xsd:string\">" + p.getCoordY() + "</CoordY>" +
                                                                "</Photo>";
                                                        countFoto += 1;
                                                    }
                                                }
                                            }
                                            answerXML += "<AnswerQuestion xsi:type=\"urn:AnswerQuestion\">" +
                                                    "<IdSet xsi:type=\"xsd:string\">" + set.getIdSet() + "</IdSet>" +
                                                    "<IdQuestion xsi:type=\"xsd:string\">" + Q.getIdQuestion() + "</IdQuestion>" +
                                                    "<IdAnswer xsi:type=\"xsd:string\">" + Q.getAswerIDEN() + "</IdAnswer>" +
                                                    "<IdType xsi:type=\"xsd:string\">" + Q.getIdType() + "</IdType>" +
                                                    "<CountPhoto xsi:type=\"xsd:string\">" + countFoto + "</CountPhoto>";
                                            if (!Q.getAswerIDEN().equalsIgnoreCase(""))
                                                vacio=true;
                                            answerXML += "<SetPhotos xsi:type=\"urn:SetPhotos\">" +
                                                    xmlphotos +
                                                    "</SetPhotos>";
                                            answerXML += "</AnswerQuestion>";
                                        }

                                    }

                                }
                                answerXML += "</SetAnswerQuestion>";
                                answerXML += "</SetAnswer>";
                            }
                        }
                        xml += "<CountAnswerSet xsi:type=\"xsd:string\">" + count + "</CountAnswerSet>";
                        xml += answerXML;
                        xml += "</SetAnswerSet>";
                    }
                    xml += "</RptaItemDC>";
                }

            }
            xml += "</SetRptaItemDC>";
        }
        xml += "</SystemsRptaDC>";
    }

    xml += "</RequestDC>" +
            "</RequestAnswerDC>" +
            "</urn:answerAnual>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    if (vacio) {
        return xml;
    }else{
        return "false";
    }
}

    public static String sendAll(String xml, String action)throws IOException {
        final String SOAP_ACTION = "urn:Configurationwsdl#" + action;
        String response = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        System.out.println("valor de xml: " + xml);
        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        return response;

    }

    //End S G


    public static String cerrarMantenimiento(String IMEI, String ID) throws Exception {
        final String SOAP_ACTION = "urn:Configurationwsdl#closeTicket";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml =
                "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                        "<soapenv:Header/>" +
                        "<soapenv:Body>" +
                        "<urn:closeTicket soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                        "<Close xsi:type=\"urn:Close\">" +
                        "<Request xsi:type=\"urn:Request\">" +
                        "<Header xsi:type=\"urn:Header\">" +
                        "<Date xsi:type=\"xsd:string\">" + formatter.format(fecha) + "</Date>" +
                        "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                        "<Imei xsi:type=\"xsd:string\">" + IMEI + "</Imei>" +
                        "<Maintenance xsi:type=\"xsd:string\">" + ID + "</Maintenance>" +
                        "</Header>" +
                        "</Request>" +
                        "</Close>" +
                        "</urn:closeTicket>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>";

        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        Log.d("RESPONSE", response);
        return response;
    }

    public static String updateApk(String IMEI) throws Exception {
        final String SOAP_ACTION = "urn:Configurationwsdl#updateApk";
        String response = null;
        String xml = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fecha = new Date();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(dummy.URL_TDC);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        xml = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Configurationwsdl\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:updateApk soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<Apk xsi:type=\"urn:Apk\">" +
                "<RequestApk xsi:type=\"urn:Request\">" +
                "<Header xsi:type=\"urn:Header\">" +
                "<Date xsi:type=\"xsd:string\">"+formatter.format(fecha)+"</Date>" +
                "<Platafform xsi:type=\"xsd:string\">MOBILE</Platafform>" +
                "<Imei xsi:type=\"xsd:string\">"+IMEI+"</Imei>" +
                "<Maintenance xsi:type=\"xsd:string\">?</Maintenance>" +
                "</Header>" +
                "</RequestApk>" +
                "</Apk>" +
                "</urn:updateApk>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";

        StringEntity se = new StringEntity(xml, HTTP.UTF_8);
        se.setContentType("text/xml");
        httpPost.addHeader(SOAP_ACTION, dummy.URL_TDC);

        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity resEntity = httpResponse.getEntity();
        response = EntityUtils.toString(resEntity);
        Log.d("RESPONSE", response);
        return response;
    }


}