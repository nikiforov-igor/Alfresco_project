<#assign typeText="мероприятие">
<#if type="lecm-meetings:document">
<#assign typeText="совещание">
</#if>
<#assign personal = (recipientMail?has_content && attendees[recipientMail]?has_content)>
<#if personal>
Уважаемый(ая) ${attendees[recipientMail]["name"]}!
</#if>

Вами создано ${typeText} ${title!""}.

Время проведения: <#if allDay>${fromDate?date?string("dd.MM.yyyy")}<#else>с ${fromDate?datetime?string("dd.MM.yyyy HH:mm")} по ${toDate?datetime?string("dd.MM.yyyy HH:mm (z)")}</#if>
Место проведения: ${location!""}
Участники: <#list attendees?keys as mail>${attendees[mail]["name"]}<#if mail_has_next>, </#if></#list>

Примите данное приглашение для актуализации своего календаря.