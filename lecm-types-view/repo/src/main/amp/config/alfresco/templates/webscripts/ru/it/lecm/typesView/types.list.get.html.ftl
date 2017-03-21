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
   </style>
<table>
	<caption><h3>${msg('lecm.viewtps.lbl.types')}</h3></caption>
	<tr>
		<th>${msg('lecm.viewtps.lbl.number')}</th>
		<th>${msg('lecm.viewtps.lbl.name')}</th>
		<th>${msg('lecm.viewtps.lbl.description')}</th>
	</tr>
	<#assign it = 1>
	<#list typeNames as typeName>
		<tr>
			<td align="center">
				${it}
				<#assign it = it + 1>
			</td>
			<td>
				<a class="atype" href="${url.context}/service/lecm/typesView/info?typeName=${typeName['name']}" target="_blank">${typeName['name']}</a>
			</td>
			<td>
				${typeName['desc']!""}
			</td>
		</tr>
	</#list>
</table>
</br>
<table>
	<caption><h3>${msg('lecm.viewtps.lbl.aspects')}</h3></caption>
	<tr>
		<th>${msg('lecm.viewtps.lbl.number')}</th>
		<th>${msg('lecm.viewtps.lbl.name')}</th>
		<th>${msg('lecm.viewtps.lbl.description')}</th>
	</tr>
	<#assign it = 1>
	<#list aspectNames as aspectName>
		<tr>
			<td align="center">
				${it}
				<#assign it = it + 1>
			</td>
			<td>
				<a class="atype" href="${url.context}/service/lecm/typesView/info?typeName=${aspectName['name']}" target="_blank">${aspectName['name']}</a>
			</td>
			<td>
				${aspectName['desc']!""}
			</td>
		</tr>
	</#list>
</table>
