<#include "/org/alfresco/components/component.head.inc">
<#include "association-search-control-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign selectedValue = "">
<#assign params = field.control.params>

<#if field.control.params.showViewIncompleteWarning?? && field.control.params.showViewIncompleteWarning == "false">
	<#assign showViewIncompleteWarning = false>
<#else>
	<#assign showViewIncompleteWarning = true>
</#if>

<#if field.control.params.showSelectedItems?? && field.control.params.showSelectedItems == "false">
	<#assign showSelectedItems = false>
<#else>
	<#assign showSelectedItems = true>
</#if>

<#assign defaultValue=field.control.params.defaultValue!"">
<#if form.arguments[field.name]?has_content>
    <#assign defaultValue=form.arguments[field.name]>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<div class="form-field">
<#if disabled>
	<div id="${controlId}" class="viewmode-field">
		<#if showViewIncompleteWarning && (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
		</#if>
		<span class="viewmode-label">${field.label?html}:</span>
		<span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
	</div>
<#else>
	<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<div id="${controlId}" class="object-finder">
		<div id="${controlId}-currentValueDisplay" class="current-values"></div>

		<#if field.disabled == false>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div id="${controlId}-itemGroupActions" class="show-picker">
                <span class="tree-picker-button">
                    <input type="button" id="${controlId}-tree-picker-button" name="-" value="..."/>
                </span>
			</div>

			<@renderSearchPickerDialogHTML controlId/>
		</#if>

		<div class="clear"></div>
	</div>
</#if>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
</div>

<script type="text/javascript">
	<#if field.control.params.selectedValueContextProperty??>
		<#if context.properties[field.control.params.selectedValueContextProperty]??>
			<#assign selectedValue = context.properties[field.control.params.selectedValueContextProperty]>
		<#elseif args[field.control.params.selectedValueContextProperty]??>
			<#assign selectedValue = args[field.control.params.selectedValueContextProperty]>
		<#elseif context.properties[field.control.params.selectedValueContextProperty]??>
			<#assign selectedValue = context.properties[field.control.params.selectedValueContextProperty]>
		</#if>
	</#if>
	<#assign optionSeparator="|">
	<#assign labelSeparator=":">
    <#if selectedValue == "" && params.selectedItemsFormArgs??>
        <#assign selectedItemsFormArgs = params.selectedItemsFormArgs?split(",")>
        <#list selectedItemsFormArgs as selectedItemsFormArg>
            <#if form.arguments[selectedItemsFormArg]??>
                <#if !selectedValue??>
                    <#assign selectedValue = ""/>
                </#if>
                <#if (selectedValue?length > 0)>
                    <#assign selectedValue = selectedValue + ","/>
                </#if>
                <#assign selectedValue = selectedValue + form.arguments[selectedItemsFormArg]/>
            </#if>
        </#list>
    </#if>


	new LogicECM.module.AssociationSearchViewer( "${fieldHtmlId}" ).setOptions({
		createDialog: true,
		<#if disabled>
			disabled: true,
		</#if>
		<#if field.control.params.rootLocation??>
			rootLocation: "${field.control.params.rootLocation}",
		</#if>
		<#if field.mandatory??>
			mandatory: ${field.mandatory?string},
		<#elseif field.endpointMandatory??>
			mandatory: ${field.endpointMandatory?string},
		</#if>
			multipleSelectMode: ${field.endpointMany?string},

		<#if field.control.params.nameSubstituteString??>
			nameSubstituteString: "${field.control.params.nameSubstituteString}",
		</#if>
		<#if field.control.params.selectedItemsNameSubstituteString??>
			selectedItemsNameSubstituteString: "${field.control.params.selectedItemsNameSubstituteString}",
		</#if>
		<#if field.control.params.rootNodeRef??>
			rootNodeRef: "${field.control.params.rootNodeRef}",
		</#if>
		<#if field.control.params.changeItemsFireAction??>
			changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
		</#if>
		<#if args.ignoreNodes??>
			ignoreNodes: "${args.ignoreNodes}".split(","),
		</#if>
			currentValue: "${field.value!''}",
		<#if selectedValue != "">
			selectedValue: "${selectedValue}",
		</#if>
        <#if field.control.params.defaultValueDataSource??>
            defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
        </#if>
        <#if defaultValue?has_content>
            defaultValue: "${defaultValue?string}",
        </#if>
		<#if field.control.params.fireAction?? && field.control.params.fireAction != "">
			fireAction: {
				<#list field.control.params.fireAction?split(optionSeparator) as typeValue>
					<#if typeValue?index_of(labelSeparator) != -1>
						<#assign type=typeValue?split(labelSeparator)>
						<#if type[0] == "addItem">
							addItem: "${type[1]}",
						</#if>
						<#if type[0] == "cancel">
							cancel: "${type[1]}",
						</#if>
					</#if>
				</#list>
			},
		</#if>
        <#if field.control.params.additionalFilter?has_content>
            additionalFilter:"${field.control.params.additionalFilter}",
        </#if>
		showSelectedItems: ${showSelectedItems?string},
		<#if field.control.params.itemType??>
			itemType: "${field.control.params.itemType}"
		<#else>
			itemType: "${field.endpointType}"
		</#if>
	}).setMessages( ${messages} );
</script>