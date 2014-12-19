package ru.it.lecm.orgstructure.scripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 10.09.2014
 * Time: 9:43
 */
public class OrgstrDicWebScriptBean extends AbstractWebScript {
    final private static Logger logger = LoggerFactory.getLogger(OrgstrDicWebScriptBean.class);

    public static final String NODE_REF_PARAM = "nodeRef";
    public static final String SEARCH_TERM_PARAM = "searchTerm";
    public static final String DASHLET_FORMAT_PARAM = "dashletFormat";

    private final static String EMPLOYEE_FORMAT_STRING = "{lecm-orgstr:employee-last-name} {lecm-orgstr:employee-first-name} {lecm-orgstr:employee-middle-name} " +
            "- {..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../lecm-orgstr:element-member-position-assoc/} (тел. {lecm-orgstr:employee-phone})";
    private final static String EMPLOYEE_DASHLET_FORMAT_STRING = "{lecm-orgstr:employee-last-name} {lecm-orgstr:employee-first-name} {lecm-orgstr:employee-middle-name}, " +
            "{..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../lecm-orgstr:element-member-position-assoc/}, " +
            "{..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../../lecm-orgstr:element-full-name} (тел. {lecm-orgstr:employee-phone})";

    private final static String EMPLOYEE_FIO = "{lecm-orgstr:employee-last-name} {lecm-orgstr:employee-first-name} {lecm-orgstr:employee-middle-name}";
    private final static String UNIT_FORMAT_STRING = "{lecm-orgstr:element-full-name}";

    private final static String UNITS_BY_TERM_QUERY = "TYPE:\"lecm-orgstr:organization-unit\" " +
            "AND ( @lecm\\-orgstr\\:element\\-full\\-name:{searchTerm} @lecm\\-orgstr\\:element\\-short\\-name:{searchTerm}) " +
            "AND @lecm\\-dic\\:active:true";

    private final static String EMPLOYEES_BY_TERM_QUERY = "TYPE:\"lecm-orgstr:employee\" " +
            "AND (@lecm\\-orgstr\\:employee\\-last\\-name:{searchTerm} @lecm\\-orgstr\\:employee\\-middle\\-name:{searchTerm} @lecm\\-orgstr\\:employee\\-email:{searchTerm} " +
            "@lecm\\-orgstr\\:employee\\-first\\-name:{searchTerm} @lecm\\-orgstr\\:employee\\-person\\-login:{searchTerm} @lecm\\-orgstr\\:employee\\-phone:{searchTerm} @lecm\\-orgstr\\:employee\\-positions:{searchTerm})" +
			"AND @lecm\\-dic\\:active:true";

    private static final String TITLE = "title";
    private static final String LABEL = "label";
    private static final String TYPE = "type";
    private static final String IS_LEAF = "isLeaf";
    private static final String EXPAND = "expand";
    private static final String NODE_REF = "nodeRef";

    private NamespaceService namespaceService;
    private OrgstructureBean orgstructureService;
    private SearchService searchService;
    private SubstitudeBean substitudeBean;

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setSubstitudeBean(SubstitudeBean substitudeBean) {
        this.substitudeBean = substitudeBean;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        List<JSONObject> nodes = new ArrayList<>();

        String nodeRef = req.getParameter(NODE_REF_PARAM);
        String searchTerm = req.getParameter(SEARCH_TERM_PARAM);
        String dashletFormat = req.getParameter(DASHLET_FORMAT_PARAM);

        final String ORG_UNIT_TYPE = OrgstructureBean.TYPE_ORGANIZATION_UNIT.toPrefixString(namespaceService);
        final String ORG_EMPLOYEE_TYPE = OrgstructureBean.TYPE_EMPLOYEE.toPrefixString(namespaceService);

        final NodeRef currentRef = nodeRef != null ? new NodeRef(nodeRef) : orgstructureService.getStructureDirectory();
        final NodeRef rootUnit = orgstructureService.getRootUnit();

        List<JSONObject> units = new ArrayList<>();
        // получаем список только Подразделений (внутри могут находиться другие объекты (Рабочие группы))
        List<NodeRef> childs;
        if (searchTerm == null) {
            childs = orgstructureService.getSubUnits(currentRef, true, false, true);
        } else if ("".equals(searchTerm.trim())){
            childs = new ArrayList<>();
        } else {
            SearchParameters sp = buildOrgArmSearchParameters(UNITS_BY_TERM_QUERY, searchTerm);
            ResultSet results = null;
            childs = new ArrayList<>();
            try {
                results = searchService.query(sp);
                for (ResultSetRow row : results) {
                    childs.add(row.getNodeRef());
                }
            } finally {
                if (results != null) {
                    results.close();
                }
            }
        }

        for (NodeRef child : childs) {
            JSONObject unit = new JSONObject();
            try {
                unit.put(NODE_REF, child.toString());
                unit.put(TYPE, ORG_UNIT_TYPE);
                String formattedString = substitudeBean.formatNodeTitle(child, UNIT_FORMAT_STRING);
                if (dashletFormat == null || !Boolean.valueOf(dashletFormat)) {
                    NodeRef unitBoss = orgstructureService.getUnitBoss(child);
                    if (unitBoss != null) {
                        formattedString = formattedString + " (" + substitudeBean.formatNodeTitle(unitBoss, EMPLOYEE_FIO) + ")";
                    }
                }
                unit.put(LABEL, formattedString);
                unit.put(TITLE, formattedString);
                unit.put(IS_LEAF, !orgstructureService.hasOrgChilds(child, true));
                unit.put(EXPAND, child.equals(rootUnit));
                units.add(unit);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        // получаем список Сотрудников
        List<JSONObject> employees = new ArrayList<>();
        if (searchTerm == null) {
            childs = orgstructureService.getUnitEmployees(currentRef, true);
        } else if ("".equals(searchTerm.trim())){
            childs = new ArrayList<>();
        } else {
            SearchParameters sp = buildOrgArmSearchParameters(EMPLOYEES_BY_TERM_QUERY, searchTerm);
            ResultSet results = null;
            childs = new ArrayList<>();
            try {
                results = searchService.query(sp);
                for (ResultSetRow row : results) {
                    childs.add(row.getNodeRef());
                }
            } finally {
                if (results != null) {
                    results.close();
                }
            }
        }

        for (NodeRef child : childs) {
            JSONObject employee = new JSONObject();
            try {
                employee.put(NODE_REF, child.toString());
                employee.put(TYPE, ORG_EMPLOYEE_TYPE);

                String formattedString = substitudeBean.formatNodeTitle(child, (dashletFormat == null || !Boolean.valueOf(dashletFormat)) ? EMPLOYEE_FORMAT_STRING : EMPLOYEE_DASHLET_FORMAT_STRING);
                employee.put(LABEL, formattedString);
                employee.put(TITLE, formattedString);
                employee.put(IS_LEAF, true);
                employee.put(EXPAND, false);
                employees.add(employee);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        nodes.addAll(units);
        nodes.addAll(employees);

        try {
            res.setContentType("application/json");
            res.setContentEncoding(Charset.defaultCharset().displayName());
            res.getWriter().write(nodes.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private SearchParameters buildOrgArmSearchParameters(final String patternQuery, final String searchTerm) {
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);

        sp.addSort("score", false);

        StringBuilder sb = new StringBuilder("(");
        String[] strings = searchTerm.split(" ");
        boolean first = true;
        for (String string : strings) {
            if (first) {
                first = false;
            } else {
                sb.append(" OR ");
            }
            sb.append("*").append(string.trim()).append("*");
        }
        sb.append(")");

        sp.setQuery(patternQuery.replace("{searchTerm}", sb));
        return sp;
    }
}
