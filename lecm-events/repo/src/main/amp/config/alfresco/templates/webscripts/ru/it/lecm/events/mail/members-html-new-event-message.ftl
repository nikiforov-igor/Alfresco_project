<#assign typeText="мероприятие">
<#if type="lecm-meetings:document">
<#assign typeText="совещание">
</#if>
<html>
<head>
	<meta http-equiv=Content-Type content="text/html; charset=UTF-8">
</head>
<body>
	<#assign personal = (recipientMail?has_content && attendees[recipientMail]?has_content)>
	<#if personal>
	<p>Уважаемый(ая) ${attendees[recipientMail]["name"]}!</p>
	<#else>
	<p>Уважаемые коллеги!</p>
	</#if>
	<br>
	<p>${initiator!""} приглашает Вас на ${link!""}.</p>
	<p>
		Время проведения: <#if allDay>${fromDate?date?string("dd.MM.yyyy")}<#else>с ${fromDate?datetime?string("dd.MM.yyyy HH:mm")} по ${toDate?datetime?string("dd.MM.yyyy HH:mm (z)")}</#if>
		<br>
		Место проведения: ${location!""}<br>
		Инициатор: ${initiator!""}<br>
		Участники: <#list attendees?keys as mail>${attendees[mail]["name"]}<#if mail_has_next>, </#if></#list>
		<br>
		<#if personal>
		Участие для Вас является <#if attendees[recipientMail]["mandatory"]>обязательным<#else>необязательным</#if>
		</#if>
	</p>
</body>
</html>