<#assign fieldValue=field.value!"">

<#if fieldValue?string != "">
	<div class="form-field">
		<div class="viewmode-field">
			<span class="viewmode-label">${field.label?html}:</span>
			<span class="viewmode-value">
				<a href="${url.context}/page/document-attachment?nodeRef=${fieldValue}" target="_blank">
					<img src="${url.context}/components/images/filetypes/img-file-16.png" width="16"/>Просмотреть файл
				</a>
			</span>
		</div>
	</div>
</#if>
