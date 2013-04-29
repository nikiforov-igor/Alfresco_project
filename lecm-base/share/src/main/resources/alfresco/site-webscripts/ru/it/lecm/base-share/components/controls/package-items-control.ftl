<#include "lecm-package-items.picker.inc.ftl" />
<#assign controlId = fieldHtmlId + "-cntrl">
<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
<script type="text/javascript">//<![CDATA[
(function()
{

<@renderPickerJS field "picker" />
    picker.setOptions(
            {
            showActions:false,
            additionalProperties:"lecm-document:list-present-string",
            <#if field.control.params.showTargetLink??>
                showLinkToTarget: ${field.control.params.showTargetLink},
                <#if page?? && page.url.templateArgs.site??>
                    targetLinkTemplate: "${url.context}/page/site/${page.url.templateArgs.site!""}/document-details?nodeRef={nodeRef}",
                <#else>
                    targetLinkTemplate: "${url.context}/page/document-details?nodeRef={nodeRef}",
                </#if>
                <#if field.control.params.viewOnLinkClick?? && form.mode == "view">
                    viewOnLinkClick: ${field.control.params.viewOnLinkClick},
                </#if>
            </#if>
            <#if field.control.params.targetLink??>
                linkToTarget: "${field.control.params.targetLink}",
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
            <#if field.control.params.nameSubstituteString??>
                nameSubstituteString: "${field.control.params.nameSubstituteString}",
            </#if>
            <#if field.control.params.substituteParent?? && field.control.params.substituteParent == "true">
                substituteParent:"${form.arguments.itemId!""}",
            </#if>
                displayMode: "${field.control.params.displayMode!"list"}"
            });
})();
//]]></script>

<div class="form-field">
<@view.viewForm formId="${controlId}-view-assoc-form"/>
<#if form.mode == "view">
    <div id="${controlId}" class="viewmode-field">
        <#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
        </#if>
        <span class="viewmode-label">${field.label?html}:</span>
        <div id="lecm-package-items-span" style="width:100%;">
            <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values" ></span>
        </div>
    </div>
    <div id="${controlId}-link" class="yui-panel" style="display: none">
        <div id="${controlId}-link-head" class="hd">${msg("logicecm.view")}</div>
        <div id="${controlId}-link-body" class="bd">
            <div id="${controlId}-link-content"></div>
            <div class="bdft">
                <span id="${controlId}-link-cancel" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button" tabindex="0" onclick="_hideLinkAttributes()">${msg("button.close")}</button>
                    </span>
                </span>
            </div>
        </div>
    </div>
<#else>
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <div id="${controlId}" class="object-finder">

        <div id="${controlId}-currentValueDisplay" class="current-values" style="width:80%; float:none"></div>

        <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
            <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
            <div id="${controlId}-itemGroupActions" class="show-picker"></div>

            <@renderPickerHTML controlId />
        </#if>
    </div>
</#if>
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
                targetLinkTemplate: ${documentLinkResolver},
                <#if form.mode == "create" && form.destination?? && form.destination?length &gt; 0>
                    startLocation: "${form.destination?js_string}",
                <#elseif field.control.params.startLocation??>
                    startLocation: "${field.control.params.startLocation?js_string}",
                </#if>
                itemType: "cm:content",
                displayMode: "${field.control.params.displayMode!"list"}",
                listItemActions: [
                    <#list actions as action>
                        {
                            name: "${action.name}",
                            <#if action.link??>
                                link: ${action.link},
                            <#elseif action.event>
                                event: "${action.event}",
                            </#if>
                            label: "${action.label}"
                        }<#if action_has_next>,</#if>
                    </#list>],
                allowRemoveAction: ${allowRemoveAction?string},
                allowRemoveAllAction: ${allowRemoveAllAction?string},
                allowSelectAction: ${allowAddAction?string},
                selectActionLabel: "${field.control.params.selectActionLabel!msg("button.add")}"
            });
})();
//]]></script>

</#macro>

<@setPackageItemOptions field />