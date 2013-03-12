/**
 * Перерисовка dataGrid
 */
(function () {

	function init() {
	YAHOO.Bubbling.fire("activeGridChanged",
		{
			datagridMeta:{
				itemType:"lecm-dic:dictionary",
                searchConfig:{
                    filter:'+PATH:"/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Справочники/*"'
                }
			}
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
