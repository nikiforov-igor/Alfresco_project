package ru.it.lecm.base.beans;

import java.util.List;

/**
 * User: pmelnikov
 * Date: 03.04.14
 * Time: 11:12
 */
public interface LecmBasePropertiesService {

    public Object getProperty(String property) throws LecmBaseException;
    public List<Object> getProperties(String property) throws LecmBaseException;
    public Boolean hasProperty(String property) throws LecmBaseException;

}
