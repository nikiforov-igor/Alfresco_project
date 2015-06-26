<#assign typeText="мероприятие">
<#if type="lecm-meetings:document">
<#assign typeText="совещание">
</#if>
${initiator} приглашает на ${link!""}. Начало: ${fromDate?date?string("dd.MM.yyyy")}, в ${fromDate?time?string("HH:mm")}
