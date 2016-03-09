package cl.tdc.felipe.tdc.webservice;

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
import cl.tdc.felipe.tdc.objects.ControSeguridadDiario.Elemento;
import cl.tdc.felipe.tdc.objects.ControSeguridadDiario.Modulo;
import cl.tdc.felipe.tdc.objects.ControSeguridadDiario.SubModulo;
import cl.tdc.felipe.tdc.objects.FormularioCierre.AREA;
import cl.tdc.felipe.tdc.objects.FormularioCierre.ITEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.QUESTION;
import cl.tdc.felipe.tdc.objects.FormularioCierre.SYSTEM;
import cl.tdc.felipe.tdc.objects.FormularioCierre.VALUE;
import cl.tdc.felipe.tdc.objects.MaintChecklist.Section;
import cl.tdc.felipe.tdc.objects.Seguimiento.Actividad;
import cl.tdc.felipe.tdc.objects.Seguimiento.Dia;
import cl.tdc.felipe.tdc.objects.Seguimiento.Proyecto;

public class XMLParserChecklists {

    public static String getResultCode(String XML) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(XML));

        Document doc = db.parse(is);

        String code = Funciones.getCharacterDataFromElement((Element) doc.getElementsByTagName("Code").item(0));
       // String description = Funciones.getCharacterDataFromElement((Element) doc.getElementsByTagName("Description").item(0));

       // return code + ";" + description;
        return code;
    }

    /*public static ArrayList<Modulo> getChecklistDaily(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        ArrayList<Modulo> mModulos = new ArrayList<>();


        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));
        Document doc = db.parse(is);

        NodeList modules = doc.getElementsByTagName("Module");


        for(int i = 0; i< modules.getLength(); i++){
            Modulo m = new Modulo();
            Element module = (Element) modules.item(i);

            m.setId(Funciones.getCharacterDataFromElement((Element)module.getElementsByTagName("IdModule").item(0)));
            m.setName(Funciones.getCharacterDataFromElement((Element) module.getElementsByTagName("NameModule").item(0)));

            NodeList submodules = module.getElementsByTagName("SubModules").item(0).getChildNodes();

            ArrayList<SubModulo> subModulos = new ArrayList<>();
            for(int j = 0; j<submodules.getLength(); j++){
                SubModulo sm = new SubModulo();
                Element subModule = (Element) submodules.item(j);

                sm.setId(Funciones.getCharacterDataFromElement((Element)subModule.getElementsByTagName("IdSubModule").item(0)));
                sm.setName(Funciones.getCharacterDataFromElement((Element) subModule.getElementsByTagName("NameSubModule").item(0)));

                NodeList activities = subModule.getElementsByTagName("Activities").item(0).getChildNodes();
                ArrayList<Elemento> elementos = new ArrayList<>();
                for(int k = 0; k<activities.getLength();k++){
                    Elemento e = new Elemento();
                    Element actividad = (Element) activities.item(k);

                    e.setId(Funciones.getCharacterDataFromElement((Element)actividad.getElementsByTagName("IdActivity").item(0)));
                    e.setName(Funciones.getCharacterDataFromElement((Element) actividad.getElementsByTagName("NameActivity").item(0)));
                    e.setType(Funciones.getCharacterDataFromElement((Element) actividad.getElementsByTagName("TypeActivity").item(0)));

                    if(e.getType().compareTo("CHECK")==0) {
                        NodeList values = actividad.getElementsByTagName("Values").item(0).getChildNodes();
                        ArrayList<String> valores = new ArrayList<>();

                        for(int x = 0; x<values.getLength(); x++){
                            Element v = (Element) values.item(x);
                            valores.add(Funciones.getCharacterDataFromElement((Element)v.getElementsByTagName("NameValue").item(0)));

                        }
                        e.setValues(valores);
                    }
                    elementos.add(e);

                }
                sm.setElementos(elementos);
                subModulos.add(sm);
            }

            m.setSubModulos(subModulos);
            mModulos.add(m);
        }



        return mModulos;
    }*/

    public static ArrayList<SYSTEM> getChecklistDaily(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));
        Document doc = db.parse(is);

        NodeList SYSTEMS = doc.getElementsByTagName("Systems");                                                               //Listado de nodos "System"

        ArrayList<SYSTEM> systemArrayList = new ArrayList<>();

        if(SYSTEMS.getLength() > 0) {

            SYSTEMS = SYSTEMS.item(0).getChildNodes();
            for(int i = 0; i<SYSTEMS.getLength(); i++){
                SYSTEM S = new SYSTEM();                                                                                    //Nuevo objeto SYSTEM
                Element sistema = (Element) SYSTEMS.item(i);                                                                //Obtenemos el nodo system en la posicion "i"
                S.setIdSystem(getNodeValue(sistema, "IdSystem"));
                S.setNameSystem(getNodeValue(sistema, "NameSystem"));

                NodeList AREAS = sistema.getElementsByTagName("Areas");                                                     //Listado de nodos "Area"
                ArrayList<AREA> areaArrayList = new ArrayList<>();


                if(AREAS.getLength() > 0) {
                    AREAS = AREAS.item(0).getChildNodes();
                    for (int j = 0; j < AREAS.getLength(); j++) {
                        AREA A = new AREA();                                                                                //Nuevo objeto AREA
                        Element area = (Element) AREAS.item(j);
                        A.setIdArea(getNodeValue(area, "IdArea"));
                        A.setNameArea(getNodeValue(area, "NameArea"));

                        NodeList ITEMS = area.getElementsByTagName("Items");                                               //Listado de nodos "Item"
                        ArrayList<ITEM> itemArrayList = new ArrayList<>();

                        if(ITEMS.getLength() > 0) {
                            ITEMS = ITEMS.item(0).getChildNodes();
                            for (int k = 0; k < ITEMS.getLength(); k++) {
                                ITEM I = new ITEM();
                                Element item = (Element) ITEMS.item(k);
                                I.setIdItem(getNodeValue(item, "IdItem"));
                                I.setIdType(getNodeValue(item, "IdType"));
                                I.setNameType(getNodeValue(item, "NameType"));
                                I.setNameItem(getNodeValue(item, "NameItem"));
                                I.setAnswer(getNodeValue(item, "Answer"));



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
                                                Q.setNameType(getNodeValue(question, "NameType"));
                                                Q.setPhoto(getNodeValue(question, "Photo"));
                                                Q.setNumberPhoto(getNodeValue(question, "NumberPhoto"));
                                                Q.setIdType(getNodeValue(question, "IdType"));

                                                NodeList VALUES = question.getElementsByTagName("Values");                          //Listado de nodos Values
                                                ArrayList<VALUE> valueArrayList = new ArrayList<>();



                                                ///ACA VA EL CODIGO QUE QUITÉ
                                                if (VALUES.getLength() > 0) {
                                                    VALUES = VALUES.item(0).getChildNodes();
                                                    for (int p = 0; p < VALUES.getLength(); p++) {
                                                        VALUE V = new VALUE();
                                                        Element value = (Element) VALUES.item(p);
                                                        V.setIdValue(getNodeValue(value, "IdValue"));
                                                        V.setNameValue(getNodeValue(value, "NameValue"));

                                                        NodeList QUESTIONSVALUE = value.getElementsByTagName("Questions");          //Resumen: Form Transporte tiene una pregunta con radio de respuestas

                                                        valueArrayList.add(V);
                                                    }

                                                    Q.setValues(valueArrayList);
                                                }


                                                questionArrayList.add(Q);
                                            }

                                            I.setQuestions(questionArrayList);
                                        }

                                    }

                                }

                                itemArrayList.add(I);

                            }

                            A.setItems(itemArrayList);
                        }

                        areaArrayList.add(A);
                    }

                    S.setAreas(areaArrayList);
                }

                systemArrayList.add(S);

            }

        }

        return systemArrayList;
    }

    private static String getNodeValue(Element e, String tagName){
        return Funciones.getCharacterDataFromElement((Element)e.getElementsByTagName(tagName).item(0));
    }

    public static ArrayList<cl.tdc.felipe.tdc.objects.MaintChecklist.Modulo> getChecklistMaintenance(String xmlRecords) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        ArrayList<cl.tdc.felipe.tdc.objects.MaintChecklist.Modulo> mModulos = new ArrayList<>();


        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));
        Document doc = db.parse(is);

        NodeList modules = doc.getElementsByTagName("Module");

        for(int i = 0; i< modules.getLength(); i++){
            cl.tdc.felipe.tdc.objects.MaintChecklist.Modulo m = new cl.tdc.felipe.tdc.objects.MaintChecklist.Modulo();
            Element module = (Element) modules.item(i);

            m.setId(Funciones.getCharacterDataFromElement((Element)module.getElementsByTagName("IdModule").item(0)));
            m.setName(Funciones.getCharacterDataFromElement((Element) module.getElementsByTagName("NameModule").item(0)));

            NodeList submodules = module.getElementsByTagName("SubModules").item(0).getChildNodes();

            ArrayList<cl.tdc.felipe.tdc.objects.MaintChecklist.SubModulo> subModulos = new ArrayList<>();
            for(int j = 0; j<submodules.getLength(); j++){
                cl.tdc.felipe.tdc.objects.MaintChecklist.SubModulo sm = new cl.tdc.felipe.tdc.objects.MaintChecklist.SubModulo();
                Element subModule = (Element) submodules.item(j);

                sm.setId(Funciones.getCharacterDataFromElement((Element)subModule.getElementsByTagName("IdSubModule").item(0)));
                sm.setName(Funciones.getCharacterDataFromElement((Element) subModule.getElementsByTagName("NameSubModule").item(0)));

                NodeList sections = subModule.getElementsByTagName("Sections").item(0).getChildNodes();
                ArrayList<Section> sectionArrayList = new ArrayList<>();
                for(int y = 0; y<sections.getLength(); y++) {
                    Section s = new Section();
                    Element Sections = (Element)sections.item(y);

                    s.setId(Funciones.getCharacterDataFromElement((Element)Sections.getElementsByTagName("IdSection").item(0)));
                    s.setName(Funciones.getCharacterDataFromElement((Element) Sections.getElementsByTagName("NameSection").item(0)));

                    NodeList activities = Sections.getElementsByTagName("Activities").item(0).getChildNodes();
                    ArrayList<Elemento> elementos = new ArrayList<>();
                    for (int k = 0; k < activities.getLength(); k++) {
                        Elemento e = new Elemento();
                        Element actividad = (Element) activities.item(k);

                        e.setId(Funciones.getCharacterDataFromElement((Element) actividad.getElementsByTagName("IdActivity").item(0)));
                        e.setName(Funciones.getCharacterDataFromElement((Element) actividad.getElementsByTagName("NameActivity").item(0)));
                        e.setType(Funciones.getCharacterDataFromElement((Element) actividad.getElementsByTagName("TypeActivity").item(0)));

                        if (e.getType().compareTo("CHECK") == 0) {
                            NodeList values = actividad.getElementsByTagName("Values").item(0).getChildNodes();
                            ArrayList<String> valores = new ArrayList<>();

                            for (int x = 0; x < values.getLength(); x++) {
                                Element v = (Element) values.item(x);
                                valores.add(Funciones.getCharacterDataFromElement((Element) v.getElementsByTagName("NameValue").item(0)));

                            }
                            e.setValues(valores);
                        }
                        elementos.add(e);

                    }
                    s.setElementos(elementos);
                    sectionArrayList.add(s);
                }
                sm.setSections(sectionArrayList);
                subModulos.add(sm);
            }

            m.setSubModulos(subModulos);
            mModulos.add(m);
        }


        return mModulos;
    }

}
