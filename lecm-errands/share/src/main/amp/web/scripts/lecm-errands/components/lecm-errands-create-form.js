(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-create-form.css'
    ]);

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;
    var formId, formButtons;
    var isRouteClick = true;

    Bubbling.on('saveDraftButtonClick', saveDraft);
    Bubbling.on('expandButtonClick', toggleSet);
    Bubbling.on('periodicallyErrandChange', toggleSet);
    Bubbling.on('errandsCreateFormScriptLoaded', init);

    function toggleSet(layer, args) {
        var sets = [];
        var fieldHtmlId;
        var hidden;
        var queryTemplate = 'div[class^=\"{formId}-form-panel {targetClass}\"]';

        switch (layer) {
            case 'expandButtonClick':
                fieldHtmlId = args[1].fieldHtmlId;
                sets = Selector.query(Substitute(queryTemplate, {
                    targetClass: 'block2',
                    formId: formId
                }));
                var expandButton = Dom.get(fieldHtmlId);
                if (sets.length) {
                    hidden = sets[0].classList.contains("hidden1");
                    if (hidden) {
                        Dom.removeClass(expandButton.parentNode, "form-4-buttons");
                        Dom.setStyle(expandButton, 'display', 'none');
                    } else {
                        Dom.addClass(expandButton.parentNode, "form-4-buttons");
                        Dom.setStyle(expandButton, 'display', 'inline-block');
                    }
                }
                break;
            case 'periodicallyErrandChange':
                fieldHtmlId = formId + '_prop_' + args[1].fieldId.replace('\:', '_');
                sets = Selector.query(Substitute(queryTemplate, {
                    targetClass: 'block3',
                    formId: formId
                }));
                hidden = Dom.get(fieldHtmlId).value == "true";
                break;
        }
        for (var i = 0; i < sets.length; i++) {
            if (hidden) {
                Dom.removeClass(sets[i], 'hidden1');
            } else {
                Dom.addClass(sets[i], 'hidden1');
            }
        }

    }

    function saveDraft(layer, args) {
        var isShort = Dom.getElementBy(function (el) {
            return el.name == "prop_lecm-errands_is-short";
        }, 'input', formId);
        var routeButton = Selector.query(".yui-submit-button", formButtons, true);
        isRouteClick = false;
        isShort.value = "false";
        routeButton.click();
    }

    function init(layer, args) {
        formId = args[1].formId;
        formButtons = Dom.get(formId + "-form-buttons");
        Dom.addClass(formButtons, "form-4-buttons");
        Event.onContentReady(formId + "-form-submit-button", function () {
            var submitButtonElement = Dom.get(formId + "-form-submit-button");
            submitButtonElement.innerHTML = Alfresco.util.message("label.route-errand");
        });

        Alfresco.util.Ajax.request({
            url: Alfresco.constants.PROXY_URI + "lecm/errands/isHideAdditionAttributes",
            successCallback: {
                fn: function (response) {
                    var oResults = JSON.parse(response.serverResponse.responseText);
                    if (oResults && !oResults.hide) {
                        YAHOO.Bubbling.fire("expandButtonClick", {
                            formId: formId,
                            fieldHtmlId: formId + "_expand-button"
                        });
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.failure")
        });

        if (args[1].fieldId == "errands-workflow-form-script") {
            var createFormModule = Alfresco.util.ComponentManager.get(formId);
            var submitElement = createFormModule.runtimeForm.submitElements[0];
            var oldSubmitFunction = submitElement.submitForm;
            var args = {
                callback: oldSubmitFunction,
                scope: createFormModule
            };
            Event.onContentReady(formId + "_assoc_lecm-errands_additional-document-assoc", function () {
                var parentDocRef = this.value;
                if (parentDocRef) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getBaseByAdditional",
                        dataObj: {
                            nodeRef: parentDocRef
                        },
                        successCallback: {
                            fn: function (response) {
                                var baseDoc = response.json;
                                if (baseDoc && baseDoc.isFinal) {
                                    submitElement.submitForm = doBeforeSubmit.bind(createFormModule, args);
                                }
                            }
                        },
                        failureMessage: Alfresco.util.message("message.failure")
                    });
                }
            });
        }
        processTemplatefields();
    }

    function processTemplatefields() {
        if (formId) {
            var formComponent = Alfresco.util.ComponentManager.find({id: formId}, true)[0];
            if (formComponent && formComponent.options && formComponent.options.args) {
                var formArgs = formComponent.options.args;
                if (formArgs["prop_lecm-errands_limitation-date"] || formArgs["readonly_prop_lecm-errands_limitation-date"]) {
                    var limitationDateRadioField = "lecm-errands:limitation-date-radio";
                    var limitationDateRadioReadyEl = LogicECM.module.Base.Util.getComponentReadyElementId(formId, limitationDateRadioField);
                    YAHOO.util.Event.onAvailable(limitationDateRadioReadyEl, function () {
                        var limitationDateRadio = Dom.get(formId + "_prop_" + limitationDateRadioField.replace(":", "_"));
                        var dateRadioButton = YAHOO.util.Selector.query("input[type=radio][value='DATE']", limitationDateRadio.parentElement, true);
                        dateRadioButton.checked = true;
                        limitationDateRadio.value = "DATE";
                        if (formArgs["readonly_prop_lecm-errands_limitation-date"]) {
                            LogicECM.module.Base.Util.readonlyControl(formId, limitationDateRadioField, true);
                        }
                        YAHOO.Bubbling.fire("changeLimitationDateRadio", {
                            value: limitationDateRadio.value,
                            formId: formId,
                            fieldId: limitationDateRadioField
                        });
                    });

                } else if (formArgs["assoc_lecm-errands_type-assoc"]) {
                    var selected = {};
                    selected[formArgs["assoc_lecm-errands_type-assoc"]] = {};
                    YAHOO.Bubbling.fire("errandTypeChanged", {
                        selectedItems: selected,
                        formId: formId,
                        fieldId: "lecm-errands:type-assoc",
                        onlyDateReinit: true
                    });
                }
            }

        }
    }

    function doBeforeSubmit(args) {
        var scope = this;
        var callback = args.callback;
        if (isRouteClick) {
            var routeDialog = new YAHOO.widget.SimpleDialog(formId + '-route-errand-dialog-panel', {
                visible: false,
                draggable: true,
                close: false,
                fixedcenter: true,
                constraintoviewport: true,
                destroyOnHide: true,
                buttons: [
                    {
                        text: Alfresco.util.message("button.ok"),
                        handler: function () {
                            callback.call(scope);
                            routeDialog.hide();
                        },
                        isDefault: true
                    },
                    {
                        text: Alfresco.util.message("button.cancel"),
                        handler: function () {
                            routeDialog.hide();
                        }
                    }
                ]
            });
            routeDialog.setHeader(Alfresco.util.message("ru.it.errands.route.dialog.title"));
            routeDialog.setBody("<p>" + Alfresco.util.message("ru.it.errand.route.message") + "</p>");
            routeDialog.render(document.body);
            routeDialog.show();

        } else {
            callback.call(this);
        }
    }
})();