package ru.it.lecm.base.beans;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptVersion;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.repo.workflow.WorkflowNodeConverter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.apache.commons.lang.StringEscapeUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: mshafeev
 * Date: 25.03.13
 * Time: 18:05
 */
public abstract class BaseWebScript extends BaseScopableProcessorExtension {

    final static protected DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * Service registry
     */
    protected ServiceRegistry serviceRegistry;
	protected LecmURLService urlService;

	private WorkflowNodeConverter workflowNodeConverter;
	private final ValueConverter valueConverter = new ValueConverter();

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

	public WorkflowNodeConverter getWorkflowNodeConverter() {
		return workflowNodeConverter;
	}

	public void setWorkflowNodeConverter(WorkflowNodeConverter workflowNodeConverter) {
		this.workflowNodeConverter = workflowNodeConverter;
	}

	public void setUrlService(LecmURLService urlService) {
		this.urlService = urlService;
	}

	public ValueConverter getValueConverter() {
		return valueConverter;
	}
    /**
     * Возвращает массив, пригодный для использования в веб-скриптах
     *
     * @param refs
     * @return Scriptable
     */
    protected Scriptable createScriptable(List<NodeRef> refs) {
        Object[] results = new Object[refs.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new ScriptNode(refs.get(i), serviceRegistry, getScope());
        }
        return Context.getCurrentContext().newArray(getScope(), results);
    }

	/**
	 * Возвращает массив версий, пригодный для использования в веб-скриптах
	 *
	 * @return Scriptable
	 */
	protected Scriptable createVersionScriptable(Collection<Version> versions) {
		Object[] results = new Object[versions.size()];
		int i = 0;
		for (Version version : versions) {
			results[i++] = new ScriptVersion(version, serviceRegistry, getScope());
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}

	/**
	 * Оборачиваем узел в ссылку html страницы
	 * @param nodeRef
	 * @param description
	 * @param linkUrl
	 * @return
	 */
	public String wrapperLink(String nodeRef, String description, String linkUrl) {
		return urlService.wrapperLink(nodeRef, description, linkUrl);
	}

    public String wrapperAttribute(ScriptNode node, String description) {
        return wrapperAttribute(node.getNodeRef(), description);
    }

	public String wrapperAttribute(ScriptNode node, String description, String formId) {
		return wrapperAttribute(node.getNodeRef(), description, formId);
	}

    /**
     * Оборачиваем узел в ссылку для просмотра атрибутов
     * @param nodeRef
     * @param description
     * @return
     */
    public String wrapperAttribute(NodeRef nodeRef, String description) {
        return wrapperAttribute(nodeRef, description, null);
    }

	public String wrapperAttribute(NodeRef nodeRef, String description, String formId) {
        return "<a href=\"javascript:void(0);\" onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + nodeRef.toString() + "\', formId: \'" + formId + "\'})\">" + description + "</a>";
    }


	/**
	 * Оборачиваем узел в ссылку на view-metadata
	 * @param node
	 * @param description
	 * @return
	 */
	public String wrapperLink(ScriptNode node, String description) {
	 	return urlService.wrapperLink(node.getNodeRef().toString(), description);
	}

	/**
	 * Оборачиваем узел в ссылку на view-metadata
	 * @param nodeRef
	 * @param description
	 * @return
	 */
	public String wrapperLink(String nodeRef, String description) {
		return urlService.wrapperLink(nodeRef, description);
	}

	/**
	 * Оборачивает узел в span с title
	 * @param text
	 * @param title
	 * @return
	 */
	public String wrapperTitle(String text, String title) {
		return  "<span class=\"wrapper-title\" title=\"" + StringEscapeUtils.escapeHtml(title) + "\">" + text + "</span>";
	}

	/**
	 * Получить значение свойства share.context из global.properties
	 * @return
	 */
	public String getShareContext() {
		return urlService.getShareContext();
	}

    /**
     * Извлекает nodeRef`ы из коллекции, представленной в виде Scriptable
     * @param scriptableCollection Scriptable, являющийся коллекцией ScriptNode или строковых NodeRef
     * @return
     */
    public List<NodeRef> getNodeRefsFromScriptableCollection(Scriptable scriptableCollection) {
        List<NodeRef> objectsRefs = null;
        if (scriptableCollection != null) {
            Object[] elements = Context.getCurrentContext().getElements(scriptableCollection);
            objectsRefs = new ArrayList<NodeRef>(elements.length);
            for (Object element : elements) {
                if (element != null) {
                    if (element instanceof ScriptNode) {
                        ScriptNode obj = (ScriptNode) element;
                        objectsRefs.add(obj.getNodeRef());
                    } else if (element instanceof NativeJavaObject) {
                        Object unwrappedObj = ((NativeJavaObject) element).unwrap();
                        if (unwrappedObj != null && unwrappedObj instanceof ScriptNode) {
                            objectsRefs.add(((ScriptNode) unwrappedObj).getNodeRef());
                        }
                    } else if (element instanceof String) {
                        if (NodeRef.isNodeRef((String) element)) {
                            objectsRefs.add(new NodeRef((String) element));
                        }
                    }
                }
            }
        }
        return objectsRefs;
    }
}
