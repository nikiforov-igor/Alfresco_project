/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * DocumentHistory
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentHistory
 */

LogicECM.services = LogicECM.services || {};

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
        Event.onDOMReady(this.setListeners, this, true);
        this.services.docViewPreferences = LogicECM.services.documentViewPreferences;
        return this;
    };

    YAHOO.extend(LogicECM.DocumentComponentBase, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.DocumentComponentBase.prototype,
        {

            MAIN_REGION: "main-region",
            CUSTOM_REGION: "custom-region",
            TITLE: "document-title",

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

            isExpanded: false,

            preferencesService: null,

            /**
             * Навешиваем обаботчики
             *
             */
            setListeners: function Base_setListeners() {
                // Нажатие на кнопку "Свернуть"
                YAHOO.util.Event.delegate('Share', 'click', this.onCollapse, '.collapse', this, true);

                if (this.isHasPreferences()) {
                    YAHOO.Bubbling.on("panelSplitedChanged", this.onPanelSplitedChanged, this);
                    YAHOO.Bubbling.on("showRightPartShortChanged", this.onShowRightPartShortChanged, this);
                }

                YAHOO.util.Event.addListener(window, 'hashchange', this.onHashChange, this, true);
            },

            /**
             * обработчик события изменения URL, нужен для того, чтобы мы могли спокойно
             * нажимать кнопку "назад" при развёрнутом дашлете
             *
             */
            onHashChange: function Base_onHashChange(event) {
                //Проверка на хэш развёрнутого дашлета. Надо допиливать, ибо в случае перехода
                //на любой отличный от 'expanded' хэщ произойдёт сворачивание дашлета
                if (event.oldURL != null && event.oldURL.split('#')[1] == 'expanded') {
                    this.collapseView();
                }
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
                    formEl.innerHTML = "";
                    formEl.innerHTML = html;
                    // подменяем заголовок
                    var titleEl = this.getDocumentTitle();
                    if (titleEl != null) {
                        if (this.isHasPreferences()) {
                            this.setLastCustomPanelView(this.getTitle());
                        }
                    }
                    if (this.isHasPreferences()) {
                        if (this.isSplitPanel()) {
                            // основноый регион остается
                            this.showSplitedPanel();
                        } else {
                            // скрываем основной регион
                            this.showOverflowedPanel();
                        }
                    } else {
                        this.showOverflowedPanel();
                    }
                    Dom.addClass(this.MAIN_REGION, "left-side");
                    // отображаем дашлет
                    Dom.setStyle(this.CUSTOM_REGION, "display", "block");
                    // добавляем в header ссылку на главную страницу документа
                    Dom.setStyle(this.TITLE + "-span", "display", "none");
                    Dom.setStyle(this.TITLE, "display", "inline-block");
                    location.hash = 'expanded';
                }

                this.isExpanded = true;

                YAHOO.Bubbling.fire("updateDocumentPage",
                    {
                        title: this.getTitle()
                    });
            },

            showSplitedPanel: function () {
                Dom.setStyle(this.MAIN_REGION, "display", "block");
                Dom.addClass(this.CUSTOM_REGION, "split");
            },

            showOverflowedPanel: function () {
                Dom.setStyle(this.MAIN_REGION, "display", "none");
                Dom.removeClass(this.CUSTOM_REGION, "split");
            },

            getDocumentTitle: function () {
                return Dom.get("document-title-breadcrumb");
            },

            /**
             * метод, сворачивающий развернутый настраиваемый дашлет
             */
            collapseView: function Base_collapseView() {
                location.hash = 'main';
                // скрываем dashlet
                Dom.setStyle(this.CUSTOM_REGION, "display", "none");
                // отображаем main region
                Dom.setStyle(this.MAIN_REGION, "display", "block");
                // убираем из header'а ссылку на главную страницу документа
                var titleEl = this.getDocumentTitle();
                if (titleEl != null) {
                    titleEl.innerHTML = "";
                }
                Dom.removeClass(this.MAIN_REGION, "left-side");
                Dom.setStyle(this.TITLE, "display", "none");
                Dom.setStyle(this.TITLE + "-span", "display", "inline-block");
                this.isExpanded = false;
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

            isSplitPanel: function () {
                return this.services.docViewPreferences.getPanelSplited();
            },

            setLastCustomPanelView: function (panelName) {
                this.services.docViewPreferences.setLastCustomPanelView(panelName);
            },

            getLastCustomPanelView: function () {
                return this.services.docViewPreferences.getLastCustomPanelView();
            },

            getShowRightPartShort: function () {
                return this.services.docViewPreferences.getShowRightPartShort();
            },

            setShowRightPartShort: function(showRightPartShort) {
                this.services.docViewPreferences.setShowRightPartShort(showRightPartShort);
            },

            onExpand: function () {
                this.expandView(this.msg("msg.no_data"));
            },

            onCollapse: function () {
                if (this.isHasPreferences()) {
                    this.services.docViewPreferences.setPanelSplited(false);
                } else {
                    this.collapseView();
                }
            },

            onPanelSplitedChanged: function (layer, args) {
                var obj = args[1];

                if (obj.panelSplit != null && obj.panelSplit != undefined) {
                    var panelMustBeSplit = obj.panelSplit;
                } else {
                    panelMustBeSplit = this.isSplitPanel();
                }

                var lastCustomPanelTitle = this.getLastCustomPanelView();

                if (this.isExpanded) {
                    if (panelMustBeSplit) {
                        this.showSplitedPanel();
                    } else {
                        this.collapseView();
                    }
                } else {
                    if (lastCustomPanelTitle === this.getTitle() && panelMustBeSplit) {
                        this.onExpand();
                    }
                }
            },

            onShowRightPartShortChanged: function (layer, args) {
                var rightPartWide = Dom.get(this.id + "-wide-view");
                var rightPartShort = Dom.get(this.id + "-short-view");
                if(this.getShowRightPartShort()) {
                    Dom.addClass(rightPartWide, "hidden");
                    Dom.removeClass(rightPartShort, "hidden");
                } else {
                    Dom.addClass(rightPartShort, "hidden");
                    Dom.removeClass(rightPartWide, "hidden");
                }
            },

            isHasPreferences: function () {
                if (LogicECM.services.documentViewPreferences) {
                    return true;
                }
            }
        }, true);
})();
