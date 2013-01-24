if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Opts = LogicECM.module.Delegation.Opts || {};

(function () {
	LogicECM.module.Delegation.Opts.Menu = function (containerId) {
		return LogicECM.module.Delegation.Opts.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Opts.Menu",
			containerId,
			["button"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Opts.Menu, Alfresco.component.Base, {

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

			//TODO: перечень делегирования показывается только тогда, когда есть права (технолог или начальник)
			Alfresco.util.createYUIButton(this, "delegationListBtn", this._delegationListBtnClick (), {});
		},

		onReady: function () {

			Alfresco.logger.info ("New LogicECM.module.Delegation.Opts.Menu has been created");

			this._onMenuReady ();

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
