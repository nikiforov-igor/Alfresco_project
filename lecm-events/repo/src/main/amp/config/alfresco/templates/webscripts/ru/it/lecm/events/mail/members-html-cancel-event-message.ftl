<#assign typeText="мероприяти">
<#if type="lecm-meetings:document">
<#assign typeText="совещани">
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
	<br/>
	<p>${link!""} отменено, либо Вы больше не являетесь его участником.</p>
	<br/>
	<p>
	Информация об отмененном ${typeText}и:<br/>
	Время проведения: 
		<#if allDay>
			${fromDate?date?string("dd.MM.yyyy")}
		<#else>
			с ${fromDate?datetime?string("dd.MM.yyyy HH:mm")} по ${toDate?datetime?string("dd.MM.yyyy HH:mm")}
		</#if>
	<br/>


	Место проведения: ${location!""}<br/>
	</p>	
</body>
</html>


