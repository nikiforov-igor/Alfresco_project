<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-arm/lecm-arm.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-arm/lecm-arm-menu.css" />
	<#include "/org/alfresco/components/form/form.dependencies.inc">
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
    <@region id="datagrid" scope="template" />
</@bpage.basePage>