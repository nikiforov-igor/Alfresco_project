<#assign el=args.htmlid?html>

<div id="${el}-body"  class="task-list">
	<div class="yui-g task-list-bar flat-button">
		<div class="yui-u first">
			<h2 id="${el}-filterTitle" class="thin">
				${msg("title.message.my-tasks")}
			</h2>
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