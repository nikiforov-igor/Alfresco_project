<#include "picker.inc.ftl" />
<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
<#if field.control.params.numberOfHiddenLayers??>
    // place a custom function before the execution of the onParentDetails-function
    LogicECM.module.ObjectFinder.prototype.onParentDetailsOriginal = LogicECM.module.ObjectFinder.prototype.onParentDetails;
    LogicECM.module.ObjectFinder.prototype.onParentDetails = function(layer, args)
    {
        // cut the last x parent elements
        var counter = 0;
        cut = function(node)
        {
            if (node.parent)
            {
                cut(node.parent);
                if (counter < ${field.control.params.numberOfHiddenLayers})
                {
                    delete node.parent;
                    counter = counter + 1;
                }
            }
        }
        cut(args[1]);
        // call the original function with the modified args-parameter
        this.onParentDetailsOriginal(layer, args);
    };
</#if>

<@renderPickerJS field "picker" />
    picker.setOptions(
            {
            <#if field.control.params.showTargetLink??>
                showLinkToTarget: ${field.control.params.showTargetLink},
                <#if page?? && page.url.templateArgs.site??>
                    targetLinkTemplate: "${url.context}/page/site/${page.url.templateArgs.site!""}/document-details?nodeRef={nodeRef}",
                <#else>
                    targetLinkTemplate: "${url.context}/page/document-details?nodeRef={nodeRef}",
                </#if>
            </#if>
            <#if field.control.params.allowNavigationToContentChildren??>
                allowNavigationToContentChildren: ${field.control.params.allowNavigationToContentChildren},
            </#if>
                itemType: "${field.endpointType}",
                multipleSelectMode: ${field.endpointMany?string},
                parentNodeRef: "alfresco://company/home",
            <#if field.control.params.rootNode??>
                rootNode: "${field.control.params.rootNode}",
            </#if>
                itemFamily: "node",
                displayMode: "${field.control.params.displayMode!"items"}",
            <#if field.control.params.nameSubstituteString??>
                nameSubstituteString: "${field.control.params.nameSubstituteString}",
            </#if>
            });
})();
//]]></script>

<div class="form-field">
<#if form.mode == "view">
    <div id="${controlId}" class="viewmode-field">
        <#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
        </#if>
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
    </div>
<#else>
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <div id="${controlId}" class="object-finder">

        <div id="${controlId}-currentValueDisplay" class="current-values"></div>

        <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
            <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
            <div id="${controlId}-itemGroupActions" class="show-picker"></div>

            <@renderPickerHTML controlId />
        </#if>
    </div>
</#if>
</div>