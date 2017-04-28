<#assign typeText="мероприятие">
<#if type="lecm-meetings:document">
<#assign typeText="совещание">
</#if>
<#assign personal = (recipientMail?has_content && attendees[recipientMail]?has_content)>
<#if personal>
Уважаемый(ая) ${attendees[recipientMail]["name"]}!
<#else>
Уважаемые коллеги!
</#if>

${initiator!""} приглашает Вас на ${typeText} ${title!""}.

Время проведения: <#if allDay>${fromDate?date?string("dd.MM.yyyy")}<#else>с ${fromDate?datetime?string("dd.MM.yyyy HH:mm")} по ${toDate?datetime?string("dd.MM.yyyy HH:mm (z)")}</#if>
Место проведения: ${location!""}
Инициатор: ${initiator!""}
Участники: <#list attendees?keys as mail>${attendees[mail]["name"]}<#if mail_has_next>, </#if></#list>
<#if personal>Участие для Вас является<#if attendees[recipientMail]["mandatory"]>обязательным<#else>необязательным</#if></#if>