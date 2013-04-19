package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;

import java.util.ArrayList;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {
    private ContractsBeanImpl contractService;
	protected NodeService nodeService;

    public void setContractService(ContractsBeanImpl contractService) {
        this.contractService = contractService;
    }

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public ScriptNode getDraftRoot() {
        return new ScriptNode(contractService.getDraftRoot(), serviceRegistry, getScope());
    }

    public String getDraftPath() {
        return contractService.getDraftPath();
    }

    /**
     * Получить количество договоров
     * @param path список путей поиска
     * @param properties список значений для фильтрации
     * @return количество
     */
    public Integer getAmountContracts(Scriptable path, Scriptable properties) {
        return contractService.getContracts(getElements(Context.getCurrentContext().getElements(path)), getElements(Context.getCurrentContext().getElements(properties))).size();
    }

    private ArrayList<String> getElements(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj instanceof String){
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }

	public void createDocumentOnBasis(String typeNodeRef, String packageNodeRef) {
		if (typeNodeRef != null && packageNodeRef != null) {
			NodeRef typeRef = new NodeRef(typeNodeRef);
			NodeRef packageRef = new NodeRef(packageNodeRef);
			if (nodeService.exists(typeRef) && nodeService.exists(packageRef)) {
				NodeRef documentRef = contractService.getDocumentService().getDocumentFromPackageItems(packageRef);
				if (documentRef != null) {
					contractService.createDocumentOnBasis(typeRef, documentRef);
				}
			}
		}
	}

	public void appendDeleteReason(String reasonNodeRef, String packageNodeRef) {
		if (reasonNodeRef != null && packageNodeRef != null) {
			NodeRef reasonRef = new NodeRef(reasonNodeRef);
			NodeRef packageRef = new NodeRef(packageNodeRef);
			if (nodeService.exists(reasonRef) && nodeService.exists(packageRef)) {
				NodeRef documentRef = contractService.getDocumentService().getDocumentFromPackageItems(packageRef);
				if (documentRef != null) {
					contractService.appendDeleteReason(reasonRef, documentRef);
				}
			}
		}
	}
}
