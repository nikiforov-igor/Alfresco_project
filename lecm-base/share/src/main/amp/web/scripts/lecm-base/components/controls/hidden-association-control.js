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
        this.selectedItems = {};

        return this;
    };

    YAHOO.extend(LogicECM.module.HiddenAssociationControl, Alfresco.component.Base, {

        selectedItems: null,

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
            this.loadSelectedItems();
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
        },

        loadSelectedItems: function () {

            var arrItems = "",
                value = this.fields.base.value;

            if (this.fields.base.value) {
                arrItems = value;
            }

            var onSuccess = function (response)
            {
                var items = response.json.data.items,
                    item;
                this.selectedItems = {};

                for (var i = 0, il = items.length; i < il; i++) {
                    item = items[i];
                    this.selectedItems[item.nodeRef] = item;
                }
            };

            var onFailure = function (response)
            {
                this.selectedItems = {};
            };

            if (arrItems !== "") {

                var items = (arrItems.indexOf(",") > 0) ? arrItems.split(",") : arrItems.split(";");

                Alfresco.util.Ajax.jsonRequest(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
                        method: "POST",
                        dataObj:
                            {
                                items: items,
                                itemValueType: "nodeRef"
                            },
                        successCallback:
                            {
                                fn: onSuccess,
                                scope: this
                            },
                        failureCallback:
                            {
                                fn: onFailure,
                                scope: this
                            }
                    });
            }
        }
    }, true);
})();
