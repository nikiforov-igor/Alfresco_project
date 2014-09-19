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


/**
 * LogicECM Orgstructure module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.OrgStructure.OrgStructure
 */
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

/**
 * OrgStructure module.
 *
 * @namespace LogicECM.module.OrgStructure
 * @class LogicECM.module.OrgStructure.DashletArmTree
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;
    var Event = YAHOO.util.Event;

    LogicECM.module.OrgStructure.DashletArmTree = function (htmlId) {
        LogicECM.module.OrgStructure.DashletArmTree.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.OrgStructure.DashletArmTree, LogicECM.module.OrgStructure.ArmTree);

    YAHOO.lang.augmentObject(LogicECM.module.OrgStructure.ArmTree.prototype,
        {
            onReady: function OT_onReady() {
                this._initToolbar();
                this._setBlocksVisibility("block", "none");
            },

            reloadTree: function OT_ReloadTree(isRecreate, node) {
                if (this.tree != null) {
                    this.tree.destroy();
                }
                if (isRecreate) {
                    this._setBlocksVisibility("none", "none");
                    this._createTree(node);
                } else {
                    this._setBlocksVisibility("block", "none");
                }
            },

            _createTree: function (rootNode) {
                this.tree = new YAHOO.widget.TreeView(this.id + "-orgstructure-tree");

                this.tree.setDynamicLoad(this._loadTree.bind(this));
                var root = this.tree.getRoot();

                if (rootNode) {
                    root.data = rootNode.data;
                }
                this._loadTree(root);

                this.tree.subscribe('clickEvent', function (event) {
                    this._treeNodeSelected(event.node);
                    return false;
                }.bind(this));

                this.tree.subscribe('expand', function (node) {
                    this._onExpand(node);
                    return true;
                }.bind(this));

                this.tree.subscribe('collapse', function (node) {
                    this._onCollapse(node);
                    return true;
                }.bind(this));

                this.tree.render();
            },

            _setBlocksVisibility: function (dValue, eValue) {
                Dom.setStyle(this.id + "-default", "display", dValue);
                Dom.setStyle(this.id + "-empty", "display", eValue);
            },

            _loadTree: function loadNodeData(node, fnLoadComplete) {
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/dictionary/branch";
                if (node.data.nodeRef != null) {
                    sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
                }
                if (this.searchTerm != null && this.searchTerm.length > 0) {
                    sUrl += ((sUrl.indexOf("?") > 0 ? "&" : "?") + "searchTerm=" + encodeURI(this.searchTerm.trim()));
                }
                sUrl += ((sUrl.indexOf("?") > 0 ? "&" : "?") + "dashletFormat=true");
                var otree = this;
                var callback = {
                    success: function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        if (oResults != null) {
                            node.children = [];
                            for (var nodeIndex in oResults) {
                                var newNode = {
                                    label: oResults[nodeIndex].label,
                                    nodeRef: oResults[nodeIndex].nodeRef,
                                    isLeaf: true,
                                    title: oResults[nodeIndex].title,
                                    type: oResults[nodeIndex].type
                                };

                                var curElement = new YAHOO.widget.TextNode(newNode, node);
                                var ref = curElement.data.nodeRef;
                                curElement.labelElId = ref.slice(ref.lastIndexOf('/') + 1);
                                curElement.id = curElement.labelElId;
                                curElement.labelStyle = curElement.data.type == "lecm-orgstr:organization-unit" ? "unit-icon" : "employee-icon";
                            }

                            if (otree.searchTerm != null && oResults.length == 0) {
                                otree._setBlocksVisibility("none", "block");
                            }
                        }

                        otree.searchTerm = null;

                        if (oResponse.argument.fnLoadComplete != null) {
                            oResponse.argument.fnLoadComplete();
                        } else {
                            otree.tree.render();
                        }
                    },
                    failure: function (oResponse) {
                        otree.searchTerm = null;
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                        if (oResponse.argument.fnLoadComplete != null) {
                            oResponse.argument.fnLoadComplete();
                        }
                    },
                    argument: {
                        node: node,
                        fnLoadComplete: fnLoadComplete
                    }
                };
                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            },
            _treeNodeSelected: function (node) {
                this.selectedNode = node;
                if (this.selectedNode.data.type == "lecm-orgstr:organization-unit") {
                    this.reloadTree(true, this.selectedNode);
                } else {
                    this._viewNode();
                }
            }
        }, true);
})();