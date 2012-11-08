package ru.it.lecm.delegation;


/**
 * WebScript-интерфейс для поддержки делегирований.
 * @author rabdullin
 */
public interface IWebScriptDelegation {

	/**
	 * Получить NodeRef корневого узла Доверенностей 
	 * @return json-строку с результатом "nodeRef"="xxx"
	 */
	String getProcuracyRootNodeRef();

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
	 * Формат:
	{
		"totalRecords": , // общее кол-во строк
		"startIndex": 0, // всегда ноль, 
		"metadata": { // непонятная секция, пока захардкодим, позжу доразберусь зачем она нужна
		},

		"parent": {
			"nodeRef": ,// ссылка на родительский контейнер в котором лежат элементы (workspace://SpacesStore/%parentId%)
			"permissions": { // права родительского контейнера
				"userAccess": {
					"create": true // нас интересует есть ли право создавать детишек в родительском контейнернере
				}
			}
		},

		"items":[ //массив с данными (JSONArray) которые мы отображаем в таблице
		{
			"nodeRef": ,//ссылка на элемент (workspace://SpacesStore/%elementId%
			"itemData": { //свойства объета которые мы возвращаем, свойства описываются по принципу prop_префиксМодели_имяСвойства (например prop_lecm-ba_dateUTCBegin)
				"prop_lecm-ba_dateUTCBegin": { //чем value отличается от displayValue я не знаю, в тех примерах что я видел value=displayValueб как-то так
					"value": , // значение свойства
					"displayValue": // отображаемое значение свойства
				}
			}
		}
	}
	 */
	String /*JSONObject*/ findProcuracyList(String /*JSONObject*/ searchArgs);

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

	String test(String /*JSONObject*/ args);
}

