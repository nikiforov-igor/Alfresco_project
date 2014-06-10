<#function declOfNum number title>
    <#assign cases = [ 2, 0, 1, 1, 1, 2 ]>
    <#if (number%100 > 4) && ((number%100) < 20)>
        <#local num = 2>
    <#elseif ((number%10) < 5)>
        <#local num = cases[number%10]>
    <#else>
        <#local num = cases[5]>
    </#if>
    <#return title[num]>
</#function>

<script type="text/javascript">
	(function() {
		LogicECM.module.Base.Util.loadCSS([
			'css/components/document-tasks-dashlet.css'
		]);
	})();
</script>

<div class="tasks-dashlet-content">
<#if data??>
    <div>
        <div>${msg("dashlet.my.tasks.assigned.count", "<a href='javascript:void(0);' onclick='documentTasksComponent.onExpand(\"tasksList\")'>"+  data.myTasksTotalCount +"</a>","${declOfNum(data.myTasksTotalCount?number, ['а','и',' '])}")}</div>

        <#if data.myLatestTask??>
            <div class="right1 task-link">
                <a href="${url.context}/page/task-edit?taskId=${data.myLatestTask.id}">${msg("dashlet.label.last.task")}</a>${data.myLatestTask.startDate}
            </div>
        </#if>
    </div>
</#if>

<#if myErrandsData??>
    <div class="errands-data">
        <div>${msg("dashlet.my.errands.assigned.count", "<a href='javascript:void(0);' onclick='documentTasksComponent.onExpand(\"myErrandsList\")'>"+myErrandsData.errandsCount+"</a>","${declOfNum(myErrandsData.errandsCount?number, ['е','я','й'])}")}</div>

        <#if myErrandsData.latestErrandNodeRef??>
            <div class="right1 task-link">
                <a href="${url.context}/page/document?nodeRef=${myErrandsData.latestErrandNodeRef}">${msg("dashlet.label.last.errand")}</a>${myErrandsData.latestErrandStartDate}
            </div>
        </#if>
    </div>
</#if>

<#if errandsIssuedByMeData??>
    <div class="issue-data">${msg("dashlet.my.errands.assigned.by.me.count","<a href='javascript:void(0);' onclick='documentTasksComponent.onExpand(\"errandsIssuedByMeList\")'>"+ errandsIssuedByMeData.errandsCount +"</a>","${declOfNum(errandsIssuedByMeData.errandsCount?number, ['е','я','й'])}")}</div>
</#if>
</div>
