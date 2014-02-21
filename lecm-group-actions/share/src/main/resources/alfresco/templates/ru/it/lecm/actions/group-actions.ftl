<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-base/components/lecm-datagrid.js"/>
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/number-range.js"></@script>
<@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>
<#include "/org/alfresco/components/form/form.get.head.ftl">
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/search/search.css" />
</@templateHeader>


<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="datagrid" scope="template" />
</@bpage.basePage>