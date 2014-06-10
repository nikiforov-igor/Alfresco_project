<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<@templateHeader "transitional">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/semantic-assist/tags-cloud.css" />
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
	<div style="float:left; width: 1000px; padding:10px;">

			<#if tagsList??>
				<#assign keys = tagsList?keys>
				<#list keys as key>
						<#if tagsList[key]??>
							<a class="cloud" href='documents-by-term?tag=${key}&type=alfresco'  target='_blank' style='font-size:${tagsList[key]}px;'>${key}</a>
						</#if>
				</#list>
			</#if>

	 </div>
</@bpage.basePage>