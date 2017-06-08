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
    String VALUE_WITH_SUBTREE_REFS = "\"#value-with-subtree-refs\"";

    /**
     * возвращает списко объектов Аккордеон по коду АРма
     */
    List<ArmNode> getAccordionsByArmCode(String armCode);
    List<ArmNode> getAccordionsByArmCode(String armCode, boolean onlyMeta);


    /**
     * возвращает списко объектов Аккордеон по коду АРма
     */
    List<ArmNode> getChildNodes(NodeRef armNode, NodeRef parentNode);
    List<ArmNode> getChildNodes(NodeRef armNode, NodeRef parentNode, boolean onlyMeta);
    List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef, NodeRef currentSection, NodeRef runAsBoss);
    /**
     * проверяет, есть ли у узла дочерние элементы
     */
    boolean hasChildNodes(ArmNode node);
    boolean hasChildNodes(ArmNode node, String runAs);

    /**
     * возвращает Узел по nodeRef
     */
    ArmNode wrapArmNodeAsObject(NodeRef armNode, boolean isAccordion);
    ArmNode wrapArmNodeAsObject(NodeRef armNode);
    ArmNode wrapArmNodeAsObject(NodeRef armNode, boolean isAccordion, boolean onlyMeta);
    ArmNode wrapArmNodeAsObject(NodeRef armNode, boolean isAccordion, boolean onlyMeta, NodeRef currentSection, NodeRef runAs);

    ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent);
    ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent, String substituteString);
    ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent, String substituteString, boolean onlyMeta);
    ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent, String substituteString, boolean onlyMeta, String searchTerm);

    ArmNode wrapStatusAsObject(String status, ArmNode parent);
    ArmNode wrapStatusAsObject(String status, ArmNode parent, boolean onlyMeta);

    String formatQuery(String templateQuery, NodeRef node);

    String getNodeSearchQuery(NodeRef nodeRef);

    boolean isAccordion(NodeRef node);
    boolean isRunAsAccordion(NodeRef node);

    /**
     * возвращает поисковый запрос для узла
     */
    String getFullQuery(ArmNode node, boolean includeTypes, boolean includeParentQuery);

    /**
     * возвращает число объектов (документов) в узле
     */
    long getObjectsCount(ArmNode node);
}
