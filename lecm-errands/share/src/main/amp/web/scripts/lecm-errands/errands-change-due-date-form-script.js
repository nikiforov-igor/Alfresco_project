(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;
    var formId;

    Bubbling.on('errandsWFChangeDueDateScriptLoaded', init);

    function init(){
        formId = args[1].formId;
        Event.onContentReady(formId + "_assoc_packageItems-added", function () {
            var errandRef = Dom.get(formId + "_assoc_packageItems-added").value;
            Alfresco.util.Ajax.request({
                url: Alfresco.constants.PROXY_URI + "lecm/errands/api/hasChildOnLifeCycle",
                dataObj: {
                    nodeRef: errandRef
                },
                successCallback: {
                    fn: function (response) {
                        var hasChildOnLifeCycle = response.json.hasChildOnLifeCycle;
                        if (hasChildOnLifeCycle) {
                            Dom.get(formId + "_prop_lecmErrandWf_changeChildDueDate").value = false;
                            LogicECM.module.Base.Util.hideControl(formId, "lecmErrandWf:changeChildDueDate");
                        }
                    }
                },
                failureMessage: Alfresco.util.message("message.failure")
            });
        });
    }
})();