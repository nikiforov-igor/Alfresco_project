if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Delegation = LogicECM.module.Delegation || {};

(function () {
	LogicECM.module.Delegation.Menu = function (containerId) {
		return LogicECM.module.Delegation.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Menu",
			containerId,
			["button", "container", "connection", "json", "selector"]);
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

		_delegationOptsBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("delegation-opts");
			}
		},

		_delegationBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("delegation");
			}
		},

		_onMenuReady: function () {

			Alfresco.util.createYUIButton(this, "delegationOptsBtn", this._delegationOptsBtnClick (), {});
			//TODO: перечень делегирования показывается только тогда, когда есть права (технолог или начальник)
			Alfresco.util.createYUIButton(this, "delegationListBtn", this._delegationListBtnClick (), {});
			Alfresco.util.createYUIButton(this, "delegationBtn", this._delegationBtnClick (), {});
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.Menu has been created");

			this._onMenuReady ();

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
