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
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;
    var docMetaOpening = false;

    /**
     * DocumentHistory constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentHistory} The new DocumentHistory instance
     * @constructor
     */
    LogicECM.DocumentMetadata = function DocumentMain_constructor(htmlId) {
        LogicECM.DocumentMetadata.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentMetadata, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentMetadata.prototype,
        {

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
                formId: "",
                containerId: null,
                moveStartPage: false

            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentMain_onReady() {
                function expandStages() {
                    this.onExpandTab('contract-stages');
                }
                YAHOO.util.Event.delegate('Share', 'click', this.onExpand, '.metadata-expand', this, true);
                YAHOO.util.Event.delegate('Share', 'click', this.onEdit, '.metadata-edit', this, true);
                YAHOO.util.Event.delegate('Share', 'click', expandStages, '.stages-expand', this, true);
                Alfresco.util.createTwister(this.id + "-heading", "DocumentMetadata");

                LogicECM.services = LogicECM.services || {};
                if(LogicECM.services.DocumentViewPreferences) {
                    var lastCustomPanelViewTitle = this.getLastCustomPanelView();
                    if (lastCustomPanelViewTitle == this.getTitle() && this.isSplitPanel()) {
                        this.onExpand();
                    }
                }
            },

            onExpandTab: function (tabId) {
                this.expand(tabId);
            },

            onExpand: function () {
                this.expand(null);
            },

            expand: function (tabId) {
                // Load the form
                var data = {
                    htmlid: "documentMetadata-" + this.options.nodeRef.replace(/\//g,"_"),
                    nodeRef: this.options.nodeRef
                };
                if (tabId != null) {
                    data.setId = tabId;
                }
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/metadata",
                        dataObj: data,
                        successCallback: {
                            fn: function (response) {
                                this.expandView(response.serverResponse.responseText);
                            },
                            scope: this
                        },
                        failureMessage: this.msg("message.failure"),
                        execScripts: true
                    });
            },

            onEdit: function () {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/api/url/edit",
                        dataObj: {
                            nodeRef: this.options.nodeRef
                        },
                        successCallback: {
                            fn:function(response){
                                window.location.href = Alfresco.constants.URL_PAGECONTEXT + response.json.url + "?nodeRef=" + this.options.nodeRef;
                            },
                            scope: this
                        },
                        failureMessage: this.msg("message.failure")
                    });
            },

            refreshContainer: function (containerId, formId, response) {
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                        dataObj: {
                            htmlid: 'documentMetadata-' + response.json.persistedObject,
                            itemKind: "node",
                            itemId: nodeRef,
                            formId: formId,
                            mode: "view"
                        },
                        successCallback: {
                            fn: function (response) {
                                var container = Dom.get(arguments[0].config.containerId);
                                container.innerHTML = response.serverResponse.responseText;
                            }
                        },
                        failureMessage: this.msg("message.failure"),
                        execScripts: true,
                        htmlId: response.json.persistedObject,
                        containerId: containerId
                    });
            }
        }, true);
})();
