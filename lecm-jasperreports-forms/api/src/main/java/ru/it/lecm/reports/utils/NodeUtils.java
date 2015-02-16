package ru.it.lecm.reports.utils;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилитки для работы с узлами.
 *
 * @author rabdullin
 */
public class NodeUtils {

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
}
