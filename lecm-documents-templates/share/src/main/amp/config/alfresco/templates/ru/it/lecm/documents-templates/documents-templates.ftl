<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>

<#assign hasPermission = isEngineer/>
<@bpage.basePageSimple showToolbar=false>
	<#if hasPermission>
		<@panels.twoPanels initialWidth="300" leftRegions=["tree-panel"] leftPanelId="tree-panel" rightPanelId="details-panel">
			<@region id="details-panel" scope="template" />
		</@>
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@>
