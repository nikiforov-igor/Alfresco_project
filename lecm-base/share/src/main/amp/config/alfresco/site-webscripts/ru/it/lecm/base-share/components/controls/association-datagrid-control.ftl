<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign objectId = field.name?replace("-", "_")>

<#-- Datagrid Config Start-->
<#assign allowDelete = true/>
<#if field.control.params.allowDelete??>
    <#assign allowDelete = (field.control.params.allowDelete == "true")/>
</#if>

<#assign allowEdit = true/>
<#if field.control.params.allowEdit??>
    <#assign allowEdit = (field.control.params.allowEdit == "true")/>
</#if>

<#assign allowExpand = true/>
<#if field.control.params.allowExpand??>
    <#assign allowExpand = (field.control.params.allowExpand == "true")/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
    <#assign showActions = field.control.params.showActions = "true"/>
</#if>
<#if ((form.mode == "view") && !allowExpand)>
    <#assign showActions = false/>
</#if>

<#assign showLabel = true>
<#if field.control.params.showLabel??>
    <#assign showLabel = (field.control.params.showLabel == "true")>
</#if>

<#assign bubblingId = containerId/>

<#assign usePagination = false/>
<#if field.control.params.usePagination??>
    <#assign usePagination = field.control.params.usePagination == "true"/>
</#if>
<#assign pageSize = 10/>
<#if field.control.params.pageSize??>
    <#assign pageSize = field.control.params.pageSize/>
</#if>

<#assign attributeForShow = ""/>
<#if field.control.params.attributeForShow??>
    <#assign attributeForShow = field.control.params.attributeForShow/>
</#if>

<#assign collapseAllOnExpand = true/>
<#if field.control.params.collapseAllOnExpand??>
    <#assign collapseAllOnExpand = field.control.params.collapseAllOnExpand == "true"/>
</#if>

<#assign noWrapValues = true/>
<#if field.control.params.datagridNoWrapValues??>
    <#assign noWrapValues = field.control.params.datagridNoWrapValues == "true"/>
</#if>
<#-- Datagrid Config End-->

<#-- Toolbar Config Start -->
<#assign showCreateButton = true/>
<#if field.control.params.showCreateButton??>
    <#assign showCreateButton = (field.control.params.showCreateButton == "true")/>
</#if>

<#assign createBtnLabel = msg("label.create-row.title")/>
<#if field.control.params.newRowButtonLabel??>
    <#if msg(field.control.params.newRowButtonLabel) != field.control.params.newRowButtonLabel>
        <#assign createBtnLabel = msg(field.control.params.newRowButtonLabel)/>
    </#if>
</#if>

<#assign showSearchControl = false/>
<#if field.control.params.showSearch??>
    <#assign showSearchControl = (field.control.params.showSearch == "true")/>
</#if>

<#assign exSearch = false/>
<#if field.control.params.showExSearchBtn??>
    <#assign exSearch = (field.control.params.showExSearchBtn == "true")/>
</#if>
<#-- Toolbar Config End -->

<#-- Dialogs Config Start -->
<#assign newRowTitle = "label.create-row.title"/>
<#if field.control.params.newRowDialogTitle??>
    <#assign newRowTitle = field.control.params.newRowDialogTitle/>
</#if>

<#assign editRowTitle = "label.edit-row.title"/>
<#if field.control.params.editRowDialogTitle??>
    <#assign editRowTitle = field.control.params.editRowDialogTitle/>
</#if>
<#-- Dialogs Config End -->

<#-- Commons Start-->
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

<#assign defaultValue=field.value>
<#if form.mode == "create" && defaultValue?string == "">
    <#if field.control.params.selectedItemsFormArgs??>
        <#assign selectedItemsFormArgs = field.control.params.selectedItemsFormArgs?split(",")>
        <#list selectedItemsFormArgs as selectedItemsFormArg>
            <#if form.arguments[selectedItemsFormArg]??>
                <#if (defaultValue?length > 0)>
                    <#assign defaultValue = defaultValue + ","/>
                </#if>
                <#assign defaultValue = defaultValue + form.arguments[selectedItemsFormArg]/>
            </#if>
        </#list>
    <#elseif form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    </#if>
</#if>

<#assign jsObjectName = "LogicECM.module.Base.AssociationDataGrid"/>
<#if field.control.params.jsObjectName??>
    <#assign jsObjectName = field.control.params.jsObjectName/>
</#if>

<#assign itemType=field.control.params.itemType!field.endpointType!"">
<#-- Commons End-->

<script type="text/javascript">//<![CDATA[
(function () {
    var Dom = YAHOO.util.Dom;

    function createToolbar(nodeRef) {
    <#if showCreateButton || showSearchControl || exSearch>
        new LogicECM.module.Base.Toolbar(null, "${fieldHtmlId}").setMessages(${messages}).setOptions({
            bubblingLabel: "${bubblingId}",
            itemType: "${itemType}",
            destination: nodeRef,
        newRowButtonType:<#if field.disabled == true>"inActive"<#else>"defaultActive"</#if>
        });
    </#if>
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
            pageSize: ${pageSize},
            createFormTitleMsg: "${newRowTitle}",
            editFormTitleMsg: "${editRowTitle}",
            formMode: "${form.mode?string}",
            editForm: "${field.control.params.editFormId!""}",
            actions: [
            <#if allowEdit && !field.disabled>
                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "onActionEdit",
                    permission: "edit",
                    label: "${msg("actions.edit")}"
                }
            </#if>
            <#if allowEdit && allowDelete && !field.disabled>,</#if>
            <#if allowDelete && !field.disabled>
                {
                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                    id: "onActionDelete",
                    permission: "delete",
                    label: "${msg("actions.delete-row")}"
                }
            </#if>
            ],
        <#if allowExpand>
            expandDataObj: {
                formId: "${field.control.params.expandFormId!"expand-info"}"
            },
        </#if>
            datagridMeta: {
                itemType: "${itemType}",
                datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                useChildQuery: false,
                useFilterByOrg: false,
                createFormId: "${field.control.params.createFormId!""}",
                nodeRef: nodeRef,
                actionsConfig: {
                    fullDelete: ${(field.control.params.fullDelete!"true")?string}
                },
                sort: "${field.control.params.sort!""}",
                searchConfig: {}
            },
            dataSource:"${field.control.params.ds!"lecm/search"}",
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
            repeating: ${field.repeating?string},
            expandable:${allowExpand?string},
            collapseAllOnExpand: ${collapseAllOnExpand?string},
            noWrapValues:${noWrapValues?string}
        }).setMessages(${messages});

        datagrid._setSearchConfigFilter('${defaultValue!''}');
        <#if field.control.params.datagridMaxSColWidth?has_content>
            datagrid._setMaxStripColumnWidth(${field.control.params.datagridMaxSColWidth});
        </#if>
        <#if field.control.params.datagridStrippedCols?has_content>
            datagrid._setStrippedColumns("${field.control.params.datagridStrippedCols}");
        </#if>
        var inputTag = Dom.get("${fieldHtmlId}");
        var inputAddedTag = Dom.get("${fieldHtmlId}-added");
        var inputRemovedTag = Dom.get("${fieldHtmlId}-removed");
        var selectItemsTag = Dom.get("${fieldHtmlId}-selectedItems");
        datagrid.input = inputTag;
        datagrid.inputAdded = inputAddedTag;
        datagrid.inputRemoved = inputRemovedTag;
        datagrid.selectItemsTag = selectItemsTag;

        datagrid.itemType = "${itemType}";
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
                                Alfresco.util.PopupManager.displayMessage({
                                    text: oResponse.responseText
                                });
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

<div class="control association-datagrid <#if field.control.params.datagridStyles??>${field.control.params.datagridStyles}</#if> with-grid">
<#if showLabel>
    <div class="label-div">
        <label for="${fieldHtmlId}">
        ${field.label?html}:
            <#if isFieldMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </div>
</#if>
    <div>
        <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
        <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added" <#if form.mode == "create">value="${defaultValue?html}"</#if>/>
        <input type="hidden" id="${fieldHtmlId}-selectedItems" <#if form.mode == "create">value="${defaultValue?html}"</#if>/>
    </div>

    <div class="container">
    <#if field.control.params.prefixLabelId?has_content>
        <div class="grid-prefix-str">${msg(field.control.params.prefixLabelId)}:</div>
    </#if>
        <div class="value-div">
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${defaultValue?html}"/>
        <#if showCreateButton || showSearchControl || exSearch>
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
        </#if>
        <@grid.datagrid containerId false/>
        </div>
    <#if field.control.params.postfixLabelId?has_content>
        <div class="grid-prefix-str">${msg(field.control.params.postfixLabelId)}</div>
    </#if>
    </div>
</div>

<div class="clear"></div>
