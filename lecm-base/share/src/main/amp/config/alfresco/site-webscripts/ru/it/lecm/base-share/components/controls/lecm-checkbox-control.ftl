<#assign isTrue=false>
<#assign readonly = false>
<#assign defaultValue="">
<#if field.control.params.defaultValue?has_content>
    <#assign defaultValue=false>
    <#if field.control.params.defaultValue?is_boolean>
		<#assign defaultValue=field.control.params.defaultValue>
	<#elseif field.control.params.defaultValue?is_string && field.control.params.defaultValue == "true">
		<#assign defaultValue=true>
	</#if>
</#if>
<#if form.arguments[field.name]?has_content>
    <#assign defaultValue=false>
    <#if form.arguments[field.name]?is_boolean>
		<#assign defaultValue=form.arguments[field.name]>
	<#elseif form.arguments[field.name]?is_string && form.arguments[field.name] == "true">
		<#assign defaultValue=true>
	<#elseif form.arguments['readonly_' + field.name]?has_content && form.arguments['readonly_' + field.name] == "true">
		<#assign defaultValue=true>
		<#assign readonly=true>
	</#if>
</#if>

<#if field.value??>
	<#if field.value?is_boolean>
		<#assign isTrue=field.value>
	<#elseif field.value?is_string && field.value == "true">
		<#assign isTrue=true>
	</#if>
</#if>

<#assign fireMandatoryByChange = false/>
<#if field.control.params.fireMandatoryByChange?? && field.control.params.fireMandatoryByChange == "true">
	<#assign fireMandatoryByChange = true/>
</#if>

<#assign fieldId=field.id!"">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function()
{
	function init() {
        LogicECM.module.Base.Util.loadScripts(['scripts/lecm-base/components/lecm-checkbox.js'],
                createControl);
	}

	function createControl() {
	var control = new LogicECM.module.Checkbox("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
			<#if disabled>
				disabled: true,
			</#if>
			<#if field.control.params.defaultValueDataSource??>
				defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
			</#if>
            <#if defaultValue?has_content>
				defaultValue: "${defaultValue?string}",
			</#if>
			<#if field.control.params.disabledFieldsIfSelect??>
				disabledFieldsIfSelect: "${field.control.params.disabledFieldsIfSelect}".split(","),
			</#if>
			<#if field.control.params.hideFieldsIfSelect??>
				hideFieldsIfSelect: "${field.control.params.hideFieldsIfSelect}".split(","),
			</#if>
			<#if field.control.params.attentionMessageKey??>
                attentionMessage: "${msg(field.control.params.attentionMessageKey)}",
			</#if>
			<#if field.control.params.disabledFieldsIfNotSelect??>
				disabledFieldsIfNotSelect: "${field.control.params.disabledFieldsIfNotSelect}".split(","),
			</#if>
			<#if field.control.params.hideFieldsIfNotSelect??>
				hideFieldsIfNotSelect: "${field.control.params.hideFieldsIfNotSelect}".split(","),
			</#if>
			<#if field.control.params.changeFireAction??>
                changeFireAction: "${field.control.params.changeFireAction}",
			</#if>
				fireMandatoryByChange: ${fireMandatoryByChange?string},
				mode: "${form.mode}",
				fieldId: "${field.configName}",
				formId: "${args.htmlid}"
			});
	<#if readonly>
		LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
	</#if>
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#if form.mode == "view">
	<div class="control checkbox-control viewmode">
		<div class="label-div">
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<#if isTrue>
					${msg("form.control.checkbox.yes")}
				<#else>
					${msg("form.control.checkbox.no")}
				</#if>
				<input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="<#if isTrue>true<#else>false</#if>" />
			</div>
		</div>
	</div>
<#else>
	<div class="control checkbox-control editmode">
		<div class="label-div">
			<label>&nbsp;</label>
		</div>
		<div class="container">
            <div class="buttons-div">
                <@formLib.renderFieldHelp field=field />
            </div>
			<div class="value-div">
				<input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="<#if isTrue>true<#else>false</#if>" />
				<input class="formsCheckBox" id="${fieldHtmlId}-entry" type="checkbox" tabindex="0" name="-" <#if field.description??>title="${field.description}"</#if>
					<#if isTrue> value="true" checked="checked"</#if>
					   <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>
					   <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
					   <#if field.control.params.style??>style="${field.control.params.style}"</#if> />
				<label for="${fieldHtmlId}-entry" class="checkbox">${field.label?html}</label>
				<div id="${fieldHtmlId}-attention" class="error"></div>
			</div>
		</div>
	</div>
</#if>
<div class="clear"></div>
