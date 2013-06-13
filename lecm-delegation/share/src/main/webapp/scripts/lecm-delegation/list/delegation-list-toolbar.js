if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.List = LogicECM.module.Delegation.List || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.List.Toolbar = function (containerId) {
		return LogicECM.module.Delegation.List.Toolbar.superclass.constructor.call(this, "LogicECM.module.Delegation.List.Toolbar", containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.List.Toolbar, LogicECM.module.Base.Toolbar, {
        _initButtons: function () {
            Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);

            Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);
        }
	});
})();
