<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign bubblingId = containerId/>

<#assign reportId = "review_list">

<#assign itemId = (args.itemId?? && args.itemId?contains("SpacesStore")) ? string(args.itemId, '')/>
<#if itemId == ''>
    <#assign itemId = (args.nodeRef?? && args.nodeRef?contains("SpacesStore")) ? string(args.nodeRef, '')/>
</#if>

<#assign showSearch=false/>
<#if params.showSearch?has_content>
    <#assign showSearch = params.showSearch=="true"/>
</#if>

<#assign showLabel = false>
<#if field.control.params.showLabel?? &&  field.control.params.showLabel == "true">
    <#assign showLabel = true>
</#if>

<#assign dataSource=""/>
<#if params.dataSource?has_content>
    <#assign dataSource = params.dataSource/>
</#if>

<#assign allowCreate = "true"/>
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


<#assign expandDataSource=""/>
<#if params.expandDataSource?has_content>
    <#assign expandDataSource = params.expandDataSource/>
</#if>


<#assign attributeForShow = ""/>
<#if params.attributeForShow??>
    <#assign attributeForShow = params.attributeForShow/>
</#if>

<#assign refreshAfterCreate = "false"/>
<#if params.refreshAfterCreate??>
    <#assign refreshAfterCreate = params.refreshAfterCreate/>
</#if>

<#assign showActions = "true"/>
<#if params.showActions??>
    <#assign showActions = params.showActions/>
</#if>

<#assign usePagination = "true"/>
<#if params.usePagination??>
    <#assign usePagination = params.usePagination/>
</#if>

<#assign showCreateButton = !field.control.params.showCreateBtn?? || "true" == field.control.params.showCreateBtn?lower_case/>

<#assign isTableSortable = false/>
<#if field.control.params.isTableSortable??>
    <#assign isTableSortable = field.control.params.isTableSortable/>
</#if>

<#assign sort = ""/>
<#if field.control.params.sort??>
    <#assign sort = field.control.params.sort/>
</#if>

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

<#assign controlMode = form.mode?string >

<#if ((params.forceEditable!"false")=="true") >
    <#assign controlMode = "edit" >
</#if>

<#assign newRowDialogTitle = ''>
<#if params.newRowDialogTitle??>
    <#assign newRowDialogTitle = params.newRowDialogTitle>
</#if>
<#assign createItemBtnMsg = ''>
<#if params.createItemBtnMsg??>
    <#assign createItemBtnMsg = params.createItemBtnMsg>
</#if>

<#assign editable = (((params.editable!"true") == "true") && !(field.disabled) && (controlMode?string=="edit"))
|| (params.forceEditable?? && params.forceEditable == "true")>

<#assign itemType=field.control.params.itemType!field.endpointType!"">

<div id="${controlId}">
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
    <div id="${controlId}-review-container" class="reviewContainer">
        <div id="${containerId}">
        <@grid.datagrid containerId false />
        </div>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function() {
    function drawForm(){
        var control = new LogicECM.module.Review.RelatedDocumentTable('${controlId}', '${itemId}').setMessages(${messages});
        control.setOptions(
                {
                    reportId: '${reportId}',
                    currentValue: "${field.value!""}",
                    itemType: "${itemType}",
                    usePagination: ${usePagination?string},
                    messages: ${messages},
                    bubblingLabel: "${bubblingId}",
                    containerId: "${containerId}",
                    datagridFormId: "${params.datagridFormId!"datagrid"}",
                    attributeForShow: "${attributeForShow}",
                    mode: "${controlMode?string}",
                    disabled: ${field.disabled?string},
                    isTableSortable: ${isTableSortable?string},
                    sort: "${sort?string}",
                    externalCreateId: "${form.arguments.externalCreateId!""}",
                    refreshAfterCreate: ${refreshAfterCreate?string},
                    allowCreate: false,
                    allowDelete: false,
                    allowEdit: false,
                    expandable: false,
                showActions: <#if editable>${showActions?string}<#else>false</#if>
                });
    }

    LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/advsearch.js',
                'modules/simple-dialog.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
                'scripts/lecm-base/components/lecm-toolbar.js',
                'scripts/documents/tables/lecm-document-table.js',
                'scripts/lecm-review/related-review-document-table.js'
            ],
            [
                'css/components/document-table-control.css',
                'css/lecm-review/review-document-table.css'
            ], drawForm);
})();
//]]></script>
