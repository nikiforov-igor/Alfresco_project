if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};
LogicECM.module.Routes.Evaluators = {
	stageItemEdit: function() {
		return true;
	},
	stageItemDelete: function() {
		return true;
	}
};
