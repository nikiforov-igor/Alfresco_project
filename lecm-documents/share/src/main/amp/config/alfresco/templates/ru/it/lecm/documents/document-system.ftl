<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/page-document.css" />
	<@templateHtmlEditorAssets />
</@>

<@templateBody>
	<@markup id="alf-hd">
	<div id="alf-hd">
		<@region id="mobile-app" scope="template"/>
		<@region scope="global" id="share-header" chromeless="true"/>
	</div>
	</@>
	<@markup id="doc-bd">
	<div id="doc-bd" class="doc-page">
		<@region id="forbidden" scope="template"/>
	</div>
	</@>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global"/>
</div>
</@>
