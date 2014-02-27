if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ARM.DocumentsReports = function (htmlId) {
		LogicECM.module.ARM.DocumentsReports.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsReports", htmlId);

		YAHOO.Bubbling.on("updateArmReports", this.onUpdateArmReports, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.DocumentsReports, Alfresco.component.Base,
		{
			onUpdateArmReports: function(layer, args) {
				var types = args[1].types;
				if (types !== null) {

				}
			}
		}, true);
})();