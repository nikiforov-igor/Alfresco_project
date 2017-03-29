package ru.it.lecm.resolutions.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentStatusesFilterBean;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.resolutions.api.ResolutionsService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.*;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:43
 */
public class ResolutionsWebScriptBean extends BaseWebScript {
    private ResolutionsService resolutionsService;
    private NamespaceService namespaceService;
    private DocumentService documentService;
    private OrgstructureBean orgstructureService;
    private IWorkCalendar workCalendar;

    public static final String ON_APPROVAL_KEY = "На утверждении";
    public static final String ON_COMPLETION_KEY = "На доработке";
    public static final String ON_EXECUTION_KEY = "На исполнении";
    public static final String ON_SOLUTION_KEY = "Требуют принятия решения";
    public static final int DEADLINE_DAY_COUNT = 5;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setWorkCalendar(IWorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public static enum IssuedByMeEnum {
        ISSUED_RESOLUTIONS_ON_APPROVAL,
        ISSUED_RESOLUTIONS_ON_COMPLETION,
        ISSUED_RESOLUTIONS_ON_EXECUTION,
        ISSUED_RESOLUTIONS_ON_SOLUTION,
        ISSUED_RESOLUTIONS_EXPIRED,
        ISSUED_RESOLUTIONS_DEADLINE,
        ISSUED_RESOLUTIONS_ON_APPROVAL_CONTROL,
        ISSUED_RESOLUTIONS_ON_COMPLETION_CONTROL,
        ISSUED_RESOLUTIONS_ON_EXECUTION_CONTROL,
        ISSUED_RESOLUTIONS_ON_SOLUTION_CONTROL,
        ISSUED_RESOLUTIONS_EXPIRED_CONTROL,
        ISSUED_RESOLUTIONS_DEADLINE_CONTROL
    }

    public ResolutionsService getResolutionsService() {
        return resolutionsService;
    }

    public void setResolutionsService(ResolutionsService resolutionsService) {
        this.resolutionsService = resolutionsService;
    }

    public Scriptable getResolutionClosers(ScriptNode resolution) {
        List<NodeRef> results = resolutionsService.getResolutionClosers(resolution.getNodeRef());
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

    public void sendAnnulSignal(String resolutionRef, String reason) {
        NodeRef resolution = new NodeRef(resolutionRef);
        if (serviceRegistry.getNodeService().exists(resolution)) {
            resolutionsService.sendAnnulSignal(resolution, reason);
        }
    }
    public void resetAnnulSignal(ScriptNode doc) {
        ParameterCheck.mandatory("nodeRef", doc.getNodeRef());
        resolutionsService.resetAnnulSignal(doc.getNodeRef());
    }
    /**
     * Возвращает NodeRef настроек дашлетов для резолюций
     * @return NodeRef настроек дашлетов для резолюций
     */
    public ScriptNode getDashletSettings() {
        NodeRef settings = resolutionsService.getDashletSettingsNode();
        if (settings != null) {
            return new ScriptNode(settings, serviceRegistry, getScope());
        }
        return null;
    }

    /**
     * Получить список выданных текущим пользователем резолюций по ключу (все, на утверждении, на доработке, на исполнении, требуют принятия решения, просроченные, с приближающимся сроком)
     *
     * @return список резолюций
     */
    public Scriptable getIssuedResolutions(String filterType) {
        List<QName> types = new ArrayList<QName>(1);
        types.add(ResolutionsService.TYPE_RESOLUTION_DOCUMENT);

        Map<String, String> filters = DocumentStatusesFilterBean.getFilterForType(ResolutionsService.TYPE_RESOLUTION_DOCUMENT.toPrefixString(namespaceService));

        List<String> paths = Arrays.asList(documentService.getDocumentsFolderPath());

        List<String> statuses = new ArrayList<String>();

        String defFilter = DocumentStatusesFilterBean.getDefaultFilter(ResolutionsService.TYPE_RESOLUTION_DOCUMENT.toPrefixString(namespaceService), false);
        String sts = DocumentStatusesFilterBean.getFilterForType(ResolutionsService.TYPE_RESOLUTION_DOCUMENT.toPrefixString(namespaceService)).get(defFilter);
        for (String status : sts.split(",")) {
            if (status != null && !status.isEmpty()) {
                statuses.add(status.trim());
            }
        }

        List<SearchParameters.SortDefinition> sort = new ArrayList<SearchParameters.SortDefinition>();
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ResolutionsService.PROP_IS_ON_CONTROL.toString(), false));
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ResolutionsService.PROP_LIMITATION_DATE.toString(), false));

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

        final String PROP_AUTHOR =
                ResolutionsService.PROP_RESOLUTIONS_AUTHOR_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXPIRED =
                ResolutionsService.PROP_IS_EXPIRED.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_CONTROL =
                ResolutionsService.PROP_IS_ON_CONTROL.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXEC_DATE =
                ResolutionsService.PROP_LIMITATION_DATE.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_REQUIRE_CLOSERS_DECISION =
                ResolutionsService.PROP_REQUIRE_CLOSERS_DECISION.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

        String issuedFilterQuery = "@" + PROP_AUTHOR + ":\"" + currentEmployee.toString().replace(":", "\\:") + "\"";

        if (filterType != null && !"".equals(filterType)) {
            switch (IssuedByMeEnum.valueOf(filterType.toUpperCase())) {
                // на утверждении
                case ISSUED_RESOLUTIONS_ON_APPROVAL_CONTROL: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_CONTROL + ":true ";
                }
                case ISSUED_RESOLUTIONS_ON_APPROVAL: {
                    statuses = getResolutionsStatuses(filters, ON_APPROVAL_KEY);
                    break;
                }
                // на доработке
                case ISSUED_RESOLUTIONS_ON_COMPLETION_CONTROL: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_CONTROL + ":true ";
                }
                case ISSUED_RESOLUTIONS_ON_COMPLETION: {
                    statuses = getResolutionsStatuses(filters, ON_COMPLETION_KEY);
                    break;
                }
                // на исполнении
                case ISSUED_RESOLUTIONS_ON_EXECUTION_CONTROL: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_CONTROL + ":true ";
                }
                case ISSUED_RESOLUTIONS_ON_EXECUTION: {
                    statuses = getResolutionsStatuses(filters, ON_EXECUTION_KEY);
                    break;
                }
                //требуют принятия решения
                case ISSUED_RESOLUTIONS_ON_SOLUTION_CONTROL: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_CONTROL + ":true ";
                }
                case ISSUED_RESOLUTIONS_ON_SOLUTION: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_REQUIRE_CLOSERS_DECISION + ":true ";
                    statuses = getResolutionsStatuses(filters, ON_SOLUTION_KEY);
                    break;
                }
                //просроченные
                case ISSUED_RESOLUTIONS_EXPIRED_CONTROL: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_CONTROL + ":true ";
                }
                case ISSUED_RESOLUTIONS_EXPIRED: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_EXPIRED + ":true ";
                    break;
                }
                //с приближающимся сроком
                case ISSUED_RESOLUTIONS_DEADLINE_CONTROL: {
                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_CONTROL + ":true ";
                }
                case ISSUED_RESOLUTIONS_DEADLINE: {
                    Date now = new Date();

                    Date deadlineDate = workCalendar.getNextWorkingDate(now, DEADLINE_DAY_COUNT, Calendar.DAY_OF_MONTH);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(deadlineDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    Date end = calendar.getTime();

                    final String MIN = DateFormatISO8601.format(now);
                    final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";

                    issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_EXEC_DATE + ":[\"" + MIN + " \" TO \"" + MAX + "\"]";

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

    private List<String> getResolutionsStatuses(Map<String, String> filters, String key) {
        List<String> statuses = new ArrayList<String>();
        String filtersStr = filters.get(key);
        String[] statusesArray = filtersStr.split(",");
        for (String st : statusesArray) {
            if (st != null && !st.isEmpty()) {
                statuses.add(st.trim());
            }
        }
        return statuses;
    }
}
