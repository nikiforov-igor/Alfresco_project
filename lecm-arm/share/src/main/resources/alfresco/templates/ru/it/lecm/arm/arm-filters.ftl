<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-arm/lecm-arm.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-arm/lecm-arm-menu.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-dictionary/dictionary-toolbar.css" />
	<#include "/org/alfresco/components/form/form.get.head.ftl">
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
    <@region id="datagrid" scope="template" />
</@bpage.basePage>