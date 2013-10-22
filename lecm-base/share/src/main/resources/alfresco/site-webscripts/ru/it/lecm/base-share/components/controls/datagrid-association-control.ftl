<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign objectId = field.name?replace("-", "_")>

<#assign allowCreate = true/>
<#if field.control.params.allowCreate??>
    <#assign allowCreate = field.control.params.allowCreate?lower_case/>
</#if>

<#assign allowDelete = "true"/>
<#if field.control.params.allowDelete??>
    <#assign allowDelete = field.control.params.allowDelete?lower_case/>
</#if>

<#assign allowEdit = "true"/>
<#if field.control.params.allowEdit??>
    <#assign allowEdit = field.control.params.allowEdit?lower_case/>
</#if>

<#assign allowExpand = "true"/>
<#if field.control.params.allowExpand??>
    <#assign allowExpand = field.control.params.allowExpand?lower_case/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
    <#assign showActions = field.control.params.showActions/>
</#if>

<#assign bubblingId = containerId/>

<#assign usePagination = false/>
<#if field.control.params.usePagination??>
    <#assign usePagination = field.control.params.usePagination/>
</#if>

<#assign attributeForShow = ""/>
<#if field.control.params.attributeForShow??>
    <#assign attributeForShow = field.control.params.attributeForShow/>
</#if>

<div class="form-field with-grid" id="${controlId}">
<label for="${controlId}" style="white-space: nowrap; overflow: visible;">${field.label?html}
    :<#if field.endpointMandatory!false || field.mandatory!false><span
        class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

<@grid.datagrid containerId false>
<script type="text/javascript">//<![CDATA[
(function () {
    var Dom = YAHOO.util.Dom;
    LogicECM.module.Base.DataGridControl_${objectId} = function (htmlId) {
        LogicECM.module.Base.DataGridControl_${objectId}.superclass.constructor.call(this, htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Base.DataGridControl_${objectId}, LogicECM.module.Base.DataGrid, {
    ${field.control.params.actionsHandler!""}
    });
    YAHOO.lang.augmentObject(LogicECM.module.Base.DataGridControl_${objectId}.prototype, {
                expand: false,
                selectedItems: {},
                onDataItemCreated: function DataGrid_onDataItemCreated(layer, args) {
                    var obj = args[1];
                    if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.nodeRef !== null)) {
                        var nodeRef = new Alfresco.util.NodeRef(obj.nodeRef);
                        // Reload the node's metadata
                        Alfresco.util.Ajax.jsonPost(
                                {
                                    url: Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + nodeRef.uri,
                                    dataObj: this._buildDataGridParams(),
                                    successCallback: {
                                        fn: function DataGrid_onDataItemCreated_refreshSuccess(response) {
                                            this.versionable = response.json.versionable;
                                            var item = response.json.item;
                                            var fnAfterUpdate = function DataGrid_onDataItemCreated_refreshSuccess_fnAfterUpdate() {
                                                var recordFound = this._findRecordByParameter(item.nodeRef, "nodeRef");
                                                if (recordFound !== null) {
                                                    var el = this.widgets.dataTable.getTrEl(recordFound);
                                                    Alfresco.util.Anim.pulse(el);
                                                }
                                            };
                                            this.afterDataGridUpdate.push(fnAfterUpdate);
                                            this.widgets.dataTable.addRow(item);
                                            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", response.config.successCallback.scope);
                                            var inputAdded = Dom.get("${fieldHtmlId}-added");
                                            inputAdded.value = (inputAdded.value != "") ? inputAdded.value + "," + item.nodeRef : item.nodeRef;
                                            if (response.config.successCallback.scope.options.formMode=="create"){
                                                var input = Dom.get("${fieldHtmlId}");
                                                input.value = (input.value != "") ? input.value + "," + item.nodeRef : item.nodeRef;
                                            }
                                        },
                                        scope: this
                                    },
                                    failureCallback: {
                                        fn: function DataGrid_onDataItemCreated_refreshFailure(response) {
                                            Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text: this.msg("message.create.refresh.failure")
                                                    });
                                        },
                                        scope: this
                                    }
                                });
                    }
                },
                onDelete_Prompt: function (fnAfterPrompt, me, items, itemsString) {
                    if (me._hasEventInterest(me.options.bubblingLabel)) {

                        Alfresco.util.PopupManager.displayPrompt(
                                {
                                    title: this.msg("message.confirm.delete.title", items.length),
                                    text: (items.length > 1) ? this.msg("message.confirm.delete.group.description", items.length) : this.msg("message.confirm.delete.description", itemsString),
                                    buttons: [
                                        {
                                            text: this.msg("button.delete"),
                                            handler: function DataGridActions__onActionDelete_delete() {
                                                var inputAdded = Dom.get("${fieldHtmlId}-added");
                                                if (inputAdded.value != "") {
                                                    var refs = inputAdded.value.split(",");
                                                    for (var i = 0; i < refs.length; i++) {
                                                        if (refs[i] == items[0].nodeRef) {
                                                            inputAdded.value = inputAdded.value.replace(items[0].nodeRef, "");
                                                        }
                                                    }
                                                };
                                                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", me);
                                                this.destroy();
                                                me.selectItems("selectNone");
                                                fnAfterPrompt.call(me, items);
                                            }
                                        },
                                        {
                                            text: this.msg("button.cancel"),
                                            handler: function DataGridActions__onActionDelete_cancel() {
                                                this.destroy();
                                            },
                                            isDefault: true
                                        }
                                    ]
                                });
                    }
                },
                expandRow: function onViewInformation() {
                    var numSelectItem = this.widgets.dataTable.getTrIndex(arguments[1].id);
                    var trId = this.widgets.dataTable.getRecord(numSelectItem).getId();
                    var selectItem = Dom.get(trId);
                    if (selectItem.getAttribute('class').indexOf("expanded") != -1) {
                        this.collapseRow(selectItem);
                    } else {
                        Alfresco.util.Ajax.request(
                                {
                                    url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                                    dataObj: {
                                        htmlid: "${fieldHtmlId}" + arguments[0].nodeRef,
                                        itemKind: "node",
                                        itemId: arguments[0].nodeRef,
                                        formId: "table-structure-info",
                                        mode: "view"
                                    },
                                    successCallback: {
                                        fn: function (response) {
                                            if (response.serverResponse != null) {
                                                var me = response.serverResponse.argument.config.scope;
                                                var rowId = response.serverResponse.argument.config.rowId;
                                                var numSelectItem = me.widgets.dataTable.getTrIndex(rowId);
                                                var oData = me.widgets.dataTable.getRecord(numSelectItem).getData();
                                                me.widgets.dataTable.addRow(oData, numSelectItem + 1);

                                                var trId = me.widgets.dataTable.getRecord(numSelectItem).getId();
                                                var selectItem = Dom.get(trId);
                                                selectItem.setAttribute('class', (selectItem.getAttribute('class') + " " + "expanded"));

                                                trId = me.widgets.dataTable.getRecord(numSelectItem + 1).getId();
                                                var newRecord = Dom.get(trId);
                                                for (var i = 0; i < newRecord.children.length; i++) {
                                                    newRecord.removeChild(newRecord.children[i]);
                                                }
                                                var colCount = me.datagridColumns.length + 1;
                                                newRecord.className = "CLASS_EXPANSION";
                                                newRecord.innerHTML = "<td colspan=" + colCount + " class=\"CLASS_EXPANSION_LINE\">" + response.serverResponse.responseText + "</td>";
                                            }
                                        }
                                    },
                                    failureMessage: "message.failure",
                                    execScripts: true,
                                    scope: this,
                                    rowId: arguments[1].id
                                });
                    }
                },
                collapseRow: function (selectItem) {
                    var infoTag = Dom.getNextSibling(selectItem);
                    this.widgets.dataTable.deleteRow(infoTag);
                    selectItem.setAttribute('class', selectItem.getAttribute('class').replace("expanded"));
                },
            },
            true
    );
    function createDataGrid(nodeRef) {
        var datagrid = new LogicECM.module.Base.DataGridControl_${objectId}('${containerId}').setOptions({
            usePagination: ${usePagination?string},
            showExtendSearchBlock: false,
            formMode: "${form.mode?string}",
            actions: [
                <#if allowExpand = "true">
                    {
                        type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                        id: "expandRow",
                        permission: "edit",
                        label: "${msg("addresser.expand")}"
                    }
                </#if>
                <#if ((allowExpand == "true") && (allowEdit == "true") && (form.mode != "view"))>,</#if>
                <#if ((allowEdit == "true") && (form.mode != "view"))>
                    {
                        type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                        id: "onActionEdit",
                        permission: "edit",
                        label: "${msg("actions.edit")}"
                    }
                </#if>
                <#if ((allowExpand == "true") && (allowDelete == "true") && (form.mode != "view")) || ((allowEdit == "true") && (allowDelete == "true") && (form.mode != "view"))>,</#if>
                <#if ((allowDelete == "true") && (form.mode != "view"))>
                    {
                        type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                        id: "onActionDelete",
                        permission: "delete",
                        label: "${msg("actions.delete-row")}"
                    }
                </#if>
            ],
            datagridMeta: {
                itemType: "${field.control.params.itemType!""}",
                datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                createFormId: "${field.control.params.createFormId!""}",
                nodeRef: nodeRef,
                actionsConfig: {
                    fullDelete: "${field.control.params.fullDelete!"true"}"
                },
                sort: "${field.control.params.sort!""}",
                searchConfig: null,
                documentRef: nodeRef
            },
            bubblingLabel: "${bubblingId}",
            <#if field.control.params.height??>
                height: ${field.control.params.height},
            </#if>
            <#if field.control.params.configURL??>
                configURL: "${field.control.params.configURL}",
            </#if>
            <#if field.control.params.repoDatasource??>
                repoDatasource: ${field.control.params.repoDatasource},
            </#if>
            <#if field.control.params.fixedHeader??>
                fixedHeader: ${field.control.params.fixedHeader},
            </#if>
        allowCreate: <#if form.mode == "view">false<#else>${allowCreate?string}</#if>,
            showActionColumn: ${showActions?string},
            showCheckboxColumn: false,
            attributeForShow: "${attributeForShow?string}"
        }).setMessages(${messages});

        var selectItems = Dom.get("${fieldHtmlId}");
        var filter = "";
        if (selectItems != null) {
            var items = selectItems.value.split(",");
            for (var item in items) {
                filter = filter + " ID:" + items[item].replace(":", "\\:");
                if (filter == "") {
                    filter += "ID:NOT_REF";
                }
            }
        }
        datagrid.options.datagridMeta.searchConfig = {filter: (filter.length > 0 ? filter : "")};
        datagrid.draw();

    };
    function loadRootNode() {
        var sUrl = "";
        <#if (form.mode == "create") || field.control.params.startLocation??>
            sUrl = Alfresco.constants.PROXY_URI + "/lecm/forms/node/search" + "?titleProperty=" + encodeURIComponent("cm:name") + "&xpath=" + encodeURIComponent("${field.control.params.startLocation}");
        <#else>
            var nodeRef = "${form.arguments.itemId}";
            sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/folder?documentNodeRef=" + nodeRef;
        </#if>
        Alfresco.util.Ajax.jsonGet(
                {
                    url: sUrl,
                    successCallback: {
                        fn: function (response) {
                            var oResults = response.json;
                            if (oResults != null) {
                                createDataGrid(response.json.nodeRef);
                            }
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function (oResponse) {
                            var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                            this.widgets.dataTable.set("MSG_ERROR", response.message);
                            this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                        },
                        scope: this
                    }
                });
    };
    function init() {
        loadRootNode();
    }

    YAHOO.util.Event.onContentReady("${fieldHtmlId}", init, true);

})();
//]]></script>
</@grid.datagrid>
<div id="${controlId}-container">
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
    <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
</div>

</div>
