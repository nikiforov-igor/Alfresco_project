<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value>
<#assign selectedValue = field.control.params.selectedValue!'def'>
<#assign values = field.control.params.values!'def'>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/controls/lecm-selectone.js' group='model-editor'/>
<script type="text/javascript">//<![CDATA[
(function() {
    function createSelectOne(obj){
        new LogicECM.module.SelectOneME("${fieldHtmlId}").setOptions({
            <#if field.control.params.withEmpty??>
            withEmpty: ${field.control.params.withEmpty},
            </#if>
            selectedValue: obj.${selectedValue},
            mandatory: ${field.mandatory?string},
            values: obj.${values}
        }).setMessages(${messages});
    }
    LogicECM.module.ModelEditor.ModelPromise.then(createSelectOne);
})();
//]]></script>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<div class="control selectone-with-webscript-values editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">${field.label?html}:
        <#if field.mandatory>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
        </#if>
        </label>
    </div>
    <div class="container">
        <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
        <div class="value-div">
            <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
                    <#if field.description??>title="${field.description}"</#if>
                    <#if field.control.params.size??>size="${field.control.params.size}"</#if>
                    <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                    <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                    <#if (field.control.params.readOnly?? && field.control.params.readOnly == "true") || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>disabled="true"</#if>>
            </select>
        </div>
    </div>
</div>
<div class="clear"></div>
