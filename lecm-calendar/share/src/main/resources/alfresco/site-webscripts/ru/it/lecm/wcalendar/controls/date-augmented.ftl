<#if field.control.params.errorContainerID??>
	<#assign errorContainerID="error-message-container-${field.control.params.errorContainerID?string}">
<#else>
	<#assign errorContainerID="error-message-container">
</#if>

<#if field.control.params.hideDateFormat?? && field.control.params.hideDateFormat == "true">
	<#assign hideDateFormat=true>
<#else>
	<#assign hideDateFormat=false>
</#if>

<#if field.control.params.showTime?? && field.control.params.showTime == "true">
	<#assign showTime=true>
<#else>
	<#assign showTime=false>
</#if>

<#if showTime>
	<#assign viewFormat>${msg("form.control.date-picker.view.time.format")}</#assign>
<#else>
	<#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>
</#if>

<#assign disabled=field.disabled>
<#if field.control.params.forceEditable?? && field.control.params.forceEditable == "true">
	<#assign disabled=false>
</#if>

<#assign multiValued=false>
<#if field.value != "" && field.value?index_of(",") != -1>
	<#assign multiValued=true>
</#if>

<#if form.capabilities?? && form.capabilities.javascript?? && form.capabilities.javascript == false><#assign jsDisabled=true><#else><#assign jsDisabled=false></#if>

<div class="form-field">
	<#if form.mode == "view">
		<div class="viewmode-field">
			<#if field.mandatory && field.value == "">
				<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<span class="viewmode-label">${field.label?html}:</span>
			<span class="viewmode-value">
			<#if field.value == "">
				${msg("form.control.novalue")}
			<#elseif !multiValued>
				${xmldate(field.value)?string(viewFormat)}
			<#else>
				<#list field.value?split(",") as dateEl>
					${xmldate(dateEl)?string(viewFormat)}<#if dateEl_has_next>,</#if>
				</#list>
			</#if>
			</span>
		</div>
	<#elseif !multiValued>
		<#if jsDisabled>
			<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
			<input id="${fieldHtmlId}" name="${field.name}" type="text" class="date-entry" value="${field.value?html}" <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"<#else>tabindex="0"</#if> <#if field.control.params.dateMaxWidth??>style="max-width: ${field.control.params.dateMaxWidth};"</#if> />
			<div class="format-info">
				<span class="date-format <#if hideDateFormat>hidden</#if>">${msg("form.control.date-picker.entry.datetime.format.nojs")}</span>
			</div>
		<#else>
			<#assign controlId = fieldHtmlId + "-cntrl">

			<script type="text/javascript">//<![CDATA[
			(function()
			{
				new Alfresco.DatePicker("${controlId}", "${fieldHtmlId}").setOptions(
				{
					<#if form.mode == "view" || disabled>disabled: true,</#if>
					currentValue: "${field.value?js_string}",
					showTime: ${showTime?string},
					mandatory: ${field.mandatory?string}
				}).setMessages(
					${messages}
				);
			})();
			//]]></script>

			<input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${field.value?html}" />

			<label for="${controlId}-date">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
			<input id="${controlId}-date" name="-" type="text" class="date-entry" <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"<#else>tabindex="0"</#if> <#if field.control.params.dateMaxWidth??>style="max-width: ${field.control.params.dateMaxWidth};"</#if> />

			<#if disabled == false>
				<a id="${controlId}-icon"><img src="${url.context}/res/components/form/images/calendar.png" class="datepicker-icon" tabindex="0"/></a>
			</#if>

			<div id="${controlId}" class="datepicker"></div>

			<#if showTime>
				<input id="${controlId}-time" name="-" type="text" class="time-entry" <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"<#else>tabindex="0"</#if> <#if field.control.params.dateMaxWidth??>style="max-width: ${field.control.params.dateMaxWidth};"</#if> />
			</#if>

			<@formLib.renderFieldHelp field=field />

			<div class="format-info">
				<span class="date-format <#if hideDateFormat>hidden</#if>">${msg("form.control.date-picker.display.date.format")}</span>
				<#if showTime><span class="time-format<#if disabled>-disabled</#if>">${msg("form.control.date-picker.display.time.format")}</span></#if>
				<div id="${errorContainerID}"></div>
			</div>
		</#if>
	</#if>
</div>
