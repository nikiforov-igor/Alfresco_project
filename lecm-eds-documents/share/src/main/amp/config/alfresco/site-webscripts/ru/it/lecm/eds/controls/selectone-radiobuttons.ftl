<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#if field.control.params.optionSeparator??>
   <#assign optionSeparator=field.control.params.optionSeparator>
<#else>
   <#assign optionSeparator=",">
</#if>
<#if field.control.params.labelSeparator??>
   <#assign labelSeparator=field.control.params.labelSeparator>
<#else>
   <#assign labelSeparator="|">
</#if>

<#assign fieldValue=field.value>
<#if form.mode == "create" && fieldValue?string == "">
    <#if field.control.params.defaultValue??>
        <#assign fieldValue=field.control.params.defaultValue>
    </#if>
    <#if form.arguments[field.name]?has_content>
        <#assign fieldValue=form.arguments[field.name]>
    </#if>
</#if>

<#assign disabled = field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>

<#if form.mode == "view">
	<div id="${fieldHtmlId}-parent" class="control selectone-radiobuttons viewmode">
		<div class="label-div">
			<label for="${fieldHtmlId}">${field.label?html}:
				<#if field.mandatory>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
        	</label>
		</div>
		<#if fieldValue?string == "">
			<#assign valueToShow=msg("form.control.novalue")>
		<#else>
			<#assign valueToShow=fieldValue>
			<#if field.control.params.options?? && field.control.params.options != "">
				<#list field.control.params.options?split(optionSeparator) as nameValue>
					<#if nameValue?index_of(labelSeparator) == -1>
						<#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)>
							<#assign valueToShow=nameValue>
							<#break>
						</#if>
					<#else>
						<#assign choice=nameValue?split(labelSeparator)>
						<#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])>
							<#assign valueToShow=msgValue(choice[1])>
							<#break>
						</#if>
					</#if>
				</#list>
			</#if>
		</#if>
		<div class="container">
			<div class="value-div">
				${valueToShow?html}
			</div>
		</div>
	</div>
<#else>
	<div id="${fieldHtmlId}-parent" class="control selectone-radiobuttons editmode">
		<div class="label-div">
            <label for="${fieldHtmlId}">${field.label?html}:
				<#if field.mandatory>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
            </label>
		</div>
		<div class="container">
            <div class="buttons-div">
                <@formLib.renderFieldHelp field=field />
            </div>
            <div class="value-div">
				<#if field.control.params.options?? && field.control.params.options != "">
					<input id="${fieldHtmlId}" type="hidden" name="-" value="${fieldValue?html}"/>
					<#list field.control.params.options?split(optionSeparator) as nameValue>
						<#if nameValue?index_of(labelSeparator) == -1>
							<input type="radio" name="${field.name}" value="${nameValue?html}"
								<#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)> checked="checked"</#if>"
                                    <#if disabled>disabled="disabled"</#if>"/>
							<label class="checkbox">${nameValue?html}</label>
						<#else>
							<#assign choice=nameValue?split(labelSeparator)>
							<input type="radio" name="${field.name}" value="${choice[0]?html}"
                                <#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])> checked="checked"</#if>
                                   <#if disabled>disabled="disabled"</#if>"/>
							<label class="checkbox">${msgValue(choice[1])?html}</label>
						</#if>
					</#list>
				<#else>
					<div id="${fieldHtmlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
				</#if>
			</div>
		</div>
	</div>
</#if>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function () {

    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-eds-documents/selectone-radiobuttons.js'
        ], processController);
    }

    function processController() {
        new LogicECM.module.SelectOneRadioButtonsController("${fieldHtmlId}").setOptions({
            <#if field.control.params.fireChangeEventName??>
                fireChangeEventName: '${field.control.params.fireChangeEventName}',
            </#if>
            fieldName: "${field.name}",
            fieldId: "${field.configName}",
            formId: "${args.htmlid}"
        });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
</script>
