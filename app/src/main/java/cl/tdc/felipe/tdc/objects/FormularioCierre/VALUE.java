package cl.tdc.felipe.tdc.objects.FormularioCierre;

import java.util.ArrayList;

/**
 * Created by felip on 05/11/2015.
 */
public class VALUE {
    String idValue;
    String nameValue;
    ArrayList<QUESTION> questions;

    public VALUE() {
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }

    public ArrayList<QUESTION> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QUESTION> questions) {
        this.questions = questions;
    }
}
