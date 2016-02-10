<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple showToolbar=isEngineer>
<#-- <#if isEngineer> -->
	<@region id="datagrid" scope="template" />
<#--
<#else>
    <@region id="forbidden" scope="template"/>
</#if>
-->
</@bpage.basePageSimple>
