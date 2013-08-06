<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.module.FormatPackageItemsDocument("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				substituteString: "${field.control.params.substituteString!'{cm:name}'}"
			});
})();
//]]></script>

<div class="form-field">
	<div class="viewmode-field">
		<label for="${fieldHtmlId}-valueDisplay">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<span id="${fieldHtmlId}-valueDisplay" class="viewmode-value">

		</span>
		<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
	</div>
</div>