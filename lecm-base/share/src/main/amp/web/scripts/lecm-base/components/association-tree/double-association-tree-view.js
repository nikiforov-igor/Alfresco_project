/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        KeyListener = YAHOO.util.KeyListener,
        Selector = YAHOO.util.Selector,
        Util = LogicECM.module.Base.Util;

    var $html = Alfresco.util.encodeHTML,
        $combine = Alfresco.util.combinePaths,
        $hasEventInterest = Alfresco.util.hasEventInterest;

    var IDENT_CREATE_NEW = "~CREATE~NEW~";

    LogicECM.module.DoubleAssociationTreeViewer = function (htmlId, globalDoublePickerItems) {
        LogicECM.module.DoubleAssociationTreeViewer.superclass.constructor.call(this, htmlId);
        this.doubleAssocPickerSelectedItems = globalDoublePickerItems;

        return this;
    };

    YAHOO.extend(LogicECM.module.DoubleAssociationTreeViewer, LogicECM.module.AssociationTreeViewer,
        {
            doubleAssocPickerSelectedItems: {},

/*            onReady: function AssociationTreeViewer_onReady()
            {
                LogicECM.module.DoubleAssociationTreeViewer.superclass.onReady.call(this);
            },*/

            _loadSelectedItems: function AssociationTreeViewer__loadSelectedItems(clearCurrentDisplayValue, updateForms) {
                var arrItems = "";
                if (this.options.selectedValue != null) {
                    arrItems = this.options.selectedValue;
                }
                else if (this.options.currentValue != null && this.isNodeRef(this.options.currentValue)) {
                    arrItems = this.options.currentValue;
                }

                if (arrItems == "" && this.defaultValue != null) {
                    arrItems += this.defaultValue;
                }

                var onSuccess = function AssociationTreeViewer__loadSelectedItems_onSuccess(response) {
                    var items = response.json.data.items,
                        item;
                    this.selectedItems = {};

                    this.singleSelectedItem = null;
                    for (var i = 0, il = items.length; i < il; i++) {
                        item = items[i];
                        if (!this.options.checkType || item.type == this.options.itemType) {
                            if (this.doubleAssocPickerSelectedItems[item.nodeRef] == null) {
                                this.selectedItems[item.nodeRef] = item;

                                if (!this.options.multipleSelectMode && this.singleSelectedItem == null) {
                                    this.singleSelectedItem = item;
                                }

                                // добавим в глобальный объект
                                this.doubleAssocPickerSelectedItems[item.nodeRef] = item;
                            }
                        }
                    }

                    if (!this.options.disabled) {
                        this.updateSelectedItems();
                        this.updateAddButtons();
                    }
                    if (updateForms) {
                        this.updateFormFields(clearCurrentDisplayValue);
                    }
                };

                var onFailure = function AssociationTreeViewer__loadSelectedItems_onFailure(response) {
                    this.selectedItems = null;
                };

                if (arrItems !== "") {
                    Alfresco.util.Ajax.jsonRequest(
                        {
                            url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
                            method: "POST",
                            dataObj: {
                                items: arrItems.split(","),
                                itemValueType: "nodeRef",
                                itemNameSubstituteString: this.options.nameSubstituteString,
                                sortProp: this.options.sortProp,
                                selectedItemsNameSubstituteString: this.getSelectedItemsNameSubstituteString()
                            },
                            successCallback: {
                                fn: onSuccess,
                                scope: this
                            },
                            failureCallback: {
                                fn: onFailure,
                                scope: this
                            }
                        });
                }
                else {
                    // if disabled show the (None) message
                    this.selectedItems = {};
                    this.singleSelectedItem = null;
                    if (!this.options.disabled) {
                        this.updateSelectedItems();
                        this.updateAddButtons();
                    } else if (Dom.get(this.options.controlId + "-currentValueDisplay") != null && Dom.get(this.options.controlId + "-currentValueDisplay").innerHTML.trim() === "") {
                        Dom.get(this.options.controlId + "-currentValueDisplay").innerHTML = this.msg("form.control.novalue");
                    }
                }
            },

            addSelectedItem: function AssociationTreeViewer_addSelectedItems(nodeRef) {
                var onSuccess = function AssociationTreeViewer_addSelectedItems_onSuccess(response) {
                    var items = response.json.data.items,
                        item;

                    //this.singleSelectedItem = null;
                    if (!this.options.multipleSelectMode && items[0]) {
                        this.selectedItems = {};
                        item = items[0];
                        if (this.doubleAssocPickerSelectedItems[item.nodeRef] == null) {
                            this.selectedItems[item.nodeRef] = item;

                            this.singleSelectedItem = items[0];

                            // добавим в глобальный объект
                            this.doubleAssocPickerSelectedItems[item.nodeRef] = item;
                        }
                    } else {
                        for (var i = 0, il = items.length; i < il; i++) {
                            item = items[i];
                            if (this.doubleAssocPickerSelectedItems[item.nodeRef] == null) {
                                this.selectedItems[item.nodeRef] = item;
                                // добавим в глобальный объект
                                this.doubleAssocPickerSelectedItems[item.nodeRef] = item;
                            }
                        }
                    }

                    if (!this.options.disabled) {
                        this.updateSelectedItems();
                        this.updateAddButtons();
                    }
                    this.updateFormFields();
                };

                var onFailure = function AssociationTreeViewer_addSelectedItems_onFailure(response) {

                };

                if (nodeRef !== "") {
                    Alfresco.util.Ajax.jsonRequest(
                        {
                            url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
                            method: "POST",
                            dataObj: {
                                items: nodeRef.split(","),
                                itemValueType: "nodeRef",
                                itemNameSubstituteString: this.options.nameSubstituteString,
                                sortProp: this.options.sortProp,
                                selectedItemsNameSubstituteString: this.getSelectedItemsNameSubstituteString()
                            },
                            successCallback: {
                                fn: onSuccess,
                                scope: this
                            },
                            failureCallback: {
                                fn: onFailure,
                                scope: this
                            }
                        });
                }
            },

            onSelectedItemAdded: function AssociationTreeViewer_onSelectedItemAdded(layer, args) {
                // Check the event is directed towards this instance
                if ($hasEventInterest(this, args)) {
                    var obj = args[1];
                    if (obj && obj.item) {
                        if (this.doubleAssocPickerSelectedItems[obj.item.nodeRef] == null) {
                            this.selectedItems[obj.item.nodeRef] = obj.item;
                            this.singleSelectedItem = obj.item;

                            this.updateAddedSelectedItem(obj.item);
                            if (!this.options.multipleSelectMode) {
                                this.updateAddButtons();
                            } else if (this.addItemButtons.hasOwnProperty(obj.item.nodeRef)) {
                                var button = this.addItemButtons[obj.item.nodeRef];
                                Dom.setStyle(button, "display", this.canItemBeSelected(obj.item.nodeRef) ? "inline" : "none");
                            }
                            // добавим в глобальный объект
                            this.doubleAssocPickerSelectedItems[obj.item.nodeRef] = obj.item;
                        }
                    }
                }
            },

            removeNode: function AssociationTreeViewer_removeNode(event, params) {
                delete this.selectedItems[params.node.nodeRef];
                delete this.doubleAssocPickerSelectedItems[params.node.nodeRef];
                this.singleSelectedItem = null;
                this.updateSelectedItems();
                this.updateAddButtons();
                if (params.updateForms) {
                    this.updateFormFields();
                }
            }
        });
})();