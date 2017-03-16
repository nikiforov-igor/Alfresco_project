<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#if field.control.params.size??>
    <#assign size=field.control.params.size>
<#else>
    <#assign size=5>
</#if>

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

<#if field.control.params.useDefaultValue?? && field.control.params.useDefaultValue == "false">
    <#assign fieldValue = "">
<#else>
    <#assign fieldValue = field.value>
</#if>

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

<#if field.control.params.nameSuffix??>
    <#assign nameSuffix=field.control.params.nameSuffix>
<#else>
    <#assign nameSuffix="">
</#if>

<#if form.mode == "view">
<div class="control selectmany viewmode">
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
        <#if field.control.params.options?? && field.control.params.options != "" && field.control.params.options?index_of(labelSeparator) != -1>
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
<div class="control selectmany editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">${field.label?html}:
            <#if field.mandatory>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
            </#if>
        </label>
    </div>
    <div class="container">
        <div class="buttons-div">
            <#if field.control.params.options?? && field.control.params.options != "">
                <@formLib.renderFieldHelp field=field />
                <#if field.control.params.mode?? && isValidMode(field.control.params.mode?upper_case)>
                    <input id="${fieldHtmlId}-mode" type="hidden" name="${field.name}-mode"
                           value="${field.control.params.mode?upper_case}"/>
                </#if>
            <#else>
                <div id="${fieldHtmlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
            </#if>
        </div>
        <div class="value-div">
            <input id="${fieldHtmlId}" type="hidden" name="${field.name}${nameSuffix}" value="${fieldValue?string}"/>
            <#if field.control.params.options?? && field.control.params.options != "">
                <select id="${fieldHtmlId}-entry" name="-" multiple="multiple" size="${size}" tabindex="0"
                        onchange="javascript:Alfresco.util.updateMultiSelectListValue('${fieldHtmlId}-entry', '${fieldHtmlId}', <#if field.mandatory>true<#else>false</#if>);"
                        <#if field.description??>title="${field.description}"</#if>
                        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                        <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>

                    <#if field.control.params.withEmpty?? && field.control.params.withEmpty == "true">
                        <option value=""></option>
                    </#if>

                    <#list field.control.params.options?split(optionSeparator) as nameValue>
                        <#if nameValue?index_of(labelSeparator) == -1>
                            <option value="${nameValue?html}"<#if isSelected(nameValue)>
                                    selected="selected"</#if>>${nameValue?html}</option>
                        <#else>
                            <#assign choice=nameValue?split(labelSeparator)>
                            <option value="${choice[0]?html}"<#if isSelected(choice[0])>
                                    selected="selected"</#if>>${msgValue(choice[1])?html}</option>
                        </#if>
                    </#list>
                </select>
            <#else>
                <div id="${fieldHtmlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
            </#if>
        </div>
    </div>
</div>
</#if>
<div class="clear"></div>

<#function isSelected optionValue>
    <#list values as value>
        <#if optionValue == value?string || (value?is_number && value?c == optionValue)>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function isValidMode modeValue>
    <#return modeValue == "OR" || modeValue == "AND">
</#function>