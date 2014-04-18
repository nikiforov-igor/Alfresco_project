package ru.it.lecm.errands.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.beans.ErrandsServiceImpl;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.*;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:56
 */
public class ErrandsWebScriptBean extends BaseWebScript {
    public static final String EXECUTION_KEY = "Ожидает исполнения";
    public static final int DEADLINE_DAY_COUNT = 5;
    ErrandsService errandsService;

    private OrgstructureBean orgstructureService;
    private DocumentService documentService;
    private IWorkCalendar workCalendar;
    private NodeService nodeService;
    private DocumentConnectionService documentConnectionService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkCalendar(IWorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public static enum IssuedByMeEnum {
        ISSUED_ERRANDS_ALL,
        ISSUED_ERRANDS_EXECUTION,
        ISSUED_ERRANDS_EXPIRED,
        ISSUED_ERRANDS_DEADLINE,
        ISSUED_ERRANDS_ALL_IMPORTANT,
        ISSUED_ERRANDS_EXECUTION_IMPORTANT,
        ISSUED_ERRANDS_EXPIRED_IMPORTANT,
        ISSUED_ERRANDS_DEADLINE_IMPORTANT
    }

    private NamespaceService namespaceService;

	public void setErrandsService(ErrandsService errandsService) {
		this.errandsService = errandsService;
	}

	public ScriptNode getSettingsNode() {
		return new ScriptNode(errandsService.getSettingsNode(), serviceRegistry, getScope());
	}

	public ScriptNode getCurrentUserSettingsNode() {
		return new ScriptNode(errandsService.getCurrentUserSettingsNode(true), serviceRegistry, getScope());
	}

	public List<NodeRef> getAvailableExecutors() {
		return  errandsService.getAvailableExecutors();
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

    public Scriptable getMyDocumentErrands(ScriptNode document, String filter) {
        List<NodeRef> myErrands = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter,
                Arrays.asList(ErrandsService.ASSOC_ERRANDS_EXECUTOR, ErrandsService.ASSOC_ERRANDS_CONTROLLER));
        return createScriptable(myErrands);
    }

    public Scriptable getDocumentErrandsIssuedByMe(ScriptNode document, String filter) {
        List<NodeRef> errandsIssuedByMe = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter,
                Arrays.asList(ErrandsService.ASSOC_ERRANDS_INITIATOR));
        return createScriptable(errandsIssuedByMe);
    }

    public Scriptable getActiveErrands(Scriptable paths,int skipCount, int maxItems) {
        List<NodeRef> activeErrands= errandsService.getActiveErrands(getElements(Context.getCurrentContext().getElements(paths)), skipCount, maxItems);
        return createScriptable(activeErrands);
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
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

    public Scriptable getErrandsDocs(Scriptable paths,int skipCount, int maxItems) {
        return createScriptable(errandsService.getErrandsDocuments(getElements(Context.getCurrentContext().getElements(paths)), skipCount, maxItems));
    }

    /**
     * Получить список выданных текущим пользователем поручений по ключу (все, на исполнении, просроченные, с приближающимся сроком)
     * @return список поручений
     */
    public Scriptable getIssuedErrands(String filterType) {
        List<QName> types = new ArrayList<QName>(1);
        types.add(ErrandsService.TYPE_ERRANDS);

        Map<String, String> filters = DocumentStatusesFilterBean.getFilterForType(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService));

        List<String> paths = Arrays.asList(documentService.getDraftPath(), documentService.getDocumentsFolderPath());

        List<String> statuses = new ArrayList<String>();

        String defFilter = DocumentStatusesFilterBean.getDefaultFilter(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService), false);
        String sts = DocumentStatusesFilterBean.getFilterForType(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService)).get(defFilter);
        for (String status : sts.split(",")) {
            if (status != null && !status.isEmpty()) {
                statuses.add(status.trim());
            }
        }

        List<SearchParameters.SortDefinition> sort = new ArrayList<SearchParameters.SortDefinition>();
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ErrandsService.PROP_ERRANDS_IS_IMPORTANT.toString(), false));
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ErrandsService.PROP_ERRANDS_LIMITATION_DATE.toString(), false));

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

        final String PROP_ITINITATOR =
                ErrandsService.PROP_ERRANDS_INITIATOR_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXPIRED =
                ErrandsServiceImpl.PROP_ERRANDS_IS_EXPIRED.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_IMPORTANT =
                ErrandsServiceImpl.PROP_ERRANDS_IS_IMPORTANT.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXEC_DATE =
                ErrandsServiceImpl.PROP_ERRANDS_LIMITATION_DATE.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

        String issuedFilterQuery = "@" + PROP_ITINITATOR + ":\"" + currentEmployee.toString().replace(":", "\\:") + "\"";

        if (filterType != null && !"".equals(filterType)) {
                switch (IssuedByMeEnum.valueOf(filterType.toUpperCase())) {
                    //просроченные
                    case ISSUED_ERRANDS_EXPIRED_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_EXPIRED: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_EXPIRED + ":true ";
                        break;
                    }
                    // на исполнении
                    case ISSUED_ERRANDS_EXECUTION_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_EXECUTION: {
                        statuses = new ArrayList<String>();
                        String filtersStr = filters.get(EXECUTION_KEY);
                        String[] statusesArray = filtersStr.split(",");
                        for (String st:statusesArray){
                            if (st != null && !st.isEmpty()){
                                statuses.add(st.trim());
                            }
                        }

                        break;
                    }
                    //с приближающимся сроком
                    case ISSUED_ERRANDS_DEADLINE_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_DEADLINE: {
                        Date now = new Date();

                        Date deadlineDate = workCalendar.getNextWorkingDate(now, DEADLINE_DAY_COUNT, Calendar.DAY_OF_MONTH);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(deadlineDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        Date end = calendar.getTime();

                        final String MIN = DocumentService.DateFormatISO8601.format(now);
                        final String MAX = end != null ? DocumentService.DateFormatISO8601.format(end) : "MAX";

                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_EXEC_DATE + ":\"" + MIN + " \"..\"" + MAX + "\"";

                        break;
                    }
                    // все
                    case ISSUED_ERRANDS_ALL_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_ALL: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
        }

        List<NodeRef> refs = documentService.getDocumentsByFilter(types, paths, statuses, issuedFilterQuery, sort);
        return createScriptable(refs);
    }

    /**
     * Получить строку с параметрами для списка поручений. Метод используется в дашлете "Выданные мною поручения" для формирования адреса перехода по ссылкам
     * @return строка с параметрами (query=[query]&formId=[formId]&filterOver=[filterOver]#filter=[filter]
     */
    public String getIssuedFilter(String filterType) {
        StringBuilder builder = new StringBuilder();
        builder.append("query=");
        Map<String, String> filters = DocumentStatusesFilterBean.getFilterForType(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService));
        String defFilter = DocumentStatusesFilterBean.getDefaultFilter(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService), false);

        // список фильтров - по умолчанию, Все (не финальные)
        String statusesStr = filters.get(defFilter);
        String form = defFilter;

        switch (IssuedByMeEnum.valueOf(filterType.toUpperCase())) {
            // на исполнении - подменяем строку со статусами
            case ISSUED_ERRANDS_EXECUTION_IMPORTANT: {
            }
            case ISSUED_ERRANDS_EXECUTION: {
                String status = EXECUTION_KEY;
                statusesStr = filters.get(status);
                form = status;
                break;
            }
            default: {
                break;
            }
        }

        builder.append(statusesStr);
        builder.append("&formId=");
        builder.append(form);

        DocumentFilter docFilter = FiltersManager.getFilterById(filterType);
        if (docFilter != null) {
            builder.append("&filterOver=true");
            builder.append("#filter=")
                    .append(docFilter.getId())
                    .append("|")
                    .append(docFilter.getParamStr() != null ? docFilter.getParamStr() : "");
        }
        return builder.toString();
    }

    public Scriptable getAvailableEmployeesForChildErrand(String parent) {
        if (NodeRef.isNodeRef(parent)) {
            NodeRef parentRef = new NodeRef(parent);
            QName type = nodeService.getType(parentRef);
            if (type.equals(ErrandsService.TYPE_ERRANDS)) {
                Set<NodeRef> employees = new HashSet<NodeRef>();
                //соисполнители - подходят!
                List<AssociationRef> empRefs = nodeService.getTargetAssocs(parentRef, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS);
                for (AssociationRef empRef : empRefs) {
                    employees.add(empRef.getTargetRef());
                }
                // подчиненые исполнителя - подходят!
                List<AssociationRef> bossRef = nodeService.getTargetAssocs(parentRef, ErrandsService.ASSOC_ERRANDS_EXECUTOR);
                if (bossRef != null && !bossRef.isEmpty()) {
                    List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(bossRef.get(0).getTargetRef());
                    employees.addAll(departmentEmployees);
                }
                return createScriptable(new ArrayList<NodeRef>(employees));
            }
        }
        return null;
    }

    public ScriptNode getAdditionalDocument(String errandNodeRef) {
        ParameterCheck.mandatory("errandNodeRef", errandNodeRef);
        NodeRef errandRef = new NodeRef(errandNodeRef);
        NodeRef additionalDocRef = errandsService.getAdditionalDocumentNode(errandRef);

        if (additionalDocRef != null) {
            return new ScriptNode(additionalDocRef, serviceRegistry, getScope());
        }

        return null;
    }


    public ScriptNode getLinkFolder(String documentRef){
        ParameterCheck.mandatory("documentRef", documentRef);
        return new ScriptNode(errandsService.getLinksFolderRef(new NodeRef(documentRef)), serviceRegistry, getScope());
    }

    public Scriptable getLinks(String documentRef) {
        ParameterCheck.mandatory("documentRef", documentRef);
        NodeRef document = new NodeRef(documentRef);
        List<NodeRef> links = errandsService.getLinks(document);
        return createScriptable(links);
    }

    public Scriptable getLinksByAssociation(String documentRef, String association) {
        ParameterCheck.mandatory("documentRef", documentRef);
        NodeRef document = new NodeRef(documentRef);
        List<NodeRef> links = errandsService.getLinksByAssociation(document, association);
        return createScriptable(links);
    }

    public NodeRef createLinks(String nodeRef, String name, String url, boolean isExecute){
        return errandsService.createLinks(new NodeRef(nodeRef), name, url, isExecute);
    }

	public void setExecutionReport(String documentRef, String report) {
		ParameterCheck.mandatory("documentRef", documentRef);
		ParameterCheck.mandatory("report", report);

		errandsService.setExecutionReport(new NodeRef(documentRef), report);
	}

	public Scriptable getChildErrands(String documentRef) {
		ParameterCheck.mandatory("documentRef", documentRef);
		NodeRef document = new NodeRef(documentRef);
		if (nodeService.exists(document)) {
			List<NodeRef> childErrands = documentConnectionService.getConnectedDocuments(document, DocumentConnectionService.DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE, ErrandsService.TYPE_ERRANDS);
			return createScriptable(childErrands);
		}
		return null;
	}
}
