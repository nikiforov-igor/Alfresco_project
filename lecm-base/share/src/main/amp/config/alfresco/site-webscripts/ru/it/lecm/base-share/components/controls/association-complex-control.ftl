<#include '/org/alfresco/components/component.head.inc'>
<#include '/ru/it/lecm/base-share/components/controls/association-control-picker.inc.ftl'>
<#import '/ru/it/lecm/base-share/components/base-components.ftl' as components>

<#assign params = field.control.params>

<#assign endpointType = params.endpointType!field.endpointType>

<#assign items = endpointType?split(',')>
<#if params.items?? && params.items?has_content>
	<#assign items = params.items?split(',')>
</#if>

<#assign disabled = 'view' == form.mode || (field.disabled && !(params.forceEditable?? && 'true' == params.forceEditable?lower_case))>
<#assign isComplex = items?size gt 1>
<#assign showAutocomplete = !disabled && !isComplex && (!params.showAutocomplete?? || 'true' == params.showAutocomplete?lower_case)>

<#if 'view' == form.mode>
	<#assign value>
		<input type='hidden' id='${fieldHtmlId}' name='${field.name}' value='${field.value?html}'>
		<div id='${fieldHtmlId}-displayed'></div>
	</#assign>
	<@components.baseControl field=field name='association-control' classes='association-control viewmode' value=value/>
<#else>
	<#assign buttons><@components.baseControlBtns field=field renderCreateBtn=false/></#assign>
	<#assign value><@components.baseControlValue field=field showAutocomplete=showAutocomplete/></#assign>
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
			new LogicECM.module.AssociationComplexControl('${fieldHtmlId}', '${field.value}', {
				disabled: ${disabled?string},
				isComplex: ${isComplex?string},
				showAutocomplete: ${showAutocomplete?string},
				<#if params.childrenDataSource??>
				childrenDataSource: '${params.childrenDataSource}',
				</#if>
				<#if params.changeItemsFireAction??>
				changeItemsFireAction: '${params.changeItemsFireAction}',
				</#if>
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
							<#if params[itemKey + '_childrenDataSource']??>
                                childrenDataSource: '${params[itemKey + '_childrenDataSource']}',
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
