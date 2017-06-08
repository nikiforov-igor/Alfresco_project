package ru.it.lecm.arm.beans.childRules;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.search.ArmChildrenRequest;
import ru.it.lecm.arm.beans.search.ArmChildrenResponse;
import ru.it.lecm.base.beans.SearchCounter;
import ru.it.lecm.base.beans.SearchQueryProcessorService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 9:57
 */
public abstract class ArmBaseChildRule {

    private Logger logger = LoggerFactory.getLogger(ArmBaseChildRule.class);

	private String substituteString;
    protected int maxItems = -1;
    protected String searchTemplate = "";
    protected String sortConfig = "";

    private static String SEARCH_TERM_TEMPLATE = "#searchTerm";
    private static String DEFAULT_SEARCH_TEMPLATE = "@cm\\:name:\"" + SEARCH_TERM_TEMPLATE + "\"";

    //заглушка для кэширования пустых значений
    public static final ArmBaseChildRule NULL_RULE = new ArmBaseChildRule() {
        @Override
        public ArmChildrenResponse build(ArmWrapperService service, ArmNode node, ArmChildrenRequest request) {
            return null;
        }

        @Override
        public List<NodeRef> getChildren(NodeRef node) {
            return null;
        }
    };

    protected NodeService nodeService;
    protected NamespaceService namespaceService;
    protected SearchService searchService;
    protected SearchQueryProcessorService processorService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setProcessorService(SearchQueryProcessorService processorService) {
        this.processorService = processorService;
    }

    abstract public ArmChildrenResponse build(ArmWrapperService service, ArmNode node, ArmChildrenRequest request);

    abstract public List<NodeRef> getChildren(NodeRef node);

	public String getSubstituteString() {
		return substituteString;
	}

	public void setSubstituteString(String substituteString) {
		this.substituteString = substituteString;
	}

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public void setSearchTemplate(String searchTemplate) {
        this.searchTemplate = searchTemplate;
    }

    public void setSortConfig(String sortConfig) {
        this.sortConfig = sortConfig;
    }

    protected String getSearchQuery(String searchTerm) {
        String preparedSearchTerm = searchTerm;
        if (StringUtils.isNotEmpty(preparedSearchTerm)) {
            if (!preparedSearchTerm.contains("*")) {
                preparedSearchTerm = "*" + preparedSearchTerm + "*";
            }
        } else {
            return "";
        }
        String template;
        if (StringUtils.isNotEmpty(searchTemplate)) {
            template = searchTemplate;
        } else {
            template = DEFAULT_SEARCH_TEMPLATE;
        }
        return "(" + template.replace(SEARCH_TERM_TEMPLATE, preparedSearchTerm) + ")";
    }

    protected void addSort(SearchParameters searchParameters) {
        if (StringUtils.isNotEmpty(sortConfig)) {
            try {
                String[] templates = sortConfig.split(";");
                for (String sort : templates) {
                    String[] splitStr = sort.split("\\|");
                    String field = splitStr[0];
                    String direction = splitStr[1];
                    searchParameters.addSort("@" + QName.createQName(field, namespaceService), direction.toLowerCase().equals("asc"));
                }
            } catch (NamespaceException e) {
                logger.error("Cannot parse sort configuration for arm child rule " + sortConfig, e);
                searchParameters.addSort("@" + ContentModel.PROP_NAME, true);
            }
        } else {
            searchParameters.addSort("@" + ContentModel.PROP_NAME, true);
        }
    }
}
