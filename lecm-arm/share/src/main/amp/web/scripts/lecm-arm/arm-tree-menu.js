if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Anim = YAHOO.util.Anim,
        Lang = YAHOO.lang,
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
        YAHOO.Bubbling.on("appendInputField", this._appendInputField, this);
        YAHOO.Bubbling.on("resetSearch", this._resetSearch, this);
        return this;
    };

    /*Объект узла дерева на основе YAHOO.widget.TextNode*/
    LogicECM.module.ARM.Node = function (oData, oParent, expanded) {
        LogicECM.module.ARM.Node.superclass.constructor.call(this, oData, oParent, expanded);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.Node, YAHOO.widget.TextNode);
    YAHOO.lang.augmentObject(LogicECM.module.ARM.Node.prototype, {
        setCounterHtml: function (html) {
            this.data.counterHTML = html;
        },

        getContentHtml: function () {
            var sb = [];
            sb[sb.length] = this.href ? '<a' : '<span';
            sb[sb.length] = ' id="' + Lang.escapeHTML(this.labelElId) + '"';
            sb[sb.length] = ' class="' + Lang.escapeHTML(this.labelStyle) + '"';
            if (this.href) {
                sb[sb.length] = ' href="' + Lang.escapeHTML(this.href) + '"';
                sb[sb.length] = ' target="' + Lang.escapeHTML(this.target) + '"';
            }
            if (this.title) {
                sb[sb.length] = ' title="' + Lang.escapeHTML(this.title) + '"';
            }
            sb[sb.length] = ' >';
            sb[sb.length] = Lang.escapeHTML(this.label);
            if (this.data.counter && this.data.counterHTML) {
                sb[sb.length] = this.data.counterHTML;
            }
            sb[sb.length] = this.href ? '</a>' : '</span>';
            return sb.join("");
        }
    }, true);

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
                        me.accordionItems = oResults.children;

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
                if (!node.tree) {
                    if (node.id == "calendar") {
                        this._createCalendar();
                    } else {
                        this._createTree(node);
                    }
                } else {
                    this.tree = node.tree;
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
            node.tree = new YAHOO.widget.TreeView("ac-content-" + node.id);
            this.tree = node.tree;
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
	        root.data.baseNodeId  = node.id;
            root.id  = node.id;
            this._loadTree(root);

            this.tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                return false;
            }.bind(this));

            this.tree.subscribe('expandComplete', this.onExpandComplete, this.tree, this);
            this.tree.subscribe('collapseComplete', this.onCollapseComplete, this.tree, this);
        },

        _loadTree: function loadNodeData(node, fnLoadComplete) {
            var sUrl = this._prepareUrl(node);
            var otree = this;
            var callback = {
                success: function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        var children = oResults.children;
                        node.children = [];
                        var parentNodeInfo = oResults.parentNodeInfo;
                        node.data.realChildrenCount = parentNodeInfo.parentNodeRealChildrenCount;
                        for (var nodeIndex in children) {
                            var newNode = {
                                id: children[nodeIndex].id + "-" + children[nodeIndex].armNodeId,
                                nodeRef: children[nodeIndex].nodeRef,
                                nodeType: children[nodeIndex].nodeType,
                                armNodeRef: children[nodeIndex].armNodeRef,
                                armNodeId: children[nodeIndex].armNodeId,
                                baseNodeId: children[nodeIndex].id,
                                label: children[nodeIndex].label,
                                isLeaf: children[nodeIndex].isLeaf,
                                types: children[nodeIndex].types,
                                columns: children[nodeIndex].columns,
                                filters: children[nodeIndex].filters,
                                searchQuery: children[nodeIndex].searchQuery,
                                counter: children[nodeIndex].counter,
                                counterLimit: children[nodeIndex].counterLimit,
                                counterDesc: children[nodeIndex].counterDesc,
                                htmlUrl: children[nodeIndex].htmlUrl,
                                maxItems: children[nodeIndex].maxItems,
                                realChildrenCount: children[nodeIndex].realChildrenCount,
                                reportCodes: children[nodeIndex].reportCodes,
                                searchType: children[nodeIndex].searchType,
                                runAs: children[nodeIndex].runAs,
                                isAggregate: children[nodeIndex].isAggregate,
                                config: {
                                    showDelay: 100,
                                    hideDelay: 200,
                                    autoDismissDelay: 0,
                                    disabled: false,
                                    value: null,
                                    mode: 'create',
                                    destination: children[nodeIndex].nodeRef,
                                    formId: ''
                                }
                            };

                            // добавляем элемент в дерево
                            var curElement = new LogicECM.module.ARM.Node(newNode, node);
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
                                    obj.context.drawCounterValue(obj.node, obj.context.getSearchQuery(obj.node), obj.node.getLabelEl());
                                }, {node: curElement, context: otree}, this);
                            }
                        }
                    }

                    if (node.data.realChildrenCount > node.data.maxItems && node.data.maxItems > 0 && node.data.realChildrenCount > node.children.length) {
                        var loadNextBlockNode = new YAHOO.widget.TextNode({
                            label: Alfresco.util.message("label.arm.node.open"),
                            labelStyle: 'load-next-block-node',
                            isLeaf: true,
                            isAggregate: false
                        }, node, false, false);
                        node.data.loadNextBlockNode = loadNextBlockNode;
                        loadNextBlockNode.data.isServiceNode = true;
                        loadNextBlockNode.data.clickHandler = function () {
                            node.data.skipCount = node.children.length - 1;
                            var url = otree._prepareUrl(node);
                            YAHOO.util.Connect.asyncRequest('GET', url, {
                                success: function (oResponse) {
                                    var oResults = eval("(" + oResponse.responseText + ")");
                                    children = oResults && oResults.children;
                                    otree._appendChildrenNodes(node, children);
                                    otree.tree.render();
                                    otree._deleteInsituEditors(node.tree.getRoot());
                                    otree._createInsituEditors(node.tree.getRoot());
                                },
                                argument: {
                                    node: node
                                }
                            });
                        };
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        otree.tree.render();
                        otree._deleteInsituEditors(node.tree.getRoot());
                        otree._createInsituEditors(node.tree.getRoot());
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

        _prepareUrl: function (node) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/arm/tree-menu?armCode=" + LogicECM.module.ARM.SETTINGS.ARM_CODE + "&noCache=" + new Date().getTime();
            if (node.data.nodeRef) {
                sUrl += "&nodeRef=" + encodeURI(node.data.nodeRef);
                if (node.data.armNodeRef) {
                    sUrl += "&armNodeRef=" + encodeURI(node.data.armNodeRef);
                }
                if (node.data.runAs) {
                    sUrl += "&runAs=" + encodeURI(node.data.runAs);
                }
                if (node.data.skipCount) {
                    sUrl += "&skipCount=" + encodeURI(node.data.skipCount);
                }
                if (node.data.searchTerm && node.data.searchTerm.length > 0) {
                    sUrl += "&searchTerm=" + encodeURI(node.data.searchTerm);
                }
                if (node.data.maxItems) {
                    sUrl += "&maxItems=" + encodeURI(node.data.maxItems);
                }
                if (node.tree.root && node.tree.root.data.nodeRef) {
                    sUrl += "&currentSection=" + encodeURI(node.tree.root.data.nodeRef);
                }
            }
            return sUrl;
        },

        _appendChildrenNodes: function (node, children) {
            if (node.data.loadNextBlockNode) {
                node.tree.popNode(node.data.loadNextBlockNode);
            }
            if (children != null && children.length) {
                for (var nodeIndex in children) {
                    var newNode = {
                        id: children[nodeIndex].id + "-" + children[nodeIndex].armNodeId,
                        nodeRef: children[nodeIndex].nodeRef,
                        nodeType: children[nodeIndex].nodeType,
                        armNodeRef: children[nodeIndex].armNodeRef,
                        armNodeId: children[nodeIndex].armNodeId,
                        baseNodeId: children[nodeIndex].id,
                        label: children[nodeIndex].label,
                        isLeaf: children[nodeIndex].isLeaf,
                        types: children[nodeIndex].types,
                        columns: children[nodeIndex].columns,
                        filters: children[nodeIndex].filters,
                        searchQuery: children[nodeIndex].searchQuery,
                        counter: children[nodeIndex].counter,
                        counterLimit: children[nodeIndex].counterLimit,
                        counterDesc: children[nodeIndex].counterDesc,
                        htmlUrl: children[nodeIndex].htmlUrl,
                        maxItems: children[nodeIndex].maxItems,
                        realChildrenCount: children[nodeIndex].realChildrenCount,
                        reportCodes: children[nodeIndex].reportCodes,
                        searchType: children[nodeIndex].searchType,
                        runAs: children[nodeIndex].runAs,
                        isAggregate: children[nodeIndex].isAggregate,
                        config: {
                            showDelay: 100,
                            hideDelay: 200,
                            autoDismissDelay: 0,
                            disabled: false,
                            value: null,
                            mode: 'create',
                            destination: children[nodeIndex].nodeRef,
                            formId: ''
                        }
                    };

                    // добавляем элемент в дерево
                    var curElement = new YAHOO.widget.TextNode(newNode, node);
                    curElement.labelElId = curElement.data.id;
                    curElement.id = curElement.data.id;

                    //раскрываем, если этот узел был последним выбранным
                    var nodeId = this._getTextNodeId(curElement);

                    curElement.expanded = node.expanded && this._isNodeExpanded(curElement.id);

                    if (!this.menuState.selected || this.menuState.selected == nodeId) {
                        this._treeNodeSelected(curElement)
                    }

                    //отрисовка счетчика, если нужно
                    if (curElement.id) {
                        Event.onAvailable(curElement.id, function (obj) {
                            obj.context.drawCounterValue(obj.node.data, obj.context.getSearchQuery(obj.node), obj.node.getLabelEl());
                        }, {node: curElement, context: this}, this);
                    }
                }

                if (node.data.realChildrenCount > node.data.maxItems && node.data.maxItems > 0 && node.data.realChildrenCount > node.children.length) {
                    node.data.loadNextBlockNode && (node.data.loadNextBlockNode.appendTo(node));
                }
            }
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
            if (node.data.isServiceNode) {
                if (YAHOO.lang.isFunction(node.data.clickHandler)) {
                    node.data.clickHandler();
                }
                return;
            }

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
                    createTypes: parent.data.createTypes,
                    currentNodeArgs: {
                        nodeRef: node.data.nodeRef,
                        nodeType: node.data.nodeType
                    }
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

        drawCounterValue: function (node, query, labelElement) {
            var data = node.data ? node.data : node;
            var label = node.label;
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

                if (searchQuery.length) {
                    Alfresco.util.Ajax.jsonRequest({
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "lecm/count/by-query",
                        dataObj: {
                            query: searchQuery
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                if (oResponse && labelElement) {
                                    var counterSpan = "<span title=\"" + data.counterDesc + "\" class=\"accordion-counter-label\">";
                                    counterSpan += "(" + (oResponse.json !== null ? oResponse.json : "-" ) + ")";
                                    counterSpan += "</span>";
                                    if (YAHOO.lang.isFunction(node.setCounterHtml)) {
                                        node.setCounterHtml(counterSpan);
                                    }
                                    labelElement.innerHTML = labelElement.innerHTML != label ? counterSpan : (labelElement.innerHTML + counterSpan);
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
            return this.PREFERENCE_KEY + encodeURIComponent(LogicECM.module.ARM.SETTINGS.ARM_CODE) + ".menu-state." + encodeURIComponent(LogicECM.currentUser);
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
        },

        _appendInputField: function () {
            var node = arguments[1][1].armNode,
                me = this;
            if (!Dom.get(node.id + '-arm-tree-attached-input-field') && node.expanded) {
                var inputField = document.createElement('input'),
                    contentEl = node.getContentEl();
                inputField.setAttribute('id', node.id + '-arm-tree-attached-input-field');
                inputField.setAttribute('placeholder', Alfresco.util.message("label.arm.node.search.input"));
                if (node.data.afterSearch) {
                    inputField.setAttribute('value', node.data.searchTerm);
                }
                Dom.addClass(inputField, 'arm-tree-attached-input-field');
                Event.removeListener(node.tree.getEl(), 'keydown', node.tree._onKeyDownEvent);
                inputField.onkeydown = function (event) {
                    if (event.keyCode == 13) {
                        Event.stopEvent(event);
                        node.data.searchTerm = event.currentTarget.value;
                        node.data.skipCount = 0;
                        node.data.afterSearch = node.data.searchTerm.length > 0;
                        me._loadTree(node);
                    }
                };
                inputField.onblur = function (event) {
                    if (contentEl) {
                        contentEl.removeChild(inputField)
                    }
                };
                inputField.onfocus = function (event) {
                    this.value = this.value;
                };
                if (contentEl) {
                    contentEl.appendChild(inputField)
                }
                inputField.focus();
            }
        },

        _resetSearch: function () {
            var node = arguments[1][1].armNode;
            node.data.searchTerm = '';
            node.data.skipCount = 0;
            node.data.afterSearch = false;
            this._loadTree(node);
        },

        onExpandComplete: function (node, tree) {
            this._createInsituEditors(tree.getRoot());
        },

        onCollapseComplete: function (node, tree) {
            this._toggleNodeVisualStyle(node);
        },

        _createInsituEditors: function (node) {
            node.children.forEach(function (childNode) {
                if ((childNode.expanded && childNode.data.maxItems > 0 || childNode.data.afterSearch) && !childNode.data.insituEditor) {

                    childNode.data.config.treeNode = childNode;
                    childNode.data.config.container = childNode.getContentEl();
                    childNode.data.config.context = childNode.getContentEl().parentElement;
                    if (childNode.data.config) {
                        childNode.data.config.title = Alfresco.util.message("label.arm.node.search");
                    }
                    childNode.data.insituEditor = new Alfresco.widget.SearchButton(null, childNode.data.config);
                    if (!childNode.data.resetButton && childNode.data.afterSearch) {
                        if (childNode.data.config) {
                            childNode.data.config.title = Alfresco.util.message("label.arm.node.reset");
                        }
                        childNode.data.resetButton = new Alfresco.widget.ResetButton(null, childNode.data.config);
                    }
                }
                if (childNode.expanded && !childNode.isLeaf) {
                    this._createInsituEditors(childNode);
                }
                this._toggleNodeVisualStyle(childNode);
            }, this);
        },

        _deleteInsituEditors: function (node) {
            node.children.forEach(function (childNode) {
                if (childNode.data.insituEditor) {
                    delete childNode.data.insituEditor;
                }
                if (childNode.data.resetButton) {
                    delete childNode.data.resetButton;
                }
                if (childNode.expanded && !childNode.isLeaf) {
                    this._deleteInsituEditors(childNode);
                }
            }, this);
        },

        _toggleNodeVisualStyle: function (node) {
            if (node.data.maxItems > 0 || node.data.afterSearch) {
                var tableEl = YAHOO.util.Selector.query('#' + node.getEl().id + ' > table.ygtvtable')[0];
                if (node.expanded) {
                    if (node.data.insituEditor) {
                        node.data.insituEditor.disabled = false;
                        Dom.setStyle(node.data.insituEditor.editIcon, "visibility", "visible");
                    }

                    var serviceNodesCount = this._getServiceNodes(node).length;
                    if (node.data.afterSearch) {
                        Dom.removeClass(tableEl, 'arm-tree-icon-container-expanded');
                        Dom.addClass(tableEl, 'arm-tree-icon-container-after-search');
                        var title = YAHOO.lang.substitute(Alfresco.util.message("label.arm.node.search.tooltip"), [
                            node.children.length - serviceNodesCount,
                            node.data.realChildrenCount,
                            node.data.searchTerm
                        ]);
                        if (node.data.insituEditor) {
                            node.data.insituEditor.editIcon.setAttribute('title', title);
                        }
                        if (tableEl) {
                            tableEl.setAttribute('title', title);
                        }
                    } else {
                        Dom.removeClass(tableEl, 'arm-tree-icon-container-after-search');
                        Dom.addClass(tableEl, 'arm-tree-icon-container-expanded');
                        if (tableEl) {
                            var title = YAHOO.lang.substitute(Alfresco.util.message("label.arm.node.tooltip"), [
                                node.children.length - serviceNodesCount,
                                node.data.realChildrenCount
                            ]);
                            tableEl.setAttribute('title', title);
                        }
                    }
                } else {
                    Dom.removeClass(tableEl, 'arm-tree-icon-container-after-search');
                    Dom.removeClass(tableEl, 'arm-tree-icon-container-expanded');
                    if (node.data.insituEditor) {
                        node.data.insituEditor.disabled = true;
                        Dom.setStyle(node.data.insituEditor.editIcon, "visibility", "hidden");
                    }
                }
            }
        },

        _getServiceNodes: function (node) {
            return node.children.filter(function (childNode) {
                return childNode.data.isServiceNode;
            })
        }
    });

    Alfresco.widget.SearchButton = function (p_editor, p_params) {
        var span = document.createElement('span');
        Alfresco.widget.SearchButton.superclass.constructor.call(this, p_editor, p_params);
        Dom.addClass(this.editIcon, 'insitu-search-on-node');
        span.innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;';
        this.params.container.appendChild(span);
        var contentEl = this.params.treeNode.getContentEl();
        if (contentEl) {
            Dom.addClass(contentEl, 'arm-tree-node-to-attach');
        }
        return this;
    };

    YAHOO.lang.extend(Alfresco.widget.SearchButton, Alfresco.widget.InsituEditorIcon, {
        onIconClick: function (e, obj) {
            if (obj.disabled) {
                return;
            }

            var node = obj.params.treeNode;
            YAHOO.Bubbling.fire('appendInputField', {
                armNode: node,
                bubblingLabel: "documents-arm"
            });

            Event.stopEvent(e);
        },

        _fadeOut: function (p_element) {
            return;
        }
    }, true);

    Alfresco.widget.ResetButton = function (p_editor, p_params) {
        var span = document.createElement('span');
        Alfresco.widget.ResetButton.superclass.constructor.call(this, p_editor, p_params);
        Dom.addClass(this.editIcon, 'reset-search-button');
        span.innerHTML = '&nbsp;&nbsp;&nbsp;';
        this.params.container.appendChild(span);
        return this;
    };

    YAHOO.lang.extend(Alfresco.widget.ResetButton, Alfresco.widget.InsituEditorIcon, {
        onIconClick: function (e, obj) {
            if (obj.disabled) {
                return;
            }

            var node = obj.params.treeNode;
            YAHOO.Bubbling.fire('resetSearch', {
                armNode: node,
                bubblingLabel: "documents-arm"
            });

            Event.stopEvent(e);
        },

        _fadeOut: function (p_element) {
            return;
        }
    }, true);
})();
