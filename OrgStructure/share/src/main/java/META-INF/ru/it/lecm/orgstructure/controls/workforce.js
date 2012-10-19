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
 * Experts module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.OrgStructure.WorkForceCtrl
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.OrgStructure.WorkForceCtrl = function (htmlId, currentValueHtmlId) {
        LogicECM.module.OrgStructure.WorkForceCtrl.superclass.constructor.call(
            this,
            "LogicECM.module.OrgStructure.WorkForceCtrl ",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        this.currentValueHtmlId = currentValueHtmlId;
        this.selectedItems = {};
        Bubbling.on("newWorkForceCreated", this._updateCtrl, this);
    };

    YAHOO.lang.extend(LogicECM.module.OrgStructure.WorkForceCtrl, Alfresco.component.Base, {
        table:null,
        currentProject:null,
        globalDataCount:0,
        buttons:null,
        //wfMetaData:null,
        messages:null,

        options:{
            mandatory:false,
            currentValue:"",
            selectedValue:"",
            selectedItems:null
        },
        setMessages:function (messages) {
            this.messages = messages;
        },
        init:function (formId) {
            this.currentProject = formId;
            this.buttons = {};
            //this.wfMetaData = {};

            var parent = Dom.get(this.id);

            // from model - field to view
            var columnDefs = [
                { key:"role", label:this.msg("control.table.column.role.title"), resizeable:true, sortable:true},
                { key:"employee", label:this.msg("control.table.column.employee.title"), resizeable:true, sortable:true},
                { key:"actions", label:this.msg("control.table.column.actions.title"), resizeable:false}
            ];

            var initialSource = new YAHOO.util.DataSource([]);
            initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            initialSource.responseSchema = {fields:["workforce-role", "workforce-employee"]};

            this.table = new YAHOO.widget.DataTable(parent, columnDefs, initialSource, {initialLoad:false});
            this.loadData();
        },
        _drawTable:function (workforces) {
            if (workforces != null) {
                for (var nodeIndex in workforces) {
                    var newRow = {
                        employee:workforces[nodeIndex].workforce_employee.name,
                        role:workforces[nodeIndex].workforce_role,
                        workforceRef:workforces[nodeIndex].nodeRef
                    };

                    this.table.addRow(newRow, this.globalDataCount);
                    this.selectedItems[newRow.workforceRef] = newRow;
                    this.globalDataCount++;
                }
                this.table.render();
            }
        },
        loadData:function () {
            if (this.currentProject.indexOf("://") > 0) { // on new create - false
                var unitNodeRef = new Alfresco.util.NodeRef(this.currentProject);
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/project/workforces/" + unitNodeRef;
                var context = this;
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        context._drawTable(oResults);
                    },
                    failure:function (oResponse) {
                        YAHOO.log("Failed to load workforces. " + "[" + oResponse.statusText + "]");
                    }
                };
                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            }
        },
        onNewWorkforce:function WFCtrl_onNewComposition(e, p_obj) {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/root?type=workforces";
            var context = this;
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        var destination = oResults[0].nodeRef;
                        var itemType = "lecm-orgstr:workforce";

                        var doBeforeDialogShow = function WorkforceCtrl_onNewRow_doBeforeDialogShow(p_form, p_dialog) {
                            Alfresco.util.populateHTML(
                                [ p_dialog.id + "-dialogTitle", "Title" ],
                                [ p_dialog.id + "-dialogHeader", "Header" ]
                            );
                        };

                        var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
                            {
                                itemKind:"type",
                                itemId:itemType,
                                destination:destination,
                                mode:"create",
                                submitType:"json"
                            });

                        // Using Forms Service, so always create new instance
                        var createRow = new Alfresco.module.SimpleDialog("newWorkforce-dialog");

                        createRow.setOptions(
                            {
                                width:"40em",
                                templateUrl:templateUrl,
                                actionUrl:null,
                                destroyOnHide:false,
                                doBeforeDialogShow:{
                                    fn:doBeforeDialogShow,
                                    scope:this
                                },
                                onSuccess:{
                                    fn:function WF_onNewRow_success(response) {
                                        YAHOO.Bubbling.fire("newWorkForceCreated",
                                            {
                                                nodeRef:response.json.persistedObject
                                            });

                                        /*Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text:this.msg("message.new-row.success")
                                            });*/
                                    },
                                    scope:this
                                },
                                onFailure:{
                                    fn:function WF_onNewRow_failure(response) {
                                        /*Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text:this.msg("message.new-row.failure")
                                            });*/
                                    },
                                    scope:this
                                }
                            }).show();
                    }
                },
                failure:function (oResponse) {
                    YAHOO.log("Failed to process XHR transaction.", "info", "example");
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },
        _updateCtrl:function WFCtrl_onNewWorkfroceCreated(layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.nodeRef !== null)) {
                // add node to table
                var newRow = {
                    employee:"New",
                    role:"New",
                    workforceRef:obj.nodeRef
                };
                this.table.addRow(newRow, this.globalDataCount);
                this.globalDataCount++;
                this.table.render();
                // add node to selected
                this.selectedItems[obj.nodeRef] = obj;
                // refresh currentValues
                this._adjustCurrentValues();
            }
        },
        onReady:function WFCtrl_onReady() {
            this.buttons.newRowButton = Alfresco.util.createYUIButton(this, "newWorkForceBtn", this.onNewWorkforce,
                {
                    disabled:false,
                    value:"create"
                });
        },
        _adjustCurrentValues:function WFCtrl__adjustCurrentValues() {
            var addedItems = this.getAddedItems(),
                removedItems = this.getRemovedItems(),
                selectedItems = this.getSelectedItems();

            Dom.get(this.id + "-added").value = addedItems.toString();
            Dom.get(this.id + "-removed").value = removedItems.toString();

            Dom.get(this.currentValueHtmlId).value = selectedItems.toString();

            if (this.options.mandatory) {
                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
            }

            YAHOO.Bubbling.fire("formValueChanged",
                {
                    eventGroup:this,
                    addedItems:addedItems,
                    removedItems:removedItems,
                    selectedItems:selectedItems,
                    selectedItemsMetaData:Alfresco.util.deepCopy(this.selectedItems)
                });
        },
        getAddedItems:function WFCtrl_getAddedItems() {
            var addedItems = [],
                currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

            for (var item in this.selectedItems) {
                if (this.selectedItems.hasOwnProperty(item)) {
                    if (!(item in currentItems)) {
                        addedItems.push(item);
                    }
                }
            }
            return addedItems;
        },
        getRemovedItems:function WFCtrl_getRemovedItems() {
            var removedItems = [],
                currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

            for (var item in currentItems) {
                if (currentItems.hasOwnProperty(item)) {
                    if (!(item in this.selectedItems)) {
                        removedItems.push(item);
                    }
                }
            }
            return removedItems;
        },
        getSelectedItems:function WFCtrl_getSelectedItems() {
            var selectedItems = [];

            for (var item in this.selectedItems) {
                if (this.selectedItems.hasOwnProperty(item)) {
                    selectedItems.push(this.selectedItems[item].nodeRef);
                }
            }
            return selectedItems;
        }
    });
})();
