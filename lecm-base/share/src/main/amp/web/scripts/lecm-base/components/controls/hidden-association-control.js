/* global Alfresco, YAHOO */

if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.HiddenAssociationControl = function (htmlId) {
        LogicECM.module.HiddenAssociationControl.superclass.constructor.call(this, "HiddenAssociationControl", htmlId);
        this.fields = {
            base: null,
            added: null
        };
        return this;
    };

    YAHOO.extend(LogicECM.module.HiddenAssociationControl, Alfresco.component.Base, {

        options: {
            valueSetFireAction: null,
            isValueSetFireEvent: false,
            addedXpath: null,
            defaultValue: null
        },
        onReady: function () {
            this.fields.base = Dom.get(this.id);
            this.fields.added = Dom.get(this.id + "-added");
            if (this.options.addedXpath) {
                this.addValue(this.options.addedXpath);
            } else if (this.options.defaultValue) {
                this.addNodeRef(this.options.defaultValue)
            }
        },
        addNodeRef: function (nodeRef) {
            this.fields.base.setAttribute("value", nodeRef);
            this.fields.added.setAttribute("value", nodeRef);
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
            Alfresco.util.Ajax.jsonGet({
                    url: sUrl,
                    dataObj: {
                        titleProperty: "cm:name",
                        xpath: xPath
                    },
                    successCallback: {
                        scope: this,
                        fn: function (response) {
                            var oResults = response.json;
                            if (oResults && oResults.nodeRef) {
                                this.addNodeRef(oResults.nodeRef);
                            }
                        }
                    },
                    failureMessage: this.msg("message.details.failure")
                });
        }
    }, true);
})();
