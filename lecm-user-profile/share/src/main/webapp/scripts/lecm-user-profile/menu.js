if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.UserProfile = LogicECM.module.UserProfile || {};


(function () {
	LogicECM.module.UserProfile.Menu = function (containerId) {
		return LogicECM.module.UserProfile.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.UserProfile.Menu",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.UserProfile.Menu, Alfresco.component.Base, {

		options: {
			pageId: null
		},

		_reloadPage: function (type) {
			var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
			window.location.href = url + type;
		},

		_userProfileAbsenceBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("my-absence");
			}
		},

		_userProfileDelegationBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("my-delegation");
			}
		},
		
		_userProfileInstantAbsenceBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("instant-absence");
			}
		},

		_onMenuReady: function () {
			Alfresco.util.createYUIButton(this, "userProfileAbsenceBtn", this._userProfileAbsenceBtnClick(), {});
			Alfresco.util.createYUIButton(this, "userProfileDelegationBtn", this._userProfileDelegationBtnClick(), {});
			Alfresco.util.createYUIButton(this, "userProfileInstantAbsenceBtn", this._userProfileInstantAbsenceBtnClick(), {});
		},

		onReady: function () {
			Alfresco.logger.info ("A new LogicECM.module.UserProfile.Menu has been created");
			this._onMenuReady ();
			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
