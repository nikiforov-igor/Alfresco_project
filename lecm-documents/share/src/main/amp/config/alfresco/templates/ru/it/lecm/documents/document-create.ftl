<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<#-- подключить все скрипты необходимые для диалоговых форм -->
	<#include "/org/alfresco/components/form/form.dependencies.inc">

	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage>
	<@region id="content" scope="template"/>
</@bpage.basePage>
