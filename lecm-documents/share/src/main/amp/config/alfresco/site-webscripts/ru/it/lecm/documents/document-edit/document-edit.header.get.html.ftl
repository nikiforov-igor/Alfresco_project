<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-header.css" />

<div class="document-header">
    <div class="status-banner">
        ${msg(accessMsg)}
	    <#if !hasPerm>
		    <script type="text/javascript">//<![CDATA[
		        window.location = Alfresco.util.siteURL("${viewUrl}?nodeRef=${nodeRef}");
		    //]]></script>
	    </#if>
    </div>
</div>
