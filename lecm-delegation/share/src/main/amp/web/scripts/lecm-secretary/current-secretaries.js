(function () {
	YAHOO.Bubbling.on("formValueChanged", onFormValueChanged);

	function onFormValueChanged(layer, args) {
		var obj = args[1];
		if (obj.eventGroup.options.fieldId == 'lecm-secretary-aspects:sec-fake-assoc') {
			YAHOO.Bubbling.unsubscribe("formValueChanged", onFormValueChanged);
			var nodeRef = Alfresco.util.ComponentManager.find({id:obj.eventGroup.options.formId})[0].options.nodeRef;
			Alfresco.util.Ajax.request({
				url: Alfresco.constants.PROXY_URI + 'lecm/secretary/unitPath?nodeRef=' + nodeRef,
				successCallback: {
					scope: this,
					fn: function (response) {
						var oResults = JSON.parse(response.serverResponse.responseText);
						if (oResults) {
							var secEl = document.getElementsByName('prop_lecm-secretary-aspects_secretary-assoc-ref')[0];
							var curValue = "";
							if(secEl) {
								curValue = secEl.value.replace(/;/g, ',');
							}
							
							var ignore = oResults.ignoredString.split(",");
							var filter = '@lecm\\-orgstr\\-aspects\\:linked\\-organization\\-assoc\\-ref:\"' + oResults.organization + '\"';

							ignore.forEach(function(el) {
								filter += ' AND NOT ID:\"' + el + '\"'
							});

							LogicECM.module.Base.Util.reInitializeControl(obj.eventGroup.options.formId, obj.eventGroup.options.fieldId, {
								currentValue: curValue,
								additionalFilter: filter
							});
						}
					}
				},
				failureMessage: 'message.failure'
			});

		}
	}
})();