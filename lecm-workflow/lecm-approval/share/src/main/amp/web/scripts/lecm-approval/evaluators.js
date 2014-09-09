if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};
LogicECM.module.Routes.Evaluators = {
	stageItemEdit: function() {
		return true;
	},
	stageItemDelete: function() {
		return true;
	},
	iterationAdd: function() {
		return true;
	},
	iterationEdit: function() {
		return true;
	},
	iterationDelete: function() {
		return true;
	}
};
