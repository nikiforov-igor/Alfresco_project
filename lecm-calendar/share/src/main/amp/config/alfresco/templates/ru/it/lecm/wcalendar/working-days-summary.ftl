<#include "/org/alfresco/include/alfresco-template.ftl"/>
<#include "/org/alfresco/components/form/form.dependencies.inc">

<script type="text/javascript">//<![CDATA[

var roles = ${roles};

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};

LogicECM.module.WCalendar.Const.ROLES = LogicECM.module.WCalendar.Const.ROLES || roles;

//]]></script>

<#assign showContent = isEngineer || isBoss>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
	<#if showContent>
		<@region id="working-days-summary-table" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
