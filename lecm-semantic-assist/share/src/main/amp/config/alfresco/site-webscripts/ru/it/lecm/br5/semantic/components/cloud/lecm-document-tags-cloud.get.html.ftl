
<!-- Parameters and libs -->
<#assign aDateTime = .now>
<#assign el=args.htmlid + aDateTime?iso_utc/>
<div id="${el}">
		<#if tagsList??>
			<#assign keys = tagsList?keys>
			<#list keys as key>
					<#if tagsList[key]??>
						<a class="cloud" href='documents-by-term?tag=${key}&type=lecm'  target='_blank' style='font-size:${tagsList[key]}px;'>${key}</a>
					</#if>
			</#list>
		</#if>

</div>
