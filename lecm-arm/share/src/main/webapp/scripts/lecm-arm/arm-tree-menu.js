if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Anim = YAHOO.util.Anim;

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
                                    selected: ""
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
		    result += "<li><div id='ac-head-" + node.nodeRef +"' class='accordion-head'><span>";
		    result += node.label;
		    result += "</span></div><div id='ac-content-" + node.nodeRef + "' class='accordion-content'></div></li>";
		    return result;
	    },

	    initAccordion : function() {
		    if (this.accordionItems != null) {
			    for (var i = 0; i < this.accordionItems.length; i++) {
				    var node = this.accordionItems[i];

				    var forceExpand = this.menuState.accordion == node.id || ((this.menuState.accordion == null || this.menuState.accordion.length == 0) && i == 0);

				    YAHOO.util.Event.onAvailable("ac-head-" + node.nodeRef, function (obj) {
					    YAHOO.util.Event.on("ac-head-" + obj.node.nodeRef, 'click', this.onAccordionClick, obj.node, this);
					    if (obj.forceExpand) {
						    this.onAccordionClick(null, obj.node);
					    }
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
		    Dom.addClass("ac-head-" + node.nodeRef, "shown");
		    Dom.addClass("ac-content-" + node.nodeRef, "shown");
		    var attributes = {
			    height: {
				    from: 0,
				    to: this.getAccordionHeight()
			    },
			    opacity: {
				    from: 0,
				    to: 1
			    }
		    };

		    var anim = new Anim("ac-content-" + node.nodeRef, attributes, .6, YAHOO.util.Easing.backOut);
		    anim.animate();
		    this.menuState.accordion = node.id;
	    },

	    collapseAccordion: function(node) {
		    Dom.removeClass("ac-head-" + node.nodeRef, "shown");
		    Dom.removeClass("ac-content-" + node.nodeRef, "shown");
		    var attributes = {
			    height: {
				    from: this.getAccordionHeight(),
				    to: 0
			    },
			    opacity: {
				    from:1,
				    to:0
			    }
		    };
		    var anim = new Anim("ac-content-" + node.nodeRef, attributes, .6, YAHOO.util.Easing.easeBoth);
		    anim.animate();
	    },

	    getAccordionHeight: function() {
		    return Dom.getY("lecm-content-ft") - Dom.getY(this.id) - 30 * this.accordionItems.length;
	    },

        _createTree: function (node) {
            this.tree = new YAHOO.widget.TreeView("ac-content-" + node.nodeRef);
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