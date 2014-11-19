package ru.it.lecm.arm.beans.childRules;

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
    public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
        //запрос по справочнику - вернет список корневых объектов
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
    public List<NodeRef> getChildren(NodeRef node) {
        return dictionaryService.getChildren(node);
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
