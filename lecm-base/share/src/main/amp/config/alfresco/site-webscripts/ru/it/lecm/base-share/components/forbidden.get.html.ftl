<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/forbidden.css" />
</@>
<div class="forbidden">
	<h1 class="theme-color-3">
		<#if args.message??>
		<span>${msg(args.message)}</span>
		<#else>
		<span>${msg("message.view-page.permissions.deny")}</span>
		</#if>
	</h1>
</div>
