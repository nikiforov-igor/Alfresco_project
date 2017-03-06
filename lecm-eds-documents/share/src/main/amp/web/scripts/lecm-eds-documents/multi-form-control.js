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

        YAHOO.Bubbling.on("reInitializeSubFormsControls", this.onRenitializeSubFormsControls, this);
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
                argsConfig: null,
                args: null,
                submitFireEvent: null,
                formId: null,
                fieldId: null
            },
            countLoadedForms: 0,
            currentLine: 0,
            rootSubmitElement: null,
            rootFormSubmitFunction: null,
            forms: [],

            onReady: function () {
                this.loadCurrentValue();
                this.updateFormCount();

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
                                    this.countLoadedForms = oResults.length;
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
                    this.updateFormCount();
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
                if (this.options.submitFireEvent) {
                    YAHOO.Bubbling.fire(this.options.submitFireEvent,
                        {
                            form: this.options.rootForm,
                            submitFunction: this.rootFormSubmitFunction,
                            submitElement: this.rootSubmitElement
                        });
                } else if (YAHOO.lang.isFunction(this.rootFormSubmitFunction) && this.rootSubmitElement) {
                    this.rootFormSubmitFunction.call(this.rootSubmitElement);
                }
            },

            buildArgsByForm: function () {
                var result = {};
                if (this.options.rootForm && this.options.argsConfig && Object.keys(this.options.argsConfig).length) {
                    var rootFormData = this.options.rootForm.getFormData();

                    if (rootFormData) {
                        for (var key in this.options.argsConfig) {
                            if (this.options.argsConfig.hasOwnProperty(key)) {
                                if (rootFormData[key]) {
                                    result[this.options.argsConfig[key]] = rootFormData[key];
                                }
                            }
                        }
                    }
                }
                return result;
            },

            onAdd: function (e, target, args) {
                this.currentLine++;
                var num = this.currentLine;
                var formId = this.id + "-line-" + this.currentLine;

                var sendArgs = args;
                if (!sendArgs) {
                    sendArgs = this.buildArgsByForm();
                }

                var dataObj = {
                    htmlid: formId,
                    itemKind: "type",
                    itemId: this.options.documentType,
                    mode: "create",
                    submitType: "json",
                    formUI: true,
                    args: JSON.stringify(sendArgs ? YAHOO.lang.merge(this.options.args, sendArgs) : this.options.args),
                    showCaption: false,
                    showCancelButton: false,
                    showSubmitButton: false
                };
                if (args && this.options.defaultValueFromId) {
                    dataObj.formId = this.options.defaultValueFromId;
                } else if (this.options.documentFromId) {
                    dataObj.formId = this.options.documentFromId;
                }
                var ul = Dom.get(this.id + "-multi-form-documents-list");
                var li = document.createElement('li');
                li.id = this.id + "_" + num + "_item";
                Dom.addClass(li, "multi-form-documents-item");
                ul.appendChild(li);
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                    dataObj: dataObj,
                    execScripts: true,
                    successCallback: {
                        fn: function (response) {
                            var html = response.serverResponse.responseText;
                            var li = Dom.get(this.id + "_" + num + "_item");

                            var itemsHtml = "";

                            var indexTemplate = "<div class='index-block' id='{divIndexId}'></div>";
                            itemsHtml += YAHOO.lang.substitute(indexTemplate, {
                                divIndexId: this.id + "_" + num + "_indexes"
                            });

                            if (!this.options.disabled && (!args || this.options.availableRemoveDefault)) {
                                itemsHtml += this.getActionsDivHTML(num);
                            }
                            itemsHtml += "<div id='" + formId + "_container' class='multi-form-documents-item-container'>";
                            itemsHtml += html;
                            itemsHtml += "</div>";

                            li.innerHTML = itemsHtml;

                            YAHOO.util.Event.onAvailable(this.id + "-line-" + num + "-form", this.calcActionsHeight, num, this);

                            ul.scrollTop = ul.scrollHeight;

                            Dom.setStyle(formId + "-form-buttons", "visibility", "hidden");
                            Dom.setStyle(formId + "-form-buttons", "display", "none");
                            this.allFormsLoaded(num);
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

            allFormsLoaded: function (counter) {
                if (counter == this.countLoadedForms || counter > this.countLoadedForms) {
                    var elIndexes = Dom.get(this.id + "_" + counter + "_indexes");
                    while (elIndexes) {
                        this.updateFormCount();
                        break;
                    }
                    for (var i = 0; i <= this.currentLine; i++) {
                        this.calcActionsHeight(i);
                    }
                }
            },

            onRemove: function remove_function(ev, args) {
                var element = document.getElementById(this.id + "_" + args.num + "_item");
                element.parentNode.removeChild(element);
                delete this.forms[this.id + "-line-" + args.num + "-form"];
                this.updateFormCount();
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
                if (li) {
                    var me = this;
                    var blockHeight = (li.offsetHeight - 10) + "px";

                    var removeItem = Dom.get(me.id + "_" + num + "_remove");
                    if (removeItem) {
                        Dom.setStyle(removeItem, "height", blockHeight);
                    }
                    var indexesBlock = Dom.get(me.id + "_" + num + "_indexes");
                    if (indexesBlock) {
                        Dom.setStyle(indexesBlock, "line-height", blockHeight);
                    }
                }
            },

            attachRemoveItemClickListener: function (num) {
                YAHOO.util.Event.on(this.id + "_" + num + "_remove", 'click', this.onRemove, {
                    num: num
                }, this);
            },

            onRenitializeSubFormsControls: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    if (args[1].subFieldId && args[1].options) {
                        for (var i in this.forms) {
                            if (this.forms.hasOwnProperty(i)) {
                                var formId= this.forms[i].formId;
                                formId = formId.substring(0, formId.length - "-form".length);

                                LogicECM.module.Base.Util.reInitializeControl(formId, args[1].subFieldId, args[1].options);
                            }
                        }
                    }
                }
            },

            updateFormCount: function() {
                var countElement = Dom.get(this.id + "-count");
                if (countElement) {
                    countElement.value = Object.keys(this.forms).length;
                }
                this.resetIndexes();
            },

            resetIndexes: function() {
                var index = 0, elIndexes;
                for (var i = 0; i <= this.currentLine; i++) {
                    elIndexes = Dom.get(this.id + "_" + i + "_indexes");
                    if (elIndexes) {
                        index++;
                        elIndexes.innerHTML = index;
                    }
                }
            }
        });
})();