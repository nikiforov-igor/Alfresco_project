<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/res/components/workflow/workflow-actions.js"></@script>
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-user-profile/distribution-tasks.js"/>

<#assign el=args.htmlid?html>

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

<script type="text/javascript">//<![CDATA[
	new LogicECM.TaskDistribution("${el}").setMessages(${messages});
//]]></script>