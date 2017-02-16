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

        Alfresco.util.Ajax.jsonGet({
            url:  Alfresco.constants.PROXY_URI + "/lecm/eds/tree/execution/datasource",
            dataObj: {
                documentNodeRef: nodeRef
            },
            successCallback: {
                fn: function (response) {
                    if (response && response.json) {
                       var items = response.json.items;
                        if (!items || !items.length){
                            var formTabs = Dom.get(formId + "-form-tabs");
                            var executionTab = Selector.query("ul li", formTabs)[2];
                            Dom.setStyle(executionTab, "display", "none");
                        }
                    }
                },
                scope: this
            },
            failureMessage: Alfresco.util.message("message.details.failure"),
            scope: this
        });
    }

})();