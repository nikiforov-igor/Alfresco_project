<#if form.mode == "view">
   <div class="control number viewmode">
	   <div class="label-div">
		   <#if field.mandatory && !(field.value?is_number) && field.value == "">
		   <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
		                                         title="${msg("form.field.incomplete")}"/><span>
		   </#if>
		   <label>${field.label?html}:</label>
	   </div>
	   <div class="container">
		   <div class="value-div">
			   <#if field.value?is_number>
			        ${field.value?c}
			   <#elseif field.value == "">
			        ${msg("form.control.novalue")}
			   <#else>
			        ${field.value?html}
			   </#if>
		   </div>
	   </div>
   </div>
<#else>
	<div class="control number editmode">
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
				<input id="${fieldHtmlId}" type="text" name="${field.name}" tabindex="0"
				       class="<#if field.control.params.styleClass??> ${field.control.params.styleClass}</#if>"
				       <#if field.control.params.style??>style="${field.control.params.style}"</#if>
				       <#if field.value?is_number>value="${field.value?c}"<#else>value="${field.value?html}"</#if>
				       <#if field.description??>title="${field.description}"</#if>
				       <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
				       <#if field.control.params.size??>size="${field.control.params.size}"</#if>
				       <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />
			</div>
		</div>
	</div>
</#if>
<div class="clear"></div>
