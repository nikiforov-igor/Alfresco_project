package ru.it.lecm.eds.processors;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.SearchQueryProcessor;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by KKurets on 10.02.2017.
 */
public class FilterByDictionaryAttributeValueProcessor extends SearchQueryProcessor {

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
        Object dictionaryName = params != null ? params.get("dic") : null;
        Object attributeName = params != null ? params.get("attr") : null;
        Serializable attributeValue = params != null ? (Serializable) params.get("value") : null;

        if (dictionaryName != null && attributeName != null && attributeValue != null) {
            records = dictionaryBean.getRecordsByParamValue(dictionaryName.toString(), QName.createQName(attributeName.toString(), namespaceService), attributeValue);
            if (records != null && records.size() != 0) {
                for (int i = 0; i < records.size(); i++) {
                    sbQuery.append("\"").append(records.get(i)).append("\"");
                    if (i != (records.size() - 1)) {
                        sbQuery.append(" OR ");
                    }
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