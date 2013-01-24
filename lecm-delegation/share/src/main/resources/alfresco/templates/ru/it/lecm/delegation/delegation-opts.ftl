<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"/>

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<#-- Стили меню для страницы delegation-opts -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/css/lecm-delegation/delegation-menu.css"/>
<#-- Data Grid stylesheet -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/datagrid.css"/>

<#-- скрипты меню для страницы delegation-list -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/opts/delegation-opts-menu.js"/>
<#-- Data Grid javascript-->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<#-- Advanced search -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-base/components/advsearch.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/opts/procuracy-grid.js"/>
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/opts/delegation-opts.js"/>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage>
	<@region id="content" scope="template"/>
</@bpage.basePage>
