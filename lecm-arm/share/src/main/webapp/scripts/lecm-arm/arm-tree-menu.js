if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

    LogicECM.module.ARM.TreeMenu = function (htmlId) {
        LogicECM.module.ARM.TreeMenu.superclass.constructor.call(
            this,
            "LogicECM.module.ARM.TreeMenu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        // Preferences service
        this.preferences = new Alfresco.service.Preferences();
	    this.accordionItems = [];
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.TreeMenu, Alfresco.component.Base, {

        PREFERENCE_KEY: "ru.it.lecm.arm.menu-state",

        tree: null,

        selectedNode: null,

        preferences: null,

        menuState: null,

	    shownAccordionItem: null,

	    accordionItems: null,

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
	                                accordion: "",
                                    selected: "",
                                    pageNum: 1
                                };
                            }
                            menu.createAccordion();
                        },
                        scope: this
                    }
                });
        },

	    createAccordion: function() {
		    var sUrl = Alfresco.constants.PROXY_URI + "lecm/arm/tree-menu?armCode=" + LogicECM.module.ARM.SETTINGS.ARM_CODE;
		    var me = this;
		    var callback = {
			    success: function (oResponse) {
				    var oResults = eval("(" + oResponse.responseText + ")");
				    if (oResults != null) {
					    me.accordionItems = oResults;
					    var accordionContent = "";
					    for (var i = 0; i < me.accordionItems.length; i++) {
						    accordionContent += me.getAccordionItemHtml(me.accordionItems[i]);
					    }
					    Dom.get(me.id + "-headlines").innerHTML = accordionContent;
					    me.initAccordion();
				    }
			    },
			    failure: function (oResponse) {
				    Alfresco.util.PopupManager.displayMessage(
					    {
						    text:me.msg("message.details.failure") + ": " + oResponse
					    });
			    },
			    scope: this
		    };
		    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
	    },

	    getAccordionItemHtml: function(node) {
			var result = "";
		    result += "<li><div id='ac-head-" + node.id +"' class='accordion-head'><span id='" + "ac-label-" + node.id + "' class='accordion-label'>";
		    result += node.label;
		    result += "</span></div><div id='ac-content-" + node.id + "' class='accordion-content wait-container'><div class='wait'></div></div></li>";
		    return result;
	    },

        initAccordion: function () {
            var context = this;
            if (this.accordionItems != null) {
                for (var i = 0; i < this.accordionItems.length; i++) {
                    var node = this.accordionItems[i];

                    var forceExpand = this.menuState.accordion == node.id || ((this.menuState.accordion == null || this.menuState.accordion.length == 0) && i == 0);

                    Event.onAvailable("ac-head-" + node.id, function (obj) {
                        Event.on("ac-head-" + obj.node.id, 'click', this.onAccordionClick, obj.node, this);
                        if (obj.forceExpand) {
                            this.onAccordionClick(null, obj.node);
                        }
                        Event.onAvailable("ac-label-" + obj.node.id, function (obj) {
                            obj.context.drawCounterValue(obj.node, obj.context.getSearchQuery(obj.node), YAHOO.util.Dom.get("ac-label-" + obj.node.id));
                        }, {node: obj.node, context: context}, this);
                    }, {node: node, forceExpand: forceExpand}, this);
                }
            }
        },

	    onAccordionClick : function(e, node) {
		    if (this.shownAccordionItem == null || this.shownAccordionItem != node) {
			    if (!node.createdTree) {
				    this._createTree(node);
				    node.createdTree = true;
			    }
			    if (this.shownAccordionItem != null) {
			        this.collapseAccordion(this.shownAccordionItem);
			    }
			    this.expandAccordion(node);
			    this.shownAccordionItem = node;
		    }
	    },

	    expandAccordion: function(node) {
		    Dom.addClass("ac-head-" + node.id, "shown");
		    Dom.addClass("ac-content-" + node.id, "shown");
		    this.menuState.accordion = node.id;
	    },

	    collapseAccordion: function(node) {
		    Dom.removeClass("ac-head-" + node.id, "shown");
		    Dom.removeClass("ac-content-" + node.id, "shown");
	    },

	    getAccordionHeight: function() {
		    return Dom.getY("lecm-content-ft") - Dom.getY(this.id) - 30 * this.accordionItems.length;
	    },

        _createTree: function (node) {
            this.tree = new YAHOO.widget.TreeView("ac-content-" + node.id);
            this.tree.singleNodeHighlight = true;
            this.tree.setDynamicLoad(this._loadTree.bind(this));

            if (this.menuState.selected == "") {
                //нет выбранного узла - сразу отсылаем событие на перерисовку грида
                YAHOO.Bubbling.fire ("armNodeSelected", {
                    armNode: null,
                    bubblingLabel: "documents-arm",
                    menuState:this.menuState
                });
                YAHOO.Bubbling.fire ("updateArmFilters", {
                    currentNode: null
                });
            }
            var root = this.tree.getRoot();
	        root.data.nodeRef  = node.nodeRef;
	        root.data.armNodeRef  = node.armNodeRef;
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
                                nodeType: oResults[nodeIndex].nodeType,
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
                            if (curElement.id) {
                                Event.onAvailable(curElement.id, function (obj) {
                                    obj.context.drawCounterValue(obj.node.data, obj.context.getSearchQuery(obj.node), obj.node.getLabelEl());
                                }, {node: curElement, context:otree}, this);
                            }
                        }
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        otree.tree.render();
                    }
                },
                failure: function (oResponse) {
	                Alfresco.util.PopupManager.displayMessage(
		                {
			                text:otree.msg("message.arm.load.failure")
		                });

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    }
                },
                argument: {
                    node: node,
                    fnLoadComplete: fnLoadComplete
                },
                timeout: 60000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },

        getSearchQuery: function (node, buffer, parentId) {
            if (node) {
                var query = node.data ? node.data.searchQuery : node.searchQuery;
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
                return this.getSearchQuery(node.parent, buffer, node.data ? node.data.armNodeId : node.armNodeId);
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
	        if (this.selectedNode != null) {
	            this.selectedNode.tree.onEventToggleHighlight(this.selectedNode);
	        }

            this.selectedNode = node;
            this.tree.onEventToggleHighlight(node);

            this.menuState.selected = this._getTextNodeId(node);

            if (node) {
	            var isReportNode = node.data.nodeType == "lecm-arm:reports-node";
	            Dom.setStyle("arm-documents-grid", "display", isReportNode ? "none" : "block");
	            Dom.setStyle("arm-documents-reports", "display", !isReportNode ? "none" : "block");

	            if (isReportNode) {
		            YAHOO.Bubbling.fire ("updateArmReports", {
			            types: node.data.types
		            });
	            }

	            YAHOO.Bubbling.fire ("armNodeSelected", {
		            armNode: node,
		            bubblingLabel: "documents-arm",
                    menuState:this.menuState
	            });

	            YAHOO.Bubbling.fire ("updateArmToolbar", {
		            createTypes: node.data.createTypes
	            });

                YAHOO.Bubbling.fire ("updateArmFilters", {
                    currentNode: node,
	                isReportNode: isReportNode
                });
            }
            // сбрасываем после того, как отослали запрос к гриду
            this.menuState.pageNum = 1;
            this.preferences.set(this.PREFERENCE_KEY, this._buildPreferencesValue());
        },

        drawCounterValue: function (data, query, labelElement) {
            if ((data && data.counter != null && ("" + data.counter == "true"))) {
                var searchQuery = query;
                if (data.counterLimit && data.counterLimit.length > 0) {
                    searchQuery += " AND (" + data.counterLimit + ") ";
                }

                var types = [];
                if (data.types != null && data.types.length > 0) {
                    types = data.types.split(",");
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
                if (typesQuery.length > 0) {
                    if (searchQuery.length > 0) {
                        searchQuery = "(" + typesQuery + ") AND (" + searchQuery + ")";
                    } else {
                        searchQuery = typesQuery;
                    }
                }

                if (searchQuery.length > 0) {
                    Alfresco.util.Ajax.jsonRequest({
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "lecm/count/by-query",
                        dataObj: {
                            query: searchQuery
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                if (oResponse != null) {
                                    if (labelElement) {
	                                    var counterSpan = "<span title=\"" + data.counterDesc + "\" class=\"accordion-counter-label\">";
	                                    counterSpan += "(" + oResponse.json + ")";
	                                    counterSpan += "</span>";

                                        labelElement.innerHTML = labelElement.innerHTML + counterSpan;
                                    }
                                }
                            }
                        },
                        failureCallback: {
                            fn: function () {
                                if (labelElement) {
                                    labelElement.innerHTML = labelElement.innerHTML + " (-)";
                                }
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
        }
    });
})();