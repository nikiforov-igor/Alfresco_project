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
    private List<ArmNode> page;
    /**
     * Полное количество нод получаемых запросом, а не количество текущей выборки ограниченной флагами maxItems и skipCount
     */
    private long totalCount;

    public ArmChildrenResponse(List<ArmNode> page) {
        this.page = page;
        this.totalCount = page.size();
    }

    public ArmChildrenResponse(List<ArmNode> page, long totalCount) {
        this.page = page;
        this.totalCount = totalCount;
    }

    /**
     * Коллекция нод в результате выполнения запроса
     */
    public List<ArmNode> getPage() {
        return page;
    }

    /**
     * Полное количество нод получаемых запросом, а не количество текущей выборки ограниченной флагами maxItems и skipCount
     */
    public long getTotalCount() {
        return totalCount;
    }
}
