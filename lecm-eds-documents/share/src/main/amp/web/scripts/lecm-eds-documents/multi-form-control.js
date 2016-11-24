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
                currentValue: [],
                rootForm: null,
                documentFromId: null,
                defaultValueFromId: null,
                documentType: null,
                defaultValueDataSource: null,
                availableRemoveDefault: true,
                fixSimpleDialogId: null,
                args: null
            },
            currentLine: 0,
            rootSubmitElement: null,
            rootFormSubmitFunction: null,
            forms: [],

            onReady: function () {
                this.loadCurrentValue();

                if (this.options.documentType && !this.options.disabled) {
                    this.loadDefaultValue();

                    Alfresco.util.createYUIButton(this, "addButton", this.onAdd);

                    this.fixSimpleDialog();

                    YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);

                    if (this.options.rootForm) {
                        this.rootSubmitElement = this.options.rootForm.submitElements[0];
                        this.rootFormSubmitFunction = this.rootSubmitElement.submitForm;

                        this.rootSubmitElement.submitForm = this.submitForms.bind(this);
                    }
                }
            },

            //SimpleDialog после открытия начинает ловить абсолютно все формы, и старается запихать их себе внутрь.
            // Если после его открытия попытаться загрузить ещё одну форму, он начинает падать.
            // Пришлось сделать такой костыль.
            fixSimpleDialog: function () {
                if (this.options.fixSimpleDialogId) {
                    var simpleDialogComponents = Alfresco.util.ComponentManager.find({
                        name: "Alfresco.module.SimpleDialog",
                        id: this.options.fixSimpleDialogId
                    });
                    if (simpleDialogComponents && simpleDialogComponents.length) {
                        var simpleDialog = simpleDialogComponents[0];
                        YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", simpleDialog.onBeforeFormRuntimeInit)
                    }
                }
            },

            loadCurrentValue: function () {
                if (this.options.currentValue && this.options.currentValue.length) {
                    this.options.currentValue.forEach(function (item) {
                        this.onAdd(null, null, item)
                    }, this);
                }
            },

            loadDefaultValue: function () {
                if (this.options.defaultValueDataSource) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI + this.options.defaultValueDataSource,
                        dataObj: this.options.args,
                        successCallback: {
                            fn: function (response) {
                                var oResults = response.json;
                                if (oResults && oResults.length) {
                                    var i;
                                    for (i = 0; i < oResults.length; i++) {
                                        this.onAdd(null, null, oResults[i]);
                                    }
                                }
                            },
                            scope: this
                        },
                        failureMessage: "message.failure"
                    });
                }
            },

            onBeforeFormRuntimeInit: function (layer, args) {
                if (args[1] && args[1].runtime && args[1].runtime.formId.indexOf(this.id + "-line-") == 0) {
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
                    this.buildFormsData();
                    this.submitRootForm();
                }
            },

            buildFormsData: function () {
                var valueElement = Dom.get(this.id), i;

                if (valueElement != null) {
                    var resultJson = [];
                    for (i in this.forms) {
                        if (this.forms.hasOwnProperty(i)) {
                            var formData = this.forms[i].getFormData();
                            if (formData) {
                                var key;
                                var data = {};
                                for (key in formData) {
                                    if (formData.hasOwnProperty(key)) {
                                        if (key.indexOf('prop_') == 0 ||
                                            (key.indexOf('assoc_') == 0 &&
                                            (key.lastIndexOf('_added') != key.length - '_added'.length) &&
                                            (key.lastIndexOf('_removed') != key.length - '_removed'.length))) {
                                            data[key] = formData[key];
                                        }
                                    }
                                }
                                resultJson.push(data);
                            }
                        }
                    }
                    valueElement.value = JSON.stringify(resultJson);
                }
            },

            submitRootForm: function () {
                if (YAHOO.lang.isFunction(this.rootFormSubmitFunction) && this.rootSubmitElement) {
                    this.rootFormSubmitFunction.call(this.rootSubmitElement);
                }
            },

            onAdd: function (e, target, args) {
                this.currentLine++;
                var num = this.currentLine;
                var formId = this.id + "-line-" + this.currentLine;

                var dataObj = {
                    htmlid: formId,
                    itemKind: "type",
                    itemId: this.options.documentType,
                    mode: "create",
                    submitType: "json",
                    formUI: true,
                    args: JSON.stringify(args ? YAHOO.lang.merge(this.options.args, args) : this.options.args),
                    showCancelButton: false,
                    showSubmitButton: false
                };
                if (args && this.options.defaultValueFromId) {
                    dataObj.formId = this.options.defaultValueFromId;
                } else if (this.options.documentFromId) {
                    dataObj.formId = this.options.documentFromId;
                }

                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                    dataObj: dataObj,
                    execScripts: true,
                    successCallback: {
                        fn: function (response) {
                            var html = response.serverResponse.responseText;
                            var ul = Dom.get(this.id + "-multi-form-documents-list");

                            var li = document.createElement('li');
                            li.id = this.id + "_" + num + "_item";
                            Dom.addClass(li, "multi-form-documents-item");

                            var itemsHtml = "";
                            if (!this.options.disabled && (!args || this.options.availableRemoveDefault)) {
                                itemsHtml += this.getActionsDivHTML(num);
                            }
                            itemsHtml += "<div id='" + formId + "_container' class='multi-form-documents-item-container'>";
                            itemsHtml += html;
                            itemsHtml += "</div>";

                            li.innerHTML = itemsHtml;
                            ul.appendChild(li);

                            YAHOO.util.Event.onAvailable(this.id + "_" + num + "_item", this.calcActionsHeight, num, this);

                            ul.scrollTop = ul.scrollHeight;

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

            getActionsDivHTML: function (num) {
                YAHOO.util.Event.onAvailable(this.id + "_" + num + "_remove", this.attachRemoveItemClickListener, num, this);

                var template = "<div class='actions' id='{divActionsId}'><div><a id='{divRemoveActionId}' title='{messageRemove}' class='remove-item' href='#'></a></div></div>";

                return YAHOO.lang.substitute(template, {
                    divActionsId: this.id + "_" + num + "_actions",
                    divRemoveActionId: this.id + "_" + num + "_remove",
                    messageRemove: this.msg('button.delete')
                });
            },

            calcActionsHeight: function (num) {
                var li = Dom.get(this.id + "_" + num + "_item");
                var removeItem = Dom.get(this.id + "_" + num + "_remove");
                if (li && removeItem) {
                    Dom.setStyle(removeItem, "height", (li.offsetHeight - 10) + "px");
                }
            },

            attachRemoveItemClickListener: function (num) {
                YAHOO.util.Event.on(this.id + "_" + num + "_remove", 'click', this.onRemove, {
                    num: num
                }, this);
            }
        });
})();