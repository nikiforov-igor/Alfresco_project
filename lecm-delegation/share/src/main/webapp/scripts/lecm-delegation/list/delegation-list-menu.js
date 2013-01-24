if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.List = LogicECM.module.Delegation.List || {};

(function () {
	LogicECM.module.Delegation.List.Menu = function (containerId) {
		return LogicECM.module.Delegation.List.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.List.Menu",
			containerId,
			["button"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.List.Menu, Alfresco.component.Base, {

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

			Alfresco.util.createYUIButton(this, "delegationListBtn", this._delegationListBtnClick (), {
                disabled: true
            });
		},

		onReady: function () {

			Alfresco.logger.info ("New LogicECM.module.Delegation.List.Menu has been created");

			this._onMenuReady ();

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
