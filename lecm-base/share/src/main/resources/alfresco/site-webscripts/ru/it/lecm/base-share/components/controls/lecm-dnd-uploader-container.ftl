<#macro renderDndUploaderContainerHTML fieldHtmlId field form>
	<#assign params = field.control.params/>
	<#assign disabled = form.mode == "view">
	<#assign autoSubmit = false/>
	<#if params.autoSubmit?? && params.autoSubmit == "true">
		<#assign autoSubmit = true/>
	</#if>

	<script type="text/javascript">//<![CDATA[
	(function() {
		var control = new LogicECM.control.DndUploader("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
				{
					uploadDirectoryPath: "${params.uploadDirectoryPath}",
					disabled: ${disabled?string},
					multipleMode: ${field.endpointMany?string},
					autoSubmit: ${autoSubmit?string},
					directoryName: "${msg(params.directoryNameCode)}",
					currentValue: "${field.value!""}"
				});
	})();
	//]]></script>

    <div id="${fieldHtmlId}-uploader-block" class="uploader-block">
        <fieldset>
            <legend>${msg("label.add-file")}</legend>
            <img id="${fieldHtmlId}-uploader-button" src="/share/res/images/lecm-base/components/plus.png" alt="" class="uploader-button">  <br/>
            <span class="drag-tip">${msg("label.drag-file")}</span>
        </fieldset>
    </div>
</#macro>