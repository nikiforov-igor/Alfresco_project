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
    var $html = Alfresco.util.encodeHTML;

    LogicECM.module.OrgStructure.WorkForceCtrl = function (htmlId, currentValueHtmlId) {
        LogicECM.module.OrgStructure.WorkForceCtrl.superclass.constructor.call(
            this,
            "LogicECM.module.OrgStructure.WorkForceCtrl ",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        this.currentValueHtmlId = currentValueHtmlId;
        this.selectedItems = {};

        Bubbling.on("newWorkForceCreated", this._updateCtrl, this);

        return this;
    };

    YAHOO.lang.extend(LogicECM.module.OrgStructure.WorkForceCtrl, Alfresco.component.Base, {
        table:null,
        currentProject:null,
        globalDataCount:0,
        buttons:null,

        options:{
            mandatory:false,
            currentValue:"",
            selectedValue:"",
            selectedItems:null
        },

        onReady: function WFCtrl_onReady(){

            this.buttons = {};

            this.buttons.newRowButton = Alfresco.util.createYUIButton(this, "newWorkForceBtn", this.onNewWorkforce,
                {
                    disabled:false,
                    value:"create"
                });

            var parent = Dom.get(this.id);

            var me = this;

            // Actions Actions module
            this.modules.actions = new LogicECM.module.Base.Actions();

            // Hook action events
            var fnActionHandler = function DataGrid_fnActionHandler(layer, args)
            {
                var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                if (owner !== null)
                {
                    if (typeof me[owner.className] == "function")
                    {
                        args[1].stop = true;
                        var pos = args[1].target.offsetParent;
                        if (pos != null) {
                            var asset = me.table.getRecord(pos).getData();
                            me[owner.className].call(me, asset, me.updateRows.bind(me), {fullDelete:true});
                        }
                    }
                }
                return true;
            };
            Bubbling.addDefaultAction("wf-crtl-link", fnActionHandler);

            // from model - field to view
            var columnDefs = [
                { key:"role", label:this.msg("control.table.column.role.title"), resizeable:true, sortable:true},
                { key:"employees", label:this.msg("control.table.column.employee.title"), resizeable:true, sortable:true},
                { key:"actions", label:this.msg("control.table.column.actions.title"), resizeable:false, sortable:false, formatter: me.fnRenderCellActions(), width: 60}
            ];

            var initialSource = new YAHOO.util.DataSource([]);
            initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            initialSource.responseSchema = {fields:["workforce-role", "workforce-employee"]};

            this.table = new YAHOO.widget.DataTable(parent, columnDefs, initialSource, {initialLoad:false});
            this.loadData();

            this.table.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
            this.table.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
        },

        setProjectRef:function (formId) {
            this.currentProject = formId;
        },

        _drawTable:function (workforces) {
            if (workforces != null) {
                for (var nodeIndex in workforces) {
                    var newRow = {
                        employees:workforces[nodeIndex].workforce_employees,
                        role:workforces[nodeIndex].workforce_role,
                        nodeRef:workforces[nodeIndex].nodeRef
                    };

                    this.table.addRow(newRow, this.globalDataCount);
                    this.selectedItems[newRow.nodeRef] = newRow;
                    this.globalDataCount++;
                }
                this.table.render();
            }
        },

        loadData:function () {
            if (this.currentProject.indexOf("://") > 0) { // on new create - false
                var unitNodeRef = new Alfresco.util.NodeRef(this.currentProject);
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/project/workforces/" + unitNodeRef.uri;
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

        updateRows:function () {
            if (this.currentProject.indexOf("://") > 0) { // on new create - false
                var unitNodeRef = new Alfresco.util.NodeRef(this.currentProject);
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/project/workforces/" + unitNodeRef.uri;
                var context = this;
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        var rows = [];
                        for (var nodeIndex in oResults) {
                            var newRow = {
                                employees:oResults[nodeIndex].workforce_employees,
                                role:oResults[nodeIndex].workforce_role,
                                nodeRef:oResults[nodeIndex].nodeRef
                            };
                            rows.push(newRow);
                        }
                        context.table.updateRows(0, rows);
                    },
                    failure:function (oResponse) {
                        YAHOO.log("Failed to load workforces. " + "[" + oResponse.statusText + "]");
                    }
                };
                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            }
        },
        deleteRows:function () {
            if (this.currentProject.indexOf("://") > 0) { // on new create - false
                var unitNodeRef = new Alfresco.util.NodeRef(this.currentProject);
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/project/workforces/" + unitNodeRef.uri;
                var context = this;
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        var rows = [];
                        for (var nodeIndex in oResults) {
                            var newRow = {
                                employees:oResults[nodeIndex].workforce_employees,
                                role:oResults[nodeIndex].workforce_role,
                                nodeRef:oResults[nodeIndex].nodeRef
                            };
                            rows.push(newRow);
                        }
                        context.table.deleteRows(0, context.globalDataCount);
                        context.globalDataCount = 0;
                        context.table.addRows(rows, context.globalDataCount);
                    },
                    failure:function (oResponse) {
                        YAHOO.log("Failed to load workforces. " + "[" + oResponse.statusText + "]");
                    }
                };
                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            }
        },
        onNewWorkforce:function WFCtrl_onNewComposition(e, p_obj) {
            var destination = this.currentProject; // save inside project directory
            var itemType = "lecm-orgstr:workforce"; // save with fix type

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
                    width:"33em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:false,
                    doBeforeDialogShow:{
                        fn:doBeforeDialogShow,
                        scope:this
                    },
                    onSuccess:{
                        fn:function WF_onNewRow_success(response) {
                            // update ctrl table
                            YAHOO.Bubbling.fire("newWorkForceCreated",
                                {
                                    nodeRef:response.json.persistedObject
                                });
                        },
                        scope:this
                    }
                }).show();
        },
        _updateCtrl:function WFCtrl_onNewWorkfroceCreated(layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.nodeRef !== null)) {
                var workNodeRef = new Alfresco.util.NodeRef(obj.nodeRef);
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/base/node/properties/" + workNodeRef.uri;
                var postData = "{employees:\'{http://www.it.ru/lecm/org/structure/1.0}workforce-employee-assoc->{http://www.alfresco.org/model/content/1.0}name\', " +
                    "role:\'{http://www.it.ru/lecm/org/structure/1.0}workforce-role-assoc->{http://www.alfresco.org/model/content/1.0}name\'}";
                var control = this;
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        if (oResults != null) {
                            // add node in table
                            var newRow = {
                                employees:oResults.employees,
                                role:oResults.role,
                                nodeRef:obj.nodeRef
                            };
                            control.table.addRow(newRow, this.globalDataCount++);
                            control.table.render();
                            // add node to selected
                            control.selectedItems[obj.nodeRef] = obj;
                            // refresh currentValues
                            control._adjustCurrentValues();
                        }
                    },
                    failure:function (oResponse) {
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    },
                    timeout:10000
                };
                YAHOO.util.Connect.asyncRequest('POST', sUrl, callback, postData);
            }
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
        },

        onActionDeleteWf:function WFCtrl_deleteWf(p_items, fnDeleteComplete, metadata) {
            var context = this;
            this.onActionDelete(p_items, function () {
                context.deleteRows();
            }, metadata);
        }
    });

    YAHOO.lang.augmentProto(LogicECM.module.OrgStructure.WorkForceCtrl, LogicECM.module.Orgstructure.CtrlActions);
})();
