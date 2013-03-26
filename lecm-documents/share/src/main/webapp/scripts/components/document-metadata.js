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
                containerId: null

            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentMain_onReady() {
                var linkEl = Dom.get(this.id + "-link");
                linkEl.onclick = this.onExpand.bind(this);
            },

            onExpand: function () {
                // Load the form
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/metadata",
                        dataObj: {
                            htmlid: "documentMetadata-" + this.options.nodeRef,
                            nodeRef: this.options.nodeRef
                        },
                        successCallback: {
                            fn:function(response){
                                this.expandView(response.serverResponse.responseText);
                            },
                            scope: this
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
            },
            onEdit: function(containerId, formId) {
                if (formId != undefined || formId != null) {
                    this.options.formId = formId;
                }
                if (containerId != undefined || containerId != null) {
                    this.options.containerId = containerId;
                }
                var templateUrl = this.generateCreateNewUrl(this.options.nodeRef,"NodeMetadata-" + this.id);
                new Alfresco.module.SimpleDialog("documentMetadata-"+this.id+"_results").setOptions({
                    width:"50em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn:this.setCreateNewFormDialogTitle
                    },
                    onSuccess:{
                        fn:function (response) {
                            //формируем путь с параметрами. Осуществляем переход
                            LogicECM.module.Base.Util.addUrlParam(location.search, 'view', 'main');
                        },
                        scope:this
                    }
                }).show();
            },
            refreshContainer: function(containerId, formId, response){
                Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                        dataObj:{
                            htmlid: 'documentMetadata-'+response.json.persistedObject,
                            itemKind: "node",
                            itemId:nodeRef,
                            formId: formId,
                            mode:"view"
                        },
                        successCallback:{
                            fn:function(response){
                                var container = Dom.get(arguments[0].config.containerId);
                                container.innerHTML = response.serverResponse.responseText;
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true,
                        htmlId:response.json.persistedObject,
                        containerId: containerId
                    });
            },
            generateCreateNewUrl: function AssociationTreeViewer_generateCreateNewUrl(nodeRef,formId) {
                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT +
                "lecm/components/form"
                    + "?itemKind={itemKind}"
                    + "&itemId={itemId}"
                    + "&mode={mode}"
                    + "&submitType={submitType}"
                + "&showCancelButton=true";
                return YAHOO.lang.substitute(templateUrl, {
                    itemKind: "node",
                    itemId:nodeRef,
                    formId: formId,
                    mode:"edit",
                    submitType:"json"
                });
            },
            setCreateNewFormDialogTitle: function (p_form, p_dialog) {
                var fileSpan = '<span class="light">Create new</span>';
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", fileSpan]
                );
            }
        }, true);
})();
