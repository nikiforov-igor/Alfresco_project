<#assign controlId = fieldHtmlId + "-cntrl">


<script type="text/javascript">
(function() {
	function onButtonClick(){
		var certBox = document.getElementById('${fieldHtmlId}');
		try {
			certBox.value = signApplet.getService().getCertFromFileUI();
		} catch(ex) {
			console.log(ex);
			return;
		}
	}

	YAHOO.util.Event.onAvailable("${fieldHtmlId}-cert-button", function () {
		var loadCertButton = new YAHOO.widget.Button("${fieldHtmlId}-cert-button");
		loadCertButton.on("click", onButtonClick);
	}, this);
})();

</script>

<div id="${controlId}" class="form-field">
	<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<textarea id="${fieldHtmlId}" name="${field.name}" rows="14" tabindex="0"
		<#if field.description??>title="${field.description}"</#if>
		<#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
		<#if field.control.params.style??>style="${field.control.params.style}"</#if>
		<#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${field.value?html}</textarea>

	<@formLib.renderFieldHelp field=field />
</div>
<button type="button" id="${fieldHtmlId}-cert-button" name="${fieldHtmlId}-cert-button" value="${msg('lecm.signdoc.lbl.load.from.file')}">${msg('lecm.signdoc.lbl.load.from.file')}</button>
