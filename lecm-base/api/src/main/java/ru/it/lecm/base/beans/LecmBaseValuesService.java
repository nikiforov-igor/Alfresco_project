package ru.it.lecm.base.beans;

import java.util.List;

public interface LecmBaseValuesService {
    public Object getPropertyValue(String property) throws LecmBaseException;
    public List<Object> getPropertyValueList(String property) throws LecmBaseException;
    public Boolean hasPropertyValue(String property) throws LecmBaseException;
}
