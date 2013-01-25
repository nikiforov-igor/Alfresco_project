if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};


(function () {
	LogicECM.module.WCalendar.Menu = function (containerId) {
		return LogicECM.module.WCalendar.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.WCalendar.Menu",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Menu, Alfresco.component.Base, {

		options: {
			pageId: null
		},

		_reloadPage: function (type) {
			var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
			window.location.href = url + type;
		},

		_wcalendarCalendarBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("wcalendar-calendar");
			}
		},

		_wcalendarSheduleBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("wcalendar-shedule");
			}
		},

		_wcalendarAbsenceBtnClick: function () {
			var scope = this;
			return function (event, obj) {
				scope._reloadPage ("wcalendar-absence");
			}
		},

		_onMenuReady: function () {
			var canUseCalendar = LogicECM.module.WCalendar.Const.ROLES.isEngineer;
			var canUseSheduleAbsence = LogicECM.module.WCalendar.Const.ROLES.isEngineer || LogicECM.module.WCalendar.Const.ROLES.isBoss;

			Alfresco.util.createYUIButton(this, "wcalendarCalendarBtn", this._wcalendarCalendarBtnClick (), {
				disabled: !canUseCalendar
			});

			Alfresco.util.createYUIButton(this, "wcalendarSheduleBtn", this._wcalendarSheduleBtnClick (), {
				disabled: !canUseSheduleAbsence
			});

			Alfresco.util.createYUIButton(this, "wcalendarAbsenceBtn", this._wcalendarAbsenceBtnClick (), {
				disabled: !canUseSheduleAbsence
			});
			
			switch (this.options.pageId) {
				case "wcalendar-calendar":
					if (canUseCalendar) {
						YAHOO.util.Dom.addClass ("menu-buttons-wcalendarCalendarBtn", "selected");
					}
					break;
				case "wcalendar-shedule":
					if (canUseSheduleAbsence) {
						YAHOO.util.Dom.addClass ("menu-buttons-wcalendarSheduleBtn", "selected");
					}
					break;
				case "wcalendar-absence":
					if (canUseSheduleAbsence) {
						YAHOO.util.Dom.addClass ("menu-buttons-wcalendarAbsenceBtn", "selected");
					}
					break;
			}
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.WCalendar.Menu has been created");

			this._onMenuReady ();

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
