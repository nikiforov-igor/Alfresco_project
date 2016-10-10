/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.DocumentCopy = function (fieldHtmlId) {
        LogicECM.module.DocumentCopy.superclass.constructor.call(this, "LogicECM.module.DocumentCopy", fieldHtmlId, [ "container", "datasource"]);
        this.controlId = fieldHtmlId + "-cntrl";
        return this;
    };

    YAHOO.extend(LogicECM.module.DocumentCopy, Alfresco.component.Base,
        {
            options: {
                documentRef: null,
                copyURL: null
            },

            controlId: null,
            copyButton: null,

            onReady: function () {
                this.copyButton = Alfresco.util.createYUIButton(this, this.controlId + "-copy-button", this.onCopy,  {
                        disabled: !this.options.copyURL,
                        title: this.options.copyURL ? this.msg("button.copy") : this.msg("button.copy.unavaiable")
                    },
                    Dom.get(this.controlId + "-copy-button"));

                Dom.addClass(this.controlId + "-copy", this.options.copyURL ? "enabled" : "disabled");
            },

            onCopy: function () {
                if (this.options.documentRef && this.options.copyURL) {
                    document.location.href = Alfresco.constants.URL_PAGECONTEXT + this.options.copyURL;
                }
            }
        });
})();