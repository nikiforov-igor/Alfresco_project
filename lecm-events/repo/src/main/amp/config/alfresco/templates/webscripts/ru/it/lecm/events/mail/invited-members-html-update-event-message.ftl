<#assign typeText="мероприяти">
<#if type="lecm-meetings:document">
<#assign typeText="совещани">
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
<br>
<p>Обратите внимание, обновилась информация по ${typeText}ю ${title!""}.</p>
<p>${typeText?capitalize}е состоится  ${date!""} по адресу ${location!""}.</p>
<p>${description!""}</p>
<p>${initiator!""}</p>
</body>
</html>
