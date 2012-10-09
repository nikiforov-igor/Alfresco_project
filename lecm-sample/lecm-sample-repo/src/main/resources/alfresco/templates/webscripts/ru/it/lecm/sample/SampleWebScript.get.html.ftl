<html>
	<head>
		<title>Sample java-backed web script. Folder ${folder.displayPath}/${folder.name}</title>
	</head>
	<body>
		Alfresco ${server.edition} Edition v${server.version} : dir
		<p>	Contents of folder ${folder.displayPath}/${folder.name}<p>
		<table>
			<#list folder.children as child>
			<tr>
			<td><#if child.isContainer>d</#if></td>
			<#if verbose>
					<td>${child.properties.modifier}</td>
					<td><#if child.isDocument>
							${child.properties.content.size}</#if></td>
					<td>${child.properties.modified?date}</td>
			</#if>
			<td>${child.name}</td>
			</tr>
			</#list>
		</table>
	</body>
</html>