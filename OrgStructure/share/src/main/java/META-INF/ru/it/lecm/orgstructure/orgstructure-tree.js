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
    var Event = YAHOO.util.Event;

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
            insituEditors:null,
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
            this.options.insituEditors = [];

            this.tree = new YAHOO.widget.TreeView(this.id);
            this.tree.singleNodeHighlight = true;
            this.tree.setDynamicLoad(this._loadTree.bind(this));
            var root = this.tree.getRoot();
            this._loadTree(root);

            this.tree.subscribe("expand", this._treeNodeSelected.bind(this));
            this.tree.subscribe("expandComplete", this.onExpandComplete, this, true);
            this.tree.subscribe("collapse", this._treeNodeSelected.bind(this));
            this.tree.subscribe('dblClickEvent', this._editNode.bind(this));
            this.tree.subscribe('clickEvent', function (event) {
                this._treeNodeSelected(event.node);
                return false;
            }.bind(this));

            this.tree.render();
        },
        onExpandComplete:function OT_onExpandComplete(oNode) {
            for (var i in this.options.insituEditors) {
                /*try {*/
                    Alfresco.util.createInsituEditor(
                        this.options.insituEditors[i].context,
                        this.options.insituEditors[i].params,
                        this.options.insituEditors[i].callback
                    );
                /*} catch (ex) {
                    YAHOO.error("Failed to create Editor.", "info", "example");
                }*/
            }
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
            var otree = this;
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

                            var curElement = new YAHOO.widget.TextNode(newNode, node);

                            var ref = curElement.data.nodeRef;
                            curElement.labelElId = ref.slice(ref.lastIndexOf('/') + 1);
                            curElement.id = curElement.labelElId;

                            otree.options.insituEditors.push(
                                {
                                    context:curElement.labelElId,
                                    params:{
                                        showDelay:300,
                                        hideDelay:300,
                                        type:"organizationUnit",
                                        unitID:curElement.labelElId,
                                        unitName:curElement.label,
                                        curElem:curElement,
                                        unitAdmin:otree
                                    },
                                    callback:null
                                });
                        }
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        otree.tree.render();
                        otree.onExpandComplete(null);
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
                    fnLoadComplete:fnLoadComplete
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _treeNodeSelected:function (node) {
            this.selectedNode = node;
            this.tree.onEventToggleHighlight(node);
            this.tree.currentFocus._removeFocus(); // for correct highlight
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
            var context = this;
            new Alfresco.module.SimpleDialog("editUnit-dialog").setOptions({
                width:"50em",
                templateUrl:templateUrl,
                actionUrl:null,
                destroyOnHide:false,
                doBeforeDialogShow:{
                    fn:this._setFormDialogTitle
                },
                onSuccess:{
                    fn:function () {
                        context._loadTree(context.selectedNode.parent).bind(context);
                    },
                    scope:this
                }
            }).show();
            /*window.location.href = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT
                + "edit-metadata?nodeRef=" + this.selectedNode.data.nodeRef;*/
        },
        _addNode:function editNodeByEvent(event) {
            var templateUrl = this._createUrl("create", this.selectedNode.data.nodeRef, "lecm-orgstr:organization-unit");
            new Alfresco.module.SimpleDialog("addUnit-dialog").setOptions({
                width:"50em",
                templateUrl:templateUrl,
                actionUrl:null,
                destroyOnHide:false,
                doBeforeDialogShow:{
                    fn:this._setFormDialogTitle
                },
                onSuccess:{
                    fn:function Tree_onNewUnit_success(response) {
                        YAHOO.Bubbling.fire("unitCreated",
                            {
                                nodeRef:response.json.persistedObject
                            });
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

                var postData = "{source:\"" + encodeURI(current.data.nodeRef) + "\", " +
                    "target:\"" + encodeURI(obj.nodeRef) + "\"," +
                    "assocType:\"" + encodeURI(current.data.childAssoc) + "\"}";
                var otree = this;
                var callback = {
                    success:function (oResponse) {
                        var sNode = otree.selectedNode;
                        otree._loadTree(sNode);
                        sNode.isLeaf = false;
                        sNode.expanded = true;
                        otree.tree.render();
                    },
                    failure:function (oResponse) {
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    },
                    timeout:7000
                };
                YAHOO.util.Connect.asyncRequest('POST', sUrl, callback, postData);
            }
        }
    });

    Alfresco.widget.InsituEditor.organizationUnit = function (p_params) {
        this.params = YAHOO.lang.merge({}, p_params);

        // Create icons instances
        this.editIcon = new Alfresco.widget.InsituEditorUnitEdit(this, p_params);
        this.addIcon = new Alfresco.widget.InsituEditorUnitAdd(this, p_params);
        return this;
    };

    YAHOO.extend(Alfresco.widget.InsituEditor.organizationUnit, Alfresco.widget.InsituEditor.textBox,
        {
            doShow:function InsituEditor_textBox_doShow() {
                if (this.contextStyle === null)
                    this.contextStyle = Dom.getStyle(this.params.context, "display");
                Dom.setStyle(this.params.context, "display", "none");
                Dom.setStyle(this.editForm, "display", "inline");
            },

            doHide:function InsituEditor_textBox_doHide(restoreUI) {
                if (restoreUI) {
                    Dom.setStyle(this.editForm, "display", "none");
                    Dom.setStyle(this.params.context, "display", this.contextStyle);
                }
            },

            _generateMarkup:function InsituEditor_textBox__generateMarkup() {
                return;
            }
        });

    Alfresco.widget.InsituEditorUnitAdd = function (p_editor, p_params) {
        this.editor = p_editor;
        this.params = YAHOO.lang.merge({}, p_params);
        this.disabled = p_params.disabled;

        this.editIcon = document.createElement("span");
        this.editIcon.title = Alfresco.util.encodeHTML(p_params.title);
        Dom.addClass(this.editIcon, "insitu-add-unit");

        this.params.context.appendChild(this.editIcon, this.params.context);
        Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
        Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
        Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
        Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
    };

    YAHOO.extend(Alfresco.widget.InsituEditorUnitAdd, Alfresco.widget.InsituEditorIcon,
        {
            onIconClick:function InsituEditorUnitAdd_onIconClick(e, obj) {
                var context = obj.params.unitAdmin;
                context.selectedNode = obj.params.curElem;
                context._addNode(e);
            }
        });

    Alfresco.widget.InsituEditorUnitEdit = function (p_editor, p_params) {
        this.editor = p_editor;
        this.params = YAHOO.lang.merge({}, p_params);
        this.disabled = p_params.disabled;

        this.editIcon = document.createElement("span");
        this.editIcon.title = Alfresco.util.encodeHTML(p_params.title);
        Dom.addClass(this.editIcon, "insitu-edit-unit");

        this.params.context.appendChild(this.editIcon, this.params.context);
        Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
        Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
        Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
        Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
    };

    YAHOO.extend(Alfresco.widget.InsituEditorUnitEdit, Alfresco.widget.InsituEditorIcon,
        {
            onIconClick:function InsituEditorUnitEdit_onIconClick(e, obj) {
                var context = obj.params.unitAdmin;
                context.selectedNode = obj.params.curElem;
                context._editNode(e);
            }
        });
})();