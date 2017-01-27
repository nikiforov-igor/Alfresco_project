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
   a.assoctype {
	font-size: 16px;
	color:#8B795E;
   }
   </style>
<table>
	<caption><h3>${msg('lecm.viewtps.lbl.properties')}</h3></caption>
	<tr>
		<th>${msg('lecm.viewtps.lbl.name')}</th>
		<th>${msg('lecm.viewtps.lbl.description')}</th>
		<th>${msg('lecm.viewtps.lbl.type')}</th>
		<th>${msg('lecm.viewtps.lbl.default')}</th>
		<th>${msg('lecm.viewtps.lbl.mandatory')}</th>
		<th>${msg('lecm.viewtps.lbl.index')}</th>
	</tr>
	<#assign orders = propsHierarchy?keys>
	<#list orders?sort as order>
		<#assign types = propsHierarchy[order]>
		<#list types["meta"] as typeMeta>
			<tr>
				<td colspan="6" align="center">
					<a class="atype" href="${url.context}/proxy/alfresco/lecm/typesView/info?typeName=${typeMeta['name']}" target="_blank">
						${typeMeta['name']}
						<#if typeMeta['desc']??>
							(${typeMeta['desc']!""})
						</#if>
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
	<caption><h3>${msg('lecm.viewtps.lbl.associations')}</h3></caption>
	<tr>
		<th>${msg('lecm.viewtps.lbl.name')}</th>
		<th>${msg('lecm.viewtps.lbl.description')}</th>
		<th>${msg('lecm.viewtps.lbl.target.type')}</th>
		<th>${msg('lecm.viewtps.lbl.target.mandatory')}</th>
		<th>${msg('lecm.viewtps.lbl.target.many')}</th>
		<th>${msg('lecm.viewtps.lbl.child')}</th>
	</tr>
	<#assign orders = assocsHierarchy?keys>
	<#list orders?sort as order>
		<#assign types = assocsHierarchy[order]>
		<#list types["meta"] as typeMeta>
			<tr>
				<td colspan="6" align="center">
					<a class="atype" href="${url.context}/proxy/alfresco/lecm/typesView/info?typeName=${typeMeta['name']}" target="_blank">
						${typeMeta['name']}
						<#if typeMeta['desc']??>
							(${typeMeta['desc']!""})
						</#if>
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
						<a class="assoctype" href="${url.context}/proxy/alfresco/lecm/typesView/info?typeName=${assoc['target']}" target="_blank">
							${assoc['target']}
							<#if assoc['targetDesc']??>
								(${assoc['targetDesc']!""})
							</#if>
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
	<caption><h3>${msg('lecm.viewtps.lbl.aspects')}</h3></caption>
	<tr>
		<th>${msg('lecm.viewtps.lbl.name')}</th>
		<th>${msg('lecm.viewtps.lbl.description')}</th>
	</tr>
	<#assign orders = aspectsHierarchy?keys>
	<#list orders?sort as order>
		<#assign types = aspectsHierarchy[order]>
		<#list types["meta"] as typeMeta>
			<tr>
				<td colspan="2" align="center">
					<a class="atype" href="${url.context}/proxy/alfresco/lecm/typesView/info?typeName=${typeMeta['name']}" target="_blank">
						${typeMeta['name']}
						<#if typeMeta['desc']??>
							(${typeMeta['desc']!""})
						</#if>
					</a>
				</td>
			</tr>
		</#list>
		<#assign aspects = types["content"]>
		<#list aspects as aspect>
			<tr>
				<td>
					<a class="assoctype" href="${url.context}/proxy/alfresco/lecm/typesView/info?typeName=${aspect['name']}" target="_blank">
						${aspect["name"]}
					</a>
				</td>
				<td>${aspect["desc"]!""}</td>
			</tr>
		</#list>
	</#list>
</table>



