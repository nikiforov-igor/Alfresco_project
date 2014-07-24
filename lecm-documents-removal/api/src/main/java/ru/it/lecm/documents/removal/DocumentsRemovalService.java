package ru.it.lecm.documents.removal;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * сервис удаления документов
 * @author vmalygin
 */
public interface DocumentsRemovalService {

	/**
	 * брутальное удаление документа с чисткой всех хвостов
	 * @param documentRef
	 */
	void purge(final NodeRef documentRef);

    /**
     * полное удаление черновика документа
     * @param document документ
     */
    public void purgeDraft(NodeRef document);
}
