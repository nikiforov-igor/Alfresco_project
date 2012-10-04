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
 * Dictionary module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Dictionary
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;
    var tableContainerId = null;  // TODO
    var nodeDictionary = null;    //TODO

    LogicECM.module.Dictionary = function (htmlId) {
        return LogicECM.module.Dictionary.superclass.constructor.call(
            this,
            "LogicECM.module.Dictionary",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.Dictionary, Alfresco.component.Base, {
        tree:null,
        selectedNode:null,
        messages:null,
        button:null,
        cDoc: null,
        options:{
            templateUrl:null,
            actionUrl:null,
            firstFocus:null,
            onSuccess:{
                fn:null,
                obj:null,
                scope:window
            }
        },
        init: function(formId) {
            this._loadNode();
        },

        setMessages:function (messages) {
            this.messages = messages;
        },

        draw:function () {
            this.cDoc = this.id;
            var dictionary = Dom.get(this.id);

            //Добавляем кнопку
            this.button = new YAHOO.widget.Button("newListButton");
            this.button.subscribe("click", function () {
                this._createNode("lecm-dic:dictionary");
                this.show();
            }.bind(this));

            //Добавляем дерево структуры предприятия
            var treeContainer = document.createElement("div");
            treeContainer.id = this.id + "-tree";
            dictionary.appendChild(treeContainer);

            //Добавляем таблицу с данными
            var tableContainer = document.createElement("div");
            tableContainer.id = this.id + "-table";
            dictionary.appendChild(tableContainer);
            tableContainerId=tableContainer.id;

            //this._loadNode();
            this.tree = new YAHOO.widget.TreeView(treeContainer.id);
            this.tree.setDynamicLoad(this._loadTree);
            var root = this.tree.getRoot();
            this._loadTree(root);
            this.tree.subscribe("labelClick", this._treeNodeSelected.bind(this));
            this.tree.subscribe("expand", this._treeNodeSelected.bind(this));
            this.tree.subscribe('dblClickEvent', this._editNode.bind(this));
            this.tree.render();
        },

        _createUrl:function (type, nodeRef, childNodeType) {
            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
            if (type == "create") {
                return YAHOO.lang.substitute(templateUrl, {
                    itemKind:"type",
                    itemId:childNodeType,
                    destination:nodeRef,
                    mode:"create",
                    submitType:"json",
                    formId:"dictionary-node-form"
                });
            } else {
                return YAHOO.lang.substitute(templateUrl, {
                    itemKind:"node",
                    itemId:nodeRef,
                    mode:"edit",
                    submitType:"json",
                    formId:"dictionary-node-form"
                });
            }
        },
        _loadNode: function () {
            var  sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/folder";
            if (this.cDoc != null) {
                sUrl += "?nodeRef=" + encodeURI(this.cDoc);
            }
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        for (var nodeIndex in oResults) {
                            nodeDictionary = oResults[nodeIndex].toString();
                        }
                    }
                },
                failure:function (oResponse) {
                    alert("Failed to load experts. " + "[" + oResponse.statusText + "]");
                },
                argument:{
                }
            };

            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _loadTree:function loadNodeData(node, fnLoadComplete) {

            var sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/dictionary";
            if (node.data.nodeRef != null) {
                sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
            }
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        node.children = [];
                        for (var nodeIndex in oResults) {
                            var newNode = {
                                label:oResults[nodeIndex].title,
                                nodeRef:oResults[nodeIndex].nodeRef,
                                isLeaf:oResults[nodeIndex].isLeaf,
                                type:oResults[nodeIndex].type
                            };
                            new YAHOO.widget.TextNode(newNode, node);
                        }
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        oResponse.argument.tree.render();
                    }
                },
                failure:function (oResponse) {
                    YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    oResponse.argument.fnLoadComplete();
                },
                argument:{
                    node:node,
                    fnLoadComplete:fnLoadComplete,
                    tree:this.tree,
                    context: this
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _treeNodeSelected:function (node) {
            this.selectedNode = node;

            Bubbling.fire("activeDataListChanged",
                {
                    dataList: {
                        description: "",
                        itemType: "lecm-dic:dictionary_values",
                        name: node.data.type,
                        nodeRef: node.data.nodeRef,
                        permissions: {
                            'delete': true,
                            'edit': true
                        },
                        title: node.label
                    },
                    scrollTo: true
                });
        },
        _createNode:function createNodeByType(type) {
            var templateUrl = null;
            if (type == "lecm-dic:dictionary"){
                templateUrl = this._createUrl("create", nodeDictionary, type);
            } else {
                templateUrl = this._createUrl("create", this.selectedNode.data.nodeRef, type);
            }
            new Alfresco.module.SimpleDialog("form-dialog").setOptions({
                width:"40em",
                templateUrl:templateUrl,
                actionUrl:null,
                destroyOnHide:true,
                doBeforeDialogShow:{
                    fn:this._setFormDialogTitle
                },
                onSuccess:{
                    fn:function () {
                        this._loadTree(this.selectedNode, function () {
                            this.selectedNode.isLeaf = false;
                            this.selectedNode.expanded = true;
                            this.tree.render();
                            this.selectedNode.focus();
                        }.bind(this));
                    },
                    scope:this
                }
            }).show();
            this.tree.render();
        },
        _editNode:function editNodeByEvent(event) {
            var templateUrl = this._createUrl("edit", this.selectedNode.data.nodeRef);
            new Alfresco.module.SimpleDialog("form-dialog").setOptions({
                width:"40em",
                templateUrl:templateUrl,
                actionUrl:null,
                destroyOnHide:true,
                doBeforeDialogShow:{
                    fn:this._setFormDialogTitle
                },
                onSuccess:{
                    fn:function () {
                        this._loadTree(this.selectedNode.parent, function () {
                            this.tree.render();
                            this.selectedNode.focus();
                        }.bind(this));
                    },
                    scope:this
                }
            }).show();
        },

        _setFormDialogTitle:function (p_form, p_dialog) {
            // Dialog title
            var fileSpan = '<span class="light">Edit Metatdata</span>';
            Alfresco.util.populateHTML(
                [ p_dialog.id + "-form-container_h", fileSpan]
            );
        }
    });
})();