/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Header module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Header
 */
LogicECM.module.Header = LogicECM.module.Header || {};

/**
 * Header module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Header.JsButton
 */
(function () {

    LogicECM.module.Header.JsButton = function (htmlId) {
        return LogicECM.module.Header.JsButton.superclass.constructor.call(
            this,
            "LogicECM.module.Header.JsButton",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.lang.extend(LogicECM.module.Header.JsButton, Alfresco.component.Base, {

        self:null,

        onReady: function () {
            self = this;

            Alfresco.logger.info ("A new LogicECM.module.Header.JsButton has been created");

            var btn = YAHOO.util.Dom.get(this.id);
            var div=document.createElement("div");
            div.innerHTML='<div id="myElem">1<div/>';
            btn.appendChild(div);

        }
    });
})();
