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
    var Event = YAHOO.util.Event;

    LogicECM.module.OrgStructure.ArmTree = function (htmlId) {
        LogicECM.module.OrgStructure.ArmTree.superclass.constructor.call(this, "LogicECM.module.OrgStructure.ArmTree", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.OrgStructure.ArmTree, Alfresco.component.Base, {
        tree: null,
        selectedNode: null,
        doubleClickLock: false,
        searchMode: false,

        searchButton : null,
        searchTerm: null,

        expandedNodes:[],

        options : {
            minSTermLength: -1,
            bubblingLabel: null
        },

        onReady: function OT_onReady() {
            this._initToolbar();
            //Добавляем дерево структуры организации
            this.reloadTree();
        },

        reloadTree: function OT_ReloadTree() {
            if (this.tree != null) {
                this.tree.destroy();
            }
            this._createTree()
        },

        _createTree: function () {
            this.tree = new YAHOO.widget.TreeView("orgstructure-tree");

            this.tree.setDynamicLoad(this._loadTree.bind(this));
            var root = this.tree.getRoot();
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

        _onExpand: function (node) {
            this.expandedNodes.push(node.id);
        },

        _onCollapse: function (node) {
            this.expandedNodes.splice(node.id);
        },

        _nodeIsExpanded: function (node) {
            for (var i = 0; i < this.expandedNodes.length; i++) {
                var expanded = this.expandedNodes[i];
                if (node.id == expanded) {
                    return true;
                }
            }
            return false;
        },

        _initToolbar: function () {
            this.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this._onSearchClick, {});

            var me = this;
            // Search
            this.checkShowClearSearch();
            Event.on(this.id + "-clearSearchInput", "click", this._onClearSearch, null, this);
            Event.on(this.id + "-full-text-search", "keyup", this.checkShowClearSearch, null, this);
            var searchInput = Dom.get(this.id + "-full-text-search");
            new YAHOO.util.KeyListener(searchInput,
                {
                    keys: 13
                },
                {
                    fn: me._onSearchClick,
                    scope: this,
                    correctScope: true
                }, "keydown").enable();


            // Finally show the component body here to prevent UI artifacts on YUI button decoration
            Dom.setStyle(this.id + "-body", "visibility", "visible");
        },

        /**
         * Скрывает кнопку поиска, если строка ввода пустая
         * @constructor
         */
        checkShowClearSearch: function Toolbar_checkShowClearSearch() {
            if (Dom.get(this.id + "-full-text-search").value.length > 0) {
                Dom.setStyle(this.id + "-clearSearchInput", "visibility", "visible");
            } else {
                Dom.setStyle(this.id + "-clearSearchInput", "visibility", "hidden");
            }
        },

        // по нажатию на кнопку Поиск
        _onSearchClick: function Tree_onSearch(e, obj) {
            var searchTerm = Dom.get(this.id + "-full-text-search").value.trim();

            var maySearch = this.options.minSTermLength <= 0 || searchTerm.length == 0;
            if (!maySearch) {// проверяем длину терма
                maySearch = (searchTerm.length >= this.options.minSTermLength);
            }
            if (maySearch){
                this.searchTerm = searchTerm;
                if (searchTerm.length > 0) {
                    this.searchMode = true;
                    this.reloadTree(true);
                } else {
                    this._onClearSearch();
                }
            } else {
                Alfresco.util.PopupManager.displayMessage(
                    {
                        displayTime: 3,
                        text: this.msg("label.need_more_symbols_for_search")
                    });
            }
        },

        _onClearSearch: function _onClearSearch() {
            Dom.get(this.id + "-full-text-search").value = "";
            this.searchTerm = null;
            this.searchMode = false;
            this.checkShowClearSearch();
            this.reloadTree();
        },


        _loadTree: function loadNodeData(node, fnLoadComplete) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/dictionary/branch";
            if (node.data.nodeRef != null) {
                sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
            }
            if (this.searchTerm != null && this.searchTerm.length > 0) {
                sUrl += ((sUrl.indexOf("?") > 0 ? "&" : "?") + "searchTerm=" + encodeURI(this.searchTerm.trim()));
            }
            var otree = this;
            var callback = {
                success: function (oResponse) {
                    otree.searchTerm = null;
                    Dom.setStyle(otree.id + "-empty", "display", "none");
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
                            curElement.labelStyle = curElement.data.type == "lecm-orgstr:organization-unit" ? "unit-icon" : "employee-icon";
                            if (!otree.searchMode) {
                                curElement.expanded = curElement.data.expand || otree._nodeIsExpanded(curElement);
                            }
                        }
                        if (oResults.length == 0) {
                            Dom.setStyle(otree.id + "-empty", "display", "block");
                        }
                    }

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
            this._viewNode();
        },

        _viewNode: function viewNodeByEvent() {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;

            LogicECM.module.Base.Util.viewAttributes({itemId:this.selectedNode.data.nodeRef.toString()});
            this.doubleClickLock = false;
        }
    });
})();