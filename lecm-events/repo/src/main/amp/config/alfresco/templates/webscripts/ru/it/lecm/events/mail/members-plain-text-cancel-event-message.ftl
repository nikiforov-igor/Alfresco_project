<#assign typeText="мероприяти">
<#if type="lecm-meetings:document">
<#assign typeText="совещани">
</#if>
<#assign personal = (recipientMail?has_content && attendees[recipientMail]?has_content)>
<#if personal>
Уважаемый(ая) ${attendees[recipientMail]["name"]}!
<#else>
Уважаемые коллеги!
</#if>
${typeText?capitalize}е ${title!""} отменено, либо Вы больше не являетесь его участником.

Информация об отмененном ${typeText}и:
Время проведения: <#if allDay>${fromDate?date?string("dd.MM.yyyy")}<#else>с ${fromDate?datetime?string("dd.MM.yyyy HH:mm")} по ${toDate?datetime?string("dd.MM.yyyy HH:mm")}</#if>
Место проведения: ${location!""}


