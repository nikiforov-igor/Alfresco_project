package ru.it.lecm.statemachine;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 22.04.13
 * Time: 11:16
 */
public class TransitionResponse {

    private String redirect = null;
    private List<String> errors = new ArrayList<String>();

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
