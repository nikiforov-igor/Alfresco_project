(function(){
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-create-form.css'
    ]);

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;
    var formId,formButtons;
    var isRouteClick = true;

    Bubbling.on('saveDraftButtonClick', saveDraft);
    Bubbling.on('expandButtonClick', toggleSet);
    Bubbling.on('periodicallyErrandChange', toggleSet);
    Bubbling.on('errandsCreateFormScriptLoaded', init);
    Bubbling.on('errandTitleChanged', titleChangeHandler);

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

    function saveDraft(layer,args) {
        var isShort = Dom.getElementBy(function (el) {
            return el.name == "prop_lecm-errands_is-short";
        }, 'input', formId);
        var routeButton = Selector.query(".yui-submit-button", formButtons, true);
        isRouteClick = false;
        isShort.value = "false";
        routeButton.click();
    }

    function titleChangeHandler(layer,args) {
        var textArea = document.getElementsByName("prop_lecm-errands_content")[0];
        if(!args[1] || !textArea || textArea.value.length > 0){
            return;
        }

        var changeValue = "";
        var selectedItems = args[1].selectedItems;
        for (var i in selectedItems) {
            changeValue = selectedItems[i].name;
            break;
        }

        textArea.value = changeValue;
    }

    function init(layer,args){
        formId = args[1].formId;
        formButtons = Dom.get(formId + "-form-buttons");
        Dom.addClass(formButtons, "form-4-buttons");
        Event.onContentReady(formId + "-form-submit-button", function() {
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

        if(args[1].fieldId == "errands-workflow-form-script") {
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
            routeDialog.setHeader("Выполнение действия \"Направить поручение\"");
            routeDialog.setBody("<p>" + Alfresco.util.message("ru.it.errand.route.message") + "</p>");
            routeDialog.render(document.body);
            routeDialog.show();

        } else {
            callback.call(this);
        }
    }
})();