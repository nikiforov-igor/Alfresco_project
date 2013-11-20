package ru.it.lecm.utils;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.beans.LinksResolver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Утилитки для работы с узлами.
 *
 * @author rabdullin
 */
public class NodeUtils {

    private static final Logger logger = LoggerFactory.getLogger(NodeUtils.class);

    private NodeUtils() {
    }

    /**
     * Перечислитель узлов
     */
    public interface NodeEnumerator {
        /**
         * Вызов для конкретного узла.
         *
         * @param node    узел
         * @param parents родительские узлы:
         *                [0] родитель самого верхнего (первого) уровня,
         *                [1] родитель второго уровня, т.е. узел вложенный в [0]
         *                и т.д.
         */
        void lookAt(NodeRef node, List<NodeRef> parents);
    }

    /**
     * Выполнить перечисление дочерних узлов, расположенных на указанном уровне
     * вложенности относительно root (первые вложенные - это уровень один).
     * Дети выбираются как все chil-узлы по связи ContentModel.ASSOC_CONTAINS.
     *
     * @param root       исходный узел
     * @param service    служба Альфреско
     * @param level      уровень относительно root, на котором надо выполнить сканирования:
     *                   (при level = 0 ничего не перечисляется)
     *                   при level = 1: перечисляются непосредственные дочерние узлы,
     *                   при level = 2: перечисляется дети детей и т.д.
     * @param enumerator "call-back" (может быть null - для подсчёта кол-ва)
     * @return кол-во встреченных детей на указанном уровне
     */
    public static int scanHierachicalChilren(NodeRef root, NodeService service, int level, NodeEnumerator enumerator) {
        return scanHierachicalChilren(root, service, level, null, enumerator);
    }

    /**
     * Выполнить перечисление дочерних узлов, расположенных на указанном уровне
     * вложенности относительно root (первые вложенные - с единицы).
     * Дети выбираются как все chil-узлы по связи ContentModel.ASSOC_CONTAINS.
     *
     * @param root       исходный узел
     * @param service    служба Альфреско
     * @param level      уровень относительно root, на котором надо выполнить сканирования:
     *                   (при level = 0 ничего не перечисляется)
     *                   при level = 1: перечисляются непосредственные дочерние узлы,
     *                   при level = 2: перечисляется дети детей и т.д.
     * @param parents    родительские для root узлы (может быть NULL)
     * @param enumerator "call-back" (может быть null - для подсчёта кол-ва)
     * @return кол-во встреченных детей на указанном уровне
     */
    public static int scanHierachicalChilren(NodeRef root, NodeService service, int level, List<NodeRef> parents, NodeEnumerator enumerator) {
        if (root == null || service == null || level < 1) {
            return 0;
        }

        int result = 0;

        if (parents == null) {
            parents = new ArrayList<NodeRef>();
        }

        final List<ChildAssociationRef> listChildren =
                service.getChildAssocs(root, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
        if (listChildren != null) {
            parents.add(root); // регим родительский узел ...
            try {
                for (ChildAssociationRef assocChild : listChildren) {
                    final NodeRef child = assocChild.getChildRef();
                    if (level == 1) { // находимся на уровне перечисления ...
                        result++;
                        if (enumerator != null) {
                            enumerator.lookAt(child, parents);
                        }
                    } else { // рекурсивный вызов перечисления внутри узла ...
                        result += scanHierachicalChilren(child, service, level - 1, parents, enumerator);
                    }
                }
            } finally {
                parents.remove(parents.size() - 1); // убрать последний
            }
        } // if

        return result;
    }


    /**
     * Получение списка вложений (пробуется child-список, parent-список и ассоциации)
     *
     * @param nodeId   NodeRef
     * @param assocRef QName
     * @return List<NodeRef>
     */
    public static List<NodeRef> findChildrenByAssoc(NodeRef nodeId, QName assocRef, NodeService nodeService) {
        final List<NodeRef> result = new ArrayList<NodeRef>();

        // TODO: в зависимости от метаданных можно точно и строго выбрать один из 4х вариантов ...
        do {
             /* классические "дети" ... */
            final List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeId, assocRef, RegexQNamePattern.MATCH_ALL);
            if (children != null && !children.isEmpty()) {
                for (ChildAssociationRef child : children) {
                    result.add(child.getChildRef());
                }
                break;
            }


             /* пробуем родителей ... */
            final List<ChildAssociationRef> parents = nodeService.getParentAssocs(nodeId, assocRef, RegexQNamePattern.MATCH_ALL);
            if (parents != null && !parents.isEmpty()) {
                for (ChildAssociationRef child : parents) {
                    result.add(child.getParentRef());
                }
                break;
            }

             /* пробуем source-связи ... */
            final List<AssociationRef> listSrcAssocs = nodeService.getSourceAssocs(nodeId, assocRef);
            if (listSrcAssocs != null && !listSrcAssocs.isEmpty()) {
                for (AssociationRef child : listSrcAssocs) {
                    result.add(child.getSourceRef());
                }
                break;
            }


             /* пробуем target-связи ... */
            final List<AssociationRef> listDestAssocs = nodeService.getTargetAssocs(nodeId, assocRef);
            if (listDestAssocs != null && !listDestAssocs.isEmpty()) {
                for (AssociationRef child : listDestAssocs) {
                    result.add(child.getTargetRef());
                }
                break;
            }

        } while (false); // one-time exec

        return (result.isEmpty()) ? null : result;
    }


    /**
     * Получение свойства по ссылке - либо непосредственно из свойств,
     * либо по ссылке.
     * <br/>Если значение НЕ содержит "{}", то предполагается что это обычный
     * атрибут, иначе - форматированный для SubstitudeService
     * <br/> Пример:
     *
     * @param sourceLink String
     * @param docId      NodeRef
     * @param props      список свойств объекта, если null, то свойство будет загружаться непосредственно
     * @param resolver   LinksResolver
     * @return полученное значение
     */
    public static Object getByLink(String sourceLink, NodeRef docId, Map<QName, Serializable> props, LinksResolver resolver) {
        if (sourceLink == null) {
            return null;
        }

        sourceLink = sourceLink.trim();
        if (sourceLink.isEmpty()) {
            return null;
        }

        final NodeService nodeService = resolver.getServices().getServiceRegistry().getNodeService();
        final NamespaceService ns = resolver.getServices().getServiceRegistry().getNamespaceService();

        Object value;
        try {
            if (resolver.isSubstCalcExpr(sourceLink)) {
                // форматируем значение вида "{a/b/c...}"
                Map<String, Object> propsMap = new HashMap<String, Object>();
                if (props != null) {
                    for (QName qName : props.keySet()) {
                        propsMap.put(qName.toPrefixString(ns), props.get(qName));
                    }
                }
                value = resolver.evaluateLinkExpr(docId, sourceLink, null, propsMap);
            } else { // простое значение выбираем в свойствах

                final QName qname = QName.createQName(sourceLink, ns);
                value = (props != null) ? props.get(qname) : nodeService.getProperty(docId, qname);
            }
        } catch (Throwable ex) {
            if (logger != null) {
                final String msg = String.format("Ignoring problem getting property \n\t for docId %s \n\t by link '%s' -> ignored \n\t %s"
                        , docId, sourceLink, ex.getMessage());
                logger.error(msg, ex);
            }
            value = null;
        }
        return value;
    }

    public static Object getByLink(String sourceLink, NodeRef docId, LinksResolver resolver) {
        return getByLink(sourceLink, docId, null, resolver);
    }
}
