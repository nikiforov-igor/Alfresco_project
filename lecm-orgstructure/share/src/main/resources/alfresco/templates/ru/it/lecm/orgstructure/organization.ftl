<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/res/ru/it/lecm/utils/generate-custom-name.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="organization-profile" scope="template" />
</@bpage.basePage>
