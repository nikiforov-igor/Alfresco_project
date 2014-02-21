package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 05.02.14
 * Time: 11:00
 */
public class ArmWrapperServiceImpl implements ArmWrapperService {

    private NodeService nodeService;
    private ArmServiceImpl service;
    private SubstitudeBean substitudeService;
    private DictionaryBean dictionaryService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setService(ArmServiceImpl service) {
        this.service = service;
    }

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public List<ArmNode> getAccordionsByArmCode(String armCode) {
        List<ArmNode> result = new ArrayList<ArmNode>();

        NodeRef arm = service.getArmByCode(armCode);
        if (arm != null) {
            List<NodeRef> accords = service.getArmAccordions(arm);
            for (NodeRef accord : accords) {
                result.add(wrapArmNodeAsObject(accord, true));
            }
        }
        return result;
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef) {
        List<ArmNode> result = new ArrayList<ArmNode>();

        ArmNode parent = wrapArmNodeAsObject(parentRef);

        // 1. Дочерние статические элементы из настроек ARM
        NodeRef parentFromArm = null;
        if (isArmElement(node)) {
            parentFromArm = node;
        } else { // узел справочника или какой-нить другой объект, то реальный родитель - берется последний узел из ARM
            if (service.isArmNode(parent.getNodeRef()) || service.isArmAccordion(parent.getNodeRef())) {
                parentFromArm = parent.getNodeRef();
            }
        }

        if (parentFromArm != null) {
            List<NodeRef> staticChilds = service.getChildNodes(parentFromArm); // узлы АРМа
            for (NodeRef staticChild : staticChilds) {
                ArmNode stNode = wrapArmNodeAsObject(staticChild);
                if (stNode.getNodeQuery() != null) {
                    List<ArmNode> queriedChilds = stNode.getNodeQuery().build(this, stNode);
                    for (ArmNode queriedChild : queriedChilds) {
                        result.add(queriedChild);
                    }
                } else {
                    result.add(stNode);
                }
            }
        }

        //2. Добавить реальных дочерних узлов для иерархического справочника!
        // в остальных случаях у нас не может быть дочерних элементов
        if (node != null && !isArmElement(node) && dictionaryService.isDictionaryValue(node)){
            List<NodeRef> dicChilds = dictionaryService.getChildren(node);
            for (NodeRef dicChild : dicChilds) {
                result.add(wrapAnyNodeAsObject(dicChild, parent));
            }
        }
        return result;
    }

    public boolean hasChildNodes(ArmNode node) {
        return !getChildNodes(node.getNodeRef(), node.getArmNodeRef()).isEmpty() ||
                (node.getNodeQuery() != null && !node.getNodeQuery().build(this, node).isEmpty());
    }

    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion) {
        ArmNode node = new ArmNode();
        node.setTitle((String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
        node.setNodeRef(nodeRef);
        if (!isAccordion) {
            node.setArmNodeRef(nodeService.getPrimaryParent(nodeRef).getParentRef()); // для узла Арм - данное поле дублируется. как так узел Арм - реален
        } else {
            node.setArmNodeRef(nodeRef);
        }
        node.setCounter(service.getNodeCounter(nodeRef));
        node.setNodeQuery(!isAccordion ? service.getNodeChildRule(nodeRef) : null);

        node.setColumns(getNodeColumns(nodeRef));
        node.setAvaiableFilters(getNodeFilters(nodeRef));
        node.setTypes(getNodeTypes(nodeRef));
        node.setCreateTypes(getNodeCreateTypes(nodeRef));

	    String searchQuery = (String) nodeService.getProperty(nodeRef, ArmService.PROP_SEARCH_QUERY);
        if (searchQuery != null) {
	        node.setSearchQuery(searchQuery);
        }

        return node;
    }

    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef) {
        return wrapArmNodeAsObject(nodeRef, false);
    }

    @Override
    public ArmNode wrapAnyNodeAsObject(NodeRef nodeRef, ArmNode parentNode) {
        ArmNode node = new ArmNode();
        node.setTitle(substitudeService.getObjectDescription(nodeRef));
        node.setNodeRef(nodeRef);
        node.setArmNodeRef(parentNode.getNodeRef());
        node.setColumns(parentNode.getColumns());
        node.setAvaiableFilters(parentNode.getAvaiableFilters());
        node.setCounter(parentNode.getCounter());
        node.setTypes(parentNode.getTypes());
        node.setCreateTypes(parentNode.getCreateTypes());
	    if (parentNode.getSearchQuery() != null) {
            node.setSearchQuery(formatQuery(parentNode.getSearchQuery(), nodeRef));
	    }
        return node;
    }

    @Override
    public ArmNode wrapStatusAsObject(String status, ArmNode parentNode) {
        ArmNode node = new ArmNode();
        node.setTitle(status);
        node.setNodeRef(null);
        node.setArmNodeRef(parentNode.getNodeRef());
        node.setColumns(parentNode.getColumns());
        node.setAvaiableFilters(parentNode.getAvaiableFilters());
        node.setCounter(parentNode.getCounter());
        node.setTypes(parentNode.getTypes());
        node.setCreateTypes(parentNode.getCreateTypes());
        node.setSearchQuery("@lecm\\-statemachine\\:status:'" + status + "'");
        //node.setSearchQuery(formatQuery(parentNode.getSearchQuery(), status));
        return node;
    }

    public String formatQuery(String templateQuery, NodeRef node) {
        String formatedQuery = substitudeService.formatNodeTitle(node, templateQuery);
        if (formatedQuery.contains(ArmWrapperService.VALUE_REF)) {
            formatedQuery = formatedQuery.replaceAll(ArmWrapperService.VALUE_REF, node.toString());
        }
        if (formatedQuery.contains(ArmWrapperService.VALUE_TEXT)) {
            formatedQuery = formatedQuery.replaceAll(ArmWrapperService.VALUE_TEXT, substitudeService.getObjectDescription(node));
        }
        return formatedQuery;
    }

    public String formatQuery(String templateQuery, String value) {
        if (templateQuery.contains(ArmWrapperService.VALUE)) {
            return templateQuery.replaceAll(ArmWrapperService.VALUE, value);
        }
        return templateQuery;
    }

    private boolean isArmElement(NodeRef node) {
        return service.isArmNode(node) || service.isArmAccordion(node);
    }

    private List<ArmColumn> getNodeColumns(NodeRef node) {
        List<ArmColumn> nodeColumns = service.getNodeColumns(node);
        if (nodeColumns.isEmpty()) {
            NodeRef parent = nodeService.getPrimaryParent(node).getParentRef();
            if (isArmElement(parent)) {
                return getNodeColumns(parent);
            }
        }
        return nodeColumns;
    }

    private List<ArmFilter> getNodeFilters(NodeRef node) {
        List<ArmFilter> nodeFilters = service.getNodeFilters(node);
        if (nodeFilters.isEmpty()) {
            NodeRef parent = nodeService.getPrimaryParent(node).getParentRef();
            if (isArmElement(parent)) {
                return getNodeFilters(parent);
            }
        }
        return nodeFilters;
    }

    private List<String> getNodeTypes(NodeRef node) {
        List<String> nodeTypes = service.getNodeTypes(node);
        if (nodeTypes.isEmpty()) {
            NodeRef parent = nodeService.getPrimaryParent(node).getParentRef();
            if (isArmElement(parent)) {
                return getNodeTypes(parent);
            }
        }
        return nodeTypes;
    }

	private List<String> getNodeCreateTypes(NodeRef node) {
        List<String> results = new ArrayList<String>();
        results.addAll(service.getNodeTypes(node));

        NodeRef parent = nodeService.getPrimaryParent(node).getParentRef();
        if (isArmElement(parent)) {
	        List<String> parentTypes = getNodeCreateTypes(parent);
	        if (parentTypes != null) {
		        for (String type: parentTypes) {
			        if (!results.contains(type)) {
				        results.add(type);
			        }
		        }
	        }
        }

        return results;
    }
}
