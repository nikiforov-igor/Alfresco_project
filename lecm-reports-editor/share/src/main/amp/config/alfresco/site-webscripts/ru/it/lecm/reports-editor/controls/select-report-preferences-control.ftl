<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign fieldId=field.id!"">

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign isFieldMandatory = false>
<#if field.control.params.notSelectedOptionLabel??>
    <#assign notSelectedText = field.control.params.notSelectedOptionLabel>
<#elseif field.control.params.notSelectedOptionLabelCode??>
    <#assign notSelectedText = msg(field.control.params.notSelectedOptionLabelCode)>
</#if>
<#assign notSelectedOption = false>
<#if field.control.params.notSelectedOption??>
    <#assign notSelectedOption = field.control.params.notSelectedOption == "true">
</#if>

<div class="control select-report-template-control editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="buttons-div">
            <span class="create-new-preference">
                <a href="javascript:void(0);" id="${controlId}-create-new">${msg("report.param.preferences.save")}</a>
            </span>
            <span class="delete-preference">
                <a href="javascript:void(0);" id="${controlId}-delete">${msg("report.param.preferences.delete")}</a>
            </span>
        </div>
        <div class="value-div">
            <select id="${fieldHtmlId}" name="-" tabindex="0"
                    <#if field.description??>title="${field.description}"</#if>
                    <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                    <#if field.control.params.style??>style="${field.control.params.style}"</#if>>
            </select>
        </div>
    </div>
</div>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.module.Base.Util.loadScripts([
        'scripts/lecm-reports-editor/select-param-preference-ctrl.js'
    ], createControl);

    function createControl() {

        var control = new LogicECM.module.ReportsEditor.SelectParamPreference("${fieldHtmlId}").setMessages(${messages});
        control.setOptions({
            reportCode: <#if field.control.params.reportId??>"${field.control.params.reportId}"<#else>"${form.arguments.itemId}"</#if>,
            maxSearchResults: 30,
            notSelectedOptionShow: ${notSelectedOption?string},
            notSelectedText: "${notSelectedText?string}",
            changeItemFireAction: "${field.control.params.changeItemFireAction!''?string}",
            fieldId: "${fieldId}"
        });

    }
})();
//]]></script>
