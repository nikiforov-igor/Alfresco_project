if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};


(function() {
	LogicECM.module.WCalendar.Menu = function(containerId) {
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
		_reloadPage: function(type) {
			var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
			window.location.href = url + type;
		},
		_wcalendarCalendarBtnClick: function() {
			var scope = this;
			return function(event, obj) {
				scope._reloadPage("wcalendar-calendar");
			};
		},
		_wcalendarScheduleBtnClick: function() {
			var scope = this;
			return function(event, obj) {
				scope._reloadPage("wcalendar-schedule");
			};
		},
		_wcalendarAbsenceBtnClick: function() {
			var scope = this;
			return function(event, obj) {
				scope._reloadPage("wcalendar-absence");
			};
		},
		_wcalendarWorkingDaysSummaryBtnClick: function() {
			var scope = this;
			return function(event, obj) {
				scope._reloadPage("wcalendar-working-days-summary");
			};
		},
		_onMenuReady: function() {
			var canUseCalendar = LogicECM.module.WCalendar.Const.ROLES.isEngineer;
			var canUseScheduleAbsence = LogicECM.module.WCalendar.Const.ROLES.isEngineer || LogicECM.module.WCalendar.Const.ROLES.isBoss;

			Alfresco.util.createYUIButton(this, "wcalendarCalendarBtn", this._wcalendarCalendarBtnClick(), {
				disabled: !canUseCalendar
			});

			Alfresco.util.createYUIButton(this, "wcalendarScheduleBtn", this._wcalendarScheduleBtnClick(), {
				disabled: !canUseScheduleAbsence
			});

			Alfresco.util.createYUIButton(this, "wcalendarAbsenceBtn", this._wcalendarAbsenceBtnClick(), {
				disabled: !canUseScheduleAbsence
			});
			Alfresco.util.createYUIButton(this, "wcalendarWorkingDaysSummaryBtn", this._wcalendarWorkingDaysSummaryBtnClick(), {
				disabled: !canUseScheduleAbsence
			});

			switch (this.options.pageId) {
				case "wcalendar-calendar":
					if (canUseCalendar) {
						YAHOO.util.Dom.addClass("menu-buttons-wcalendarCalendarBtn", "selected");
					}
					break;
				case "wcalendar-schedule":
					if (canUseScheduleAbsence) {
						YAHOO.util.Dom.addClass("menu-buttons-wcalendarScheduleBtn", "selected");
					}
					break;
				case "wcalendar-absence":
					if (canUseScheduleAbsence) {
						YAHOO.util.Dom.addClass("menu-buttons-wcalendarAbsenceBtn", "selected");
					}
					break;
				case "wcalendar-working-days-summary":
					if (canUseScheduleAbsence) {
						YAHOO.util.Dom.addClass("menu-buttons-wcalendarWorkingDaysSummaryBtn", "selected");
					}
					break;
			}
		},
		onReady: function() {
			Alfresco.logger.info("A new LogicECM.module.WCalendar.Menu has been created");
			this._onMenuReady();
			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
		}
	});
})();
