<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<@templateHeader "transitional">
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
	<#if page.url.args.tag??>
		<p>${page.url.args.tag}</p>
	</#if>

</@bpage.basePage>