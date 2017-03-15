<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#if field.control.params.size??><#assign size=field.control.params.size><#else><#assign size=1></#if>

<#if field.control.params.optionSeparator??>
    <#assign optionSeparator=field.control.params.optionSeparator>
<#else>
    <#assign optionSeparator=",">
</#if>
<#if field.control.params.labelSeparator??>
    <#assign labelSeparator=field.control.params.labelSeparator>
<#else>
    <#assign labelSeparator="|">
</#if>

<#assign fieldValue=field.value>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<#if fieldValue?string != "">
    <#assign values=fieldValue?split(",")>
<#else>
    <#assign values=[]>
</#if>

<#if field.control.params.multiply??>
    <#assign multiply=(field.control.params.multiply == "true")>
<#else>
    <#assign multiply=false/>
</#if>

<#assign endpointMany = multiply>
<#if field.control.params.endpointMany??>
    <#assign endpointMany = (field.control.params.endpointMany == "true")>
</#if>

<#if field.control.params.docType??>
    <#assign docType=field.control.params.docType/>
<#else>
    <#assign docType=''/>
</#if>

<#if field.control.params.nameSuffix??>
    <#assign nameSuffix=field.control.params.nameSuffix>
<#else>
    <#assign nameSuffix="">
</#if>

<#assign controlId = fieldHtmlId + "-cntrl">

<#if form.mode == "view">
<div class="control select-status-control viewmode">
    <div class="label-div">
        <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
                                              title="${msg("form.field.incomplete")}"/><span>
        </#if>
        <label>${field.label?html}:</label>
    </div>
    <#if fieldValue?string == "">
        <#assign valueToShow=msg("form.control.novalue")>
    <#else>
        <#if field.control.params.options?? && field.control.params.options != "" &&
        field.control.params.options?index_of(labelSeparator) != -1>
            <#assign valueToShow="">
            <#assign firstLabel=true>
            <#list field.control.params.options?split(optionSeparator) as nameValue>
                <#assign choice=nameValue?split(labelSeparator)>
                <#if isSelected(choice[0])>
                    <#if !firstLabel>
                        <#assign valueToShow=valueToShow+",">
                    <#else>
                        <#assign firstLabel=false>
                    </#if>
                    <#assign valueToShow=valueToShow+choice[1]>
                </#if>
            </#list>
        <#else>
            <#assign valueToShow=fieldValue>
        </#if>
    </#if>
    <div class="container">
        <div class="value-div">
        ${valueToShow?html}
        </div>
    </div>
</div>
<#else>
<div class="control select-status-control editmode">
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
            <input id="${fieldHtmlId}" type="hidden" name="${field.name}${nameSuffix}" value="${fieldValue?string}"/>

            <select id="${controlId}" name="-" <#if endpointMany>multiple="multiple" </#if> tabindex="0"
                    onchange="javascript:Alfresco.util.updateMultiSelectListValue('${controlId}', '${fieldHtmlId}', <#if field.mandatory>true<#else>false</#if>);"
                    <#if field.description??>title="${field.description}"</#if>
                    <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                    <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                    <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
                <#if field.control.params.withEmpty?? && field.control.params.withEmpty == "true">
                    <option value=""></option></#if>
            </select>
        </div>
    </div>
</div>
</#if>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-documents/select-status-control.js'
        ], createControl);
    }

    function createControl() {
        var control = new LogicECM.module.Document.SelectStatusCtrl("${fieldHtmlId}").setOptions({
            controlId: "${controlId}",
            docType: "${docType}",
            selectedValue: "${fieldValue}",
            mandatory: ${field.mandatory?string},
        <#if form.arguments??>
            currentNodeRef: "${form.arguments.itemId}",
        </#if>
        <#if field.control.params.valuesDelimiter??>
            valuesDelimiter: "${field.control.params.valuesDelimiter}",
        </#if>
            destination: "${args.destination!""}"
        }).setMessages(${messages});
        control.draw();
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#function isSelected optionValue>
    <#list values as value>
        <#if optionValue == value?string || (value?is_number && value?c == optionValue)>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

