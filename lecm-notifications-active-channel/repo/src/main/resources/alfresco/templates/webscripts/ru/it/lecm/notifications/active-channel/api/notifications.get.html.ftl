<#if notifications??>
	<#list notifications as notf>
		<div class="notification-row">
			${notf.properties["lecm-notf:forming-date"]?string("dd.MM.yyyy HH:mm")}: ${notf.properties["lecm-notf:description"]}
		</div>
	</#list>
</#if>