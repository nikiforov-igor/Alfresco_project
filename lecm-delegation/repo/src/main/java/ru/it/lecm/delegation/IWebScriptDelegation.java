package ru.it.lecm.delegation;


/**
 * WebScript-интерфейс для поддержки делегирований.
 * @author rabdullin
 */
public interface IWebScriptDelegation {

	/**
	 * Создание новой доверенности
	 * @param args json-строка с аргументами (названия соот-ют модели "lecm-ba:delegations")
	 * @return json-строка с id созданного узла
	 */
	String createProcuracy(String args);

	/**
	 * Получить указанную Доверенность
	 * @param argId json-строка с "id"=procuracyId
	 * @return свойства доверенности в виде json-строки или null если нет такой
	 */
	String /*JSONObject*/ getProcuracy(String argId);

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
	 * @return json-строка со списком доверенностей удовлетворяющих критериям searchArgs
	 * (при procuracyId != null список будет содержать одну указанную доверенность)
	 */
	String /*JSONArray*/ findProcuracyList(String /*JSONObject*/ searchArgs);

	/**
	 * Обновление данных указанной доверенности
	 * @param args данные доверенности (+ "id"=xxx)
	 * @return json-строку с результатом "result"="ok" или поднимается исключение 
	 */
	String updateProcuracy(String /*JSONObject*/ args);

	/**
	 * Удаление указанной Доверенности
	 * @param json-строка с "id"=procuracyId для удаления
	 * @return json-строку с результатом "result"="ok" или поднимается исключение 
	 */
	String deleteProcuracy(String /*JSONObject*/ argId);

}
