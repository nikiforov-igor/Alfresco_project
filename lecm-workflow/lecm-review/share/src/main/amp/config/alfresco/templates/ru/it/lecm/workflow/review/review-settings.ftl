<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
	<#if allowEdit>
		<@region id="review-settings" scope="template"/>
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>