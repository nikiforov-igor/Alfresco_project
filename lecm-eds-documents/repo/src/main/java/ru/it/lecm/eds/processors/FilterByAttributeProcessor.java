package ru.it.lecm.eds.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.BooleanUtils;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.List;
import java.util.Map;

/**
 * Created by KKurets on 10.02.2017.
 */
public class FilterByAttributeProcessor extends SearchQueryProcessor {

    private DictionaryBean dictionaryBean;

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    /*
	 * Usage example: {{FILTER_BY_ATTRIBUTE({dic:'Типы поручений', attr:'cm:name', value:'Для информации'})}}
	 */

    @Override
    public String getQuery(Map<String, Object> params) {
        StringBuilder sbQuery = new StringBuilder();
        List<NodeRef> records;
        Object dic = params != null ? params.get("dic") : null;
        Object attr = params != null ? params.get("attr") : null;
        Object val = params != null ? params.get("value") : null;

        if (dic != null && attr != null && val != null) {
            String dictionary = dic.toString();
            String attribute = attr.toString();
            String value = val.toString();
            Object boolValue;
            if ((boolValue = BooleanUtils.toBooleanObject(value)) != null) {
                records = dictionaryBean.getRecordsByParamValue(dictionary, QName.createQName(attribute, namespaceService), (boolean) boolValue);
            } else {
                records = dictionaryBean.getRecordsByParamValue(dictionary, QName.createQName(attribute, namespaceService), value);
            }
            if (records != null && records.size() != 0) {
                for (NodeRef record : records) {
                    sbQuery.append("\"").append(record).append("\"");
                }
            } else {
                sbQuery.append("NOT_REF");
            }
        } else {
            sbQuery.append("NOT_REF");
        }
        return sbQuery.toString();
    }
}