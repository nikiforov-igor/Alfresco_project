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
 * @class LogicECM.module.OrgStructure.UnitCompositionCtrl
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.OrgStructure.UnitCompositionCtrl = function (htmlId, currentValueHtmlId) {
        LogicECM.module.OrgStructure.UnitCompositionCtrl.superclass.constructor.call(
            this,
            "LogicECM.module.OrgStructure.UnitCompositionCtrl ",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);

        this.currentValueHtmlId = currentValueHtmlId;
        this.selectedItems = {};
        Bubbling.on("neStaffListCreated", this.updateTable, this);
    };

    YAHOO.lang.extend(LogicECM.module.OrgStructure.UnitCompositionCtrl, Alfresco.component.Base, {
        table:null,
        currentUnit:null,
        globalDataCount:0,
        buttons:null,
        //ucMetaData:null,
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
            this.currentUnit = formId;
            this.buttons = {};
            //this.ucMetaData = {};

            var parent = Dom.get(this.id);

            // from model - field to view
            var columnDefs = [
                { key:"employee", label:this.msg("control.table.column.employee.title"), resizeable:true, sortable:true},
                { key:"position", label:this.msg("control.table.column.position.title"), resizeable:true, sortable:true},
                { key:"is_boss", label:this.msg("control.table.column.boss.title"), resizeable:false},
                { key:"is_primary", label:this.msg("control.table.column.primary.title"), resizeable:false},
                { key:"actions", label:this.msg("control.table.column.actions.title"), resizeable:false}
            ];

            var initialSource = new YAHOO.util.DataSource([]);
            initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            initialSource.responseSchema = {fields:["employee", "position", "is_boss", "is_primary"]};

            this.table = new YAHOO.widget.DataTable(parent, columnDefs, initialSource, {initialLoad:false});

            this.loadData();
        },

        _drawTable:function (compositions) {
            if (compositions != null) {
                for (var nodeIndex in compositions) {
                    var newRow = {
                        employee:compositions[nodeIndex].employee != null ? compositions[nodeIndex].employee.name : "",
                        position:compositions[nodeIndex].position.name,
                        is_boss:compositions[nodeIndex].is_boss,
                        is_primary:compositions[nodeIndex].is_primary,
                        compositionRef:compositions[nodeIndex].nodeRef
                    };
                    this.table.addRow(newRow, this.globalDataCount);
                    this.selectedItems[newRow.compositionRef] = newRow;
                    this.globalDataCount++;
                }
                this.table.render();
            }
        },
        loadData:function () {
            if (this.currentUnit.indexOf("://") > 0) {
                var unitNodeRef = new Alfresco.util.NodeRef(this.currentUnit);
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/unit/staff-lists/" + unitNodeRef.uri;
                var context = this;
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        context._drawTable(oResults);
                    },
                    failure:function (oResponse) {
                        YAHOO.log("Failed to load compositions. " + "[" + oResponse.statusText + "]");
                    }
                };
                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            }
        },
        onNewComposition:function UCCtrl_onNewComposition(e, p_obj) {
            var destination = this.currentUnit;
            var itemType = "lecm-orgstr:staff-list";

            var doBeforeDialogShow = function UCCtrl_onNewRow_doBeforeDialogShow(p_form, p_dialog) {
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
            var createRow = new Alfresco.module.SimpleDialog("newStaffList-dialog");

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
                        fn:function UCCtrl_onNewRow_success(response) {
                            YAHOO.Bubbling.fire("neStaffListCreated",
                                {
                                    nodeRef:response.json.persistedObject
                                });

                            /*Alfresco.util.PopupManager.displayMessage(
                             {
                             text:("message.new-row.success")
                             });*/
                        },
                        scope:this
                    },
                    onFailure:{
                        fn:function DataListToolbar_onNewRow_failure(response) {
                            /*Alfresco.util.PopupManager.displayMessage(
                             {
                             text:this.msg("message.new-row.failure")
                             });*/
                        },
                        scope:this
                    }
                }).show();

        },
        updateTable:function UCCtrl_onNewCompositionCreated(layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.nodeRef !== null)) {
                var unitNodeRef = new Alfresco.util.NodeRef(obj.nodeRef);
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/base/node/properties/" + unitNodeRef.uri;
                var postData = "{employee:\'{http://www.it.ru/lecm/org/structure/1.0}composition-employee-assoc->{http://www.alfresco.org/model/content/1.0}name\', " +
                    "position:\'{http://www.it.ru/lecm/org/structure/1.0}composition-position-assoc->{http://www.alfresco.org/model/content/1.0}name\'," +
                    "is_boss:\'{http://www.it.ru/lecm/org/structure/1.0}composition-is-boss\'," +
                    "is_primary:\'{http://www.it.ru/lecm/org/structure/1.0}composition-is-primary\'}";
                var control = this;
                var callback = {
                    success:function (oResponse) {
                        var oResults = eval("(" + oResponse.responseText + ")");
                        if (oResults != null) {
                            // add node in table
                            var newRow = {
                                employee:oResults.employee,
                                position:oResults.position,
                                is_boss:oResults.is_boss,
                                is_primary:oResults.is_primary,
                                compositionRef:obj.nodeRef
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
        onReady:function UCCtrl_onReady() {
            this.buttons.newRowButton = Alfresco.util.createYUIButton(this, "newCompositionBtn", this.onNewComposition,
                {
                    disabled:false,
                    value:"create"
                });
        },
        _adjustCurrentValues:function UCCtrl__adjustCurrentValues() {

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
        getAddedItems:function UCCtrl_getAddedItems() {
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
        getRemovedItems:function UCCtrl_getRemovedItems() {
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
        getSelectedItems:function UCCtrl_getSelectedItems() {
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
