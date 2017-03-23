(function() {

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;
    var formId;

    Bubbling.on('errandProcessOtherChildAutoClose', processOtherChildAutoClose);

    function processOtherChildAutoClose(layer, args) {
        formId = args[1].formId;
        if (formId) {
            Event.onContentReady(formId + "_assoc_lecm-errands_additional-document-assoc", function () {
                var parentDocRef = this.value;
                if (parentDocRef) {
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                        dataObj: {
                            nodeRef: parentDocRef,
                            substituteString: "{lecm-document:doc-type}"
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response && response.json.formatString) {
                                    var docType = response.json.formatString;
                                    if (docType == "Поручение") {
                                        checkOtherChildAutoClose(parentDocRef);
                                    }
                                }
                            },
                            scope: this
                        },
                        failureMessage: Alfresco.util.message('message.failure'),
                        scope: this
                    });
                }
            });
        }
    }
    function checkOtherChildAutoClose(nodeRef) {
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getChildErrands",
            dataObj: {
                nodeRef: nodeRef
            },
            successCallback: {
                fn: function (response) {
                    var children = response.json;
                    if (children && children.length) {
                        var haveSomeAutoCloseChild = children.some(function (child) {
                            return !!child.autoClose
                        });
                        if (haveSomeAutoCloseChild) {
                            LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:auto-close");
                        }
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.failure")
        });
    }
})();