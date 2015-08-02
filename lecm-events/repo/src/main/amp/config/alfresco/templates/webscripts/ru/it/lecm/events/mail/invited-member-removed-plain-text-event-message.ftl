<#assign typeText="мероприятия">
<#if type="lecm-meetings:document">
<#assign typeText="совещания">
</#if>
<#if recipientMail?has_content && attendees[recipientMail]?has_content>
Уважаемый(ая) ${attendees[recipientMail]["name"]}!
Вы больше не являетесь участником ${typeText} ${title!""}.
<#else>
Уважаемые коллеги!
${initiator!""} отменил проведение ${typeText} ${title!""}.
</#if>
