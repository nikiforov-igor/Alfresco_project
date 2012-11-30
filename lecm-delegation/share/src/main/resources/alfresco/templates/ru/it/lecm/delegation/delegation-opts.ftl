<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"/>

<#-- Далее перечисляются самописные скрипты LogicECM  -->
<@script type="text/javascript" src="${page.url.context}/res/scripts/lecm-delegation/delegation-const.js"/>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePage>
	<@region id="delegation-opts" scope="template"/>
</@bpage.basePage>
