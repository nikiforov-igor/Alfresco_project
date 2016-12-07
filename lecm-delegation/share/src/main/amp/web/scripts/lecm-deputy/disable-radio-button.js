(function () {

	YAHOO.Bubbling.on('gotDeputyItems', onDeputiesListChanged);

	function onDeputiesListChanged(layer, args) {
		var obj = args[1];
		var formId = obj.formId;
		var hasDeputies = obj.hasDeputies;

		if(!hasDeputies) {
			LogicECM.module.Base.Util.disableControl(formId, 'select-from-deputies');
		} else {
			LogicECM.module.Base.Util.enableControl(formId, 'select-from-deputies');
		}
	}
})();
