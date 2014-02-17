<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary-all.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
<#if isEngineer>
	<@region id="datagrid" scope="template" />
<#else>
    <@region id="forbidden" scope="template"/>
</#if>
</@bpage.basePage>