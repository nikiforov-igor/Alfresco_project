/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom;

    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.AssociationTreeControl = function (htmlId) {
        LogicECM.module.AssociationTreeControl.superclass.constructor.call(this, "AssociationTreeControl", htmlId);
        YAHOO.Bubbling.on("hideControl", this.onHideControl, this);
        YAHOO.Bubbling.on("showControl", this.onShowControl, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.AssociationTreeControl, Alfresco.component.Base,
        {
            tree: null,
            eventGroup: null,
            currentNode: null,
            rootNode: null,

            options: {
                prefixPickerId: null,
                showParentNodeInTreeView: true,
                changeItemsFireAction: null,
                currentValue: "",
                mandatory: false,
                rootLocation: "/app:company_home",
                rootNodeRef: "",
                itemType: "cm:content",
                treeRoteNodeTitleProperty: "cm:name",
                treeNodeSubstituteString: "",
                treeNodeTitleSubstituteString: "",
                sortProp: null,
                // скрывать ли игнорируемые ноды в дереве
                ignoreNodesInTreeView: true,
                ignoreNodes: null,
                treeBranchesDatasource: "lecm/components/association-tree",
                useDeferedReinit: false,
                fieldId: null,
                formId: false
            },

            onReady: function () {
                this.eventGroup = (this.options.prefixPickerId == null ? this.id + '-cntrl' : this.options.prefixPickerId) + Dom.generateId();

                if (this.options.useDeferedReinit) {
                    this.reinitDeferedList = new Alfresco.util.Deferred(["eventRecieved", "rootNodeLoaded"],
                        {
                            fn: this.deferredReinit,
                            scope: this
                        });
                }

                this.init();
            },

            init: function () {
                this.options.controlId = this.id + '-cntrl';
                if (this.options.prefixPickerId == null) {
                    this.options.prefixPickerId = this.options.controlId;
                }
                this.options.pickerId = this.options.prefixPickerId + '-picker';

                this.fillPickerDialog();

                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            },

            // Когда фокус приходит в дерево, делаем выбранным
            // либо ранее выбранный элемент, если он был,
            // либо первый - корневой - элемент
            //focusToTheTree: function (a, args) {
            //	var e = args[1];
            //	var node = this.selectedTreeNode ? this.selectedTreeNode : this.rootNode;
            //
            //	node.focus();
            //	this.treeViewClicked(node);
            //	this.tree.onEventToggleHighlight(node);
            //	Event.stopPropagation(e);
            //},

            // Fill tree view group selector with node data
            fillPickerDialog: function () {
                this.tree = new YAHOO.widget.TreeView(this.options.pickerId + "-groups");
                this.tree.singleNodeHighlight = true;
                this.tree.setDynamicLoad(this._loadNode.bind(this));

                this.tree.subscribe('clickEvent', function (event) {
                    event.node.focus();
                    this.treeViewClicked(event.node);
                    this.tree.onEventToggleHighlight(event);
                    return false;
                }.bind(this));

                this._loadRootNode();
            },

            _loadRootNode: function () {
                var sUrl = this._generateRootUrlPath(this.options.rootNodeRef) + this._generateRootUrlParams();
                var me = this;
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: sUrl,
                        successCallback: {
                            fn: function (response) {

                                if (me.options.useDeferedReinit) {
                                    me.reinitDeferedList.fulfil("rootNodeLoaded");
                                }

                                var oResults = response.json;
                                if (oResults != null) {
                                    if (me.options.showParentNodeInTreeView) {
                                        var newNode = {
                                            label: oResults.title,
                                            nodeRef: oResults.nodeRef,
                                            isLeaf: oResults.isLeaf,
                                            type: oResults.type,
                                            isContainer: oResults.isContainer,
                                            displayPath: oResults.displayPath,
                                            path: oResults.path,
                                            simplePath: oResults.simplePath,
                                            renderHidden: true
                                        };
                                        me.rootNode = new YAHOO.widget.TextNode(newNode, me.tree.getRoot());
                                    } else {
                                        me.rootNode = me.tree.getRoot();
                                        var augmented = Alfresco.util.deepCopy(me.tree.getRoot());
                                        augmented.data = {
                                            nodeRef: oResults.nodeRef
                                        };
                                        me._loadNode(augmented);
                                    }
                                    me.options.rootNodeRef = oResults.nodeRef;

                                    me.tree.draw();

                                    me.treeViewClicked(me.rootNode);
                                    me.tree.onEventToggleHighlight(me.rootNode);
                                }
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (oResponse) {
                                var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                                this.widgets.dataTable.set("MSG_ERROR", response.message);
                                this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                            },
                            scope: this
                        }
                    });
            },

            _generateRootUrlPath: function (nodeRef) {
                return $combine(Alfresco.constants.PROXY_URI, "/lecm/forms/node/search", nodeRef.replace("://", "/"));
            },

            _generateRootUrlParams: function () {
                return "?titleProperty=" + encodeURIComponent(this.options.treeRoteNodeTitleProperty) +
                    "&xpath=" + encodeURIComponent(this.options.rootLocation);
            },

            _loadNode: function (node, fnLoadComplete) {
                var sUrl = this._generateItemsUrlPath(node.data.nodeRef) + this._generateItemsUrlParams();

                var callback = {
                    success: function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        if (oResults != null) {
                            node.children = [];
                            for (var nodeIndex in oResults) {
                                var nodeRef = oResults[nodeIndex].nodeRef;
                                var ignore = false;
                                if (this.argument.context.options.ignoreNodesInTreeView) {
                                    var ignoreNodes = this.argument.context.options.ignoreNodes;
                                    if (ignoreNodes != null) {
                                        for (var i = 0; i < ignoreNodes.length; i++) {
                                            if (ignoreNodes[i] == nodeRef) {
                                                ignore = true;
                                            }
                                        }
                                    }
                                }

                                if (!ignore) {
                                    var newNode = {
                                        label: oResults[nodeIndex].label,
                                        title: oResults[nodeIndex].title,
                                        nodeRef: oResults[nodeIndex].nodeRef,
                                        isLeaf: oResults[nodeIndex].isLeaf,
                                        type: oResults[nodeIndex].type,
                                        isContainer: oResults[nodeIndex].isContainer,
                                        renderHidden: true
                                    };

                                    new YAHOO.widget.TextNode(newNode, node);
                                }
                            }
                        }

                        if (oResponse.argument.fnLoadComplete != null) {
                            oResponse.argument.fnLoadComplete();
                        } else {
                            oResponse.argument.tree.draw();
                        }
                    },
                    failure: function (oResponse) {
                        var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                        this.widgets.dataTable.set("MSG_ERROR", response.message);
                        this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                    },
                    argument: {
                        node: node,
                        fnLoadComplete: fnLoadComplete,
                        tree: this.tree,
                        context: this
                    },
                    timeout: 60000
                };
                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            },

            _generateItemsUrlPath: function (nodeRef) {
                return $combine(Alfresco.constants.PROXY_URI, "/" + this.options.treeBranchesDatasource + "/", nodeRef.replace("://", "/"), "items");
            },

            _generateItemsUrlParams: function () {

                var params = "?nodeSubstituteString=" + encodeURIComponent(this.options.treeNodeSubstituteString) +
                    "&nodeTitleSubstituteString=" + encodeURIComponent(this.options.treeNodeTitleSubstituteString) +
                    "&selectableType=" + encodeURIComponent(this.options.itemType);

                if (this.options.sortProp != null) {
                    params += "&sortProp=" + encodeURIComponent(this.options.sortProp);
                }
                return params;
            },

            treeViewClicked: function (node) {
                this.currentNode = node;
                this.updateFormFields();
            },

            // Updates all form fields
            updateFormFields: function (clearCurrentDisplayValue, changeItemsFireAction) {
                // Just element
                if (clearCurrentDisplayValue == null) {
                    clearCurrentDisplayValue = true;
                }
                if (changeItemsFireAction == null) {
                    changeItemsFireAction = true;
                }
                var el;

                var addItems = this.getAddedItems();

                // Update added fields in main form to be submitted
                el = Dom.get(this.options.controlId + "-added");
                if (el != null) {
                    if (clearCurrentDisplayValue) {
                        el.value = '';
                    }
                    var i;
                    for (i in addItems) {
                        el.value += ( i < addItems.length - 1 ? addItems[i] + ',' : addItems[i] );
                    }
                }

                var removedItems = this.getRemovedItems();

                // Update removed fields in main form to be submitted
                var removedEl = Dom.get(this.options.controlId + "-removed");
                if (removedEl != null) {
                    removedEl.value = '';
                    for (i in removedItems) {
                        removedEl.value += (i < removedItems.length - 1 ? removedItems[i] + ',' : removedItems[i]);
                    }
                }

                var selectedElements = this.getSelectedItems();
                // Update removed fields in main form to be submitted
                el = Dom.get(this.id);
                if (el != null) {
                    el.value = '';
                    for (i in selectedElements) {
                        el.value += (i < selectedElements.length - 1 ? selectedElements[i] + ',' : selectedElements[i]);
                    }
                }

                if (this.options.mandatory) {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                }

                YAHOO.Bubbling.fire("formValueChanged",
                    {
                        eventGroup: this,
                        addedItems: addItems,
                        removedItems: removedItems,
                        selectedItem: this.currentNode
                    });

                if (changeItemsFireAction && this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
                    YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
                        selectedItem: this.currentNode,
                        formId: this.options.formId,
                        fieldId: this.options.fieldId,
                        control: this
                    });
                }
            },

            getAddedItems: function () {
                var addedItems = [],
                    currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

                if (this.currentNode != null) {
                    if (!(this.currentNode.data.nodeRef in currentItems)) {
                        addedItems.push(this.currentNode.data.nodeRef);
                    }
                }
                return addedItems;
            },

            getRemovedItems: function () {
                var removedItems = [],
                    currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

                var selectedItems = [];
                if (this.currentNode != null) {
                    selectedItems.push(this.currentNode.data.nodeRef);
                }

                for (var item in currentItems) {
                    if (currentItems.hasOwnProperty(item)) {
                        if (!(item in selectedItems)) {
                            removedItems.push(item);
                        }
                    }
                }
                return removedItems;
            },

            getSelectedItems:function () {
                var selectedItems = [];
                if (this.currentNode != null) {
                    selectedItems.push(this.currentNode.data.nodeRef);
                }
                return selectedItems;
            },

            onHideControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    YAHOO.util.Dom.setStyle(this.id + '-cntrl-edt', "display", "none");
                }
            },
            onShowControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    YAHOO.util.Dom.setStyle(this.id + '-cntrl-edt', "display", "block");
                }
            }
        });
})();
