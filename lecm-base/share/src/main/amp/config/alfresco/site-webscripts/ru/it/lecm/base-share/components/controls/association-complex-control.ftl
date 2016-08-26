<#include '/org/alfresco/components/component.head.inc'>
<#include '/ru/it/lecm/base-share/components/controls/association-control-picker.inc.ftl'>
<#import '/ru/it/lecm/base-share/components/base-components.ftl' as components>

<#assign params = field.control.params>

<#assign endpointType = params.endpointType!field.endpointType>

<#assign endpointMany = field.endpointMany>
<#if params.endpointMany??>
    <#assign endpointMany = (params.endpointMany == "true")>
</#if>

<#assign items = endpointType?split(',')>
<#if params.items?? && params.items?has_content>
	<#assign items = params.items?split(',')>
</#if>

<#assign showAssocViewForm = true>
<#if params.showAssocViewForm?? && params.showAssocViewForm == "false">
    <#assign showAssocViewForm = false>
</#if>

<#assign defaultValue = "">
<#if form.mode == "create" && !field.disabled>
	<#if params.selectedItemsFormArgs??>
		<#assign selectedItemsFormArgs = params.selectedItemsFormArgs?split(",")>
		<#list selectedItemsFormArgs as selectedItemsFormArg>
			<#if form.arguments[selectedItemsFormArg]??>
				<#if (defaultValue?length > 0)>
					<#assign defaultValue = defaultValue + ","/>
				</#if>
				<#assign defaultValue = defaultValue + form.arguments[selectedItemsFormArg]/>
			</#if>
		</#list>

	<#elseif form.arguments[field.name]?has_content>
		<#assign defaultValue=form.arguments[field.name]>
	<#elseif params.defaultValue??>
		<#assign defaultValue=params.defaultValue>
	</#if>

	<#assign fieldValue = defaultValue>
<#else>
	<#assign fieldValue = field.value>
</#if>


<#assign disabled = 'view' == form.mode || (field.disabled && !(params.forceEditable?? && 'true' == params.forceEditable?lower_case))>
<#assign isComplex = items?size gt 1>
<#assign showAutocomplete = !disabled && (!params.showAutocomplete?? || 'true' == params.showAutocomplete?lower_case)>

<#if 'view' == form.mode>
	<#assign value>
		<input type='hidden' id='${fieldHtmlId}' name='${field.name}' value='${fieldValue?html}'>
		<div id='${fieldHtmlId}-displayed'></div>
	</#assign>
	<@components.baseControl field=field name='association-control' classes='association-control viewmode' value=value disabled=disabled/>
<#else>
	<#assign buttons><@components.baseControlBtns field=field renderCreateBtn=false/></#assign>
	<#assign value><@components.baseControlValue field=field fieldValue=fieldValue showAutocomplete=showAutocomplete isDefaultValue=defaultValue?has_content/></#assign>
	<@components.baseControl field=field name='association-control' classes='association-control' buttons=buttons value=value>
		<#if showAutocomplete>
		<div id='${fieldHtmlId}-autocomplete-container'></div>
		</#if>
		<div class='hidden'>
			<@picker controlId=fieldHtmlId items=items params=params/>
		</div>
	</@>
</#if>
<div class='clear'></div>
<script type='text/javascript'>//<![CDATA[
	(function () {
		function initAssociationControl() {
			new LogicECM.module.AssociationComplexControl('${fieldHtmlId}', '${fieldValue}', {
				disabled: ${disabled?string},
				isComplex: ${isComplex?string},
				showAutocomplete: ${showAutocomplete?string},
				<#if params.autocompleteDataSource??>
				autocompleteDataSource: '${params.autocompleteDataSource}',
				</#if>
				<#if params.changeItemsFireAction??>
				changeItemsFireAction: '${params.changeItemsFireAction}',
				</#if>
                multipleSelectMode: ${endpointMany?string},
                showAssocViewForm: ${showAssocViewForm?string},
				itemsOptions: [
					<#list items as i>
						<#assign itemKey = i?replace(":", "_")>
					{
						itemKey: '${itemKey}',
						options: {
							disabled: ${disabled?string},
							<#if isComplex>
							itemType: '${params[itemKey + '_endpointType']}',
							<#else>
							itemType: '${i}',
							</#if>
							<#if args.ignoreNodes??>
								ignoreNodes: '${args.ignoreNodes?split(',')}',
							<#else>
								ignoreNodes: [],
							</#if>
							<#list params?keys as key>
								<#assign isNotBoolean = 'true' != params[key] && 'false' != params[key]>
								<#if isComplex && key?starts_with(itemKey)>
									<#-- если контрол комплексный, то отрезаем префикс от параметров -->
									'${key?replace(itemKey + '_', "")}': <#if isNotBoolean>'</#if>${params[key]}<#if isNotBoolean>'</#if>,
								<#elseif !isComplex>
									<#-- если контрол простой, то передаем параметры как есть -->
									'${key}': <#if isNotBoolean>'</#if>${params[key]}<#if isNotBoolean>'</#if>,
								</#if>
							</#list>
						}
					}<#if i_has_next>,</#if>
					</#list>
				]
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-base/components/controls/association-control.js',
			'scripts/lecm-base/components/controls/association-control.lib.js',
			'scripts/lecm-base/components/controls/association-control.picker.js',
			'scripts/lecm-base/components/controls/association-control.item.js'
		], [
			'css/lecm-base/components/controls/association-control.css',
			'css/lecm-base/components/controls/association-control.picker.css'
		], initAssociationControl);
	})();
//]]></script>
