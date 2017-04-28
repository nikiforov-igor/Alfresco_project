<#if recipientMail?has_content && attendees[recipientMail]?has_content>
Уважаемый(ая) ${attendees[recipientMail]["name"]}!
<#else>
Уважаемые коллеги!
</#if>
Компания ${organization!""} приглашает Вас на мероприятие ${title!""}, которое состоится  ${date!""} по адресу ${location!""}.
	
${description!""}

${initiator!""}
