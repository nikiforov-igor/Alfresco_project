package ru.it.lecm.base.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.SubstitudeBean;

import java.util.List;

/**
 * @author dbashmakov
 *         Date: 28.12.12
 *         Time: 15:10
 */
public class SubstitudeWebScriptBean extends BaseWebScript {
    private SubstitudeBean service;

    public void setService(SubstitudeBean service) {
        this.service = service;
    }

	/**
	 * Форматирование для элемента
	 * @param node элемент для форматирования
	 * @param title форматная строка
	 * @return форматированный текст
	 */
    public String formatNodeTitle(ScriptNode node, String title) {
        return service.formatNodeTitle(node.getNodeRef(), title);
    }

	/**
	 * Форматирование для элемента
	 * @param nodeRef nodeRef элемент для форматирования
	 * @param title форматная строка
	 * @return форматированный текст
	 */
    public String formatNodeTitle(String nodeRef, String title) {
        return service.formatNodeTitle(new NodeRef(nodeRef), title);
    }

    /**
     * Получить список объектов по форматной строке
     * @param node ролдительски элемент
     * @param title форматная строка
     */
    public Scriptable getObjectsByTitle(ScriptNode node, String title) {
        List<NodeRef> results = service.getObjectsByTitle(node.getNodeRef(), title);
        return createScriptable(results);
    }

    /**
     * Получить описания объекта согласно форматной строке из справочника
     * @param nodeRef элемент
     */
    public String getObjectDescription(String nodeRef) {
		return service.getObjectDescription(new NodeRef(nodeRef));
	}

    /**
     * Получить описания объекта согласно форматной строке из справочника
     * @param node элемент
     */
    public String getObjectDescription(ScriptNode node) {
		return service.getObjectDescription(node.getNodeRef());
	}

}
