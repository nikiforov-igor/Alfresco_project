(function() {

	YAHOO.Bubbling.on('initiatorChanged', reInit);

	function reInit(layer, args) {
		var obj = args[1];
		var nodeRef;

		if(obj.selectedItems) {
			for (var prop in obj.selectedItems) {
				nodeRef = prop;
				break;
			}
		}

		if(nodeRef) {
			var initiatorId = Alfresco.util.NodeRef(nodeRef).id;
			var url = 'lecm/errands/executorsByInitiator/' + initiatorId + '/picker';
			LogicECM.module.Base.Util.reInitializeControl(obj.formId, 'lecm-errands:executor-assoc', {
				childrenDataSource: url
			});
            LogicECM.module.Base.Util.reInitializeControl(obj.formId, 'lecm-errands:coexecutors-assoc', {
				childrenDataSource: url
			});
		}

	}

})();