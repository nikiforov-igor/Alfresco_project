<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />


<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.dependencies.inc">
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=isAdmin showMenu=isAdmin>
	<#if isAdmin>
		<@region id="datagrid" scope="template" />

		<@region id="html-upload" scope="template"/>
		<@region id="flash-upload" scope="template"/>
		<@region id="file-upload" scope="template"/>
		<@region id="dnd-upload" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
