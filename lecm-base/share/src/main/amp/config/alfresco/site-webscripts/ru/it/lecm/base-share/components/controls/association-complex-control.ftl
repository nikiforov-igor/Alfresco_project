<#include '/org/alfresco/components/component.head.inc'>
<#include '/ru/it/lecm/base-share/components/controls/association-control-picker.inc.ftl'>
<#import '/ru/it/lecm/base-share/components/base-components.ftl' as components>

<#assign formId = args.htmlid>

<#assign fieldId = field.configName>

<#assign params = field.control.params>

<#assign endpointType = params.endpointType!field.endpointType>

<#assign endpointMany = field.endpointMany>
<#if params.endpointMany??>
    <#assign endpointMany = (params.endpointMany == "true")>
</#if>

<#assign autocompleteDataSourceMethodPost = false>
<#if params.autocompleteDataSourceMethodPost?? && params.autocompleteDataSourceMethodPost == "true">
	<#assign autocompleteDataSourceMethodPost = true>
</#if>

<#assign items = endpointType?split(',')>
<#if params.items?? && params.items?has_content>
	<#assign items = params.items?split(',')>
</#if>

<#assign showAssocViewForm = true>
<#if params.showAssocViewForm?? && params.showAssocViewForm == "false">
    <#assign showAssocViewForm = false>
</#if>

<#assign readonly = false>
<#assign defaultValue = "">
<#if form.mode == "create" && !field.disabled>
	<#if form.arguments[field.name]?has_content>
		<#assign defaultValue=form.arguments[field.name]>
	<#elseif form.arguments['readonly_' + field.name]?has_content>
		<#assign defaultValue=form.arguments['readonly_' + field.name]>
		<#assign readonly = true>
	<#elseif params.defaultValue??>
		<#assign defaultValue=params.defaultValue>
		<#assign defaultValue=params.defaultValue>
	<#elseif params.selectedItemsFormArgs??>
		<#assign selectedItemsFormArgs = params.selectedItemsFormArgs?split(",")>
		<#list selectedItemsFormArgs as selectedItemsFormArg>
			<#if form.arguments[selectedItemsFormArg]??>
				<#if (defaultValue?length > 0)>
					<#assign defaultValue = defaultValue + ","/>
				</#if>
				<#assign defaultValue = defaultValue + form.arguments[selectedItemsFormArg]/>
			</#if>
		</#list>
	</#if>

	<#assign fieldValue = defaultValue>
<#else>
	<#assign fieldValue = field.value>
</#if>


<#assign disabled = 'view' == form.mode || (field.disabled && !(params.forceEditable?? && 'true' == params.forceEditable?lower_case))>
<#assign isComplex = items?size gt 1>
<#assign showAutocomplete = !disabled && (!params.showAutocomplete?? || 'true' == params.showAutocomplete?lower_case)>

<#assign sortSelected = false>
<#if params.sortSelected?? && params.sortSelected == "true">
	<#assign  sortSelected = true>
</#if>
<#assign showCreateNewButton = false>
<#if params.showCreateNewButton?? && params.showCreateNewButton == "true" && !isComplex>
	<#assign showCreateNewButton = true>
</#if>
<#if 'view' == form.mode>
	<#assign value>
		<input type='hidden' id='${fieldHtmlId}' name='${field.name}' value='${fieldValue?html}'>
		<div id='${fieldHtmlId}-displayed'></div>
	</#assign>
	<@components.baseControl field=field name='association-control' classes='association-control viewmode' value=value disabled=disabled/>
<#else>
	<#assign buttons><@components.baseControlBtns field=field renderCreateBtn=showCreateNewButton/></#assign>
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
				fieldId: '${fieldId}',
				formId: '${formId}',
				disabled: ${disabled?string},
				isComplex: ${isComplex?string},
				showAutocomplete: ${showAutocomplete?string},
				<#if params.autocompleteDataSource??>
				autocompleteDataSource: '${params.autocompleteDataSource}',
				</#if>
				<#if params.dataSourceLogic??>
                dataSourceLogic: '${params.dataSourceLogic}',
				</#if>
                autocompleteDataSourceMethodPost: ${autocompleteDataSourceMethodPost?string},
				<#if params.changeItemsFireAction??>
				changeItemsFireAction: '${params.changeItemsFireAction}',
				</#if>
                endpointMany: ${endpointMany?string},
                showAssocViewForm: ${showAssocViewForm?string},
                showCreateNewButton: ${showCreateNewButton?string},
                sortSelected: ${sortSelected?string},
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
								ignoreNodes: '${args.ignoreNodes}'.split(','),
							<#else>
								ignoreNodes: [],
							</#if>
							<#if params[itemKey + '_getExtSearchQuery']??>
                                getExtSearchQueryFunction: ${params[itemKey + '_getExtSearchQuery']},
							</#if>
							<#if params[itemKey + '_getArgumentsFromForm']??>
                                getArgumentsFromFormFunction: ${params[itemKey + '_getArgumentsFromForm']},
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
							<#if !params?keys?seq_contains("endpointMany") && !params?keys?seq_contains(itemKey + "_endpointMany")>
							    'endpointMany': ${endpointMany?string},
							</#if>
							<#if params[itemKey + "_rootLocationArg"]?? && form.arguments[params[itemKey + "_rootLocationArg"]]??>
								'rootLocation': '${form.arguments[params[itemKey + "_rootLocationArg"]]}',
							<#elseif params.rootLocationArg?? && form.arguments[params.rootLocationArg]??>
								'rootLocation': '${form.arguments[params.rootLocationArg]}',
							</#if>
							<#if params.substituteParent?? && params.substituteParent == "true">
                                'substituteParent':"${form.arguments.itemId!""}",
							</#if>
						}
					}<#if i_has_next>,</#if>
					</#list>
				]
			}, ${messages});
		<#if readonly>
			LogicECM.module.Base.Util.readonlyControl('${formId}', '${fieldId}', true);
		</#if>
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-base/components/controls/association-control.js',
			'scripts/lecm-base/components/controls/association-control.lib.js',
			'scripts/lecm-base/components/controls/association-control.picker.js',
			'scripts/lecm-base/components/controls/association-control.item.js'
			<#if params.additionalScripts?has_content>
				<#list params.additionalScripts?split(",") as js>
					,'${js}'
				</#list>
			</#if>
		], [
			'css/lecm-base/components/controls/association-control.css',
			'css/lecm-base/components/controls/association-control.picker.css'
			<#if params.additionalStyles?has_content>
				<#list params.additionalStyles?split(",") as css>
					,'${css}'
				</#list>
			</#if>
		], initAssociationControl);
	})();
//]]></script>
