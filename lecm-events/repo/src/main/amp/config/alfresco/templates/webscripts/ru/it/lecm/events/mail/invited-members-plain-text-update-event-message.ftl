<#assign typeText="мероприяти">
<#if type="lecm-meetings:document">
<#assign typeText="совещани">
</#if>
<#if recipientMail?has_content && attendees[recipientMail]?has_content>
Уважаемый(ая) ${attendees[recipientMail]["name"]}!
<#else>
Уважаемые коллеги!
</#if>

Обратите внимание, обновилась информация по ${typeText}ю ${title!""}.
${typeText?capitalize}е состоится  ${date!""} по адресу ${location!""}.

${description!""}

${initiator!""}
