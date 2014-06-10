<@script type="text/javascript" src="${url.context}/res/scripts/semantic-assist/document-experts.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/semantic-assist/document-tags.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/semantic-assist/documents-by-document.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/semantic-assist/documents-by-document-list.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/semantic-assist/tags-cloud.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/semantic-assist/semantic-dashlet.css" />

<#assign el=args.htmlid?js_string/>

<#if aspect??>

	<#if aspect.hasAspect == "true">
		<#--div, по которому можно узнать подключен модуль semantic assist или нет-->
		<div id="semantic-mudule-active-htmlid" type="hidden">
		<#if page.id == "document-details">
			<div class="document-details-panel">
			<#if aspect.hasTags == "true">
				<h2 id="${el}-heading" class="thin dark">${msg("heading")}</h2>
			<#else>
				<h2 id="${el}-heading" class="grey">${msg("heading")}</h2>
			</#if>
		<#else>
			<div class="document-components-panel">
			<#if aspect.hasTags == "true">
				<h2 id="${el}-heading" class="dark">${msg("heading")}</h2>
			<#else>
				<h2 id="${el}-heading" class="grey">${msg("heading")}</h2>
			</#if>
		</#if>

		<#if aspect.hasTags == "true">
			<div class="panel-body" style="display: block; padding-top:0;">
					<div class="link-info">
						<#if nodeRef??>
							<#if page.id == "document">
								<a  id = "cloud-term-ref" title="${msg('cloud.text')}" class="semantic-button-grey text-cropped" href="javascript:void(0)" >${msg('cloud.text')}</a>
							<#else>
								<a id = "cloud-term-ref" title="${msg('cloud.text')}" class="simple-link" target="_blank" href="cloud-theme?nodeRef=${nodeRef}" >${msg('cloud.text')}</a>
							</#if>
						<#else>
							<a title="${msg('cloud.text')}" class="simple-link" href="#" >${msg('cloud.text')}</a>
						</#if>
					</div>
					<div class="link-info">
						<#if nodeRef??>
							<#if page.id == "document">
								<a id = "experts-by-document-ref" title="${msg('experts.theme.text')}" class="semantic-button-grey text-cropped" href="javascript:void(0)" >${msg('experts.theme.text')}</a>
							<#else>
								<a title="${msg('experts.theme.text')}" class="simple-link" target="_blank" href="experts-by-document?nodeRef=${nodeRef}" >${msg('experts.theme.text')}</a>
							</#if>
						<#else>
							 <a title="${msg('experts.theme.text')}" class="simple-link" href="#" >${msg('experts.theme.text')}</a>
						</#if>
					</div>
					<div class="link-info">
						<#if nodeRef??>
							<#if page.id == "document">
								<a id = "documents-by-document-ref" title="${msg('similar.document.text')}" class="semantic-button-grey text-cropped" href="javascript:void(0)" >${msg('similar.document.text')}</a>
							<#else>
								<a title="${msg('similar.document.text')}" class="simple-link" target="_blank" href="documents-by-term?nodeRef=${nodeRef}&type=alfresco" >${msg('similar.document.text')}</a>
							</#if>
						<#else>
							<a title="${msg('similar.document.text')}" class="simple-link" href="#" >${msg('similar.document.text')}</a>
						</#if>
					</div>
			</div>
		</#if>
		<#if aspect.hasTags == "true">
			<script type="text/javascript">//<![CDATA[
				Alfresco.util.createTwister("${el}-heading", "DocumentMetadata");
			//]]></script>
		</#if>
		</div>
	</#if>
</#if>

  <script type="text/javascript">//<![CDATA[
	function init() {
		ExpertsByComponent =new LogicECM.DocumentExperts("${el}").setOptions(
				{
					nodeRef: "${nodeRef}",
					title: "${msg('title.experts.by.document')}",
				}).setMessages(${messages});
		DocumentsByDocumentComponent =new LogicECM.DocumentDocuments("${el}").setOptions(
				{
					nodeRef: "${nodeRef}",
					title: "${msg('title.documents.by.document')}",
				}).setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
    //]]></script>