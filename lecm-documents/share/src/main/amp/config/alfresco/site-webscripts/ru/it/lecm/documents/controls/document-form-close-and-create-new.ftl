<#assign isTrue=false>
<#if field.control.params.defaultValue?? && field.control.params.defaultValue == "true">
	<#assign isTrue=true>
</#if>

<div class="control checkbox-control editmode">
	<div class="label-div">
		<label>&nbsp;</label>
	</div>
	<div class="container">
		<div class="buttons-div">
			<@formLib.renderFieldHelp field=field />
		</div>
		<div class="value-div">
			<input class="formsCheckBox" id="document-form-close-and-create-new" type="checkbox" tabindex="0" name="-" <#if field.description??>title="${field.description}"</#if>
				<#if isTrue>checked="checked"</#if>
				   <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>
				   <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
				   <#if field.control.params.style??>style="${field.control.params.style}"</#if> />
			<label for="document-form-close-and-create-new" class="checkbox">${field.label?html}</label>
		</div>
	</div>
</div>
