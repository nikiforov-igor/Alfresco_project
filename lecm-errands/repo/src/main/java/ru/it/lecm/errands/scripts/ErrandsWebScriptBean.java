package ru.it.lecm.errands.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.errands.ErrandsService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:56
 */
public class ErrandsWebScriptBean extends BaseWebScript {
	ErrandsService errandsService;
    private NamespaceService namespaceService;

	public void setErrandsService(ErrandsService errandsService) {
		this.errandsService = errandsService;
	}

	public ScriptNode getSettingsNode() {
		return new ScriptNode(errandsService.getSettingsNode(), serviceRegistry, getScope());
	}

	public ScriptNode getCurrentUserSettingsNode() {
		return new ScriptNode(errandsService.getCurrentUserSettingsNode(), serviceRegistry, getScope());
	}

	public List<NodeRef> getAvailableInitiators() {
		return  errandsService.getAvailableInitiators();
	}

	public boolean isDefaultWithoutInitiatorApproval() {
		return  errandsService.isDefaultWithoutInitiatorApproval();
	}

	public NodeRef getDefaultInitiator() {
		return  errandsService.getDefaultInitiator();
	}

	public NodeRef getDefaultSubject() {
		return  errandsService.getDefaultSubject();
	}

	public void requestDueDateChange() {
		errandsService.requestDueDateChange();
	}

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    /**
     * Получить количество документов
     * @return количество
     */
    public Integer getAmountDocuments(Scriptable types, Scriptable paths, Scriptable statuses, boolean considerFilter) {
    return null;
    }

    private ArrayList<String> getElements(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj != null && obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj != null && obj instanceof String){
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }

    public List<NodeRef> getErrandsDocs(Scriptable paths,int skipCount, int maxItems) {
        return errandsService.getErrandsDocuments(getElements(Context.getCurrentContext().getElements(paths)), skipCount, maxItems);
    }

}
