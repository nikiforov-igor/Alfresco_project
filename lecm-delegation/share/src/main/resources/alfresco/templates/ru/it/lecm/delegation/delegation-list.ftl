<#include "/org/alfresco/include/alfresco-template.ftl"/>

<@templateHeader>
<script type="text/javascript">//<![CDATA[

var delegationContainer = ${delegationContainer};

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER = LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER || delegationContainer;
//]]>
</script>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.get.head.ftl">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"/>

<#-- Далее перечисляются самописные скрипты LogicECM  -->

</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="delegation-list-grid" scope="template"/>
</@bpage.basePage>
