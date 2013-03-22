package ru.it.lecm.statemachine;

import java.util.HashSet;
import java.util.Set;

/**
 * User: pmelnikov
 * Date: 22.03.13
 * Time: 11:46
 */
public class StateFields {

    private boolean hasStatemachine;
    private Set<StateField> fields = new HashSet<StateField>();

    public StateFields(boolean hasStatemachine) {
        this.hasStatemachine = hasStatemachine;
    }

    public StateFields(boolean hasStatemachine, Set<StateField> fields) {
        this.hasStatemachine = hasStatemachine;
        this.fields = fields;
    }

    public boolean hasStatemachine() {
        return hasStatemachine;
    }

    public Set<StateField> getFields() {
        return fields;
    }

}
