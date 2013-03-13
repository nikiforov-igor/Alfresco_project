package ru.it.lecm.statemachine.action;

import ru.it.lecm.statemachine.StateField;

/**
 * User: pmelnikov
 * Date: 05.03.13
 * Time: 11:27
 */
public class StateFieldImpl implements StateField {

    private String name;
    private boolean isEditable;

    public StateFieldImpl(String name, boolean editable) {
        this.name = name;
        isEditable = editable;
    }

    public String getName() {
        return name;
    }

    public boolean isEditable() {
        return isEditable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateFieldImpl that = (StateFieldImpl) o;

        if (isEditable != that.isEditable) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (isEditable ? 1 : 0);
        return result;
    }
}
