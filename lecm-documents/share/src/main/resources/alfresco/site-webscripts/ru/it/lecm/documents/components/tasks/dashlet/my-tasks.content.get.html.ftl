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

<div style="padding: 10px;">
<#if data??>
    <div>
        <div style="float:left;">${msg("dashlet.my.tasks.assigned.count", "<a href='javascript:void(0);' onclick='documentTasksComponent.onExpand(\"tasksList\")'>"+  data.myTasksTotalCount +"</a>","${declOfNum(data.myTasksTotalCount?number, ['а','и',' '])}")}</div>

        <#if data.myLatestTask??>
            <div style="float:right;">
                <a href="${url.context}/page/task-edit?taskId=${data.myLatestTask.id}"
                   style="padding-right: 30px;">${msg("dashlet.label.last.task")}</a>${data.myLatestTask.startDate}
            </div>
        </#if>
    </div>
</#if>

<#if myErrandsData??>
    <div style="clear: both; padding-top: 10px;">
        <div style="float:left;">${msg("dashlet.my.errands.assigned.count", "<a href='javascript:void(0);' onclick='documentTasksComponent.onExpand(\"myErrandsList\")'>"+myErrandsData.errandsCount+"</a>","${declOfNum(myErrandsData.errandsCount?number, ['е','я','й'])}")}</div>

        <#if myErrandsData.latestErrandNodeRef??>
            <div style="float:right;">
                <a href="${url.context}/page/document?nodeRef=${myErrandsData.latestErrandNodeRef}"
                   style="padding-right: 30px;">${msg("dashlet.label.last.errand")}</a>${myErrandsData.latestErrandStartDate}
            </div>
        </#if>
    </div>
</#if>

<#if errandsIssuedByMeData??>
    <div style="clear: both; padding-top: 10px;">${msg("dashlet.my.errands.assigned.by.me.count","<a href='javascript:void(0);' onclick='documentTasksComponent.onExpand(\"errandsIssuedByMeList\")'>"+ errandsIssuedByMeData.errandsCount +"</a>","${declOfNum(errandsIssuedByMeData.errandsCount?number, ['е','я','й'])}")}</div>
</#if>
</div>
