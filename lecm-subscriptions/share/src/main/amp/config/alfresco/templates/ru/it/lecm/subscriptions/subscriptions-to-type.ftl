<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#assign hasPermission = isEngineer/>
<@bpage.basePageSimple>
	<#if hasPermission>
		<@region id="type-grid" scope="template" />
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
