<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<@documentLibraryJS />
	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
	<@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>
	<#include "/org/alfresco/components/documentlibrary/documentlist.get.head.ftl" />

	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/res/ru/it/lecm/utils/generate-custom-name.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="positions-grid" scope="template" />
</@bpage.basePage>
