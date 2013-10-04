package ru.it.lecm.reports.beans;

import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;


public interface LinksResolver {


	/**
	 * Проверить является ли указанное поле вычисляемым (в понимании SubstitudeBean):
	 * если первый символ "{", то является.
	 * @param expression
	 * @return true, если является.
	 */
	boolean isSubstCalcExpr(final String expression);

	/**
	 * Выполнить разименование указанной ссылки и приведение типа. 
	 * @param docId id узла, относительно которого задано выражение
	 * @param linkExpression выражение для разименования
	 * @param destClassName желаемый тип результата, если null или пучто - преобразование не требуется
	 * @param curProps список подгруженных свойств (может быть null), 
	 * ключь здесь - само выражение, если под таким ключом будет элемент, то 
	 * именно его значение станет результатом. 
	 * @return
	 */
	Object evaluateLinkExpr(
			NodeRef docId
			, String linkExpression
			, String destClassName
			, Map<String, Object> curProps  // already loaded props, nullable
	);

	/**
	 * Выполнить разименование ссылки в нативный тип. 
	 * см также {@lik #evaluateLinkExpr(NodeRef, String, String, Map)} 
	 * @param docId
	 * @param linkExpression
	 * @return
	 */
	Object evaluateLinkExpr(
			NodeRef docId
			, String linkExpression
	);

	/** Часто используемые службы */
	WKServiceKeeper getServices();
}

