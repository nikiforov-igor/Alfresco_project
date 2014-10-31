<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/forbidden.css" />
</@>
<div class="forbidden">
	<h1 class="theme-color-3">
		<#if args.message??>
		<span>${msg(args.message)}</span>
		<#else>
		<span>У вас нет прав для просмотра страницы. Обратитесь к администратору системы.</span>
		</#if>
	</h1>
</div>
