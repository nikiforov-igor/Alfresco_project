package ru.it.lecm.delegation;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Интерфейс для поддержки делегирований.
 * @author rabdullin
 */
public interface IDelegation {

	public enum DelegationStatus {
		  _new
		, _active
		, _revoked
		, _closed
		;
	}

	/**
	 * Создание новой доверенности
	 * @param args аргументы
	 * @return id созданного узла
	 */
	String createProcuracy(JSONObject args);

	/**
	 * Получить указанную Доверенность
	 * @return доверенность или null если нет такой
	 */
	JSONObject getProcuracy(String procuracyId);

	/**
	 * Получить Доверенность(и) удовлетворяющие условиям поиска
	 * (от указанного пользователя или для него)
	 * 
	 * @param searchArgs карта с дополнительными критериями для поиска:
	 *    key="ownerId" id выдавшего Доверенность, при наличии этого параметра в 
	 * searchArgs, значения null или пусто не допускаются, 
	 *    key="procuracyId" id нужной Доверенности, null или пусто для получения  
	 * списка всех доверенностей от ownerId
	 *    key="fromEmployee" или "toEmployee" если задачны выполняется фильтрация
	 * согласно критерию.
	 * @return список доверенностей удовлетворяющих критериям searchArgs
	 * (при procuracyId != null список из одной указанной)
	 */
	JSONArray findProcuracyList(JSONObject searchArgs);

	/**
	 * Отозвать/активировать Доверенность
	 * @param procuracyId
	 * @param status желаемый новый статус Доверенности
	 */
	// TODO: void setStatusProcuracy(String procuracyId, DelegationStatus status);

	/* TODO: подумать о необходимости методов update/delete - если их реализовать 
	непосредственно, тогда надо не забыть перегенерировать права (снять прежние,
	выдать новые) и обратить внимание на: 
		1) смену источника (владельца) или цели делегирования (делегата),
		2) смену дат from-to
		3) смену статуса.
	 */

	/**
	 * Обновление данных указанной доверенности
	 * @param procuracyId id доверенности
	 * @param args данные доверенности
	 */
	void updateProcuracy(String procuracyId, JSONObject args);

	/**
	 * Удаление указанной Доверенности
	 * @param procuracyId
	 */
	void deleteProcuracy(String procuracyId);

	JSONObject test(JSONObject args);

}
