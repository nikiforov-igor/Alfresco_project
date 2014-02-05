package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.arm.beans.query.ArmBaseQuery;
import ru.it.lecm.base.beans.SubstitudeBean;

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

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setService(ArmServiceImpl service) {
        this.service = service;
    }

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
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
    public List<ArmNode> getChildNodes(NodeRef node) {
        List<ArmNode> result = new ArrayList<ArmNode>();

        // 1. Дочерние статические элементы
        List<NodeRef> staticChilds = service.getChildNodes(node);
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
            //result.add(wrapArmNodeAsObject(staticChild));
        }
        //2. Элементы согласно запросу
        /*ArmNode armNode = wrapArmNodeAsObject(node);
        if (armNode.getNodeQuery() != null) {
            List<ArmNode> queriedChilds = armNode.getNodeQuery().build(this, armNode);
            for (ArmNode queriedChild : queriedChilds) {
                result.add(queriedChild);
            }
        }*/
        return result;
    }

    public boolean hasChildNodes(ArmNode node) {
        return !service.getChildNodes(node.getNodeRef()).isEmpty() || (node.getNodeQuery() != null && !node.getNodeQuery().build(this, node).isEmpty());
    }

    @Override
    public boolean isNodeSelectable(ArmNode armNode) {
        //TODO метод-флаг можно ли выбирать узел
        return true; //return (armNode.getNodeQuery() != null && armNode.getNodeQuery() instanceof ArmStaticQuery);
    }

    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion) {
        ArmNode node = new ArmNode();
        node.setTitle((String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
        node.setNodeRef(nodeRef);
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
        //TODO метод для формирования запроса по шаблону
        return templateQuery;
    }
}
