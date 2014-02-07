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
    String VALUE_TEXT = "#value-text";

    /**
     * возвращает списко объектов Аккордеон по коду АРма
     */
    public List<ArmNode> getAccordionsByArmCode(String armCode);


    /**
     * возвращает списко объектов Аккордеон по коду АРма
     */
    public List<ArmNode> getChildNodes(NodeRef armNode, NodeRef parentNode);

    /**
     * проверяет, есть ли у узла дочерние элементы
     */
    public boolean hasChildNodes(ArmNode node);

    /**
     * возвращает Узел по nodeRef
     */
    public ArmNode wrapArmNodeAsObject(NodeRef armNode, boolean isAccordion);
    public ArmNode wrapArmNodeAsObject(NodeRef armNode);

    public ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent);

    /**
     * возвращает объект Аккордеон по nodeRef
     */
    public boolean isNodeSelectable(ArmNode armNode);

    public String formatQuery(String templateQuery, NodeRef node);
}
