(function () {

	YAHOO.Bubbling.on('gotDeputyItems', onDeputiesListChanged);

	function onDeputiesListChanged(layer, args) {
		var obj = args[1];
		var formId = obj.formId;
		var hasDeputies = obj.hasDeputies;
		LogicECM.module.Base.Util.readonlyControl(formId, 'select-from-deputies', !hasDeputies);
	}
})();
