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

            MAIN_REGION: "main-region",
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
                title: "Custom Dashlet",
                dashletId: null
            },

            /**
             * метод, разворачивающий свернутый настраиваемый дашлет и обновляющий его содержимое
             *
             * @property html
             * @type string
             */
            expandView: function Base_expandView(html) {
                // копируем контент в дашлет
                var formEl = this.getCustomDashletContent();
                if (formEl != null) {
                    formEl.innerHTML = html;
                    // подменяем заголовок
                    var titleEl = this.getCustomDashletTitle();
                    if (titleEl != null) {
                        titleEl.innerHTML = this.getTitle();
                    }
                    // скрываем основной регион
                    Dom.setStyle(this.MAIN_REGION, "display", "none");
                    // отображаем дашлет
                    Dom.setStyle(this.CUSTOM_DASHLET, "display", "block");
                }
            },

            /**
             * метод, сворачивающий развернутый настраиваемый дашлет
             */
            collapseView: function Base_collapseView() {
                // скрываем dashlet
                Dom.setStyle(this.CUSTOM_DASHLET, "display", "none");
                // отображаем main region
                Dom.setStyle(this.MAIN_REGION, "display", "block");
            },

            /**
             * метод получения заголовка компонента
             */
            getTitle: function () {
                return this.options.title;
            },

            /**
             * метод получения элемента с разметкой текущей "формы"
             */
            getFormElement: function () {
                return Dom.get(this.id + "-formContainer");
            },

            /**
             * метод получения заголовка кастомного(разворачивающегося) дашлета
             */
            getCustomDashletTitle: function () {
                return Dom.get("custom-dashlet-title");
            },

            /**
             * метод получения контента кастомного(разворачивающегося) дашлета
             */
            getCustomDashletContent: function () {
                return Dom.get("custom-dashlet-content");
            },

            /**
             * метод получения объекта с содержимым дашлета, связанного с данным объектом через параметр dashletId
             */
            getDashletContainer: function () {
                if (this.options.dashletId != null) {
                    return Dom.get(this.options.dashletId + "_results");
                }
                return null;
            },

            /**
             * метод записывающий html код в дашлет, связанный с данным объектом через параметр dashletId
             * Код записывается в div c ID= "dashletId_results"
             *
             * @property html
             * @type string
             */
            writeToDashlet: function (html) {
                var dashlet = this.getDashletContainer();
                if (dashlet != null) {
                    dashlet.innerHtml = html;
                }
            }
        }, true);
})();
