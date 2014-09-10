  <style type="text/css">
   a:link {
    text-decoration: none;
   }
   table{
	border-collapse: collapse;
	border-spacing: 5px;
	padding: 5px;
   }
   td{
	padding: 3px;
   }
   table, th, td {
    border: 1px solid black;
   }
   a.atype{
	color:#FA881E;
   }
   a.atype:hover{
	font-weight: bold;
   }
   </style>
<table>
	<caption><h3>Типы</h3></caption>
	<tr>
		<th>Номер</th>
		<th>Имя</th>
		<th>Описание</th>
	</tr>
	<#assign it = 1>
	<#list typeNames as typeName>
		<tr>
			<td align="center">
				${it}
				<#assign it = it + 1>
			</td>
			<td align="center">
				<a class="atype" href="/share/proxy/alfresco/lecm/typesView/info?typeName=${typeName['name']}" target="_blank">${typeName['name']}</a>
			</td>
			<td align="center">
				${typeName['desc']!""}
			</td>
		</tr>
	</#list>
</table>