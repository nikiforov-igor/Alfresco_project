<#macro renderDndUploaderContainerHTML fieldHtmlId uploadDirectoryPath directoryName disabled=false multiple=true>
	<style type="text/css">
		.dndDocListHighlight
		{
			outline: 2px solid #4F94C9;
			outline-offset: -2px;
		}
	</style>

	<script type="text/javascript">//<![CDATA[
	(function()
	{
		var control = new LogicECM.control.DndUploader("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
				{
					uploadDirectoryPath: "${uploadDirectoryPath}",
					disabled: ${disabled?string},
					multipleMode: ${multiple?string},
					directoryName: "${directoryName}"
				});
	})();
	//]]></script>

	<div id="${fieldHtmlId}-uploader-block" style="width: 250px; border: 1px solid; margin-left: 450px; height: 100px; text-align: center;">
		${msg("label.add-file")}
		<img id="${fieldHtmlId}-uploader-button" src="/share/res/components/images/add-icon-16.png" alt="" style="cursor: pointer">
	</div>
</#macro>