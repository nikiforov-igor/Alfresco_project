<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/components/workflow/workflow-actions.js" group="distribution-tasks"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-user-profile/distribution-tasks.js" group="distribution-tasks"/>
</@>

<@markup id="widgets">
	<@createWidgets group="distribution-tasks"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div id="${el}-body"  class="task-list">
			<div class="yui-g task-list-bar flat-button">
				<div class="yui-u first">
					<span class="thin">
						<input type="checkbox" id="${el}-select-all-tasks"/>
					</span>
				</div>
				<div class="yui-u">
					<div id="${el}-paginator" class="paginator">&nbsp;</div>
				</div>
			</div>
			<div id="${el}-tasks" class="tasks"></div>
		</div>
	</@>
</@>