if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};


(function () {
    LogicECM.module.WCalendar.Calendar.Menu = function (containerId) {
        return LogicECM.module.WCalendar.Calendar.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.WCalendar.Calendar.Menu",
            containerId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.lang.extend(LogicECM.module.WCalendar.Calendar.Menu, Alfresco.component.Base, {

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
                scope._reloadPage ("wcalendar");
            }
        },

        _wcalendarSheduleBtnClick: function () {
            var scope = this;
            return function (event, obj) {
                scope._reloadPage ("shedule");
            }
        },
        
        _wcalendarAbsenceBtnClick: function () {
            var scope = this;
            return function (event, obj) {
                scope._reloadPage ("absence");
            }
        },

        _onMenuReady: function () {
            //TODO: скрывать некоторые кнопки, если нет прав
            Alfresco.util.createYUIButton(this, "wcalendarCalendarBtn", this._wcalendarCalendarBtnClick (), {});
            Alfresco.util.createYUIButton(this, "wcalendarSheduleBtn", this._wcalendarSheduleBtnClick (), {});
            Alfresco.util.createYUIButton(this, "wcalendarAbsenceBtn", this._wcalendarAbsenceBtnClick (), {});
        },

        onReady: function () {

            Alfresco.logger.info ("A new LogicECM.module.WCalendar.Calendar.Menu has been created");

            this._onMenuReady ();

            YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
        }
    });
})();
