<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#--todo: здесь у toolbar-а должен быть свой id, но с другим пока не работает-->
<#assign controlIdToolbar = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign objectId = field.name?replace("-", "_")>

<#-- Datagrid -->
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
<#if ((form.mode == "view") && (allowExpand == "false"))>
    <#assign showActions = false/>
</#if>

<#assign showLabel = true>
<#if field.control.params.showLabel?? &&  field.control.params.showLabel == "false">
    <#assign showLabel = false>
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

<#assign isSortable = (form.mode == "view")/>
<#if field.control.params.isSortable?? && (form.mode != "view")>
    <#assign isSortable = field.control.params.isSortable/>
</#if>

<#-- Toolbar -->
<#assign showSearchControl = true/>
<#if field.control.params.showSearch??>
    <#assign showSearchControl = field.control.params.showSearch/>
</#if>

<#assign exSearch = false/>
<#if field.control.params.showExSearchBtn??>
    <#assign exSearch = field.control.params.showExSearchBtn/>
</#if>

<#assign showCreateButton = true/>
<#if field.control.params.showCreateBtn??>
    <#assign showCreateButton = field.control.params.showCreateBtn/>
</#if>

<#assign newRowTitle = "label.create-row.title"/>
<#if field.control.params.newRowDialogTitle??>
    <#assign newRowTitle = field.control.params.newRowDialogTitle/>
</#if>

<#assign createBtnLabel = msg("label.create-row.title")/>

<#if field.control.params.newRowButtonLabel??>
    <#if msg(field.control.params.newRowButtonLabel) != field.control.params.newRowButtonLabel>
        <#assign createBtnLabel = msg(field.control.params.newRowButtonLabel)/>
    </#if>
</#if>

<script type="text/javascript">//<![CDATA[
(function () {
    function createToolabar(nodeRef) {
        new LogicECM.module.Base.Toolbar(null, "${controlIdToolbar}").setMessages(${messages}).setOptions({
            bubblingLabel: "${bubblingId}",
            itemType: "${field.control.params.itemType!""}",
            destination: nodeRef,
        newRowButtonType:<#if field.disabled == true>"inActive"<#else>"defaultActive"</#if>
        });
    }

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
                    label: "${msg("action.expand")}"
                }
            </#if>
            <#if ((allowExpand == "true") && (allowEdit == "true") && (field.disabled != true))>,</#if>
            <#if ((allowEdit == "true") && (field.disabled != true))>
                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "onActionEdit",
                    permission: "edit",
                    label: "${msg("actions.edit")}"
                }
            </#if>
            <#if ((allowExpand == "true") && (allowDelete == "true") && (field.disabled != true)) || ((allowEdit == "true") && (allowDelete == "true"))>,</#if>
            <#if ((allowDelete == "true") && (field.disabled != true))>
                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "onActionDelete",
                    permission: "delete",
                    label: "${msg("actions.delete-row")}"
                }
            </#if>
            ],
            otherActions:[
                                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "onMoveTableRowUp",
                    permission: "edit",
                    label: "${msg("actions.tableRowUp")}"
                },
                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "onMoveTableRowDown",
                    permission: "edit",
                    label: "${msg("action.tableRowDown")}"
                },
                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "onAddRow",
                    permission: "edit",
                    label: "${msg("action.addRow")}"
                }
            ],
            datagridMeta: {
                itemType: "${field.control.params.itemType!""}",
                datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                createFormId: "${field.control.params.createFormId!""}",
                useChildQuery: true,
                nodeRef: nodeRef,
                actionsConfig: {
                    fullDelete: "${field.control.params.fullDelete!"true"}"
                },
                sort: "${field.control.params.sort!"lecm-document:indexTableRow"}",
                searchConfig: {}
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
            showActionColumn: ${showActions?string},
            showOtherActionColumn: true,
            showCheckboxColumn: false,
            attributeForShow: "${attributeForShow?string}",
            repeating: ${field.repeating?string},
            overrideSortingWith: ${isSortable?string},
            useCookieForSort: false
        }).setMessages(${messages});

        var inputTag = Dom.get("${fieldHtmlId}");
        var inputAddedTag = Dom.get("${fieldHtmlId}-added");
        var inputRemovedTag = Dom.get("${fieldHtmlId}-removed");
        var selectItemsTag = Dom.get("${controlId}-selectedItems");

	    datagrid.options.datagridMeta.searchNodes = "${field.value?html}".split(",");
        datagrid.filterValues = inputTag.value;
        datagrid.input = inputTag;
        datagrid.inputAdded = inputAddedTag;
        datagrid.inputRemoved = inputRemovedTag;
        datagrid.selectItems = selectItemsTag;
        datagrid.itemType = "${field.endpointType}";
        datagrid.assocType = "${field.configName}";
        datagrid.documentRef = "${form.arguments.itemId}";
        datagrid.draw();
    }

    function loadRootNode() {
        var sUrl = "";
    <#if field.control.params.startLocation??>
        sUrl = Alfresco.constants.PROXY_URI + "/lecm/forms/node/search" + "?titleProperty=" + encodeURIComponent("cm:name") + "&xpath=" + encodeURIComponent("${field.control.params.startLocation}");
    <#elseif (field.control.params.startLocationScriptUrl?? && (form.mode != "create"))>
        var nodeRef = "${form.arguments.itemId}";
        sUrl = Alfresco.constants.PROXY_URI + "${field.control.params.startLocationScriptUrl}?nodeRef=" + nodeRef;
    </#if>
        if (sUrl != "") {
        Alfresco.util.Ajax.jsonGet(
                {
                    url: sUrl,
                    successCallback: {
                        fn: function (response) {
                            var oResults = response.json;
                            if (oResults != null) {
                                createToolabar(response.json.nodeRef);
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
        }
    }
    function init() {
        loadRootNode();
    }

    YAHOO.util.Event.onContentReady("${fieldHtmlId}", init, true);
})();
//]]></script>

<@comp.baseToolbar controlIdToolbar true showSearchControl exSearch>
    <#if showCreateButton>
    <div class="new-row">
        <span id="${controlIdToolbar}-newRowButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${createBtnLabel}">${createBtnLabel}</button>
           </span>
        </span>
    </div>
    </#if>
</@comp.baseToolbar>

<div class="form-field with-grid" id="${controlId}">
    <#if showLabel>
        <label for="${controlId}" style="white-space: nowrap; overflow: visible;">${field.label?html}:
            <#if field.endpointMandatory!false || field.mandatory!false>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
            </#if>
        </label>
    </#if>

<@grid.datagrid containerId false>
<script type="text/javascript">//<![CDATA[
(function () {
    var Dom = YAHOO.util.Dom;
    LogicECM.module.Base.DataGridControl_${objectId} = function (htmlId) {
        LogicECM.module.Base.DataGridControl_${objectId}.superclass.constructor.call(this, htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Base.DataGridControl_${objectId}, LogicECM.module.Base.DataGridAssociation, {
    ${field.control.params.actionsHandler!""}
    });
})();
//]]></script>
    <div style="display:none">
        <!-- Action Set "More..." container -->
        <div id="${containerId}-otherMoreActions">
            <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span></span></a></div>
            <div class="more-actions hidden"></div>
        </div>
        <div id="${containerId}-otherActionSet" class="action-set simple"></div>
    </div>
</@grid.datagrid>
<div id="${controlId}-container">
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
    <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
    <input type="hidden" id="${controlId}-selectedItems"/>
</div>

</div>