package cl.tdc.felipe.tdc.webservice;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cl.tdc.felipe.tdc.extras.Funciones;
import cl.tdc.felipe.tdc.objects.FormularioCierre.AREA;
import cl.tdc.felipe.tdc.objects.FormularioCierre.ITEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.QUESTION;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SET;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SYSTEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.VALUE;

public class XMLParserTDC {

    public static ArrayList<String> getUpdateInfo(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        ArrayList<String> models = new ArrayList<>();


        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        Element response = (Element) doc.getElementsByTagName("ResponseApk").item(0);

        models.add(getNodeValue(response, "Version"));
        models.add(getNodeValue(response, "Link"));
        models.add(getNodeValue(response, "Name"));

        return models;
        //return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }


    public static ArrayList<SYSTEM> parseFormulario(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList SYSTEMS = doc.getElementsByTagName("Systems");                                                               //Listado de nodos "System"
        ArrayList<SYSTEM> systemArrayList = new ArrayList<>();

        if(SYSTEMS.getLength() > 0){
            SYSTEMS = SYSTEMS.item(0).getChildNodes();
            for(int i = 0; i<SYSTEMS.getLength(); i++){
                SYSTEM S = new SYSTEM();                                                                                    //Nuevo objeto SYSTEM
                Element sistema = (Element) SYSTEMS.item(i);                                                                //Obtenemos el nodo system en la posicion "i"
                S.setIdSystem(getNodeValue(sistema, "IdSystem"));
                S.setNameSystem(getNodeValue(sistema, "NameSystem"));

                NodeList AREAS = sistema.getElementsByTagName("Areas");                                                     //Listado de nodos "Area"
                ArrayList<AREA> areaArrayList = new ArrayList<>();

                if(AREAS.getLength() > 0){
                    AREAS = AREAS.item(0).getChildNodes();
                    for(int j=0; j < AREAS.getLength(); j++){
                        AREA A = new AREA();                                                                                //Nuevo objeto AREA
                        Element area = (Element) AREAS.item(j);
                        A.setIdArea(getNodeValue(area, "IdArea"));
                        A.setNameArea(getNodeValue(area, "NameArea"));

                        NodeList ITEMS = area.getElementsByTagName("Items");                                               //Listado de nodos "Item"
                        ArrayList<ITEM> itemArrayList = new ArrayList<>();

                        if(ITEMS.getLength() > 0){
                            ITEMS = ITEMS.item(0).getChildNodes();
                            for(int k = 0; k<ITEMS.getLength(); k++){
                                ITEM I = new ITEM();
                                Element item = (Element) ITEMS.item(k);
                                I.setIdItem(getNodeValue(   item,   "IdItem"));
                                I.setIdType(getNodeValue(   item,   "IdType"));
                                I.setNameItem(getNodeValue( item,   "NameItem"));
                                I.setNameType(getNodeValue( item,   "NameType"));
                                I.setAnswer(getNodeValue(   item,   "Answer"));

                                /*----------------------------------------------------------------*/
                                NodeList itemChilds = item.getChildNodes();
                                for(int x = 4; x < itemChilds.getLength(); x++) {
                                    if (itemChilds.item(x).getNodeName().equals("Questions")) {
                                        NodeList QUESTIONS = itemChilds.item(x).getChildNodes();                                //Listado de nodos "Question"
                                        ArrayList<QUESTION> questionArrayList = new ArrayList<>();

                                        if (QUESTIONS.getLength() > 0) {
                                            //QUESTIONS = QUESTIONS.item(0).getChildNodes();
                                            for (int l = 0; l < QUESTIONS.getLength(); l++) {
                                                QUESTION Q = new QUESTION();
                                                Element question = (Element) QUESTIONS.item(l);
                                                Q.setIdQuestion(getNodeValue(question, "IdQuestion"));
                                                Q.setNameQuestion(getNodeValue(question, "NameQuestion"));
                                                Q.setIdType(getNodeValue(question, "IdType"));
                                                Q.setNameType(getNodeValue(question, "NameType"));
                                                Q.setPhoto(getNodeValue(question, "Photo"));
                                                Q.setNumberPhoto(getNodeValue(question, "NumberPhoto"));

                                                NodeList VALUES = question.getElementsByTagName("Values");                          //Listado de nodos Values
                                                ArrayList<VALUE> valueArrayList = new ArrayList<>();

                                                if (VALUES.getLength() > 0) {
                                                    VALUES = VALUES.item(0).getChildNodes();
                                                    for (int p = 0; p < VALUES.getLength(); p++) {
                                                        VALUE V = new VALUE();
                                                        Element value = (Element) VALUES.item(p);
                                                        V.setIdValue(getNodeValue(value, "IdValue"));
                                                        V.setNameValue(getNodeValue(value, "NameValue"));
                                                                                                                                    //Editado por George & Sarah 2-12-2015
                                                        NodeList QUESTIONSVALUE = value.getElementsByTagName("Questions");          //Resumen: Form Transporte tiene una pregunta con radio de respuestas
                                                        if (QUESTIONSVALUE.getLength()>0){                                          //uno de los radio tiene preguntas internas (Si responde No) se debe
                                                            NodeList QUESTIONVALUE = value.getElementsByTagName("Question");        //mostrar la pregunta interna.
                                                            ArrayList<QUESTION> qvArrayList = new ArrayList<>();                    //Por eso: Evaluamos si la respuesta contiene una etiqueta de pregunta
                                                            if (QUESTIONVALUE.getLength()>0) {                                      //en caso de tener pregunta interna, iteramos sobre las preguntas,
                                                                for (int qv = 0; qv < QUESTIONVALUE.getLength(); qv++) {            //al final agregamos las preguntas a un arreglo de preguntas
                                                                    QUESTION QV = new QUESTION();
                                                                    Element questionv = (Element) QUESTIONVALUE.item(qv);

                                                                    QV.setIdQuestion(getNodeValue(questionv, "IdQuestion"));
                                                                    QV.setNameQuestion(getNodeValue(questionv, "NameQuestion"));
                                                                    QV.setIdType(getNodeValue(questionv, "IdType"));
                                                                    QV.setNameType(getNodeValue(questionv, "NameType"));
                                                                    QV.setPhoto(getNodeValue(questionv, "Photo"));
                                                                    QV.setNumberPhoto(getNodeValue(questionv, "NumberPhoto"));


                                                                    NodeList VALUESQ = questionv.getElementsByTagName("Values");                          //Listado de nodos Values
                                                                    ArrayList<VALUE> valueqArrayList = new ArrayList<>();

                                                                    if (VALUESQ.getLength() > 0) {                                                         //Extraemos el valor interno
                                                                        VALUESQ = VALUESQ.item(0).getChildNodes();
                                                                        for (int pr = 0; pr < VALUESQ.getLength(); pr++) {
                                                                            VALUE VR = new VALUE();
                                                                            Element valueR = (Element) VALUESQ.item(pr);
                                                                            VR.setIdValue(getNodeValue(valueR, "IdValue"));
                                                                            VR.setNameValue(getNodeValue(valueR, "NameValue"));
                                                                            //Editado por George & Sarah 10-12-2015

                                                                            valueqArrayList.add(VR);                                                      //Agregamos V a la lista de values
                                                                        }
                                                                        QV.setValues(valueqArrayList);                                                    //Agregamos los values a la question
                                                                    }

                                                                    qvArrayList.add(QV);

                                                                }
                                                                V.setQuestions(qvArrayList);                                        //fin Sarah & George
                                                            }
                                                        }
                                                        valueArrayList.add(V);                                                      //Agregamos V a la lista de values
                                                    }
                                                    Q.setValues(valueArrayList);                                                    //Agregamos los values a la question
                                                }


                                                NodeList REPEATQ = question.getElementsByTagName("RepeatQuestion");                          //Listado de nodos RepeatQuestion
                                                ArrayList<QUESTION> repeatqArrayList = new ArrayList<>();

                                                if(REPEATQ.getLength() > 0){
                                                    REPEATQ = REPEATQ.item(0).getChildNodes();
                                                    for(int p = 0; p <REPEATQ.getLength();p++){
                                                        QUESTION V = new QUESTION();
                                                        Element questionR = (Element) REPEATQ.item(p);
                                                        V.setIdQuestion(getNodeValue(questionR, "IdQuestion"));
                                                        V.setNameQuestion(getNodeValue(questionR, "NameQuestion"));
                                                        V.setNameType(getNodeValue(questionR, "NameType"));
                                                        V.setPhoto(getNodeValue(questionR, "Photo"));
                                                        V.setNumberPhoto(getNodeValue(questionR, "NumberPhoto"));
                                                        V.setIdType(getNodeValue(questionR, "IdType"));

                                                        NodeList VALUESRR = questionR.getElementsByTagName("Values");                          //Listado de nodos Values
                                                        ArrayList<VALUE> valueArrayListR = new ArrayList<>();

                                                        if (VALUESRR.getLength() > 0) {                                                         //Extraemos el valor interno
                                                            VALUESRR = VALUESRR.item(0).getChildNodes();
                                                            for (int pr = 0; pr < VALUESRR.getLength(); pr++) {
                                                                VALUE VR = new VALUE();
                                                                Element valueR = (Element) VALUESRR.item(pr);
                                                                VR.setIdValue(getNodeValue(valueR, "IdValue"));
                                                                VR.setNameValue(getNodeValue(valueR, "NameValue"));
                                                                //Editado por George & Sarah 10-12-2015

                                                                valueArrayListR.add(VR);                                                      //Agregamos V a la lista de values
                                                            }
                                                            V.setValues(valueArrayListR);                                                    //Agregamos los values a la question
                                                        }

                                                        repeatqArrayList.add(V);                                                      //Agregamos V a la lista de values
                                                    }

                                                    Q.setQuestions(repeatqArrayList);                                                    //Agregamos los values a la question
                                                }

                                                questionArrayList.add(Q);                                                           //Agregamos Q a la lista de questions
                                            }
                                            I.setQuestions(questionArrayList);                                                      //Agregamos las questions al item
                                        }
                                        break;
                                    }
                                }
                                /*----------------------------------------------------------------*/
                                NodeList REPEAT = item.getElementsByTagName("Repeat");                                      //Listado de nodos Set
                                ArrayList<SET> setArrayList = new ArrayList<>();

                                if(REPEAT.getLength() > 0){
                                    REPEAT = REPEAT.item(0).getChildNodes();
                                    for(int l = 0; l<REPEAT.getLength(); l++){
                                        SET R = new SET();
                                        Element set = (Element) REPEAT.item(l);
                                        R.setIdSet(getNodeValue(set,"IdSet"));
                                        R.setNameSet(getNodeValue(set,"NameSet"));
                                        R.setValueSet(getNodeValue(set,"ValueSet"));

                                        NodeList QUESTIONS = set.getElementsByTagName("Questions");                                //Listado de nodos "Question"
                                        ArrayList<QUESTION> _questionArrayList = new ArrayList<>();

                                        if(QUESTIONS.getLength() > 0){
                                            QUESTIONS = QUESTIONS.item(0).getChildNodes();
                                            for(int ll = 0; ll < QUESTIONS.getLength(); ll++){
                                                QUESTION Q = new QUESTION();
                                                Element question = (Element) QUESTIONS.item(ll);
                                                Q.setIdQuestion(getNodeValue(question, "IdQuestion"));
                                                Q.setNameQuestion(getNodeValue(question, "NameQuestion"));
                                                Q.setIdType(getNodeValue(question, "IdType"));
                                                Q.setNameType(getNodeValue(question, "NameType"));
                                                Q.setPhoto(getNodeValue(question, "Photo"));
                                                Q.setNumberPhoto(getNodeValue(question, "NumberPhoto"));

                                                NodeList VALUES = question.getElementsByTagName("Values");                          //Listado de nodos Values
                                                ArrayList<VALUE> valueArrayList = new ArrayList<>();

                                                if(VALUES.getLength() > 0){
                                                    VALUES = VALUES.item(0).getChildNodes();
                                                    for(int p = 0; p<VALUES.getLength();p++){
                                                        VALUE V = new VALUE();
                                                        Element value = (Element) VALUES.item(p);
                                                        V.setIdValue(getNodeValue(value,"IdValue"));
                                                        V.setNameValue(getNodeValue(value,"NameValue"));
                                                        valueArrayList.add(V);                                                      //Agregamos V a la lista de values
                                                    }
                                                    Q.setValues(valueArrayList);                                                    //Agregamos los values a la question
                                                }

                                                _questionArrayList.add(Q);                                                           //Agregamos Q a la lista de questions
                                            }
                                            R.setQuestions(_questionArrayList);                                                      //Agregamos las questions al item

                                        }


                                        setArrayList.add(R);
                                    }
                                    I.setSetArrayList(setArrayList);
                                }
                                /*----------------------------------------------------------------*/
                                NodeList VALUES = item.getElementsByTagName("Values");                                      //Listado de values dentro de Item
                                ArrayList<VALUE> valueArrayList = new ArrayList<>();

                                if(VALUES.getLength() > 0){
                                    VALUES = VALUES.item(0).getChildNodes();
                                    for(int p = 0; p<VALUES.getLength();p++){
                                        VALUE V = new VALUE();
                                        Element value = (Element) VALUES.item(p);
                                        V.setIdValue(getNodeValue(value,"IdValue"));
                                        V.setNameValue(getNodeValue(value,"NameValue"));
                                        valueArrayList.add(V);                                                              //Agregamos V a la lista de values
                                    }
                                    I.setValues(valueArrayList);                                                            //Agregamos la lista de values al item
                                }

                                itemArrayList.add(I);                                                                       //Agregamos I a la lista de items
                            }
                            A.setItems(itemArrayList);                                                                      //Agregamos los items al area
                        }
                        areaArrayList.add(A);                                                                               //Agregamos A a la lista de areas
                    }
                    S.setAreas(areaArrayList);                                                                              //Agregamos las areas al sistema
                }
                systemArrayList.add(S);                                                                                     //Agregamos S a la lista de sistemas
            }
        }

        return systemArrayList;
        //return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }


    private static String getNodeValue(Element e, String tagName){
        return Funciones.getCharacterDataFromElement((Element)e.getElementsByTagName(tagName).item(0));
    }

}
