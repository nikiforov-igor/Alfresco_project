if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Document = LogicECM.module.Document|| {};
LogicECM.module.Document.Ajax = LogicECM.module.Document.Ajax|| {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Document.Ajax.Content = function DocumentAjaxContent_constructor(htmlId) {
        LogicECM.module.Document.Ajax.Content.superclass.constructor.call(this, "LogicECM.module.Document.Ajax.Content", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Document.Ajax.Content, Alfresco.component.Base,
        {
            options: {
                requestParams: null,
                nodeRef: null,
                containerId: null,
                contentURL: null
            },

            contentContainer: null,

            onReady: function () {
                if (this.options.contentURL == null) {
                    return;
                }

                this.contentContainer = Dom.get(this.options.containerId);
                if (this.contentContainer == null) {
                    return;
                }

                Dom.addClass(this.options.containerId, 'lecm-document-content-ajax-loading');

                Alfresco.util.Ajax.request({
                    url: this.options.contentURL,
                    dataObj: this.options.requestParams,
                    successCallback: {
                        fn: function (response) {
                            Dom.removeClass(this.options.containerId, 'lecm-document-content-ajax-loading');
                            this.contentContainer.innerHTML = response.serverResponse.responseText;
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            }

        });
})();