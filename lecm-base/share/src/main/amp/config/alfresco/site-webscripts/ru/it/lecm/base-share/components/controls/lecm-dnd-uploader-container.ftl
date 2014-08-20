<#macro renderDndUploaderContainerHTML fieldHtmlId field form suppressRefreshEvent="false" showPreview="true">
	<#assign params = field.control.params/>
	<#assign disabled = form.mode == "view">

	<#assign autoSubmit = false/>
	<#if params.autoSubmit?? && params.autoSubmit == "true">
		<#assign autoSubmit = true/>
	</#if>

	<#assign showUploadNewVersion = false/>
	<#if params.showUploadNewVersion?? && params.showUploadNewVersion == "true">
		<#assign showUploadNewVersion = true/>
	</#if>

	<#assign checkRights = false/>
	<#if params.checkRights?? && params.checkRights == "true">
		<#assign checkRights = true/>
	</#if>

	<script type="text/javascript">//<![CDATA[
	(function() {
		function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/lecm-dnd-uploader.js',
                'scripts/lecm-base/components/lecm-uploader-initializer.js',
                'scripts/lecm-base/components/lecm-dnd-uploader-control.js'
			], createDndUploader);
		}
		function createDndUploader() {
			var control = new LogicECM.control.DndUploader("${fieldHtmlId}").setMessages(${messages});
			control.setOptions(
			{
				uploadDirectoryPath: "${params.uploadDirectoryPath}",
				disabled: ${disabled?string},
				multipleMode: ${field.endpointMany?string},
				autoSubmit: ${autoSubmit?string},
				showUploadNewVersion: ${showUploadNewVersion?string},
				directoryName: "${msg(params.directoryNameCode)}",
				checkRights: ${checkRights?string},
				itemNodeRef: "${form.arguments.itemId}",
				currentValue: "${field.value!""}",
            	suppressRefreshEvent: ${suppressRefreshEvent?string},
				<#if params.defaultSelectedShowPreviewButton??>
					defaultSelectedShowPreviewButton: ${params.defaultSelectedShowPreviewButton?string},
				</#if>
            	showPreview: ${showPreview?string}
			});
		}
		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

	<#if !disabled>
	    <div id="${fieldHtmlId}-uploader-block" class="uploader-block <#if checkRights>hidden</#if>">
	        <fieldset>
	            <legend>${msg("label.add-file")}</legend>
	            <img id="${fieldHtmlId}-uploader-button" src="/share/res/images/lecm-base/components/plus.png" alt="" class="uploader-button">  <br/>
	            <span class="drag-tip">${msg("label.drag-file")}</span>
	        </fieldset>
	    </div>
	</#if>
</#macro>