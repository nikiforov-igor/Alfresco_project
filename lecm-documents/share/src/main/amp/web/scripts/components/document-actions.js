// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.services = LogicECM.services || {};

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentActions
 */
LogicECM.DocumentActions = LogicECM.DocumentActions || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.DocumentActions = function (htmlId) {
        LogicECM.DocumentActions.superclass.constructor.call(this, "LogicECM.DocumentActions", htmlId);
        YAHOO.Bubbling.on("showRightPartShortChanged", this.onRightPartShortChanged, this);
        return this;
    };

    YAHOO.lang.extend(LogicECM.DocumentActions, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.DocumentActions.prototype, {
        wideContainer: null,
        shortContainer: null,
        actionsContainer: null,
        actionsShown: false,

        options: {
            isBaseDocActions: false
        },

        onReady: function DocumentActions_OnReady() {
            if (LogicECM.services.documentViewPreferences) {
                var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
                if (shortView) {
                    Dom.addClass(this.id + "-wide-view", "hidden");
                } else {
                    Dom.addClass(this.id + "-short-view", "hidden");
                }

                var showRightPartShort = Dom.getElementsByClassName('show-right-part-short');
                var showRightPartWide = Dom.getElementsByClassName('show-right-part-wide');

                if (!this.options.isBaseDocActions) {
                    Event.addListener(showRightPartShort, 'click', this.onShowRightPartShort, this, true);
                    Event.addListener(showRightPartWide, 'click', this.onShowRightPartWide, this, true);
                }
                this.actionsContainer = Dom.get(this.id + "-formContainer");
                this.shortContainer = Dom.get(this.id + "-short-view");
                this.wideContainer = Dom.get(this.id + "-wide-view");
                this.actionsShown = false;


                var actionsButton = Dom.get(this.id + "-actions-button");
                Event.addListener(actionsButton, 'click', this.onShowActions, this, true);
            } else {
                Dom.addClass(this.id + "-short-view", "hidden");
                Dom.addClass(this.id + "-show-right-part-short-container", "hidden");
            }
        },

        onShowRightPartShort: function DocumentActions_onShowRightPart() {
            var showRightPartShort = !LogicECM.services.documentViewPreferences.getShowRightPartShort();
            LogicECM.services.documentViewPreferences.setShowRightPartShort(showRightPartShort);
            YAHOO.Bubbling.fire('showRightPartShortChanged');
        },

        onShowRightPartWide: function DocumentActions_onShowRightPartWide() {
            var showRightPartShort = !LogicECM.services.documentViewPreferences.getShowRightPartShort();
            LogicECM.services.documentViewPreferences.setShowRightPartShort(showRightPartShort);
            YAHOO.Bubbling.fire('showRightPartShortChanged');
        },


        actionBtnClickedFirstHandling: false,

        onShowActions: function DocumentActions_onActionsButtonClicked() {
            if (this.actionsShown) return;
            this.shortContainer.appendChild(this.actionsContainer);
            this.actionsShown = true;
            this.actionBtnClickedFirstHandling = true;
            Event.addListener('Share', 'click', this.onActionButtonClicked, this, true);
        },

        onActionButtonClicked: function (e) {
            /* Пропускаем обработку клика onShowActions, провалившегося в этот метод */
            if (this.actionBtnClickedFirstHandling) {
                this.actionBtnClickedFirstHandling = false;
                return;
            }
            if (this.actionsContainer != e.target && !this.actionsContainer.contains(e.target)) {
                this.wideContainer.appendChild(this.actionsContainer);
                this.actionsShown = false;
                Event.removeListener('Share', 'click', this.onActionButtonClicked);
            }
        },

        onRightPartShortChanged: function DocumentActions_onShowRightPartShortChanged() {
            var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
            if (shortView) {
                Dom.addClass(this.id + "-wide-view", "hidden");
                Dom.removeClass(this.id + "-short-view", "hidden");
            } else {
                Dom.addClass(this.id + "-short-view", "hidden");
                Dom.removeClass(this.id + "-wide-view", "hidden");
            }
            this.wideContainer.appendChild(this.actionsContainer);
            this.actionsShown = false;
        }
    }, true);
})();