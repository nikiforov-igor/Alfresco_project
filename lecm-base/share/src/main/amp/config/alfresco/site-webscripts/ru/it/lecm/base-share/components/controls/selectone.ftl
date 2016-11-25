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

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
   <#if context.properties[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
   <#elseif args[field.control.params.defaultValueContextProperty]??>
      <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
   </#if>
</#if>

<#assign disabled = field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>

<#assign hidden = false>
<#if field.control.params.hidden?? && field.control.params.hidden == "true">
    <#assign hidden = true>
</#if>

<#if form.mode == "view">
	<div id="${fieldHtmlId}-parent" class="control selectone viewmode<#if hidden> hidden1</#if>">
		<div class="label-div">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
			                                      title="${msg("form.field.incomplete")}"/><span>
			</#if>
			<label>${field.label?html}:</label>
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
	<div id="${fieldHtmlId}-parent" class="control selectone editmode<#if hidden> hidden1</#if>">
		<div class="label-div">
			<label for="${fieldHtmlId}">${field.label?html}:
				<#if field.mandatory>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div class="container">
            <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
            <div class="value-div">
				<#if field.control.params.options?? && field.control.params.options != "">
					<select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
					        <#if field.description??>title="${field.description}"</#if>
					        <#if field.control.params.size??>size="${field.control.params.size}"</#if>
					        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
					        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
					        <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
						<#list field.control.params.options?split(optionSeparator) as nameValue>
							<#if nameValue?index_of(labelSeparator) == -1>
								<option value="${nameValue?html}"<#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)> selected="selected"</#if>>${nameValue?html}</option>
							<#else>
								<#assign choice=nameValue?split(labelSeparator)>
								<option value="${choice[0]?html}"<#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])> selected="selected"</#if>>${msgValue(choice[1])?html}</option>
							</#if>
						</#list>
					</select>
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
            'scripts/lecm-base/components/selectone-controller.js'
        ], processController);
    }

    function processController() {
        new LogicECM.module.SelectOneController("${fieldHtmlId}").setOptions({
        <#if field.configName??>
            fieldId: "${field.configName}",
        </#if>
        <#if args.htmlid??>
            formId: "${args.htmlid}",
        </#if>
        <#if field.control.params.fireChangeEventName??>
            fireChangeEventName: '${ield.control.params.fireChangeEventName}',
        </#if>
            disabled: ${disabled?string},
        });
    }

    YAHOO.util.Event.onDOMReady(init);
  })();
</script>
