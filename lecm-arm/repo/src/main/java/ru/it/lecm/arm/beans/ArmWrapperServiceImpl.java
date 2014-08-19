package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.arm.beans.childRules.ArmStatusesChildRule;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import ru.it.lecm.statemachine.StateMachineServiceBean;

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
	private NamespaceService namespaceService;
//    private StateMachineServiceBean stateMachineHelper;

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

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public List<ArmNode> getAccordionsByArmCode(String armCode) {
        return getAccordionsByArmCode(armCode, false);
    }

    @Override
    public List<ArmNode> getAccordionsByArmCode(String armCode, boolean onlyMeta) {
        List<ArmNode> result = new ArrayList<ArmNode>();

        NodeRef arm = service.getArmByCode(armCode);
        if (arm != null) {
            List<NodeRef> accords = service.getArmAccordions(arm);
            for (NodeRef accord : accords) {
                result.add(wrapArmNodeAsObject(accord, true, onlyMeta));
            }
        }
        return result;
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef) {
        return getChildNodes(node, parentRef, false);
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef, boolean onlyMeta) {
        List<ArmNode> result = new ArrayList<ArmNode>();

        ArmNode parent = wrapArmNodeAsObject(parentRef, false, onlyMeta);

        // 1. Дочерние статические элементы из настроек ARM
        NodeRef parentFromArm = null;
        if (isArmElement(node)) {
            parentFromArm = node;
        } else { // узел справочника или какой-нить другой объект, то реальный родитель - берется последний узел из ARM
            parentFromArm = parent.getNodeRef();
        }

        if (parentFromArm != null) {
            List<NodeRef> staticChilds = service.getChildNodes(parentFromArm); // узлы АРМа
            for (NodeRef staticChild : staticChilds) {
                ArmNode stNode = wrapArmNodeAsObject(staticChild, false, onlyMeta);
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
                result.add(wrapAnyNodeAsObject(dicChild, parent, onlyMeta));
            }
        }
        return result;
    }

    private boolean hasChildNodes(NodeRef node, NodeRef parentRef) {

        // 1. Дочерние статические элементы из настроек ARM
        NodeRef parentFromArm;
        if (isArmElement(node)) {
            parentFromArm = node;
        } else { // узел справочника или какой-нить другой объект, то реальный родитель - берется последний узел из ARM
            parentFromArm = parentRef;
        }

        //2. Добавить реальных дочерних узлов для иерархического справочника!
        // в остальных случаях у нас не может быть дочерних элементов
        if (node != null && !isArmElement(node) && dictionaryService.isDictionaryValue(node)){
            List<NodeRef> dicChilds = dictionaryService.getChildren(node);
            if (!dicChilds.isEmpty()) {
                return true;
            }
        }

        if (parentFromArm != null) {
            List<NodeRef> staticChilds = service.getChildNodes(parentFromArm); // узлы АРМа
            for (NodeRef staticChild : staticChilds) {
                if (service.getNodeChildRule(staticChild) != null) {
                    ArmNode stNode = wrapArmNodeAsObject(staticChild);
                    List<ArmNode> queriedChilds = stNode.getNodeQuery().build(this, stNode);
                    if (!queriedChilds.isEmpty()) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }


        return false;
    }

    public boolean hasChildNodes(ArmNode node) {
        return hasChildNodes(node.getNodeRef(), node.getArmNodeRef()) ||
                (node.getNodeQuery() != null && !node.getNodeQuery().build(this, node).isEmpty());
    }

    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion) {
        return wrapArmNodeAsObject(nodeRef, isAccordion, false);
    }

    @Override
    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef) {
        return wrapArmNodeAsObject(nodeRef, false, false);
    }

    @Override
    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion, boolean onlyMeta) {
        ArmNode node = new ArmNode();
        node.setTitle((String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
        node.setNodeRef(nodeRef);
        node.setNodeType(nodeService.getType(nodeRef).toPrefixString(namespaceService));
        if (!isAccordion) {
            node.setArmNodeRef(nodeService.getPrimaryParent(nodeRef).getParentRef()); // для узла Арм - данное поле дублируется. как так узел Арм - реален
        } else {
            node.setArmNodeRef(nodeRef);
        }

        node.setNodeQuery(!isAccordion ? service.getNodeChildRule(nodeRef) : null);
        node.setTypes(getNodeTypes(nodeRef));

        String searchQuery = (String) nodeService.getProperty(nodeRef, ArmService.PROP_SEARCH_QUERY);
        if (searchQuery != null) {
            node.setSearchQuery(searchQuery.replaceAll("\\n", " ").replaceAll("\\r", " "));
        }
	    node.setHtmlUrl((String) nodeService.getProperty(nodeRef, ArmService.PROP_HTML_URL));
        node.setReportCodes((String) nodeService.getProperty(nodeRef, ArmService.PROP_REPORT_CODES));

        if (!onlyMeta) {
            node.setCounter(service.getNodeCounter(nodeRef));
            node.setColumns(getNodeColumns(nodeRef));
            node.setAvaiableFilters(getNodeFilters(nodeRef));
            node.setCreateTypes(getNodeCreateTypes(nodeRef));
        }

        return node;
    }

    @Override
    public ArmNode wrapAnyNodeAsObject(NodeRef nodeRef, ArmNode parentNode) {
        return wrapAnyNodeAsObject(nodeRef, parentNode, false);
    }

    @Override
    public ArmNode wrapAnyNodeAsObject(NodeRef nodeRef, ArmNode parentNode, boolean onlyMeta) {
        ArmNode node = new ArmNode();
        node.setTitle(substitudeService.getObjectDescription(nodeRef));
        node.setNodeRef(nodeRef);
        node.setNodeType(nodeService.getType(nodeRef).toPrefixString(namespaceService));
        node.setArmNodeRef(parentNode.getNodeRef());
        node.setTypes(parentNode.getTypes());

        StringBuilder sb = new StringBuilder();
        if (parentNode.getSearchQuery() != null) {
            sb.append(formatQuery(parentNode.getSearchQuery(), nodeRef));
        }

        NodeRef realParent = parentNode.getArmNodeRef();
        if (realParent != null && !realParent.equals(nodeRef)) {
            Object searchQuery = nodeService.getProperty(realParent, ArmService.PROP_SEARCH_QUERY);
            if (searchQuery != null) {
                String parentQuery = searchQuery.toString().replaceAll("\\n", " ").replaceAll("\\r", " ");
                if (!parentQuery.isEmpty()) {
                    sb.append(sb.length() > 0 ? " AND (" : "(");
                    if (parentQuery.startsWith("NOT")) {
                        sb.append("ISNOTNULL:\"cm:name\" AND ");
                    }
                    sb.append(parentQuery).append(")");
                }
            }
        }

        node.setSearchQuery(sb.toString());

        node.setHtmlUrl(parentNode.getHtmlUrl());
        node.setReportCodes(parentNode.getReportCodes());

        if (!onlyMeta) {
            node.setColumns(parentNode.getColumns());
            node.setAvaiableFilters(parentNode.getAvaiableFilters());
            node.setCounter(parentNode.getCounter());
            node.setCreateTypes(parentNode.getCreateTypes());

        }
        return node;
    }

    @Override
    public ArmNode wrapStatusAsObject(String status, ArmNode parentNode) {
        return wrapStatusAsObject(status, parentNode, false);
    }

    @Override
    public ArmNode wrapStatusAsObject(String status, ArmNode parentNode, boolean onlyMeta) {
        ArmNode node = new ArmNode();
        node.setTitle(status);
        node.setNodeRef(null);
        node.setArmNodeRef(parentNode.getNodeRef());
        node.setNodeType(ArmService.TYPE_ARM_NODE.toPrefixString(namespaceService));

        StringBuilder sb = new StringBuilder();
        if (parentNode.getSearchQuery() != null) {
            sb.append("@lecm\\-statemachine\\:status:'").append(status).append("'");
        }

        NodeRef realParent = parentNode.getArmNodeRef();
        if (realParent != null) {
            Object searchQuery = nodeService.getProperty(realParent, ArmService.PROP_SEARCH_QUERY);
            if (searchQuery != null) {
                String parentQuery = searchQuery.toString().replaceAll("\\n", " ").replaceAll("\\r", " ");
                if (!parentQuery.isEmpty()) {
                    sb.append(sb.length() > 0 ? " AND (" : "(");
                    if (parentQuery.startsWith("NOT")) {
                        sb.append("ISNOTNULL:\"cm:name\" AND ");
                    }
                    sb.append(parentQuery).append(")");
                }
            }
        }

        node.setSearchQuery(sb.toString());

        node.setTypes(parentNode.getTypes());

        if (!onlyMeta) {
            node.setColumns(parentNode.getColumns());
            node.setAvaiableFilters(parentNode.getAvaiableFilters());
            node.setCounter(parentNode.getCounter());
            node.setCreateTypes(parentNode.getCreateTypes());
        }
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

    public String getNodeSearchQuery(NodeRef nodeRef) {
        List<AssociationRef> queryAssoc = nodeService.getTargetAssocs(nodeRef, ArmService.ASSOC_NODE_CHILD_RULE);
        if (queryAssoc != null && queryAssoc.size() > 0) {
            NodeRef query = queryAssoc.get(0).getTargetRef();
            QName queryType = nodeService.getType(query);
            Map<QName, Serializable> props = nodeService.getProperties(query);

            if (ArmService.TYPE_STATUSES_CHILD_RULE.equals(queryType)) {
                ArmStatusesChildRule node = new ArmStatusesChildRule();
                node.setRule((String) props.get(ArmService.PROP_STATUSES_RULE));
                String selectedStatuses = (String) props.get(ArmService.PROP_SELECTED_STATUSES);
                if (selectedStatuses != null) {
                    List<String> selectedStatusesList = new ArrayList<String>();
                    for (String str: selectedStatuses.split(",")) {
                        String status = str.trim();
                        if (status.length() > 0) {
                            selectedStatusesList.add(status);
                        }
                    }

                    node.setSelectedStatuses(selectedStatusesList);
                }

                return node.getQuery();
            }
        }
        return (String) nodeService.getProperty(nodeRef, ArmService.PROP_SEARCH_QUERY);
    }

    public String formatQuery(String templateQuery, String value) {
        if (templateQuery.contains(ArmWrapperService.VALUE)) {
            return templateQuery.replaceAll(ArmWrapperService.VALUE, value);
        }
        return templateQuery;
    }

    private boolean isArmElement(NodeRef node) {
        return service.isArmElement(node);
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

//    public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
//        this.stateMachineHelper = stateMachineHelper;
//    }

    public boolean isAccordion(NodeRef node) {
        return service.isArmAccordion(node);
    }
}
