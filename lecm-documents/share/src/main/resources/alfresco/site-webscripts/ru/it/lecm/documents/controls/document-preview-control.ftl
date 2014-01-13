<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign params = field.control.params/>

<div class="form-field document-preview-cntrol">
	<script type="text/javascript">//<![CDATA[
	(function() {
		var control = new LogicECM.module.Documents.DocumentPreviewControl("${fieldHtmlId}").setMessages(${messages});
		control.setOptions({
			taskId: "${form.arguments.itemId}"
		});
	})();
	//]]></script>

	<div class="preview-select">
		<select id="${fieldHtmlId}-attachment-select"></select>
	</div>
	<div id="${fieldHtmlId}-preview-container"></div>

	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
</div>