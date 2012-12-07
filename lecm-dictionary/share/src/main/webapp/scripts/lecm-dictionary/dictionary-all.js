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
                    filter:'+PATH:"/app:company_home/lecm-dic:Dictionary/*"'
                }
			}
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
