<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#assign htmlid=args.htmlid?html>
<#assign fieldValue=field.value!"">
<#assign fieldId=field.id!"">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{

    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-association-cascading-selectone.js'
        ], createAssociationSelectOne, [ "container", "resize", "datasource"]);
    }

    function createAssociationSelectOne() {
        var control = new LogicECM.module.AssociationCascadingSelectOne("${fieldHtmlId}").setMessages(${messages});
        control.setOptions(
                {
                <#if field.control.params.parentNodeRef??>
                    parentNodeRef: "${field.control.params.parentNodeRef}",
                </#if>
                <#if field.control.params.startLocation??>
                    startLocation: "${field.control.params.startLocation}",
                </#if>
                <#if field.mandatory??>
                    mandatory: ${field.mandatory?string},
                <#elseif field.endpointMandatory??>
                    mandatory: ${field.endpointMandatory?string},
                </#if>
                    itemType: "${field.endpointType}",
                    itemFamily: "node",
                    maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
                    selectedValueNodeRef: "${fieldValue}",
                    nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
                    sortProp: "${field.control.params.sortProp!'cm:name'}",
                    htmlId: "${htmlid}",
                    dependentFieldName: {
                    <#if field.control.params.dependentFieldName?? && field.control.params.dependentFieldName != "">
                        <#assign fieldName=field.control.params.dependentFieldName?split(",")>
                        <#list fieldName as name>
                            "${name_index}": "${name?replace(":","_")}",
                        </#list>
                    </#if>
                    },
                    dependentFieldArgKey: {
                    <#if field.control.params.dependentFieldArgKey?? && field.control.params.dependentFieldArgKey != "">
                        <#assign fieldName=field.control.params.dependentFieldArgKey?split(",")>
                        <#list fieldName as name>
                            "${name_index}": "${name?replace(":","_")}",
                        </#list>
                    </#if>
                    },
                    webScriptUrl: "${field.control.params.webScriptUrl}",
                    fieldId: "${fieldId}",
                <#if field.control.params.notSelectedOptionShow?? && field.control.params.notSelectedOptionShow == "false">
                    showDefaultOptions: false,
                </#if>
                <#if field.control.params.defaultLoadData?? && field.control.params.defaultLoadData == "true">
                    defaultLoadData: true,
                </#if>
                <#if field.control.params.primaryCascading??>
                    primaryCascading: ${field.control.params.primaryCascading}
                </#if>
                });
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#if form.mode == "view">
<div class="control association-cascading-select-one viewmode">
    <div class="label-div">
        <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
        </#if>
        <label>${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div">
            <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
            <span id="${fieldHtmlId}-currentValueDisplay" class="mandatory-highlightable"></span>
        </div>
    </div>
</div>
<#else>
<div class="control association-cascading-select-one editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}-added">
            ${field.label?html}:
            <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </div>
    <div class="container">
        <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
        <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>

        <div id="${fieldHtmlId}-controls" class="value-div">
            <select id="${fieldHtmlId}-added" name="${field.name}_added" tabindex="0"
                    <#if field.description??>title="${field.description}"</#if>
                    <#if field.control.params.size??>size="${field.control.params.size}"</#if>
                    <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                    <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                    <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"
                    <#elseif field.control.params.disabled?? && field.control.params.disabled == "true">disabled="true"</#if>>
                <#if field.control.params.notSelectedOptionShow?? && field.control.params.notSelectedOptionShow == "true">
                    <option value="">
                        <#if field.control.params.notSelectedOptionLabel??>
                            ${field.control.params.notSelectedOptionLabel}
                        <#elseif field.control.params.notSelectedOptionLabelCode??>
                        ${msg(field.control.params.notSelectedOptionLabelCode)}
                        </#if>
                    </option>
                </#if>
            </select>
        </div>
    </div>
</div>
</#if>
<div class="clear"></div>
