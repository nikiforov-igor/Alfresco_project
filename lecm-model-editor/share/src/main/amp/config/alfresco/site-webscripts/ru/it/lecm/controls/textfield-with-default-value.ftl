<#assign hideValue = false>
<#if field.control.params.hideValue??>
	<#assign hideValue = true>
</#if>

<#assign mandatory = false>
<#if field.control.params.mandatory??>
	<#if field.control.params.mandatory == "true">
		<#assign mandatory = true>
	</#if>
<#elseif field.mandatory??>
	<#assign mandatory = field.mandatory>
</#if>

<#assign defaultValue = field.control.params.defaultValue!'def'>
<#assign defaultValueDataSource = field.control.params.defaultValueDataSource!''>
<#assign allowInNonCreateMode = (field.control.params.allowInNonCreateMode!'false') == 'true'>

<#if field.value?is_number>
	<#assign defaultValue1 =  field.value?c>
<#else>
	<#assign defaultValue1 =  field.value?html>
</#if>
<@script type='text/javascript' src='${url.context}/res/components/model-editor/controls/lecm-textfield-with-default-value.js' group='model-editor'/>
<script type="text/javascript">
	(function() {
		function createControl(obj) {
		var control = new LogicECM.module.TextfieldWithDefaultValue('${fieldHtmlId}').setMessages(${messages});
		control.setOptions(
				{
					
					defaultValue: obj.${defaultValue},
					defaultValueDataSource: '${defaultValueDataSource}',
					allowInNonCreateMode: true,
					mode: '${form.mode}',
					fieldId: '${fieldHtmlId}',
					disabled: ${field.disabled?string}
				});
		}
		LogicECM.module.ModelEditor.ModelPromise.then(createControl);
	})();
</script>

<#if form.mode == "view">
	<div class="control textfield viewmode">
		<div class="label-div">
			<#if mandatory && !(field.value?is_number) && field.value == "">
				<span class="incomplete-warning">
					<img src="${url.context}/res/components/form/images/warning-16.png" title="${msg('form.field.incomplete')}"/>
				<span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
					<#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
				<#else>
					<#if field.value?is_number>
						<#assign fieldValue=field.value?c>
					<#else>
						<#assign fieldValue=field.value?html>
					</#if>
				</#if>
				<!--<span><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>-->
				<input id="${fieldHtmlId}" style="width:600px" name="${field.name}" disabled="true"/>
			</div>
		</div>
	</div>
<#else>
	<div class="control textfield editmode">
		<div class="label-div">
			<label for="${fieldHtmlId}">${field.label?html}:
				<#if mandatory>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div class="container">
			<div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
			<div class="value-div">
				<input id="${fieldHtmlId}" name="${field.name}" tabindex="0"
					 <#if field.control.params.password??>type="password"<#else>type="text"</#if>
					 <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
					 <#if field.control.params.style??>style="${field.control.params.style}"</#if>
					 <#if !hideValue>
						<#if field.value?is_number>value="${field.value?c}"<#else>value="${field.value?html}"</#if>
					 </#if>
					 <#if field.description??>title="${field.description}"</#if>
					 <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
					 <#if field.control.params.size??>size="${field.control.params.size}"</#if>
					 <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />
			</div>
		</div>
	</div>
</#if>
<div class="clear"></div>
