<#assign typeText="мероприятие">
<#if type="lecm-meetings:document">
<#assign typeText="совещание">
</#if>
<html>
<head>
	<meta http-equiv=Content-Type content="text/html; charset=UTF-8">
</head>
<body>
	<#if recipientMail?has_content && attendees[recipientMail]?has_content>
	<p>Уважаемый(ая) ${attendees[recipientMail]["name"]}!</p>
	<#else>
	<p>Уважаемые коллеги!</p>
	</#if>
	<br/>
	<p>Компания ${organization!""} приглашает вас на ${typeText} ${title!""}, которое состоится ${date!""} по адресу ${location!""}.</p>
	<br/>
	<p>${description!""}</p>
	<br/>
	<p>${initiator!""}</p>
</body>
</html>
