<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />


<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.get.head.ftl">
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="datagrid" scope="template" />
</@bpage.basePage>