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
    LogicECM.module.Contracts = function (htmlId) {
        return LogicECM.module.Contracts.superclass.constructor.call(
            this,
            "LogicECM.module.Contracts",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.Contracts, Alfresco.component.Base, {
        messages:null,
        catalog: null,
        workflowId: "contractWorkflow",
        list: null,
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
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/contracts/root";
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    oResponse.argument.contractsObject.catalog = oResults.nodeRef;
                },
                argument:{
                    contractsObject: this
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);

            var oButton = new YAHOO.widget.Button({
                id: "addDocumentButton",
                type: "button",
                label: "Add Document",
                container: "contracts"
            });
            oButton.on("click", this._createContract.bind(this));

            this.list = document.createElement("div");
            Dom.get(this.id).appendChild(this.list);
            this._refreshList();
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
                    formId:"contract-node-form"
                });
            }
        },
        /*_createNode: function createNodeByType(type) {
         var templateUrl = this._createUrl("create", this.selectedNode.data.nodeRef, type);
         new Alfresco.module.SimpleDialog("form-dialog").setOptions({
         width: "40em",
         templateUrl: templateUrl,
         actionUrl: null,
         destroyOnHide: true,
         doBeforeDialogShow: {
         fn: this._setFormDialogTitle
         },
         onSuccess:{
         fn: function () {
         this._loadTree(this.selectedNode, function() {
         this.selectedNode.isLeaf = false;
         this.selectedNode.expanded = true;
         this.tree.render();
         this.selectedNode.focus();
         }.bind(this));
         },
         scope: this
         }
         }).show();
         },*/
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
        _createContract: function() {
            var templateUrl = this._createUrl("create", this.catalog, "lecm-contract:document");
            new Alfresco.module.SimpleDialog("form-dialog").setOptions({
                width: "40em",
                templateUrl: templateUrl,
                actionUrl: null,
                destroyOnHide: true,
                /*doBeforeDialogShow: {
                    fn: this._setFormDialogTitle
                },*/
                onSuccess:{
                    fn: function (response) {
                        var workflowUrl = Alfresco.constants.PROXY_URI + "lecm/workflow/stateProcess?workflowId={workflowId}&nodeRef={nodeRef}";

                        workflowUrl = YAHOO.lang.substitute(workflowUrl, {
                            workflowId: this.workflowId,
                            nodeRef: response.json.persistedObject
                        });

                        var callback = {
                            success:function (oResponse) {
                                oResponse.argument.contractsObject._refreshList();
                            },
                            argument:{
                                contractsObject: this
                            },
                            timeout:7000
                        };
                        YAHOO.util.Connect.asyncRequest('GET', workflowUrl, callback);
                    },
                    scope: this
                }
            }).show();
        },
        _refreshList: function() {
            sUrl =Alfresco.constants.URL_SERVICECONTEXT + "lecm/contracts/documents";
            callback = {
                success:function (oResponse) {
                    oResponse.argument.contractsObject.list.innerHTML = oResponse.responseText;
                },
                argument:{
                    contractsObject: this
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        }

    });

})();