<html>
<head>
	<meta http-equiv=Content-Type content="text/html; charset=UTF-8">
</head>
<body>
	<h3><a href="${documentLink}">${documentName}</a></h3>
	<ul>
		<#if properties??>
			<#list properties?keys as propTitle>
				<#if propTitle??>
					<#if properties[propTitle]??>
						<li>
						<#if properties[propTitle]?is_date>
							<#assign propValue = properties[propTitle]?datetime>
						<#elseif properties[propTitle]?is_enumerable>
							<#assign propValue = "">
							<#list properties[propTitle] as value>
								<#assign propValue = propValue + value.properties["cm:name"]>
								<#if value_has_next>
									<#assign propValue = propValue + ", ">
								</#if>
							</#list>
						<#else>
							<#assign propValue = properties[propTitle]?string>
						</#if>
						${propTitle}: <#if propValue??>${propValue}</#if>
						</li>
					</#if>
				</#if>
			</#list>
		</#if>
	</ul>
</body>
</html>
