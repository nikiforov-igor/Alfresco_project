<#macro renderDndUploaderContainerHTML fieldHtmlId uploadDirectoryPath directoryName disabled=false multiple=true autoSubmit=false>
	<script type="text/javascript">//<![CDATA[
	(function() {
		var control = new LogicECM.control.DndUploader("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
				{
					uploadDirectoryPath: "${uploadDirectoryPath}",
					disabled: ${disabled?string},
					multipleMode: ${multiple?string},
					autoSubmit: ${autoSubmit?string},
					directoryName: "${directoryName}"
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