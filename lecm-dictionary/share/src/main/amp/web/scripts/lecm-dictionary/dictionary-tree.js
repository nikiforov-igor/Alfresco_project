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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary.Dictionary
 */
LogicECM.module.Dictionary = LogicECM.module.Dictionary || {};

/**
 * Dictionary module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Dictionary
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.Dictionary.Tree = function (htmlId) {
        LogicECM.module.Dictionary.Tree.superclass.constructor.call(
            this,
            "LogicECM.module.Dictionary.Tree",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
        Bubbling.on("itemsListChanged", this._renderTree, this);
        Bubbling.on("addTreeItem", this.onAddTreeItem, this);
        Bubbling.on("deleteSelectedTreeItem", this.onDeleteSelectedTreeItem, this);
        Bubbling.on("refreshTreeParentNode", this.onRefreshParentNode, this);
        return this;
    };

    var tree,
        dragContainerId = 'dragContainer',
        dragContainer,
        dragTree;

    var ddNodes = [];

    YAHOO.extend(LogicECM.module.Dictionary.Tree, Alfresco.component.Base, {
        selectedNode:null,
        button:null,
        cDoc:null,
        treeContainer:'dictionary',
        rootNode:null,
        doubleClickLock: false,
        options:{
	        dictionaryName: null,
            templateUrl:null,
            actionUrl:null,
            firstFocus:null,
            onSuccess:{
                fn:null,
                obj:null,
                scope:window
            },
            dictionaryURL: "lecm/dictionary/dictionary-tree"
        },

        onReady:function () {

            this._loadRootNode();

            dragContainer = Dom.get(this.treeContainer).parentNode.appendChild(document.createElement('div'));
            dragTree = new YAHOO.widget.TreeView(dragContainer);
            dragContainer.id = dragContainerId;
        },
        /**
         * Отрисовка дерева
         */
        draw:function () {
            this.cDoc = this.id;

            //Добавление дерева
            this._createTree();
        },
        /**
         * Создание дерева
         * @method _createTree
         */
        _createTree:function () {
            tree = new YAHOO.widget.TreeView(this.treeContainer);
            tree.singleNodeHighlight = true;
            tree.setDynamicLoad(this._loadTree.bind(this));

            var root = tree.getRoot();
            var newRootNode = null;
            if (this.rootNode != null) {
                var newNode = {
                    label:this.rootNode.title,
                    description:this.rootNode.description,
                    nodeRef:this.rootNode.nodeRef,
                    childType: this.rootNode.itemType,
                    isLeaf:false,
                    expanded:true,
                    type:this.rootNode.type,
                    renderHidden:true
                };
                newRootNode = new YAHOO.widget.TextNode(newNode, root);
            } else {
                this._loadTree(root);
            }

            tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                tree.onEventToggleHighlight(event);
                return false;
            }.bind(this));
//            tree.subscribe('dblClickEvent', this._editNode.bind(this));
            tree.render();

            if (newRootNode != null) {
                this._treeNodeSelected(newRootNode);
                tree.onEventToggleHighlight(newRootNode);
            }
        },
        /**
         * Перерисовка дерева
         */
        _renderTree:function (layer, args) {
            this._loadTree(this.selectedNode);
        },

	    onAddTreeItem: function (layer, args) {
		    if (args[1] != null && args[1].nodeRef != null) {
			    var me = this;
			    var nodeRef = args[1].nodeRef;

			    var loadTreeComplete = function() {
				    tree.render();

				    var highliteSelectedNode = function() {
					    var selectedNode = me.selectedNode;
					    for (var i = 0; i < selectedNode.children.length; i++) {
						    var child = selectedNode.children[i];
						    if (child.data.nodeRef == nodeRef) {
							    me.selectedNode = child;
						    }
					    }

					    me._treeNodeSelected(me.selectedNode);
					    tree.onEventToggleHighlight(me.selectedNode);
				    };

				    if (!me.selectedNode.expanded) {
					    tree.subscribe('expandComplete', function (event) {
						    tree.unsubscribe("expandComplete");
						    highliteSelectedNode();
                });

					    me.selectedNode.expand();
            } else {
					    highliteSelectedNode();
				    }
			    };
			    this._loadTree(this.selectedNode, loadTreeComplete);
		    }
	    },

	    onDeleteSelectedTreeItem: function (layer, args) {
		    var me = this;
		    var loadTreeComplete = function() {
			    me.selectedNode.parent.isLeaf = me.selectedNode.parent.children == 0;
			    tree.render();

			    me._treeNodeSelected(me.selectedNode.parent);
			    tree.onEventToggleHighlight(me.selectedNode);
		    };
		    this._loadTree(this.selectedNode.parent, loadTreeComplete);
	    },

	    onRefreshParentNode: function() {
		    var parent = this.selectedNode.parent;
		    var me = this;

		    var loadTreeComplete = function() {
			    tree.render();

			    var selectedNodeRef = me.selectedNode.data.nodeRef;
			    for (var i = 0; i < parent.children.length; i++) {
				    var child = parent.children[i];
				    if (child.data.nodeRef == selectedNodeRef) {
					    me.selectedNode = child;
            }
			    }

			    tree.onEventToggleHighlight(me.selectedNode);
		    };

		    this._loadTree(parent, loadTreeComplete);
        },

        /**
         * Получение корневого узла
         * @private
         */
        _loadRootNode:function () {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary",
	            dataObj: {
		            dicName: this.options.dictionaryName
	            },
				successCallback: {
					scope: this,
                    fn: function (response) {
                        var oResults = response.json;
                        if (oResults && oResults.nodeRef) {
                            this.rootNode = oResults;
                        }
						this.draw();
                    }
                },
                failureMessage: this.msg('message.dictionary.loading.fail')
            });
        },
        /**
         * Получение значений дерева
         * @param node {object} значения узла
         * @param fnLoadComplete
         */
        _loadTree:function loadNodeData(node, fnLoadComplete) {
            var dataObj = {};
            if (node.data.nodeRef) {
	            dataObj.nodeRef = node.data.nodeRef;
            }
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + this.options.dictionaryURL,
                dataObj: dataObj,
                successCallback: {
	                scope: this,
                    fn: function (response) {
                        var oResults = response.json;
                        if (oResults) {
                            node.children = [];
                            if (node.isLeaf) {
                                node.isLeaf = oResults.length == 0
                            }
                            for (var nodeIndex in oResults) {
                                var newNode = {
                                    label: oResults[nodeIndex].title,
                                    nodeRef: oResults[nodeIndex].nodeRef,
                                    isLeaf: oResults[nodeIndex].isLeaf,
                                    type: oResults[nodeIndex].type,
                                    childType: oResults[nodeIndex].childType,
                                    renderHidden: true
                                };
                                new YAHOO.widget.TextNode(newNode, node);
                            }
                        }

                        if (YAHOO.lang.isFunction(fnLoadComplete)) {
                            fnLoadComplete.call();
                        } else {
                            tree.render();
                        }
                    }
                },
                failureCallback: {
                    fn: function (response) {
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                        if (YAHOO.lang.isFunction(fnLoadComplete)) {
                            fnLoadComplete.call();
                        }
                    },
                    scope: this
                }
            });

        },
        /**
         * Выбор ветки/листа в дереве и перерисовка таблицы значений ветки
         * @param node {object} значения узла
         * @private
         */
        _treeNodeSelected:function (node) {
            this.selectedNode = node;
                     Bubbling.fire("activeGridChanged",
                         {
	                         datagridMeta: {
                        useFilterByOrg: false,
                        itemType: node.data.childType,
	                    currentItemType: node.data.type,
                        recreate: true,
		                         nodeRef: node.data.nodeRef,
								 searchConfig: ('lecm-contractor:contractor-type' == node.data.childType) ? {
	 								filter: 'NOT ASPECT:"lecm-orgstr-aspects:is-organization-aspect"'
	 							} : null
                             },
                             scrollTo: true
                         });
	                 YAHOO.Bubbling.fire("hideFilteredLabel");
                 },
        /**
         * Редактирование элемента дерева в отдельном диалоговом окне
         * @param event {object}
         */
        _editNode:function editNodeByEvent(event) {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
            var templateUrl =  Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	        var templateRequestParams = {
		        itemKind:"node",
		        itemId:event.node.data.nodeRef,
		        mode:"edit",
		        submitType:"json",
		        formId:"dictionary-node-form",
		        showCancelButton: true,
				showCaption: false
	        };
            new Alfresco.module.SimpleDialog("form-dialog").setOptions({
                width:"40em",
                templateUrl:templateUrl,
	            templateRequestParams:templateRequestParams,
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
//                            makeDraggable();
                        }.bind(this));
                        this.doubleClickLock = false;
                    },
                    scope:this
                },
                onFailure: {
                    fn:function (response) {
                        this.doubleClickLock = false;
                    },
                    scope:this
                }
            }).show();
        },
        /**
         * Заголовок диалогового окна при редактировании значений элемента
         * @param p_form {object} параметры формы
         * @param p_dialog {object} параметры диалога
         * @private
         */
        _setFormDialogTitle:function (p_form, p_dialog) {
            // Заголовок диалогового окна
            var fileSpan = '<span class="light">Edit Metatdata</span>';
            Alfresco.util.populateHTML(
                [ p_dialog.id + "-form-container_h", fileSpan]
            );
            this.doubleClickLock = false;
	        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
        }
    });

    var DDNode = function (id, sGroup, config) {
        DDNode.superclass.constructor.call(this, id, sGroup, config);

    };

    var makeDraggable = function () {
        for (var i = 0, l = ddNodes.length; i < l; i++) {
        }
        ddNodes = [];

        var nodes = tree.getNodesBy(function () {
            return true;
        });
        if (nodes) {
            for (i = 0, l = nodes.length; i < l; i++) {
                ddNodes.push(
                    new DDNode(
                        nodes[i].getContentEl(),
                        'default',
                        {
                            dragElId:dragContainerId
                        }
                    )
                );
            }
        }
    }

    var isDragElement = false;

    YAHOO.extend(DDNode, YAHOO.util.DDProxy, {
        srcNode:null,
        destNode:null,
        /**
         * Старт перетаскивания ветки дерево в другую
         * @param x {int}
         * @param y {int}
         */
        startDrag:function (x, y) {
            this.srcNode = tree.getNodeByElement(this.getEl());
            // The following section of code resizes the container of the proxy element.
            (function () {
                var proxyEl = this.getDragEl(),
                    dragEl = this.srcNode.getEl(),
                    dragRegion = Dom.getRegion(dragEl);

                Dom.setStyle(proxyEl, "width", dragRegion.width + "px");
                Dom.setStyle(proxyEl, "height", dragRegion.height + "px");
            }).call(this);

            if (!isDragElement && this.srcNode.data.type != "lecm-dic:dictionary") {
                isDragElement = true;
                Dom.setStyle(this.srcNode.getEl(), "visibility", "hidden");
                dragTree.buildTreeFromObject(this.getTreeNodeDefinition(this.srcNode));
                dragTree.render();
            }
        },
        /**
         * Метод переносит данные из одной ветки в выбранную, при этом проверяется может ли выбранная ветка перенесена.
         * Вводится диалоговое окно подверждения переноса данных в другую ветку.
         * @param e {Event} событие mousemove
         * @param id {String|DragDrop[]} id на которым находимся. В INTERSECT режиме массив элементов dd
         */
        onDragDrop:function (e, id) {

            var me = this;
            if (!this.destNode) {
                return;
            }

            var fnActionMoveConfirm = function DictionaryActions__onActionMove_confirm() {
                var parent = dest = me.destNode,
                    src = me.srcNode;

                var dataObj = {
	                childNodeRef:encodeURI(src.data.nodeRef),
	                parentNodeRef:encodeURI(dest.data.nodeRef)
                };

                Alfresco.util.Ajax.jsonRequest(
                    {
                        method:Alfresco.util.Ajax.POST,
                        url:Alfresco.constants.PROXY_URI + "/lecm/dictionary/action/changeParent/node",
                        dataObj:dataObj,
                        successCallback:{
                            fn:function (response, obj) {
                                var oResults = eval("(" + response.responseText + ")");
                                if (response.json.success) {
                                    dest.isLeaf = false;
                                    while (parent) {
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
                                    src.appendTo(dest);
                                    tree.render();
                                    makeDraggable();

                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:Alfresco.util.message("dictionary.message.moveSuccess", "LogicECM.module.Dictionary.Tree")
                                        });
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:Alfresco.util.message("dictionary.message.moveFailure", "LogicECM.module.Dictionary.Tree")
                                        });
                                }
                            }
                        },
                        failureMessage:Alfresco.util.message("dictionary.message.moveFailure", "LogicECM.module.Dictionary.Tree"),
                        failureCallback:{
                            fn:function () {
                                alert("!!!FALSE!!!");
                            }
                        }
                    });
            };

            Alfresco.util.PopupManager.displayPrompt(
                {
                    title:Alfresco.util.message("dictionary.message.confirm.move.title", "LogicECM.module.Dictionary.Tree"),
                    text:Alfresco.util.message("dictionary.message.confirm.move.description", "LogicECM.module.Dictionary.Tree", {"0":me.srcNode.label, "1":me.srcNode.parent.label, "2":me.destNode.label}),
                    buttons:[
                        {
                            text:Alfresco.util.message("dictionary.button.move", "LogicECM.module.Dictionary.Tree"),
                            handler:function DataListActions__onActionMove_move() {
                                this.destroy();
                                fnActionMoveConfirm.call();
                            }
                        },
                        {
                            text:Alfresco.util.message("button.cancel"),
                            handler:function DictionaryActions__onActionMove_cancel() {
                                this.destroy();
                            },
                            isDefault:true
                        }
                    ]
                });

        },
        /**
         * Установка стилей и параметров при завершении перетаскивания.
         * @param x {int}
         * @param y {int}
         */
        endDrag:function (x, y) {
            Dom.setStyle(this.srcNode.getEl(), "visibility", "");
            if (this.destNode) {
                Dom.removeClass(this.destNode.getContentEl(), 'dropTarget');
            }

            isDragElement = false;
	        if (dragTree.getRoot().children.length > 0) {
				dragTree.removeNode(dragTree.getRoot().children[0]);
	        }
            Dom.get(dragContainerId).innerHTML = "";
        },
        /**
         * Метод вызывается при зависании над другим элементом при перетаскивании
         * @param e {Event} событие mousemove
         * @param id {String|DragDrop[]} id на которым находимся. В INTERSECT режиме массив элементов dd
         */
        onDragOver:function (e, id) {
            var tmpTarget = tree.getNodeByElement(Dom.get(id));

	        if (tmpTarget.data.childType == this.srcNode.data.type) {
            if (this.destNode != tmpTarget) {
                if (this.destNode) {
                    Dom.removeClass(this.destNode.getContentEl(), 'dropTarget');
                }
                Dom.addClass(tmpTarget.getContentEl(), 'dropTarget');
                this.destNode = tmpTarget;
            }
	        }
        },

	    getTreeNodeDefinition: function(node) {

			var def, defs = YAHOO.lang.merge(node.data), children = [];

            defs.label = node.label;
		    if (node.labelStyle != 'ygtvlabel') { defs.style = node.labelStyle; }
		    if (node.title) { defs.title = node.title; }
		    if (node.href) { defs.href = node.href; }
		    if (node.target != '_self') { defs.target = node.target; }
		    if (node.expanded) {defs.expanded = node.expanded; }
		    if (!node.multiExpand) { defs.multiExpand = node.multiExpand; }
		    if (node.renderHidden) { defs.renderHidden = node.renderHidden; }
		    if (!node.hasIcon) { defs.hasIcon = node.hasIcon; }
		    if (node.nowrap) { defs.nowrap = node.nowrap; }
		    if (node.className) { defs.className = node.className; }
		    if (node.editable) { defs.editable = node.editable; }
		    if (!node.enableHighlight) { defs.enableHighlight = node.enableHighlight; }
		    if (node.highlightState) { defs.highlightState = node.highlightState; }
		    if (node.propagateHighlightUp) { defs.propagateHighlightUp = node.propagateHighlightUp; }
		    if (node.propagateHighlightDown) { defs.propagateHighlightDown = node.propagateHighlightDown; }
		    defs.type = node._type;

		    for (var i = 0; i < node.children.length;i++) {
			    def = this.getTreeNodeDefinition(node.children[i]);
			    if (def === false) { return false;}
			    children.push(def);
		    }
		    if (children.length) { defs.children = children; }
		    return defs;
	    }
    });
})();
