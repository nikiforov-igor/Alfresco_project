<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<script type="text/javascript">//<![CDATA[

    LogicECM.module = LogicECM.module || {};
    LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};
    LogicECM.module.BusinessJournal.ARCHIVER_SETTINGS_REF = LogicECM.module.BusinessJournal.ARCHIVER_SETTINGS_REF || {};

    LogicECM.module.BusinessJournal.IS_ENGINEER = ${isEngineer?string};
    //]]>
</script>
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#assign hasPermission = isEngineer/>
<@bpage.basePageSimple>
	<#if hasPermission>
		<@region id="business-journal-settings" scope="template" />
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
