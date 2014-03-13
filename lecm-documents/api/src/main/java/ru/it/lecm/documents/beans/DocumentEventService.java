package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.Set;

/**
 * User: pmelnikov
 * Date: 13.03.14
 * Time: 9:54
 */
public interface DocumentEventService {

    public static QName ASPECT_EVENT_LISTENERS = QName.createQName(DocumentService.DOCUMENT_ASPECTS_NAMESPACE_URI, "event-listeners-aspect");
    public static QName ASPECT_EVENT_SENDER = QName.createQName(DocumentService.DOCUMENT_ASPECTS_NAMESPACE_URI, "event-sender-aspect");

    public static QName ASSOC_EVENT_LISTENERS = QName.createQName(DocumentService.DOCUMENT_ASPECTS_NAMESPACE_URI, "event-listeners");
    public static QName PROP_EVENT_SENDER = QName.createQName(DocumentService.DOCUMENT_ASPECTS_NAMESPACE_URI, "event-sender");

    /**
     * Подписание объекта-получателя на событие изменения целевого объекта
     *
     * @param object - объект, у которого будут отслеживаться изменения
     * @param listener - объект получатель
     */
    public void subscribe(NodeRef object, NodeRef listener);

    /**
     * Отписание объекта-получателя на событие изменения целевого объекта
     *
     * @param object - объект, у которого будут отслеживаться изменения
     * @param listener - объект получатель
     */
    public void unsubscribe(NodeRef object, NodeRef listener);

    /**
     * Получение объектов, которые вызвали событие по изменению своего состояния
     * @param listener
     * @return
     */
    public Set<NodeRef> getEventSenders(NodeRef listener);

    /**
     * Удаление объекта отправителя сообщения об изменении состояния после его обработки
     * @param listener
     * @param sender
     */
    public void removeEventSender(NodeRef listener, NodeRef sender);



}
