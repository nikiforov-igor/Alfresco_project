<#include "/org/alfresco/components/form/controls/common/picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">

<#function isValidMode modeValue>
	<#return modeValue == "OR" || modeValue == "AND">
</#function>


<script type="text/javascript">//<![CDATA[
(function() {
	<@renderPickerJS field "picker" />
	picker.setOptions({
		itemType: "cm:category",
		multipleSelectMode: ${(field.control.params.multipleSelectMode!true)?string},
		parentNodeRef: "${field.control.params.parentNodeRef!"alfresco://category/root"}",
		itemFamily: "category",
		maintainAddedRemovedItems: false,
		params: "${field.control.params.params!""}",
		createNewItemUri: "${field.control.params.createNewItemUri!}",
		createNewItemIcon: "${field.control.params.createNewItemIcon!}"
	});
})();
//]]></script>

<#if form.mode == "view">
<div class="control textfield viewmode">
	<div class="label-div">
		<#if (field.mandatory!false) && (field.value == "")>
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /></span>
		</#if>
		<#if field.label != ""><label>${field.label?html}:</label></#if>
	</div>
	<div class="container">
		<div class="value-div">
			<span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
		</div>
	</div>
</div>
<#else>
<div class="control category editmode">
	<div class="label-div">
		<#if field.label != "">
		<label for="${controlId}">${field.label?html}:<#if field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		</#if>
	</div>
	<div class="container">
		<div class="buttons-div">
			<@formLib.renderFieldHelp field=field />
			<#if field.disabled == false>
				<div id="${controlId}-itemGroupActions" class="show-picker inlineable"></div>
			</#if>
		</div>
		<div class="value-div">
			<div id="${controlId}" class="object-finder inlineable">
				<div id="${controlId}-currentValueDisplay" class="current-values inlineable" style="box-shadow: 0.33px 2px 8px rgba(0, 0, 0, 0.1); width: 100%;"></div>
				<#if field.disabled == false>
					<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />

					<#if field.control.params.showSubCategoriesOption?? && field.control.params.showSubCategoriesOption == "true">
						<div class="subcats-option">
							<input type="checkbox" name="${field.name}_usesubcats" value="true" checked="true" />&nbsp;${msg("form.control.category.include.subcats")}
						</div>
					</#if>

					<#if field.control.params.mode?? && isValidMode(field.control.params.mode?upper_case)>
						<input id="${fieldHtmlId}-mode" type="hidden" name="${field.name}-mode" value="${field.control.params.mode?upper_case}" />
					</#if>

					<@renderPickerHTML controlId />
				</#if>
			</div>
		</div>
	</div>
</div>
</#if>
<div class="clear"></div>
