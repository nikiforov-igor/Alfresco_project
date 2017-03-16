<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@bpage.templateHeader />


<@bpage.basePage showToolbar=isAdmin showMenu=isAdmin>
	<#if isAdmin>
		<@region id="datagrid" scope="template" />
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
