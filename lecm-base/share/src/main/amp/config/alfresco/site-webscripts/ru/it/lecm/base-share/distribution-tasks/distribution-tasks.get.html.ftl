<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadResources([
			'/components/workflow/workflow-actions.js',
			'/scripts/lecm-base/distribution-tasks/distribution-tasks.js'
		], [
			'/components/workflow/task-list.css'
		], createObject);
	}

	function createObject() {
		new LogicECM.TaskDistribution("${el}").setMessages(${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

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