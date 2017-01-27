<#assign id = args.htmlid>

<#if myTasks??>
	<div class="dashlet contracts-tasks bordered">
		<div class="title dashlet-title">
			<span>${msg("label.title")}</span>
		</div>
		<div class="body scrollableList dashlet-body" id="${id}_results">
			<#if myTasks?size == 0>
				<div class="no-tasks">
					<div class="img">
						<img src="${url.context}/res/images/lecm-contracts/alf_dashlet-32_tasks.png" />
					</div>
					<div>
						<h3>${msg("empty.title")}</h3>
						${msg("empty.description")}
					</div>
				</div>
			<#else>
				<#list myTasks as task>
					<div class="my-task">
						<div class="workflow-date">${task.startDate}</div>
						<div class="workflow-task-status ${task.type}">${task.typeMessage}</div>
						<div class="clear"></div>
						<div class="workflow-task-main-text">
							<span class="workflow-task-title">
								<a href="${url.context}/page/task-edit?taskId=${task.id}">${task.title}:</a>
							</span>&nbsp;${task.documentPresentString!""}
						</div>
					</div>
				</#list>
			</#if>
		</div>
	</div>

	<script type="text/javascript">
	(function() {
		LogicECM.module.Base.Util.loadCSS(['css/lecm-contracts/contracts-tasks.css']);
	})();
	</script>
</#if>
