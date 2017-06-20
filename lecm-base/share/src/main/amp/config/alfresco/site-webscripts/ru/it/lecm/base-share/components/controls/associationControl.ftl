<#--This associationControl.ftl is deprecated!
    Use association-control.ftl instead
!-->
<#include "picker.inc.ftl" />
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as view/>
<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function()
{
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/object-finder/lecm-object-finder.js'
		], createPicker);
	}
	function createPicker(){
		<@renderPickerJS field "picker" />
	    picker.setOptions({
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
        <#if field.control.params.sortProp??>
            sortProp: "${field.control.params.sortProp}",
        </#if>
	    <#if field.control.params.substituteParent?? && field.control.params.substituteParent == "true">
	    	substituteParent:"${form.arguments.itemId!""}",
	    </#if>
	    displayMode: "${field.control.params.displayMode!"items"}"
		});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#if disabled>
	<div id="${controlId}" class="control association-control viewmode">
		<div class="label-div">
			<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
			</div>
		</div>
	</div>

    <div id="${controlId}-link" class="yui-panel hidden1">
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
	<div class="control association-control editmode">
		<div class="label-div">
			<label for="${controlId}">
				${field.label?html}:
				<#if field.endpointMandatory!false || field.mandatory!false>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div id="${controlId}" class="container">
			<#if field.disabled == false>
				<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
				<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
				<input type="hidden" id="${controlId}-selectedItems"/>

				<div id="${controlId}-itemGroupActions" class="buttons-div">
					<div id="${controlId}-itemGroupActions" class="show-picker"></div>
				</div>

				<@renderPickerHTML controlId />
			</#if>

			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable"></div>
			</div>

		</div>
	</div>
</#if>
<div class="clear"></div>