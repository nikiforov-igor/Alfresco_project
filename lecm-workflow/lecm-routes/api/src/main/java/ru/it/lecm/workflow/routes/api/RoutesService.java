package ru.it.lecm.workflow.routes.api;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Сервис маршрутов документов.
 * @author vlevin
 */
public interface RoutesService {
	NodeRef getRoutesFolder();
	NodeRef createNewTemporaryNode(NodeRef parentNode, QName nodeType);

	/**
	 * получение NodeRef-ы на текущую итерацию согласования в указанном документе
	 * @param documentRef NodeRef-а на документ
	 * @return NodeRef на текущую итерацию или null, если такой нет
	 * @throws AlfrescoRuntimeException если папка "Согласование" не существует, а мы пытаемся получить текущую итерацию согласования
	 */
	NodeRef getDocumentCurrentIteration(final NodeRef documentRef);

	/**
	 * создание новой пустой итерации согласования в указанном документе
	 * @param documentRef NodeRef-а на документ
	 * @return NodeRef на новую пустую итерацию
	 * @throws AlfrescoRuntimeException если папка "Согласование" не существует, а мы пытаемся создать пустую итерацию согласования
	 */
	NodeRef createDocumentCurrentIteration(final NodeRef documentRef);

	//TODO: добавить метод перемещения рабочей итерации в историю
	/**
	 * отправить текущую итерацию согласования в архив (папка "Согласование/История")
	 * @param documentRef NodeRef-а на документ
	 * @return true если удалось отправить в архив, false если не удалось отправить в архив
	 * @throws AlfrescoRuntimeException если папка "Согласование" или "Согласование/История" не существует, а мы пытаемся отправить итерацию в архив
	 */
	boolean archiveDocumentCurrentIteration(final NodeRef documentRef);
}
