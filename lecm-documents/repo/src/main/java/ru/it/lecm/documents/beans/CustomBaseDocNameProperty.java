package ru.it.lecm.documents.beans;

import org.alfresco.repo.jscript.app.PropertyDecorator;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;

/**
 * User: dbashmakov
 * Date: 21.05.13
 * Time: 12:20
 */
public class CustomBaseDocNameProperty implements PropertyDecorator {

    private NodeService nodeService = null;
    private DictionaryService dictionaryService;

    @Override
    public Serializable decorate(NodeRef nodeRef, String propertyName, Serializable value) {
        if (dictionaryService.isSubClass(nodeService.getType(nodeRef), DocumentService.TYPE_BASE_DOCUMENT)) {
            //Получаем представление
            Serializable replaceValue = nodeService.getProperty(nodeRef, DocumentService.PROP_EXT_PRESENT_STRING);
            //Замещаем имя на представление или возвращаем обычное
            return replaceValue != null ? replaceValue.toString().replaceAll("<.*?>","") : value;
        }
        return value;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
