(function () {

	var subscribed = false;

	YAHOO.Bubbling.on('formValueChanged', function(layer, args) {
		if(!subscribed) {
			var fieldId = args[1].eventGroup.options.fieldId;
			var formId = args[1].eventGroup.options.formId;
			if(fieldId == 'os-aspects:nomenclature-organization-assoc') {
				if(!LogicECM.Nomenclature.isCentralized) {
					Alfresco.util.Ajax.jsonGet({
						url: Alfresco.constants.PROXY_URI + "/lecm/os/nomenclature/getOrgPath",
						successCallback: {
							fn: function (response) {
								var oResults = response.json;
								orgXPath = oResults.xPath;
								LogicECM.module.Base.Util.reInitializeControl(formId, fieldId, {
									rootLocation: orgXPath,
									additionalFilter: "+PATH:\"" + orgXPath + "/*\""
								});
							}
						}
					});
				} else {
					LogicECM.module.Base.Util.disableControl(formId, fieldId);
				}
			}
			subscribed = true;
		}
	});

})();