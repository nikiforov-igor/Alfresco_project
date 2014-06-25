package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;

import java.io.Serializable;
import org.alfresco.repo.jscript.app.BasePropertyDecorator;
import org.alfresco.service.namespace.QName;
import org.json.simple.JSONAware;

/**
 * User: dbashmakov
 * Date: 21.05.13
 * Time: 12:20
 */
public class CustomBaseDocNameProperty extends BasePropertyDecorator {

    private DictionaryService dictionaryService;

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

	@Override
	@SuppressWarnings("unchecked")
	public JSONAware decorate(QName qname, NodeRef nodeRef, Serializable value) {
		SimpleJSONString result = new SimpleJSONString(value);
		if (dictionaryService.isSubClass(nodeService.getType(nodeRef), DocumentService.TYPE_BASE_DOCUMENT)) {
			//Получаем представление
            Serializable replaceValue = nodeService.getProperty(nodeRef, DocumentService.PROP_EXT_PRESENT_STRING);
            //Замещаем имя на представление или возвращаем обычное
            result.setValue(replaceValue != null ? replaceValue.toString().replaceAll("<.*?>","").replace("\"", "\\\"") : value);
			return result;
		}
		return result;

	}

	private class SimpleJSONString implements JSONAware {

		private Serializable value;

		public void setValue(Serializable value) {
			this.value = value;
		}

		public SimpleJSONString(Serializable value) {
			this.value = value;
		}

		@Override
		public String toJSONString() {
			return "\"" + this.value.toString() + "\"";
		}

	}
}
