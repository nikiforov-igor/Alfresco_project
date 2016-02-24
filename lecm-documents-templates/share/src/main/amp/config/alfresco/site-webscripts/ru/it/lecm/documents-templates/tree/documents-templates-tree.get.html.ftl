<#assign treeViewId = args.htmlid + '-templatesTreeViewContainer'/>

<div id='${treeViewId}'>
	<div id='${treeViewId}-tree'></div>
</div>
<script type='text/javascript'>//<![CDATA[
	(function () {

		function initDocumentsTemplatesTree() {
			new LogicECM.module.DocumentsTemplates.TreeView('${treeViewId}', {
				selectableType: '${args.selectableType}',
				xpath: '${args.xpath}'
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/documents-templates/tree/documents-templates-tree.js'
		],[
			'css/documents-templates/tree/documents-templates-tree.css'
		], initDocumentsTemplatesTree);
	})();
//]]></script>
