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
					<#if propTitle??>
						<#if properties[propTitle]??>
							<#if properties[propTitle]?is_date>
								<#assign propValue = properties[propTitle]?datetime>
							<#else>
								<#assign propValue = properties[propTitle]?string>
							</#if>
						</#if>
						${propTitle}: <#if propValue??>${propValue}</#if>
					</#if>
				</li>
			</#list>
		</#if>
	</ul>
</body>
</html>