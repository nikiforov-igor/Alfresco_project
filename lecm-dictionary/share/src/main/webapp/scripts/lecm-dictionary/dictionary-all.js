/**
 * Перерисовка dataGrid
 */
(function () {

	function init() {
	YAHOO.Bubbling.fire("activeGridChanged",
		{
			datagridMeta:{
				itemType:"lecm-dic:dictionary",
                searchConfig:{ //настройки поиска (необязателен)
                    filter:"",
                    /** Настройки полнотекстового поиска. Пример объекта:
                     {
                     parentNodeRef - относительно какой директории искать (чаще всего совпадает с datagridMeta.nodeRef
                     fields - какие свойства объекта следует заполнить и вернуть
                     searchTerm - строка для поиска
                     }
                     */
                    fullTextSearch: null,
                    sort: "cm:name|true" // сортировка. Указываем по какому полю и порядок (true - asc), например, cm:name|true
                }
			}
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
