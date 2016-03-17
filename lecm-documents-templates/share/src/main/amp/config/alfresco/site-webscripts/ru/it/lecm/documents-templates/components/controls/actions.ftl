<div id='${fieldHtmlId}' class='doc-right-part'>
	<div id='${fieldHtmlId}-actions' class='widget-panel-grey'>
		<div id='${fieldHtmlId}-action-add' class='widget-button-grey text-cropped'>${msg('template-action-add.title')}</div>
		<div id='${fieldHtmlId}-action-submit' class='widget-button-grey text-cropped'>${msg('template-action-submit.title')}</div>
		<div id='${fieldHtmlId}-action-clear' class='widget-button-grey text-cropped'>${msg('template-action-clear.title')}</div>
	</div>
</div>
<script type='text/javascript'>//<![CDATA[
	(function () {

		function initDocumentsTemplatesActions() {
			new LogicECM.module.DocumentsTemplates.Actions('${fieldHtmlId}', {
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/documents-templates/components/controls/documents-templates-actions.js'
		],[
			'css/documents-templates/components/controls/documents-templates-actions.css'
		], initDocumentsTemplatesActions);
	})();
//]]></script>
