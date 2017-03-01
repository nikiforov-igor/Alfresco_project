<#include "/org/alfresco/components/form/form.dependencies.inc">
<!-- Document Metadata Header -->
<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-metadata.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-component-base.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/document-details/document-metadata.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-metadata.js"></@script>
</@>

<@markup id="html">
	<#if document??>
	    <!-- Parameters and libs -->
	    <#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
	    <#assign el=args.htmlid/>
	
	<!-- Markup -->
	<div class="widget-bordered-panel">
	    <div class="document-metadata-header document-components-panel">
	        <h2 id="${el}-heading" class="dark">
	            ${msg("heading")}
	            <span class="alfresco-twister-actions">
	            <a id="${el}-link" href="javascript:void(0);" onclick="" class="expand metadata-expand" title="${msg("label.view")}">&nbsp;</a>
	         </span>
	        </h2>
	        <div id="${el}-formContainer"></div>
	    </div>
	</div>
	
	<!-- Javascript instance -->
	<script type="text/javascript">//<![CDATA[
	//TODO:
	var documentMetadataComponent = null;
	(function(){
	    var alfrescoDocumentMetadata = new Alfresco.DocumentMetadata("${el}").setOptions(
	            {
	                nodeRef: "${nodeRef}",
	                site: null,
	                formId: <#if formId??>"${formId?js_string}"<#else>null</#if>
	            }).setMessages(${messages});
	    documentMetadataComponent = new LogicECM.DocumentMetadata("${el}").setOptions(
	            {
	                nodeRef: "${nodeRef}",
	                title:"${msg('heading')}"
	            }).setMessages(${messages});

		// ALFFIVE-32
		// В новой версии DocumentMetadata зачем то есть подписка на metadataRefresh
		// что в свою очередь вызывает странное поведение модуля. Т.к в нашем функционале
		// нету завязки на такую логику - безопаснее будет вырубить её
		// TODO: Выяснить, точно ли нам не нужна такая логика

		YAHOO.Bubbling.unsubscribe("metadataRefresh", alfrescoDocumentMetadata.doRefresh, alfrescoDocumentMetadata);
	})();
	//]]></script>
	</#if>
</@>