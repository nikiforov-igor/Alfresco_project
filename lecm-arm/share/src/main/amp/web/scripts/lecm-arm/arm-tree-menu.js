if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
    var Dom = YAHOO.util.Dom,
        Anim = YAHOO.util.Anim,
        Event = YAHOO.util.Event;

    LogicECM.module.ARM.TreeMenu = function (htmlId) {
        LogicECM.module.ARM.TreeMenu.superclass.constructor.call(
            this,
            "LogicECM.module.ARM.TreeMenu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        this.accordionItems = [];

        YAHOO.Bubbling.on("updateCurrentColumns", this.onUpdateSelectedColumns, this);
        YAHOO.Bubbling.on("armRefreshSelectedTreeNode", this.onRefreshSelectedTreeNode, this);
        YAHOO.Bubbling.on("armRefreshParentSelectedTreeNode", this.onRefreshParentSelectedTreeNode, this);
        YAHOO.Bubbling.on("beforeDateChanged", this.onCalSelect, this);
        YAHOO.Bubbling.on("selectedParentCurrentNode", this.onSelectedParentCurrentNode, this);
        YAHOO.Bubbling.on("selectedCurrentNode", this.onSelectedCurrentNode, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.TreeMenu, Alfresco.component.Base, {

        PREFERENCE_KEY: "ru.it.lecm.arm.",

        tree: null,

        selectedNode: null,

        menuState: null,

        shownAccordionItem: null,

        accordionItems: null,

        accordionHeight: null,

        expiresDate: new Date(),

        calendarNode: null,

        expandedChildren: [],

        notSingleQueryPattern: /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i,

        onReady: function () {
            var date = new Date;
            date.setDate(date.getDate() + 30);
            this.expiresDate = date;

            if (LogicECM.module.ARM.SETTINGS.ARM_PATH.hasOwnProperty("accordion")) {
                LogicECM.module.Base.Util.setCookie(this._buildPreferencesKey(), JSON.stringify(LogicECM.module.ARM.SETTINGS.ARM_PATH), {expires:this.expiresDate});
            }

            var menu = this;
            var menuPref = LogicECM.module.Base.Util.getCookie(this._buildPreferencesKey());
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

        createAccordion: function() {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/arm/tree-menu?armCode=" + LogicECM.module.ARM.SETTINGS.ARM_CODE;
            var me = this;
            var callback = {
                success: function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        me.accordionItems = oResults;

                        if (LogicECM.module.ARM.SETTINGS.ARM_SHOW_CALENDAR) {
                            this.calendarNode = {
                                id: "calendar",
                                label: this.msg("lecm.arm.lbl.calendar"),
                                createTypes: [
                                    {
                                        disabled: false,
                                        label: this.msg("lecm.arm.lbl.event"),
                                        type: "lecm-events:document",
                                        page: "event-create"
                                    }
                                ]
                            };

                            me.accordionItems.push(this.calendarNode);
                        }

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
            result += "<li><div id='ac-head-" + node.id +"' class='accordion-head'>";
            result += node.label;
            result += "<span id='" + "ac-label-" + node.id + "' class='accordion-label'></span>";
            result += "</div><div id='ac-content-" + node.id + "' class='accordion-content wait-container'><div class='wait'></div></div></li>";
            return result;
        },

        initAccordion: function () {
            var context = this;
            if (this.accordionItems != null) {
                var forceAvailable = false;
                for (var i = 0; i < this.accordionItems.length; i++) {
                    var node = this.accordionItems[i];

                    var forceExpand = this.menuState.accordion == node.id || ((this.menuState.accordion == null || this.menuState.accordion.length == 0) && i == 0);
                    forceAvailable = forceAvailable || forceExpand;

                    Event.onAvailable("ac-head-" + node.id, function (obj) {
                        Event.on("ac-head-" + obj.node.id, 'click', this.onAccordionClick, obj.node, this);
                        if (obj.forceExpand) {
                            this.onAccordionClick(null, obj.node);
                            if (obj.node.id == "calendar") {
                                this.onCalSelect();
                                YAHOO.Bubbling.fire("dateChanged",
                                    {
                                        date: new Date()
                                    });
                            }
                        }
                        Event.onAvailable("ac-label-" + obj.node.id, function (obj) {
                            obj.context.drawCounterValue(obj.node, obj.context.getSearchQuery(obj.node), YAHOO.util.Dom.get("ac-label-" + obj.node.id));
                        }, {node: obj.node, context: context}, this);
                    }, {node: node, forceExpand: forceExpand}, this);
                }
                if (!forceAvailable) {
                    var node = this.accordionItems[0];
                    if (node != null) {
                        this.onAccordionClick(null, node);
                    }
                }
            }
        },

        onAccordionClick : function(e, node) {
            if (this.shownAccordionItem == null || this.shownAccordionItem != node) {
                if (!node.createdTree) {
                    if (node.id == "calendar") {
                        this._createCalendar();
                    } else {
                        this._createTree(node);
                    }
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

            var anim = new Anim("ac-content-" + node.id, attributes, .6, YAHOO.util.Easing.backOut);
            anim.animate();
            this.menuState.accordion = node.id;
        },

        collapseAccordion: function(node) {
            Dom.removeClass("ac-head-" + node.id, "shown");
            Dom.removeClass("ac-content-" + node.id, "shown");

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
            var anim = new Anim("ac-content-" + node.id, attributes, .6, YAHOO.util.Easing.easeBoth);
            anim.animate();
        },

        getAccordionHeight: function() {
            if (this.accordionHeight == null) {
                this.accordionHeight = Dom.getY("lecm-content-ft") - Dom.getY(this.id) - 34 * this.accordionItems.length;
            }
            return this.accordionHeight;
        },

        _createCalendar: function() {
            var parentNode = Dom.get("ac-content-calendar");
            parentNode.innerHTML = "";

            var miniCalendar = Dom.get("arm-mini-calendar");
            Dom.setStyle(miniCalendar.id, "display", "block");
            parentNode.appendChild(miniCalendar);
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
                    menuState:this.menuState,
                    isNotGridNode:false
                });
            }
            var root = this.tree.getRoot();
            root.data.nodeRef  = node.nodeRef;
            root.data.armNodeRef  = node.armNodeRef;
            root.data.createTypes  = node.createTypes;
            root.data.searchQuery  = node.searchQuery;
            root.data.searchType  = node.searchType;
	        root.data.runAs  = node.runAs;
            root.id  = node.id;
            this._loadTree(root);

            this.tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                return false;
            }.bind(this));
        },

        _loadTree: function loadNodeData(node, fnLoadComplete) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/arm/tree-menu?armCode=" + LogicECM.module.ARM.SETTINGS.ARM_CODE + "&noCache=" + new Date().getTime();
            if (node.data.nodeRef != null) {
                sUrl += "&nodeRef=" + encodeURI(node.data.nodeRef);
                if (node.data.armNodeRef != null) {
                    sUrl += "&armNodeRef=" + encodeURI(node.data.armNodeRef);
                }
                if (node.data.runAs != null) {
                    sUrl += "&runAs=" + encodeURI(node.data.runAs);
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
                                id: oResults[nodeIndex].id + "-" + oResults[nodeIndex].armNodeId,
                                nodeRef: oResults[nodeIndex].nodeRef,
                                nodeType: oResults[nodeIndex].nodeType,
                                armNodeRef: oResults[nodeIndex].armNodeRef,
                                armNodeId: oResults[nodeIndex].armNodeId,
                                baseNodeId: oResults[nodeIndex].id,
                                label: oResults[nodeIndex].label,
                                isLeaf: oResults[nodeIndex].isLeaf,
                                types: oResults[nodeIndex].types,
                                columns: oResults[nodeIndex].columns,
                                filters: oResults[nodeIndex].filters,
                                searchQuery: oResults[nodeIndex].searchQuery,
                                counter: oResults[nodeIndex].counter,
                                counterLimit: oResults[nodeIndex].counterLimit,
                                counterDesc: oResults[nodeIndex].counterDesc,
                                htmlUrl: oResults[nodeIndex].htmlUrl,
                                reportCodes: oResults[nodeIndex].reportCodes,
	                            searchType: oResults[nodeIndex].searchType,
                                runAs:oResults[nodeIndex].runAs,
                                isAggregate: oResults[nodeIndex].isAggregate
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
                            } else {
                                otree._treeNodeSelected(curElement);
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
                    var include = node.data ? !node.data.isAggregate : true;
                    var baseNodeId = node.data ? node.data.baseNodeId : node.baseNodeId;
                    if (!buffer) {
                        buffer = [];
                        include = true;
                    }
                    if (!parentId) {
                        parentId = baseNodeId;
                    }
                    if ((parentId == baseNodeId ||
                        (parentId + '-' + (node.data.runAs ? new Alfresco.util.NodeRef(node.data.runAs).id : "")) == baseNodeId) && include) {
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
                        var isSingleNotQuery = q.indexOf("NOT") == 0 && !this.notSingleQueryPattern.test(q.trim());
                        resultedQuery += ((!isSingleNotQuery && q.length > 0 ? "(" : "") + q + (!isSingleNotQuery && q.length > 0 ? ")" : "" ) + " AND ");
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

            // При смене узла дерева - выбрать первую страницу, а не сохраненную
            if (this.menuState.selected != this._getTextNodeId(node)) {
                this.menuState.pageNum = 1;
            }

            this.menuState.selected = this._getTextNodeId(node);

            if (node) {

                LogicECM.module.Base.Util.resetAdditionalObjects();

                if (node.data.nodeType == "lecm-dic:dictionary") {
                    node.data.nodeType = "lecm-arm:html-node";
                    node.data.htmlUrl = "page/dictionary?dic=" + node.label;
                }

                var isReportNode = node.data.nodeType == "lecm-arm:reports-node";
                var isHtmlNode = node.data.htmlUrl != null && node.data.htmlUrl.length > 0;
                var isNotGridNode = isReportNode || isHtmlNode;

                Dom.setStyle("arm-documents-toolbar", "display", "block");
                Dom.setStyle("arm-documents-grid", "display", isNotGridNode ? "none" : "block");
                Dom.setStyle("arm-documents-reports", "display", !isReportNode ? "none" : "block");
                Dom.setStyle("arm-documents-html", "display", !isHtmlNode ? "none" : "block");

                Dom.setStyle("arm-calendar-toolbar", "display", "none");
                Dom.setStyle("arm-calendar", "display", "none");

                if (isReportNode) {
                    YAHOO.Bubbling.fire ("updateArmReports", {
                        types: node.data.types,
                        reportCodes: node.data.reportCodes
                    });
                    YAHOO.Bubbling.fire ("updateArmFilters", {
                        currentNode: null,
                        isNotGridNode: isNotGridNode
                    });
                }
                if (isHtmlNode) {
                    LogicECM.module.Base.Util.saveAdditionalObjects();
                    YAHOO.Bubbling.fire ("updateArmHtmlNode", {
                        armNode: node
                    });
                    YAHOO.Bubbling.fire ("updateArmFilters", {
                        currentNode: null,
                        isNotGridNode: isNotGridNode
                    });
                } else {
                    YAHOO.Bubbling.fire ("armNodeSelected", {
                        armNode: node,
                        bubblingLabel: "documents-arm",
                        menuState:this.menuState,
                        isNotGridNode: isNotGridNode
                    });
                }

                var parent = node;
                while (parent.parent != null) {
                    parent = parent.parent;
                }

                YAHOO.Bubbling.fire ("updateArmToolbar", {
                    createTypes: parent.data.createTypes
                });
            }
            // сбрасываем после того, как отослали запрос к гриду
            this.menuState.pageNum = 1;

            LogicECM.module.Base.Util.setCookie(this._buildPreferencesKey(), this._buildPreferencesValue(), {expires:this.expiresDate});
        },

        onCalSelect: function (e, args) {
            LogicECM.module.Base.Util.setCookie(this._buildPreferencesKey(), YAHOO.lang.JSON.stringify({
                accordion: "calendar",
                selected: "",
                pageNum: 1
            }), {expires:this.expiresDate});

            YAHOO.Bubbling.fire ("updateArmToolbar", {
                createTypes: this.calendarNode.createTypes
            });

            Dom.setStyle("arm-documents-toolbar", "display", "none");
            Dom.setStyle("arm-documents-grid", "display", "none");
            Dom.setStyle("arm-documents-reports", "display", "none");
            Dom.setStyle("arm-documents-html", "display", "none");

            Dom.setStyle("arm-calendar-toolbar", "display", "block");
            Dom.setStyle("arm-calendar", "display", "block");
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
                        var useBrack = searchQuery.indexOf("NOT") < 0;
                        searchQuery = "(" + typesQuery + ") AND " + (useBrack ? "(" : "" ) + searchQuery + (useBrack ? ")" : "" );
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
                                        counterSpan += "(" + (oResponse.json !== null ? oResponse.json : "-" )+ ")";
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
            if (node != null) {
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
            } else {
                return idsArray.reverse().join(".");
            }
        },

        _buildPreferencesValue: function () {
            return YAHOO.lang.JSON.stringify(this.menuState);
        },

        _buildPreferencesKey: function () {
            return this.PREFERENCE_KEY +  LogicECM.module.ARM.SETTINGS.ARM_CODE + ".menu-state." + LogicECM.currentUser;
        },

        _isNodeExpanded: function (nodeId) {
            if (this.expandedChildren.length != 0) {
                var index = this.expandedChildren.indexOf(nodeId);
                if (index >= 0) {
                    this.expandedChildren.splice(index, 1);
                    return true;
                }
            } else if (nodeId && this.menuState.selected.length > 0) {
                return this.menuState.selected.indexOf(nodeId) >= 0;
            }
            return false;
        },

        onUpdateSelectedColumns: function (layer, args) {
            var columns = args[1].selectedColumns;
            var menu = this;
            if (columns != null) {
                var ref = this.selectedNode.data.nodeRef;
                if (!ref || this.selectedNode.data.nodeType != "lecm-arm:node") {
                    ref = this.selectedNode.data.armNodeRef;
                }
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/arm/save/user-columns",
                    dataObj: {
                        columns: YAHOO.lang.JSON.stringify({
                            selected:columns
                        }),
                        nodeRef: ref
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            if (oResponse.json) {
                                menu._loadTree(menu.selectedNode.parent);
                            }
                        }
                    },
                    failureCallback: {
                        fn: function (oResponse) {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            }
        },

        onRefreshSelectedTreeNode: function() {
            this._loadTree(this.selectedNode);
        },

        onRefreshParentSelectedTreeNode: function() {
            var nodeParent = this.selectedNode.parent;
            this.expandedChildren = [];
            for (var i=0; i < nodeParent.children.length; i++) {
                if (nodeParent.children[i].expanded) {
                    this.expandedChildren.push(nodeParent.children[i].id);
                }
            }
            this._loadTree(nodeParent);
        },

        onSelectedParentCurrentNode: function() {
            this._treeNodeSelected(this.selectedNode.parent);
        },

        onSelectedCurrentNode: function() {
            this._treeNodeSelected(this.selectedNode);
        }
    });
})();
