<#assign typeText="мероприятию">
<#if type="lecm-meetings:document">
<#assign typeText="совещанию">
</#if>
${initiator} обновил информацию о ${link!""}. Начало: ${fromDate?date?string("dd.MM.yyyy")}, в ${fromDate?time?string("HH:mm")}