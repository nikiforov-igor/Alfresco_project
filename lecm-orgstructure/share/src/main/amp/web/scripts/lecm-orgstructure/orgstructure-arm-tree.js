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
 * @class LogicECM.module.OrgStructure.ArmTree
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.OrgStructure.ArmTree = function (htmlId) {
        LogicECM.module.OrgStructure.ArmTree.superclass.constructor.call(this, "LogicECM.module.OrgStructure.ArmTree", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.OrgStructure.ArmTree, Alfresco.component.Base, {
        tree: null,
        selectedNode: null,
        doubleClickLock: false,

        onReady: function OT_onReady() {
            var orgStructure = Dom.get(this.id);
            //Добавляем дерево структуры организации
            this._createTree(orgStructure);
        },

        _createTree: function (parent) {
            this.tree = new YAHOO.widget.TreeView(this.id);
            //this.tree.singleNodeHighlight = true;

            this.tree.setDynamicLoad(this._loadTree.bind(this));
            var root = this.tree.getRoot();
            this._loadTree(root);

            this.tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                return false;
            }.bind(this));

            this.tree.render();
        },

        _createUrl: function (nodeRef) {
            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT +
                "lecm/components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&showCancelButton=true";
            return YAHOO.lang.substitute(templateUrl, {
                itemKind: "node",
                itemId: nodeRef,
                mode: "view"
            })
        },

        _loadTree: function loadNodeData(node, fnLoadComplete) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/arm/branch";
            if (node.data.nodeRef != null) {
                sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
            }
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
                                isLeaf: oResults[nodeIndex].isLeaf,
                                expand: oResults[nodeIndex].expand,
                                title: oResults[nodeIndex].title,
                                type: oResults[nodeIndex].type
                            };

                            var curElement = new YAHOO.widget.TextNode(newNode, node);
                            var ref = curElement.data.nodeRef;
                            curElement.labelElId = ref.slice(ref.lastIndexOf('/') + 1);
                            curElement.id = curElement.labelElId;
                        }
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        if (curElement.data.expand) {
                            curElement.expanded = true;
                        }

                        otree.tree.render();
                    }
                },
                failure: function (oResponse) {
                    YAHOO.log("Failed to process XHR transaction.", "info", "example");
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
            this.selectedNode = node;
            this._viewNode();
        },

        _viewNode: function viewNodeByEvent() {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;

            viewAttributes(this.selectedNode.data.nodeRef, null, "", null);

            this.doubleClickLock = false;
        },

        _setFormDialogTitle: function (p_form, p_dialog) {
            var message = this.msg("dialog.view.title");
            var fileSpan = '<span class="light">' + message + '</span>';
            Alfresco.util.populateHTML(
                [ p_dialog.id + "-form-container_h", fileSpan]
            );
            this.doubleClickLock = false;
        }
    });
})();