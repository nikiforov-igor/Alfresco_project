<#assign typeText="мероприятия">
<#if type="lecm-meetings:document">
<#assign typeText="совещания">
</#if>
<#if recipientMail?has_content && attendees[recipientMail]?has_content>
Уважаемый(ая) ${attendees[recipientMail]["name"]}!
<#else>
Уважаемые коллеги!
</#if>
${initiator!""} отменил проведение ${typeText} ${title!""}.
