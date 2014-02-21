if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {

    LogicECM.module.ARM.TreeMenu = function (htmlId) {
        LogicECM.module.ARM.TreeMenu.superclass.constructor.call(
            this,
            "LogicECM.module.ARM.TreeMenu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        // Preferences service
        this.preferences = new Alfresco.service.Preferences();

        //YAHOO.Bubbling.on("updateNodeCounters", this.onUpdateNodeCounters, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.TreeMenu, Alfresco.component.Base, {

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
                                    selected: ""
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

            if (this.menuState.selected == "") {
                //нет выбранного узла - сразу отсылаем событие на перерисовку грида
                YAHOO.Bubbling.fire ("armNodeSelected", {
                    armNode: null,
                    bubblingLabel: "documents-arm"
                });
                YAHOO.Bubbling.fire ("updateArmFilters", {
                    currentNode: null
                });
            }
            var root = this.tree.getRoot();
            this._loadTree(root);

            this.tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                return false;
            }.bind(this));
        },

        _loadTree: function loadNodeData(node, fnLoadComplete) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/arm/tree-menu?armCode=" + LogicECM.module.ARM.SETTINGS.ARM_CODE;
            if (node.data.nodeRef != null) {
                sUrl += "&nodeRef=" + encodeURI(node.data.nodeRef);
                if (node.data.armNodeRef != null) {
                    sUrl += "&armNodeRef=" + encodeURI(node.data.armNodeRef);
                }
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
                                armNodeRef: oResults[nodeIndex].armNodeRef,
                                armNodeId: oResults[nodeIndex].armNodeId,
                                label: oResults[nodeIndex].label,
                                isLeaf: oResults[nodeIndex].isLeaf,
                                types: oResults[nodeIndex].types,
                                columns: oResults[nodeIndex].columns,
                                filters: oResults[nodeIndex].filters,
                                searchQuery: oResults[nodeIndex].searchQuery,
                                counter: oResults[nodeIndex].counter,
                                counterLimit: oResults[nodeIndex].counterLimit,
                                counterDesc: oResults[nodeIndex].counterDesc,
	                            createTypes: oResults[nodeIndex].createTypes
                            };

                            // добавляем элемент в дерево
                            var curElement = new YAHOO.widget.TextNode(newNode, node);
                            curElement.labelElId = curElement.data.id;
                            curElement.id = curElement.data.id;

                            //раскрываем, если этот узел был последним выбранным
                            var nodeId = otree._getTextNodeId(curElement);

                            curElement.expanded = node.expanded && otree._isNodeExpanded(curElement.id);

                            if (otree.menuState.selected.length > 0) {
                                if (otree.menuState.selected == nodeId) {
                                    otree._treeNodeSelected(curElement)
                                }
                            }

                            //отрисовка счетчика, если нужно
                            otree.drawCounterValue(curElement, null);
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

        getSearchQuery: function (node, buffer, parentId) {
            if (node) {
                var query = node.data.searchQuery;
                if (query && query.length > 0) {
                    if (!buffer) {
                        buffer = [];
                    }
                    if (!parentId) {
                        parentId = node.id;
                    }
                    if (parentId == node.id) {
                        buffer.push(query);
                    }
                }
                return this.getSearchQuery(node.parent, buffer, node.data.armNodeId);
            } else {
                var resultedQuery = "";

                if (buffer) {
                    buffer = buffer.reverse();

                    for (var i = 0; i < buffer.length; i++) {
                        var q = buffer[i];
                        resultedQuery += "(" + q + ") AND "
                    }
                }

                return resultedQuery.length > 4 ?
                    resultedQuery.substring(0, resultedQuery.length - 4) : resultedQuery;
            }
        },

        _treeNodeSelected: function (node) {
            this.selectedNode = node;
            this.tree.onEventToggleHighlight(node);

            this.menuState.selected = this._getTextNodeId(node);

            if (node) {
	            YAHOO.Bubbling.fire ("armNodeSelected", {
		            armNode: node,
		            bubblingLabel: "documents-arm"
	            });

	            YAHOO.Bubbling.fire ("updateArmToolbar", {
		            createTypes: node.data.createTypes
	            });

                YAHOO.Bubbling.fire ("updateArmFilters", {
                    currentNode: node
                });
            }
            this.preferences.set(this.PREFERENCE_KEY, this._buildPreferencesValue());
        },

        drawCounterValue: function(node, filterQuery) {
            if (node.data && node.data.counter != null && ("" + node.data.counter == "true")) {
                var searchQuery = this.getSearchQuery(node);
                if (node.data.counterLimit && node.data.counterLimit.length > 0) {
                    searchQuery += " AND (" + node.data.counterLimit + ") ";
                }

                var types = [];
                if (node.data.types != null && node.data.types.length > 0) {
                    types = node.data.types.split(",");
                } else {
                    types.push("lecm-document:base");
                }

                var numTypes = types.length;
                var typesQuery = "";
                for (var count = 0; count < numTypes; count++) {
                    var type = types[count];
                    if (type != null && type.length > 0) {
                        if (typesQuery.length > 0) {
                            typesQuery += " ";
                        }
                        typesQuery += '+TYPE:"' + type + '"'
                    }
                }
                if (searchQuery.length > 0) {
                    Alfresco.util.Ajax.jsonRequest({
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "lecm/count/by-query",
                        dataObj: {
                            query: "(" + typesQuery + ") AND (" + searchQuery + ")"
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                if (oResponse != null) {
                                    var label = node.getLabelEl();
                                    label.innerHTML = node.label + " (" + oResponse.json + ")";
                                }
                            }
                        },
                        failureCallback: {
                            fn: function () {
                                node.label = node.label + " (" + 0 + ")";
                            }
                        }
                    });
                }
            }
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

        _isNodeExpanded: function (nodeId) {
            if (nodeId && this.menuState.selected.length > 0) {
                return this.menuState.selected.indexOf(nodeId) >= 0;
            }
            return false;
        },

        //TODO методы ниже понадобятся, если будет динамически обновлять счетчик
        onUpdateNodeCounters: function (layer, args) {
            var root = this.tree.getRoot();
            for (var count = 0; count < root.children.length; count++) {
                var child = root.children[count];
                this._updateCounterInner(child, args[1].filterQuery);
            }
        },

        _updateCounterInner: function(node, filterQuery){
            this.drawCounterValue(node, filterQuery);
            for (var count = 0; count < node.children.length; count++) {
                var child = node.children[count];
                this._updateCounterInner(child, filterQuery);
            }
        }
    });
})();