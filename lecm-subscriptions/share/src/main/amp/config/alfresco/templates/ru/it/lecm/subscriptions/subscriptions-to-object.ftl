<script type="text/javascript">//<![CDATA[
    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.Subscriptions = LogicECM.module.Subscriptions || {};
    (function () {
        //TODO:
        LogicECM.module.Subscriptions.IS_ENGINEER = ${isEngineer?string};
    })();
//]]>
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple>
	<#assign hasPermission = isEngineer/>
		<#if hasPermission>
			<@region id="to-object-grid" scope="template" />
		<#else>
			<@region id="forbidden" scope="template"/>
		</#if>
</@bpage.basePageSimple>
