<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-user-profile/distribution-tasks-toolbar.js" group="distribution-tasks-tollbar"/>
</@>

<@markup id="widgets">
	<@createWidgets group="distribution-tasks-tollbar"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

		<@comp.baseToolbar el true false false>
			<div id="${el}-btnReassignAllTasks"></div>
		</@comp.baseToolbar>
	</@>
</@>
