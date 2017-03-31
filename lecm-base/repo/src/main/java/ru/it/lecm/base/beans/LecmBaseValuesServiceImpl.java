package ru.it.lecm.base.beans;

import java.util.List;

public class LecmBaseValuesServiceImpl implements LecmBaseValuesService {

    @Override
    public Object getPropertyValue(String property) throws LecmBaseException {
        String prop = System.getProperty(property);
        if (prop == null) {
            throw new LecmBaseException();
        }
        String normalizeProp = prop.replaceAll("\\.|.", "");
        if (normalizeProp.length() > 0) {
            return  prop;
        } else {
            throw new LecmBaseException();
        }
    }

    @Override
    public List<Object> getPropertyValueList(String property) throws LecmBaseException {
        return null;
    }

    @Override
    public Boolean hasPropertyValue(String property) throws LecmBaseException {
        String prop = System.getProperty(property);
        if (prop == null) {
            throw new LecmBaseException();
        }
        String normalizeProp = prop.replaceAll("\\.|.", "");
        if (normalizeProp.length() > 0) {
            return true;
        } else {
            throw new LecmBaseException();
        }
    }
}
