package ru.it.lecm.arm.beans.childRules;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
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
	private String substituteString;
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
    protected SearchCounter searchCounter;
    protected SearchService searchService;
    protected SearchQueryProcessorService processorService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setSearchCounter(SearchCounter searchCounter) {
        this.searchCounter = searchCounter;
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

}
