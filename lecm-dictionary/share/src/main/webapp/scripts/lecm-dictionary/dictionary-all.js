/**
 * Перерисовка dataGrid
 */
(function () {

	function showDatagrid(rootNodeRef) {
		YAHOO.Bubbling.fire("activeGridChanged",
			{
				bubblingLabel: "dictionaries-all-datagrid",

				datagridMeta:{
					itemType: "lecm-dic:dictionary",
					nodeRef: rootNodeRef
				}
			});
	}

	function loadRootNode() {
		var sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/folder";
		var callback = {
			success:function (oResponse) {
				var oResults = eval("(" + oResponse.responseText + ")");
				if (oResults != null && oResults.nodeRef != null) {
					showDatagrid(oResults.nodeRef);
				}
			}
		};

		YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
	}

	YAHOO.util.Event.onDOMReady(loadRootNode);
})();
