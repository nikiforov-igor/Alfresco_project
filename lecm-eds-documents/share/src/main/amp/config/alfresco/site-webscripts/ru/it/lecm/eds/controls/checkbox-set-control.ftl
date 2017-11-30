<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<div id="${fieldHtmlId}">
    <div class="control lecm-checkboxtable with-grid" id="${controlId}">
        <div class="label-div">
            <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        </div>
        <div class="container">
            <div class="value-div">
                <div id="${fieldHtmlId}-body" class="datagrid">
                    <div id="${fieldHtmlId}-dataTable" class="grid"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-eds-documents/controls/checkbox-set-control.js'
        ], createDatagrid);
    }

    function createDatagrid() {
        var control = new LogicECM.module.EDS.CheckboxSet('${fieldHtmlId}').setOptions({
            valueSet: ${field.control.params.set},
        <#if field.control.params.nameColumnId??>
            nameColumnId: "${field.control.params.nameColumnId}",
        </#if>
            fieldId: "${field.configName}",
            formArguments: {
            <#list form.arguments?keys as k>
                "${k}": "${form.arguments[k]!''}"<#if k_has_next>,</#if>
            </#list>}
        }).setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>