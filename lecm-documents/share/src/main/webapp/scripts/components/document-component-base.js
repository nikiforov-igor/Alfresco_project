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
            CUSTOM_REGION: "custom-region",

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
                // копируем контент в кастомный регион
                var formEl = this.getCustomRegion();
                if (formEl != null) {
                    currentExtendedComponent = this;
                    formEl.innerHTML = html;
                    // подменяем заголовок
                    var titleEl = this.getDocumentTitle();
                    if (titleEl != null) {
                        titleEl.innerHTML = " :: " + this.getTitle();
                    }
                    // скрываем основной регион
                    Dom.setStyle(this.MAIN_REGION, "display", "none");
                    // отображаем дашлет
                    Dom.setStyle(this.CUSTOM_REGION, "display", "block");
                }
            },

            getDocumentTitle: function(){
                return Dom.get("document-title-breadcrumb");
            },

            /**
             * метод, сворачивающий развернутый настраиваемый дашлет
             */
            collapseView: function Base_collapseView() {
                // скрываем dashlet
                Dom.setStyle(this.CUSTOM_REGION, "display", "none");
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
            getComponentContainer: function () {
                return Dom.get(this.id + "-formContainer");
            },

            /**
             * метод получения контента кастомного(разворачивающегося) дашлета
             */
            getCustomRegion: function () {
                return Dom.get("custom-region");
            },

            onExpand: function() {
                this.expandView("Нет данных");
            },
            onCollapse: function(){
                this.collapseView();
            }
        }, true);
})();
