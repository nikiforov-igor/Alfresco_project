<#assign treeViewId = args.htmlid + '-templatesTreeViewContainer'/>

<div id='${treeViewId}'>
	<div id='${treeViewId}-tree' class='ygtv-highlight templateTree'></div>
</div>
<script type='text/javascript'>//<![CDATA[
	(function () {

		function initDocumentsTemplatesTree() {
			new LogicECM.module.DocumentsTemplates.TreeView('${treeViewId}', {
				bubblingLabel: 'documentsTemplatesTreeView'
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/documents-templates/tree/documents-templates-tree.js'
		],[
			'css/documents-templates/tree/documents-templates-tree.css'
		], initDocumentsTemplatesTree);
	})();
//]]></script>
