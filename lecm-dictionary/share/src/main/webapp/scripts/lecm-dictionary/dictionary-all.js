/**
 * Перерисовка dataGrid
 */
(function () {

	function init() {
	YAHOO.Bubbling.fire("activeGridChanged",
		{
			datagridMeta:{
				itemType:"lecm-dic:dictionary"
			}
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
