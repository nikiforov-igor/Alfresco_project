<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

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

<#-- Toolbar -->
<#assign showSearchControl = true/>
<#if field.control.params.showSearch??>
    <#assign showSearchControl = (field.control.params.showSearch == "true")/>
</#if>

<#assign exSearch = false/>
<#if field.control.params.showExSearchBtn??>
    <#assign exSearch = (field.control.params.showExSearchBtn == "true")/>
</#if>

<#assign showCreateButton = true/>
<#if field.control.params.showCreateBtn??>
    <#assign showCreateButton = field.control.params.showCreateBtn/>
</#if>

<#assign newRowTitle = "label.create-row.title"/>
<#if field.control.params.newRowDialogTitle??>
    <#assign newRowTitle = field.control.params.newRowDialogTitle/>
</#if>
<#assign editRowTitle = "label.edit-row.title"/>
<#if field.control.params.editRowDialogTitle??>
    <#assign editRowTitle = field.control.params.editRowDialogTitle/>
</#if>

<#assign createBtnLabel = msg("label.create-row.title")/>

<#assign isFieldMandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign isFieldMandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign isFieldMandatory = field.mandatory>
<#elseif field.endpointMandatory??>
    <#assign isFieldMandatory = field.endpointMandatory>
</#if>

<#if field.control.params.newRowButtonLabel??>
    <#if msg(field.control.params.newRowButtonLabel) != field.control.params.newRowButtonLabel>
        <#assign createBtnLabel = msg(field.control.params.newRowButtonLabel)/>
    </#if>
</#if>

<#assign jsObjectName = "LogicECM.module.Base.AssociationDataGrid"/>
<#if field.control.params.jsObjectName??>
    <#assign jsObjectName = field.control.params.jsObjectName/>
</#if>

<script type="text/javascript">//<![CDATA[
(function () {
    var Dom = YAHOO.util.Dom;

    function createToolbar(nodeRef) {
        new LogicECM.module.Base.Toolbar(null, "${fieldHtmlId}").setMessages(${messages}).setOptions({
            bubblingLabel: "${bubblingId}",
            itemType: "${field.control.params.itemType!""}",
            destination: nodeRef,
            newRowButtonType:<#if field.disabled == true>"inActive"<#else>"defaultActive"</#if>
        });
    }

    function createDataGrid(nodeRef) {
        LogicECM.module.Base.DataGridControl_${objectId} = function (htmlId) {
            LogicECM.module.Base.DataGridControl_${objectId}.superclass.constructor.call(this, htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation"]);
            return this;
        };

        YAHOO.extend(LogicECM.module.Base.DataGridControl_${objectId}, ${jsObjectName}, {
            ${field.control.params.actionsHandler!""}
        });

        var datagrid = new LogicECM.module.Base.DataGridControl_${objectId}('${containerId}').setOptions({
            usePagination: ${usePagination?string},
            showExtendSearchBlock: false,
            createFormTitleMsg: "${newRowTitle}",
            editFormTitleMsg: "${editRowTitle}",
            formMode: "${form.mode?string}",
			editForm: "${field.control.params.editFormId!""}",
            actions: [
            <#if allowExpand = "true">
                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "expandRow",
                    permission: "edit",
                    label: "${msg("addresser.expand")}"
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
            datagridMeta: {
                itemType: "${field.control.params.itemType!""}",
                datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                useChildQuery: false,
                useFilterByOrg: false,
                createFormId: "${field.control.params.createFormId!""}",
                nodeRef: nodeRef,
                actionsConfig: {
                    fullDelete: "${field.control.params.fullDelete!"true"}"
                },
                sort: "${field.control.params.sort!""}",
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
		<#if field.control.params.isTableSortable?has_content>
			overrideSortingWith: ${field.control.params.isTableSortable?string},
		</#if>

		    showActionColumn: ${showActions?string},
            showCheckboxColumn: false,
            attributeForShow: "${attributeForShow?string}",
            repeating: ${field.repeating?string}
        }).setMessages(${messages});

        var inputTag = Dom.get("${fieldHtmlId}");
        var inputAddedTag = Dom.get("${fieldHtmlId}-added");
        var inputRemovedTag = Dom.get("${fieldHtmlId}-removed");
        var selectItemsTag = Dom.get("${fieldHtmlId}-selectedItems");
        var filter = "";
        if (inputTag != null && inputTag.value != "") {
            var items = inputTag.value.split(",");
            selectItemsTag.value = inputTag.value;
            for (var item in items) {
                filter = filter + " ID:" + items[item].replace(":", "\\:");
            }
        }
        if (filter == "") {
            filter += "ID:NOT_REF";
        }
        datagrid.options.datagridMeta.searchConfig = {filter: (filter.length > 0 ? filter : "")};
        datagrid.filterValues = inputTag.value;
        datagrid.input = inputTag;
        datagrid.inputAdded = inputAddedTag;
        datagrid.inputRemoved = inputRemovedTag;
        datagrid.selectItemsTag = selectItemsTag;
        datagrid.itemType = "${field.endpointType}";
        datagrid.assocType = "${field.configName}";
        <#if form.mode != "create">
            datagrid.documentRef = "${form.arguments.itemId}";
        </#if>
        datagrid.draw();
    }

    function getRootUrlParams(directoryPath) {
        if (directoryPath) {
            if (directoryPath.charAt(0) == "{") {
                var location = directoryPath;
                if (directoryPath == "{companyhome}") {
                    location = "alfresco://company/home";
                } else if (directoryPath == "{userhome}") {
                    location = "alfresco://user/home";
                } else if (directoryPath == "{siteshome}") {
                    location = "alfresco://sites/home";
                } else if (directoryPath == "{usertemp}") {
                    location = "alfresco://user/temp";
                }
                return location;
            }
        }

        return directoryPath;
    }

    function loadRootNode() {
        var sUrl = "";
    <#if field.control.params.startLocation?? || field.control.params.rootNode??>
        sUrl = Alfresco.constants.PROXY_URI + "/lecm/forms/node/search" + "?titleProperty=" + encodeURIComponent("cm:name") +
        <#if field.control.params.startLocation??>"&xpath=" + encodeURIComponent("${field.control.params.startLocation}")<#else>""</#if> +
        <#if field.control.params.rootNode??>"&rootNode=" + encodeURIComponent(getRootUrlParams("${field.control.params.rootNode}"))<#else>""</#if>;
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
                                    createToolbar(response.json.nodeRef);
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
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-toolbar.js',
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-base/components/lecm-association-datagrid-control.js'
            <#if field.control.params.jsDependencies??>
                <#list field.control.params.jsDependencies?split(",") as js>
                    ,'${js}'
                </#list>
            </#if>
        ],
        [
            'css/lecm-base/components/association-datagrid-control.css'
            <#if field.control.params.cssDependencies??>
                <#list field.control.params.cssDependencies?split(",") as css>
                    ,'${css}'
                </#list>
            </#if>
        ],loadRootNode);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control association-datagrid with-grid">
    <div class="label-div">
    <#if showLabel>
        <label for="${fieldHtmlId}">
        ${field.label?html}:
            <#if isFieldMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </#if>
        <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
        <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
        <input type="hidden" id="${fieldHtmlId}-selectedItems"/>
    </div>

    <div class="container">
        <div class="value-div">
        <@comp.baseToolbar fieldHtmlId true showSearchControl exSearch>
            <#if showCreateButton>
                <div class="new-row">
                <span id="${fieldHtmlId}-newRowButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="${createBtnLabel}">${createBtnLabel}</button>
                   </span>
                </span>
                </div>
            </#if>
        </@comp.baseToolbar>

        <@grid.datagrid containerId false/>
        <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
        </div>
    </div>
</div>

<div class="clear"></div>
