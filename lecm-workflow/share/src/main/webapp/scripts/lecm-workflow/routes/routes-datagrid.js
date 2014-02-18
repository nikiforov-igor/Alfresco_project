if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	LogicECM.module.Routes.DataGrid = function(containerId) {
		return LogicECM.module.Routes.DataGrid.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Routes.DataGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Routes.DataGrid.prototype, {}, true);
})();
