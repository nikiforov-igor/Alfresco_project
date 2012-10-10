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
    var nodeDictionary = null;    //TODO
    var nodeType = "lecm-dic:dictionary_values";  //TODO

    LogicECM.module.Dictionary = function (htmlId) {
        LogicECM.module.Dictionary.superclass.constructor.call(
            this,
            "LogicECM.module.Dictionary",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
        Bubbling.on("itemsListChanged", this._renderTree, this);
        return this;
    };

    var tree,
        dragContainerId = 'dragContainer',
        dragContainer,
        dragTree;

    var ddNodes = [];

    YAHOO.extend(LogicECM.module.Dictionary, Alfresco.component.Base, {
        selectedNode:null,
        button:null,
        cDoc: null,
        treeContainer: 'dictionary',
        rootNode: null,
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
        init: function() {
            this._loadNode();

            dragContainer = Dom.get(this.treeContainer).parentNode.appendChild(document.createElement('div'));
            dragTree = new YAHOO.widget.TreeView(dragContainer);
            dragContainer.id = dragContainerId;
        },

        draw:function () {
            this.cDoc = this.id;

            //Добавление дерева
            this._createTree();

            //Добавляем кнопку
            this.button = new YAHOO.widget.Button("newListButton");
            this.button.subscribe("click", function () {
                this._createNode("lecm-dic:dictionary");
            }.bind(this));
        },
        _createTree: function () {
            tree = new YAHOO.widget.TreeView(this.treeContainer);
            tree.singleNodeHighlight = true;
            tree.setDynamicLoad(this._loadTree);
            var root = tree.getRoot();
            this._loadTree(root);
            tree.subscribe("expand", this._treeNodeSelected.bind(this));
            tree.subscribe('clickEvent', function(event) {
                this._treeNodeSelected(event.node);
                tree.onEventToggleHighlight(event);
                return false;
            }.bind(this));
            tree.subscribe('dblClickEvent', this._editNode.bind(this));
            tree.render();
        },
        _renderTree: function () {
            this._loadTree(this.selectedNode);
            this.selectedNode.isLeaf = false;
            this.selectedNode.expanded = true;
            tree.render();
            this.selectedNode.focus();
            makeDraggable();
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
                                type:oResults[nodeIndex].type,
                                renderHidden:true
                            };
                            new YAHOO.widget.TextNode(newNode, node);
                        }
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        oResponse.argument.tree.render();
                    }
                    makeDraggable();
                },
                failure:function (oResponse) {
                    YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    oResponse.argument.fnLoadComplete();
                },
                argument:{
                    node:node,
                    fnLoadComplete:fnLoadComplete,
                    tree:tree,
                    context: this
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _treeNodeSelected:function (node) {
            this.selectedNode = node;
             var  sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/type";
             if (node.data.nodeRef != null) {
                 sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
             }

             var callback = {
                 success:function (oResponse) {
                     var oResults = eval("(" + oResponse.responseText + ")");
                     if (oResults != null) {
                         for (var nodeIndex in oResults) {
                             nodeType = oResults[nodeIndex].toString();
                             if (nodeType=="" || nodeType == null){
                                 nodeType = "lecm-dic:dictionary_values";
                             }
                         }
                     };
                     Bubbling.fire("activeDataListChanged",
                         {
                             dataList: {
                                 description: "",
                                 itemType: nodeType,
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
                 failure:function (oResponse) {
                     alert("Failed to load type. " + "[" + oResponse.statusText + "]");
                 },
                 argument:{
                 }
             };

             YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _createNode:function createNodeByType(type) {
            var templateUrl = this._createUrl("create", nodeDictionary, type);

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
                        this._createTree();
                    },
                    scope:this
                }
            }).show();
        },
        _editNode:function editNodeByEvent(event) {
            var templateUrl = this._createUrl("edit", event.node.data.nodeRef);
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
                        this._loadTree(event.node.parent, function () {
                            tree.render();
                            event.node.focus();
                            makeDraggable();
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

    var DDNode = function(id, sGroup, config) {
        DDNode.superclass.constructor.call(this, id, sGroup, config);

    };

    var makeDraggable = function() {
        for (var i = 0, l = ddNodes.length;i < l;i++) {
//            ddNodes[i].unreg();
        }
        ddNodes = [];

        var nodes = tree.getNodesBy(function(){return true;});
        if (nodes) {
            for (i = 0,l = nodes.length;i<l;i++) {
                ddNodes.push(
                    new DDNode(
                        nodes[i].getContentEl(),
                        'default',
                        {
                            dragElId: dragContainerId
                        }
                    )
                );
            }
        }
    }

    var isDragElement = false;
    var dragElementIsLeaf;

    YAHOO.extend(DDNode, YAHOO.util.DDProxy, {
        srcNode: null,
        destNode: null,

        startDrag: function(x, y) {
            this.srcNode = tree.getNodeByElement(this.getEl());
            // The following section of code resizes the container of the proxy element.
            (function () {
                var proxyEl = this.getDragEl(),
                    dragEl = this.srcNode.getEl(),
                    dragRegion = Dom.getRegion(dragEl);

                Dom.setStyle( proxyEl, "width",  dragRegion.width  + "px" );
                Dom.setStyle( proxyEl, "height", dragRegion.height + "px" );
            }).call(this);

            if (!isDragElement && this.srcNode.data.type != "dictionary") {
                isDragElement = true;
                Dom.setStyle(this.srcNode.getEl(), "visibility", "hidden");

                dragElementIsLeaf = this.srcNode.isLeaf;
                this.srcNode.isLeaf = true;
                dragTree.buildTreeFromObject(this.srcNode.getNodeDefinition());
                this.srcNode.isLeaf = false;
                dragTree.render();
            }
        },
        onDragDrop: function (e, id) {

            var me = this;

            var fnActionMoveConfirm = function DictionaryActions__onActionMove_confirm()
            {
                var parent = dest = me.destNode,
                    src = me.srcNode;
                if (!dest) { return; }

                var dataObj = {childNodeRef: encodeURI(src.data.nodeRef), parentNodeRef: encodeURI(dest.data.nodeRef)};

                Alfresco.util.Ajax.jsonRequest(
                    {
                        method: Alfresco.util.Ajax.POST,
                        url: Alfresco.constants.PROXY_URI + "/lecm/dictionary/action/changeParent/node",
                        dataObj: dataObj,
                        successCallback:
                        {
                            fn: function(response, obj)
                            {
                                var oResults = eval("(" + response.responseText + ")");
                                if (response.json.success) {
                                    dest.isLeaf = false;
                                    while(parent) {
                                        if (parent == src) {
                                            return;
                                        }
                                        parent = parent.parent;
                                    }
                                    var oldParent = src.parent;
                                    tree.popNode(src);
                                    if (oldParent.children.length == 0) {
                                        oldParent.isLeaf = true;
                                    }
                                    src.isLeaf = dragElementIsLeaf;
                                    src.appendTo(dest);
                                    tree.render();
                                    makeDraggable();

                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: Alfresco.util.message("dictionary.message.moveSuccess", "LogicECM.module.Dictionary")
                                        });
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: Alfresco.util.message("dictionary.message.moveFailure", "LogicECM.module.Dictionary")
                                        });
                                    }
                            }
                        },
                        failureMessage: Alfresco.util.message("dictionary.message.moveFailure", "LogicECM.module.Dictionary"),
                        failureCallback:
                        {
                            fn: function()
                            {
                                alert("!!!FALSE!!!");
                            }
                        }
                    });
            };

            Alfresco.util.PopupManager.displayPrompt(
                {
                    title: Alfresco.util.message("dictionary.message.confirm.move.title", "LogicECM.module.Dictionary"),
                    text: Alfresco.util.message("dictionary.message.confirm.move.description", "LogicECM.module.Dictionary"),
                    buttons: [
                        {
                            text: Alfresco.util.message("dictionary.button.move", "LogicECM.module.Dictionary"),
                            handler: function DataListActions__onActionMove_move()
                            {
                                this.destroy();
                                fnActionMoveConfirm.call();
                            }
                        },
                        {
                            text: Alfresco.util.message("button.cancel"),
                            handler: function DictionaryActions__onActionMove_cancel()
                            {
                                this.destroy();
                            },
                            isDefault: true
                        }]
                });

        },

        endDrag: function(x,y) {
            Dom.setStyle(this.srcNode.getEl(), "visibility", "");
            if (this.destNode) {
                Dom.removeClass(this.destNode.getContentEl(),'dropTarget');
            }

            isDragElement = false;
            var nodes = dragTree.getNodesBy(function(){return true;});
            if (nodes) {
                for (i = 0,l = nodes.length;i<l;i++) {
                    dragTree.removeNode(nodes[i]);
                }
            }
            Dom.get(dragContainerId).innerHTML = "";
        },

        onDragOver: function(e, id) {
            var tmpTarget = tree.getNodeByElement(Dom.get(id));

            if (this.destNode != tmpTarget) {
                if (this.destNode) {
                    Dom.removeClass(this.destNode.getContentEl(),'dropTarget');
                }
                Dom.addClass(tmpTarget.getContentEl(),'dropTarget');
                this.destNode = tmpTarget;
            }
        }
    });
})();