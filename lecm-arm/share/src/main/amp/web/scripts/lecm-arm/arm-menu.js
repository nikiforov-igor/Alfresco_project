if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {

    LogicECM.module.ARM.Menu = function (htmlId) {
        return LogicECM.module.ARM.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.ARM.Menu",
            htmlId,
            ["button"]);
    };

    YAHOO.extend(LogicECM.module.ARM.Menu, Alfresco.component.Base, {

        onHomePage: true,

        setOnHomePage: function (value) {
            this.onHomePage = value;
        },

        onReady:function Menu_onReady () {
            var context = this;
            var onClickHome = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "arm-settings";
            };
            this.widgets.filtersDicButton = Alfresco.util.createYUIButton(this, "homeBtn", onClickHome, {
                disabled: context.onHomePage
            });

            var onClickDic = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "arm-filters";
            };
            this.widgets.filtersDicButton = Alfresco.util.createYUIButton(this, "filtersDicBtn", onClickDic, {
                disabled: !context.onHomePage
            });
        }
    });
})();
