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
 * DocumentHistory
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentHistory
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * LogicECM.DocumentComponentBase constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentComponentBase} The new LogicECM.DocumentComponentBase instance
     * @constructor
     */
    LogicECM.DocumentComponentBase = function DocumentConnections_constructor(htmlId) {
        LogicECM.DocumentComponentBase.superclass.constructor.call(this, "LogicECM.DocumentComponentBase", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentComponentBase, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.DocumentComponentBase.prototype,
        {

            MAIN_REGION: "main-content-region",
            CUSTOM_DASHLET: "custom-dashlet",

            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                /**
                 * The nodeRefs to load the form for.
                 *
                 * @property nodeRef
                 * @type string
                 * @required
                 */
                nodeRef: null,
                title: "Custom Dashlet"
            },

            onExtendView: function Base_onExtendView() {
                // копируем скрытый контент в дашлет
                var formEl = this.getCustomDashletContent();
                formEl.innerHTML = this.getFormElement().innerHTML;
                // подменяем заголовок
                var titleEl = this.getCustomDashletTitle();
                titleEl.innerHTML = this.getTitle();
                // скрываем основной регион
                Dom.setStyle(this.MAIN_REGION, "display", "none");
                // отображаем дашлет
                Dom.setStyle(this.CUSTOM_DASHLET, "display", "block");
            },

            getTitle: function () {
                return this.options.title;
            },

            getFormElement: function () {
                return Dom.get(this.id + "-form");
            },

            getCustomDashletTitle: function () {
                return Dom.get("custom-dashlet-title");
            },

            getCustomDashletContent: function () {
                return Dom.get("custom-dashlet-content");
            }
        }, true);
})();
