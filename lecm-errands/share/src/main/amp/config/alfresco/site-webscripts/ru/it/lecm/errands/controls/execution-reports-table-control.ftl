<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign bubblingId = containerId/>

<#assign attributeForShow = ""/>
<#if params.attributeForShow??>
    <#assign attributeForShow = params.attributeForShow/>
</#if>

<#assign showActions = false/>
<#if params.showActions?? && params.showActions == "true">
    <#assign showActions = true/>
</#if>

<#assign showPreviousReports = false/>
<#if params.showPreviousReports?? && params.showPreviousReports == "true">
    <#assign showPreviousReports = true/>
</#if>

<#assign isTableSortable = false/>
<#if field.control.params.isTableSortable??>
    <#assign isTableSortable = field.control.params.isTableSortable/>
</#if>

<#assign  expandDataSource = "components/form?formId=table-structure-expand"/>
<#if params.expandDataSource??>
    <#assign  expandDataSource = params.expandDataSource/>
</#if>

<#assign sort = ""/>
<#if field.control.params.sort??>
    <#assign sort = field.control.params.sort/>
</#if>
<script type="text/javascript">//<![CDATA[
(function() {
    function drawForm(){
        var control = new LogicECM.errands.ExecutionReportsTS("${fieldHtmlId}").setMessages(${messages});
        control.setOptions(
                {
                    currentValue: "${field.value!""}",
                    messages: ${messages},
                    bubblingLabel: "${bubblingId}",
                    containerId: "${containerId}",
                    datagridFormId: "${params.datagridFormId!"datagrid"}",
                    attributeForShow: "${attributeForShow}",
                    mode: "${form.mode?string}",
                    disabled: ${field.disabled?string},
                    isTableSortable: ${isTableSortable?string},
                    sort: "${sort?string}",
                    externalCreateId: "${form.arguments.externalCreateId!""}",
                    refreshAfterCreate: false,
                <#if params.deleteMessageFunction??>
                    deleteMessageFunction: "${params.deleteMessageFunction}",
                </#if>
                <#if params.editFormTitleMsg??>
                    editFormTitleMsg: "${params.editFormTitleMsg}",
                </#if>
                <#if params.createFormTitleMsg??>
                    createFormTitleMsg: "${params.createFormTitleMsg}",
                </#if>
                <#if params.viewFormTitleMsg??>
                    viewFormTitleMsg: "${params.viewFormTitleMsg}",
                </#if>
                    expandable: true,
                    expandDataSource: "${expandDataSource}",
                    documentNodeRef: "${form.arguments.itemId}",
                    showActions: ${showActions?string},
                    showPreviousReports: ${showPreviousReports?string}
                });
    }
    function init() {
        LogicECM.module.Base.Util.loadResources([
                    'scripts/lecm-base/components/advsearch.js',
                    'scripts/lecm-base/components/lecm-datagrid.js',
                    'scripts/documents/tables/lecm-document-table.js',
                    'scripts/lecm-errands/execution-reports-table-control.js'
                ],
                [
                    'css/lecm-errands/execution-reports-table-control.css'
                ], drawForm);
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="form-field with-grid execution-reports" id="${controlId}">
    <div class="execution-reports-additional-block">
        <div class="show-previous-reports-block">
            <input type="checkbox" id="${controlId}-show-previous-reports">
            <label id="${controlId}-change-filter-label" for="${controlId}-show-previous-reports">${msg("label.errands.execution-reports.show-previous-reports")}</label>
        </div>
    </div>
    <div id="${containerId}-grid-block" class="grid-block hidden1">
        <@grid.datagrid containerId false/>
        <div id="${controlId}-container">
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
        </div>
    </div>
</div>