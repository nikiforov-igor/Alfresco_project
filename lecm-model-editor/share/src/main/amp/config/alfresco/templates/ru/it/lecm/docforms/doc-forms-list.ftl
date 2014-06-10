<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />


<@templateHeader "transitional">
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
	<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
	<#include "/org/alfresco/components/form/form.dependencies.inc">
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="datagrid" scope="template" />

	<@region id="html-upload" scope="template"/>
	<@region id="flash-upload" scope="template"/>
	<@region id="file-upload" scope="template"/>
	<@region id="dnd-upload" scope="template"/>
</@bpage.basePage>