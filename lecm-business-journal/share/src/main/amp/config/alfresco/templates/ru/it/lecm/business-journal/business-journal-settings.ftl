<#include "/org/alfresco/include/alfresco-template.ftl" />
<script type="text/javascript">//<![CDATA[
    var bjSettings = ${bjSettings};

    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.BusinessJournal = LogicECM.module.BusinessJournal || {};
    LogicECM.module.BusinessJournal.ARCHIVER_SETTINGS_REF = LogicECM.module.BusinessJournal.ARCHIVER_SETTINGS_REF || bjSettings.nodeRef;

    LogicECM.module.BusinessJournal.IS_ENGINEER = ${isEngineer?string};
    //]]>
</script>
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#assign hasPermission = isEngineer/>
<@bpage.basePageSimple>
	<#if hasPermission>
		<@region id="business-journal-settings" scope="template" />
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
