<#assign el=args.htmlid?html>

<@markup id="css" >
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-delegation/delegation-global-settings.css" group="delegation-settings"/>
</@>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/components/console/consoletool.js" group="delegation-settings"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-delegation/delegation-global-settings.js" group="delegation-settings"/>
	<@script type="text/javascript" src="${url.context}/res/components/form/form.js" group="delegation-settings"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js" group="delegation-settings"/>
</@>

<@markup id="widgets">
	<@createWidgets group="delegation-settings"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<div id="${el}-body" class="delegation-settings">
			<div class="yui-g">
				<div class="yui-u first">
					<div class="title">${msg("label.title")}</div>
				</div>
			</div>

			<div id="${el}-settings"></div>
		</div>
	</@>
</@>