<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="/res/modules/simple-dialog.js"></@script>

<@script type="text/javascript" src="/res/components/form/form.js" />
<@script type="text/javascript" src="/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<@script type="text/javascript" src="/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="/res/components/form/number-range.js"></@script>
<#include "/org/alfresco/components/form/form.get.head.ftl">
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/search/search.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-arm/lecm-arm.css" />
<#include "/org/alfresco/components/form/form.dependencies.inc">
</@templateHeader>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="datagrid" scope="template" />
</@bpage.basePage>