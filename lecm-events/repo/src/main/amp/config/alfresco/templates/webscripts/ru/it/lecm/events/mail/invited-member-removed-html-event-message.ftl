<#assign typeText="мероприятия">
<#if type="lecm-meetings:document">
<#assign typeText="совещания">
</#if>
<html>
<head>
	<meta http-equiv=Content-Type content="text/html; charset=UTF-8">
</head>
<body>
	<#if recipientMail?has_content && attendees[recipientMail]?has_content>
	<p>Уважаемый(ая) ${attendees[recipientMail]["name"]}!</p>
	<br>
	<p>Вы больше не являетесь участником ${typeText} ${title!""}.</p>
	<#else>
	<p>Уважаемые коллеги!</p>
	<br>
	<p>${initiator!""} отменил проведение ${typeText} ${title!""}.</p>
	</#if>
</body>
</html>
