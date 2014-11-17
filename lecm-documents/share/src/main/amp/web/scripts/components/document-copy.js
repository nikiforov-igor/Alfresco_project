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
    var $siteURL = Alfresco.util.siteURL;

    LogicECM.module.DocumentCopy = function (fieldHtmlId) {
        LogicECM.module.DocumentCopy.superclass.constructor.call(this, "LogicECM.module.DocumentCopy", fieldHtmlId, [ "container", "datasource"]);
        this.controlId = fieldHtmlId + "-cntrl";
        return this;
    };

    YAHOO.extend(LogicECM.module.DocumentCopy, Alfresco.component.Base,
        {
            options: {
                documentRef: null
            },

            controlId: null,
            copyButton: null,

            onReady: function () {
                this.copyButton = Alfresco.util.createYUIButton(this, this.controlId + "-copy-button", this.onCopy, {},
                    Dom.get(this.controlId + "-copy-button"));

                this.render();
            },

            render: function () {
                Dom.get(this.controlId + "-copy-button").title = this.msg("button.copy");
                Dom.addClass(this.controlId + "-copy", "enabled");
            },

            onCopy: function () {
                if (this.options.documentRef) {
                    var nodeRef = new Alfresco.util.NodeRef(this.options.documentRef);
                    Alfresco.util.Ajax.request({
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "lecm/document/api/duplicate/node/" + nodeRef.uri,
                        dataObj: { "nodeRefs": [this.options.documentRef] },
                        requestContentType: "application/json",
                        responseContentType: "application/json",
                        successCallback: {
                            fn: function(data) {
                                if (data != null) {
                                    if (data.json != null && data.json.results != null && data.json.results.length == 1) {
                                        window.location = $siteURL("document?nodeRef=" + data.json.results[0].nodeRef);
                                    }
                                }
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function() {
                                Alfresco.util.PopupManager.displayMessage({
                                    text: Alfresco.component.Base.prototype.msg("message.duplicate.failure")
                                });
                            },
                            scope:this
                        }
                    });
                }
            }
        });
})();