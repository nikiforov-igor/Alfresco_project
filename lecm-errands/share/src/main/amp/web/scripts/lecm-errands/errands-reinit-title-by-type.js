(function() {
    var errandsTypesForTitles = [];
    Alfresco.util.Ajax.jsonGet({
        url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getTypes",
        successCallback: {
            fn: function (response) {
                var oResults = response.json;
                if (oResults && oResults.length) {
                    var i;
                    for (i = 0; i < oResults.length; i++) {
                        errandsTypesForTitles[oResults[i].nodeRef] = oResults[i].defaultTitle;
                    }
                }
            },
            scope: this
        }
    });


	YAHOO.Bubbling.on('errandTypeChanged', reInit);

	function reInit(layer, args) {
		var obj = args[1];
		var nodeRef;

		if(obj.selectedItems) {
			for (var prop in obj.selectedItems) {
				nodeRef = obj.selectedItems[prop];
				break;
			}
		}

		if(nodeRef) {
            var titleElement = Dom.get(obj.formId + "_prop_lecm-errands_title");
            if (titleElement && errandsTypesForTitles[nodeRef]) {
                titleElement.value = errandsTypesForTitles[nodeRef];
            }
		}
	}
})();