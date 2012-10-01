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
    var resultset = new Array();  // TODO
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
        menu:null,
        search:null,
        tree:null,
        selectedNode:null,
        messages:null,
        table:null,
        layout:null,
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
//            this._loadJSON();
        },

        setMessages:function (messages) {
            this.messages = messages;
        },

        draw:function () {
            this.cDoc = this.id;
            var dictionary = Dom.get(this.id);

//			//Добавляем меню
            var menu = document.createElement("div");
            menu.id = this.id + "-menu";
            dictionary.appendChild(menu);

            var menuData = [
                {
                    text:this.messages["dictionary.append"],
                    submenu:{
                        id:"appendmenu",
                        itemdata:[
                            {
                                id:"appendEmployee",
                                text:this.messages["dictionary.append.employee"],
                                disabled:true
                            },
                            {
                                id:"appendDivision",
                                text:this.messages["dictionary.append.division"],
                                disabled:true
                            }
                        ]
                    }
                }
            ];

            this.menu = new YAHOO.widget.MenuBar(menu.id + "-menubar", {
                lazyload:false,
                itemdata:menuData
            });

            this.menu.subscribe("click", this._menuSelected.bind(this));
            this.menu.render(menu);


            //Добавляем строку поиска
            this.search = document.createElement("div");
            this.search.id = this.id + "-search";
            this.search.innerHTML = "Search";
            dictionary.appendChild(this.search);

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
//                        oResponse.argument.context._showTable(resultset);
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
        _showTable:function(dataSource){
            var myColumnDefs = [
                {key:"name", sortable:true, resizeable:true},
//				{key:"description", formatter:YAHOO.widget.DataTable.formatDate, sortable:true, sortOptions:{defaultDir:YAHOO.widget.DataTable.CLASS_DESC},resizeable:true},
//				{key:"quantity", formatter:YAHOO.widget.DataTable.formatNumber, sortable:true, resizeable:true},
//				{key:"amount", formatter:YAHOO.widget.DataTable.formatCurrency, sortable:true, resizeable:true},
                {key:"description", sortable:true, resizeable:true}
            ];

            var myDataSource = new YAHOO.util.DataSource(dataSource);
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            myDataSource.responseSchema = {
                fields: ["name","description"]
            };

            this.table = new YAHOO.widget.DataTable(tableContainerId,
                myColumnDefs, myDataSource, {caption:"DataTable Caption"});
        },
        _redrawTable: function(node){
            resultset = [];
            var  sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/items";
            if (node.data.nodeRef != null) {
                sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
            }
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        node.children = [];
                        for (var nodeIndex in oResults) {
                            resultset.push({
                                name:oResults[nodeIndex].name,
                                description:oResults[nodeIndex].description
                            });
                        }
                    }
                    this._showTable(resultset);
                }.bind(this),
                failure:function (oResponse) {
                    alert("Failed to load items. " + "[" + oResponse.statusText + "]");
                },
                argument:{
                }
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _treeNodeSelected:function (node) {
            this.selectedNode = node;

            this._redrawTable(node);

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

            if (this.selectedNode.data.type == "dictionary") {
                this.menu.getSubmenus()[0].getItem(0).cfg.setProperty("disabled", false);
                this.menu.getSubmenus()[0].getItem(1).cfg.setProperty("disabled", false);
            } else {
                this.menu.getSubmenus()[0].getItem(0).cfg.setProperty("disabled", true);
                this.menu.getSubmenus()[0].getItem(1).cfg.setProperty("disabled", true);
            }
        },
        _menuSelected:function onClickMenu(p_sType, p_aArgs) {
            var selectedMenu = p_aArgs[1];
            if (selectedMenu instanceof YAHOO.widget.MenuItem) {
                if (selectedMenu.id == "appendEmployee") {
                    this._createNode("lecm-dic:dictionary_values");
                } else if (selectedMenu.id == "appendDivision") {
                    this._createNode("lecm-dic:dictionary");
                } else if (selectedMenu.id == "appendFolder") {
                    this._createNode("cm:folder");
                }
            }

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
//
//        _loadJSON: function () {
//            var  sUrl = "/share/service/components/data-lists/config/columns?itemType="  + encodeURIComponent("lecm-dic:dictionary_values");
//
//            var callback = {
//                success:function (oResponse) {
////                    alert("yes");
//                },
//                failure:function (oResponse) {
////                    alert("Failed to load experts. " + "[" + oResponse.statusText + "]");
//                },
//                argument:{
//                }
//            };
//
//            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
//        }

    });
})();



(function()
{
    /**
     * YUI Library aliases
     */
    var Bubbling = YAHOO.Bubbling;

    /**
     * Alfresco.service.DataListActions implementation
     */
    Alfresco.service.DataListActions = {};
    Alfresco.service.DataListActions.prototype =
    {

    };
})();

(function()
{
    Alfresco.module.DataListActions = function()
    {
        return null;
    };

    Alfresco.module.DataListActions.prototype =
    {

    };
})();