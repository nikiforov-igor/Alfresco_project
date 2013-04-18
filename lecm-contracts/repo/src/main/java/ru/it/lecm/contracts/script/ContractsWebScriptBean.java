package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;

import java.util.List;

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
     * все договоры
     * @return
     */
    public String getAllContracts() {
        String filter = "";
        filter = " AND ((PATH:\"/app:company_home/cm:Черновики/cm:Contracts//*\"" +
                 " OR PATH:\"/app:company_home/cm:Business_x0020_platform/cm:Documents//*\"))" +
                 " AND NOT ((ASPECT:\""+"lecm\\-dic:aspect_active\""+") OR @lecm\\-dic\\:active:true)"+
                 "";

        return "" + contractService.getContracts(filter).size();
    }

    /**
     * Контракты в разработке
     * @return
     */
    public String getContractsDevelelop() {
        String filter = " AND ( (@lecm\\-statemachine\\:status:\"Черновик\" )" +
                        " OR @lecm\\-statemachine\\:status:\"Проект зарегестрирован\" " +
                        " OR @lecm\\-statemachine\\:status:\"На согласовании\" " +
                        " OR @lecm\\-statemachine\\:status:\"Согласован\" " +
                        " OR @lecm\\-statemachine\\:status:\"На доработке\" " +
                        " OR @lecm\\-statemachine\\:status:\"На подписании\" )" +
                        " AND (PATH:\"/app:company_home/cm:Черновики/cm:Contracts//*\"" +
                        " OR PATH:\"/app:company_home/cm:Business_x0020_platform/cm:Documents//*\")" +
                        " AND NOT ((ASPECT:\"lecm\\-dic\\:aspect_active\") OR @lecm\\-dic\\:active:true)";
        return "" + contractService.getContracts(filter).size();
    }

    /**
     * Активные договора
     * @return
     */
    public String getActiveContracts() {
        String filter = " AND ( @lecm\\-statemachine\\:status:\"Подписан\"" +
                        " OR @lecm\\-statemachine\\:status:\"Зарегестрирован\"" +
                        " OR @lecm\\-statemachine\\:status:\"Действует\" )" +
                        " AND (PATH:\"/app:company_home/cm:Черновики/cm:Contracts//*\"" +
                        " OR PATH:\"/app:company_home/cm:Business_x0020_platform/cm:Documents//*\")" +
                        " AND NOT ((ASPECT:\"lecm\\-dic\\:aspect_active\") OR @lecm\\-dic\\:active:true)";
        return "" + contractService.getContracts(filter).size();
    }

    /**
     * Неактивные договора
     * @return
     */
    public String getInactiveContracts() {
        String filter = " AND (PATH:\"/app:company_home/cm:Черновики/cm:Contracts//*\"" +
                        " OR PATH:\"/app:company_home/cm:Business_x0020_platform/cm:Documents//*\")" +
                        " AND NOT ((ASPECT:\"lecm\\-dic\\:aspect_active\") OR @lecm\\-dic\\:active:true)";
        return "" + contractService.getContracts(filter).size();
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
