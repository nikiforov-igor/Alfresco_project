package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentFilter;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.FiltersManager;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {
    private ContractsBeanImpl contractService;
	protected NodeService nodeService;
    private BusinessJournalService businessJournalService;
    private OrgstructureBean orgstructureService;
    private PreferenceService preferenceService;
    private AuthenticationService authService;
    private NamespaceService namespaceService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    private static enum WhoseEnum {
        MY,
        DEPARTMENT,
        MEMBER
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

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

    /**
     * Получить количество участников договорной деятельности
     * @return
     */
    public Integer getAmountMembers() {
        return contractService.getAllMembers().size();
    }

    /**
     * Получить список участников договорной деятельности
     * @param sortColumnName сортируемый атрибут
     * @param sortAscending сортировка
     * @return список участников
     */
    public Scriptable getMembers(String sortColumnName, Boolean sortAscending) {
        return createScriptable(contractService.getAllMembers(sortColumnName, sortAscending));
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

    public NodeRef[] getContractsByFilters(String daysCount, String userFilter){
        Date now = new Date();
        Date start = null;

        if (daysCount != null &&  !"".equals(daysCount)) {
            Integer days = Integer.parseInt(daysCount);

            if (days > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, (-1) * (days - 1));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                start = calendar.getTime();
            }
        }

        List<NodeRef> employees = new ArrayList<NodeRef>();
        List<NodeRef> docs = new ArrayList<NodeRef>();

        if (userFilter != null && !"".equals(userFilter)) {
            NodeRef employee = orgstructureService.getCurrentEmployee();
            if (employee != null) {
                switch(WhoseEnum.valueOf(userFilter.toUpperCase())) {
                    case MY : {
                        employees.add(employee);
                        break;
                    }
                    case DEPARTMENT: {
                            List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(employee);
                            employees.addAll(departmentEmployees);
                            //departmentEmployees.add(employee);
                        break;
                    }
                    case MEMBER: {
                        List<AssociationRef> membersUnits =  nodeService.getSourceAssocs(employee, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE);
                        for (AssociationRef membersUnit : membersUnits) {
                            NodeRef memberUnit = membersUnit.getSourceRef();
                            List<AssociationRef> documents = nodeService.getSourceAssocs(memberUnit, DocumentMembersService.ASSOC_DOC_MEMBERS);
                            for (AssociationRef document : documents) {
                                NodeRef doc = document.getSourceRef();
                                if (!contractService.isArchive(doc)){
                                    docs.add(doc);
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        employees.add(orgstructureService.getCurrentEmployee());
                        break;
                    }
                }
            }
        }

        List<NodeRef> refs = contractService.getContractsByFilter(DocumentService.PROP_STATUS_CHANGED_DATE, start, now,
                Arrays.asList(contractService.getDraftPath(), contractService.getDocumentsFolderPath()), null, employees, docs, true);
        return refs.toArray(new NodeRef[refs.size()]);
    }

	public Scriptable getAllContractDocuments(ScriptNode document) {
		List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
		return createScriptable(additionalDocuments);
	}

    public Scriptable getAdditionalDocsByType(String typeFilter, String queryFilterId) {
        String[] types = typeFilter != null && typeFilter.length() > 0 ? typeFilter.split("\\s*,\\s") : new String[0];
        String filter = "";
        for (String type : types) {
            if (filter.length() > 0) {
                filter += " OR ";
            }
            filter += "@lecm\\-additional\\-document\\:additionalDocumentType\\-text\\-content:\"" + type + "\"";
        }

        if (filter.length() > 0) {
            filter = " (" + filter + ") ";
        }

        if (queryFilterId != null && !queryFilterId.isEmpty()) {
            String filterId = DocumentService.PREF_DOCUMENTS + "." +
                    ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT.toPrefixString(namespaceService).replaceAll(":", "_") + "." + queryFilterId;
            String currentUser = authService.getCurrentUserName();
            Map<String, Serializable> preferences = preferenceService.getPreferences(currentUser, filterId);
            String filterData = preferences.get(filterId).toString();
            String employeesFilter = "";
            DocumentFilter docFilter = FiltersManager.getFilterById(queryFilterId);
            if (docFilter != null && filterData != null && !filterData.isEmpty()) {
                employeesFilter = docFilter.getQuery((Object[])filterData.split("/"));
            }
            if (employeesFilter.length() > 0) {
                filter += " AND (" + employeesFilter + ")";
            }
        }

        List<NodeRef> additionalDocuments = this.contractService.getAdditionalDocs(filter.length() > 0 ? filter : null);
        return createScriptable(additionalDocuments);
    }

	public ScriptNode dublicateContract(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);
		NodeRef ref = new NodeRef(nodeRef);
		if (nodeService.exists(ref)) {
			NodeRef createdNode = this.contractService.dublicateContract(ref);
			if (createdNode != null) {
				return new ScriptNode(createdNode, serviceRegistry, getScope());
			}
		}
		return null;
	}
}
