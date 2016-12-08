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
            var keys = Object.keys(obj.selectedItems);
            if (keys.length == 1) {
                nodeRef = keys[0];
            }
		}

		if(nodeRef) {
            var titleElement = Dom.get(obj.formId + "_prop_lecm-errands_title");
            var contentElement = Dom.get(obj.formId + "_prop_lecm-errands_content");
            if (errandsTypesForTitles[nodeRef]) {
                if (titleElement) {
                    titleElement.value = errandsTypesForTitles[nodeRef];
                }
                if (contentElement && !contentElement.value) {
                    contentElement.value = errandsTypesForTitles[nodeRef];
                }
            }

		}
	}
})();