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
        cDoc:null,
        treeContainer:'dictionary',
        rootNode:null,
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
        /**
         * Инициализация начальных парметров при старте
         * @method init
         * @param dictionaryName {string} имя справочника
         */
        init:function (dictionaryName) {

            this._loadRootNode(dictionaryName);

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
            tree.setDynamicLoad(this._loadTree);

            var root = tree.getRoot();
            var newRootNode = null;
            if (this.rootNode != null) {
                var newNode = {
                    label:this.rootNode.title,
                    description:this.rootNode.description,
                    nodeRef:this.rootNode.nodeRef,
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
            tree.subscribe('dblClickEvent', this._editNode.bind(this));
            tree.render();

            if (newRootNode != null) {
                this._treeNodeSelected(newRootNode);
                tree.onEventToggleHighlight(newRootNode);
            }
        },
        /**
         * Перерисовка дерева
         */
        _renderTree:function () {
            this._loadTree(this.selectedNode);
            this.selectedNode.isLeaf = false;
            this.selectedNode.expanded = true;
            tree.render();
            this.selectedNode.focus();
            makeDraggable();
        },
        /**
         * Формирование адреса при редактировании или создании элемента дерева
         * @param type {string} тип действия edit - редактирование элемента
         * @param nodeRef {string} ссылка на узел
         * @param childNodeType childNodeType {string} ссылка на дочерний узел
         * @return {*}
         * @private
         */
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
        /**
         * Получение корневого узла
         * @param dictionaryName {string}
         * @private
         */
        _loadRootNode:function (dictionaryName) {
            var me = this;

            if (dictionaryName !== null && dictionaryName !== "") {
                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + dictionaryName;
//
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        if (oResults != null && oResults.nodeRef != null) {
                            nodeDictionary = oResults.nodeRef;
                            me.rootNode = oResults;
                        }

                        me.draw();
                    },
                    failure:function (oResponse) {
                        alert("Failed to load dictionary " + dictionaryName);
                    },
                    argument:{
                    }
                };
                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            } else {
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/folder";
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
                        me.draw();
                    },
                    failure:function (oResponse) {
                        alert("Failed to load experts. " + "[" + oResponse.statusText + "]");
                    },
                    argument:{
                    }
                };

                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            }
        },
        /**
         * Получение значений дерева
         * @param node {object} значения узла
         * @param fnLoadComplete
         */
        _loadTree:function loadNodeData(node, fnLoadComplete) {

            var sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/dictionary-tree";
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
                    context:this
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        /**
         * Выбор ветки/листа в дереве и перерисовка таблицы значений ветки
         * @param node {object} значения узла
         * @private
         */
        _treeNodeSelected:function (node) {
            this.selectedNode = node;
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/type";
            if (node.data.nodeRef != null) {
                sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
            }

             var callback = {
                 success:function (oResponse) {
                     var oResults = eval("(" + oResponse.responseText + ")");
                     var nodeType = "lecm-dic:hierarchical_dictionary_values";
                     if (oResults != null) {
                         for (var nodeIndex in oResults) {
                             nodeType = oResults[nodeIndex].toString();
                             if (nodeType=="" || nodeType == null){
                                 nodeType = "lecm-dic:hierarchical_dictionary_values";
                             }
                         }
                     }
                     Bubbling.fire("activeGridChanged",
                         {
	                         datagridMeta: {
                                 itemType: nodeType,
		                         nodeRef: node.data.nodeRef,
                                 searchConfig: {
                                    filter: 'PARENT:"' + node.data.nodeRef + '" AND (NOT (ASPECT:"lecm-dic:aspect_active") OR lecm\\-dic:active:true)'
                                 }
                             },
                             scrollTo: true
                         });
	                 YAHOO.Bubbling.fire("hideFilteredLabel");
                 },
                 failure:function (oResponse) {
                     alert("Failed to load type. " + "[" + oResponse.statusText + "]");
                 },
                 argument:{
                 }
             };

            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        /**
         * Редактирование элемента дерева в отдельном диалоговом окне
         * @param event {object}
         */
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

                var dataObj = {childNodeRef:encodeURI(src.data.nodeRef), parentNodeRef:encodeURI(dest.data.nodeRef)};

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
                                            text:Alfresco.util.message("dictionary.message.moveSuccess", "LogicECM.module.Dictionary")
                                        });
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:Alfresco.util.message("dictionary.message.moveFailure", "LogicECM.module.Dictionary")
                                        });
                                }
                            }
                        },
                        failureMessage:Alfresco.util.message("dictionary.message.moveFailure", "LogicECM.module.Dictionary"),
                        failureCallback:{
                            fn:function () {
                                alert("!!!FALSE!!!");
                            }
                        }
                    });
            };

            Alfresco.util.PopupManager.displayPrompt(
                {
                    title:Alfresco.util.message("dictionary.message.confirm.move.title", "LogicECM.module.Dictionary"),
                    text:Alfresco.util.message("dictionary.message.confirm.move.description", "LogicECM.module.Dictionary", {"0":me.srcNode.label, "1":me.srcNode.parent.label, "2":me.destNode.label}),
                    buttons:[
                        {
                            text:Alfresco.util.message("dictionary.button.move", "LogicECM.module.Dictionary"),
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

            if (this.destNode != tmpTarget) {
                if (this.destNode) {
                    Dom.removeClass(this.destNode.getContentEl(), 'dropTarget');
                }
                Dom.addClass(tmpTarget.getContentEl(), 'dropTarget');
                this.destNode = tmpTarget;
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