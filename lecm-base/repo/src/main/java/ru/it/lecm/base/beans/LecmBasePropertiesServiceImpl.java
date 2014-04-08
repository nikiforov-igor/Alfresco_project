package ru.it.lecm.base.beans;

import java.util.List;

/**
 * User: pmelnikov
 * Date: 03.04.14
 * Time: 13:56
 */
public class LecmBasePropertiesServiceImpl implements LecmBasePropertiesService {

    @Override
    public Object getProperty(String property) throws LecmBaseException {
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
    public List<Object> getProperties(String property) throws LecmBaseException {
        return null;
    }

    @Override
    public Boolean hasProperty(String property) throws LecmBaseException {
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
