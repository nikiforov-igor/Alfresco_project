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
 * OrgStructure module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.OrgStructure
 */
(function () {

    var Dom = YAHOO.util.Dom
    var Bubbling = YAHOO.Bubbling;
    LogicECM.module.OrgStructure = function (htmlId) {
        return LogicECM.module.OrgStructure.superclass.constructor.call(
            this,
            "LogicECM.module.OrgStructure",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.OrgStructure, Alfresco.component.Base, {
        menu:null,
        search:null,
        tree:null,
        table:null,
        selectedNode:null,
        messages:null,
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

        setMessages:function (messages) {
            this.messages = messages;
        },

        draw:function () {
            var orgStructure = Dom.get(this.id);
            //Добавляем дерево структуры предприятия
            this._createTree(orgStructure);
        },

        _createTree:function (parent) {
            var treeContainer = document.createElement("div");
            treeContainer.id = this.id + "-tree";
            parent.appendChild(treeContainer);

            this.tree = new YAHOO.widget.TreeView(treeContainer.id);
            this.tree.singleNodeHighlight = true;
            this.tree.setDynamicLoad(this._loadTree);
            var root = this.tree.getRoot();
            this._loadTree(root);
            this.tree.subscribe("labelClick", this._treeNodeSelected.bind(this));
            this.tree.subscribe("expand", this._treeNodeSelected.bind(this));
            this.tree.subscribe("collapse", this._treeNodeSelected.bind(this));
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
                    formId:"orgstructure-node-form"
                });
            } else {
                return YAHOO.lang.substitute(templateUrl, {
                    itemKind:"node",
                    itemId:nodeRef,
                    mode:"edit",
                    submitType:"json",
                    formId:"orgstructure-node-form"
                });
            }
        },
        _loadTree:function loadNodeData(node, fnLoadComplete) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/branch";
            if (node.data.nodeRef != null) {
                sUrl += "?nodeRef=" + encodeURI(node.data.nodeRef);
                if (node.data.type != null) {
                    var type = node.data.type;
                    sUrl += "&type=" + encodeURI(type.slice(type.indexOf(':') + 1));
                }
            } else {
                sUrl += "?onlyStructure=true";
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
                                type:"lecm-orgstr:" + oResults[nodeIndex].type,
                                dsUri:oResults[nodeIndex].dsUri,
                                childType:"lecm-orgstr:" + oResults[nodeIndex].childType
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
                    tree:this.tree
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _treeNodeSelected:function (node) {
            this.selectedNode = node;
            if (this.selectedNode.data.dsUri != null && this.selectedNode.data.dsUri != '') {
                Bubbling.fire("orgElementSelected",
                    {
                        orgstructureElement:{
                            description:"",
                            type:node.data.type,
                            itemType:"lecm-orgstr:employee",
                            name:node.data.type,
                            nodeRef:node.data.nodeRef,
                            dataSourceUri:node.data.dsUri,
                            permissions:{
                                'delete':false,
                                'edit':false
                            },
                            title:node.label
                        },
                        scrollTo:true
                    });
            }
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
(function () {
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

(function () {
    Alfresco.module.DataListActions = function () {
        return null;
    };

    Alfresco.module.DataListActions.prototype =
    {

    };
})();