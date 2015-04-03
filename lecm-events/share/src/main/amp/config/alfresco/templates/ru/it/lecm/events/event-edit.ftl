<#include "/org/alfresco/include/alfresco-template.ftl"/>
<#include "/ru/it/lecm/base-share/components/view.lib.ftl"/>

<@templateHeader>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>

    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-regnum-uniqueness-validator.js"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/events-constraints.js"/>

<#-- подключить все скрипты необходимые для диалоговых форм -->
    <#include "/org/alfresco/components/form/form.dependencies.inc">
</@>

<@viewForm />
<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePage showHeader=true showTitle=true showToolbar=false showMenu=false>
		<#if hasPermission>
    <@region id="content" scope="template"/>
<#else>
    <@region id="not-permission-header" scope="template"/>
</#if>
	</@bpage.basePage>
</div>