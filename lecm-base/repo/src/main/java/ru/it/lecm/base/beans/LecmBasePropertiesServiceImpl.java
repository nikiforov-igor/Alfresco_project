package ru.it.lecm.base.beans;

import java.util.List;

public class LecmBasePropertiesServiceImpl implements LecmBasePropertiesService {

    private LecmBaseNamesService namesService;
    private LecmBaseValuesService valuesService;

    public void setNamesService(LecmBaseNamesService namesService){
        this.namesService = namesService;
    }

    public void setValuesService(LecmBaseValuesService valuesService){
        this.valuesService = valuesService;
    }

    @Override
    public Object getProperty(String property) throws LecmBaseException {
        Object propertyName = namesService.getPropertyName(property);
        if (propertyName != null) {
            return valuesService.getPropertyValue(property);
        }
        else {
            throw new LecmBaseException();
        }
    }

    @Override
    public List<Object> getProperties(String property) throws LecmBaseException {
        Object propertyName = namesService.getPropertyName(property);
        if (propertyName != null) {
            return valuesService.getPropertyValueList(property);
        }
        else {
            throw new LecmBaseException();
        }
    }

    @Override
    public Boolean hasProperty(String property) throws LecmBaseException {
        Object propertyName = namesService.getPropertyName(property);
        if (propertyName != null) {
            return valuesService.hasPropertyValue(property);
        }
        else {
            throw new LecmBaseException();
        }
    }

}
