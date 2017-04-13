/* global Alfresco, YAHOO */

if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.HiddenAssociationControl = function (htmlId) {
        LogicECM.module.HiddenAssociationControl.superclass.constructor.call(this, "HiddenAssociationControl", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.HiddenAssociationControl, Alfresco.component.Base, {

        fieldValues: [],

        options: {
            valueSetFireAction: null,
            isValueSetFireEvent: false,
            addedXpath: null,
            defaultValue: null
        },
        onReady: function () {
            if (this.options.addedXpath) {
                this.addValue(this.options.addedXpath);
            } else if (this.options.defaultValue) {
                this.addNodeRef(this.options.defaultValue)
            }
        },
        addNodeRef: function (nodeRef) {
            Dom.get(this.id).setAttribute("value", nodeRef);
            Dom.get(this.id + "-added").setAttribute("value", nodeRef);
            if (this.options.isValueSetFireEvent && this.options.valueSetFireAction) {
                YAHOO.Bubbling.fire(this.options.valueSetFireAction, {
                    items: nodeRef,
                    formId: this.options.formId,
                    fieldId: this.options.fieldId,
                    control: this
                });
            }
        },
        addValue: function (xPath) {
            var sUrl = Alfresco.constants.PROXY_URI + "/lecm/forms/node/search";
            Alfresco.util.Ajax.jsonGet(
                {
                    url: sUrl,
                    dataObj: {
                        titleProperty: encodeURIComponent("cm:name"),
                        xpath: encodeURIComponent(xPath)
                    },
                    successCallback: {
                        fn: function (response) {
                            var oResults = response.json;
                            if (oResults && oResults.nodeRef) {
                                this.addNodeRef(oResults.nodeRef);
                            }
                        },
                        scope: this
                    },
                    failureMessage: Alfresco.util.message("message.details.failure"),
                    scope: this
                });
        }
    }, true);
})();
