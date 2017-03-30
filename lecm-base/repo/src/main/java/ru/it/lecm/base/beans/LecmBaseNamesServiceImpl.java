package ru.it.lecm.base.beans;

public class LecmBaseNamesServiceImpl implements LecmBaseNamesService{

    @Override
    public Object getPropertyName(String property) throws LecmBaseException {
        String prop = System.getProperty(property);
        return prop != null ? prop : property;
    }
}
