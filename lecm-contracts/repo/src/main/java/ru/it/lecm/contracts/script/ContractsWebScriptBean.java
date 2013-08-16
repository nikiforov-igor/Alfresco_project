package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.preference.PreferenceService;
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
    private OrgstructureBean orgstructureService;
    private PreferenceService preferenceService;
    private AuthenticationService authService;
    private NamespaceService namespaceService;
    private DocumentService documentService;

    private final String[] contractsDocsFinalStatuses = {"Аннулирован", "Отменен", "Исполнен"};

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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

    public NodeRef[] getContractsByFilters(String daysCount, String userFilter) {
        List<QName> types = new ArrayList<QName>(2);
        types.add(ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT);
        types.add(ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);

        Date now = new Date();
        Date start = null;

        if (daysCount != null && !"".equals(daysCount)) {
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

        String filterQuery = "";

        List<NodeRef> initiatorsList = new ArrayList<NodeRef>();

        if (userFilter != null && !"".equals(userFilter)) {
            NodeRef employee = orgstructureService.getCurrentEmployee();
            if (employee != null) {
                switch (WhoseEnum.valueOf(userFilter.toUpperCase())) {
                    case MY: {
                        initiatorsList.add(employee);
                        break;
                    }
                    case DEPARTMENT: {
                        List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(employee);
                        initiatorsList.addAll(departmentEmployees);
                        break;
                    }
                    case MEMBER: {
                        final String PROP_MEMBERS =
                                DocumentMembersService.PROP_DOC_MEMBERS.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                        filterQuery += " @" + PROP_MEMBERS + ":*" + employee.toString().replace(":", "\\:") + "*";
                        break;
                    }
                    default: {
                        initiatorsList.add(orgstructureService.getCurrentEmployee());
                        break;
                    }
                }

                String employeesQuery = "";
                // фильтр по сотрудниками-создателям
                if (!initiatorsList.isEmpty()) {
                    Map<QName, List<NodeRef>> initList = new HashMap<QName, List<NodeRef>>();
                    initList.put(ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT, initiatorsList);
                    initList.put(ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, initiatorsList);

                    boolean addOR = false;
                    for (QName type : types) {
                        String authorProperty = documentService.getAuthorProperty(type);
                        authorProperty = authorProperty.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                        for (NodeRef employeeRef : initList.get(type)) {
                            employeesQuery += (addOR ? " OR " : "") + "@" + authorProperty + ":\"" + employeeRef.toString().replace(":", "\\:") + "\"";
                            addOR = true;
                        }
                    }

                    if (employeesQuery.length() > 0) {
                        filterQuery += (filterQuery.length() > 0 ? " AND (" : "") + employeesQuery + (filterQuery.length() > 0 ? ")" : "");
                    }
                }
            }
        }

        // Фильтр по датам
        final String MIN = start != null ? DocumentService.DateFormatISO8601.format(start) : "MIN";
        final String MAX = DocumentService.DateFormatISO8601.format(now);

        String property = DocumentService.PROP_STATUS_CHANGED_DATE.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        filterQuery += (filterQuery.length() > 0 ? " AND " : "") + "@" + property + ":\"" + MIN + " \"..\"" + MAX + "\"";

        List<NodeRef> refs = documentService.getDocumentsByFilter(types,
                Arrays.asList(contractService.getDraftPath(), contractService.getDocumentsFolderPath()), null, filterQuery, null);

        return refs.toArray(new NodeRef[refs.size()]);
    }

	public Scriptable getAllContractDocuments(ScriptNode document) {
		List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
		return createScriptable(additionalDocuments);
	}

    public Scriptable getAdditionalDocsByType(Scriptable paths, String typeFilter, String queryFilterId, boolean activeDocs) {
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
        List<QName> docType = new ArrayList<QName>();
        docType.add(ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);

        List<String> statuses = new ArrayList<String>();
        for (String finalStatus : contractsDocsFinalStatuses) {
            statuses.add((activeDocs ? "!" : "") + finalStatus);
        }
        List<NodeRef> additionalDocuments = this.documentService.getDocumentsByFilter(docType, getElements(Context.getCurrentContext().getElements(paths)), statuses, filter, null);
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
