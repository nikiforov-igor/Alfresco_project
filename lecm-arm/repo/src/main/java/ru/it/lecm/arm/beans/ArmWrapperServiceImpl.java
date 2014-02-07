package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.query.ArmBaseQuery;
import ru.it.lecm.arm.beans.query.ArmStaticQuery;
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
                    result.add(wrapArmNodeAsObject(staticChild));
                }
            }
        }

        //2. Добавить реальных дочерних узлов для иерархического справочника!
        // в остальных случаях у нас не может быть дочерних элементов
        if (!isArmElement(node) && dictionaryService.isDictionaryValue(node)){
            List<NodeRef> dicChilds = dictionaryService.getChildren(node);
            for (NodeRef dicChild : dicChilds) {
                result.add(wrapAnyNodeAsObject(dicChild, parent));
            }
        }
        return result;
    }

    public boolean hasChildNodes(ArmNode node) {
        return !getChildNodes(node.getNodeRef(), node.getArmNodeRef()).isEmpty() ||
                (node.getNodeQuery() != null && (!(node.getNodeQuery() instanceof ArmStaticQuery) && !node.getNodeQuery().build(this, node).isEmpty()));
    }

    @Override
    public boolean isNodeSelectable(ArmNode armNode) {
        //TODO метод-флаг можно ли выбирать узел (нужен ли?)
        return true; //return (armNode.getNodeQuery() != null && armNode.getNodeQuery() instanceof ArmStaticQuery);
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
        node.setColumns(service.getNodeColumns(nodeRef));
        node.setAvaiableFilters(service.getNodeFilters(nodeRef));
        node.setCounter(service.getNodeCounter(nodeRef));
        node.setTypes(service.getNodeTypes(nodeRef));

        node.setNodeQuery(!isAccordion ? service.getNodeQuery(nodeRef) : service.getAccordionQuery(nodeRef));

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

        ArmBaseQuery parentQuery = parentNode.getNodeQuery();
        if (parentQuery != null) {
            ArmBaseQuery dupQuery = parentQuery.getDuplicate();
            dupQuery.setSearchQuery(formatQuery(parentQuery.getSearchQuery(), nodeRef));
            node.setNodeQuery(dupQuery);
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

    private boolean isArmElement(NodeRef node) {
        return service.isArmNode(node) || service.isArmAccordion(node);
    }
}
