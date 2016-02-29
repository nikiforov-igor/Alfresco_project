<div id='${fieldHtmlId}'>
</div>
<script type='text/javascript'>//<![CDATA[
	(function () {

		function initDocumentsTemplatesAttributes() {
			new LogicECM.module.DocumentsTemplates.Attributes('${fieldHtmlId}', {
				bubblingLabel: 'documentsTemplatesAttributes'
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/documents-templates/components/controls/documents-templates-attributes.js'
		],[
			'css/documents-templates/components/controls/documents-templates-attributes.css'
		], initDocumentsTemplatesAttributes);
	})();
//]]></script>
