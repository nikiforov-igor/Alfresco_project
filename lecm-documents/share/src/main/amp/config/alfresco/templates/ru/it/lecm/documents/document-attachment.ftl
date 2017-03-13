<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
	<@script type="text/javascript" src="${url.context}/res/components/documentlibrary/actions.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-details-panel.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/page-document.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-details-attachment.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/document-components-panel.css" />
	<@templateHtmlEditorAssets />
	<#if dependencies??>
		<#if dependencies.css??>
			<#list dependencies.css as cssFile>
				<@link rel="stylesheet" type="text/css" href="${url.context}/res/${cssFile}" group="documentlibrary"/>
			</#list>
		</#if>
		<#if dependencies.js??>
			<#list dependencies.js as jsFile>
				<@script type="text/javascript" src="${url.context}/res/${jsFile}" group="documentlibrary"/>
			</#list>
		</#if>
	</#if>
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
		<@region id="actions-common" scope="template"/>
		<@region id="actions" scope="template"/>
		<@region id="document-attachment-header" scope="template"/>
		<#if hasPermission>
		<div class="yui-gc">
			<div id="doc-attach-right" class="bordered-panel doc-right-part">
				<@region id="document-actions" scope="template"/>
				<@region id="document-signing" scope="template"/>
				<@region id="document-tags" scope="template"/>
				<@region id="document-metadata" scope="template"/>
				<@region id="document-versions" scope="template"/>
				<@region id="document-attachment-dnd" scope="template"/>
			</div>
			<div class="main-content preview">
				<#if (config.scoped['DocumentDetails']['document-details'].getChildValue('display-web-preview') == "true")>
					<@region id="web-preview" scope="template"/>
				</#if>
				<@region id="comments" scope="template"/>
			</div>
		</div>
		</#if>
		<@region id="html-upload" scope="template"/>
		<@region id="flash-upload" scope="template"/>
		<@region id="file-upload" scope="template"/>
		<@region id="dnd-upload" scope="template"/>
		<@region id="lecm-dnd-upload" scope="template"/>
	</div>
	</@>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global"/>
</div>
</@>
