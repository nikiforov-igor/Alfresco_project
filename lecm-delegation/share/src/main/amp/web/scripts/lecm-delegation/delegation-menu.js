if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Delegation = LogicECM.module.Delegation || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.Menu = function (containerId) {
		return LogicECM.module.Delegation.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Menu",
			containerId,
			["button"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Menu, Alfresco.component.Base, {

		options: {
			pageId: null
		},

		_reloadPage: function (type) {
			var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
			window.location.href = url + type;
		},

		_delegationListBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("delegation-list");
			}
		},

		_onMenuReady: function () {

			var disable;
			switch (this.options.pageId) {
				case "delegation-list":
					disable = true;
					break;
				case "delegation-opts":
					//она будет выключена тогда и только тогда, когда прав нету
					disable = !LogicECM.module.Delegation.Const.isBoss && !LogicECM.module.Delegation.Const.isEngineer;
					break;
			}

			Alfresco.util.createYUIButton(this, "delegationListBtn", this._delegationListBtnClick (), {
                disabled: disable
            });
			if (!disable) {
				YAHOO.util.Dom.addClass ("menu-buttons-delegationListBtn", "selected");
			}
		},

		onReady: function () {

			Alfresco.logger.info ("New LogicECM.module.Delegation.List.Menu has been created");

			this._onMenuReady ();

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
