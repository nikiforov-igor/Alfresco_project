package ru.it.lecm.arm.beans.search;

import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.List;

/**
 * Ответ на запрос получения списка дочерних элементов
 *
 * User: pmelnikov
 * Date: 01.06.2017
 */
public class ArmChildrenResponse {

    /**
     * Коллекция нод согласно условиям поиска
     */
    private List<ArmNode> nodes;
    /**
     * Полное количество нод получаемых запросом, а не количество текущей выборки ограниченной флагами maxItems и skipCount
     */
    private long childCount;

    public ArmChildrenResponse(List<ArmNode> nodes, long childCount) {
        this.nodes = nodes;
        this.childCount = childCount;
    }

    /**
     * Коллекция нод в результате выполнения запроса
     */
    public List<ArmNode> getNodes() {
        return nodes;
    }

    /**
     * Полное количество нод получаемых запросом, а не количество текущей выборки ограниченной флагами maxItems и skipCount
     */
    public long getChildCount() {
        return childCount;
    }
}
