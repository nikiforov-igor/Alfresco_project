(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-view-form.css'
    ]);
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;

    Bubbling.on("errandsTabsFormScriptLoaded", processForm);

    function processForm(layer, args){
        var formId = args[1].formId;
        var nodeRef = args[1].nodeRef;

        if (nodeRef) {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "/lecm/eds/tree/execution/datasource",
                dataObj: {
                    documentNodeRef: nodeRef
                },
                successCallback: {
                    fn: function (response) {
                        if (response && response.json) {
                            var items = response.json.items;
                            if (!items || !items.length) {
                                var formTabs = Dom.get(formId + "-form-tabs");
                                var executionTab = Selector.query("ul li", formTabs)[2];
                                var executionTabContents = Selector.query('.yui-content .tab', formTabs)[2];
                                executionTab.parentNode.removeChild(executionTab);
                                executionTabContents.parentNode.removeChild(executionTabContents);
                            }
                        }
                    },
                    scope: this
                },
                failureMessage: Alfresco.util.message("message.details.failure"),
                scope: this
            });
        }
        if (formId) {
            var periodicallyFieldId = "lecm-errands:periodically";
            var field = YAHOO.util.Dom.get(formId + "_prop_" + periodicallyFieldId.replace(":", "_"));
            var periodicallySet = YAHOO.util.Selector.query(".set > .periodicallySet", YAHOO.util.Dom.get(formId), true);
            if (field && field.value == "true" && periodicallySet) {
                    YAHOO.util.Dom.removeClass(periodicallySet, "hidden1");
            } else {
                YAHOO.util.Dom.addClass(periodicallySet, "hidden1");
            }

            var limitationDateControlField = "lecm-errands:limitation-date-cntrl-date";
            var limitationDateRadioField = "lecm-errands:limitation-date-radio";
            var limitationDateControl = Dom.get(formId + "_prop_" + limitationDateControlField.replace(":", "_"));
            if(limitationDateControl.disabled){
                LogicECM.module.Base.Util.readonlyControl(formId, limitationDateRadioField, true);
            }
        }
    }

})();