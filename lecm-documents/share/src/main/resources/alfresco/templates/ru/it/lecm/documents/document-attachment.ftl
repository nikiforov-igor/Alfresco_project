<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/document-details/document-details-panel.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/page-document.css" />
	<@templateHtmlEditorAssets />
</@>

<@templateBody>
<div id="alf-hd">
	<@region id="header" scope="global"/>
	<@region id="title" scope="template"/>
</div>
<div id="doc-bd">
	<@region id="actions-common" scope="template"/>
	<@region id="actions" scope="template"/>
	<@region id="document-attachment-header" scope="template"/>
	<div class="yui-gc">
		<div class="yui-u first">
			<#if (config.scoped['DocumentDetails']['document-details'].getChildValue('display-web-preview') == "true")>
				<@region id="web-preview" scope="template"/>
			</#if>
            <@region id="comments" scope="template"/>
		</div>
		<div class="yui-u bordered">
			<@region id="document-actions" scope="template"/>
            <@region id="document-tags" scope="template"/>
            <@region id="document-metadata" scope="template"/>
            <@region id="document-versions" scope="template"/>
		</div>
	</div>

	<@region id="html-upload" scope="template"/>
	<@region id="flash-upload" scope="template"/>
	<@region id="file-upload" scope="template"/>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global"/>
</div>
</@>
