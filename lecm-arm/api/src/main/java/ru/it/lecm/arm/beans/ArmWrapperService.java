package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 05.02.14
 * Time: 12:17
 */
public interface ArmWrapperService {
    String VALUE_REF = "#value-ref";
    String VALUE = "#value";
    String VALUE_TEXT = "#value-text";
    String VALUE_WITH_CHILDREN_REFS = "\"#value-with-children-refs\"";

    /**
     * возвращает списко объектов Аккордеон по коду АРма
     */
    public List<ArmNode> getAccordionsByArmCode(String armCode);
    public List<ArmNode> getAccordionsByArmCode(String armCode, boolean onlyMeta);


    /**
     * возвращает списко объектов Аккордеон по коду АРма
     */
    public List<ArmNode> getChildNodes(NodeRef armNode, NodeRef parentNode);
    public List<ArmNode> getChildNodes(NodeRef armNode, NodeRef parentNode, boolean onlyMeta);

    /**
     * проверяет, есть ли у узла дочерние элементы
     */
    public boolean hasChildNodes(ArmNode node);

    /**
     * возвращает Узел по nodeRef
     */
    public ArmNode wrapArmNodeAsObject(NodeRef armNode, boolean isAccordion);
    public ArmNode wrapArmNodeAsObject(NodeRef armNode);
    public ArmNode wrapArmNodeAsObject(NodeRef armNode, boolean isAccordion, boolean onlyMeta);

    public ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent);
    public ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent, boolean onlyMeta);

    public ArmNode wrapStatusAsObject(String status, ArmNode parent);
    public ArmNode wrapStatusAsObject(String status, ArmNode parent, boolean onlyMeta);

    public String formatQuery(String templateQuery, NodeRef node);

    public String getNodeSearchQuery(NodeRef nodeRef);

    public boolean isAccordion(NodeRef node);
    public boolean isRunAsAccordion(NodeRef node);
}
