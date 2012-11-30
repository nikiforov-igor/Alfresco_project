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
                    filter:'+PATH:"/app:company_home/lecm-dic:Dictionary/*"',
                    fullTextSearch: null,
                    sort: "cm:name|true"
                }
			}
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
