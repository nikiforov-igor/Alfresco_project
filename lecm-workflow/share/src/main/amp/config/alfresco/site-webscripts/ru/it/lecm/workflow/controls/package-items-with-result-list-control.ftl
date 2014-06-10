<#include "lecm-package-items.picker.inc.ftl" />
<#assign controlId = fieldHtmlId + "-cntrl">

<#assign resultListType = field.control.params.resultListType!'lecm-workflow-result:workflow-result-list'>

<script type="text/javascript">//<![CDATA[
(function () {
<@renderPickerJS field "picker" />
    picker.setOptions(
            {
				resultListType: "${resultListType}",
                showActions: false,
                additionalProperties: "lecm-document:list-present-string",
                itemType: "cm:content",
                multipleSelectMode: ${field.endpointMany?string},
                parentNodeRef: "alfresco://company/home",
            <#if field.control.params.rootNode??>
                rootNode: "${field.control.params.rootNode}",
            </#if>
                itemFamily: "node",
            <#if field.control.params.nameSubstituteString??>
                nameSubstituteString: "${field.control.params.nameSubstituteString}",
            </#if>
            <#if field.control.params.substituteParent?? && field.control.params.substituteParent == "true">
                substituteParent: "${form.arguments.itemId!""}",
            </#if>
                displayMode: "list"
            });
})();
//]]></script>
<#-- Лютый костыль для правки отображения формы как поля (иначе отображается с рамкой и выползает) -->
<style type="text/css">
    div#lecm-result-list-span div.form-fields {
        border: none !important;
        padding-left: 0 !important;
        padding-right: 0 !important;
    }
</style>

<div class="form-field">
    <div id="${controlId}" class="viewmode-field">
        <span class="viewmode-label">${msg("label.attachments")}:</span>
        <div id="lecm-package-items-span">
            <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
        </div>
    </div>
    <div id="${controlId}-list" class="viewmode-field">
        <span class="viewmode-label result-list-label">${msg("label.resultList")}:</span>
        <div id="lecm-result-list-span">
            <span id="${controlId}-currentListValueDisplay" class="viewmode-value"></span>
        </div>
    </div>
</div>

<#macro setPackageItemOptions field>

    <#local documentLinkResolver>
    function(item)
    {
        return Alfresco.util.siteURL("document?nodeRef=" + item.nodeRef);
    }
</#local>
    <#local allowAddAction = false>
    <#local allowRemoveAllAction = false>
    <#local allowRemoveAction = false>
    <#local actions = []>

    <#if form.data['prop_bpm_packageActionGroup']?? && form.data['prop_bpm_packageActionGroup']?is_string && form.data['prop_bpm_packageActionGroup']?length &gt; 0>
        <#local allowAddAction = true>
</#if>

    <#if form.data['prop_bpm_packageItemActionGroup']?? && form.data['prop_bpm_packageItemActionGroup']?is_string && form.data['prop_bpm_packageItemActionGroup']?length &gt; 0>
        <#local packageItemActionGroup = form.data['prop_bpm_packageItemActionGroup']>
        <#local viewMoreAction = { "name": "view_more_actions", "label": "form.control.object-picker.workflow.view_more_actions", "link": documentLinkResolver }>
        <#if packageItemActionGroup == "read_package_item_actions" || packageItemActionGroup == "edit_package_item_actions">
            <#local actions = actions + [viewMoreAction]>
        <#elseif packageItemActionGroup == "remove_package_item_actions" || packageItemActionGroup == "start_package_item_actions" || packageItemActionGroup == "edit_and_remove_package_item_actions">
            <#local actions = actions + [viewMoreAction]>
            <#local allowRemoveAllAction = true>
            <#local allowRemoveAction = true>
        <#elseif packageItemActionGroup >
        <#else>
            <#local actions = actions + [viewMoreAction]>
</#if>
</#if>


<#-- Additional item actions -->

<script type="text/javascript">//<![CDATA[
(function()
{
<#-- Modify the properties on the object finder created by association control-->
    var picker = Alfresco.util.ComponentManager.get("${controlId}");
    picker.setOptions(
            {
                showLinkToTarget: true,
                targetLinkTemplate: ${documentLinkResolver}
            });
})();
//]]></script>

</#macro>

<@setPackageItemOptions field />