if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.DocumentErrandsList = function (htmlId) {
        LogicECM.DocumentErrandsList.superclass.constructor.call(this, "LogicECM.DocumentErrandsList", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentErrandsList, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.DocumentErrandsList.prototype,
        {
            options: {
                nodeRef: null,
                componentHtmlId: "main-region"
            },

            onReady: function () {
                this.htmlid = this.id + "-" + Alfresco.util.generateDomId();

                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/tasks-errands",
                    dataObj: {
                        htmlid: this.htmlid,
                        nodeRef: this.options.nodeRef
                    },
                    successCallback: {
                        fn: function (response) {
                            var html = response.serverResponse.responseText;

                            var formEl = Dom.get(this.options.componentHtmlId);
                            if (formEl) {
                                formEl.innerHTML = "";
                                formEl.innerHTML = html;
                            }

                            this.loadErrandsList();

                            YAHOO.util.Event.on(this.htmlid + "-errands-filter", "change", this.loadErrandsList, this, true);
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            loadErrandsList: function () {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/errandsList",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        htmlid: this.htmlid + "_errandsList",
                        isAnchor: false,
                        filter: Dom.get(this.htmlid + "-errands-filter").value
                    },
                    successCallback: {
                        fn: function (response) {
                            var html = response.serverResponse.responseText;
                            var listContainer = Dom.get(this.htmlid + "_errandsList");
                            listContainer.innerHTML = html;
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            }
        }, true);
})();