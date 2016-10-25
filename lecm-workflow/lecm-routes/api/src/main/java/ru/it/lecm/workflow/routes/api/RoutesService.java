package ru.it.lecm.workflow.routes.api;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * Сервис маршрутов документов.
 *
 * @author vlevin
 */
public interface RoutesService {

	NodeRef getRoutesFolder();

	NodeRef createNewTemporaryNode(NodeRef parentNode, QName nodeType);

	/**
	 * получение NodeRef-ы на текущую итерацию согласования в указанном документе
	 *
	 * @param documentRef NodeRef-а на документ
	 * @return NodeRef на текущую итерацию или null, если такой нет
	 * @throws AlfrescoRuntimeException если папка "Согласование" не существует, а мы пытаемся получить текущую итерацию
	 * согласования
	 */
	NodeRef getDocumentCurrentIteration(final NodeRef documentRef);

	/**
	 * отправить текущую итерацию согласования в архив (папка "Согласование/История")
	 *
	 * @param documentRef NodeRef-а на документ
	 * @return true если удалось отправить в архив, false если не удалось отправить в архив
	 * @throws AlfrescoRuntimeException если папка "Согласование" или "Согласование/История" не существует, а мы
	 * пытаемся отправить итерацию в архив
	 */
	boolean archiveDocumentCurrentIteration(final NodeRef documentRef);

	List<NodeRef> getAllowedRoutesForCurrentUser(NodeRef documentRef);

	List<NodeRef> getAllowedRoutesForEmployee(NodeRef employeeRef, NodeRef documentRef);

	ConvertRouteToIterationResult convertRouteToIteration(NodeRef documentNode, NodeRef routeNode);

	NodeRef createEmptyIteration(NodeRef documentNode);

	NodeRef createIterationFromPrevious(NodeRef documentNode);

	void deleteDocumentCurrentIteration(final NodeRef documentRef);

	boolean resolveStageItemMacros(NodeRef stageItemNode, NodeRef resolveStageItemMacros);

	/**
	 * Получить маршрут, на основе которого сформирована итерация.
	 * Определение исходного маршрут основано на ассоциации cm:original,
	 * которая проставляется при копировании маршрута в итерацию.
	 *
	 * @param iterationNode Итерация согласования
	 * @return Маршрут, на котором основана итерация. Если такового нет, то null
	 */
	NodeRef getSourceRouteForIteration(NodeRef iterationNode);

	NodeRef getDocumentByIteration(NodeRef iterationNode);
	NodeRef getDocumentByStage(NodeRef stageNode);
	NodeRef getDocumentByStageItem(NodeRef stageItemNode);

	/**
	 * Проверить существует ли привязанном к документу маршруте хотя бы один сотрудник
	 * @param nodeRef NodeRef-а на документ или непосредственно маршрут
	 * @return true если хотя бы в одном этапе маршрута есть хотя бы один сотрудник. false в противном случае, а также
	 * если маршрута нет
	 */
	boolean hasEmployeesInRoute(final NodeRef nodeRef);
	boolean hasEmployeesInRoute(final NodeRef route, final NodeRef docForExpression);

	/**
	 * Проверить не состоит ли маршрут только из потенциально пропущенных этапов
	 * @param nodeRef NodeRef-а Документа или маршрута
	 * @return true если в маршруте нет ни одного полноценного этапа согласования по условиям выражений доступности
	 */
	boolean isRouteEmpty(final NodeRef nodeRef);

	/**
	 * Проверить не состоит ли маршрут только из потенциально пропущенных этапов
	 * @param route NodeRef-а маршрута
	 * @param docForExpression NodeRef-а документа на котором планируется запустить маршрут
	 * @return true если в маршруте нет ни одного полноценного этапа согласования по условиям выражений доступности
	 */
	boolean isRouteEmpty(final NodeRef route, final NodeRef docForExpression);

	boolean hasPotentialEmployeesInRoute(final NodeRef routeRef, final NodeRef documentRef);

	String getApprovalState(NodeRef documentNode);

	List<NodeRef> getEmployeesByRoute(NodeRef route);

	List<NodeRef> getEmployeesOfAllDocumentRoutes(NodeRef document);
}
