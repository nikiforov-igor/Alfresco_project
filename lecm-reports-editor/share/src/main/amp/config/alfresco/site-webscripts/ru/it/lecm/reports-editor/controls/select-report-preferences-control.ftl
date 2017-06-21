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

<div class="control select-report-preferences-control editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div preferences-select">
            <select id="${fieldHtmlId}" name="-"></select>
        </div>
        <div class="buttons-div">
            <span class="create-new-preference">
                <a href="javascript:void(0);" id="${controlId}-create-new">${msg("report.param.preferences.save")}</a>
            </span>
            <span class="delete-preference">
                <a href="javascript:void(0);" id="${controlId}-delete">${msg("report.param.preferences.delete")}</a>
            </span>
        </div>
    </div>
</div>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.module.Base.Util.loadResources([
        'scripts/lecm-reports-editor/select-param-preference-ctrl.js'
    ],
    ['css/lecm-reports-editor/select-report-preferences-control.css'], createControl);

    function createControl() {

        var control = new LogicECM.module.ReportsEditor.SelectParamPreference("${fieldHtmlId}").setMessages(${messages});
        control.setOptions({
            reportCode: <#if field.control.params.reportId??>"${field.control.params.reportId}"<#else>"${form.arguments.itemId}"</#if>,
            maxSearchResults: 30,
            formId: "${args.htmlid}",
            fieldId: "${field.configName}",
            notSelectedOptionShow: ${notSelectedOption?string},
            notSelectedText: "${notSelectedText?string}",
            changeItemFireAction: "${field.control.params.changeItemFireAction!''?string}",
            <#if field.control.params.currentValue?has_content>
                currentValue: "${field.control.params.currentValue}",
            </#if>
            <#if field.control.params.preferencesValue?has_content>
                preferencesValue: "${field.control.params.preferencesValue?json_string}",
                needSort: false,
            </#if>
            defaultValues: {}
        });

    }
})();
//]]></script>
