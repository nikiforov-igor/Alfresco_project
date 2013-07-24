<html>
<head>
	<meta http-equiv=Content-Type content="text/html; charset=UTF-8">
</head>
<body>
	<h3><a href="${documentLink}">${documentName}</a></h3>
	<ul>
		<#if properties??>
			<#list properties?keys as propTitle>
				<li>
					<#if properties[propTitle]?is_date>
						${propTitle}: ${properties[propTitle]?datetime}
					<#else>
						${propTitle}: ${properties[propTitle]?string}
					</#if>
				</li>
			</#list>
		</#if>
	</ul>
</body>
</html>