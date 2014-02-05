package ru.it.lecm.arm.beans.query;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 10:00
 */
public class ArmDictionaryDynamicQuery extends ArmBaseQuery {
	private NodeRef dictionary;

    private DictionaryBean dictionaryService;

	public NodeRef getDictionary() {
		return dictionary;
	}

	public void setDictionary(NodeRef dictionary) {
		this.dictionary = dictionary;
	}

    @Override
    public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
        List<ArmNode> nodes = new ArrayList<ArmNode>();
        //шаблонный запрос из верхнего узла
        List<NodeRef> childrens = dictionaryService.getChildren(getDictionary());
        for (NodeRef children : childrens) {
            ArmNode childNode = service.wrapAnyNodeAsObject(children, node);
            nodes.add(childNode);
        }

        return nodes;
    }

    @Override
    public ArmBaseQuery getDuplicate() {
        ArmDictionaryDynamicQuery query =  new ArmDictionaryDynamicQuery();
        query.setDictionaryService(dictionaryService);
        query.setDictionary(dictionary);
        query.setSearchQuery(getSearchQuery());

        return query;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
