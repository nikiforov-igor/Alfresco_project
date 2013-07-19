package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
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

	/**
	 * Расторжение договора
	 * @param document - основной документ
	 * @param reasonDocumentRef - стороковая ссылка на документ основание
	 */
	public void terminateContract(ScriptNode document, String reasonDocumentRef) {
        List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
        for (NodeRef additionalDocument : additionalDocuments) {
            HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
            nodeService.addAspect(additionalDocument, ContractsBeanImpl.ASPECT_PRIMARY_DOCUMENT_DELETE, aspectProps);
            nodeService.setProperty(additionalDocument, ContractsBeanImpl.PROP_PRIMARY_DOCUMENT_DELETE, true);
        }

        List<String> objects = new ArrayList<String>();
		objects.add(reasonDocumentRef);
		businessJournalService.log(document.getNodeRef(), EventCategory.EXEC_ACTION, "#initiator зафиксировал(а) факт расторжения договора #mainobject. Основанием изменения является #object1.", objects);
	}

    /**
     * Расторжение договора
     * @param document - основной документ
     */
    public void contractExecuted(ScriptNode document) {
        List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
        for (NodeRef additionalDocument : additionalDocuments) {
            HashMap<QName, Serializable> aspectProps = new HashMap<QName, Serializable>();
            nodeService.addAspect(additionalDocument, ContractsBeanImpl.ASPECT_PRIMARY_DOCUMENT_EXECUTED, aspectProps);
            nodeService.setProperty(additionalDocument, ContractsBeanImpl.PROP_PRIMARY_DOCUMENT_EXECUTED, true);
        }

        businessJournalService.log(document.getNodeRef(), EventCategory.EXEC_ACTION, "#initiator зафиксировал(а) факт исполнения договора #mainobject.");
    }

	public Scriptable getAllContractDocuments(ScriptNode document) {
		List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
		return createScriptable(additionalDocuments);
	}

    public Scriptable getAdditionalDocsByType(String typeFilter, boolean considerFilter){
        String[] types = typeFilter != null && typeFilter.length() > 0 ? typeFilter.split("\\s*,\\s"): new String[0];
        String filter = "";
        for (String type : types) {
            if (filter.length() > 0) {
                filter += " OR ";
            }
            filter += "@lecm\\-additional\\-document\\:additionalDocumentType\\-text\\-content:\"" + type + "\"";
        }
        if (filter.length() > 0) {
            filter = " AND (" + filter + ")";
        }
        if (considerFilter) {
            String username = authService.getCurrentUserName();
            if (username != null) {
                NodeRef currentEmployee = orgstructureService.getEmployeeByPerson(username);
                String typeStr = ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT.toPrefixString(namespaceService).replace(":","_");
                Map<String, Serializable> typePrefs =
                        preferenceService.getPreferences(username, DocumentService.PREF_DOCUMENTS + "." + typeStr);
                Serializable key = typePrefs.get(DocumentService.PREF_DOCUMENTS + "." + typeStr + DocumentService.PREF_DOC_LIST_AUTHOR);
                String filterKey = key != null ? (String)key : null;
                List<NodeRef> employees = new ArrayList<NodeRef>();

                if (filterKey != null) {
                    switch(DocumentService.AuthorEnum.valueOf(filterKey.toUpperCase())) {
                        case MY : {
                            employees.add(currentEmployee);
                            break;
                        }
                        case DEPARTMENT: {
                            List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(currentEmployee);
                            employees.addAll(departmentEmployees);
                            //departmentEmployees.add(employee);
                            break;
                        }
                        case FAVOURITE: {
                            break;
                        }
                        case ALL: {
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
                if (employees.size() > 0) {
                    String employeesFilter = "";
                    boolean addOR = false;
                    String authorProperty = contractService.getAuthorProperty();
                    authorProperty = authorProperty.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                    for (NodeRef employeeRef : employees) {
                        employeesFilter += (addOR ? " OR " : "") + "@" + authorProperty + ":\"" + employeeRef.toString().replace(":", "\\:") + "\"";
                        addOR = true;
                    }
                    if (employeesFilter.length() > 0) {
                        filter += " AND (" + employeesFilter + ")";
                    }
                }
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
