<#assign el=args.htmlid?js_string/>

<#if aspect??>
	<#if aspect.hasAspect == "true">
		<#if page.id == "document-details">
			<div class="document-details-panel">
			<h2 id="${el}-heading" class="thin dark">${msg("heading")}</h2>
		<#else>
			<div class="document-components-panel">
			<h2 id="${el}-heading" class="dark">${msg("heading")}</h2>
		</#if>
			<div class="panel-body" style="display: block;">
					<div class="link-info">
						<#if nodeRef??>
							<a title="${msg('cloud.text')}" class="simple-link" href="cloud-theme?nodeRef=${nodeRef}" >${msg('cloud.text')}</a>
						<#else>
							<a title="${msg('cloud.text')}" class="simple-link" href="#" >${msg('cloud.text')}</a>
						</#if>
					</div>
					<div class="link-info">
					   <a title="${msg('experts.theme.text')}" class="simple-link" href="#" >${msg('experts.theme.text')}</a>
					</div>
					<div class="link-info">
					   <a title="${msg('similar.document.text')}" class="simple-link" href="#" >${msg('similar.document.text')}</a>
					</div>
			</div>

			<script type="text/javascript">//<![CDATA[
				Alfresco.util.createTwister("${el}-heading", "DocumentMetadata");
			//]]></script>
		</div>
	</#if>
</#if>