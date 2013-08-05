package ru.it.lecm.errands.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;
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
	ErrandsService errandsService;

    private OrgstructureBean orgstructureService;
    private DocumentService documentService;
    private IWorkCalendar workCalendar;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkCalendar(IWorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public static enum IssuedByMeEnum {
        ALL,
        EXECUTION,
        EXPIRED,
        DEADLINE
    }

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

    public List<NodeRef> getErrandsDocs(Scriptable paths,int skipCount, int maxItems) {
        return errandsService.getErrandsDocuments(getElements(Context.getCurrentContext().getElements(paths)), skipCount, maxItems);
    }

    public Scriptable getIssuedErrands(String filterType) {
        List<QName> types = new ArrayList<QName>(1);
        types.add(ErrandsService.TYPE_ERRANDS);

        List<String> paths = Arrays.asList(documentService.getDraftPathByType(ErrandsService.TYPE_ERRANDS), documentService.getDocumentsFolderPath());

        List<String> statuses = new ArrayList<String>();
        statuses.add("!Отменено");
        statuses.add("!Удалено");
        statuses.add("!Исполнено");
        statuses.add("!Не исполнено");

        List<SearchParameters.SortDefinition> sort = new ArrayList<SearchParameters.SortDefinition>();
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ErrandsService.PROP_ERRANDS_IS_IMPORTANT.toString(), false));
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ErrandsService.PROP_ERRANDS_LIMITATION_DATE.toString(), false));

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

        final String PROP_ITINITATOR =
                ErrandsService.PROP_ERRANDS_INITIATOR_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXPIRED =
                ErrandsServiceImpl.PROP_ERRANDS_IS_EXPIRED.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXEC_DATE =
                ErrandsServiceImpl.PROP_ERRANDS_LIMITATION_DATE.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

        String issuedFilterQuery = "@" + PROP_ITINITATOR + ":\"" + currentEmployee.toString().replace(":", "\\:") + "\"";

        if (filterType != null && !"".equals(filterType)) {
                switch (IssuedByMeEnum.valueOf(filterType.toUpperCase())) {
                    case EXPIRED: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_EXPIRED + ":true ";
                        break;
                    }
                    case EXECUTION: {
                        statuses = new ArrayList<String>();
                        statuses.add("Ожидает исполнения");
                        break;
                    }
                    case DEADLINE: {
                        Date now = new Date();

                        Date deadlineDate = workCalendar.getNextWorkingDate(now, 5);
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
                    case ALL: {
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
}
