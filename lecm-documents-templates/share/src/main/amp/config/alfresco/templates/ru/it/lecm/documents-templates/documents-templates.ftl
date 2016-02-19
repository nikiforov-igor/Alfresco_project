<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>

<@bpage.basePageSimple showToolbar=false>
	<@panels.twoPanels leftRegions=["tree-panel"] leftPanelId="tree-panel" rightPanelId="details-panel">
		<@region id="details-panel" scope="template" />
	</@>
</@>
