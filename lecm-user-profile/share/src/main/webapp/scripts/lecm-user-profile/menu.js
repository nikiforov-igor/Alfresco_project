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

		instantAbsenceBtn: null,

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

		_onCurrentEmployeeAbsenceChecked: function(layer, args) {
			if (args[1].isAbsent && !this.instantAbsenceBtn.get("disabled")) {
				Alfresco.util.disableYUIButton(this.instantAbsenceBtn);
			} else if (!args[1].isAbsent && this.instantAbsenceBtn.get("disabled")){
				Alfresco.util.enableYUIButton(this.instantAbsenceBtn);
			}
		},

		_onMenuReady: function () {
			var disableInstantAbsence = true;
			if (typeof LogicECM.module.WCalendar.Absence.isAbsent != "undefined") {
				disableInstantAbsence = LogicECM.module.WCalendar.Absence.isAbsent;
			}
			Alfresco.util.createYUIButton(this, "userProfileAbsenceBtn", this._userProfileAbsenceBtnClick(), {});
			Alfresco.util.createYUIButton(this, "userProfileDelegationBtn", this._userProfileDelegationBtnClick(), {});

			this.instantAbsenceBtn = Alfresco.util.createYUIButton(this, "userProfileInstantAbsenceBtn", this._userProfileInstantAbsenceBtnClick(), {
				disabled: disableInstantAbsence
			});

			YAHOO.Bubbling.on("currentEmployeeAbsenceChanged", this._onCurrentEmployeeAbsenceChecked, this);
		},

		onReady: function () {
			Alfresco.logger.info ("A new window.LogicECM.module.UserProfile.Menu has been created");
			this._onMenuReady ();
			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
