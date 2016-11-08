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
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.eds = LogicECM.module.eds || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.eds.MultiFormControl = function (htmlId) {
        LogicECM.module.eds.MultiFormControl.superclass.constructor.call(this, "LogicECM.module.eds.MultiFormControl", htmlId, ["container", "json"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.eds.MultiFormControl, Alfresco.component.Base,
        {
            options: {
                disabled: false,
                rootForm: null,
                documentFromId: null,
                documentType: null,
                args: null
            },
            rootFolder: null,
            currentLine: 0,
            rootSubmitElement: null,
            rootFormSubmitFunction: null,
            forms: [],
            createdDocuments: [],

            onReady: function () {
                if (this.options.documentType && !this.options.disabled) {
                    this.loadDraftRoot();

                    Alfresco.util.createYUIButton(this, "addButton", this.onAdd);
                    Alfresco.util.createYUIButton(this, "removeAllButton", this.onRemoveAll);

                    this.fixSimpleDialog();

                    YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);

                    if (this.options.rootForm) {
                        this.rootSubmitElement = this.options.rootForm.submitElements[0];
                        this.rootFormSubmitFunction = this.rootSubmitElement.submitForm;

                        this.rootSubmitElement.submitForm = this.submitForms.bind(this);
                    }
                }
            },

            fixSimpleDialog: function () {
                var simpleDialogComponents = Alfresco.util.ComponentManager.find({
                    name: "Alfresco.module.SimpleDialog",
                    id: "workflow-form"
                });
                if (simpleDialogComponents && simpleDialogComponents.length) {
                    var simpleDialog = simpleDialogComponents[0];
                    YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", simpleDialog.onBeforeFormRuntimeInit)
                }
            },

            loadDraftRoot: function () {
                var url;
                var template = '{proxyUri}lecm/document-type/settings?docType={docType}';
                var successCallback;
                if (this.options.documentType) {
                    url = YAHOO.lang.substitute(template, {
                        proxyUri: Alfresco.constants.PROXY_URI,
                        docType: encodeURIComponent(this.options.documentType)
                    });

                    successCallback = {
                        scope: this,
                        fn: function (serverResponse) {
                            this.rootFolder = serverResponse.json.nodeRef;
                        }
                    };

                    Alfresco.util.Ajax.jsonGet({
                        url: url,
                        successCallback: successCallback,
                        failureMessage: this.msg('message.failure')
                    });
                }
            },

            onBeforeFormRuntimeInit: function (layer, args) {
                if (args[1] && args[1].runtime && args[1].runtime.formId.indexOf(this.id + "-line-") == 0) {
                    args[1].runtime.ajaxSubmitHandlers.successCallback.fn = this.onSuccessDocumentSubmit.bind(this);
                    this.forms[args[1].runtime.formId] = args[1].runtime;
                }
            },

            submitForms: function () {
                var documentsFormsValid = true, i;
                for (i in this.forms) {
                    if (this.forms.hasOwnProperty(i)) {
                        this.forms[i]._setAllFieldsAsVisited();
                        if (!this.forms[i]._runValidations(null, null, Alfresco.forms.Form.NOTIFICATION_LEVEL_CONTAINER)) {
                            documentsFormsValid = false;
                            break;
                        }
                    }
                }

                if (documentsFormsValid) {
                    if (this.createdDocuments.length == Object.keys(this.forms).length) {
                        this.submitRootForm();
                    } else {
                        this.options.rootForm._toggleSubmitElements(false);

                        var submitEvent = document.createEvent('Event');
                        submitEvent.initEvent('submit', true, false);
                        for (i in this.forms) {
                            if (this.forms.hasOwnProperty(i)) {
                                this.forms[i]._submitInvoked(submitEvent);
                            }
                        }
                    }
                }
            },

            onSuccessDocumentSubmit: function (successResponse) {
                var createdDocumentRef = new Alfresco.util.NodeRef(successResponse.json.persistedObject);
                this.createdDocuments.push(createdDocumentRef);

                var rootCurrentValue = Dom.get(this.id);
                if (rootCurrentValue) {
                    if (rootCurrentValue.value.length) {
                        rootCurrentValue.value += ",";
                    }
                    rootCurrentValue.value += createdDocumentRef;
                }

                if (this.createdDocuments.length == Object.keys(this.forms).length) {
                    this.submitRootForm();
                }
            },

            submitRootForm: function() {
                if (YAHOO.lang.isFunction(this.rootFormSubmitFunction) && this.rootSubmitElement) {
                    this.rootFormSubmitFunction.call(this.rootSubmitElement);
                }
            },

            onAdd: function () {
                this.currentLine++;
                var num = this.currentLine;
                var formId = this.id + "-line-" + this.currentLine;

                var dataObj = {
                    htmlid: formId,
                    itemKind: "type",
                    destination: this.rootFolder,
                    itemId: this.options.documentType,
                    mode: "create",
                    submitType: "json",
                    formUI: true,
                    args: JSON.stringify(this.options.args),
                    showCancelButton: false,
                    showSubmitButton: false
                };
                if (this.options.documentFromId) {
                    dataObj.formId = this.options.documentFromId;
                }

                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                    dataObj: dataObj,
                    execScripts: true,
                    successCallback: {
                        fn: function (response) {
                            var html = response.serverResponse.responseText;
                            var ul = Dom.get(this.id + "-primary-routing-documents-list");

                            var li = document.createElement('li');
                            li.id = this.id + "_" + num + "_item";
                            Dom.addClass(li, "primary-routing-documents-item");

                            var itemsHtml = "";
                            if (!this.options.disabled) {
                                itemsHtml += this.getActionsDivHTML(num);
                            }
                            itemsHtml += "<div id='" + formId + "_container' class='primary-routing-documents-item-container'>";
                            itemsHtml += html;
                            itemsHtml += "</div>";

                            li.innerHTML = itemsHtml;
                            ul.appendChild(li);

                            Dom.setStyle(formId + "-form-buttons", "visibility", "hidden");
                            Dom.setStyle(formId + "-form-buttons", "display", "none");
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function refreshFailure(response) {
                            console.log(response);
                        },
                        scope: this
                    }
                });
            },

            onRemove: function remove_function(ev, args) {
                var element = document.getElementById(this.id + "_" + args.num + "_item");
                element.parentNode.removeChild(element);
                delete this.forms[this.id + "-line-" + args.num + "-form"];
            },

            onRemoveAll: function remove_function() {
                var element = document.getElementById(this.id + "-primary-routing-documents-list");
                element.innerHTML = "";
                this.forms = {};
            },

            getActionsDivHTML: function (num) {
                YAHOO.util.Event.onAvailable(this.id + "_" + num + "_remove", this.attachRemoveItemClickListener, num, this);
                var divHtml = "<div class='actions' id='" + this.id + "_" + num + "_actions'>";
                divHtml += "<div><a id='" + this.id + "_" + num + "_remove' title='Удалить' class='remove-item' href='#'></a></div>";
                divHtml += "</div>";
                return divHtml;
            },

            attachRemoveItemClickListener: function (num) {
                YAHOO.util.Event.on(this.id + "_" + num + "_remove", 'click', this.onRemove, {
                    num: num
                }, this);
            }
        });
})();