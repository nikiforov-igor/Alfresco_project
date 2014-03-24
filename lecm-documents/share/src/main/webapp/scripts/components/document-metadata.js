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
                var linkEl = Dom.get(this.id + "-link");
                if (linkEl) {
                    linkEl.onclick = this.onExpand.bind(this);
                }
                Alfresco.util.createTwister(this.id + "-heading", "DocumentMetadata");

                // Во время закрытия диалогового окна сбрасываем параметр в false
                Bubbling.on("formContainerDestroyed", function() {
                    if (docMetaOpening) {
                        docMetaOpening = false;
                    }
                });
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
                    htmlid: "documentMetadata-" + this.options.nodeRef,
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
                        failureMessage: "message.failure",
                        execScripts: true
                    });
            },

            onEdit: function (containerId, formId, moveStartPage) {
                // Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку редактирования)
                if (docMetaOpening) {
                    return;
                }
                docMetaOpening = true;
                if (formId != undefined || formId != null) {
                    this.options.formId = formId;
                }
                if (containerId != undefined || containerId != null) {
                    this.options.containerId = containerId;
                }
                if (moveStartPage != undefined || moveStartPage != null) {
                    this.options.moveStartPage = moveStartPage;
                }
                var templateUrl = this.generateCreateNewUrl(this.options.nodeRef, "NodeMetadata-" + this.id);

                new Alfresco.module.SimpleDialog("documentMetadata-" + this.id + "_results").setOptions({
                    width: "80em",
                    templateUrl: templateUrl,
                    actionUrl: null,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: this.beforeDialogShow
                    },
                    onSuccess: {
                        fn: function (response) {
                            if (this.options.moveStartPage) {
                                window.location.reload();
                            } else {
                                //формируем путь с параметрами. Осуществляем переход
                                LogicECM.module.Base.Util.addUrlParam(location.search, 'view', 'main');
                            }
                        },
                        scope: this
                    }
                }).show();
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
                        failureMessage: "message.failure",
                        execScripts: true,
                        htmlId: response.json.persistedObject,
                        containerId: containerId
                    });
            },

            generateCreateNewUrl: function AssociationTreeViewer_generateCreateNewUrl(nodeRef, formId) {
                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT +
                    "lecm/components/form"
                    + "?itemKind={itemKind}"
                    + "&itemId={itemId}"
                    + "&mode={mode}"
                    + "&submitType={submitType}"
                    + "&showCancelButton=true";
                return YAHOO.lang.substitute(templateUrl, {
                    itemKind: "node",
                    itemId: nodeRef,
                    formId: formId,
                    mode: "edit",
                    submitType: "json"
                });
            },
            beforeDialogShow: function(p_form, p_dialog) {
                var fileSpan = '<span class="light">' + this.msg("document.main.form.edit") + '</span>';
                Alfresco.util.populateHTML(
                    [p_dialog.id + "-form-container_h", fileSpan]
                );

                Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
            }
        }, true);
})();
