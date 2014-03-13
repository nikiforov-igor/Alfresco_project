package ru.it.lecm.documents.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentEventServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 13.03.14
 * Time: 11:06
 */
public class DocumentEventWebScript extends BaseWebScript {
    private DocumentEventServiceImpl documentEventService;

    public void setDocumentEventService(DocumentEventServiceImpl documentEventService) {
        this.documentEventService = documentEventService;
    }

    /**
     * Подписание объекта-получателя на событие изменения целевого объекта
     *
     * @param object - объект, у которого будут отслеживаться изменения
     * @param listener - объект получатель
     */
    public void subscribe(ScriptNode object, ScriptNode listener) {
        documentEventService.subscribe(object.getNodeRef(), listener.getNodeRef());
    }

    /**
     * Отписание объекта-получателя на событие изменения целевого объекта
     *
     * @param object - объект, у которого будут отслеживаться изменения
     * @param listener - объект получатель
     */
    public void unsubscribe(ScriptNode object, ScriptNode listener) {
        documentEventService.unsubscribe(object.getNodeRef(), listener.getNodeRef());
    }

    /**
     * Получение объектов, которые вызвали событие по изменению своего состояния
     * @param listener
     * @return
     */
    public Scriptable getEventSenders(ScriptNode listener) {
        List<NodeRef> nodes = new ArrayList<NodeRef>();
        nodes.addAll(documentEventService.getEventSenders(listener.getNodeRef()));
        return createScriptable(nodes);
    }

    /**
     * Удаление объекта отправителя сообщения об изменении состояния после его обработки
     * @param listener
     * @param sender
     */
    public void removeEventSender(ScriptNode listener, ScriptNode sender) {
        documentEventService.removeEventSender(listener.getNodeRef(), sender.getNodeRef());
    }

}
