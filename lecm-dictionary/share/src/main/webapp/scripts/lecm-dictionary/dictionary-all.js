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
                    filter:'+PATH:"/app:company_home/lecm-dic:Dictionary/*" AND (NOT (ASPECT:"lecm-dic:aspect_active") OR lecm\\-dic:active:true)',
                    fullTextSearch: null,
                    sort: "cm:name|true"
                }
			}
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
