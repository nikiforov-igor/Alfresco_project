<#assign nodeRef = form.arguments.itemId!"">
<#assign text = field.control.params.text!"">

<#if nodeRef?string != "" && text?string != "">
	<div class="form-field">
		<div class="viewmode-field">
			<span class="viewmode-value">
				<a href="${url.context}/page/document?nodeRef=${nodeRef}" target="_blank">${text?string}</a>
			</span>
		</div>
	</div>
</#if>
