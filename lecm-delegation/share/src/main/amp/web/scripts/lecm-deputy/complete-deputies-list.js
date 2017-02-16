(function () {
	YAHOO.Bubbling.on("formValueChanged", onFormValueChanged);
	var avaliableAssocs = ['lecm-deputy:complete-deputy-assoc', 'lecm-deputy:employee-assoc'];

	function onFormValueChanged(layer, args) {
		var obj = args[1];

		if('lecm-deputy:subject-assoc' == obj.eventGroup.options.fieldId) {
			var labels = YAHOO.util.Selector.query('label[for=' + obj.eventGroup.id + '-cntrl]');
			if(labels && labels.length) {
				labels[0].innerHTML = "Критерий: " + (LogicECM.module.Deputy.Const.dictionaryDesc ? LogicECM.module.Deputy.Const.dictionaryDesc : 'Не задан');
			}
		}

		if (avaliableAssocs.indexOf(obj.eventGroup.options.fieldId) > -1) {


			var insertValues = 'lecm-deputy:complete-deputy-assoc' == obj.eventGroup.options.fieldId;
			var simpleDialog = Alfresco.util.ComponentManager.find({id:obj.eventGroup.options.formId})[0];
			var nodeRef;
			if (simpleDialog.options.currentEmployeeRef) {
				nodeRef = simpleDialog.options.currentEmployeeRef;
			} else {
				nodeRef = simpleDialog.options.nodeRef;
			}
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/deputy/getCompleteDeputiesList?nodeRef=' + nodeRef,
				successCallback: {
					scope: this,
					fn: function (response) {
						YAHOO.Bubbling.unsubscribe('formValueChanged', onFormValueChanged);
						var oResults = response.json;
						if (oResults) {
							var ignore = oResults.ignoredString.split(",");
							var filter = '@lecm\\-orgstr\\-aspects\\:linked\\-organization\\-assoc\\-ref:\"' + oResults.organization + '\"';

							ignore.forEach(function(el) {
								filter += ' AND NOT ID:\"' + el + '\"';
							});

							var refsString = oResults.nodes.join(',');
							LogicECM.module.Base.Util.reInitializeControl(obj.eventGroup.options.formId, obj.eventGroup.options.fieldId, {
								currentValue: insertValues ? refsString : "",
								additionalFilter: filter
							});
						}
					}
				},
				failureMessage: Alfresco.util.message('message.failure')
			});
		}
	}

})();
