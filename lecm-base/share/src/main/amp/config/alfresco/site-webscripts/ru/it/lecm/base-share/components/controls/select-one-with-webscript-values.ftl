<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value>
<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-selectone.js'
        ], createSelectOne);
    }
    function createSelectOne(){
        new LogicECM.module.SelectOne("${fieldHtmlId}").setOptions({
            controlId: "${controlId}",
            webscriptType: "${field.control.params.webscriptType!"share"}",
            <#if field.control.params.webscript??>
            webscript: "${field.control.params.webscript}",
            </#if>
            <#if field.control.params.withEmpty??>
            withEmpty: ${field.control.params.withEmpty},
            </#if>
            <#if field.control.params.needSort??>
            needSort: ${field.control.params.needSort},
            </#if>
            selectedValue: "${fieldValue}",
            mandatory: ${field.mandatory?string},
            currentNodeRef: "${form.arguments.itemId}",
            destination: "${args.destination!""}",
            updateOnAction: "${field.control.params.updateOnAction!""}",
            <#if field.control.params.changeItemFireAction??>
                changeItemFireAction: "${field.control.params.changeItemFireAction}",
            </#if>
            fieldId: "${field.configName}",
            formId: "${args.htmlid}"
        }).setMessages(${messages});
    }
    YAHOO.util.Event.onDOMReady(init);
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
                    <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
            </select>
        </div>
    </div>
</div>
<div class="clear"></div>
