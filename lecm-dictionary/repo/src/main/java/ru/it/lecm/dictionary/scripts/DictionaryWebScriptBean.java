package ru.it.lecm.dictionary.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.List;

/**
 * User: ORakovskaya
 * Date: 27.12.12
 */
public class DictionaryWebScriptBean extends BaseWebScript {

    final String BJ_NAMESPACE_URI = "http://www.it.ru/logicECM/business-journal/1.0";
    final QName PROP_OBJ_TYPE_CODE = QName.createQName(BJ_NAMESPACE_URI, "objectType-code");

    public static final String DICTIONARY_TYPE_OBJECT_NAME = "Тип объекта";
    private DictionaryBean dictionaryService;

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

	/**
	 * Получение справочника по имени
	 * @param name название справочника
	 * @return справочник
	 */
    public ScriptNode getDictionaryByName(String name) {
        NodeRef dictionary = dictionaryService.getDictionaryByName(name);

        return (dictionary == null) ? null : new ScriptNode(dictionary, serviceRegistry, getScope());
    }

	/**
	 * Полчение елементов справоника, или вложенных элементов в иерархический элемент справочника
	 * @param parent справоник или иерархический элемент справочника
	 * @return массив вложенных элементов справочника
	 */
    public Scriptable getChildren(String parent) {
        List<NodeRef> children = dictionaryService.getChildren(new NodeRef(parent));

        return createScriptable(children);
    }

    public ScriptNode getDictionaryByCode(String code) {
        NodeRef dictionary = dictionaryService.getRecordByParamValue(DICTIONARY_TYPE_OBJECT_NAME, PROP_OBJ_TYPE_CODE, code);

        return (dictionary == null) ? null : new ScriptNode(dictionary, serviceRegistry, getScope());
    }

	/**
	 * Получение корневой папки для справочников
	 * @return папка со справочниками
	 */
	public ScriptNode getRootDirectory() {
		NodeRef ref = this.dictionaryService.getDictionariesRoot();
		return new ScriptNode(ref, serviceRegistry, getScope());
	}
}
