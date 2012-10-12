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
 * @class LogicECM.module.OrgStructure.Tree
 */
(function () {

    var Dom = YAHOO.util.Dom
    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.OrgStructure.Tree = function (htmlId) {
        LogicECM.module.OrgStructure.Tree.superclass.constructor.call(
            this,
            "LogicECM.module.OrgStructure.Tree",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        Bubbling.on("unitCreated", this.onNewUnitCreated, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.OrgStructure.Tree, Alfresco.component.Base, {
        tree:null,
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
            /*var treeContainer = document.createElement("div");
             treeContainer.id = this.id + "-tree";
             parent.appendChild(treeContainer);*/

            this.tree = new YAHOO.widget.TreeView(this.id);
            this.tree.singleNodeHighlight = true;
            this.tree.setDynamicLoad(this._loadTree);
            var root = this.tree.getRoot();
            this._loadTree(root);
            this.tree.subscribe("expand", this._treeNodeSelected.bind(this));
            this.tree.subscribe("collapse", this._treeNodeSelected.bind(this));
            this.tree.subscribe('dblClickEvent', this._editNode.bind(this));
            this.tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                return false;
            }.bind(this));
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
                            var namespace = "lecm-orgstr";
                            var newNode = {
                                label:oResults[nodeIndex].title,
                                nodeRef:oResults[nodeIndex].nodeRef,
                                isLeaf:oResults[nodeIndex].isLeaf,
                                type:namespace + ":" + oResults[nodeIndex].type,
                                dsUri:oResults[nodeIndex].dsUri,
                                childType:namespace + ":" + oResults[nodeIndex].childType,
                                childAssoc:namespace + ":" + oResults[nodeIndex].childAssoc
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
                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    }
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
            this.tree.onEventToggleHighlight(node);
            this.tree.currentFocus._removeFocus();
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
        },
        onNewUnitCreated:function Tree_onNewUnitCreated(layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.nodeRef !== null)) {
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/assoc";
                var current = this.selectedNode;

                var postData = "{source:\""+ encodeURI(current.data.nodeRef) + "\", " +
                    "target:\"" + encodeURI(obj.nodeRef) + "\"," +
                    "assocType:\"" + encodeURI(current.data.childAssoc) + "\"}";

                var callback = {
                    success:function (oResponse) {
                        var sNode = oResponse.argument.context.selectedNode;
                        oResponse.argument.context._loadTree(sNode);
                        sNode.isLeaf = false;
                        sNode.expanded = true;
                        oResponse.argument.context.tree.render();
                    },
                    failure:function (oResponse) {
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    },
                    argument:{
                        context:this
                    },
                    timeout:7000
                };
                YAHOO.util.Connect.asyncRequest('POST', sUrl, callback, postData);
            }
        }
    });

})();