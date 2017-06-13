<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "lecm-dnd-uploader-container.ftl">

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + aDateTime?iso_utc>
<#assign params = field.control.params/>

<div class="form-field dnd-uploader">
	<input id="${controlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
	<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>

	<#assign disabled = form.mode == "view">
	<#assign showUploadNewVersion = false/>
	<#if params.showUploadNewVersion?? && params.showUploadNewVersion == "true">
		<#assign showUploadNewVersion = true/>
	</#if>

	<script type="text/javascript">//<![CDATA[
	(function() {

		function init() {
            LogicECM.module.Base.Util.loadResources([
                'components/upload/dnd-upload.js',
		        'components/upload/html-upload.js',
		        'components/upload/file-upload.js',
		        'components/upload/flash-upload.js',
		        'scripts/lecm-base/components/lecm-uploader-initializer.js',
                'scripts/lecm-base/components/lecm-uploader-with-preview-control.js',
                'components/preview/web-preview.js',
                'components/preview/WebPreviewer.js',
                'js/flash/extMouseWheel.js',
                'components/preview/StrobeMediaPlayback.js',
                'components/preview/Video.js',
                'scripts/lecm-base/components/lecm-dnd-uploader.js',
                'components/preview/Audio.js',
		        'components/preview/Flash.js',
                'components/preview/Image.js',
				'extras/components/preview/ResizeSensor.js',
				'extras/components/preview/ElementQueries.js',
				'extras/components/preview/l10n.js',
                'extras/components/preview/viewer.js',
                'scripts/components/preview/ComplexAttachment.js',
		        'extras/components/preview/web-preview-extend.js',
		        'extras/components/preview/PdfJs.js',
		        'extras/components/preview/Embed.js',
		        'extras/components/preview/pdfjs/compatibility.js',
		        'extras/components/preview/pdfjs/pdf.js',
		        'extras/components/preview/spin.js'
		    ], [
                'extras/components/preview/viewer.css',
	            'css/lecm-base/components/lecm-uploader-with-preview-control.css'
            ], createUploader);
	    }
		
		function createUploader() {
            loadExternalResourceBundle();
			var control = new LogicECM.control.UploaderWithPreviw("${controlId}").setMessages(${messages});
			control.setOptions(
					{
						uploadDirectoryPath: "${params.uploadDirectoryPath}",
						disabled: ${disabled?string},
						multipleMode: ${field.endpointMany?string},
						showUploadNewVersion: ${showUploadNewVersion?string},
						directoryName: "${msg(params.directoryNameCode)}",
						currentValue: "${field.value!""}"
					});
		}

        function loadExternalResourceBundle() {
            var resourceRef = document.createElement('link');
            resourceRef.setAttribute('rel', 'resource');
            resourceRef.setAttribute('type', 'application/l10n');
            resourceRef.setAttribute('href', Alfresco.constants.URL_RESCONTEXT + 'extras/components/preview/locale/locale.properties');
            document.getElementsByTagName("head")[0].appendChild(resourceRef);
        }

                YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>

	<#if !disabled>
		<div id="${controlId}-uploader-block" class="big-uploader-block">
			<fieldset>
				<legend>${msg("label.add-file")}</legend>
				<img id="${controlId}-uploader-button" src="${url.context}/res/images/lecm-base/components/plus-big.png" alt="" class="uploader-button">  <br/>
				<span class="drag-tip">${msg("label.drag-file")}</span>
			</fieldset>
		</div>
	</#if>
	<#if !disabled>
		<div id="${controlId}-uploader-remove" class="uploader-remove-button">
			<a href="javascript:void(0);" id="${controlId}-uploader-remove-link">
			${msg("label.file-assoc.delete")}<img src="${url.context}/res/components/images/delete-16.png"/>
			</a>
		</div>
	</#if>
	<div id="${controlId}-uploader-preview-container" class="body"></div>
</div>