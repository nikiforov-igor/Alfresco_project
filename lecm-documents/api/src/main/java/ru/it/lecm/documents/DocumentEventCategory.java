package ru.it.lecm.documents;

import ru.it.lecm.businessjournal.beans.EventCategory;

/**
 * User: dbashmakov
 * Date: 13.03.13
 * Time: 16:10
 */
public interface DocumentEventCategory extends EventCategory {
    /**
     * Добавление нового участника
     */
    String INVITE_DOCUMENT_MEMBER = "INVITE_DOCUMENT_MEMBER";
    /**
     * Присвоение рейтинга
     */
    String SET_RATING = "SET_RATING";
    /**
     * Добавление ссылки
     */
    String LINK_ADDED = "LINK_ADDED";

}
