package ru.it.lecm.arm.beans.childRules;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.search.ArmChildrenRequest;
import ru.it.lecm.arm.beans.search.ArmChildrenResponse;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 10:00
 */
public class ArmDictionaryChildRule extends ArmBaseChildRule {
	private NodeRef dictionary;

    private DictionaryBean dictionaryService;

	public NodeRef getDictionary() {
		return dictionary;
	}

	public void setDictionary(NodeRef dictionary) {
		this.dictionary = dictionary;
	}

    @Override
    public ArmChildrenResponse build(ArmWrapperService service, ArmNode node, ArmChildrenRequest request) {

        List<NodeRef> resultList = new ArrayList<>();

        Path path = nodeService.getPath(getDictionary());
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
        String query = "PATH:\"" + path.toPrefixString(namespaceService) + "/*\" AND NOT @lecm\\-dic\\:active:false";

        String preparedSearchTerm = request.getSearchTerm();
        if (preparedSearchTerm != null && preparedSearchTerm.length() > 0) {
            if (!preparedSearchTerm.contains("*")) {
                preparedSearchTerm = "*" + preparedSearchTerm + "*";
            }
            query += " AND @cm\\:name:\"" + preparedSearchTerm + "\"";
        }
        query = processorService.processQuery(query);
        sp.setQuery(query);
        long totalChildren = -1;
        if (request.getMaxItems() != -1) {
            totalChildren = searchCounter.query(sp, false, 0, 0);
            sp.setSkipCount(request.getSkipCount());
            sp.setMaxItems(request.getMaxItems());
        }

        sp.addSort("@" + ContentModel.PROP_NAME, true);

        ResultSet resultSet = searchService.query(sp);

        List<ArmNode> nodes = new ArrayList<>();
        if (resultSet != null) {
            for (ResultSetRow row : resultSet) {
                nodes.add(service.wrapAnyNodeAsObject(row.getNodeRef(), node, getSubstituteString()));
            }
        }
        return new ArmChildrenResponse(nodes, totalChildren == -1 ? nodes.size() : totalChildren);
    }

    @Override
    public List<NodeRef> getChildren(NodeRef node) {
        return dictionaryService.getChildrenSortedByName(node);
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
