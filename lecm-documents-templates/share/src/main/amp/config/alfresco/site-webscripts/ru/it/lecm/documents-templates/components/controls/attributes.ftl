<#assign formId = args.htmlid + '-form'>

<div id='${fieldHtmlId}'>
	<input id='${fieldHtmlId}-value' type='hidden' name='${field.name}' value='${field.value?string}'>
	<div id='${fieldHtmlId}-datatable' class='attributes hidden'></div>
	<div id='${fieldHtmlId}-delete-template' class='hidden'>
		<a id='{id}-delete' class='delete' title='{title}'></a>
	</div>
	<div id='${fieldHtmlId}-attribute-template' class='hidden'>
		<select id='{id}-attribute' class='attribute'>{options}</select>
	</div>
	<div id='${fieldHtmlId}-value-template' class='hidden'>
		<div id='{id}-value' class='value'></div>
	</div>
</div>
<script type='text/javascript'>//<![CDATA[
	(function () {

		function initDocumentsTemplatesAttributes() {
			new LogicECM.module.DocumentsTemplates.Attributes('${fieldHtmlId}', {
				bubblingLabel: 'documentsTemplatesAttributes',
				formId: '${formId}'
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/documents-templates/components/controls/documents-templates-attributes.js'
		],[
			'css/documents-templates/components/controls/documents-templates-attributes.css'
		], initDocumentsTemplatesAttributes);
	})();
//]]></script>
