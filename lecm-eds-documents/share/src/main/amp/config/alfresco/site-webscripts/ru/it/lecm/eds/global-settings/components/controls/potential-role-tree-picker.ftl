<#include "/org/alfresco/components/component.head.inc">
<#include "/ru/it/lecm/base-share/components/controls/association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign plane = false>
<#assign showCreateNewLink = false>
<#assign showCreateNewButton = false>
<#assign showSelectedItemsPath = false>

<#assign isTrue=false>
<#if field.value??>
	<#if field.value?is_boolean>
		<#assign isTrue=field.value>
	<#elseif field.value?is_string && field.value == "true">
		<#assign isTrue=true>
	</#if>
</#if>

<#if field.control.params.showSearch?? && field.control.params.showSearch == "false">
	<#assign showSearch = false>
<#else>
	<#assign showSearch = true>
</#if>

<#if field.control.params.showViewIncompleteWarning?? && field.control.params.showViewIncompleteWarning == "false">
	<#assign showViewIncompleteWarning = false>
<#else>
	<#assign showViewIncompleteWarning = true>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<div class="form-field potential-role-control">
    <#if disabled>
        <div id="${controlId}" class="viewmode-field">
            <span class="viewmode-label">${field.label?html}:</span>
            <span class="viewmode-value"><#if isTrue>${msg("form.control.checkbox.yes")}<#else>${msg("form.control.checkbox.no")}</#if></span>
        </div>
    <#else>
        <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <div id="${controlId}" class="yui-buttongroup picker-visibility-buttons"
        	<#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
        </div>
        <#--<input class="formsCheckBox" id="${controlId}" type="checkbox" tabindex="0" name="-" <#if field.description??>title="${field.description}"</#if>
             <#if isTrue> value="true" checked="checked"</#if>
             <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />-->
             <#--onchange='javascript:YAHOO.util.Dom.get("${fieldHtmlId}").value=YAHOO.util.Dom.get("${fieldHtmlId}-entry").checked;'-->

        <#assign pickerId = controlId + "-picker">
        <div id="${pickerId}" class="object-finder">
			<div id="${pickerId}-body" class="bd">
				<#if showSearch>
					<div class="yui-gb orgchart-picker-menu">
						<div id="${pickerId}-searchContainer" class="first yui-skin-sam search">
							<input type="text" class="search-input" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
							<span class="search-button"><button id="${pickerId}-searchButton" name="-">&nbsp;</button></span>
						</div>
					</div>
				</#if>

				<div><strong>${msg("logicecm.base.elements-for-select")}</strong></div>
				<div class="yui-g">
					<#if !plane>
						<div id="${pickerId}-treeSelector" class="yui-u first panel-left tree">
							<div id="${pickerId}-groups" class="picker-items ygtv-highlight picker-groups">
							</div>
						</div>
					</#if>
					<div id="${pickerId}-dataTable" class="<#if !plane>yui-u panel-right<#else>width100</#if>">
						<div id="${pickerId}-group-members" class="picker-items group-members"></div>
					</div>
				</div>

				<div id="${pickerId}-selection">
					<div><strong>${msg("logicecm.base.selected-elements")}</strong></div>
					<div id="${pickerId}-selected-elements"></div>
				</div>
			</div>
            <div class="clear"></div>
        </div>
    </#if>
    <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${(!isTrue)?string}" />
</div>

<script type="text/javascript">

    <#if field.control.params.selectedValueContextProperty??>
        <#if context.properties[field.control.params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
        <#elseif args[field.control.params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = args[field.control.params.selectedValueContextProperty]>
        <#elseif context.properties[field.control.params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
        </#if>
    </#if>
    <#assign optionSeparator="|">
    <#assign labelSeparator=":">


    new LogicECM.module.Eds.GlobalSettings.PotentialRolesTreeViewer( "${fieldHtmlId}" ).setOptions({
        <#if disabled>
            disabled: true,
        </#if>
        <#if field.control.params.rootLocation??>
            rootLocation: "${field.control.params.rootLocation}",
        </#if>
		<#if field.control.params.businessRoleId??>
            businessRoleId: "${field.control.params.businessRoleId}",
        </#if>
        <#if field.control.params.employeesLocation??>
            employeesLocation: "${field.control.params.employeesLocation}",
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
        <#if field.control.params.treeNodeSubstituteString??>
            treeNodeSubstituteString: "${field.control.params.treeNodeSubstituteString}",
	    </#if>
        <#if field.control.params.treeNodeTitleSubstituteString??>
            treeNodeTitleSubstituteString: "${field.control.params.treeNodeTitleSubstituteString}",
	    </#if>
        <#if field.control.params.rootNodeRef??>
            rootNodeRef: "${field.control.params.rootNodeRef}",
        </#if>
	    <#if field.control.params.treeItemType??>
		    treeItemType: "${field.control.params.treeItemType}",
	    </#if>
        <#if field.control.params.changeItemsFireAction??>
	        changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
	    </#if>
        <#if args.ignoreNodes??>
            ignoreNodes: "${args.ignoreNodes}".split(","),
	    </#if>
        showCreateNewLink: ${showCreateNewLink?string},
		<#if field.control.params.createNewMessage??>
			createNewMessage: "${field.control.params.createNewMessage}",
		<#elseif field.control.params.createNewMessageId??>
			createNewMessage: "${msg(field.control.params.createNewMessageId)}",
	    </#if>
        <#if field.control.params.createDialogClass??>
            createDialogClass: "${field.control.params.createDialogClass}",
        </#if>
	    showSearch: ${showSearch?string},
        currentValue: ${isTrue?string},
        showSelectedItemsPath: ${showSelectedItemsPath?string},
        <#if renderPickerJSSelectedValue??>selectedValue: "${renderPickerJSSelectedValue}",</#if>
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
        plane: ${plane?string}
    }).setMessages( ${messages} );
</script>