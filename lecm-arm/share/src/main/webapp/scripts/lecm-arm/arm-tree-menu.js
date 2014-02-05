/**
 * ARM module.
 *
 * @namespace LogicECM.module.ARM
 * @class LogicECM.module.ARM.Tree
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;
    var Event = YAHOO.util.Event;

    LogicECM.module.ARM.Tree = function (htmlId) {
        LogicECM.module.ARM.Tree.superclass.constructor.call(
            this,
            "LogicECM.module.ARM.Tree",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        // Preferences service
        this.preferences = new Alfresco.service.Preferences();
        //this.menuState = null;
        //this.actions = null;

        //YAHOO.Bubbling.on("newReportCreated", this.onNewReportCreated, this);
        //YAHOO.Bubbling.on("dataItemCreated", this.onUpdateTree, this);
        //YAHOO.Bubbling.on("dataItemsDeleted", this.onUpdateTree, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.Tree, Alfresco.component.Base, {

        PREFERENCE_KEY: "ru.it.lecm.arm.menu-state",

        tree: null,

        selectedNode: null,

        preferences: null,

        menuState: null,

        onReady: function () {
            var menu = this;
            this.preferences.request(this.PREFERENCE_KEY,
                {
                    successCallback: {
                        fn: function (p_oResponse) {
                            var menuPref = Alfresco.util.findValueByDotNotation(p_oResponse.json, menu.PREFERENCE_KEY);
                            if (menuPref !== null) {
                                menu.menuState = YAHOO.lang.JSON.parse(menuPref);
                            } else {
                                menu.menuState = {
                                    expanded: "",
                                    selected: "",
                                    redirectUrl: "arm"
                                };
                            }
                            menu._createTree();
                        },
                        scope: this
                    }
                });
        },

        _createTree: function () {
            this.tree = new YAHOO.widget.TreeView(this.id);
            this.tree.singleNodeHighlight = true;
            this.tree.setDynamicLoad(this._loadTree.bind(this));

            var root = this.tree.getRoot();
            this._loadTree(root);

            this.tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                return false;
            }.bind(this));

            this.tree.subscribe('expand', function (node) {
                this.onExpand(node);
                return true;
            }.bind(this));

            this.tree.subscribe('collapse', function (node) {
                this.onCollapse(node);
                return true;
            }.bind(this));
        },

        onCollapse: function (oNode) {
            /*var expandedNodesArray = this.menuState.expanded.trim().split(",");
            var index = expandedNodesArray.indexOf(this._getTextNodeId(oNode));
            if (index >= 0) {
                expandedNodesArray.splice(index, 1);
            }

            this.menuState.expanded = expandedNodesArray.join(",");
            if (this.menuState.expanded.indexOf(",") == 0) {
                this.menuState.expanded = this.menuState.expanded.substr(1, this.menuState.expanded.length - 1);
            }

            this.preferences.set(this.PREFERENCE_KEY, this._buildPreferencesValue());*/
        },

        onExpand: function (oNode) {
            /*var expandedNodesArray = this.menuState.expanded.trim().split(",");
            expandedNodesArray.push(this._getTextNodeId(oNode));

            this.menuState.expanded = expandedNodesArray.join(",");
            if (this.menuState.expanded.indexOf(",") == 0) {
                this.menuState.expanded = this.menuState.expanded.substr(1, this.menuState.expanded.length - 1);
            }

            this.preferences.set(this.PREFERENCE_KEY, this._buildPreferencesValue());*/
        },

        onNewReportCreated: function (layer, args) {
            var obj = args[1];
            var otree = this;
            if ((obj !== null) && (obj.reportId !== null)) {
                var sNode = otree.selectedNode;
                otree._loadTree(sNode);
                sNode.isLeaf = false;
                sNode.expanded = true;
                otree.tree.render();
            }
        },

        onUpdateTree: function (layer, args) {
            this._loadTree(this.selectedNode, function () {
                if (this.selectedNode.children.length == 0) {
                    this.selectedNode.isLeaf = true;
                    this.selectedNode.expanded = false;
                }
                this.tree.render();
            }.bind(this));
        },

        _loadTree: function loadNodeData(node, fnLoadComplete) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/arm/tree-menu?armCode=" + LogicECM.module.ARM.SETTINGS.ARM_CODE;
            if (node.data.nodeRef != null) {
                sUrl += "&nodeRef=" + encodeURI(node.data.nodeRef);
            }

            var otree = this;
            var callback = {
                success: function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        node.children = [];
                        for (var nodeIndex in oResults) {
                            var newNode = {
                                id: oResults[nodeIndex].id,
                                nodeRef: oResults[nodeIndex].nodeRef,
                                label: oResults[nodeIndex].label,
                                isLeaf: oResults[nodeIndex].isLeaf,
                                childType: oResults[nodeIndex].childType,
                                filters: oResults[nodeIndex].filters,
                                searchQuery: oResults[nodeIndex].searchQuery,
                                counterValue: oResults[nodeIndex].counterValue,
                                not_selectable: oResults[nodeIndex].not_selectable
                            };

                            var curElement = new YAHOO.widget.TextNode(newNode, node);
                            curElement.labelElId = curElement.data.id;
                            curElement.id = curElement.data.id;

                            //var nodeId = otree._getTextNodeId(curElement);

                            //curElement.expanded = node.expanded && otree._isNodeExpanded(nodeId);

                            if (curElement.data.counterValue != null && curElement.data.counterValue != "undefined") {
                                curElement.label = curElement.label + " (" + curElement.data.counterValue + ")";
                            }
                            /*if (otree.menuState.selected.length > 0) {
                                if (otree.menuState.selected == nodeId) {
                                    otree._treeNodeSelected(curElement)
                                }
                            }*/
                        }
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        otree.tree.render();
                    }
                },
                failure: function (oResponse) {
                    alert(oResponse);

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    }
                },
                argument: {
                    node: node,
                    fnLoadComplete: fnLoadComplete
                },
                timeout: 20000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },

        _treeNodeSelected: function (node) {
            var nodeId = this._getTextNodeId(node);
            if ((typeof this.selectedNode  === "undefined" || this.selectedNode === null)|| (this.menuState.selected != nodeId)) {
                this.selectedNode = node;
                this.tree.onEventToggleHighlight(node);

                this.menuState.selected = nodeId;

                if (node.data.type == "") {
                    var success = null;
                    if (node.data.filter) {
                        var meta = this.modules.dataGrid.datagridMeta;
                        meta.itemType = node.data.itemType;
                        meta.nodeRef = node.data.nodeRef;
                        meta.searchConfig = {
                            filter: node.data.filter
                        };
                        Bubbling.fire("activeGridChanged",
                            {
                                datagridMeta: meta,
                                bubblingLabel: "documents-arm"
                            }
                        );
                    }
                    //this.preferences.set(this.PREFERENCE_KEY, this._buildPreferencesValue(), {successCallback: success});
                }
            }
        },

        _isNodeExpanded: function (nodeId) {
            if (nodeId && this.menuState.expanded.length > 0) {
                return this._inArray(nodeId, this.menuState.expanded.split(","));
            }
            return false;
        },

        _getTextNodeId: function (node, idsArray) {
            var id = node.id;
            if (id) {
                if (!idsArray) {
                    idsArray = [];
                }
                idsArray.push(id);
                return this._getTextNodeId(node.parent, idsArray);
            } else {
                return idsArray.reverse().join(".");
            }
        },

        _buildPreferencesValue: function () {
            return YAHOO.lang.JSON.stringify(this.menuState);
        },

        _inArray: function (value, array) {
            for (var i = 0; i < array.length; i++) {
                if (array[i] == value) {
                    return true;
                }
            }
            return false;
        }
    });
})();