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
   a.assoctype:hover{
	font-weight: bold;
   }
   a.assoctype {
	font-size: 16px;
	color:#8B795E;
   }
   </style>
<table>
	<caption><h3>Свойства</h3></caption>
	<tr>
		<th>Имя</th>
		<th>Описание</th>
		<th>Тип</th>
		<th>По-умолчанию</th>
		<th>Обязательное</th>
		<th>Индекс</th>
	</tr>
	<#assign orders = propsHierarchy?keys>
	<#list orders?sort as order>
		<#assign types = propsHierarchy[order]>
		<#list types["meta"] as typeMeta>
			<tr>
				<td colspan="6" align="center">
					<a class="atype" href="/share/proxy/alfresco/lecm/typesView/info?typeName=${typeMeta['name']}" target="_blank">
						${typeMeta['desc']!typeMeta['name']}
					</a>
				</td>
			</tr>
		</#list>
		<#assign props = types["content"]>
		<#list props as prop>
			<tr>
				<td>${prop["name"]!"none"}</td>
				<td>${prop["desc"]!""}</td>
				<td>${prop["type"]!"none"}</td>
				<td>${prop["default"]!"none"}</td>
				<td>${prop["mandatory"]!"none"}</td>
				<td>${prop["indexed"]!"none"}</td>
			</tr>
		</#list>
	</#list>
</table>

</br>
<table>
	<caption><h3>Ассоциации</h3></caption>
	<tr>
		<th>Имя</th>
		<th>Описание</th>
		<th>Тип цели</th>
		<th>Обязательность цели</th>
		<th>Множественность цели</th>
		<th>Дочерняя</th>
	</tr>
	<#assign orders = assocsHierarchy?keys>
	<#list orders?sort as order>
		<#assign types = assocsHierarchy[order]>
		<#list types["meta"] as typeMeta>
			<tr>
				<td colspan="6" align="center">
					<a class="atype" href="/share/proxy/alfresco/lecm/typesView/info?typeName=${typeMeta['name']}" target="_blank">
						${typeMeta['desc']!typeMeta['name']}
					</a>
				</td>
			</tr>
		</#list>
		<#assign assocs = types["content"]>
		<#list assocs as assoc>
			<tr>
				<td>${assoc["name"]!"none"}</td>
				<td>${assoc["desc"]!""}</td>
				<td>
					<#if assoc["target"]??>
						<a class="assoctype" href="/share/proxy/alfresco/lecm/typesView/info?typeName=${assoc['target']}" target="_blank">
							${assoc['targetDesc']!assoc['target']}
						</a>
					</#if>
				</td>
				<td>${assoc["targetMandatory"]!"none"}</td>
				<td>${assoc["targetMany"]!"none"}</td>
				<td>${assoc["child"]!"none"}</td>
			</tr>
		</#list>
	</#list>
</table>

</br>
<table>
	<caption><h3>Аспекты</h3></caption>
	<tr>
		<th>Имя</th>
		<th>Описание</th>
	</tr>
	<#assign orders = aspectsHierarchy?keys>
	<#list orders?sort as order>
		<#assign types = aspectsHierarchy[order]>
		<#list types["meta"] as typeMeta>
			<tr>
				<td colspan="2" align="center">
					<a class="atype" href="/share/proxy/alfresco/lecm/typesView/info?typeName=${typeMeta['name']}" target="_blank">
						${typeMeta['desc']!typeMeta['name']}
					</a>
				</td>
			</tr>
		</#list>
		<#assign aspects = types["content"]>
		<#list aspects as aspect>
			<tr>
				<td>${aspect["name"]!"none"}</td>
				<td>${aspect["desc"]!""}</td>
			</tr>
		</#list>
	</#list>
</table>



