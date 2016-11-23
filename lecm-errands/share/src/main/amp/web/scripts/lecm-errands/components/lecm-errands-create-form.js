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

    Bubbling.on('saveDraftButtonClick', saveDraft);
    Bubbling.on('expandButtonClick', toggleSet);
    Bubbling.on('periodicallyErrandChange', toggleSet);
    Bubbling.on('errandsCreateFormScriptLoaded', init);

    function toggleSet(layer, args) {
        var sets = [];
        var fieldHtmlId;
        var hidden;
        var queryTemplate = 'div[class^=\"' + formId + '-form-panel {targetClass}\"]';

        switch (layer) {
            case 'expandButtonClick':
                fieldHtmlId = args[1].fieldHtmlId;
                sets = Selector.query(Substitute(queryTemplate, {
                    targetClass: 'block2'
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
                    targetClass: 'block3'
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

        isShort.value = "false";
        routeButton.click();
    }

    function init(layer,args){
        formId = args[1].formId;
        formButtons = Dom.get(formId + "-form-buttons");

        Dom.addClass(formButtons, "form-4-buttons");

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
            failureMessage: "message.failure"
        });

    }


})();