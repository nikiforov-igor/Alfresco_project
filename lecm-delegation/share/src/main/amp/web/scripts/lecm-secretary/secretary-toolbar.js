if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Secretary = LogicECM.module.Secretary || {};

(function () {
	"use strict";
	LogicECM.module.Secretary.Toolbar = function (containerId) {
		return LogicECM.module.Secretary.Toolbar.superclass.constructor.call(this, "LogicECM.module.Secretary.Toolbar", containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Secretary.Toolbar, LogicECM.module.Base.Toolbar, {
        _initButtons: function () {
            Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);

            Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);
        }
	});
})();
