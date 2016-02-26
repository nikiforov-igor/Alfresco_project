<#assign detailsViewId = args.htmlid + '-templatesDetailsViewContainer'/>

<div id='${detailsViewId}'>
	<div id='${detailsViewId}-details' class='templateDetails'></div>
</div>
<script type='text/javascript'>//<![CDATA[
	(function () {

		function initDocumentsTemplatesDetails() {
			new LogicECM.module.DocumentsTemplates.DetailsView('${detailsViewId}', {
				bubblingLabel: 'documentsTemplatesDetailsView'
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/documents-templates/details/documents-templates-details.js'
		],[
			'css/documents-templates/details/documents-templates-details.css'
		], initDocumentsTemplatesDetails);
	})();
//]]></script>
