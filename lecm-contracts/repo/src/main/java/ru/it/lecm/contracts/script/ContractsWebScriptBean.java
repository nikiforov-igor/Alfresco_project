package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {
    private ContractsBeanImpl contractService;
	protected NodeService nodeService;
    private BusinessJournalService businessJournalService;

    public void setContractService(ContractsBeanImpl contractService) {
        this.contractService = contractService;
    }

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
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

    public Integer getAmountMembers(Scriptable path, Scriptable properties) {
        return contractService.getAllMembers(getElements(Context.getCurrentContext().getElements(path)), getElements(Context.getCurrentContext().getElements(properties))).size();
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

	public String createDocumentOnBasis(String typeNodeRef, String packageNodeRef) {
		if (typeNodeRef != null && packageNodeRef != null) {
			NodeRef typeRef = new NodeRef(typeNodeRef);
			NodeRef packageRef = new NodeRef(packageNodeRef);
			if (nodeService.exists(typeRef) && nodeService.exists(packageRef)) {
				NodeRef documentRef = contractService.getDocumentService().getDocumentFromPackageItems(packageRef);
				if (documentRef != null) {
					return contractService.createDocumentOnBasis(typeRef, documentRef);
				}
			}
		}
        return null;
	}

    /**
     * Обозначение причины удаления документа
     *
     * @param reasonNodeRef - ссылка на запись справочника причин удаления документа
     * @param packageNodeRef - ссылка на пакет документов процесса
     */
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

    /**
     * Изменение срока договора
     * @param document - основной документ
     * @param fromDate - дата начала действия договора
     * @param toDate - дата завершения действия договора
     * @param reasonDocumentRef - стороковая ссылка на документ основание
     */
    public void setContractTime(ScriptNode document, Date fromDate, Date toDate, String reasonDocumentRef) {
        nodeService.setProperty(document.getNodeRef(), ContractsBeanImpl.TYPE_CONTRACTS_START_DATE, fromDate);
        nodeService.setProperty(document.getNodeRef(), ContractsBeanImpl.TYPE_CONTRACTS_END_DATE, toDate);

        List<String> objects = new ArrayList<String>();
        objects.add(reasonDocumentRef);
        businessJournalService.log(document.getNodeRef(), EventCategory.EXEC_ACTION, "#initiator изменил(а) срок действия договора #mainobject. Основанием изменения является данный документ #object1.", objects);
    }

	/**
	 * Расторжение договора
	 * @param document - основной документ
	 * @param reasonDocumentRef - стороковая ссылка на документ основание
	 */
	public void terminateContract(ScriptNode document, String reasonDocumentRef) {
		List<String> objects = new ArrayList<String>();
		objects.add(reasonDocumentRef);
		businessJournalService.log(document.getNodeRef(), EventCategory.EXEC_ACTION, "#initiator зафиксировал(а) факт расторжения договора #mainobject. Основанием изменения является #object1.", objects);
	}
}
