/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module.ReportsEditor
 * @class LogicECM.module.ReportsEditor.Tree
 */
(function () {
    LogicECM.module.ReportsEditor.Tree = function (htmlId) {
        LogicECM.module.ReportsEditor.Tree.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.Tree",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        this.menuState = null;
        this.actions = null;

        YAHOO.Bubbling.on("newReportCreated", this.onNewReportCreated, this);
        YAHOO.Bubbling.on("dataItemsDeleted", this.onUpdateTree, this);
        YAHOO.Bubbling.on("datagridRefresh", this.onUpdateTree, this);
        YAHOO.Bubbling.on("importFileSuccess", this.onUpdateTree, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.Tree, Alfresco.component.Base, {

        PREFERENCE_KEY: "ru.it.lecm.reports-editor.state.",

        tree: null,

        selectedNode: null,

        menuState: null,

        actions: null,

        expiresDate: new Date(),

        onReady: function () {
            var date = new Date;
            date.setDate(date.getDate() + 30);
            this.expiresDate = date;

            var menu = this;
            var menuPref = LogicECM.module.Base.Util.getCookie(this.PREFERENCE_KEY  + encodeURIComponent(LogicECM.currentUser));
            if (menuPref !== null) {
                menu.menuState = YAHOO.lang.JSON.parse(menuPref);
            } else {
                menu.menuState = {
                    expanded: "reports",
                    selected: "reports",
                    redirectUrl: "reports-editor"
                };
            }
            menu._createTree();
        },

        _createTree: function () {
            this.actions = [];

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

            this.tree.subscribe('expandComplete', function (node) {
                this.onExpandComplete(node);
                return true;
            }.bind(this));

            this.onExpandComplete(null);
        },

        onCollapse: function (oNode) {
            var expandedNodesArray = this.menuState.expanded.trim().split(",");
            var index = expandedNodesArray.indexOf(this._getTextNodeId(oNode));
            if (index >= 0) {
                expandedNodesArray.splice(index, 1);
            }

            this.menuState.expanded = expandedNodesArray.join(",");
            if (this.menuState.expanded.indexOf(",") == 0) {
                this.menuState.expanded = this.menuState.expanded.substr(1, this.menuState.expanded.length - 1);
            }

            LogicECM.module.Base.Util.setCookie(this.PREFERENCE_KEY  + encodeURIComponent(LogicECM.currentUser), this._buildPreferencesValue(), {expires:this.expiresDate});
        },

        onExpand: function (oNode) {
            var expandedNodesArray = this.menuState.expanded.trim().split(",");
            expandedNodesArray.push(this._getTextNodeId(oNode));

            this.menuState.expanded = expandedNodesArray.join(",");
            if (this.menuState.expanded.indexOf(",") == 0) {
                this.menuState.expanded = this.menuState.expanded.substr(1, this.menuState.expanded.length - 1);
            }

            LogicECM.module.Base.Util.setCookie(this.PREFERENCE_KEY  + encodeURIComponent(LogicECM.currentUser), this._buildPreferencesValue(), {expires:this.expiresDate});
        },

        onExpandComplete: function (oNode) {
            for (var i in this.actions) {
                Alfresco.util.createInsituEditor(
                    this.actions[i].context,
                    this.actions[i].params,
                    this.actions[i].callback
                );
            }
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
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/reports-editor/menu";
            if (node.data.nodeRef != null) {
                sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
                if (node.data.childType != null) {
                    sUrl += "&childType=" + encodeURI(node.data.childType);
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
                                label: oResults[nodeIndex].label,
                                isLeaf: oResults[nodeIndex].isLeaf,
                                title: oResults[nodeIndex].title,
                                redirect: oResults[nodeIndex].redirect,
                                childType: oResults[nodeIndex].childType,
                                actions: oResults[nodeIndex].actions
                            };

                            var curElement = new YAHOO.widget.TextNode(newNode, node);
                            curElement.labelElId = curElement.data.id;
                            curElement.id = curElement.data.id;

                            var nodeId = otree._getTextNodeId(curElement);

                            curElement.expanded = node.expanded && otree._isNodeExpanded(nodeId);

                            if (otree.menuState.selected.length > 0) {
                                if (otree.menuState.selected == nodeId) {
                                    otree.tree.onEventToggleHighlight(curElement);// начальная подсветка узла
                                    otree._treeNodeSelected(curElement)
                                }
                            }
                            if (curElement.data.actions && curElement.data.actions != "") {
                                otree.actions.push(
                                    {
                                        context: curElement.labelElId,
                                        params: {
                                            showDelay: 300,
                                            hideDelay: 300,
                                            type: curElement.data.actions,
                                            curElem: curElement,
                                            reportTree: otree
                                        },
                                        callback: null
                                    });
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
                    alert(oResponse);

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    }
                },
                argument: {
                    node: node,
                    fnLoadComplete: fnLoadComplete
                },
                timeout: 10000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },

        _treeNodeSelected: function (node) {
            var nodeId = this._getTextNodeId(node);
            this.selectedNode = node;
            if (this.menuState.selected != nodeId) { // сработает при переключени узла
                this.tree.onEventToggleHighlight(node);
            }

            this.menuState.selected = nodeId;

            var success = null;
            if (node.data.redirect) {
                var url = YAHOO.lang.substitute(node.data.redirect, {
                    reportId: node.data.nodeRef
                });
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + url,
                        dataObj: {
                            htmlid: Alfresco.util.generateDomId()
                        },
                        successCallback: {
                            fn: function (response) {
                                var contentEl = Dom.get("reports-editor-panel");
                                YAHOO.Bubbling.fire("unsubscribeBubbling");
                                contentEl.innerHTML = response.serverResponse.responseText;
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });

                LogicECM.module.Base.Util.setCookie(this.PREFERENCE_KEY + encodeURIComponent(LogicECM.currentUser), this._buildPreferencesValue(), {expires: this.expiresDate});
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

    Alfresco.widget.InsituEditor.reportActions = function (p_params) {
        this.params = YAHOO.lang.merge({}, p_params);

        // Create icons instances
        this.deployIcon = new Alfresco.widget.InsituEditorDeployReport(this, p_params);
        return this;
    };

    YAHOO.extend(Alfresco.widget.InsituEditor.reportActions, Alfresco.widget.InsituEditor.textBox,
        {
            doShow: function () {
                if (this.contextStyle === null) {
                    this.contextStyle = Dom.getStyle(this.params.context, "display");
                }
                Dom.setStyle(this.params.context, "display", "none");
                Dom.setStyle(this.editForm, "display", "inline");
            },

            doHide: function (restoreUI) {
                if (restoreUI) {
                    Dom.setStyle(this.editForm, "display", "none");
                    Dom.setStyle(this.params.context, "display", this.contextStyle);
                }
            },

            _generateMarkup: function () {
                return;
            }
        });

    Alfresco.widget.InsituEditorDeployReport = function (p_editor, p_params) {
        this.params = YAHOO.lang.merge({}, p_params);
        this.disabled = p_params.disabled;

        this.editIcon = document.createElement("span");
        this.editIcon.title = Alfresco.util.encodeHTML(p_params.title);
        Dom.addClass(this.editIcon, "insitu-deploy-report");

        this.params.context.appendChild(this.editIcon, this.params.context);
        YAHOO.util.Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
        YAHOO.util.Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
        YAHOO.util.Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
        YAHOO.util.Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
    };

    YAHOO.extend(Alfresco.widget.InsituEditorDeployReport, Alfresco.widget.InsituEditorIcon,
        {
            onIconClick: function (e, obj) {
                Alfresco.util.PopupManager.displayPrompt({
                    title: Alfresco.util.message("lecm.re.lbl.register-report"),
                    text: Alfresco.util.message("lecm.re.lbl.sure-deploy-report"),
                    buttons: [
                        {
                            text: Alfresco.util.message("lecm.re.msg.deploy.yes"),
                            handler: function () {
                                this.destroy();
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/deployReport?reportDescNode={reportDescNode}";
                                sUrl = YAHOO.lang.substitute(sUrl, {
                                    reportDescNode: obj.params.curElem.data.nodeRef
                                });
                                var callback = {
                                    success: function (oResponse) {
                                        obj.params.reportTree._treeNodeSelected(obj.params.curElem);
                                        var response = eval("(" + oResponse.responseText + ")");
                                        Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text: (response != null && response.success) ? Alfresco.util.message("lecm.re.msg.deploy.success") : Alfresco.util.message("lecm.re.msg.deploy.error"),
                                                displayTime: 3
                                            });
                                    },
                                    failure: function (oResponse) {
                                        obj.params.reportTree._treeNodeSelected(obj.params.curElem);
                                        alert(oResponse.responseText);
                                        Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text: Alfresco.util.message("lecm.re.msg.deploy.error"),
                                                displayTime: 3
                                            });
                                    },
                                    timeout: 30000
                                };
                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                            }
                        },
                        {
                            text: Alfresco.util.message("lecm.re.msg.deploy.no"),
                            handler: function () {
                                this.destroy();
                                obj.params.reportTree._treeNodeSelected(obj.params.curElem);
                            },
                            isDefault: true
                        }
                    ]
                });
            }
        });
})();